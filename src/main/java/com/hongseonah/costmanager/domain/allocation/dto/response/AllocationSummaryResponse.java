package com.hongseonah.costmanager.domain.allocation.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record AllocationSummaryResponse(
        String month,
        long businessUnitCount,
        long projectCount,
        long entryCount,
        long activeProjectCount,
        BigDecimal totalCost,
        BigDecimal directCost,
        BigDecimal sharedCost,
        List<BusinessUnitAllocationResponse> businessUnits
) {
}
