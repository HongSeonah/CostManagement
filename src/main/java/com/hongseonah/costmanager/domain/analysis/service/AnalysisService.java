package com.hongseonah.costmanager.domain.analysis.service;

import com.hongseonah.costmanager.domain.analysis.dto.response.AnalysisSummaryResponse;

public interface AnalysisService {

    AnalysisSummaryResponse getSummary(String month);
}
