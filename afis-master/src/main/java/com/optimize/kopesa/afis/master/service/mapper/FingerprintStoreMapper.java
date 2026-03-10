package com.optimize.kopesa.afis.master.service.mapper;

import com.optimize.kopesa.afis.master.domain.FingerprintStore;
import com.optimize.kopesa.afis.master.domain.ProcessingFingerprint;
import com.optimize.kopesa.afis.master.service.dto.FingerprintStoreDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper for the entity {@link FingerprintStore} and its DTO {@link FingerprintStoreDTO}.
 */
@Mapper(componentModel = "spring")
public interface FingerprintStoreMapper extends EntityMapper<FingerprintStoreDTO, FingerprintStore> {
    ProcessingFingerprint toProcessingFingerprint(FingerprintStoreDTO fingerprintStoreDTO);

    FingerprintStore toFingerprintStore(ProcessingFingerprint processingFingerprint);

    List<ProcessingFingerprint> toProcessingFingerprints(List<FingerprintStoreDTO> fingerprintStoreDTOs);

    List<FingerprintStore> toFingerprintStores(List<ProcessingFingerprint> processingFingerprints);
}
