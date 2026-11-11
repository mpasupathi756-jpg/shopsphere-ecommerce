# AI shopping assistant

## Current implementation

Phase 5 now has a local Ollama-backed, one-turn conversational endpoint built with Spring AI 1.1.8. Spring AI 1.1.x supports this repository's Spring Boot 3.5.x baseline.

- `POST /api/v1/assistant/chat` — authenticated one-turn chat; request body contains a message (1–1000 characters).
- Default local model: `qwen2.5:3b`.
- Ollama endpoint: `http://localhost:11434` by default.
- Override with `OLLAMA_BASE_URL` and `OLLAMA_MODEL`.
- Ollama does not automatically download the model at application startup. Install Ollama and pull the configured model separately.

## Model tools

The model receives only these read-only tools for each request:

- `searchProducts` — active catalogue search, maximum 5 products.
- `getProductDetails` — product details and active seller offers.
- `compareProducts` — compare 2–4 active products.
- `getMyOrders` — the signed-in customer's order list.
- `getMyOrder` — details of one order belonging to the signed-in customer.

The HTTP tool endpoints remain available under `/api/v1/assistant/tools` for direct frontend use and testing.

## Security boundaries

- Both chat and tool endpoints require authentication.
- Customer identity is derived from the verified JWT subject, not from model arguments or request body fields.
- Customer-order tools are instantiated per chat request with the authenticated customer's ID. The ID is not part of the model-visible tool schema.
- Order lookup delegates to the existing customer-scoped order query service.
- The tool layer delegates to catalogue and order services; it does not expose repositories, persistence entities, SQL or arbitrary database access.
- All model tools are read-only. No cart writes, checkout, payments, cancellations, seller/admin actions or arbitrary URL fetches are exposed.
- Search is bounded; comparison accepts only 2–4 distinct slugs.
- Model output is never authorization. Any future write operation requires a separate explicit user confirmation and backend validation.
- Prompts are limited to 1000 characters. Rate limiting and persistent conversation history are not implemented yet.

## Privacy and deployment

The initial development provider is Ollama running locally. With a local Ollama instance, prompts and retrieved catalogue/order tool results stay on the local machine rather than being sent to a hosted model API. This is a development default, not a production deployment configuration.

Do not expose a developer's localhost Ollama service to the public internet. Production model hosting/provider, access controls, network path, privacy terms, rate limits, retention and cost must be reviewed before deployment. No model API key or production AI service has been provisioned.

## Next steps

1. Run the assistant locally with Ollama and verify model tool calling end to end.
2. Add endpoint tests for unauthenticated access and cross-customer order isolation.
3. Add rate limiting, conversation persistence and safe observability that excludes sensitive prompt/order content.
4. Build the React assistant chat interface and loading/error states.
5. Evaluate production model hosting/provider and deployment security before enabling the assistant in production.
