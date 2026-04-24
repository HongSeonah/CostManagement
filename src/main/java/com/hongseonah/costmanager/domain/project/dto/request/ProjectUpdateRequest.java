package com.hongseonah.costmanager.domain.project.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.hongseonah.costmanager.domain.project.entity.ProjectStatus;

public record ProjectUpdateRequest(
        @NotBlank String projectName,
        @NotBlank String clientName,
        @NotNull Long businessUnitId,
        @NotNull ProjectStatus status,
        @NotNull @DecimalMin("0.0") BigDecimal budgetAmount,
        LocalDate startDate,
        LocalDate endDate
) {
}
