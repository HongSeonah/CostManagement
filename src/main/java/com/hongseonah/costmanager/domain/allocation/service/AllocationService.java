package com.hongseonah.costmanager.domain.allocation.service;

import com.hongseonah.costmanager.domain.allocation.dto.response.AllocationSummaryResponse;
import com.hongseonah.costmanager.domain.allocation.dto.response.AllocationCloseResponse;

public interface AllocationService {

    AllocationSummaryResponse getSummary(String month);
    AllocationCloseResponse closeMonth(String month);
    byte[] exportMonth(String month);
}
