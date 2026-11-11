package com.shopsphere.identity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import jakarta.servlet.http.Cookie;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class IdentityIntegrationTest {
    @Container static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");
    @DynamicPropertySource static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test void registersCustomerRotatesRefreshTokenAndAuthenticatesProfile() throws Exception {
        MvcResult csrfResult = mvc.perform(get("/api/v1/auth/csrf")).andExpect(status().isOk()).andReturn();
        Cookie csrfCookie = csrfResult.getResponse().getCookie("shopsphere_csrf");
        assertNotNull(csrfCookie);
        String csrf = mapper.readTree(csrfResult.getResponse().getContentAsString()).get("csrfToken").asText();
        assertEquals(csrfCookie.getValue(), csrf);

        String registration = "{\"displayName\":\"Test Customer\",\"email\":\"customer@example.com\",\"password\":\"A-Strong-Test-Passphrase-2026\"}";
        MvcResult registered = mvc.perform(post("/api/v1/auth/register").cookie(csrfCookie).header("X-CSRF-TOKEN", csrf).contentType(MediaType.APPLICATION_JSON).content(registration))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.user.roles[0]").value("CUSTOMER")).andReturn();
        JsonNode registrationBody = mapper.readTree(registered.getResponse().getContentAsString());
        String firstAccess = registrationBody.get("accessToken").asText();
        Cookie firstRefresh = registered.getResponse().getCookie("shopsphere_refresh");
        assertNotNull(firstRefresh);
        assertTrue(firstRefresh.isHttpOnly());

        MvcResult refreshed = mvc.perform(post("/api/v1/auth/refresh").cookie(csrfCookie, firstRefresh).header("X-CSRF-TOKEN", csrf).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk()).andReturn();
        JsonNode refreshedBody = mapper.readTree(refreshed.getResponse().getContentAsString());
        String secondAccess = refreshedBody.get("accessToken").asText();
        Cookie secondRefresh = cookie(refreshed, "shopsphere_refresh");
        assertNotEquals(firstAccess, secondAccess);
        assertNotNull(secondRefresh);
        assertNotEquals(firstRefresh.getValue(), secondRefresh.getValue());

        mvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer " + secondAccess))
                .andExpect(status().isOk()).andExpect(jsonPath("$.email").value("customer@example.com"));
        mvc.perform(get("/api/v1/admin/overview").header("Authorization", "Bearer " + secondAccess))
                .andExpect(status().isForbidden());

        mvc.perform(post("/api/v1/auth/refresh").cookie(csrfCookie, firstRefresh).header("X-CSRF-TOKEN", csrf).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
    }
}
