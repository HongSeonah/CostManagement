package com.hongseonah.costmanager.domain.entry.dto.response;

import com.hongseonah.costmanager.domain.entry.entity.CostEntry;
import com.hongseonah.costmanager.domain.entry.entity.CostEntryCategory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CostEntryResponse(
        Long id,
        Long projectId,
        String projectCode,
        String projectName,
        LocalDate entryDate,
        CostEntryCategory category,
        String itemName,
        BigDecimal amount,
        String memo,
        LocalDateTime createdAt
) {
    public static CostEntryResponse from(CostEntry entry) {
        return new CostEntryResponse(
                entry.getId(),
                entry.getProject().getId(),
                entry.getProject().getProjectCode(),
                entry.getProject().getProjectName(),
                entry.getEntryDate(),
                entry.getCategory(),
                entry.getItemName(),
                entry.getAmount(),
                entry.getMemo(),
                entry.getCreatedAt()
        );
    }
}

