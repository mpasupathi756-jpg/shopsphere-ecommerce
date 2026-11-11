package com.shopsphere.marketplace;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test","local"})
class MarketplaceIntegrationTest {
    private static final String CATEGORY_ID = "00000000-0000-0000-0000-000000000001";
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test void customerCanApplySellerListProductAndPublishOfferAfterModeration() throws Exception {
        MvcResult csrfResult = mvc.perform(get("/api/v1/auth/csrf")).andExpect(status().isOk()).andReturn();
        Cookie csrfCookie = cookie(csrfResult, "shopsphere_csrf");
        String csrf = mapper.readTree(csrfResult.getResponse().getContentAsString()).get("csrfToken").asText();

        String register = "{\"displayName\":\"Marketplace Seller\",\"email\":\"seller@example.com\",\"password\":\"A-Strong-Seller-Passphrase-2026\"}";
        MvcResult registered = mvc.perform(post("/api/v1/auth/register").cookie(csrfCookie).header("X-CSRF-TOKEN", csrf).contentType(MediaType.APPLICATION_JSON).content(register))
                .andExpect(status().isCreated()).andReturn();
        JsonNode registeredBody = mapper.readTree(registered.getResponse().getContentAsString());
        String customerToken = registeredBody.get("accessToken").asText();
        Cookie refreshCookie = cookie(registered, "shopsphere_refresh");

        String application = "{\"legalName\":\"Seller Private Limited\",\"storeName\":\"Clear Choice Store\",\"businessEmail\":\"business@example.com\",\"phone\":\"+919876543210\",\"businessAddress\":\"Bengaluru, Karnataka, India\",\"gstin\":null}";
        MvcResult submitted = mvc.perform(post("/api/v1/seller-applications").header("Authorization", "Bearer " + customerToken).contentType(MediaType.APPLICATION_JSON).content(application))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("PENDING_REVIEW")).andReturn();
        String sellerId = mapper.readTree(submitted.getResponse().getContentAsString()).get("id").asText();
        mvc.perform(get("/api/v1/seller-applications/me").header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(sellerId));

        String adminLogin = "{\"email\":\"admin@example.com\",\"password\":\"A-Strong-Admin-Passphrase-2026\"}";
        MvcResult adminResult = mvc.perform(post("/api/v1/auth/login").cookie(csrfCookie).header("X-CSRF-TOKEN", csrf).contentType(MediaType.APPLICATION_JSON).content(adminLogin))
                .andExpect(status().isOk()).andReturn();
        String adminToken = mapper.readTree(adminResult.getResponse().getContentAsString()).get("accessToken").asText();
        mvc.perform(patch("/api/v1/admin/sellers/" + sellerId + "/approve").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("APPROVED"));

        MvcResult refreshed = mvc.perform(post("/api/v1/auth/refresh").cookie(csrfCookie, refreshCookie).header("X-CSRF-TOKEN", csrf).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk()).andReturn();
        String sellerToken = mapper.readTree(refreshed.getResponse().getContentAsString()).get("accessToken").asText();

