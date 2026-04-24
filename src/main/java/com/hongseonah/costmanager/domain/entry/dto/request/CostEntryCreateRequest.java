package com.hongseonah.costmanager.domain.entry.dto.request;

import com.hongseonah.costmanager.domain.entry.entity.CostEntryCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CostEntryCreateRequest(
        @NotNull Long projectId,
        @NotNull LocalDate entryDate,
        @NotNull CostEntryCategory category,
        @NotBlank String itemName,
        @NotNull @DecimalMin("0.0") BigDecimal amount,
        String memo
) {
}

