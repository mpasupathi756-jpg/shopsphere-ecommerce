# Phase 4 — Orders, payment boundary and seller fulfilment

## Customer order placement

- POST /api/v1/checkout/orders requires an authenticated customer, an address owned by that customer and an Idempotency-Key header.
- Checkout locks the customer row before checking the idempotency key, serializing duplicate submissions from one account.
- Cart lines and listing inventory are locked during checkout.
- The backend revalidates listing, product, seller approval, currency and stock at the point of order creation.
- One customer order is created with one seller order per seller.
- Order items retain immutable title, seller, SKU, unit-price and quantity snapshots.
- Inventory is moved from available to reserved before payment confirmation.
- The cart is cleared only after the order and payment intent have been created successfully in the same transaction.
- A 15-minute payment expiry is assigned to pending orders by default.

## Payment provider boundary

- PaymentGateway is the provider abstraction.
- The local profile uses MockPaymentGateway. It returns a local-only checkout route and cannot charge real money.
- The local-only mock success endpoint is POST /api/v1/payments/mock/{orderNumber}/success.
- Non-local profiles use UnconfiguredPaymentGateway. It returns HTTP 503 and throws before the checkout transaction commits; no order or stock reservation is persisted.
- No production payment provider credentials or project have been configured.

A real provider must be selected before production order placement is enabled. It must support an Indian marketplace model with seller-level settlement/splits, refunds, webhook signature verification, idempotency and reconciliation.

## Order state transitions

Customer order:
- PAYMENT_PENDING → PAID → PROCESSING → SHIPPED → DELIVERED
- PAYMENT_PENDING → CANCELLED when the payment window expires

Seller order:
- PAYMENT_PENDING → PAID → PROCESSING → SHIPPED → DELIVERED
- PAYMENT_PENDING → CANCELLED when the parent payment expires

Seller-specific orders are isolated by seller ID. Sellers can only view and fulfil their own seller-order records. Customer order details are scoped to the authenticated customer.

## Inventory safety

- Checkout locks listing inventory and reserves stock.
- Successful mock payment converts reserved stock to sold stock without restoring available quantity.
- Expired pending orders release reserved stock.
- Due-order expiry and payment confirmation use pessimistic order locks to avoid duplicate state transitions.
- Inventory movements are recorded for reservation, sale and release.

## API endpoints

Customer:
- POST /api/v1/checkout/orders
- GET /api/v1/orders
- GET /api/v1/orders/{orderNumber}

Seller:
- GET /api/v1/seller/orders
- PATCH /api/v1/seller/orders/{sellerOrderId}/processing
- PATCH /api/v1/seller/orders/{sellerOrderId}/ship
- PATCH /api/v1/seller/orders/{sellerOrderId}/delivered

Local-only payment simulation:
- POST /api/v1/payments/mock/{orderNumber}/success

## Production blockers

- Select and integrate a marketplace-capable payment provider.
- Implement signed payment webhooks and replay protection.
- Implement provider reconciliation and seller payout/commission ledger.
- Implement refund and cancellation workflows.
- Confirm shipping rates, tax calculation, seller settlement and operational policies.
- Configure production secrets and environment, run security review and test against the real provider sandbox.

The mock provider is for local development and automated tests only. Never deploy the local profile to production.
