package com.hongseonah.costmanager.domain.employee.controller;

import com.hongseonah.costmanager.common.response.ApiResponse;
import com.hongseonah.costmanager.domain.employee.dto.request.EmployeeCreateRequest;
import com.hongseonah.costmanager.domain.employee.dto.request.EmployeeUpdateRequest;
import com.hongseonah.costmanager.domain.employee.dto.response.EmployeeResponse;
import com.hongseonah.costmanager.domain.employee.service.EmployeeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ApiResponse<List<EmployeeResponse>> findAll(@RequestParam(required = false) Long businessUnitId) {
        return ApiResponse.success(employeeService.findAll(businessUnitId));
    }

    @GetMapping("/{id}")
    public ApiResponse<EmployeeResponse> findById(@PathVariable Long id) {
        return ApiResponse.success(employeeService.findById(id));
    }

    @PostMapping
    public ApiResponse<EmployeeResponse> create(@Valid @RequestBody EmployeeCreateRequest request) {
        return ApiResponse.success("인력을 등록했습니다.", employeeService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<EmployeeResponse> update(@PathVariable Long id, @Valid @RequestBody EmployeeUpdateRequest request) {
        return ApiResponse.success("인력을 수정했습니다.", employeeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ApiResponse.<Void>success("인력을 삭제했습니다.", null);
    }
}
