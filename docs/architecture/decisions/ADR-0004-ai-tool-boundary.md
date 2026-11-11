# ADR-0004: AI uses backend-controlled tools

- Status: Accepted
- Context: The shopping assistant must answer from live catalogue facts without exposing database internals.
- Decision: The model may request allow-listed tools. Backend services validate inputs, permissions and limits, then query through repositories. No arbitrary SQL or database credentials are exposed to the model.
- Consequences: AI can be grounded in catalogue data; checkout and account changes always use normal backend authorization and explicit user confirmation.
