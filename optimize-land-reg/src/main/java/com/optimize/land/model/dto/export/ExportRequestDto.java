package com.optimize.land.model.dto.export;

import com.optimize.land.model.dto.SearchFilterDto;
import lombok.Data;

@Data
public class ExportRequestDto {
    private String targetModule; // ACTORS, FINDINGS, SYNCHRO
    private String format; // CSV, XLSX
    private SearchFilterDto filters;
}
