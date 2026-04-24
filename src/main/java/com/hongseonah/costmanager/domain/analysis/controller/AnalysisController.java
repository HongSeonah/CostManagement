package com.hongseonah.costmanager.domain.analysis.controller;

import com.hongseonah.costmanager.common.response.ApiResponse;
import com.hongseonah.costmanager.domain.analysis.dto.response.AnalysisSummaryResponse;
import com.hongseonah.costmanager.domain.analysis.service.AnalysisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping("/summary")
    public ApiResponse<AnalysisSummaryResponse> summary(@RequestParam(required = false) String month) {
        return ApiResponse.success(analysisService.getSummary(month));
    }
}
