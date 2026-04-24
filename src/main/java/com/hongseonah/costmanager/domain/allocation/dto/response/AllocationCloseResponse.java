package com.hongseonah.costmanager.domain.allocation.dto.response;

import java.time.LocalDateTime;

public record AllocationCloseResponse(
        String month,
        LocalDateTime closedAt,
        boolean alreadyClosed
) {
}
