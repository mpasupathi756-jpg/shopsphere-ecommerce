package com.shopsphere.assistant.service;
import com.shopsphere.marketplace.api.ProductCardResponse;
import com.shopsphere.marketplace.api.ProductDetailResponse;
import java.util.List;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/** Public catalogue-only tools exposed to the model for one chat turn. */
@Component
public class ShoppingAssistantModelTools {
    private final ShoppingAssistantToolService tools;
    public ShoppingAssistantModelTools(ShoppingAssistantToolService tools) { this.tools = tools; }

    @Tool(description = "Search the live ShopSphere product catalogue by product name or keyword. Returns up to five active product matches with starting price and seller count.")
    public List<ProductCardResponse> searchProducts(
            @ToolParam(description = "A product name or search phrase, at least two characters") String query) {
        return tools.searchProducts(query, null, null, 0, 5).getContent();
    }

    @Tool(description = "Get full information for one active ShopSphere product, including specifications, images and active seller offers. Use the exact product slug from search results.")
    public ProductDetailResponse getProductDetails(
            @ToolParam(description = "Exact product slug returned by catalogue search") String slug) {
        return tools.getProductDetails(slug);
    }

    @Tool(description = "Compare two to four active ShopSphere products side by side. Use exact product slugs returned by catalogue search.")
    public List<ProductDetailResponse> compareProducts(
            @ToolParam(description = "Two to four distinct product slugs from catalogue search") List<String> slugs) {
        return tools.compareProducts(slugs);
    }
}
