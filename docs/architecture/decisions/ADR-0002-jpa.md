# ADR-0002: Use Spring Data JPA and Hibernate

- Status: Accepted
- Context: Java 21 / Spring Boot backend with DAO-oriented layering.
- Decision: Use Jakarta Persistence, Hibernate and Spring Data repository interfaces. No hand-written JDBC. Use Flyway for versioned schema changes.
- Consequences: Persistence is expressed through entity mappings and repositories; custom repository implementations are reserved for complex queries.
