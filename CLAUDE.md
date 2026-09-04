# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this project is

A Spring Boot cashback-rewards API, used as the worked example for the Udemy course *Spec-Driven Development and TDD with AI*. Each course section has `section-N/start` and `section-N/solution` branches (see the Branch Map in `README.md`), so **the code on the current branch is deliberately incomplete** — it is the starting point for a section, not a finished application. Check the branch name before assuming a class, package, or `.claude/` config file should already exist.

Java 25, Maven wrapper, Spring Boot 3.5.x.

## Commands

```bash
./mvnw test                                  # unit tests (*Test)
./mvnw verify                                # unit + acceptance tests (*IT)
./mvnw -Dtest=CashbackCalculatorTest test    # single test class
./mvnw -Dtest=CashbackCalculatorTest#roundsFractionsOfACentDown test   # single test method
./mvnw spring-boot:run                       # run the app
./mvnw clean package                         # build the JAR
```

Note: the failsafe plugin is not yet in `pom.xml` on early branches, so `./mvnw verify` will not actually execute `*IT` classes until it is added.

## Architecture

Hexagonal (ports and adapters). Dependencies point inward:

```
adapter/in/web  →  application  →  domain  ←  application  ←  adapter/out/persistence
```

- `domain/` — pure business logic. **No Spring, no JPA, no framework imports of any kind.** Entities and value objects as Java records; domain services; business-rule exceptions. Testable with plain JUnit, no Spring context.
- `application/` — use-case orchestration. `port/in/` (interfaces the web adapter calls), `port/out/` (interfaces persistence implements), `service/` (`@Service` beans that wire the two).
- `adapter/in/web/` — thin `@RestController`s. DTOs only at the boundary, `@Valid` on request bodies, no business logic.
- `adapter/out/persistence/` — JPA entities and repositories, implementing the outbound ports. JPA annotations live **only** here.

The domain never depends on the adapters. If domain code needs a framework type, the design is wrong — introduce a port instead.

## Conventions

- **Money is always `BigDecimal`** with an explicit scale of 2 and an explicit `RoundingMode`. Never `double`, `float`, or `int` for monetary amounts, and never compare with `equals()` — use AssertJ's `isEqualByComparingTo`.
  Rounding mode is currently inconsistent: `CashbackCalculator` uses `RoundingMode.DOWN`, while `doc/specs/basic-cashback-calculation.md` specifies banker's rounding (`HALF_EVEN`). The spec is the source of truth — confirm with the user before changing either.
- Prefer modern Java (records, sealed types, pattern matching, `var` where it aids readability).
- Constructor injection for Spring beans; no field `@Autowired`.
- Tests: JUnit 5 + AssertJ. `@DisplayName` on the class states the business rule; method names describe the behaviour (`calculatesTwoPercentOfThePurchaseAmount`).

## Test layout

Test packages mirror production packages. Maven naming decides which phase runs them:

- `*Test.java` — unit tests, run by `./mvnw test`. Domain tests use no Spring; controller tests use `@WebMvcTest`; repository tests use `@DataJpaTest` (H2 in PostgreSQL compatibility mode).
- `*IT.java` — acceptance tests in `src/test/java/**/acceptance/`, run by `./mvnw verify`. These drive the feature end to end against in-memory implementations of the outbound ports, so no database is needed.

Never recompute an expected value in a test using the same logic as the production code — expected values come from the spec and are written as literals.

## Spec-driven workflow

Feature specs live in `doc/specs/` in **Example Mapping** format: a user story, then `### Rule:` sections, each with examples (`The one where...` bullets, or a markdown table when inputs vary independently) and at least one counter-example. Rules start with "Should" or "Must". Specs are the contract — they drive acceptance tests, which drive the implementation. When a spec changes, tests change first.

The intended cycle is **Discover → Accept → TDD → Review**:

1. `/discover "<user story>"` — Example Mapping to produce a spec in `doc/specs/`. Present open questions to the user one at a time and fold the answers in; never save a spec with unresolved questions, and never save without approval.
2. Write a failing acceptance test (`*IT`) for the next unimplemented rule in the spec.
3. Drive the implementation with red → green → refactor cycles against unit tests.
4. Review the uncommitted diff for architecture and quality.

Only `/discover` exists as a command on early branches (`.claude/commands/discover.md`); `/accept`, `/tdd`, `/review`, the path-scoped rules in `.claude/rules/`, and the hooks in `.claude/settings.json` are added in later course sections.
