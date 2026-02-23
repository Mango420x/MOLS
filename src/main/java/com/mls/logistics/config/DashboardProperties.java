package com.mls.logistics.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Dashboard configuration values.
 *
 * Exposed as configuration properties to avoid hardcoded thresholds in the UI.
 */
@ConfigurationProperties(prefix = "mols.dashboard")
public class DashboardProperties {

    private int lowStockThreshold;
    private int criticalStockThreshold;
    private int lowStockListLimit;

    private int staleOrderDays;
    private int staleOrdersListLimit;

    private int recentActivityHours;
    private int movementChartDays;

    private double fulfillmentTargetPercent;

    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    public int getCriticalStockThreshold() {
        return criticalStockThreshold;
    }

    public void setCriticalStockThreshold(int criticalStockThreshold) {
        this.criticalStockThreshold = criticalStockThreshold;
    }

    public int getLowStockListLimit() {
        return lowStockListLimit;
    }

    public void setLowStockListLimit(int lowStockListLimit) {
        this.lowStockListLimit = lowStockListLimit;
    }

    public int getStaleOrderDays() {
        return staleOrderDays;
    }

    public void setStaleOrderDays(int staleOrderDays) {
        this.staleOrderDays = staleOrderDays;
    }

    public int getStaleOrdersListLimit() {
        return staleOrdersListLimit;
    }

    public void setStaleOrdersListLimit(int staleOrdersListLimit) {
        this.staleOrdersListLimit = staleOrdersListLimit;
    }

    public int getRecentActivityHours() {
        return recentActivityHours;
    }

    public void setRecentActivityHours(int recentActivityHours) {
        this.recentActivityHours = recentActivityHours;
    }

    public int getMovementChartDays() {
        return movementChartDays;
    }

    public void setMovementChartDays(int movementChartDays) {
        this.movementChartDays = movementChartDays;
    }

    public double getFulfillmentTargetPercent() {
        return fulfillmentTargetPercent;
    }

    public void setFulfillmentTargetPercent(double fulfillmentTargetPercent) {
        this.fulfillmentTargetPercent = fulfillmentTargetPercent;
    }
}
