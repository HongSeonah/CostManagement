package com.hongseonah.costmanager.domain.allocation.service;

import com.hongseonah.costmanager.domain.allocation.dto.response.AllocationSummaryResponse;

public interface AllocationService {

    AllocationSummaryResponse getSummary(String month);
}
