package com.mls.logistics.service;

import com.mls.logistics.domain.Order;
import com.mls.logistics.domain.OrderItem;
import com.mls.logistics.domain.Shipment;
import com.mls.logistics.domain.Vehicle;
import com.mls.logistics.domain.Warehouse;
import com.mls.logistics.dto.request.AdjustStockRequest;
import com.mls.logistics.dto.request.CreateShipmentRequest;
import com.mls.logistics.dto.request.UpdateShipmentRequest;
import com.mls.logistics.exception.InsufficientStockException;
import com.mls.logistics.exception.InvalidRequestException;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.repository.ShipmentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Shipment-related business operations.
 *
 * <p>This service sits between controllers and repositories and is responsible for applying
 * shipment business rules while keeping controllers free of business logic.</p>
 *
 * <h3>Fulfillment rule</h3>
 * <ul>
 *   <li>When a shipment transitions to {@code DELIVERED}, the system deducts stock from the
 *   shipment's origin warehouse for each item in the associated order.</li>
 *   <li>Stock deduction is performed via {@link StockService#adjustStock(Long, com.mls.logistics.dto.request.AdjustStockRequest)}
 *   to guarantee that each deduction produces a {@code Movement} audit record (EXIT).</li>
 * </ul>
 *
 * <p><strong>Important:</strong> If a shipment is already {@code DELIVERED}, it cannot be reverted
 * to a non-delivered status. This avoids accidental double accounting and keeps the audit trail deterministic.</p>
 */
@Service
@Transactional(readOnly = true)
public class ShipmentService {

    private static final String STATUS_DELIVERED = "DELIVERED";

    private final ShipmentRepository shipmentRepository;
    private final OrderItemService orderItemService;
    private final StockService stockService;

    /**
     * Constructor-based dependency injection.
     *
     * @param shipmentRepository repository for shipment persistence
     * @param orderItemService   used to load order items during fulfillment
     * @param stockService       used to deduct stock and generate movement audit records
     */
    public ShipmentService(ShipmentRepository shipmentRepository,
                           OrderItemService orderItemService,
                           StockService stockService) {
        this.shipmentRepository = shipmentRepository;
        this.orderItemService = orderItemService;
        this.stockService = stockService;
    }

    /**
     * Retrieves all registered shipments.
     *
     * @return all shipments
     */
    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    /**
     * Retrieves all registered shipments with sorting.
     *
     * @param sort sorting configuration
     * @return sorted list of shipments
     */
    public List<Shipment> getAllShipments(Sort sort) {
        return shipmentRepository.findAll(sort);
    }

    /**
     * Retrieves a shipment by its identifier.
     *
     * @param id shipment identifier
     * @return optional shipment
     */
    public Optional<Shipment> getShipmentById(Long id) {
        return shipmentRepository.findById(id);
    }

    /**
     * Lists shipments associated with a given order.
     */
    public List<Shipment> getShipmentsByOrderId(Long orderId, Sort sort) {
        return shipmentRepository.findByOrderId(orderId, sort);
    }

    /**
     * Creates a new shipment.
     *
     * <p>If the shipment is created with status {@code DELIVERED}, fulfillment is executed immediately.</p>
     *
     * @param shipment shipment entity
     * @return created shipment
     */
    @Transactional
    public Shipment createShipment(Shipment shipment) {
        Shipment saved = shipmentRepository.save(shipment);

        if (isDelivered(saved.getStatus())) {
            fulfillShipment(saved);
        }

        return saved;
    }

    /**
     * Creates a new shipment from a DTO request.
     *
     * <p>This keeps API contracts (DTOs) separate from domain entities. The request is mapped to a
     * {@link Shipment} with references to {@link Order}, {@link Vehicle} and {@link Warehouse} using IDs.</p>
     *
     * <p>If the shipment is created with status {@code DELIVERED}, fulfillment is executed immediately.</p>
     *
     * @param request create shipment request
     * @return created shipment
     */
    @Transactional
    public Shipment createShipment(CreateShipmentRequest request) {
        Shipment shipment = new Shipment();

        Order order = new Order();
        order.setId(request.getOrderId());

        Vehicle vehicle = new Vehicle();
        vehicle.setId(request.getVehicleId());

        Warehouse warehouse = new Warehouse();
        warehouse.setId(request.getWarehouseId());

        shipment.setOrder(order);
        shipment.setVehicle(vehicle);
        shipment.setWarehouse(warehouse);
        shipment.setStatus(request.getStatus());

        Shipment saved = shipmentRepository.save(shipment);

        if (isDelivered(saved.getStatus())) {
            fulfillShipment(saved);
        }

        return saved;
    }

    /**
     * Updates an existing shipment.
     *
     * <p>Only non-null fields from {@code request} are applied.</p>
     *
     * <p>When the shipment transitions from a non-delivered status to {@code DELIVERED}, this method
     * triggers fulfillment (stock deductions + movement audit entries).</p>
     *
     * @param id      shipment identifier
     * @param request update request
     * @return updated shipment
     * @throws ResourceNotFoundException if shipment doesn't exist
     * @throws InvalidRequestException if attempting to revert a delivered shipment or if required references are missing
     * @throws InsufficientStockException if stock is insufficient to fulfill the order from the origin warehouse
     */
    @Transactional
    public Shipment updateShipment(Long id, UpdateShipmentRequest request) {
        Shipment shipment = shipmentRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "id", id));

        String previousStatus = shipment.getStatus();

        if (request.getOrderId() != null) {
            Order order = new Order();
            order.setId(request.getOrderId());
            shipment.setOrder(order);
        }
        if (request.getVehicleId() != null) {
            Vehicle vehicle = new Vehicle();
            vehicle.setId(request.getVehicleId());
            shipment.setVehicle(vehicle);
        }
        if (request.getWarehouseId() != null) {
            Warehouse warehouse = new Warehouse();
            warehouse.setId(request.getWarehouseId());
            shipment.setWarehouse(warehouse);
        }
        if (request.getStatus() != null) {
            shipment.setStatus(request.getStatus());
        }

        String nextStatus = shipment.getStatus();

        if (isDelivered(previousStatus) && !isDelivered(nextStatus)) {
            throw new InvalidRequestException(
                    "Cannot revert a DELIVERED shipment to a non-delivered status. Shipment id: " + id
            );
        }

        if (!isDelivered(previousStatus) && isDelivered(nextStatus)) {
            fulfillShipment(shipment);
        }

        return shipmentRepository.save(shipment);
    }

    /**
     * Deletes a shipment by ID.
     *
     * @param id shipment identifier
     * @throws ResourceNotFoundException if shipment doesn't exist
     */
    @Transactional
    public void deleteShipment(Long id) {
        if (!shipmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Shipment", "id", id);
        }
        shipmentRepository.deleteById(id);
    }

    public long countByStatus(String status) {
        return shipmentRepository.countByStatus(status);
    }

    /**
     * Executes shipment fulfillment.
     *
     * <p>For each {@link OrderItem} of the shipment's order, the corresponding stock record is located
     * in the shipment's origin warehouse, then adjusted by a negative delta (EXIT). The underlying
     * {@link StockService} ensures that each adjustment is audited as a {@code Movement} record.</p>
     *
     * <p>This method assumes it is called only when transitioning to {@code DELIVERED} (or creating
     * a shipment already delivered), and is designed to fail fast when required references are missing.</p>
     *
     * @param shipment persisted shipment
     */
    private void fulfillShipment(Shipment shipment) {
        if (shipment.getId() == null) {
            throw new InvalidRequestException("Shipment must be persisted before fulfillment.");
        }
        if (shipment.getOrder() == null || shipment.getOrder().getId() == null) {
            throw new InvalidRequestException(
                    "Cannot deliver shipment without an associated order. Shipment id: " + shipment.getId()
            );
        }
        if (shipment.getWarehouse() == null || shipment.getWarehouse().getId() == null) {
            throw new InvalidRequestException(
                    "Cannot deliver shipment without an origin warehouse. Shipment id: " + shipment.getId()
            );
        }

        Long orderId = shipment.getOrder().getId();
        Long warehouseId = shipment.getWarehouse().getId();
        Long shipmentId = shipment.getId();

        Sort itemSort = Sort.by(Sort.Direction.ASC, "id");
        List<OrderItem> items = orderItemService.getOrderItemsByOrderId(orderId, itemSort);

        for (OrderItem item : items) {
            if (item.getResource() == null || item.getResource().getId() == null) {
                throw new InvalidRequestException(
                        "Order item is missing a resource reference. Order id: " + orderId
                );
            }

            int quantity = item.getQuantity();
            if (quantity <= 0) {
                throw new InvalidRequestException(
                        "Order item quantity must be positive. Provided: " + quantity + ". Order id: " + orderId
                );
            }

            Long resourceId = item.getResource().getId();
            var stock = stockService
                    .getStockByResourceAndWarehouse(resourceId, warehouseId)
                    .orElseThrow(() -> new InsufficientStockException(
                            "No stock record found for resource id: " + resourceId +
                                    " in warehouse id: " + warehouseId +
                                    ". Cannot deliver shipment id: " + shipment.getId()
                    ));

                stockService.adjustStock(
                    stock.getId(),
                    new AdjustStockRequest(
                        -quantity,
                        "Shipment delivered",
                        orderId,
                        shipmentId
                    )
                );
        }
    }

    /**
     * Case-insensitive check for DELIVERED status.
     */
    private boolean isDelivered(String status) {
        return status != null && STATUS_DELIVERED.equalsIgnoreCase(status.trim());
    }
}
