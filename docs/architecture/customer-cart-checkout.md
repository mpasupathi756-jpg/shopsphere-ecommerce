# Phase 3 — Customer shopping and checkout preview

## Implemented customer workflows

- A signed-in customer can add an active seller listing to their cart.
- Cart lines are unique per listing; adding the same offer increments the quantity.
- Quantity is limited to 1–20 per line and cannot exceed currently available stock.
- Customers can update and remove only their own cart lines.
- Customer addresses are private to the owning account; the first saved address becomes default.
- A customer can change the default address without changing another customer's addresses.
- Wishlist entries are unique per customer and canonical product.
- Checkout preview requires an address owned by the customer and a non-empty cart.
- Preview revalidates seller approval, product/listing activation and available stock.
- Preview groups lines by seller and calculates integer-paise subtotals.
- Preview is read-only: it does not create an order, reserve inventory, or initiate a payment.

## Data model

- carts and cart_items represent one active cart per user.
- customer_addresses stores the delivery details needed for a future order snapshot.
- wishlists and wishlist_items store canonical product saves.
- orders is the future customer-level parent order.
- seller_orders is the seller-specific fulfilment split beneath the customer order.
- order_items stores listing, product, seller, SKU, title, unit-price and quantity snapshots.
- payments, payment_webhook_events, and order_events establish persistence boundaries for later payment/order lifecycle work.

Money is represented as integer paise. Product listing price and inventory are revalidated on every cart mutation and checkout preview.

## API endpoints

### Cart
- GET /api/v1/cart
- POST /api/v1/cart/items
- PATCH /api/v1/cart/items/{itemId}
- DELETE /api/v1/cart/items/{itemId}

### Addresses
- GET /api/v1/addresses
- POST /api/v1/addresses
- PATCH /api/v1/addresses/{id}/default

### Wishlist
- GET /api/v1/wishlist
- POST /api/v1/wishlist/products/{productId}
- DELETE /api/v1/wishlist/products/{productId}

### Checkout preview
- POST /api/v1/checkout/preview

All customer endpoints derive the user ID from the verified JWT subject. The API does not accept a customer ID supplied by the browser.

## Payment and order-creation gate

Order placement is deliberately not enabled in this phase. No payment provider has been selected or configured, and there is no live payment intent, webhook verification, seller settlement, cancellation/refund or reservation-expiry workflow. Creating a payable order before those controls exist could strand customer funds or reserve stock indefinitely.

The UI therefore labels checkout as a preview and explicitly states that no order is created, no stock is reserved and no payment is collected. The next phase must select and implement a payment provider that supports an Indian multi-seller marketplace and seller settlement model before enabling order placement.

## Verification

The integration test covers the customer-to-seller catalogue flow followed by customer address creation, cart add, quantity/stock limits, cart retrieval and update, wishlist idempotency, address ownership isolation, checkout grouping and totals, no-stock-reservation behavior, empty-cart rejection and wishlist/cart removal.

CI runs frontend typecheck/build and backend Maven tests. A PostgreSQL 16 service is used for a production-profile smoke startup so Flyway migrations and Hibernate schema validation are checked against PostgreSQL, not only H2.
