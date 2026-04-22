package com.optimize.land.model.dto;

import java.time.LocalDateTime;

public record FindingFilterDto(
    String region,
    String prefecture,
    String commune,
    String canton,
    LocalDateTime startDate,
    LocalDateTime endDate
) {}
