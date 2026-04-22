package com.optimize.land.model.dto;

import java.time.LocalDateTime;

public record DateFilterDto(
    LocalDateTime startDate,
    LocalDateTime endDate
) {}
