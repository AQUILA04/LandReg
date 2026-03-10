package com.optimize.kopesa.afis.service.service.mapper;

import com.optimize.kopesa.afis.service.domain.FingerprintStore;
import com.optimize.kopesa.afis.service.service.dto.FingerprintStoreDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FingerprintStore} and its DTO {@link FingerprintStoreDTO}.
 */
@Mapper(componentModel = "spring")
public interface FingerprintStoreMapper extends EntityMapper<FingerprintStoreDTO, FingerprintStore> {}
