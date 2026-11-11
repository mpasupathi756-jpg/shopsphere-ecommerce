# ADR-0001: Start with a modular monolith

- Status: Accepted
- Context: Full marketplace scope, initially delivered by a small team.
- Decision: Deploy one Spring Boot application and enforce business module boundaries in code.
- Consequences: Simpler local development, transactions and operations. Extract modules only when measured scale, team ownership or independent release requirements justify the cost.
