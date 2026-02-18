package com.mls.logistics.web;

import com.mls.logistics.service.OrderService;
import com.mls.logistics.service.OrderItemService;
import com.mls.logistics.service.MovementService;
import com.mls.logistics.service.ResourceService;
import com.mls.logistics.service.ShipmentService;
import com.mls.logistics.service.StockService;
import com.mls.logistics.service.UnitService;
import com.mls.logistics.service.VehicleService;
import com.mls.logistics.service.WarehouseService;
import com.mls.logistics.dto.request.AdjustStockRequest;
import com.mls.logistics.dto.request.CreateOrderItemRequest;
import com.mls.logistics.dto.request.CreateOrderRequest;
import com.mls.logistics.dto.request.CreateResourceRequest;
import com.mls.logistics.dto.request.CreateShipmentRequest;
import com.mls.logistics.dto.request.CreateUnitRequest;
import com.mls.logistics.dto.request.CreateVehicleRequest;
import com.mls.logistics.dto.request.CreateWarehouseRequest;
import com.mls.logistics.dto.request.CreateStockRequest;
import com.mls.logistics.dto.request.UpdateOrderItemRequest;
import com.mls.logistics.dto.request.UpdateOrderRequest;
import com.mls.logistics.dto.request.UpdateResourceRequest;
import com.mls.logistics.dto.request.UpdateShipmentRequest;
import com.mls.logistics.dto.request.UpdateUnitRequest;
import com.mls.logistics.dto.request.UpdateVehicleRequest;
import com.mls.logistics.dto.request.UpdateWarehouseRequest;
import com.mls.logistics.exception.InsufficientStockException;
import com.mls.logistics.exception.InvalidRequestException;
import com.mls.logistics.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping
/**
 * Thymeleaf UI controller for the admin web interface.
 *
 * Responsibilities:
 * - Serve server-rendered pages under /ui/**
 * - Provide admin-friendly validation and flash messages
 * - Delegate all business logic to services (no direct repository access)
 * - Support server-side sorting for list tables
 */
public class UiController {

    private static final String SESSION_ORDER_DRAFT_ITEMS = "orderDraftItems";
    private static final String SESSION_ORDER_DRAFT_HEADER = "orderDraftHeader";

    private final WarehouseService warehouseService;
    private final VehicleService vehicleService;
    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final UnitService unitService;
    private final ResourceService resourceService;
    private final ShipmentService shipmentService;
    private final StockService stockService;
    private final MovementService movementService;

    public UiController(WarehouseService warehouseService,
                        VehicleService vehicleService,
                        OrderService orderService,
                        OrderItemService orderItemService,
                        UnitService unitService,
                        ResourceService resourceService,
                        ShipmentService shipmentService,
                        StockService stockService,
                        MovementService movementService) {
        this.warehouseService = warehouseService;
        this.vehicleService = vehicleService;
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        this.unitService = unitService;
        this.resourceService = resourceService;
        this.shipmentService = shipmentService;
        this.stockService = stockService;
        this.movementService = movementService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/ui";
    }

    @GetMapping("/ui")
    public String dashboard(Model model) {
        model.addAttribute("warehouseCount", safeCount(() -> warehouseService.getAllWarehouses().size(), model));
        model.addAttribute("vehicleCount", safeCount(() -> vehicleService.getAllVehicles().size(), model));
        model.addAttribute("orderCount", safeCount(() -> orderService.getAllOrders().size(), model));
        model.addAttribute("unitCount", safeCount(() -> unitService.getAllUnits().size(), model));
        return "ui/dashboard";
    }

