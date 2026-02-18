package com.mls.logistics.web;

import com.mls.logistics.service.OrderService;
import com.mls.logistics.service.VehicleService;
import com.mls.logistics.service.WarehouseService;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping
public class UiController {

    private final WarehouseService warehouseService;
    private final VehicleService vehicleService;
    private final OrderService orderService;

    public UiController(WarehouseService warehouseService,
                        VehicleService vehicleService,
                        OrderService orderService) {
        this.warehouseService = warehouseService;
        this.vehicleService = vehicleService;
        this.orderService = orderService;
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
        return "ui/dashboard";
    }

    @GetMapping("/ui/warehouses")
    public String warehouses(Model model) {
        model.addAttribute("warehouses", safeList(() -> warehouseService.getAllWarehouses(), model));
        return "ui/warehouses";
    }

    @GetMapping("/ui/vehicles")
    public String vehicles(Model model) {
        model.addAttribute("vehicles", safeList(() -> vehicleService.getAllVehicles(), model));
        return "ui/vehicles";
    }

    @GetMapping("/ui/orders")
    public String orders(Model model) {
        model.addAttribute("orders", safeList(() -> orderService.getAllOrders(), model));
        return "ui/orders";
    }

    private int safeCount(CountSupplier supplier, Model model) {
        try {
            return supplier.get();
        } catch (DataAccessException ex) {
            model.addAttribute("dataError", "Unable to access the database. Start PostgreSQL (docker-compose) to see real data.");
            return 0;
        }
    }

    private <T> List<T> safeList(ListSupplier<T> supplier, Model model) {
        try {
            return supplier.get();
        } catch (DataAccessException ex) {
            model.addAttribute("dataError", "Unable to access the database. Start PostgreSQL (docker-compose) to see real data.");
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
