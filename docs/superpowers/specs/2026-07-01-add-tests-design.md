# Add tests to hql-query-builder — design

Date: 2026-07-01

## Goal

This repository currently has no automated tests (per CLAUDE.md). Add tests across both
modules (`hql-query-builder`, `hql-query-builder-generator`) to get the highest practical
coverage, wire up JaCoCo so coverage is measurable, and open a PR.

## Build wiring

- Parent `pom.xml`:
  - `<dependencyManagement>`: `org.junit:junit-bom` (import scope), `org.mockito:mockito-core`,
    `org.mockito:mockito-junit-jupiter`, `org.assertj:assertj-core` — all `test` scope.
  - `<pluginManagement>`: pin `maven-surefire-plugin` to a current 3.x version (the inherited
    Maven default predates the JUnit Platform launcher and won't discover JUnit 5 tests),
    and add `jacoco-maven-plugin` bound to `prepare-agent` (test phase) and `report` (verify
    phase).
- Both submodule `pom.xml` files: add `junit-jupiter`, `mockito-core`,
  `mockito-junit-jupiter`, `assertj-core` as `test`-scope dependencies (versions inherited).

## `hql-query-builder` runtime module

- `restrictions` package: one test class per restriction type, calling `toHQL(...)` against
  a mocked `HQLQueryBuilder` (stubbing `getShortProperty`) and asserting the HQL fragment +
  bound parameter values in `HQLRestrictionValues`.
- `beans` package: construction/getter tests for `HQLAlias`, `HQLOrder`,
  `HQLRestrictionValues`, `HQLStatisticItem`, `HQLStatistic`, `GetIdException`.
- `paths` package: `HQLSafePath` (equals/hashCode/compareTo/toString),
  `HQLSafePathEntity`, `HQLSafePathBasic`/`BasicString`/`BasicNumber`/`BasicDate` — verify
  delegation to a mocked `HQLQueryBuilder`.
- `HQLQueryBuilder` + `HQLQueryBuilderCache`: tested with Mockito mocks of `EntityManager` /
  `Query` / `EntityType` / `Attribute` / `PluralAttribute` (no real Hibernate). Covers
  `getList`, `getCount`, `getMultiSelect`, `getStatistics`, `getListWithStatistics`,
  `getShortProperty` path-aliasing, `addFetch`, `patchPropertyName`, pagination, the
  `org.hibernate.cacheable` query hint, the `computeListUsingIdSubQuery` branch, and `getId`
  reflection success/failure.
  - **Static-state isolation**: `HQLQueryBuilder.cache` and `HQLQueryBuilderCache.metamodel`
    are private `static` fields populated lazily once per JVM. A test-only reflection helper
    resets both fields in `@BeforeEach` so tests are isolated and order-independent. No
    production code changes.

## `hql-query-builder-generator` module

No new test-framework dependency for compile-testing; drive the JDK's in-process
`javax.tools.JavaCompiler` directly:

- Sample `@Entity` / `@MappedSuperclass` / `@Embeddable` source classes (basic attributes,
  a `@ManyToOne` relation, a collection) live as test resources, compiled with
  `JPAMetaModelEntityProcessor` registered via `setProcessors(...)`, generated sources
  written to a `@TempDir`.
- Assert the generated `<Entity>_.java` files exist, contain the expected
  restriction/path methods, and compile successfully in a second pass (the generated
  builders only need `jakarta.persistence` + `hql-query-builder`, already on the test
  classpath).
- For `@HQLQueries` interfaces: assert the generated `<Name>Provider.java` source text
  contains the expected factory methods, but do **not** attempt to compile it. Its template
  hardcodes legacy `javax.enterprise.context.ApplicationScoped` / `javax.inject.Inject`
  imports (not `jakarta.*`), which aren't on this project's classpath. This is pre-existing
  behavior, out of scope for this task — noted in the PR description, not fixed.

## Workflow

Work happens on a new branch (`add-tests`). Run `mvn clean verify` locally to confirm
everything passes and review the JaCoCo report before opening a PR against `master` (per
CLAUDE.md, avoid pushing directly to `master`).
