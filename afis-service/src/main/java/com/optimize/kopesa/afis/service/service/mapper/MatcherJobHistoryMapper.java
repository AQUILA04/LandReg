package com.optimize.kopesa.afis.service.service.mapper;

import com.optimize.kopesa.afis.service.domain.MatcherJobHistory;
import com.optimize.kopesa.afis.service.service.dto.MatcherJobHistoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MatcherJobHistory} and its DTO {@link MatcherJobHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface MatcherJobHistoryMapper extends EntityMapper<MatcherJobHistoryDTO, MatcherJobHistory> {}
