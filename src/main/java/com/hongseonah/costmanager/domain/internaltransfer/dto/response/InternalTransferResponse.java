package com.hongseonah.costmanager.domain.internaltransfer.dto.response;

import com.hongseonah.costmanager.domain.internaltransfer.entity.InternalTransfer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record InternalTransferResponse(
        Long id,
        String transferCode,
        LocalDate transferDate,
        String transferMonth,
        Long sourceBusinessUnitId,
        String sourceBusinessUnitName,
        Long targetBusinessUnitId,
        String targetBusinessUnitName,
        Long sourceProjectId,
        String sourceProjectName,
        Long targetProjectId,
        String targetProjectName,
        BigDecimal amount,
        String memo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static InternalTransferResponse from(InternalTransfer transfer) {
        return new InternalTransferResponse(
                transfer.getId(),
                transfer.getTransferCode(),
                transfer.getTransferDate(),
                transfer.getTransferDate() == null ? null : transfer.getTransferDate().toString().substring(0, 7),
                transfer.getSourceBusinessUnit().getId(),
                transfer.getSourceBusinessUnit().getUnitName(),
                transfer.getTargetBusinessUnit().getId(),
                transfer.getTargetBusinessUnit().getUnitName(),
                transfer.getSourceProject() == null ? null : transfer.getSourceProject().getId(),
                transfer.getSourceProject() == null ? null : transfer.getSourceProject().getProjectName(),
                transfer.getTargetProject() == null ? null : transfer.getTargetProject().getId(),
                transfer.getTargetProject() == null ? null : transfer.getTargetProject().getProjectName(),
                transfer.getAmount(),
                transfer.getMemo(),
                transfer.getCreatedAt(),
                transfer.getUpdatedAt()
        );
    }
}