        String product = "{\"categoryId\":\"" + CATEGORY_ID + "\",\"brandId\":null,\"title\":\"ThinkPad Coding Laptop\",\"description\":\"A reliable laptop for software development and everyday work.\"}";
        MvcResult productResult = mvc.perform(post("/api/v1/seller/products").header("Authorization", "Bearer " + sellerToken).contentType(MediaType.APPLICATION_JSON).content(product))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("PENDING_REVIEW")).andReturn();
        JsonNode productBody = mapper.readTree(productResult.getResponse().getContentAsString());
        String productId = productBody.get("id").asText();
        String slug = productBody.get("slug").asText();
        mvc.perform(patch("/api/v1/admin/catalog/products/" + productId + "/approve").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("ACTIVE"));

        String listing = "{\"productId\":\"" + productId + "\",\"variantId\":null,\"sellerSku\":\"CLEAR-THINKPAD-001\",\"priceMinor\":5999900,\"currency\":\"INR\",\"condition\":\"NEW\",\"shippingDaysMin\":2,\"shippingDaysMax\":5,\"returnWindowDays\":7,\"warrantyText\":\"1 year manufacturer warranty\",\"availableQuantity\":5}";
        MvcResult listingResult = mvc.perform(post("/api/v1/seller/listings").header("Authorization", "Bearer " + sellerToken).contentType(MediaType.APPLICATION_JSON).content(listing))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("PENDING_REVIEW")).andReturn();
        String listingId = mapper.readTree(listingResult.getResponse().getContentAsString()).get("id").asText();
        mvc.perform(patch("/api/v1/admin/catalog/listings/" + listingId + "/approve").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("ACTIVE"));

        mvc.perform(get("/api/v1/catalog/products").param("q", "ThinkPad"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].slug").value(slug))
                .andExpect(jsonPath("$.content[0].startingPriceMinor").value(5999900))
                .andExpect(jsonPath("$.content[0].sellerCount").value(1));
        mvc.perform(get("/api/v1/catalog/products/" + slug)).andExpect(status().isOk())
                .andExpect(jsonPath("$.offers[0].availableQuantity").value(5));

        String buyerRegister = "{\"displayName\":\"Test Buyer\",\"email\":\"buyer@example.com\",\"password\":\"A-Strong-Buyer-Passphrase-2026\"}";
        MvcResult buyerResult = mvc.perform(post("/api/v1/auth/register").cookie(csrfCookie).header("X-CSRF-TOKEN", csrf).contentType(MediaType.APPLICATION_JSON).content(buyerRegister))
                .andExpect(status().isCreated()).andReturn();
        String buyerToken = mapper.readTree(buyerResult.getResponse().getContentAsString()).get("accessToken").asText();

        String address = "{\"recipientName\":\"Test Buyer\",\"phone\":\"+919876543210\",\"addressLine1\":\"12 Sample Road\",\"addressLine2\":null,\"city\":\"Bengaluru\",\"state\":\"Karnataka\",\"postalCode\":\"560001\",\"country\":\"India\",\"defaultAddress\":true}";
        MvcResult addressResult = mvc.perform(post("/api/v1/addresses").header("Authorization", "Bearer " + buyerToken).contentType(MediaType.APPLICATION_JSON).content(address))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.defaultAddress").value(true)).andReturn();
        String addressId = mapper.readTree(addressResult.getResponse().getContentAsString()).get("id").asText();

        String cartItem = "{\"listingId\":\"" + listingId + "\",\"quantity\":2}";
        mvc.perform(post("/api/v1/cart/items").header("Authorization", "Bearer " + buyerToken).contentType(MediaType.APPLICATION_JSON).content(cartItem))
                .andExpect(status().isOk()).andExpect(jsonPath("$.itemCount").value(2)).andExpect(jsonPath("$.subtotalMinor").value(11999800));
        mvc.perform(post("/api/v1/wishlist/products/" + productId).header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].productId").value(productId));
        String preview = "{\"addressId\":\"" + addressId + "\"}";
        mvc.perform(post("/api/v1/checkout/preview").header("Authorization", "Bearer " + buyerToken).contentType(MediaType.APPLICATION_JSON).content(preview))
                .andExpect(status().isOk()).andExpect(jsonPath("$.paymentEnabled").value(true))
                .andExpect(jsonPath("$.sellerOrders[0].items[0].quantity").value(2))
                .andExpect(jsonPath("$.totalMinor").value(11999800));

        MvcResult orderResult = mvc.perform(post("/api/v1/checkout/orders").header("Authorization", "Bearer " + buyerToken).header("Idempotency-Key", "buyer-checkout-001").contentType(MediaType.APPLICATION_JSON).content(preview))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("PAYMENT_PENDING"))
                .andExpect(jsonPath("$.paymentProvider").value("MOCK")).andExpect(jsonPath("$.paymentStatus").value("PENDING")).andReturn();
        String orderNumber = mapper.readTree(orderResult.getResponse().getContentAsString()).get("orderNumber").asText();
        mvc.perform(get("/api/v1/catalog/products/" + slug)).andExpect(status().isOk())
                .andExpect(jsonPath("$.offers[0].availableQuantity").value(3));
        mvc.perform(get("/api/v1/orders").header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(post("/api/v1/checkout/orders").header("Authorization", "Bearer " + buyerToken).header("Idempotency-Key", "buyer-checkout-001").contentType(MediaType.APPLICATION_JSON).content(preview))
                .andExpect(status().isOk()).andExpect(jsonPath("$.orderNumber").value(orderNumber));
        mvc.perform(post("/api/v1/payments/mock/" + orderNumber + "/success").header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("PAID"));
        mvc.perform(get("/api/v1/products/" + slug + "/reviews/eligibility").header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.eligible").value(false));
        mvc.perform(post("/api/v1/products/" + slug + "/reviews").header("Authorization", "Bearer " + buyerToken).contentType(MediaType.APPLICATION_JSON)
                .content("{\"rating\":5,\"title\":\"Great laptop\",\"body\":\"Fast and reliable for development.\"}"))
                .andExpect(status().isForbidden());
        MvcResult sellerOrdersResult = mvc.perform(get("/api/v1/seller/orders").header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().isOk()).andReturn();
        String sellerOrderId = mapper.readTree(sellerOrdersResult.getResponse().getContentAsString()).get(0).get("sellerOrderId").asText();
        mvc.perform(patch("/api/v1/seller/orders/" + sellerOrderId + "/processing").header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("PROCESSING"));
        String shipment = "{\"carrier\":\"Test Express\",\"trackingNumber\":\"TRACK-0001\"}";
        mvc.perform(patch("/api/v1/seller/orders/" + sellerOrderId + "/ship").header("Authorization", "Bearer " + sellerToken).contentType(MediaType.APPLICATION_JSON).content(shipment))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("SHIPPED"));
        mvc.perform(patch("/api/v1/seller/orders/" + sellerOrderId + "/delivered").header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("DELIVERED"));
        mvc.perform(get("/api/v1/orders/" + orderNumber).header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("DELIVERED"));
        mvc.perform(get("/api/v1/catalog/products/" + slug)).andExpect(status().isOk())
                .andExpect(jsonPath("$.offers[0].availableQuantity").value(3));

        mvc.perform(get("/api/v1/products/" + slug + "/reviews/eligibility").header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.eligible").value(true));
        MvcResult reviewResult = mvc.perform(post("/api/v1/products/" + slug + "/reviews").header("Authorization", "Bearer " + buyerToken).contentType(MediaType.APPLICATION_JSON)
                .content("{\"rating\":5,\"title\":\"Great laptop\",\"body\":\"Fast and reliable for development.\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.verifiedPurchase").value(true)).andExpect(jsonPath("$.rating").value(5)).andReturn();
        String reviewId = mapper.readTree(reviewResult.getResponse().getContentAsString()).get("id").asText();
        mvc.perform(post("/api/v1/products/" + slug + "/reviews").header("Authorization", "Bearer " + buyerToken).contentType(MediaType.APPLICATION_JSON)
                .content("{\"rating\":4,\"title\":\"Duplicate\",\"body\":\"Duplicate review.\"}"))
                .andExpect(status().isConflict());
        mvc.perform(get("/api/v1/products/" + slug + "/reviews")).andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1)).andExpect(jsonPath("$.content[0].authorName").value("Test Buyer"));
        mvc.perform(get("/api/v1/catalog/products/" + slug)).andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(5.0)).andExpect(jsonPath("$.reviewCount").value(1));
        mvc.perform(patch("/api/v1/me/reviews/" + reviewId).header("Authorization", "Bearer " + sellerToken).contentType(MediaType.APPLICATION_JSON)
                .content("{\"rating\":1,\"title\":\"Hijack\",\"body\":\"Not my review.\"}"))
                .andExpect(status().isNotFound());
        mvc.perform(post("/api/v1/reviews/" + reviewId + "/reports").header("Authorization", "Bearer " + sellerToken).contentType(MediaType.APPLICATION_JSON)
                .content("{\"reasonCode\":\"OTHER\",\"details\":\"Test report\"}"))
                .andExpect(status().isOk());
        mvc.perform(post("/api/v1/reviews/" + reviewId + "/reports").header("Authorization", "Bearer " + sellerToken).contentType(MediaType.APPLICATION_JSON)
                .content("{\"reasonCode\":\"OTHER\",\"details\":\"Duplicate report\"}"))
                .andExpect(status().isConflict());
        mvc.perform(patch("/api/v1/admin/reviews/" + reviewId + "/moderation").header("Authorization", "Bearer " + buyerToken).contentType(MediaType.APPLICATION_JSON)
                .content("{\"action\":\"HIDDEN\",\"reason\":\"Customer cannot moderate\"}"))
                .andExpect(status().isForbidden());
        MvcResult reportQueue = mvc.perform(get("/api/v1/admin/reviews/reports").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1)).andReturn();
        String reportId = mapper.readTree(reportQueue.getResponse().getContentAsString()).get("content").get(0).get("id").asText();
        mvc.perform(patch("/api/v1/admin/reviews/" + reviewId + "/moderation").header("Authorization", "Bearer " + adminToken).contentType(MediaType.APPLICATION_JSON)
                .content("{\"action\":\"HIDDEN\",\"reason\":\"Test moderation\"}"))
                .andExpect(status().isOk());
        mvc.perform(get("/api/v1/products/" + slug + "/reviews")).andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(0));
        mvc.perform(get("/api/v1/catalog/products/" + slug)).andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(org.hamcrest.Matchers.nullValue())).andExpect(jsonPath("$.reviewCount").value(0));
        mvc.perform(patch("/api/v1/admin/reviews/reports/" + reportId).header("Authorization", "Bearer " + adminToken).contentType(MediaType.APPLICATION_JSON)
                .content("{\"action\":\"ACTIONED\",\"note\":\"Review hidden\"}"))
                .andExpect(status().isOk());
    }

    private Cookie cookie(MvcResult result, String name) {
        String header = result.getResponse().getHeaders("Set-Cookie").stream().filter(h -> h.startsWith(name + "=")).findFirst().orElseThrow();
        return new Cookie(name, header.substring(name.length() + 1).split(";", 2)[0]);
    }
}
