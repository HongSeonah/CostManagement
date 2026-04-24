package com.hongseonah.costmanager.domain.dashboard.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryResponse(
        long businessUnitCount,
        long projectCount,
        long activeProjectCount,
        BigDecimal totalBudget,
        BigDecimal totalSpent,
        BigDecimal thisMonthSpent,
        long thisMonthEntryCount,
        List<BusinessUnitDashboardResponse> businessUnits
) {
}
