package com.hongseonah.costmanager.domain.businessunit.service;

import com.hongseonah.costmanager.domain.businessunit.dto.request.BusinessUnitCreateRequest;
import com.hongseonah.costmanager.domain.businessunit.dto.request.BusinessUnitUpdateRequest;
import com.hongseonah.costmanager.domain.businessunit.dto.response.BusinessUnitResponse;
import java.util.List;

public interface BusinessUnitService {
    List<BusinessUnitResponse> findAll();
    BusinessUnitResponse findById(Long id);
    BusinessUnitResponse create(BusinessUnitCreateRequest request);
    BusinessUnitResponse update(Long id, BusinessUnitUpdateRequest request);
    void delete(Long id);
}
