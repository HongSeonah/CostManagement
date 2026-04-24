package com.hongseonah.costmanager.domain.employee.dto.request;

import com.hongseonah.costmanager.domain.employee.entity.EmployeeStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeCreateRequest(
        @NotBlank String employeeCode,
        @NotBlank String employeeName,
        @NotBlank String positionName,
        @NotNull Long businessUnitId,
        Long assignedProjectId,
        @NotNull EmployeeStatus status,
        @NotNull @Min(0) BigDecimal monthlyLaborCost,
        LocalDate joinedDate
) {
}
