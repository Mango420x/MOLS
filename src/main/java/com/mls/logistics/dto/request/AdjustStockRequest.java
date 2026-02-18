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

    /** Optional reason label for auditing/UI (kept short intentionally). */
    private String reason;

    /** Optional order id that caused this stock change (traceability). */
    private Long orderId;

    /** Optional shipment id that caused this stock change (traceability). */
    private Long shipmentId;

    public AdjustStockRequest() {
    }

    public AdjustStockRequest(Integer delta) {
        this.delta = delta;
    }

    public AdjustStockRequest(Integer delta, String reason, Long orderId, Long shipmentId) {
        this.delta = delta;
        this.reason = reason;
        this.orderId = orderId;
        this.shipmentId = shipmentId;
    }

    public Integer getDelta() {
        return delta;
    }

    public void setDelta(Integer delta) {
        this.delta = delta;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Long shipmentId) {
        this.shipmentId = shipmentId;
    }
}