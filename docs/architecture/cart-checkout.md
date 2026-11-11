# Cart, wishlist and checkout preview — Phase 3

## Cart rules
- A cart is unique per customer account.
- Cart lines reference seller Listings, not canonical Products, so seller/price/stock facts remain specific.
- Quantity is limited to 1–20 per line.
- Add/update checks that the listing, product and seller remain active/approved and that current available stock is sufficient.
- Cart operations do not reserve stock. Checkout preview revalidates all offers and quantities.

## Addresses
- Customer addresses are private to their owner.
- A customer may mark one address as default; creating the first address makes it default.
- Checkout preview accepts only an address owned by the authenticated customer.
- The selected delivery address is returned as the current preview. Orders will persist a separate immutable address snapshot in Phase 4.

## Wishlist and comparison
- Wishlist is unique per customer and stores canonical product IDs, not seller offers.
- Wishlist entries can show the current lowest active offer where one exists.
- Product comparison is currently a client-side selection over canonical product details; persistence and richer compare API can be added later.

## Checkout preview
- POST /api/v1/checkout/preview validates cart lines, active seller offers, current stock and customer-owned shipping address.
- It groups line items by seller to show the parent checkout breakdown.
- The preview is read-only: it creates no order, does not reserve inventory and does not collect money.
- Shipping charges are provisional zero until shipping rules/carriers are selected.
- Payment is intentionally disabled until a marketplace-capable payment provider is selected and configured. The response explicitly warns that no order or charge has been made.

## APIs
- GET/POST /api/v1/addresses; PATCH /api/v1/addresses/{id}/default
- GET /api/v1/cart; POST /api/v1/cart/items; PATCH/DELETE /api/v1/cart/items/{id}
- GET /api/v1/wishlist; POST/DELETE /api/v1/wishlist/products/{productId}
- POST /api/v1/checkout/preview

## Phase 4 requirements
- Select a provider with Indian marketplace/seller settlement support.
- Implement provider adapter, idempotent payment creation, signed webhook verification, payment state transitions, order creation and seller sub-orders.
- Reserve inventory with row-level locking and a bounded expiry; release reservations on payment failure/expiry/cancellation.
- Add seller fulfilment, shipment tracking, refunds, commission ledger and payout reconciliation.
