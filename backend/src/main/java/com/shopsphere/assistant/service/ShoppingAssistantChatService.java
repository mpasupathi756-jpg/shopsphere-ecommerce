package com.shopsphere.assistant.service;
import com.shopsphere.assistant.api.AssistantChatResponse;
import java.util.UUID;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** One-turn model orchestration; only read-only tools are available to the model. */
@Service
public class ShoppingAssistantChatService {
    private static final String SYSTEM_PROMPT = """
        You are the ShopSphere shopping assistant.
        Help customers discover products, understand seller offers, compare products,
        and check their own order information using the tools provided.
        Use live catalogue or order tools whenever a factual answer depends on ShopSphere data.
        Never invent product names, prices, stock, delivery estimates, seller details, or order status.
        If a tool cannot find the information, say so clearly.
        Do not reveal another customer's data. The order tools are scoped to the signed-in customer.
        You may only read information. You cannot add to cart, place orders, cancel orders,
        make payments, change account details, or perform seller/admin operations.
        Never ask for passwords, one-time codes, card numbers, or payment credentials.
        Be concise, factual, and transparent when you are uncertain.
        """;

    private final ChatClient chatClient;
    private final ShoppingAssistantModelTools catalogueTools;
    private final ShoppingAssistantToolService tools;
    private final String model;

    public ShoppingAssistantChatService(
            ChatClient.Builder builder,
            ShoppingAssistantModelTools catalogueTools,
            ShoppingAssistantToolService tools,
            @Value("${spring.ai.ollama.chat.options.model:qwen2.5:3b}") String model) {
        this.chatClient = builder.build();
        this.catalogueTools = catalogueTools;
        this.tools = tools;
        this.model = model;
    }

    public AssistantChatResponse chat(String message, UUID authenticatedCustomerId) {
        try {
            String answer = chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(message.trim())
                    .tools(catalogueTools, new CustomerOrderModelTools(tools, authenticatedCustomerId))
                    .call()
                    .content();
            if (answer == null || answer.isBlank()) {
                throw new IllegalStateException("The model returned an empty response");
            }
            return new AssistantChatResponse(answer, model);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "The local AI model is unavailable. Start Ollama and ensure the configured model is installed.");
        }
    }
}
