package com.hongseonah.costmanager.domain.employee.dto.response;

import com.hongseonah.costmanager.domain.employee.entity.Employee;
import com.hongseonah.costmanager.domain.employee.entity.EmployeeStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmployeeResponse(
        Long id,
        String employeeCode,
        String employeeName,
        String positionName,
        Long businessUnitId,
        String businessUnitCode,
        String businessUnitName,
        Long assignedProjectId,
        String assignedProjectCode,
        String assignedProjectName,
        EmployeeStatus status,
        BigDecimal monthlyLaborCost,
        LocalDate joinedDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getEmployeeName(),
                employee.getPositionName(),
                employee.getBusinessUnit().getId(),
                employee.getBusinessUnit().getUnitCode(),
                employee.getBusinessUnit().getUnitName(),
                employee.getAssignedProject() == null ? null : employee.getAssignedProject().getId(),
                employee.getAssignedProject() == null ? null : employee.getAssignedProject().getProjectCode(),
                employee.getAssignedProject() == null ? null : employee.getAssignedProject().getProjectName(),
                employee.getStatus(),
                employee.getMonthlyLaborCost(),
                employee.getJoinedDate(),
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
    }
}
