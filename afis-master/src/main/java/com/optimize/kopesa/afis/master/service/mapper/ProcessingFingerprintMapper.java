package com.optimize.kopesa.afis.master.service.mapper;

import com.optimize.kopesa.afis.master.domain.ProcessingFingerprint;
import com.optimize.kopesa.afis.master.service.dto.ProcessingFingerprintDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProcessingFingerprint} and its DTO {@link ProcessingFingerprintDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProcessingFingerprintMapper extends EntityMapper<ProcessingFingerprintDTO, ProcessingFingerprint> {}
