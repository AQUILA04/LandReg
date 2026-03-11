package com.optimize.land.service;

import com.optimize.common.entities.exception.ResourceNotFoundException;
import com.optimize.common.securities.models.User;
import com.optimize.common.securities.security.services.UserService;
import com.optimize.land.model.dto.SynchroHistoryDto;
import com.optimize.land.model.entity.SynchroHistory;
import com.optimize.land.model.enumeration.SynchroStatus;
import com.optimize.land.model.enumeration.SynchroType;
import com.optimize.land.model.mapper.SynchroHistoryMapper;
import com.optimize.land.repository.SynchroHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SynchroHistoryServiceTest {

    @Mock
    private SynchroHistoryRepository repository;

    @Mock
    private UserService userService;

    @Mock
    private SynchroHistoryMapper synchroHistoryMapper;

    @InjectMocks
    private SynchroHistoryService synchroHistoryService;

    private User currentUser;
    private SynchroHistoryDto dto;
    private SynchroHistory entity;

    @BeforeEach
    void setUp() {
        currentUser = new User("Test", "User", "MALE", "test@test.com", "12345678", "testuser", "password");

        dto = new SynchroHistoryDto();
        dto.setId(1L);

        entity = new SynchroHistory();
        entity.setId(1L);
        entity.setBatchNumber("batch-123");
        entity.setSynchroCandidateCount(10);
    }

    @Test
    void initSynchro_Success() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(synchroHistoryMapper.toEntity(any(SynchroHistoryDto.class))).thenReturn(entity);
        when(repository.save(any(SynchroHistory.class))).thenReturn(entity);
        when(synchroHistoryMapper.toDto(any(SynchroHistory.class))).thenReturn(dto);

        SynchroHistoryDto result = synchroHistoryService.initSynchro(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userService).getCurrentUser();
        verify(repository).save(any(SynchroHistory.class));
    }

    @Test
    void finishSynchro_Success() {
        when(repository.getByBatchNumber("batch-123")).thenReturn(entity);
        when(synchroHistoryMapper.toDto(entity)).thenReturn(dto);

        SynchroHistoryDto result = synchroHistoryService.finishSynchro("batch-123");

        assertNotNull(result);
        verify(repository).getByBatchNumber("batch-123");
    }

    @Test
    void finishSynchro_ThrowsException_WhenBatchNumberIsNull() {
        assertThrows(ResourceNotFoundException.class, () -> {
            synchroHistoryService.finishSynchro(null);
        });
    }

    @Test
    void receivedPacket_Success() {
        when(repository.getByBatchNumber("batch-123")).thenReturn(entity);

        synchroHistoryService.receivedPacket("batch-123", "packet-1", SynchroType.ACTOR);

        assertEquals(SynchroType.ACTOR, entity.getType());
        verify(repository).saveAndFlush(entity);
    }

    @Test
    void failedPacket_Success() {
        when(repository.getByBatchNumber("batch-123")).thenReturn(entity);

        synchroHistoryService.failedPacket("batch-123", "packet-1");

        verify(repository).saveAndFlush(entity);
    }

    @Test
    void duplicatedPacket_Success() {
        when(repository.getByBatchNumber("batch-123")).thenReturn(entity);

        synchroHistoryService.duplicatedPacket("batch-123", "packet-1");

        verify(repository).saveAndFlush(entity);
    }

    @Test
    void successPacket_Success() {
        when(repository.getByBatchNumber("batch-123")).thenReturn(entity);

        synchroHistoryService.successPacket("batch-123", "packet-1");

        verify(repository).saveAndFlush(entity);
    }
}