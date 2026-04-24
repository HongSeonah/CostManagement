package com.hongseonah.costmanager.domain.analysis.dto.response;

import java.math.BigDecimal;

public record BusinessUnitAnalysisResponse(
        Long id,
        String unitCode,
        String unitName,
        String managerName,
        long employeeCount,
        long projectCount,
        BigDecimal actualCost,
        BigDecimal standardCost,
        BigDecimal laborCost,
        BigDecimal transferInAmount,
        BigDecimal transferOutAmount,
        BigDecimal transferNetAmount,
        BigDecimal variance,
        BigDecimal performanceRate
) {
}
