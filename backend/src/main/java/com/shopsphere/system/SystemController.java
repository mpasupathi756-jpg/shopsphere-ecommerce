package com.shopsphere.system;
import java.time.Instant;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/v1/system")
public class SystemController {
 @GetMapping("/info")
 public Map<String,Object> info() { return Map.of("application","ShopSphere","status","RUNNING","milestone","PHASE_5_ASSISTANT_TOOL_BOUNDARY","timestamp",Instant.now().toString()); }
}
