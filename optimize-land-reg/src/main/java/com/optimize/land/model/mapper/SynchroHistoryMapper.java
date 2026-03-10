package com.optimize.land.model.mapper;

import com.optimize.common.entities.mapper.BaseMapper;
import com.optimize.land.model.dto.SynchroHistoryDto;
import com.optimize.land.model.entity.SynchroHistory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SynchroHistoryMapper extends BaseMapper<SynchroHistory, SynchroHistoryDto> {
}
