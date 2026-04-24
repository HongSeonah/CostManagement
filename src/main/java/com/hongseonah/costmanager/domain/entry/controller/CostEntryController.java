package com.hongseonah.costmanager.domain.entry.controller;

import com.hongseonah.costmanager.common.response.ApiResponse;
import com.hongseonah.costmanager.domain.entry.dto.request.CostEntryCreateRequest;
import com.hongseonah.costmanager.domain.entry.dto.response.CostEntryResponse;
import com.hongseonah.costmanager.domain.entry.service.CostEntryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cost-entries")
public class CostEntryController {

    private final CostEntryService costEntryService;

    public CostEntryController(CostEntryService costEntryService) {
        this.costEntryService = costEntryService;
    }

    @GetMapping
    public ApiResponse<List<CostEntryResponse>> findAll() {
        return ApiResponse.success(costEntryService.findAll());
    }

    @PostMapping
    public ApiResponse<CostEntryResponse> create(@Valid @RequestBody CostEntryCreateRequest request) {
        return ApiResponse.success("원가 항목을 등록했습니다.", costEntryService.create(request));
    }
}

