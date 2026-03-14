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
import com.optimize.land.repository.*;
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
import java.util.Optional;


@Service
@Transactional(readOnly = true)
@Slf4j
public class ActorService extends GenericService<AbstractActor, Long> {
    private final ActorMapper actorMapper;
    private final FingerprintStoreService fingerprintStoreService;
    private final SynchroHistoryService synchroHistoryService;
    private final AfisProducer afisProducer;
    private final AfisClient afisClient;
    private final PersonRepository personRepository;
    private final InformalGroupRepository informalGroupRepository;
    private final PrivateLegalEntityRepository privateLegalEntityRepository;
    private final PublicLegalEntityRepository publicLegalEntityRepository;
    private UserService userService;

    protected ActorService(ActorRepository repository,
                           ActorMapper actorMapper,
                           FingerprintStoreService fingerprintStoreService,
                           SynchroHistoryService synchroHistoryService,
                           AfisProducer afisProducer,
                           AfisClient afisClient,
                           PersonRepository personRepository,
                           InformalGroupRepository informalGroupRepository,
                           PrivateLegalEntityRepository privateLegalEntityRepository,
                           PublicLegalEntityRepository publicLegalEntityRepository) {
        super(repository);
        this.actorMapper = actorMapper;
        this.fingerprintStoreService = fingerprintStoreService;
        this.synchroHistoryService = synchroHistoryService;
        this.afisProducer = afisProducer;
        this.afisClient = afisClient;
        this.personRepository = personRepository;
        this.informalGroupRepository = informalGroupRepository;
        this.privateLegalEntityRepository = privateLegalEntityRepository;
        this.publicLegalEntityRepository = publicLegalEntityRepository;
    }

