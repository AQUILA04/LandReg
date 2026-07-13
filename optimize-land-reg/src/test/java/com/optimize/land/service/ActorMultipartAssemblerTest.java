package com.optimize.land.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import com.optimize.common.entities.exception.CustomValidationException;
import com.optimize.land.model.dto.v2.ActorDtoV2;
import com.optimize.land.model.dto.v2.FingerprintStoreV2Dto;
import com.optimize.land.model.entity.FingerprintStore;
import com.optimize.land.model.enumeration.Finger;
import com.optimize.land.model.enumeration.HandType;
import com.optimize.land.model.mapper.ActorMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class ActorMultipartAssemblerTest {

    @Mock
    private ActorMapper actorMapper;

    @InjectMocks
    private ActorMultipartAssembler assembler;

    @Test
    void assemblesFingerprintStoresFromMultipartFiles() {
        ActorDtoV2 actorDto = new ActorDtoV2();
        FingerprintStoreV2Dto meta = new FingerprintStoreV2Dto();
        meta.setFingerStr("Pouce Gauche");
        meta.setHandType(HandType.LEFT);
        meta.setFingerName(Finger.THUMB);
        actorDto.setFingerprintStores(Set.of(meta));

        MockMultipartFile file = new MockMultipartFile(
            "fingerprints",
            "thumb.jpg",
            "image/jpeg",
            "binary-image".getBytes(StandardCharsets.UTF_8)
        );

        FingerprintStore entity = new FingerprintStore();
        when(actorMapper.toFingerprintStore(eq(meta), any(), eq("image/jpeg"))).thenReturn(entity);

        Set<FingerprintStore> result = assembler.assembleFingerprintStores(actorDto, List.of(file));

        assertEquals(1, result.size());
        assertEquals(entity, result.iterator().next());
    }

    @Test
    void rejectsMismatchedFileCount() {
        ActorDtoV2 actorDto = new ActorDtoV2();
        FingerprintStoreV2Dto meta = new FingerprintStoreV2Dto();
        meta.setFingerStr("Pouce Gauche");
        actorDto.setFingerprintStores(Set.of(meta));

        assertThrows(
            CustomValidationException.class,
            () -> assembler.assembleFingerprintStores(actorDto, List.of())
        );
    }

    @Test
    void ordersFilesByPartIndexWhenProvided() {
        ActorDtoV2 actorDto = new ActorDtoV2();
        FingerprintStoreV2Dto thumbMeta = new FingerprintStoreV2Dto();
        thumbMeta.setFingerStr("Pouce Gauche");
        thumbMeta.setPartIndex(1);
        FingerprintStoreV2Dto indexMeta = new FingerprintStoreV2Dto();
        indexMeta.setFingerStr("Index Gauche");
        indexMeta.setPartIndex(0);
        actorDto.setFingerprintStores(Set.of(thumbMeta, indexMeta));

        MockMultipartFile indexFile = new MockMultipartFile(
            "fingerprints",
            "index.jpg",
            "image/jpeg",
            "index-bytes".getBytes(StandardCharsets.UTF_8)
        );
        MockMultipartFile thumbFile = new MockMultipartFile(
            "fingerprints",
            "thumb.jpg",
            "image/jpeg",
            "thumb-bytes".getBytes(StandardCharsets.UTF_8)
        );

        FingerprintStore indexEntity = new FingerprintStore();
        FingerprintStore thumbEntity = new FingerprintStore();
        when(actorMapper.toFingerprintStore(eq(indexMeta), any(), eq("image/jpeg"))).thenReturn(indexEntity);
        when(actorMapper.toFingerprintStore(eq(thumbMeta), any(), eq("image/jpeg"))).thenReturn(thumbEntity);

        assembler.assembleFingerprintStores(actorDto, List.of(indexFile, thumbFile));

        InOrder inOrder = inOrder(actorMapper);
        inOrder.verify(actorMapper).toFingerprintStore(eq(indexMeta), any(), eq("image/jpeg"));
        inOrder.verify(actorMapper).toFingerprintStore(eq(thumbMeta), any(), eq("image/jpeg"));
    }
}
