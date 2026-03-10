package com.optimize.land.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.optimize.common.entities.enums.State;
import com.optimize.common.entities.exception.ApplicationException;
import com.optimize.common.entities.exception.CustomValidationException;
import com.optimize.common.entities.service.GenericService;
import com.optimize.common.securities.models.User;
import com.optimize.common.securities.security.services.UserService;
import com.optimize.land.client.AfisClient;
import com.optimize.land.jms.AfisProducer;
import com.optimize.land.jms.model.AfisMasterRequest;
import com.optimize.land.jms.model.RegistrationProcessorFeedback;
import com.optimize.land.model.dto.*;
import com.optimize.land.model.entity.*;
import com.optimize.land.model.enumeration.ActorType;
import com.optimize.land.model.enumeration.BioAuthResponse;
import com.optimize.land.model.enumeration.RegistrationStatus;
import com.optimize.land.model.enumeration.SynchroType;
import com.optimize.land.model.mapper.ActorMapper;
import com.optimize.land.model.projection.ActorProjection;
import com.optimize.land.repository.ActorRepository;
import com.optimize.land.util.ProfilConstant;
import com.optimize.land.util.UniqueIDGenerator;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;


@Service
@Transactional(readOnly = true)
@Slf4j(topic = "EventLog")
public class ActorService extends GenericService<AbstractActor, Long> {
    private final ActorMapper actorMapper;
    private final FingerprintStoreService fingerprintStoreService;
    private final SynchroHistoryService synchroHistoryService;
    private final AfisProducer afisProducer;
    private final AfisClient afisClient;
    private UserService userService;

    protected ActorService(ActorRepository repository,
                           ActorMapper actorMapper,
                           FingerprintStoreService fingerprintStoreService,
                           SynchroHistoryService synchroHistoryService,
                           AfisProducer afisProducer,
                           AfisClient afisClient) {
        super(repository);
        this.actorMapper = actorMapper;
        this.fingerprintStoreService = fingerprintStoreService;
        this.synchroHistoryService = synchroHistoryService;
        this.afisProducer = afisProducer;
        this.afisClient = afisClient;
    }

