package com.mls.logistics.dto.request;

/**
 * Data Transfer Object for creating a new Shipment.
 * 
 * This class defines the structure of data accepted by the API
 * when creating a shipment, separating API contracts from
 * domain entities.
 */
public class CreateShipmentRequest {

    private Long orderId;
    private Long vehicleId;
    private Long warehouseId;
    private String status;

    /**
     * Default constructor for deserialization.
     */
    public CreateShipmentRequest() {
    }

    /**
     * Constructs a CreateShipmentRequest with all fields.
     *
     * @param orderId order identifier
     * @param vehicleId vehicle identifier
     * @param warehouseId warehouse identifier
     * @param status shipment status
     */
    public CreateShipmentRequest(Long orderId, Long vehicleId, Long warehouseId, String status) {
        this.orderId = orderId;
        this.vehicleId = vehicleId;
        this.warehouseId = warehouseId;
        this.status = status;
    }

    // Getters and setters

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
