package com.hongseonah.costmanager.domain.analysis.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record AnalysisSummaryResponse(
        String month,
        long businessUnitCount,
        long projectCount,
        long employeeCount,
        BigDecimal actualCostTotal,
        BigDecimal standardCostTotal,
        BigDecimal laborCostTotal,
        BigDecimal transferAmountTotal,
        BigDecimal varianceTotal,
        List<BusinessUnitAnalysisResponse> businessUnits
) {
}
