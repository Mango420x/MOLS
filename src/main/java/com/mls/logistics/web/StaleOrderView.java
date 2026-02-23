package com.mls.logistics.web;

/**
 * Dashboard-only projection for pending orders past the configured threshold.
 */
public record StaleOrderView(Long orderId, String unitName, long daysPending) {
}
