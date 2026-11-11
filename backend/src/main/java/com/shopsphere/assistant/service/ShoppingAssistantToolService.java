package com.shopsphere.assistant.service;
import com.shopsphere.commerce.api.OrderDetailResponse;
import com.shopsphere.commerce.api.OrderSummaryResponse;
import com.shopsphere.commerce.service.OrderQueryService;
import com.shopsphere.marketplace.api.ProductCardResponse;
import com.shopsphere.marketplace.api.ProductDetailResponse;
import com.shopsphere.marketplace.service.CatalogueService;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Read-only, authorization-aware application tools for the shopping assistant.
 * These methods deliberately delegate to existing catalogue and order services;
 * they do not expose repositories, persistence entities, or arbitrary database access.
 */
@Service
public class ShoppingAssistantToolService {
    private final CatalogueService catalogue;
    private final OrderQueryService orders;

    public ShoppingAssistantToolService(CatalogueService catalogue, OrderQueryService orders) {
        this.catalogue = catalogue;
        this.orders = orders;
    }

    @Transactional(readOnly = true)
    public Page<ProductCardResponse> searchProducts(String query, String category, String brand, int page, int size) {
        if (query == null || query.isBlank() || query.trim().length() < 2 || query.trim().length() > 120) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search text must contain 2 to 120 characters");
        }
        if (page < 0 || page > 100 || size < 1 || size > 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assistant search supports pages 0–100 and page sizes 1–8");
        }
        return catalogue.search(query.trim(), category, brand, page, size);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetails(String slug) {
        return catalogue.detail(slug);
    }

    @Transactional(readOnly = true)
    public List<ProductDetailResponse> compareProducts(List<String> slugs) {
        if (slugs == null || slugs.size() < 2 || slugs.size() > 4
                || slugs.stream().anyMatch(s -> s == null || s.isBlank() || s.length() > 160)
                || new HashSet<>(slugs).size() != slugs.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide 2–4 distinct product slugs");
        }
        return slugs.stream().map(catalogue::detail).toList();
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> getMyOrders(UUID authenticatedUserId) {
        return orders.customerOrders(authenticatedUserId);
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getMyOrder(UUID authenticatedUserId, String orderNumber) {
        return orders.customerOrder(authenticatedUserId, orderNumber);
    }
}
