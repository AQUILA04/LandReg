package com.optimize.kopesa.afis.master.service.mapper;

import com.optimize.kopesa.afis.master.domain.MatcherJobHistory;
import com.optimize.kopesa.afis.master.service.dto.MatcherJobHistoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MatcherJobHistory} and its DTO {@link MatcherJobHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface MatcherJobHistoryMapper extends EntityMapper<MatcherJobHistoryDTO, MatcherJobHistory> {}
