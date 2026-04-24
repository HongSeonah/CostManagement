package com.hongseonah.costmanager.domain.allocation.controller;

import com.hongseonah.costmanager.common.response.ApiResponse;
import com.hongseonah.costmanager.domain.allocation.dto.response.AllocationSummaryResponse;
import com.hongseonah.costmanager.domain.allocation.service.AllocationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/allocation")
public class AllocationController {

    private final AllocationService allocationService;

    public AllocationController(AllocationService allocationService) {
        this.allocationService = allocationService;
    }

    @GetMapping("/summary")
    public ApiResponse<AllocationSummaryResponse> summary(@RequestParam(required = false) String month) {
        return ApiResponse.success(allocationService.getSummary(month));
    }
}
