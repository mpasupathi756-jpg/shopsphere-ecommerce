package com.shopsphere.commerce.api;
import com.shopsphere.commerce.service.AddressService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController @RequestMapping("/api/v1/addresses")
public class AddressController {
 private final AddressService service;
 public AddressController(AddressService service){this.service=service;}
 @GetMapping public List<AddressResponse> list(@AuthenticationPrincipal Jwt jwt){return service.list(UUID.fromString(jwt.getSubject()));}
 @PostMapping public org.springframework.http.ResponseEntity<AddressResponse> create(@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody AddressRequest request){return org.springframework.http.ResponseEntity.status(201).body(service.create(UUID.fromString(jwt.getSubject()),request));}
 @PatchMapping("/{id}/default") public AddressResponse setDefault(@AuthenticationPrincipal Jwt jwt,@PathVariable UUID id){return service.setDefault(UUID.fromString(jwt.getSubject()),id);}
}
