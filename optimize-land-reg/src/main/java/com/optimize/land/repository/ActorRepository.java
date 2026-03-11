package com.optimize.land.repository;

import com.optimize.common.entities.exception.ResourceNotFoundException;
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
}
