package com.mls.logistics.web;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * UI form for adjusting stock for non-technical users.
 */
public class StockAdjustForm {

    public enum Operation {
        INCREASE,
        DECREASE
    }

    @NotNull(message = "Operation is required")
    private Operation operation;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private Integer amount;

    public StockAdjustForm() {
    }

    public StockAdjustForm(Operation operation, Integer amount) {
        this.operation = operation;
        this.amount = amount;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
