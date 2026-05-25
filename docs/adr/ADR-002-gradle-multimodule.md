# ADR-002: Gradle Multi-Module Layout

**Date:** 2026-05-24  
**Status:** Accepted  
**Applies to:** Project structure

---

## Context

Omnia is intended to be a multi-domain ERP system. We need a way to:
- Keep each business domain (products, inventory, sales, etc.) physically separated
- Share common primitives without duplicating them
- Allow each module to have its own dependencies and build lifecycle
- Avoid the monolith anti-pattern where all code is mixed in one package

## Decision

Use a **Gradle multi-project build** where each domain is a subproject under `modules/`.

```
settings.gradle:
  include 'modules:shared'
  include 'modules:products'
  include 'modules:inventory'   ← added when the module is built
  ...
```

The root project owns the Spring Boot plugin, main class, and shared dependency management (BOM).
Each submodule is a `java-library` that the root project depends on.

## Consequences

**Good:**
- Hard boundary between modules — cross-module imports are explicit and visible in `build.gradle`
- Each module can be developed and understood independently
- Shared kernel (`modules:shared`) is a single explicit dependency
- New modules are added by creating a folder and one line in `settings.gradle`

**Trade-off:**
- Slightly more build configuration overhead
- Cross-module calls must go through defined interfaces, not direct class references
  (which is actually a good constraint, not a real downside)
