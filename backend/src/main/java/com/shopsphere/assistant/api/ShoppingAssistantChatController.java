package com.shopsphere.assistant.api;
import com.shopsphere.assistant.service.ShoppingAssistantChatService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@Validated
@RequestMapping("/api/v1/assistant")
public class ShoppingAssistantChatController {
    private final ShoppingAssistantChatService assistant;
    public ShoppingAssistantChatController(ShoppingAssistantChatService assistant) { this.assistant = assistant; }

    @PostMapping("/chat")
    public AssistantChatResponse chat(@Valid @RequestBody AssistantChatRequest request,
                                       @AuthenticationPrincipal Jwt jwt) {
        return assistant.chat(request.message(), UUID.fromString(jwt.getSubject()));
    }
}
