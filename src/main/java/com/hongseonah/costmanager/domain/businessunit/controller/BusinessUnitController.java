package com.hongseonah.costmanager.domain.businessunit.controller;

import com.hongseonah.costmanager.common.response.ApiResponse;
import com.hongseonah.costmanager.domain.businessunit.dto.request.BusinessUnitCreateRequest;
import com.hongseonah.costmanager.domain.businessunit.dto.request.BusinessUnitUpdateRequest;
import com.hongseonah.costmanager.domain.businessunit.dto.response.BusinessUnitResponse;
import com.hongseonah.costmanager.domain.businessunit.service.BusinessUnitService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/business-units")
public class BusinessUnitController {

    private final BusinessUnitService businessUnitService;

    public BusinessUnitController(BusinessUnitService businessUnitService) {
        this.businessUnitService = businessUnitService;
    }

    @GetMapping
    public ApiResponse<List<BusinessUnitResponse>> findAll() {
        return ApiResponse.success(businessUnitService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<BusinessUnitResponse> findById(@PathVariable Long id) {
        return ApiResponse.success(businessUnitService.findById(id));
    }

    @PostMapping
    public ApiResponse<BusinessUnitResponse> create(@Valid @RequestBody BusinessUnitCreateRequest request) {
        return ApiResponse.success("본부를 등록했습니다.", businessUnitService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<BusinessUnitResponse> update(@PathVariable Long id, @Valid @RequestBody BusinessUnitUpdateRequest request) {
        return ApiResponse.success("본부를 수정했습니다.", businessUnitService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        businessUnitService.delete(id);
        return ApiResponse.<Void>success("본부를 삭제했습니다.", null);
    }
}
