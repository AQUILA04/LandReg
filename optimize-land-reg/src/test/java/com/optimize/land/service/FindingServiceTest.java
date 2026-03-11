package com.optimize.land.service;

import com.optimize.common.entities.enums.State;
import com.optimize.common.entities.exception.ApplicationException;
import com.optimize.common.securities.models.User;
import com.optimize.common.securities.security.services.UserService;
import com.optimize.land.model.dto.FindingDto;
import com.optimize.land.model.entity.CheckListOperation;
import com.optimize.land.model.entity.Finding;
import com.optimize.land.model.enumeration.SynchroType;
import com.optimize.land.model.mapper.FindingMapper;
import com.optimize.land.model.projection.FindingProjection;
import com.optimize.land.repository.FindingRepository;
import com.optimize.land.util.ProfilConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindingServiceTest {

    @Mock
    private FindingRepository repository;

    @Mock
    private FindingMapper findingMapper;

    @Mock
    private CheckListOperationService checkListOperationService;

    @Mock
    private SynchroHistoryService synchroHistoryService;

    @Mock
    private UserService userService;

    @InjectMocks
    private FindingService findingService;

    private User currentUser;
    private FindingDto findingDto;
    private Finding finding;

    @BeforeEach
    void setUp() {
        currentUser = new User("Test", "User", "MALE", "test@test.com", "12345678", "testuser", "password");

        findingDto = new FindingDto();
        findingDto.setSynchroBatchNumber("batch123");
        findingDto.setSynchroPacketNumber("packet1");

        finding = new Finding();
        finding.setId(1L);
        finding.setFirstCheckListOperation(new CheckListOperation());
        finding.setLastCheckListOperation(new CheckListOperation());
    }

    @Test
    void register_Success() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(findingMapper.toEntity(findingDto)).thenReturn(finding);
        when(checkListOperationService.create(any())).thenReturn(new CheckListOperation());
        when(repository.save(any(Finding.class))).thenReturn(finding);

        Long id = findingService.register(findingDto);

        assertNotNull(id);
        assertEquals(1L, id);
        verify(synchroHistoryService).receivedPacket("batch123", "packet1", SynchroType.FINDING);
        verify(checkListOperationService, times(2)).create(any());
        verify(repository).save(finding);
    }

    @Test
    void register_ThrowsException() {
        when(findingMapper.toEntity(findingDto)).thenThrow(new RuntimeException("Mapping error"));

        ApplicationException exception = assertThrows(ApplicationException.class, () -> findingService.register(findingDto));

        assertTrue(exception.getMessage().contains("Une Erreur S'est produite lors de l'enregistrement de la constatation"));
        verify(synchroHistoryService).failedPacket("batch123", "packet1");
    }

    @Test
    void getAllToProjection_AsAdmin() {
        // Admin user does not have LAND_AGENT_OPERATOR profile
        when(userService.getCurrentUser()).thenReturn(currentUser);

        Page<FindingProjection> expectedPage = new PageImpl<>(Collections.emptyList());
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findByStateOrderByIdDesc(State.ENABLED, pageable)).thenReturn(expectedPage);

        Page<FindingProjection> result = findingService.getAllToProjection(pageable);

        assertEquals(expectedPage, result);
        verify(repository).findByStateOrderByIdDesc(State.ENABLED, pageable);
    }
}
