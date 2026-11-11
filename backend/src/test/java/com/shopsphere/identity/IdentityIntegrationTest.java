package com.shopsphere.identity;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IdentityIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test void registersLogsInRotatesRefreshTokenAndEnforcesRoles() throws Exception {
        MvcResult csrfResult = mvc.perform(get("/api/v1/auth/csrf")).andExpect(status().isOk()).andReturn();
        Cookie csrfCookie = cookie(csrfResult, "shopsphere_csrf");
        assertNotNull(csrfCookie);
        String csrf = mapper.readTree(csrfResult.getResponse().getContentAsString()).get("csrfToken").asText();
        assertEquals(csrfCookie.getValue(), csrf);

        String registration = "{\"displayName\":\"Test Customer\",\"email\":\"customer@example.com\",\"password\":\"A-Strong-Test-Passphrase-2026\"}";
        MvcResult registered = mvc.perform(post("/api/v1/auth/register").cookie(csrfCookie).header("X-CSRF-TOKEN", csrf).contentType(MediaType.APPLICATION_JSON).content(registration))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.user.roles[0]").value("CUSTOMER")).andReturn();
        JsonNode registrationBody = mapper.readTree(registered.getResponse().getContentAsString());
        String firstAccess = registrationBody.get("accessToken").asText();
        Cookie firstRefresh = cookie(registered, "shopsphere_refresh");
        assertNotNull(firstRefresh);
        assertTrue(setCookieHeader(registered, "shopsphere_refresh").contains("HttpOnly"));

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

        String login = "{\"email\":\"customer@example.com\",\"password\":\"A-Strong-Test-Passphrase-2026\"}";
        mvc.perform(post("/api/v1/auth/login").cookie(csrfCookie).header("X-CSRF-TOKEN", csrf).contentType(MediaType.APPLICATION_JSON).content(login))
                .andExpect(status().isOk()).andExpect(jsonPath("$.user.email").value("customer@example.com"));
        String invalidLogin = "{\"email\":\"customer@example.com\",\"password\":\"incorrect-password\"}";
        mvc.perform(post("/api/v1/auth/login").cookie(csrfCookie).header("X-CSRF-TOKEN", csrf).contentType(MediaType.APPLICATION_JSON).content(invalidLogin))
                .andExpect(status().isUnauthorized());
    }

    private Cookie cookie(MvcResult result, String name) {
        String header = setCookieHeader(result, name);
        if (header == null) return null;
        String value = header.substring(name.length() + 1).split(";", 2)[0];
        return new Cookie(name, value);
    }
    private String setCookieHeader(MvcResult result, String name) {
        return result.getResponse().getHeaders("Set-Cookie").stream()
                .filter(header -> header.startsWith(name + "=")).findFirst().orElse(null);
    }
}
