package com.hongseonah.costmanager.domain.standardcost.dto.request;

import com.hongseonah.costmanager.domain.standardcost.entity.StandardCostBasisType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record StandardCostUpdateRequest(
        @NotBlank String planMonth,
        @NotNull Long businessUnitId,
        Long projectId,
        @NotNull StandardCostBasisType basisType,
        @NotNull @Min(0) BigDecimal standardAmount,
        String memo
) {
}
