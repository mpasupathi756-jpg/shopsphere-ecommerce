# AI shopping assistant — safe tool boundary

## Phase 5 implementation status

This phase starts with a provider-independent application tool layer. The HTTP endpoints are useful for frontend integration and establish the exact backend operations that a future Spring AI model adapter may call. This is not yet a conversational model integration.

## Available read-only tools

All endpoints are under `/api/v1/assistant/tools` and require an authenticated session, except catalogue data is also available through the existing public catalogue API.

- `GET /products?q=...&category=...&brand=...&page=0&size=8` — bounded catalogue search.
- `GET /products/{slug}` — active product details and seller offers.
- `POST /products/compare` — compare 2–4 distinct product slugs.
- `GET /orders` — order history for the authenticated customer.
- `GET /orders/{orderNumber}` — one order belonging to the authenticated customer.

## Security boundaries

- Customer identity is derived from the verified JWT subject; clients cannot supply another user's ID.
- Order lookup delegates to the existing customer-scoped order query service.
- The tool layer delegates to catalogue and order services; it does not expose repositories or persistence entities.
- Search results are bounded to at most 8 products per page and page numbers 0–100.
- Comparison accepts only 2–4 distinct product slugs.
- All operations are read-only. There are no cart writes, checkout, payment, seller/admin actions, SQL tools or arbitrary URL fetch tools.
- Model output must never be treated as authorization. Future write operations require a separate explicit user confirmation and backend validation.

## Model provider decision

No external provider has been selected or configured. Keep the orchestration/provider boundary independent until privacy, cost, deployment and data-retention requirements are reviewed. No API key is committed and no customer prompt or order data is sent to a model provider by this implementation.

## Next implementation steps

1. Add Spring AI behind an explicit provider adapter and configuration flag.
2. Expose only these read-only operations as model tools, with strict argument validation.
3. Add conversation endpoint, rate limits, request-size limits and safe error handling.
4. Add tests for cross-customer order access, invalid comparison inputs, and bounded search.
5. Build the React assistant UI with clear disclosure that answers are based on current catalogue/order data.
6. Only then evaluate a model provider and deployment configuration.
