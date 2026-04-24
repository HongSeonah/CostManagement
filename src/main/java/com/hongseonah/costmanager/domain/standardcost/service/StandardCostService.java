package com.hongseonah.costmanager.domain.standardcost.service;

import com.hongseonah.costmanager.domain.standardcost.dto.request.StandardCostCreateRequest;
import com.hongseonah.costmanager.domain.standardcost.dto.request.StandardCostUpdateRequest;
import com.hongseonah.costmanager.domain.standardcost.dto.response.StandardCostResponse;
import java.util.List;

public interface StandardCostService {

    List<StandardCostResponse> findAll(String month);
    StandardCostResponse findById(Long id);
    StandardCostResponse create(StandardCostCreateRequest request);
    StandardCostResponse update(Long id, StandardCostUpdateRequest request);
    void delete(Long id);
}
