package com.hongseonah.costmanager.domain.project.dto.response;

import com.hongseonah.costmanager.domain.project.entity.CostProject;
import com.hongseonah.costmanager.domain.project.entity.ProjectStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProjectResponse(
        Long id,
        String projectCode,
        String projectName,
        String clientName,
        Long businessUnitId,
        String businessUnitCode,
        String businessUnitName,
        String managerName,
        ProjectStatus status,
        BigDecimal budgetAmount,
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        LocalDate startDate,
        LocalDate endDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ProjectResponse from(CostProject project) {
        BigDecimal remaining = project.getBudgetAmount().subtract(project.getSpentAmount());
        return new ProjectResponse(
                project.getId(),
                project.getProjectCode(),
                project.getProjectName(),
                project.getClientName(),
                project.getBusinessUnit().getId(),
                project.getBusinessUnit().getUnitCode(),
                project.getBusinessUnit().getUnitName(),
                project.getBusinessUnit().getManagerName(),
                project.getStatus(),
                project.getBudgetAmount(),
                project.getSpentAmount(),
                remaining,
                project.getStartDate(),
                project.getEndDate(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
