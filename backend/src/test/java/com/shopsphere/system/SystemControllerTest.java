package com.shopsphere.system;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;
import com.shopsphere.config.SecurityConfig;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(SystemController.class)
@TestPropertySource(properties = "app.security.jwt.secret-base64=VGVzdFNob3BTcGhlcmVTZWNyZXRLZXlGb3JUZXN0aW5nMDAx")
@Import(SecurityConfig.class)
class SystemControllerTest {
 @Autowired MockMvc mockMvc;
 @Test void exposesFoundationInfo() throws Exception {
  mockMvc.perform(get("/api/v1/system/info")).andExpect(status().isOk())
   .andExpect(jsonPath("$.application").value("ShopSphere"))
   .andExpect(jsonPath("$.status").value("RUNNING"))
   .andExpect(jsonPath("$.milestone").value("PHASE_0_FOUNDATION"));
 }
}
