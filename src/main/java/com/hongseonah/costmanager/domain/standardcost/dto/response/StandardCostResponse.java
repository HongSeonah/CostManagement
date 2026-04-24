package com.hongseonah.costmanager.domain.standardcost.dto.response;

import com.hongseonah.costmanager.domain.standardcost.entity.StandardCostPlan;
import com.hongseonah.costmanager.domain.standardcost.entity.StandardCostBasisType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StandardCostResponse(
        Long id,
        String standardCode,
        String planMonth,
        Long businessUnitId,
        String businessUnitName,
        Long projectId,
        String projectName,
        StandardCostBasisType basisType,
        BigDecimal standardAmount,
        String memo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static StandardCostResponse from(StandardCostPlan plan) {
        return new StandardCostResponse(
                plan.getId(),
                plan.getStandardCode(),
                plan.getPlanMonth(),
                plan.getBusinessUnit().getId(),
                plan.getBusinessUnit().getUnitName(),
                plan.getProject() == null ? null : plan.getProject().getId(),
                plan.getProject() == null ? null : plan.getProject().getProjectName(),
                plan.getBasisType(),
                plan.getStandardAmount(),
                plan.getMemo(),
                plan.getCreatedAt(),
                plan.getUpdatedAt()
        );
    }
}
