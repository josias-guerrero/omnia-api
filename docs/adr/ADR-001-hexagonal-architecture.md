# ADR-001: Hexagonal Architecture (Ports & Adapters)

**Date:** 2026-05-24  
**Status:** Accepted  
**Applies to:** All modules

---

## Context

We need an architecture that:
- Keeps business logic testable without starting a database or web server
- Allows swapping infrastructure (e.g., different DB, different framework version) without touching domain code
- Makes it clear where to put new code when extending the system

## Decision

Each module uses **Hexagonal Architecture**, also called Ports & Adapters.

The domain is the center. It defines **ports** (interfaces) for what it needs from the outside world.
The infrastructure provides **adapters** that implement those ports.

```
[ REST Controller ]  →  [ Use Case ]  →  [ Domain Entity ]
                              ↓
                       [ Repository Port (interface) ]
                              ↓
                    [ JPA Repository Impl (adapter) ]  →  [ PostgreSQL ]
```

**Concrete rule:** The `domain` package has zero imports from Spring, JPA, or any framework.

## Consequences

**Good:**
- Domain logic can be unit tested with plain Java (no Spring context needed)
- Infrastructure can change without touching business rules
- Forces explicit boundaries — you always know which layer you're in

**Trade-off:**
- More files per feature (entity + JPA entity + mapper + port + impl)
- Takes discipline to not leak infrastructure concerns into the domain
