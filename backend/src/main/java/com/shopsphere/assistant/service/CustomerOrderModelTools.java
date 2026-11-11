package com.shopsphere.assistant.service;
import com.shopsphere.commerce.api.OrderDetailResponse;
import com.shopsphere.commerce.api.OrderSummaryResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * Per-request order tools. The customer ID is injected from the verified JWT
 * by the controller and is never included in the model-visible tool schema.
 */
public class CustomerOrderModelTools {
    private final ShoppingAssistantToolService tools;
    private final UUID authenticatedCustomerId;

    public CustomerOrderModelTools(ShoppingAssistantToolService tools, UUID authenticatedCustomerId) {
        this.tools = tools;
        this.authenticatedCustomerId = authenticatedCustomerId;
    }

    @Tool(description = "List the signed-in customer's own recent ShopSphere orders. Use only when the customer asks about their orders.")
    public List<OrderSummaryResponse> getMyOrders() {
        return tools.getMyOrders(authenticatedCustomerId);
    }

    @Tool(description = "Get details and fulfilment status for one of the signed-in customer's own orders. The order number must be supplied by the customer.")
    public OrderDetailResponse getMyOrder(
            @ToolParam(description = "The order number provided by the signed-in customer") String orderNumber) {
        return tools.getMyOrder(authenticatedCustomerId, orderNumber);
    }
}
