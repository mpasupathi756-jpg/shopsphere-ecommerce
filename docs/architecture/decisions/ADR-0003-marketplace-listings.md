# ADR-0003: Separate canonical products from seller listings

- Status: Accepted
- Context: Multiple sellers may offer the same product at different prices, stock levels and fulfilment terms.
- Decision: Product is shared catalogue identity; Listing is one seller's offer; Inventory is listing-scoped.
- Consequences: Cart and order lines reference listings; order snapshots preserve the seller and price at purchase time.
