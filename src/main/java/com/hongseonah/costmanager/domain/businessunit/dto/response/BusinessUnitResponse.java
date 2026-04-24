package com.hongseonah.costmanager.domain.businessunit.dto.response;

import com.hongseonah.costmanager.domain.businessunit.entity.BusinessUnit;
import java.time.LocalDateTime;

public record BusinessUnitResponse(
        Long id,
        String unitCode,
        String unitName,
        String managerName,
        Integer activeProjectLimit,
        LocalDateTime createdAt
) {
    public static BusinessUnitResponse from(BusinessUnit unit) {
        return new BusinessUnitResponse(
                unit.getId(),
                unit.getUnitCode(),
                unit.getUnitName(),
                unit.getManagerName(),
                unit.getActiveProjectLimit(),
                unit.getCreatedAt()
        );
    }
}

