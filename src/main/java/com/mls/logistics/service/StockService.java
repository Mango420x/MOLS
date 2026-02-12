package com.mls.logistics.service;

import com.mls.logistics.domain.Stock;
import com.mls.logistics.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Stock-related business operations.
 * 
 * This class acts as an intermediary between controllers and repositories,
 * enforcing business rules and application logic.
 */
@Service
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
    public Stock createStock(Stock stock) {
        return stockRepository.save(stock);
    }
}
