package com.optimize.land.repository;

import com.optimize.common.entities.exception.ResourceNotFoundException;
import com.optimize.land.model.dto.ActorRespDto;
import com.optimize.land.model.entity.AbstractActor;
import com.optimize.land.model.entity.Actor;
import com.optimize.land.model.entity.Registration;
import com.optimize.land.model.enumeration.RegistrationStatus;
import com.optimize.land.model.projection.ActorProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ActorRepository extends BaseActorRepository<AbstractActor, Long> {

    Optional<AbstractActor> findByRid(String rid);

    Optional<Registration> findByRidAndRegistrationStatusIn(String rid, List<RegistrationStatus> status);

    Optional<AbstractActor> findBySynchroBatchNumberAndSynchroPacketNumber(String synchroBatchNumber, String synchroPacketNumber);

    Optional<Actor> findByUinAndRegistrationStatus(String rid, RegistrationStatus status);

    //@Query(value = "SELECT a.id, a.uin, a.registrationStatus, a.role FROM Actor a WHERE a.registrationStatus = :status ORDER BY a.id DESC")
    Page<ActorProjection> findByRegistrationStatus(@Param(value = "status") RegistrationStatus status, Pageable pageable);

    Page<ActorProjection> findByRegistrationStatusInOrderByIdDesc(@Param(value = "status") List<RegistrationStatus> status, Pageable pageable);

    //@Query(value = "SELECT a.id, a.uin, a.registrationStatus, a.role FROM Actor a WHERE a.registrationStatus = :status AND a.operatorAgent = :operatorAgent ORDER BY a.id DESC")
    Page<ActorProjection> findByRegistrationStatusAndOperatorAgent(@Param(value = "status") RegistrationStatus status, @Param(value = "operatorAgent") String operatorAgent, Pageable pageable);

    Page<ActorProjection> findByRegistrationStatusInAndOperatorAgentOrderByIdDesc(@Param(value = "status") List<RegistrationStatus> status, @Param(value = "operatorAgent") String operatorAgent, Pageable pageable);

    @Query(value = "SELECT a.id, a.uin, a.registrationStatus, a.role FROM Actor a WHERE a.registrationStatus = :status ORDER BY a.id DESC")
    List<ActorProjection> findByRegistrationStatus(@Param(value = "status") RegistrationStatus status);

    @Query(value = "SELECT a.id, a.uin, a.registrationStatus, a.role FROM Actor a WHERE a.registrationStatus = :status AND a.operatorAgent = :operatorAgent ORDER BY a.id DESC")
    List<ActorProjection> findByRegistrationStatus(@Param(value = "status") RegistrationStatus status, @Param(value = "operatorAgent") String operatorAgent);

    List<Actor> findByUinInAndRegistrationStatus(Set<String> uinList, RegistrationStatus status);

    default AbstractActor getByRid(String rid) {
        return findByRid(rid).orElseThrow(() -> new ResourceNotFoundException("Could not find actor by rid " + rid));
    }

    default Registration getRegistrationByRid(String rid) {
        return findByRidAndRegistrationStatusIn(rid, List.of(RegistrationStatus.PENDING, RegistrationStatus.QUEUED)).orElseThrow(() -> new ResourceNotFoundException("Could not find registration by rid " + rid));
    }

    default Actor getByUin(String uin) {
        return findByUinAndRegistrationStatus(uin, RegistrationStatus.ACTOR).orElse(null);
    }

    boolean existsByRidAndRegistrationStatusIn(String rid, List<RegistrationStatus> statusList);

    @Query("SELECT new com.optimize.land.model.dto.ActorRespDto(" +
            "a.id, " +
            "a.uin, " +
            "CASE " +
            "WHEN a.type = com.optimize.land.model.enumeration.ActorType.PHYSICAL_PERSON THEN CONCAT(a.physicalPerson.firstname, ' ', a.physicalPerson.lastname) " +
            "WHEN a.type = com.optimize.land.model.enumeration.ActorType.INFORMAL_GROUP THEN a.informalGroup.groupName " +
            "WHEN a.type = com.optimize.land.model.enumeration.ActorType.PRIVATE_LEGAL_ENTITY THEN a.privateLegalEntity.companyName " +
            "WHEN a.type = com.optimize.land.model.enumeration.ActorType.PUBLIC_LEGAL_ENTITY THEN a.publicLegalEntity.name " +
            "ELSE '' END, " +
            "a.type, " +
            "a.role, " +
            "a.registrationStatus, " +
            "a.statusObservation) " +
            "FROM AbstractActor a WHERE a.registrationStatus = com.optimize.land.model.enumeration.RegistrationStatus.ACTOR " +
            "AND a.state = com.optimize.common.entities.enums.State.ENABLED " +
            "ORDER BY a.id DESC")
    Page<ActorRespDto> findAllActors(Pageable pageable);

    @Query("SELECT new com.optimize.land.model.dto.ActorRespDto(" +
            "a.id, " +
            "a.uin, " +
            "CASE " +
            "WHEN a.type = com.optimize.land.model.enumeration.ActorType.PHYSICAL_PERSON THEN CONCAT(a.physicalPerson.firstname, ' ', a.physicalPerson.lastname) " +
            "WHEN a.type = com.optimize.land.model.enumeration.ActorType.INFORMAL_GROUP THEN a.informalGroup.groupName " +
            "WHEN a.type = com.optimize.land.model.enumeration.ActorType.PRIVATE_LEGAL_ENTITY THEN a.privateLegalEntity.companyName " +
            "WHEN a.type = com.optimize.land.model.enumeration.ActorType.PUBLIC_LEGAL_ENTITY THEN a.publicLegalEntity.name " +
            "ELSE '' END, " +
            "a.type, " +
            "a.role, " +
            "a.registrationStatus, " +
            "a.statusObservation) " +
            "FROM AbstractActor a WHERE a.registrationStatus IN :status " +
            "AND a.state = com.optimize.common.entities.enums.State.ENABLED " +
            "ORDER BY a.id DESC")
    Page<ActorRespDto> findByRegistrationStatusIn(@Param("status") List<RegistrationStatus> status, Pageable pageable);

    @Query("SELECT new com.optimize.land.model.dto.ActorRespDto(" +
            "a.id, " +
            "a.uin, " +
            "CASE " +
            "WHEN a.type = com.optimize.land.model.enumeration.ActorType.PHYSICAL_PERSON THEN CONCAT(a.physicalPerson.firstname, ' ', a.physicalPerson.lastname) " +
            "WHEN a.type = com.optimize.land.model.enumeration.ActorType.INFORMAL_GROUP THEN a.informalGroup.groupName " +
            "WHEN a.type = com.optimize.land.model.enumeration.ActorType.PRIVATE_LEGAL_ENTITY THEN a.privateLegalEntity.companyName " +
            "WHEN a.type = com.optimize.land.model.enumeration.ActorType.PUBLIC_LEGAL_ENTITY THEN a.publicLegalEntity.name " +
            "ELSE '' END, " +
            "a.type, " +
            "a.role, " +
            "a.registrationStatus, " +
            "a.statusObservation) " +
            "FROM AbstractActor a WHERE a.registrationStatus IN :status " +
            "AND a.operatorAgent = :operatorAgent " +
            "AND a.state = com.optimize.common.entities.enums.State.ENABLED " +
            "ORDER BY a.id DESC")
    Page<ActorRespDto> findByRegistrationStatusInAndOperatorAgent(@Param("status") List<RegistrationStatus> status, @Param("operatorAgent") String operatorAgent, Pageable pageable);

    @Query("SELECT new com.optimize.land.model.dto.ActorRespDto(" +
            "a.id, " +
            "a.uin, " +
            "CASE " +
            "WHEN a.type = com.optimize.land.model.enumeration.ActorType.PHYSICAL_PERSON THEN CONCAT(a.physicalPerson.firstname, ' ', a.physicalPerson.lastname) " +
            "WHEN a.type = com.optimize.land.model.enumeration.ActorType.INFORMAL_GROUP THEN a.informalGroup.groupName " +
            "WHEN a.type = com.optimize.land.model.enumeration.ActorType.PRIVATE_LEGAL_ENTITY THEN a.privateLegalEntity.companyName " +
            "WHEN a.type = com.optimize.land.model.enumeration.ActorType.PUBLIC_LEGAL_ENTITY THEN a.publicLegalEntity.name " +
            "ELSE '' END, " +
            "a.type, " +
            "a.role, " +
            "a.registrationStatus, " +
            "a.statusObservation) " +
            "FROM AbstractActor a WHERE a.registrationStatus IN :status " +
            "AND a.state = com.optimize.common.entities.enums.State.ENABLED " +
            "ORDER BY a.id DESC")
    List<ActorRespDto> findByRegistrationStatusIn(@Param("status") List<RegistrationStatus> status);

    @Query("SELECT new com.optimize.land.model.dto.ActorRespDto(" +
            "a.id, " +
            "a.uin, " +
            "CASE " +
            "WHEN a.type = com.optimize.land.model.enumeration.ActorType.PHYSICAL_PERSON THEN CONCAT(a.physicalPerson.firstname, ' ', a.physicalPerson.lastname) " +
            "WHEN a.type = com.optimize.land.model.enumeration.ActorType.INFORMAL_GROUP THEN a.informalGroup.groupName " +
            "WHEN a.type = com.optimize.land.model.enumeration.ActorType.PRIVATE_LEGAL_ENTITY THEN a.privateLegalEntity.companyName " +
            "WHEN a.type = com.optimize.land.model.enumeration.ActorType.PUBLIC_LEGAL_ENTITY THEN a.publicLegalEntity.name " +
            "ELSE '' END, " +
            "a.type, " +
            "a.role, " +
            "a.registrationStatus, " +
            "a.statusObservation) " +
            "FROM AbstractActor a WHERE a.registrationStatus IN :status " +
            "AND a.operatorAgent = :operatorAgent " +
            "AND a.state = com.optimize.common.entities.enums.State.ENABLED " +
            "ORDER BY a.id DESC")
    List<ActorRespDto> findByRegistrationStatusInAndOperatorAgent(@Param("status") List<RegistrationStatus> status, @Param("operatorAgent") String operatorAgent);
}
