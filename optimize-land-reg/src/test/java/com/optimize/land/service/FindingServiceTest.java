package com.optimize.land.service;

import com.optimize.common.entities.enums.State;
import com.optimize.common.entities.exception.ApplicationException;
import com.optimize.common.securities.models.User;
import com.optimize.common.securities.security.services.UserService;
import com.optimize.land.model.dto.CheckListOperationDto;
import com.optimize.land.model.dto.FindingDto;
import com.optimize.land.model.entity.Bordering;
import com.optimize.land.model.entity.CheckListOperation;
import com.optimize.land.model.entity.Conflict;
import com.optimize.land.model.entity.Finding;
import com.optimize.land.model.enumeration.SynchroType;
import com.optimize.land.model.mapper.FindingMapper;
import com.optimize.land.model.projection.FindingProjection;
import com.optimize.land.repository.FindingRepository;
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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private Conflict conflict;
    private Set<Bordering> borderings;

    @BeforeEach
    void setUp() {
        currentUser = new User("Test", "User", "MALE", "test@test.com", "12345678", "testuser", "password");

        findingDto = new FindingDto();
        findingDto.setId(1L);
        findingDto.setSynchroBatchNumber("batch123");
        findingDto.setSynchroPacketNumber("packet1");
        
        CheckListOperationDto clDto1 = new CheckListOperationDto();
        CheckListOperationDto clDto2 = new CheckListOperationDto();
        findingDto.setFirstCheckListOperation(clDto1);
        findingDto.setLastCheckListOperation(clDto2);

        finding = new Finding();
        finding.setId(1L);
        
        CheckListOperation op1 = new CheckListOperation();
        CheckListOperation op2 = new CheckListOperation();
        
        borderings = new HashSet<>();
        Bordering b1 = new Bordering();
        b1.setId(1L);
        borderings.add(b1);
        op1.setBorderingList(borderings);
        op2.setBorderingList(borderings);

        finding.setFirstCheckListOperation(op1);
        finding.setLastCheckListOperation(op2);

        conflict = new Conflict();
        conflict.setId(1L);
        finding.setConflict(conflict);
        finding.setHasConflict(true);
    }

    @Test
    void register_Success() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(findingMapper.toEntity(findingDto)).thenReturn(finding);
        when(checkListOperationService.create(any(CheckListOperation.class))).thenReturn(new CheckListOperation());
        when(repository.save(any(Finding.class))).thenReturn(finding);

        Long id = findingService.register(findingDto);

        assertNotNull(id);
        assertEquals(1L, id);
        assertEquals(conflict, finding.getConflict());
        assertFalse(finding.getFirstCheckListOperation().getBorderingList().isEmpty());

        verify(synchroHistoryService).receivedPacket("batch123", "packet1", SynchroType.FINDING);
        verify(checkListOperationService, times(2)).create(any(CheckListOperation.class));
        verify(repository).save(finding);
    }

    @Test
    void register_ThrowsException() {
        when(findingMapper.toEntity(findingDto)).thenThrow(new RuntimeException("Mapping error"));

        ApplicationException exception = assertThrows(ApplicationException.class, () -> findingService.register(findingDto));

        assertTrue(exception.getMessage().contains("Une Erreur S'est produite lors de l'enregistrement de la constatation"));
        verify(synchroHistoryService).failedPacket("batch123", "packet1");
        verify(repository, never()).save(any(Finding.class));
    }

    @Test
    void updateFinding_Success() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(findingMapper.toEntity(findingDto)).thenReturn(finding);
        when(repository.findById(1L)).thenReturn(Optional.of(finding));
        when(checkListOperationService.update(any(CheckListOperation.class))).thenReturn(new CheckListOperation());
        when(repository.saveAndFlush(any(Finding.class))).thenReturn(finding);

        Long id = findingService.updateFinding(findingDto);

        assertNotNull(id);
        assertEquals(1L, id);

        //verify(synchroHistoryService).receivedPacket("batch123", "packet1", SynchroType.FINDING);
        verify(checkListOperationService, times(2)).update(any(CheckListOperation.class));
        verify(repository).saveAndFlush(finding);
    }

    @Test
    void updateFinding_ThrowsException() {
        when(findingMapper.toEntity(findingDto)).thenThrow(new RuntimeException("Mapping error"));

        ApplicationException exception = assertThrows(ApplicationException.class, () -> findingService.updateFinding(findingDto));

        assertTrue(exception.getMessage().contains("Une Erreur S'est produite lors de la modification de la constatation"));
        //verify(synchroHistoryService).failedPacket("batch123", "packet1");
        verify(repository, never()).saveAndFlush(any(Finding.class));
    }

    @Test
    void getAllToProjection_AsAdmin() {
        when(userService.getCurrentUser()).thenReturn(currentUser);

        Page<FindingProjection> expectedPage = new PageImpl<>(Collections.emptyList());
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findByStateOrderByIdDesc(State.ENABLED, pageable)).thenReturn(expectedPage);

        Page<FindingProjection> result = findingService.getAllToProjection(pageable);

        assertEquals(expectedPage, result);
        verify(repository).findByStateOrderByIdDesc(State.ENABLED, pageable);
    }

    @Test
    void search_Success() {
        Page<FindingProjection> expectedPage = new PageImpl<>(Collections.emptyList());
        Pageable pageable = PageRequest.of(0, 10);
        String keyword = "test";
        String keywordFinal = "%test%";

        when(repository.searchByKeyword(keywordFinal, pageable)).thenReturn(expectedPage);

        Page<FindingProjection> result = findingService.search(keyword, pageable);

        assertEquals(expectedPage, result);
        verify(repository).searchByKeyword(keywordFinal, pageable);
    }
}