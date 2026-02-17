package com.mls.logistics.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for adjusting stock quantity.
 *
 * Uses a delta (difference) instead of an absolute value intentionally.
 * In logistics, stock changes are always expressed as movements:
 * "10 units entered" or "5 units exited", never "quantity is now 45".
 *
 * Positive delta = ENTRY (stock increases)
 * Negative delta = EXIT  (stock decreases)
 */
public class AdjustStockRequest {

    @NotNull(message = "Delta is required")
    private Integer delta;

    public AdjustStockRequest() {
    }

    public AdjustStockRequest(Integer delta) {
        this.delta = delta;
    }

    public Integer getDelta() {
        return delta;
    }

    public void setDelta(Integer delta) {
        this.delta = delta;
    }
}