    @Transactional
    public synchronized String register(@NotNull ActorDto actorDto) throws JsonProcessingException {
        synchroHistoryService.receivedPacket(actorDto.getSynchroBatchNumber(),
                actorDto.getSynchroPacketNumber(), SynchroType.ACTOR);
        try {
            actorDto.setId(null);
            actorDto.validateUniqueActorType();
            Registration registration = actorMapper.toRegistration(actorDto);
            registration.validateUniqueActorType();
            registration.fingerprintMandatoryCheck();
            final String rid = UniqueIDGenerator.generateRID();
            registration.addRid(rid);
            registration.setOperatorAgent(userService.getCurrentUser().getUsername());
            if (!registration.getFingerprintStores().isEmpty()) {
                fingerprintStoreService.getRepository().saveAll(registration.getFingerprintStores());
            }
            //registration.updateFingerprint();
            create(registration);
            //fingerprintStoreService.getRepository().saveAllAndFlush(registration.getFingerprintStores());
            if(registration.actorTypeIs(ActorType.PHYSICAL_PERSON)) {
                afisProducer.sendMatchingRequest(new AfisMasterRequest(registration.getRid(),
                        registration.getFingerprintStores()));
                registration.setRegistrationStatus(RegistrationStatus.QUEUED);
                update(registration);
            } else {
                validateLegalEntity(registration);
            }
            return "{\"rid\":"+rid +"}";
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ACTOR REGISTRATION ERROR: {}", e.getLocalizedMessage());
            synchroHistoryService.failedPacket(actorDto.getSynchroBatchNumber(), actorDto.getSynchroPacketNumber());
            throw new ApplicationException("ACTOR REGISTRATION ERROR: "+ e.getMessage());
        }

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void validate(String rid) {
        Registration registration = (Registration) getRepository().getByRid(rid);
        Actor actor = new Actor();
                actor = actorMapper.registrationToActor(registration);
        actor.addUin(UniqueIDGenerator.generateUIN());
        getRepository().delete(registration);
        create(actor);
        synchroHistoryService.successPacket(actor.getSynchroBatchNumber(), actor.getSynchroPacketNumber());
    }

    @Transactional(noRollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void failed(String rid, String message) {
        Registration registration = getRepository().getRegistrationByRid(rid);
        registration.setRegistrationStatus(RegistrationStatus.FAILED);
        registration.setStatusObservation(message);
        update(registration);
        synchroHistoryService.failedPacket(registration.getSynchroBatchNumber(), registration.getSynchroPacketNumber());
    }

    @Transactional
    public void duplicate(String rid, String message) {
        Registration registration = getRepository().getRegistrationByRid(rid);
        registration.setRegistrationStatus(RegistrationStatus.DUPLICATED);
        registration.setStatusObservation(message);
        update(registration);
        synchroHistoryService.duplicatedPacket(registration.getSynchroBatchNumber(), registration.getSynchroPacketNumber());
    }

    @Transactional
    public void afterMatchingOperation(RegistrationProcessorFeedback feedback) {
        try {
            if (Boolean.TRUE.equals(feedback.getFoundMatch())) {
                log.info("===> AFIS DUPLICATED UPDATE");
                duplicate(feedback.getRid(), feedback.getMatchedRID());
            } else {
                log.info("===> AFIS VALIDATED UPDATE");
                validate(feedback.getRid());
            }
        } catch (Exception e) {
            log.error("===> AFIS UPDATE FAILED");
            log.error(e.getLocalizedMessage());
            failed(feedback.getRid(), e.getLocalizedMessage());
        }
    }

    public Page<ActorProjection> getByStatus(RegistrationStatus status, Pageable pageable) {
        User user = userService.getCurrentUser();
        List<RegistrationStatus> statusList = List.of(status);;
        if (!RegistrationStatus.ACTOR.equals(status)) {
            statusList = List.of(RegistrationStatus.PENDING, RegistrationStatus.QUEUED, RegistrationStatus.DUPLICATED, RegistrationStatus.FAILED, RegistrationStatus.IN_PROGRESS);
        }
        if (user.is(ProfilConstant.LAND_AGENT_OPERATOR)) {
            return getRepository().findByRegistrationStatusInAndOperatorAgentOrderByIdDesc(statusList, user.getUsername(), pageable);
        }
//        Page<AbstractActor> pageActor = getRepository().findByRegistrationStatusAndStateOrderByIdDesc(status, State.ENABLED, pageable);
//        pageActor.getContent().forEach(AbstractActor::getAllOperations);
//        return pageActor;
        return getRepository().findByRegistrationStatusInOrderByIdDesc(statusList, pageable);
    }

    public List<ActorProjection> getByStatus(RegistrationStatus status) {
        User user = userService.getCurrentUser();
        if (user.is(ProfilConstant.LAND_AGENT_OPERATOR)) {
            return getRepository().findByRegistrationStatus(status, user.getUsername());
        }
        return getRepository().findByRegistrationStatus(status);
    }

    public FingerprintAuthenticationResp bioAuth(BioAuthDto dto) {
        Actor actor = getRepository().getByUin(dto.getUin());
        FingerprintAuthenticationResp resp = new FingerprintAuthenticationResp();
        if (Objects.isNull(actor)) {
            resp.setStatus(BioAuthResponse.UIN_NOT_FOUND);
            return resp;
        }
//        if (!actor.getRole().equals(dto.getRole())) {
//            resp.setStatus(BioAuthResponse.ROLE_NOT_MATCH);
//            return resp;
//        }
        dto.setRid(actor.getRid());
        String fingerprint = dto.getFingerprint().split(",")[1];
        dto.setFingerprint(fingerprint);
        try {
            if (BioAuthResponse.MATCH.equals(afisClient.bioAuthRequest(dto))) {
                resp.setStatus(BioAuthResponse.MATCH);
                resp.setActor(actor.toActorModel());
            } else {
                resp.setStatus(BioAuthResponse.FINGERPRINT_NOT_MATCH);
            }
        } catch (Exception e) {
            log.error("ERROR: {}",e.getLocalizedMessage());
            throw new ApplicationException(e.getLocalizedMessage());
        }

        return resp;
    }

    @Transactional
    public void validateLegalEntity(Registration registration) {
        //TODO: gérer ça avec un event
        if (Objects.nonNull(registration.getFingerprintStores()) && !registration.getFingerprintStores().isEmpty() ) {
            afisClient.sendLegalEntityFingerprint(registration.getFingerprintStores());
        }
        validate(registration.getRid());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AbstractActor> getAll() {
        return super.getAll();
    }

    @Override
    public Page<AbstractActor> getAll(Pageable pageable) {
        Page<AbstractActor> pageActor = getRepository().findByRegistrationStatusAndStateOrderByIdDesc(RegistrationStatus.ACTOR, State.ENABLED, pageable);
        pageActor.getContent().forEach(AbstractActor::getAllOperations);
        return pageActor;
    }

    public List<ActorModel> getUINDetails(UINWrapper uinWrapper) {
        List<Actor> actors = getRepository().findByUinInAndRegistrationStatus(uinWrapper.getUinList(), RegistrationStatus.ACTOR);
        return actors.stream().map(AbstractActor::toActorModel).toList();
    }

    @Transactional
    public synchronized String updateActor(ActorDto actorDto, Long id) {
        try {
            actorDto.setId(id);
            Registration registration = actorMapper.toRegistration(actorDto);
            Actor actor = actorMapper.registrationToActor(registration);
            updateFingerprint(actor);
            updateIdentificationDoc(actor);
            actor.setRegistrationStatus(RegistrationStatus.ACTOR);
            Actor old = (Actor) getById(id);
            actor.setRid(old.getRid());
            actor.setFingerprintStores(fingerprintStoreService.getRepository().findByRid(old.getRid()));
            update(actor);
            return "updated:success";
        }catch (Exception e) {
            log.error("ACTOR UPDATE ERROR: {}", e.getLocalizedMessage());
            throw new ApplicationException("ACTOR UPDATE ERROR: "+ e.getMessage());
        }
    }

    public void updateFingerprint(Actor actor) {
        if (Objects.nonNull(actor.getFingerprintStores()) && !actor.getFingerprintStores().isEmpty()) {
            throw new ApplicationException("Fingerprint update is not supported yet !!!");
        }
        //actor.setFingerprintStores(fingerprintStoreService.getRepository().findByRid(actor.getRid()));
    }

    public void updateIdentificationDoc(Actor actor) {
        if (actor.actorTypeIs(ActorType.PHYSICAL_PERSON) && Objects.nonNull(actor.getPhysicalPerson().getIdentificationDoc()) && Objects.isNull(actor.getPhysicalPerson().getIdentificationDoc().getId())) {
            throw new CustomValidationException("l'identifiant du document d'identification est obligatoire pour la mise à jour !");
        }

        if (actor.actorTypeIs(ActorType.PRIVATE_LEGAL_ENTITY) && Objects.nonNull(actor.getPrivateLegalEntity().getIdentificationDoc()) && Objects.isNull(actor.getPhysicalPerson().getIdentificationDoc().getId())) {
            throw new CustomValidationException("l'identifiant du document d'identification est obligatoire pour la mise à jour !");
        }

        Actor existed = (Actor) getById(actor.getId());
        if (actor.actorTypeIs(ActorType.PHYSICAL_PERSON) && Objects.isNull(actor.getPhysicalPerson().getIdentificationDoc())) {
            actor.getPhysicalPerson().setIdentificationDoc(existed.getPhysicalPerson().getIdentificationDoc());
        }
        else if (actor.actorTypeIs(ActorType.PRIVATE_LEGAL_ENTITY) && Objects.isNull(actor.getPrivateLegalEntity().getIdentificationDoc())) {
            actor.getPrivateLegalEntity().setIdentificationDoc(existed.getPrivateLegalEntity().getIdentificationDoc());
        }
    }

    public void putInQueue()  {

    }

    public boolean existsByRid(String rid) {
        return getRepository().existsByRidAndRegistrationStatusIn(rid, List.of(RegistrationStatus.PENDING, RegistrationStatus.QUEUED));
    }

    public ActorRepository getRepository() {
        return (ActorRepository) repository;
    }

    @Override
    public AbstractActor getById(Long id) {
        AbstractActor actor = super.getById(id);
        if (actor.actorTypeIs(ActorType.PHYSICAL_PERSON)) {
            actor.setFingerprintStores(fingerprintStoreService.getByRid(actor.getRid()));
        }
        return actor;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
