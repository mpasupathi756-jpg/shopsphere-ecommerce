package com.shopsphere.assistant.service;
import com.shopsphere.commerce.service.OrderQueryService;
import com.shopsphere.marketplace.service.CatalogueService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
class ShoppingAssistantToolServiceTest {
    private CatalogueService catalogue;
    private OrderQueryService orders;
    private ShoppingAssistantToolService tools;
    @BeforeEach void setUp() {
        catalogue = mock(CatalogueService.class);
        orders = mock(OrderQueryService.class);
        tools = new ShoppingAssistantToolService(catalogue, orders);
    }
    @Test void rejectsBlankAndTooShortSearches() {
        assertThrows(ResponseStatusException.class, () -> tools.searchProducts(" ", null, null, 0, 8));
        assertThrows(ResponseStatusException.class, () -> tools.searchProducts("a", null, null, 0, 8));
        verifyNoInteractions(catalogue, orders);
    }
    @Test void rejectsUnboundedSearchRequests() {
        assertThrows(ResponseStatusException.class, () -> tools.searchProducts("phone", null, null, 0, 9));
        assertThrows(ResponseStatusException.class, () -> tools.searchProducts("phone", null, null, 101, 8));
        verifyNoInteractions(catalogue, orders);
    }
    @Test void rejectsInvalidOrDuplicateComparisonSlugs() {
        assertThrows(ResponseStatusException.class, () -> tools.compareProducts(List.of("one")));
        assertThrows(ResponseStatusException.class, () -> tools.compareProducts(List.of("same", "same")));
        assertThrows(ResponseStatusException.class, () -> tools.compareProducts(List.of("one", "two", "three", "four", "five")));
        verifyNoInteractions(catalogue, orders);
    }
}
