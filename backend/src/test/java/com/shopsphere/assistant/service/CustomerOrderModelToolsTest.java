package com.shopsphere.assistant.service;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
class CustomerOrderModelToolsTest {
    @Test void bindsOrderLookupToAuthenticatedCustomerRatherThanModelSuppliedIdentity() {
        ShoppingAssistantToolService service = mock(ShoppingAssistantToolService.class);
        UUID authenticatedCustomer = UUID.randomUUID();
        CustomerOrderModelTools tools = new CustomerOrderModelTools(service, authenticatedCustomer);
        tools.getMyOrders();
        tools.getMyOrder("SS-ORDER-1001");
        verify(service).getMyOrders(authenticatedCustomer);
        verify(service).getMyOrder(authenticatedCustomer, "SS-ORDER-1001");
    }
}