    @GetMapping("/ui/warehouses")
    public String warehouses(
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "dir", required = false) String dir,
            Model model) {

        String sortKey = (sort == null || sort.isBlank()) ? "id" : sort.trim().toLowerCase();
        String dirValue = (dir == null || dir.isBlank()) ? "asc" : dir.trim().toLowerCase();
        Sort.Direction direction = "desc".equals(dirValue) ? Sort.Direction.DESC : Sort.Direction.ASC;

        String property = switch (sortKey) {
            case "id" -> "id";
            case "location" -> "location";
            case "name" -> "name";
            default -> "name";
        };

        Sort sorting = Sort.by(direction, property).and(Sort.by(Sort.Direction.ASC, "id"));
        model.addAttribute("sortKey", sortKey);
        model.addAttribute("sortDir", direction == Sort.Direction.ASC ? "asc" : "desc");

        model.addAttribute("warehouses", safeList(() -> warehouseService.getAllWarehouses(sorting), model));
        return "ui/warehouses";
    }

    @GetMapping("/ui/resources")
    public String resources(
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "dir", required = false) String dir,
            Model model) {

        String sortKey = (sort == null || sort.isBlank()) ? "id" : sort.trim().toLowerCase();
        String dirValue = (dir == null || dir.isBlank()) ? "asc" : dir.trim().toLowerCase();
        Sort.Direction direction = "desc".equals(dirValue) ? Sort.Direction.DESC : Sort.Direction.ASC;

        String property = switch (sortKey) {
            case "id" -> "id";
            case "name" -> "name";
            case "type" -> "type";
            case "criticality" -> "criticality";
            default -> "id";
        };

        Sort sorting = Sort.by(direction, property).and(Sort.by(Sort.Direction.ASC, "id"));
        model.addAttribute("sortKey", sortKey);
        model.addAttribute("sortDir", direction == Sort.Direction.ASC ? "asc" : "desc");

        model.addAttribute("resources", safeList(() -> resourceService.getAllResources(sorting), model));
        return "ui/resources";
    }

    @GetMapping("/ui/resources/new")
    public String newResource(Model model) {
        if (!model.containsAttribute("resourceForm")) {
            model.addAttribute("resourceForm", new CreateResourceRequest());
        }
        model.addAttribute("formMode", "create");
        return "ui/resource-form";
    }
    @PostMapping("/ui/resources")
    public String createResource(
            @Valid @ModelAttribute("resourceForm") CreateResourceRequest form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("formMode", "create");
            return "ui/resource-form";
        }

        try {
            resourceService.createResource(form);
            redirectAttributes.addFlashAttribute("successMessage", "Resource created successfully.");
            return "redirect:/ui/resources";
        } catch (DataAccessException ex) {
            model.addAttribute("formMode", "create");
            model.addAttribute("errorMessage", "We couldn't save your changes right now. Please try again.");
            return "ui/resource-form";
        }
    }


                @GetMapping("/ui/shipments")
                public String shipments(
                        @RequestParam(value = "sort", required = false) String sort,
                        @RequestParam(value = "dir", required = false) String dir,
                        Model model) {

                    String sortKey = (sort == null || sort.isBlank()) ? "id" : sort.trim().toLowerCase();
                    String dirValue = (dir == null || dir.isBlank()) ? "asc" : dir.trim().toLowerCase();
                    Sort.Direction direction = "desc".equals(dirValue) ? Sort.Direction.DESC : Sort.Direction.ASC;

                    String property = switch (sortKey) {
                        case "id" -> "id";
                        case "order" -> "order.id";
                        case "vehicle" -> "vehicle.id";
                        case "warehouse" -> "warehouse.id";
                        case "status" -> "status";
                        default -> "id";
                    };

                    Sort sorting = Sort.by(direction, property).and(Sort.by(Sort.Direction.ASC, "id"));
                    model.addAttribute("sortKey", sortKey);
                    model.addAttribute("sortDir", direction == Sort.Direction.ASC ? "asc" : "desc");

                    model.addAttribute("shipments", safeList(() -> shipmentService.getAllShipments(sorting), model));
                    return "ui/shipments";
                }

                @GetMapping("/ui/shipments/new")
                public String newShipment(Model model) {
                    if (!model.containsAttribute("shipmentForm")) {
                        model.addAttribute("shipmentForm", new CreateShipmentRequest());
                    }
                    populateShipmentReferenceData(model);
                    model.addAttribute("formMode", "create");
                    return "ui/shipment-form";
                }

                @PostMapping("/ui/shipments")
                public String createShipment(
                        @Valid @ModelAttribute("shipmentForm") CreateShipmentRequest form,
                        BindingResult bindingResult,
                        RedirectAttributes redirectAttributes,
                        Model model) {

                    if (bindingResult.hasErrors()) {
                        populateShipmentReferenceData(model);
                        model.addAttribute("formMode", "create");
                        return "ui/shipment-form";
                    }

                    try {
                        shipmentService.createShipment(form);
                        redirectAttributes.addFlashAttribute("successMessage", "Shipment created successfully.");
                        return "redirect:/ui/shipments";
                    } catch (DataIntegrityViolationException ex) {
                        populateShipmentReferenceData(model);
                        model.addAttribute("formMode", "create");
                        model.addAttribute("errorMessage", "Please verify the selected order, vehicle, and warehouse.");
                        return "ui/shipment-form";
                    } catch (DataAccessException ex) {
                        populateShipmentReferenceData(model);
                        model.addAttribute("formMode", "create");
                        model.addAttribute("errorMessage", "We couldn't save your changes right now. Please try again.");
                        return "ui/shipment-form";
                    }
                }

                @GetMapping("/ui/shipments/{id}/edit")
                public String editShipment(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
                    try {
                        var shipment = shipmentService.getShipmentById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "id", id));

                        if (!model.containsAttribute("shipmentForm")) {
                            CreateShipmentRequest form = new CreateShipmentRequest();
                            if (shipment.getOrder() != null) {
                                form.setOrderId(shipment.getOrder().getId());
                            }
                            if (shipment.getVehicle() != null) {
                                form.setVehicleId(shipment.getVehicle().getId());
                            }
                            if (shipment.getWarehouse() != null) {
                                form.setWarehouseId(shipment.getWarehouse().getId());
                            }
                            form.setStatus(shipment.getStatus());
                            model.addAttribute("shipmentForm", form);
                        }

                        populateShipmentReferenceData(model);
                        model.addAttribute("shipmentId", id);
                        model.addAttribute("formMode", "edit");
                        return "ui/shipment-form";
                    } catch (ResourceNotFoundException ex) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Shipment not found.");
                        return "redirect:/ui/shipments";
                    } catch (DataAccessException ex) {
                        redirectAttributes.addFlashAttribute("errorMessage", "We couldn't load this information right now. Please try again.");
                        return "redirect:/ui/shipments";
                    }
                }

                @PostMapping("/ui/shipments/{id}")
                public String updateShipment(
                        @PathVariable Long id,
                        @Valid @ModelAttribute("shipmentForm") CreateShipmentRequest form,
                        BindingResult bindingResult,
                        RedirectAttributes redirectAttributes,
                        Model model) {

                    if (bindingResult.hasErrors()) {
                        populateShipmentReferenceData(model);
                        model.addAttribute("shipmentId", id);
                        model.addAttribute("formMode", "edit");
                        return "ui/shipment-form";
                    }

                    try {
                        UpdateShipmentRequest request = new UpdateShipmentRequest();
                        request.setOrderId(form.getOrderId());
                        request.setVehicleId(form.getVehicleId());
                        request.setWarehouseId(form.getWarehouseId());
                        request.setStatus(form.getStatus());

                        shipmentService.updateShipment(id, request);
                        redirectAttributes.addFlashAttribute("successMessage", "Shipment updated successfully.");
                        return "redirect:/ui/shipments";
                    } catch (ResourceNotFoundException ex) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Shipment not found.");
                        return "redirect:/ui/shipments";
                    } catch (DataIntegrityViolationException ex) {
                        populateShipmentReferenceData(model);
                        model.addAttribute("shipmentId", id);
                        model.addAttribute("formMode", "edit");
                        model.addAttribute("errorMessage", "Please verify the selected order, vehicle, and warehouse.");
                        return "ui/shipment-form";
                    } catch (DataAccessException ex) {
                        populateShipmentReferenceData(model);
                        model.addAttribute("shipmentId", id);
                        model.addAttribute("formMode", "edit");
                        model.addAttribute("errorMessage", "We couldn't save your changes right now. Please try again.");
                        return "ui/shipment-form";
                    }
                }

                @PostMapping("/ui/shipments/{id}/delete")
                public String deleteShipment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
                    try {
                        shipmentService.deleteShipment(id);
                        redirectAttributes.addFlashAttribute("successMessage", "Shipment deleted successfully.");
                    } catch (DataIntegrityViolationException ex) {
                        redirectAttributes.addFlashAttribute("errorMessage", "This shipment is in use and cannot be deleted.");
                    } catch (DataAccessException ex) {
                        redirectAttributes.addFlashAttribute("errorMessage", "We couldn't complete that action right now. Please try again.");
                    }
                    return "redirect:/ui/shipments";
                }
    @GetMapping("/ui/resources/{id}/edit")
    public String editResource(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            var resource = resourceService.getResourceById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Resource", "id", id));

            if (!model.containsAttribute("resourceForm")) {
                CreateResourceRequest form = new CreateResourceRequest();
                form.setName(resource.getName());
                form.setType(resource.getType());
                form.setCriticality(resource.getCriticality());
                model.addAttribute("resourceForm", form);
            }

            model.addAttribute("resourceId", id);
            model.addAttribute("formMode", "edit");
            return "ui/resource-form";
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Resource not found.");
            return "redirect:/ui/resources";
        } catch (DataAccessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "We couldn't load this information right now. Please try again.");
            return "redirect:/ui/resources";
        }
    }

    @PostMapping("/ui/resources/{id}")
    public String updateResource(
            @PathVariable Long id,
            @Valid @ModelAttribute("resourceForm") CreateResourceRequest form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("resourceId", id);
            model.addAttribute("formMode", "edit");
            return "ui/resource-form";
        }

        try {
            UpdateResourceRequest request = new UpdateResourceRequest();
            request.setName(form.getName());
            request.setType(form.getType());
            request.setCriticality(form.getCriticality());

            resourceService.updateResource(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Resource updated successfully.");
            return "redirect:/ui/resources";
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Resource not found.");
            return "redirect:/ui/resources";
        } catch (DataAccessException ex) {
            model.addAttribute("resourceId", id);
            model.addAttribute("formMode", "edit");
            model.addAttribute("errorMessage", "We couldn't save your changes right now. Please try again.");
            return "ui/resource-form";
        }
    }

    @PostMapping("/ui/resources/{id}/delete")
    public String deleteResource(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            resourceService.deleteResource(id);
            redirectAttributes.addFlashAttribute("successMessage", "Resource deleted successfully.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "This resource is in use and cannot be deleted.");
        } catch (DataAccessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "We couldn't complete that action right now. Please try again.");
        }
        return "redirect:/ui/resources";
    }

    @GetMapping("/ui/warehouses/new")
    public String newWarehouse(Model model) {
        if (!model.containsAttribute("warehouseForm")) {
            model.addAttribute("warehouseForm", new CreateWarehouseRequest());
        }
        model.addAttribute("formMode", "create");
        return "ui/warehouse-form";
    }

    @PostMapping("/ui/warehouses")
    public String createWarehouse(
            @Valid @ModelAttribute("warehouseForm") CreateWarehouseRequest form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("formMode", "create");
            return "ui/warehouse-form";
        }

        try {
            warehouseService.createWarehouse(form);
            redirectAttributes.addFlashAttribute("successMessage", "Warehouse created successfully.");
            return "redirect:/ui/warehouses";
        } catch (DataAccessException ex) {
            model.addAttribute("formMode", "create");
            model.addAttribute("errorMessage", "We couldn't save your changes right now. Please try again.");
            return "ui/warehouse-form";
        }
    }

    @GetMapping("/ui/warehouses/{id}/edit")
    public String editWarehouse(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            var warehouse = warehouseService.getWarehouseById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));

            if (!model.containsAttribute("warehouseForm")) {
                CreateWarehouseRequest form = new CreateWarehouseRequest();
                form.setName(warehouse.getName());
                form.setLocation(warehouse.getLocation());
                model.addAttribute("warehouseForm", form);
            }
            model.addAttribute("warehouseId", id);
            model.addAttribute("formMode", "edit");
            return "ui/warehouse-form";
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Warehouse not found.");
            return "redirect:/ui/warehouses";
        } catch (DataAccessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "We couldn't load this information right now. Please try again.");
            return "redirect:/ui/warehouses";
        }
    }

    @PostMapping("/ui/warehouses/{id}")
    public String updateWarehouse(
            @PathVariable Long id,
            @Valid @ModelAttribute("warehouseForm") CreateWarehouseRequest form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("warehouseId", id);
            model.addAttribute("formMode", "edit");
            return "ui/warehouse-form";
        }

        try {
            UpdateWarehouseRequest request = new UpdateWarehouseRequest();
            request.setName(form.getName());
            request.setLocation(form.getLocation());

            warehouseService.updateWarehouse(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Warehouse updated successfully.");
            return "redirect:/ui/warehouses";
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Warehouse not found.");
            return "redirect:/ui/warehouses";
        } catch (DataAccessException ex) {
            model.addAttribute("warehouseId", id);
            model.addAttribute("formMode", "edit");
            model.addAttribute("errorMessage", "We couldn't save your changes right now. Please try again.");
            return "ui/warehouse-form";
        }
    }

    @PostMapping("/ui/warehouses/{id}/delete")
    public String deleteWarehouse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            warehouseService.deleteWarehouse(id);
            redirectAttributes.addFlashAttribute("successMessage", "Warehouse deleted successfully.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete warehouse because it is referenced by other records.");
        } catch (DataAccessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "We couldn't complete that action right now. Please try again.");
        }
        return "redirect:/ui/warehouses";
    }

    @GetMapping("/ui/vehicles")
    public String vehicles(
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "dir", required = false) String dir,
            Model model) {

        String sortKey = (sort == null || sort.isBlank()) ? "id" : sort.trim().toLowerCase();
        String dirValue = (dir == null || dir.isBlank()) ? "asc" : dir.trim().toLowerCase();
        Sort.Direction direction = "desc".equals(dirValue) ? Sort.Direction.DESC : Sort.Direction.ASC;

        String property = switch (sortKey) {
            case "id" -> "id";
            case "capacity" -> "capacity";
            case "status" -> "status";
            case "type" -> "type";
            default -> "type";
        };

        Sort sorting = Sort.by(direction, property).and(Sort.by(Sort.Direction.ASC, "id"));
        model.addAttribute("sortKey", sortKey);
        model.addAttribute("sortDir", direction == Sort.Direction.ASC ? "asc" : "desc");

        model.addAttribute("vehicles", safeList(() -> vehicleService.getAllVehicles(sorting), model));
        return "ui/vehicles";
    }

    @GetMapping("/ui/vehicles/new")
    public String newVehicle(Model model) {
        if (!model.containsAttribute("vehicleForm")) {
            model.addAttribute("vehicleForm", new CreateVehicleRequest());
        }
        model.addAttribute("formMode", "create");
        return "ui/vehicle-form";
    }

    @PostMapping("/ui/vehicles")
    public String createVehicle(
            @Valid @ModelAttribute("vehicleForm") CreateVehicleRequest form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("formMode", "create");
            return "ui/vehicle-form";
        }

        try {
            vehicleService.createVehicle(form);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle created successfully.");
            return "redirect:/ui/vehicles";
        } catch (DataAccessException ex) {
            model.addAttribute("formMode", "create");
            model.addAttribute("errorMessage", "We couldn't save your changes right now. Please try again.");
            return "ui/vehicle-form";
        }
    }

    @GetMapping("/ui/vehicles/{id}/edit")
    public String editVehicle(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            var vehicle = vehicleService.getVehicleById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));

            if (!model.containsAttribute("vehicleForm")) {
                CreateVehicleRequest form = new CreateVehicleRequest();
                form.setType(normalizeVehicleType(vehicle.getType()));
                form.setCapacity(vehicle.getCapacity());
                form.setStatus(normalizeVehicleStatus(vehicle.getStatus()));
                model.addAttribute("vehicleForm", form);
            }

            model.addAttribute("vehicleId", id);
            model.addAttribute("formMode", "edit");
            return "ui/vehicle-form";
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vehicle not found.");
            return "redirect:/ui/vehicles";
        } catch (DataAccessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "We couldn't load this information right now. Please try again.");
            return "redirect:/ui/vehicles";
        }
    }

    @PostMapping("/ui/vehicles/{id}")
    public String updateVehicle(
            @PathVariable Long id,
            @Valid @ModelAttribute("vehicleForm") CreateVehicleRequest form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("vehicleId", id);
            model.addAttribute("formMode", "edit");
            return "ui/vehicle-form";
        }

        try {
            UpdateVehicleRequest request = new UpdateVehicleRequest();
            request.setType(form.getType());
            request.setCapacity(form.getCapacity());
            request.setStatus(form.getStatus());

            vehicleService.updateVehicle(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle updated successfully.");
            return "redirect:/ui/vehicles";
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vehicle not found.");
            return "redirect:/ui/vehicles";
        } catch (DataAccessException ex) {
            model.addAttribute("vehicleId", id);
            model.addAttribute("formMode", "edit");
            model.addAttribute("errorMessage", "We couldn't save your changes right now. Please try again.");
            return "ui/vehicle-form";
        }
    }

    @PostMapping("/ui/vehicles/{id}/delete")
    public String deleteVehicle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            vehicleService.deleteVehicle(id);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle deleted successfully.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete vehicle because it is referenced by other records.");
        } catch (DataAccessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "We couldn't complete that action right now. Please try again.");
        }
        return "redirect:/ui/vehicles";
    }

    @GetMapping("/ui/orders")
    public String orders(
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "dir", required = false) String dir,
            Model model) {

        String sortKey = (sort == null || sort.isBlank()) ? "date" : sort.trim().toLowerCase();
        String dirValue = (dir == null || dir.isBlank()) ? "desc" : dir.trim().toLowerCase();
        Sort.Direction direction = "asc".equals(dirValue) ? Sort.Direction.ASC : Sort.Direction.DESC;

        String property = switch (sortKey) {
            case "id" -> "id";
            case "unit" -> "unit.id";
            case "status" -> "status";
            case "date" -> "dateCreated";
            default -> "dateCreated";
        };

        Sort sorting = Sort.by(direction, property).and(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("sortKey", sortKey);
        model.addAttribute("sortDir", direction == Sort.Direction.ASC ? "asc" : "desc");

        List<com.mls.logistics.domain.Order> orders = safeList(() -> orderService.getAllOrders(sorting), model);
        model.addAttribute("orders", orders);

        List<Long> orderIds = orders.stream()
                .map(com.mls.logistics.domain.Order::getId)
                .filter(id -> id != null)
                .toList();

        Sort itemSort = Sort.by(Sort.Direction.ASC, "order.id")
                .and(Sort.by(Sort.Direction.ASC, "resource.name"))
                .and(Sort.by(Sort.Direction.ASC, "id"));

        List<com.mls.logistics.domain.OrderItem> allItems = safeList(() -> orderItemService.getOrderItemsByOrderIds(orderIds, itemSort), model);
        Map<Long, List<com.mls.logistics.domain.OrderItem>> orderItemsByOrderId = new HashMap<>();
        for (var item : allItems) {
            if (item.getOrder() == null || item.getOrder().getId() == null) {
                continue;
            }
            orderItemsByOrderId.computeIfAbsent(item.getOrder().getId(), k -> new ArrayList<>()).add(item);
        }
        model.addAttribute("orderItemsByOrderId", orderItemsByOrderId);

        return "ui/orders";
    }

    @GetMapping("/ui/orders/new")
    public String newOrder(Model model, HttpSession session) {
        if (!model.containsAttribute("orderForm")) {
            CreateOrderRequest header = getOrInitDraftOrderHeader(session);
            model.addAttribute("orderForm", header);
        }

        if (!model.containsAttribute("draftItemForm")) {
            model.addAttribute("draftItemForm", new OrderDraftItemForm());
        }

        populateOrderReferenceData(model);
        addDraftOrderItemsToModel(session, model);

        model.addAttribute("formMode", "create");
        return "ui/order-form";
    }

    @PostMapping("/ui/orders/draft/items")
    public String addDraftOrderItem(
            @ModelAttribute("orderForm") CreateOrderRequest header,
            @Valid @ModelAttribute("draftItemForm") OrderDraftItemForm draftItemForm,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {

        // Persist header values into session so user doesn't lose work when adding/removing items
        session.setAttribute(SESSION_ORDER_DRAFT_HEADER, header);

        if (bindingResult.hasErrors()) {
            populateOrderReferenceData(model);
            addDraftOrderItemsToModel(session, model);
            model.addAttribute("formMode", "create");
            return "ui/order-form";
        }

        try {
            int available = stockService.getTotalAvailableQuantity(draftItemForm.getResourceId());
            if (draftItemForm.getQuantity() > available) {
                populateOrderReferenceData(model);
                addDraftOrderItemsToModel(session, model);
                model.addAttribute("formMode", "create");
                model.addAttribute("errorMessage", "Not enough stock available for the requested quantity.");
                return "ui/order-form";
            }

            List<OrderDraftItem> draftItems = getOrInitDraftOrderItems(session);
            draftItems.add(new OrderDraftItem(draftItemForm.getResourceId(), draftItemForm.getQuantity()));
            session.setAttribute(SESSION_ORDER_DRAFT_ITEMS, draftItems);

            return "redirect:/ui/orders/new";
        } catch (DataAccessException ex) {
            populateOrderReferenceData(model);
            addDraftOrderItemsToModel(session, model);
            model.addAttribute("formMode", "create");
            model.addAttribute("errorMessage", "We couldn't add this item right now. Please try again.");
            return "ui/order-form";
        }
    }

    @PostMapping("/ui/orders/draft/items/{index}/remove")
    public String removeDraftOrderItem(
            @PathVariable int index,
            @ModelAttribute("orderForm") CreateOrderRequest header,
            HttpSession session) {

        session.setAttribute(SESSION_ORDER_DRAFT_HEADER, header);

        List<OrderDraftItem> draftItems = getOrInitDraftOrderItems(session);
        if (index >= 0 && index < draftItems.size()) {
            draftItems.remove(index);
        }
        session.setAttribute(SESSION_ORDER_DRAFT_ITEMS, draftItems);
        return "redirect:/ui/orders/new";
    }

    @PostMapping("/ui/orders")
    public String createOrder(
            @Valid @ModelAttribute("orderForm") CreateOrderRequest form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model,
            HttpSession session) {

        if (bindingResult.hasErrors()) {
            populateOrderReferenceData(model);
            addDraftOrderItemsToModel(session, model);
            if (!model.containsAttribute("draftItemForm")) {
                model.addAttribute("draftItemForm", new OrderDraftItemForm());
            }
            model.addAttribute("formMode", "create");
            return "ui/order-form";
        }

        try {
            List<CreateOrderItemRequest> items = getOrInitDraftOrderItems(session)
                    .stream()
                    .map(d -> new CreateOrderItemRequest(null, d.getResourceId(), d.getQuantity()))
                    .toList();

            orderService.createOrderWithItems(form, items);

            clearDraftOrder(session);
            redirectAttributes.addFlashAttribute("successMessage", "Order created successfully.");
            return "redirect:/ui/orders";
        } catch (InsufficientStockException ex) {
            populateOrderReferenceData(model);
            addDraftOrderItemsToModel(session, model);
            if (!model.containsAttribute("draftItemForm")) {
                model.addAttribute("draftItemForm", new OrderDraftItemForm());
            }
            model.addAttribute("formMode", "create");
            model.addAttribute("errorMessage", "Not enough stock available for one or more items.");
            return "ui/order-form";
        } catch (DataIntegrityViolationException ex) {
            populateOrderReferenceData(model);
            addDraftOrderItemsToModel(session, model);
            if (!model.containsAttribute("draftItemForm")) {
                model.addAttribute("draftItemForm", new OrderDraftItemForm());
            }
            model.addAttribute("formMode", "create");
            model.addAttribute("errorMessage", "Please verify the selected unit.");
            return "ui/order-form";
        } catch (DataAccessException ex) {
            populateOrderReferenceData(model);
            addDraftOrderItemsToModel(session, model);
            if (!model.containsAttribute("draftItemForm")) {
                model.addAttribute("draftItemForm", new OrderDraftItemForm());
            }
            model.addAttribute("formMode", "create");
            model.addAttribute("errorMessage", "We couldn't save your changes right now. Please try again.");
            return "ui/order-form";
        }
    }

    private CreateOrderRequest getOrInitDraftOrderHeader(HttpSession session) {
        Object existing = session.getAttribute(SESSION_ORDER_DRAFT_HEADER);
        if (existing instanceof CreateOrderRequest header) {
            if (header.getDateCreated() == null) {
                header.setDateCreated(LocalDate.now());
            }
            if (header.getStatus() == null || header.getStatus().isBlank()) {
                header.setStatus("CREATED");
            }
            return header;
        }

        CreateOrderRequest header = new CreateOrderRequest();
        header.setDateCreated(LocalDate.now());
        header.setStatus("CREATED");
        session.setAttribute(SESSION_ORDER_DRAFT_HEADER, header);
        return header;
    }

    @SuppressWarnings("unchecked")
    private List<OrderDraftItem> getOrInitDraftOrderItems(HttpSession session) {
        Object existing = session.getAttribute(SESSION_ORDER_DRAFT_ITEMS);
        if (existing instanceof List<?> list) {
            try {
                return (List<OrderDraftItem>) list;
            } catch (ClassCastException ex) {
                // fall through
            }
        }
        List<OrderDraftItem> draft = new ArrayList<>();
        session.setAttribute(SESSION_ORDER_DRAFT_ITEMS, draft);
        return draft;
    }

    private void clearDraftOrder(HttpSession session) {
        session.removeAttribute(SESSION_ORDER_DRAFT_ITEMS);
        session.removeAttribute(SESSION_ORDER_DRAFT_HEADER);
    }

    private void addDraftOrderItemsToModel(HttpSession session, Model model) {
        List<OrderDraftItem> draftItems = getOrInitDraftOrderItems(session);
        Sort resourceSort = Sort.by(Sort.Direction.ASC, "name").and(Sort.by(Sort.Direction.ASC, "id"));
        var resources = safeList(() -> resourceService.getAllResources(resourceSort), model);

        // also used by the draft "Add item" dropdown
        model.addAttribute("resources", resources);

        List<OrderDraftItemView> view = new ArrayList<>();
        for (int i = 0; i < draftItems.size(); i++) {
            OrderDraftItem item = draftItems.get(i);
            var resolved = resources.stream()
                    .filter(r -> r.getId() != null && r.getId().equals(item.getResourceId()))
                    .findFirst()
                    .orElse(null);

            String name = resolved != null ? resolved.getName() : "—";
            String type = resolved != null ? resolved.getType() : null;
            view.add(new OrderDraftItemView(i, item.getResourceId(), name, type, item.getQuantity()));
        }

        model.addAttribute("draftItems", view);
    }

    @GetMapping("/ui/orders/{id}/edit")
    public String editOrder(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            var order = orderService.getOrderById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

            if (!model.containsAttribute("orderForm")) {
                CreateOrderRequest form = new CreateOrderRequest();
                if (order.getUnit() != null) {
                    form.setUnitId(order.getUnit().getId());
                }
                form.setDateCreated(order.getDateCreated());
                form.setStatus(order.getStatus());
                model.addAttribute("orderForm", form);
            }

            populateOrderReferenceData(model);
            populateOrderItemReferenceData(model);
            model.addAttribute("orderItems", safeList(() -> orderItemService.getOrderItemsByOrderId(id, Sort.by(Sort.Direction.ASC, "id")), model));
            if (!model.containsAttribute("inlineItemForm")) {
                model.addAttribute("inlineItemForm", new OrderDraftItemForm());
            }
            model.addAttribute("orderId", id);
            model.addAttribute("formMode", "edit");
            return "ui/order-form";
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Order not found.");
            return "redirect:/ui/orders";
        } catch (DataAccessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "We couldn't load this information right now. Please try again.");
            return "redirect:/ui/orders";
        }
    }

    @PostMapping("/ui/orders/{id}")
    public String updateOrder(
            @PathVariable Long id,
            @Valid @ModelAttribute("orderForm") CreateOrderRequest form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            populateOrderReferenceData(model);
            populateOrderItemReferenceData(model);
            model.addAttribute("orderItems", safeList(() -> orderItemService.getOrderItemsByOrderId(id, Sort.by(Sort.Direction.ASC, "id")), model));
            if (!model.containsAttribute("inlineItemForm")) {
                model.addAttribute("inlineItemForm", new OrderDraftItemForm());
            }
            model.addAttribute("orderId", id);
            model.addAttribute("formMode", "edit");
            return "ui/order-form";
        }

        try {
            UpdateOrderRequest request = new UpdateOrderRequest();
            request.setUnitId(form.getUnitId());
            request.setDateCreated(form.getDateCreated());
            request.setStatus(form.getStatus());

            orderService.updateOrder(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Order updated successfully.");
            return "redirect:/ui/orders";
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Order not found.");
            return "redirect:/ui/orders";
        } catch (DataIntegrityViolationException ex) {
            populateOrderReferenceData(model);
            populateOrderItemReferenceData(model);
            model.addAttribute("orderItems", safeList(() -> orderItemService.getOrderItemsByOrderId(id, Sort.by(Sort.Direction.ASC, "id")), model));
            if (!model.containsAttribute("inlineItemForm")) {
                model.addAttribute("inlineItemForm", new OrderDraftItemForm());
            }
            model.addAttribute("orderId", id);
            model.addAttribute("formMode", "edit");
            model.addAttribute("errorMessage", "Please verify the selected unit.");
            return "ui/order-form";
        } catch (DataAccessException ex) {
            populateOrderReferenceData(model);
            populateOrderItemReferenceData(model);
            model.addAttribute("orderItems", safeList(() -> orderItemService.getOrderItemsByOrderId(id, Sort.by(Sort.Direction.ASC, "id")), model));
            if (!model.containsAttribute("inlineItemForm")) {
                model.addAttribute("inlineItemForm", new OrderDraftItemForm());
            }
            model.addAttribute("orderId", id);
            model.addAttribute("formMode", "edit");
            model.addAttribute("errorMessage", "We couldn't save your changes right now. Please try again.");
            return "ui/order-form";
        }
    }

    @PostMapping("/ui/orders/{orderId}/items/inline-add")
    public String addOrderItemInline(
            @PathVariable Long orderId,
            @Valid @ModelAttribute("inlineItemForm") OrderDraftItemForm inlineItemForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            try {
                var order = orderService.getOrderById(orderId)
                        .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

                CreateOrderRequest orderForm = new CreateOrderRequest();
                if (order.getUnit() != null) {
                    orderForm.setUnitId(order.getUnit().getId());
                }
                orderForm.setDateCreated(order.getDateCreated());
                orderForm.setStatus(order.getStatus());
                model.addAttribute("orderForm", orderForm);

                populateOrderReferenceData(model);
                populateOrderItemReferenceData(model);
                model.addAttribute("orderItems", safeList(() -> orderItemService.getOrderItemsByOrderId(orderId, Sort.by(Sort.Direction.ASC, "id")), model));
                model.addAttribute("orderId", orderId);
                model.addAttribute("formMode", "edit");
                model.addAttribute("errorMessage", "Please correct the item fields.");
                return "ui/order-form";
            } catch (DataAccessException ex) {
                redirectAttributes.addFlashAttribute("errorMessage", "We couldn't complete that action right now. Please try again.");
                return "redirect:/ui/orders/" + orderId + "/edit";
            }
        }

        try {
            orderItemService.createOrderItem(new CreateOrderItemRequest(orderId, inlineItemForm.getResourceId(), inlineItemForm.getQuantity()));
            redirectAttributes.addFlashAttribute("successMessage", "Item added to the order.");
        } catch (InsufficientStockException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Not enough stock available for the requested quantity.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please verify the selected resource.");
        } catch (DataAccessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "We couldn't complete that action right now. Please try again.");
        }

        return "redirect:/ui/orders/" + orderId + "/edit";
    }

    @PostMapping("/ui/orders/{orderId}/items/{itemId}/inline-update")
    public String updateOrderItemInline(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @RequestParam("quantity") int quantity,
            RedirectAttributes redirectAttributes) {

        try {
            var item = orderItemService.getOrderItemById(itemId)
                    .orElseThrow(() -> new ResourceNotFoundException("OrderItem", "id", itemId));

            if (item.getOrder() == null || item.getOrder().getId() == null || !item.getOrder().getId().equals(orderId)) {
                redirectAttributes.addFlashAttribute("errorMessage", "This item does not belong to the selected order.");
                return "redirect:/ui/orders/" + orderId + "/edit";
            }

            UpdateOrderItemRequest request = new UpdateOrderItemRequest();
            request.setOrderId(orderId);
            request.setQuantity(quantity);
            orderItemService.updateOrderItem(itemId, request);

            redirectAttributes.addFlashAttribute("successMessage", "Item updated.");
        } catch (InsufficientStockException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Not enough stock available for the requested quantity.");
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Order item not found.");
        } catch (DataAccessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "We couldn't complete that action right now. Please try again.");
        }

        return "redirect:/ui/orders/" + orderId + "/edit";
    }

    @PostMapping("/ui/orders/{orderId}/items/{itemId}/inline-delete")
    public String deleteOrderItemInline(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            RedirectAttributes redirectAttributes) {

        try {
            orderItemService.deleteOrderItem(itemId);
            redirectAttributes.addFlashAttribute("successMessage", "Item removed from the order.");
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Order item not found.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "This item is in use and cannot be deleted.");
        } catch (DataAccessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "We couldn't complete that action right now. Please try again.");
        }

        return "redirect:/ui/orders/" + orderId + "/edit";
    }

    @PostMapping("/ui/orders/{id}/delete")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.deleteOrder(id);
            redirectAttributes.addFlashAttribute("successMessage", "Order deleted successfully.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete this order because it is referenced by other records.");
        } catch (DataAccessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "We couldn't complete that action right now. Please try again.");
        }
        return "redirect:/ui/orders";
    }

    @GetMapping("/ui/movements")
    public String movements(
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "dir", required = false) String dir,
            Model model) {

        String sortKey = (sort == null || sort.isBlank()) ? "date" : sort.trim().toLowerCase();
        String dirValue = (dir == null || dir.isBlank()) ? "desc" : dir.trim().toLowerCase();

        Sort.Direction direction = "asc".equals(dirValue) ? Sort.Direction.ASC : Sort.Direction.DESC;

        String property = switch (sortKey) {
            case "resource" -> "stock.resource.name";
            case "warehouse" -> "stock.warehouse.name";
            case "action" -> "type";
            case "amount" -> "quantity";
            case "stock" -> "stock.id";
            case "date" -> "dateTime";
            default -> "dateTime";
        };

        Sort sorting = Sort.by(direction, property).and(Sort.by(Sort.Direction.DESC, "id"));

        model.addAttribute("sortKey", sortKey);
        model.addAttribute("sortDir", direction == Sort.Direction.ASC ? "asc" : "desc");

        model.addAttribute("movements", safeList(() -> movementService.getAllMovements(sorting), model));
        return "ui/movements";
    }

    @GetMapping("/ui/stocks")
    public String stocks(
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "dir", required = false) String dir,
            Model model) {

        String sortKey = (sort == null || sort.isBlank()) ? "id" : sort.trim().toLowerCase();
        String dirValue = (dir == null || dir.isBlank()) ? "asc" : dir.trim().toLowerCase();
        Sort.Direction direction = "desc".equals(dirValue) ? Sort.Direction.DESC : Sort.Direction.ASC;

        String property = switch (sortKey) {
            case "id" -> "id";
            case "warehouse" -> "warehouse.name";
            case "quantity" -> "quantity";
            case "resource" -> "resource.name";
            default -> "resource.name";
        };

        Sort sorting = Sort.by(direction, property).and(Sort.by(Sort.Direction.ASC, "id"));
        model.addAttribute("sortKey", sortKey);
        model.addAttribute("sortDir", direction == Sort.Direction.ASC ? "asc" : "desc");

        model.addAttribute("stocks", safeList(() -> stockService.getAllStocks(sorting), model));
        return "ui/stocks";
    }

    @GetMapping("/ui/stocks/new")
    public String newStock(Model model) {
        if (!model.containsAttribute("stockForm")) {
            model.addAttribute("stockForm", new CreateStockRequest());
        }
        model.addAttribute("warehouses", safeList(() -> warehouseService.getAllWarehouses(), model));
        model.addAttribute("resources", safeList(() -> resourceService.getAllResources(), model));
        return "ui/stock-form";
    }

    @PostMapping("/ui/stocks")
    public String createStock(
            @Valid @ModelAttribute("stockForm") CreateStockRequest form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("warehouses", safeList(() -> warehouseService.getAllWarehouses(), model));
            model.addAttribute("resources", safeList(() -> resourceService.getAllResources(), model));
            return "ui/stock-form";
        }

        try {
            var existing = stockService.getStockByResourceAndWarehouse(form.getResourceId(), form.getWarehouseId());
            if (existing.isPresent()) {
                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "This resource already has a stock entry in the selected warehouse. Use Adjust on the existing entry to change quantity."
                );
                return "redirect:/ui/stocks";
            }

            stockService.createStock(form);
            redirectAttributes.addFlashAttribute("successMessage", "Stock record created successfully.");
            return "redirect:/ui/stocks";
        } catch (InvalidRequestException ex) {
            model.addAttribute("warehouses", safeList(() -> warehouseService.getAllWarehouses(), model));
            model.addAttribute("resources", safeList(() -> resourceService.getAllResources(), model));
            model.addAttribute("errorMessage", ex.getMessage());
            return "ui/stock-form";
        } catch (DataAccessException ex) {
            model.addAttribute("warehouses", safeList(() -> warehouseService.getAllWarehouses(), model));
            model.addAttribute("resources", safeList(() -> resourceService.getAllResources(), model));
            model.addAttribute("errorMessage", "We couldn't save your changes right now. Please try again.");
            return "ui/stock-form";
        }
    }

    @GetMapping("/ui/stocks/{id}/adjust")
    public String adjustStockPage(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            var stock = stockService.getStockById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Stock", "id", id));

            if (!model.containsAttribute("adjustForm")) {
                model.addAttribute("adjustForm", new StockAdjustForm(StockAdjustForm.Operation.INCREASE, null));
            }
            model.addAttribute("stock", stock);
            return "ui/stock-adjust";
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Stock record not found.");
            return "redirect:/ui/stocks";
        } catch (DataAccessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "We couldn't load this information right now. Please try again.");
            return "redirect:/ui/stocks";
        }
    }

    @PostMapping("/ui/stocks/{id}/adjust")
    public String adjustStock(
            @PathVariable Long id,
            @Valid @ModelAttribute("adjustForm") StockAdjustForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        var stock = stockService.getStockById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock", "id", id));

        if (bindingResult.hasErrors()) {
            model.addAttribute("stock", stock);
            return "ui/stock-adjust";
        }

        try {
            int amount = form.getAmount();
            int delta = form.getOperation() == StockAdjustForm.Operation.DECREASE ? -amount : amount;

            stockService.adjustStock(id, new AdjustStockRequest(delta));
            redirectAttributes.addFlashAttribute("successMessage", "Stock updated successfully.");
            return "redirect:/ui/stocks";
        } catch (InsufficientStockException | InvalidRequestException ex) {
            model.addAttribute("stock", stock);

            if (ex instanceof InsufficientStockException) {
                model.addAttribute(
                        "errorMessage",
                        "Not enough stock to decrease by " + form.getAmount() + ". Current quantity is " + stock.getQuantity() + "."
                );
            } else {
                model.addAttribute("errorMessage", "Unable to apply this change. Please review the values and try again.");
            }
            return "ui/stock-adjust";
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Stock record not found.");
            return "redirect:/ui/stocks";
        } catch (DataAccessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "We couldn't complete that action right now. Please try again.");
            return "redirect:/ui/stocks";
        }
    }

    @PostMapping("/ui/stocks/{id}/delete")
    public String deleteStock(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            stockService.deleteStock(id);
            redirectAttributes.addFlashAttribute("successMessage", "Stock record deleted successfully.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete stock because it is referenced by other records.");
        } catch (DataAccessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "We couldn't complete that action right now. Please try again.");
        }
        return "redirect:/ui/stocks";
    }

    @GetMapping("/ui/units")
    public String units(
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "dir", required = false) String dir,
            Model model) {

        String sortKey = (sort == null || sort.isBlank()) ? "id" : sort.trim().toLowerCase();
        String dirValue = (dir == null || dir.isBlank()) ? "asc" : dir.trim().toLowerCase();
        Sort.Direction direction = "desc".equals(dirValue) ? Sort.Direction.DESC : Sort.Direction.ASC;

        String property = switch (sortKey) {
            case "id" -> "id";
            case "location" -> "location";
            case "name" -> "name";
            default -> "name";
        };

        Sort sorting = Sort.by(direction, property).and(Sort.by(Sort.Direction.ASC, "id"));
        model.addAttribute("sortKey", sortKey);
        model.addAttribute("sortDir", direction == Sort.Direction.ASC ? "asc" : "desc");

        model.addAttribute("units", safeList(() -> unitService.getAllUnits(sorting), model));
        return "ui/units";
    }

    @GetMapping("/ui/units/new")
    public String newUnit(Model model) {
        if (!model.containsAttribute("unitForm")) {
            model.addAttribute("unitForm", new CreateUnitRequest());
        }
        model.addAttribute("formMode", "create");
        return "ui/unit-form";
    }

    @PostMapping("/ui/units")
    public String createUnit(
            @Valid @ModelAttribute("unitForm") CreateUnitRequest form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("formMode", "create");
            return "ui/unit-form";
        }

        try {
            unitService.createUnit(form);
            redirectAttributes.addFlashAttribute("successMessage", "Unit created successfully.");
            return "redirect:/ui/units";
        } catch (DataAccessException ex) {
            model.addAttribute("formMode", "create");
            model.addAttribute("errorMessage", "We couldn't save your changes right now. Please try again.");
            return "ui/unit-form";
        }
    }

    @GetMapping("/ui/units/{id}/edit")
    public String editUnit(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            var unit = unitService.getUnitById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Unit", "id", id));

            if (!model.containsAttribute("unitForm")) {
                CreateUnitRequest form = new CreateUnitRequest();
                form.setName(unit.getName());
                form.setLocation(unit.getLocation());
                model.addAttribute("unitForm", form);
            }
            model.addAttribute("unitId", id);
            model.addAttribute("formMode", "edit");
            return "ui/unit-form";
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unit not found.");
            return "redirect:/ui/units";
        } catch (DataAccessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "We couldn't load this information right now. Please try again.");
            return "redirect:/ui/units";
        }
    }

    @PostMapping("/ui/units/{id}")
    public String updateUnit(
            @PathVariable Long id,
            @Valid @ModelAttribute("unitForm") CreateUnitRequest form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("unitId", id);
            model.addAttribute("formMode", "edit");
            return "ui/unit-form";
        }

        try {
            UpdateUnitRequest request = new UpdateUnitRequest();
            request.setName(form.getName());
            request.setLocation(form.getLocation());

            unitService.updateUnit(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Unit updated successfully.");
            return "redirect:/ui/units";
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unit not found.");
            return "redirect:/ui/units";
        } catch (DataAccessException ex) {
            model.addAttribute("unitId", id);
            model.addAttribute("formMode", "edit");
            model.addAttribute("errorMessage", "We couldn't save your changes right now. Please try again.");
            return "ui/unit-form";
        }
    }

    @PostMapping("/ui/units/{id}/delete")
    public String deleteUnit(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            unitService.deleteUnit(id);
            redirectAttributes.addFlashAttribute("successMessage", "Unit deleted successfully.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete unit because it is referenced by other records.");
        } catch (DataAccessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "We couldn't complete that action right now. Please try again.");
        }
        return "redirect:/ui/units";
    }

    private void populateShipmentReferenceData(Model model) {
        Sort orderSort = Sort.by(Sort.Direction.DESC, "dateCreated").and(Sort.by(Sort.Direction.DESC, "id"));
        Sort vehicleSort = Sort.by(Sort.Direction.ASC, "id");
        Sort warehouseSort = Sort.by(Sort.Direction.ASC, "name").and(Sort.by(Sort.Direction.ASC, "id"));

        model.addAttribute("orders", safeList(() -> orderService.getAllOrders(orderSort), model));
        model.addAttribute("vehicles", safeList(() -> vehicleService.getAllVehicles(vehicleSort), model));
        model.addAttribute("warehouses", safeList(() -> warehouseService.getAllWarehouses(warehouseSort), model));
        model.addAttribute("shipmentStatuses", List.of("PLANNED", "IN_TRANSIT", "DELIVERED"));
    }

    private void populateOrderReferenceData(Model model) {
        Sort unitSort = Sort.by(Sort.Direction.ASC, "name").and(Sort.by(Sort.Direction.ASC, "id"));
        model.addAttribute("units", safeList(() -> unitService.getAllUnits(unitSort), model));
        model.addAttribute("orderStatuses", List.of("CREATED", "VALIDATED", "COMPLETED"));
    }

    private void populateOrderItemReferenceData(Model model) {
        Sort resourceSort = Sort.by(Sort.Direction.ASC, "name").and(Sort.by(Sort.Direction.ASC, "id"));
        model.addAttribute("resources", safeList(() -> resourceService.getAllResources(resourceSort), model));
    }

    private int safeCount(CountSupplier supplier, Model model) {
        try {
            return supplier.get();
        } catch (DataAccessException ex) {
            model.addAttribute("dataError", "Data is temporarily unavailable. Please try again in a moment.");
            return 0;
        }
    }

    private String normalizeVehicleType(String rawType) {
        if (rawType == null) {
            return null;
        }
        String value = rawType.trim().toUpperCase();
        return switch (value) {
            // canonical
            case "LAND", "SEA", "AIR" -> value;

            // legacy Spanish
            case "TERRESTRE" -> "LAND";
            case "MARITIMO" -> "SEA";
            case "AEREO" -> "AIR";

            // other common English variants
            case "TERRESTRIAL", "GROUND" -> "LAND";
            case "MARITIME" -> "SEA";
            case "AERIAL" -> "AIR";

            default -> rawType;
        };
    }

    private String normalizeVehicleStatus(String rawStatus) {
        if (rawStatus == null) {
            return null;
        }
        String value = rawStatus.trim().toUpperCase();
        return switch (value) {
            // canonical
            case "AVAILABLE", "IN_USE", "IN_REPAIR" -> value;

            // legacy variants
            case "INREPAIR", "REPAIR", "REPAIRS", "MAINTENANCE", "IN_MAINTENANCE" -> "IN_REPAIR";

            default -> rawStatus;
        };
    }

    private <T> List<T> safeList(ListSupplier<T> supplier, Model model) {
        try {
            return supplier.get();
        } catch (DataAccessException ex) {
            model.addAttribute("dataError", "Data is temporarily unavailable. Please try again in a moment.");
            return List.of();
        }
    }

    @FunctionalInterface
    private interface ListSupplier<T> {
        List<T> get();
    }

    @FunctionalInterface
    private interface CountSupplier {
        int get();
    }
}
