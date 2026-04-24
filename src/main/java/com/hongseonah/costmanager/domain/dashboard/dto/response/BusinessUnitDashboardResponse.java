package com.hongseonah.costmanager.domain.dashboard.dto.response;

import java.math.BigDecimal;

public record BusinessUnitDashboardResponse(
        Long id,
        String unitCode,
        String unitName,
        String managerName,
        Integer activeProjectLimit,
        long projectCount,
        long activeProjectCount,
        BigDecimal totalBudget,
        BigDecimal totalSpent,
        BigDecimal utilizationRate
) {
}
