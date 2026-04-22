package com.optimize.land.model.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SearchFilterDto {
    private String region;
    private String prefecture;
    private String commune;
    private String canton;
    private LocalDate startDate;
    private LocalDate endDate;
}
