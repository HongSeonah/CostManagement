package com.hongseonah.costmanager.domain.standardcost.controller;

import com.hongseonah.costmanager.common.response.ApiResponse;
import com.hongseonah.costmanager.domain.standardcost.dto.request.StandardCostCreateRequest;
import com.hongseonah.costmanager.domain.standardcost.dto.request.StandardCostUpdateRequest;
import com.hongseonah.costmanager.domain.standardcost.dto.response.StandardCostResponse;
import com.hongseonah.costmanager.domain.standardcost.service.StandardCostService;
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
@RequestMapping("/api/standard-costs")
public class StandardCostController {

    private final StandardCostService standardCostService;

    public StandardCostController(StandardCostService standardCostService) {
        this.standardCostService = standardCostService;
    }

    @GetMapping
    public ApiResponse<List<StandardCostResponse>> findAll(@RequestParam(required = false) String month) {
        return ApiResponse.success(standardCostService.findAll(month));
    }

    @GetMapping("/{id}")
    public ApiResponse<StandardCostResponse> findById(@PathVariable Long id) {
        return ApiResponse.success(standardCostService.findById(id));
    }

    @PostMapping
    public ApiResponse<StandardCostResponse> create(@Valid @RequestBody StandardCostCreateRequest request) {
        return ApiResponse.success("표준원가를 등록했습니다.", standardCostService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<StandardCostResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody StandardCostUpdateRequest request) {
        return ApiResponse.success("표준원가를 수정했습니다.", standardCostService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        standardCostService.delete(id);
        return ApiResponse.<Void>success("표준원가를 삭제했습니다.", null);
    }
}
