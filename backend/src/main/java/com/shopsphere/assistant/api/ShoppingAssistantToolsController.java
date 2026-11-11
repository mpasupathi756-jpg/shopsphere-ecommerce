package com.shopsphere.assistant.api;
import com.shopsphere.assistant.service.ShoppingAssistantToolService;
import com.shopsphere.commerce.api.OrderDetailResponse;
import com.shopsphere.commerce.api.OrderSummaryResponse;
import com.shopsphere.marketplace.api.ProductCardResponse;
import com.shopsphere.marketplace.api.ProductDetailResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authenticated read-only endpoints that form the safe tool boundary for the
 * future model adapter. No model-supplied user ID is accepted by these routes.
 */
@RestController
@Validated
@RequestMapping("/api/v1/assistant/tools")
public class ShoppingAssistantToolsController {
    private final ShoppingAssistantToolService tools;

    public ShoppingAssistantToolsController(ShoppingAssistantToolService tools) {
        this.tools = tools;
    }

    @GetMapping("/products")
    public Page<ProductCardResponse> searchProducts(
            @RequestParam String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(defaultValue = "0") @Min(0) @Max(100) int page,
            @RequestParam(defaultValue = "8") @Min(1) @Max(8) int size) {
        return tools.searchProducts(q, category, brand, page, size);
    }

    @GetMapping("/products/{slug}")
    public ProductDetailResponse productDetails(@PathVariable String slug) {
        return tools.getProductDetails(slug);
    }

    @PostMapping("/products/compare")
    public List<ProductDetailResponse> compareProducts(@Valid @RequestBody ProductComparisonRequest request) {
        return tools.compareProducts(request.slugs());
    }

    @GetMapping("/orders")
    public List<OrderSummaryResponse> myOrders(@AuthenticationPrincipal Jwt jwt) {
        return tools.getMyOrders(UUID.fromString(jwt.getSubject()));
    }

    @GetMapping("/orders/{orderNumber}")
    public OrderDetailResponse myOrder(@AuthenticationPrincipal Jwt jwt, @PathVariable String orderNumber) {
        return tools.getMyOrder(UUID.fromString(jwt.getSubject()), orderNumber);
    }
}
