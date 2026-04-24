package com.hongseonah.costmanager.domain.businessunit.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BusinessUnitCreateRequest(
        @NotBlank String unitCode,
        @NotBlank String unitName,
        @NotBlank String managerName,
        @NotNull @Min(1) Integer activeProjectLimit
) {
}
