package com.hongseonah.costmanager.domain.employee.service;

import com.hongseonah.costmanager.domain.employee.dto.request.EmployeeCreateRequest;
import com.hongseonah.costmanager.domain.employee.dto.request.EmployeeUpdateRequest;
import com.hongseonah.costmanager.domain.employee.dto.response.EmployeeResponse;
import java.util.List;

public interface EmployeeService {

    List<EmployeeResponse> findAll(Long businessUnitId);
    EmployeeResponse findById(Long id);
    EmployeeResponse create(EmployeeCreateRequest request);
    EmployeeResponse update(Long id, EmployeeUpdateRequest request);
    void delete(Long id);
}
