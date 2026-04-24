package com.hongseonah.costmanager.domain.internaltransfer.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record InternalTransferUpdateRequest(
        @NotNull LocalDate transferDate,
        @NotNull Long sourceBusinessUnitId,
        @NotNull Long targetBusinessUnitId,
        Long sourceProjectId,
        Long targetProjectId,
        @NotNull @Min(0) BigDecimal amount,
        String memo
) {
}