    @Transactional
    public synchronized String register(@NotNull ActorDto actorDto) throws JsonProcessingException {
        log.info("ACTOR REGISTRATION: {}, Fingerprint count {}", actorDto.getSynchroBatchNumber(), actorDto.getFingerprintStores().size());
        log.info("ACTOR DTO {}", actorDto);
        if (synchroHistoryService.getRepository().existsByBatchNumberAndPacketsNumberContains(actorDto.getSynchroBatchNumber(), actorDto.getSynchroPacketNumber())) {
            Optional<AbstractActor> optionalActor = getRepository().findBySynchroBatchNumberAndSynchroPacketNumber(actorDto.getSynchroBatchNumber(), actorDto.getSynchroPacketNumber());
            if (optionalActor.isPresent()) {
                return "{\"rid\":\"" + optionalActor.get().getRid() + "\"}";
            }
        }
        synchroHistoryService.receivedPacket(actorDto.getSynchroBatchNumber(),
                actorDto.getSynchroPacketNumber(), SynchroType.ACTOR);
        try {
            checkUnicity(actorDto);
            actorDto.setId(null);
            actorDto.validateUniqueActorType();
            Registration registration = actorMapper.toRegistration(actorDto);
            registration.validateUniqueActorType();
            registration.fingerprintMandatoryCheck();
            final String rid = UniqueIDGenerator.generateRID();
            registration.addRid(rid);
            registration.setOperatorAgent(userService.getCurrentUser().getUsername());
            if (!registration.getFingerprintStores().isEmpty()) {
                log.info("==> fingerprints non null | SAVING FINGERPRINTS");
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
            return "{\"rid\":\""+rid +"\"}";
        } catch (Exception e) {
            log.error("ACTOR REGISTRATION ERROR: {}", e.getLocalizedMessage());
            log.error(e.getLocalizedMessage(), e.getCause());
            synchroHistoryService.failedPacket(actorDto.getSynchroBatchNumber(), actorDto.getSynchroPacketNumber());
            throw new ApplicationException("ACTOR REGISTRATION ERROR: "+ e.getMessage());
        }

    }

    public Page<ActorRespDto> search(@NotNull(message = "Le mot clé de la recherche est obligatoire !") String keyword, Pageable pageable) {
        final String keywordFinal = "%" + keyword.toLowerCase().trim() + "%";
        return getRepository().searchByKeyword(keywordFinal, pageable);
    }

    private void checkUnicity(ActorDto actorDto) {
        if (ActorType.PHYSICAL_PERSON.equals(actorDto.getType()) && Objects.nonNull(actorDto.getPhysicalPerson())) {
            PersonDto person = actorDto.getPhysicalPerson();
            if (personRepository.existsByLastnameAndFirstnameAndSexAndMaritalStatusAndBirthDateAndPlaceOfBirthAndNationalityAndProfessionAndAddressAndPrimaryPhoneAndEmail(
                    person.getLastname(), person.getFirstname(), person.getSex(), person.getMaritalStatus(), person.getBirthDate(),
                    person.getPlaceOfBirth(), person.getNationality(), person.getProfession(), person.getAddress(), person.getPrimaryPhone(), person.getEmail())) {
                throw new ApplicationException("Une personne physique avec le nom " + person.getLastname() + " et le prénom " + person.getFirstname() + " existe déjà !");
            }
        } else if (ActorType.INFORMAL_GROUP.equals(actorDto.getType()) && Objects.nonNull(actorDto.getInformalGroup())) {
            InformalGroupDto informalGroup = actorDto.getInformalGroup();
            if (informalGroupRepository.existsByGroupNameAndAddressAndPhoneNumber(informalGroup.getGroupName(), informalGroup.getAddress(), informalGroup.getPhoneNumber())) {
                throw new ApplicationException("Un groupe informel avec le nom " + informalGroup.getGroupName() + " existe déjà !");
            }
        } else if (ActorType.PRIVATE_LEGAL_ENTITY.equals(actorDto.getType()) && Objects.nonNull(actorDto.getPrivateLegalEntity())) {
            PrivateLegalEntityDto privateLegalEntity = actorDto.getPrivateLegalEntity();
            if (privateLegalEntityRepository.existsByCompanyNameAndAddressAndPhoneNumber(privateLegalEntity.getCompanyName(), privateLegalEntity.getAddress(), privateLegalEntity.getPhoneNumber())) {
                throw new ApplicationException("Une personne morale de droit privé avec le nom " + privateLegalEntity.getCompanyName() + " existe déjà !");
            }
        } else if (ActorType.PUBLIC_LEGAL_ENTITY.equals(actorDto.getType()) && Objects.nonNull(actorDto.getPublicLegalEntity())) {
            PublicLegalEntityDto publicLegalEntity = actorDto.getPublicLegalEntity();
            if (publicLegalEntityRepository.existsByNameAndPhoneNumber(publicLegalEntity.getName(), publicLegalEntity.getPhoneNumber())) {
                throw new ApplicationException("Une personne morale de droit public avec le nom " + publicLegalEntity.getName() + " existe déjà !");
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void validate(String rid) {
        log.info("===> VALIDATION REQUEST RID {}", rid);
        Registration registration = (Registration) getRepository().getByRid(rid);
        Actor actor = new Actor();
                actor = actorMapper.registrationToActor(registration);
        log.info("SETTING UIN FOR ACTOR {}", actor);
        actor.addUin(UniqueIDGenerator.generateUIN());
        getRepository().delete(registration);
        create(actor);
        synchroHistoryService.successPacket(actor.getSynchroBatchNumber(), actor.getSynchroPacketNumber());
    }

    @Transactional(noRollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void failed(String rid, String message) {
        log.error("===> FAILED REQUEST RID {} | MESSAGE {}", rid, message);
        Registration registration = getRepository().getRegistrationByRid(rid);
        registration.setRegistrationStatus(RegistrationStatus.FAILED);
        registration.setStatusObservation(message);
        update(registration);
        synchroHistoryService.failedPacket(registration.getSynchroBatchNumber(), registration.getSynchroPacketNumber());
    }

    @Transactional
    public void duplicate(String rid, String message) {
        log.info("===> DUPLICATED REQUEST RID {} | MESSAGE {}", rid, message);
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

    public List<ActorRespDto> getByStatus(List<RegistrationStatus> statusList) {
        User user = userService.getCurrentUser();
        if (user.is(ProfilConstant.LAND_AGENT_OPERATOR)) {
            return getRepository().findByRegistrationStatusInAndOperatorAgent(statusList, user.getUsername());
        }
        return getRepository().findByRegistrationStatusIn(statusList);
    }

    public Page<ActorRespDto> getByStatus(RegistrationStatus status, Pageable pageable) {
        User user = userService.getCurrentUser();
        List<RegistrationStatus> statusList = List.of(status);
        if (!RegistrationStatus.ACTOR.equals(status)) {
            statusList = List.of(RegistrationStatus.PENDING, RegistrationStatus.QUEUED, RegistrationStatus.DUPLICATED, RegistrationStatus.FAILED, RegistrationStatus.IN_PROGRESS);
        }
        if (user.is(ProfilConstant.LAND_AGENT_OPERATOR)) {
            return getRepository().findByRegistrationStatusInAndOperatorAgent(statusList, user.getUsername(), pageable);
        }
        return getRepository().findByRegistrationStatusIn(statusList, pageable);
    }

    public List<ActorRespDto> getByStatus(RegistrationStatus status) {
        return getByStatus(List.of(status));
    }

    public FingerprintAuthenticationResp bioAuth(BioAuthDto dto) {
        log.info("===> BIOMETRIC AUTHENTICATION {}", dto);
        Actor actor = getRepository().getByUin(dto.getUin());
        log.info("===> BIOMETRIC AUTHENTICATION ACTOR {}", actor);
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
        log.info("===> BIOMETRIC AUTHENTICATION RID {} AND Fingerprint starting with {}", dto.getRid(), dto.getFingerprint().substring(0, 255));
        String fingerprint = dto.getFingerprint().split(",")[1];
        dto.setFingerprint(fingerprint);
        try {
            BioAuthResponse bioAuthResponse = afisClient.bioAuthRequest(dto);
            if (BioAuthResponse.MATCH.equals(bioAuthResponse)) {
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

    public Page<ActorRespDto> getAllActors(Pageable pageable) {
        return getRepository().findAllActors(pageable);
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
