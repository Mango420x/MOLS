package com.mls.logistics.service;

import com.mls.logistics.domain.Resource;
import com.mls.logistics.domain.Stock;
import com.mls.logistics.domain.Warehouse;
import com.mls.logistics.dto.request.CreateStockRequest;
import com.mls.logistics.dto.request.UpdateStockRequest;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Stock-related business operations.
 * 
 * This class acts as an intermediary between controllers and repositories,
 * enforcing business rules and application logic.
 */
@Service
@Transactional(readOnly = true)
public class StockService {

    private final StockRepository stockRepository;

    /**
     * Constructor-based dependency injection.
     * This is the recommended approach in Spring.
     */
    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * Retrieves all registered stocks.
     */
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    /**
     * Retrieves a stock by its identifier.
     */
    public Optional<Stock> getStockById(Long id) {
        return stockRepository.findById(id);
    }

    /**
     * Creates a new stock.
     * 
     * Business rules can be added here in the future
     * (e.g. inventory management and thresholds).
     */
    @Transactional
    public Stock createStock(Stock stock) {
        return stockRepository.save(stock);
    }

    /**
     * Creates a new stock from a DTO request.
     * 
     * This method separates API contracts from domain logic.
     *
     * @param request DTO containing stock data
     * @return created stock entity
     */
    @Transactional
    public Stock createStock(CreateStockRequest request) {
        Stock stock = new Stock();

        Resource resource = new Resource();
        resource.setId(request.getResourceId());

        Warehouse warehouse = new Warehouse();
        warehouse.setId(request.getWarehouseId());

        stock.setResource(resource);
        stock.setWarehouse(warehouse);
        stock.setQuantity(request.getQuantity());

        return stockRepository.save(stock);
    }

    /**
     * Updates an existing stock.
     * 
     * Only non-null fields from the request are updated.
     *
     * @param id stock identifier
     * @param request update data
     * @return updated stock
     * @throws ResourceNotFoundException if stock doesn't exist
     */
    @Transactional
    public Stock updateStock(Long id, UpdateStockRequest request) {
        Stock stock = stockRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock", "id", id));

        if (request.getResourceId() != null) {
            Resource resource = new Resource();
            resource.setId(request.getResourceId());
            stock.setResource(resource);
        }
        if (request.getWarehouseId() != null) {
            Warehouse warehouse = new Warehouse();
            warehouse.setId(request.getWarehouseId());
            stock.setWarehouse(warehouse);
        }
        if (request.getQuantity() != null) {
            stock.setQuantity(request.getQuantity());
        }

        return stockRepository.save(stock);
    }

    /**
     * Deletes a stock by ID.
     *
     * @param id stock identifier
     * @throws ResourceNotFoundException if stock doesn't exist
     */
    @Transactional
    public void deleteStock(Long id) {
        if (!stockRepository.existsById(id)) {
            throw new ResourceNotFoundException("Stock", "id", id);
        }
        stockRepository.deleteById(id);
    }
}
