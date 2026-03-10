package com.optimize.land.service;

import com.optimize.common.entities.exception.ResourceNotFoundException;
import com.optimize.common.entities.service.GenericService;
import com.optimize.common.securities.security.services.UserService;
import com.optimize.land.model.dto.SynchroHistoryDto;
import com.optimize.land.model.entity.SynchroHistory;
import com.optimize.land.model.enumeration.SynchroStatus;
import com.optimize.land.model.enumeration.SynchroType;
import com.optimize.land.model.mapper.SynchroHistoryMapper;
import com.optimize.land.repository.SynchroHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class SynchroHistoryService extends GenericService<SynchroHistory, Long> {
    private final UserService userService;
    private final SynchroHistoryMapper synchroHistoryMapper;

    protected SynchroHistoryService(SynchroHistoryRepository repository,
                                    UserService userService,
                                    SynchroHistoryMapper synchroHistoryMapper) {
        super(repository);
        this.userService = userService;
        this.synchroHistoryMapper = synchroHistoryMapper;
    }

    public SynchroHistoryDto initSynchro(SynchroHistoryDto dto) {
        dto.setBatchNumber(UUID.randomUUID().toString());
        dto.setInitDate(LocalDate.now());
        dto.setSynchroStatus(SynchroStatus.PENDING);
        dto.setOperatorAgent(userService.getCurrentUser().getUsername());
        return synchroHistoryMapper.toDto(create(synchroHistoryMapper.toEntity(dto)));
    }

    public SynchroHistoryDto finishSynchro(String batchNumber) {
        if (Objects.nonNull(batchNumber)) {
            return synchroHistoryMapper.toDto(getRepository().getByBatchNumber(batchNumber)) ;
        }
        throw new ResourceNotFoundException("batchNumber ne peut pas être null !");
    }

    @Transactional(noRollbackFor = RuntimeException.class, propagation = Propagation.REQUIRES_NEW)
    public void receivedPacket(String batchNumber, String packetNumber, SynchroType type) {
        if (Objects.nonNull(batchNumber) && Objects.nonNull(packetNumber)) {
            SynchroHistory history = getRepository().getByBatchNumber(batchNumber);
            history.setType(type);
            history.incrementReceived();
            history.addPacketNumber(packetNumber);
            getRepository().saveAndFlush(history);
        }
    }

    @Transactional(noRollbackFor = RuntimeException.class, propagation = Propagation.REQUIRES_NEW)
    public void failedPacket(String batchNumber, String packetNumber) {
        if (Objects.nonNull(batchNumber) && Objects.nonNull(packetNumber)) {
            SynchroHistory history = getRepository().getByBatchNumber(batchNumber);
            history.incrementFailed();
            getRepository().saveAndFlush(history);
        }
    }

    @Transactional(noRollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void duplicatedPacket(String batchNumber, String packetNumber) {
        if (Objects.nonNull(batchNumber) && Objects.nonNull(packetNumber)) {
            SynchroHistory history = getRepository().getByBatchNumber(batchNumber);
            history.incrementDuplicated();
            getRepository().saveAndFlush(history);
        }
    }

    public void successPacket(String batchNumber, String packetNumber) {
        if (Objects.nonNull(batchNumber) && Objects.nonNull(packetNumber)) {
            SynchroHistory history = getRepository().getByBatchNumber(batchNumber);
            history.incrementSuccess();
            getRepository().saveAndFlush(history);
        }
    }


    public SynchroHistoryRepository getRepository() {
        return (SynchroHistoryRepository) repository;
    }
}
