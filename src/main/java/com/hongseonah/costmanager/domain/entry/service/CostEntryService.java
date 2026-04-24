package com.hongseonah.costmanager.domain.entry.service;

import com.hongseonah.costmanager.domain.entry.dto.request.CostEntryCreateRequest;
import com.hongseonah.costmanager.domain.entry.dto.response.CostEntryResponse;
import java.util.List;

public interface CostEntryService {

    List<CostEntryResponse> findAll();

    CostEntryResponse create(CostEntryCreateRequest request);
}

