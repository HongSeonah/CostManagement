package com.hongseonah.costmanager.domain.allocation.controller;

import com.hongseonah.costmanager.common.response.ApiResponse;
import com.hongseonah.costmanager.domain.allocation.dto.request.AllocationCloseRequest;
import com.hongseonah.costmanager.domain.allocation.dto.response.AllocationCloseResponse;
import com.hongseonah.costmanager.domain.allocation.dto.response.AllocationSummaryResponse;
import com.hongseonah.costmanager.domain.allocation.service.AllocationService;
import java.nio.charset.StandardCharsets;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/close")
    public ApiResponse<AllocationCloseResponse> close(@RequestBody AllocationCloseRequest request) {
        return ApiResponse.success("월 마감을 확정했습니다.", allocationService.closeMonth(request.month()));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam(required = false) String month) {
        byte[] bytes = allocationService.exportMonth(month);
        String fileName = "cost-management-allocation-" + allocationService.getSummary(month).month() + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(fileName, StandardCharsets.UTF_8)
                                .build()
                                .toString())
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }
}
