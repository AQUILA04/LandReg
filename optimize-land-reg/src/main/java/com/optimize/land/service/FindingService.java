package com.optimize.land.service;

import com.optimize.common.entities.enums.State;
import com.optimize.common.entities.exception.ApplicationException;
import com.optimize.common.entities.repository.GenericRepository;
import com.optimize.common.entities.service.GenericService;
import com.optimize.common.securities.models.User;
import com.optimize.common.securities.security.services.UserService;
import com.optimize.land.model.dto.FindingDto;
import com.optimize.land.model.entity.Finding;
import com.optimize.land.model.enumeration.SynchroType;
import com.optimize.land.model.mapper.FindingMapper;
import com.optimize.land.model.projection.FindingProjection;
import com.optimize.land.repository.FindingRepository;
import com.optimize.land.util.ProfilConstant;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FindingService extends GenericService<Finding, Long> {
    private final FindingMapper findingMapper;
    private final CheckListOperationService checkListOperationService;
    private final SynchroHistoryService synchroHistoryService;
    private final UserService userService;

    protected FindingService(FindingRepository repository,
                             FindingMapper findingMapper,
                             CheckListOperationService checkListOperationService,
                             SynchroHistoryService synchroHistoryService,
                             UserService userService) {
        super(repository);
        this.findingMapper = findingMapper;
        this.checkListOperationService = checkListOperationService;
        this.synchroHistoryService = synchroHistoryService;
        this.userService = userService;
    }

    @Transactional
    public Long register(@NotNull FindingDto findingDto) {
        this.synchroHistoryService.receivedPacket(findingDto.getSynchroBatchNumber(), findingDto.getSynchroPacketNumber(), SynchroType.FINDING);
        try {
            //findingDto.validateFirstAndLastCheckListOperations();
            Finding finding = findingMapper.toEntity(findingDto);
            this.checkListOperationService.create(finding.getFirstCheckListOperation());
            this.checkListOperationService.create(finding.getLastCheckListOperation());
            finding.setOperatorAgent(userService.getCurrentUser().getUsername());
            create(finding);
            return finding.getId();
        } catch (Exception e) {
            this.synchroHistoryService.failedPacket(findingDto.getSynchroBatchNumber(), findingDto.getSynchroPacketNumber());
            throw new ApplicationException("Une Erreur S'est produite lors de l'enregistrement de la constatation: ", e.getMessage());
        }

    }

    public Page<FindingProjection> getAllToProjection(Pageable pageable) {
        User user = userService.getCurrentUser();
        if (user.is(ProfilConstant.LAND_AGENT_OPERATOR)) {
            return getRepository().findByStateAndOperatorAgentOrderByIdDesc(State.ENABLED, user.getUsername(), pageable);
        }
        return this.getRepository().findByStateOrderByIdDesc(State.ENABLED, pageable);
    }

    @Override
    public FindingRepository getRepository() {
        return (FindingRepository) super.getRepository();
    }
}
