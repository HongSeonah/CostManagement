package com.hongseonah.costmanager.domain.allocation.dto.response;

import java.math.BigDecimal;

public record BusinessUnitAllocationResponse(
        Long id,
        String unitCode,
        String unitName,
        String managerName,
        long projectCount,
        long activeProjectCount,
        BigDecimal directCost,
        BigDecimal sharedCost,
        BigDecimal totalCost,
        BigDecimal allocationRate
) {
}
