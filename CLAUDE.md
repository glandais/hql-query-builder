# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

A Java library that builds HQL (Hibernate Query Language) queries dynamically through a type-safe, generated DSL, instead of hand-writing JPQL/HQL strings or using the JPA Criteria API. It's a multi-module Maven project:

- **`hql-query-builder`**: the runtime library. Contains `HQLQueryBuilder` (assembles and executes HQL via `jakarta.persistence.EntityManager`), the `HQLRestriction*` classes (a small criteria-style DSL: eq, neq, in, like, isNull, isEmpty, memberOf, and/or/not...), and `HQLSafePath*` (typed path wrappers used by generated code to build property paths like `this_.foo.bar`).
- **`hql-query-builder-generator`**: a Java annotation processor (`javax.annotation.processing.Processor`, registered in `META-INF/services`) derived from Hibernate's JPA static metamodel generator. At compile time it scans classes annotated with `@Entity`, `@MappedSuperclass`, `@Embeddable` and generates, for each entity, a `<EntityName>_` class (suffix defined by `ClassWriter.SUFFIX`) extending `HQLSafePathEntity`/`HQLSafePath`, giving compile-time-safe restriction/path methods per attribute (templated from `hql-query-builder-generator/src/main/resources/template.java`). It also scans interfaces annotated with `@HQLQueries` (from `hql-query-builder`) and generates a CDI `@ApplicationScoped` `<Name>Provider` class injecting `EntityManager` and exposing factory methods for each generated `<EntityName>_` root builder.

Consumers add `hql-query-builder` as a runtime dependency and `hql-query-builder-generator` as an annotation processor; the generator's output is what application code actually uses to build queries fluently against generated entity-shaped builders.

## Key implementation notes

- `HQLQueryBuilder` assumes **Hibernate** as the JPA provider: it sets the Hibernate-specific `org.hibernate.cacheable` query hint in `createRealQuery`. The `jakarta.persistence-api` dependency is `provided` scope — the actual JPA implementation (Hibernate) is supplied by the consuming application.
- `HQLQueryBuilderCache` (keyed by entity canonical name, in a static synchronized map on `HQLQueryBuilder`) caches per-entity-type metadata (aliases, bag/collection detection, `EntityType` lookups) computed via the JPA `Metamodel`, since that introspection is expensive and stable per entity class.
- The generator module's `javax.*` imports (`javax.annotation.processing`, `javax.lang.model`, `javax.tools`) are the **annotation-processing API**, unrelated to JPA — do not confuse these with `jakarta.persistence`. All actual JPA references (annotations like `@Entity`, types like `EntityManager`/`Query`/`EntityType`) use the `jakarta.persistence` namespace (Jakarta Persistence 3.2 / Jakarta EE 11 baseline).
- There are currently no automated tests in this repository.

## Build and CI

- Build everything: `mvn clean install`
- Compile/verify only: `mvn -B -ntp verify` (this is what `PR Checks` CI runs on every PR)
- Requires **Java 17+** (`maven.compiler.source`/`target` = 17), matching the `jakarta.persistence-api` 3.2.0 baseline.
- `master` branch pushes (`.github/workflows/master.yml`) auto-release: strip `-SNAPSHOT`, build, commit/tag the release, then bump to the next `-SNAPSHOT` and push — this is automatic on every push to `master`, so avoid pushing directly to `master` outside of merging a reviewed PR.
- `jitpack.yml` still pins `openjdk11` for JitPack builds (publishing via JitPack) — this is stale relative to the Java 17 requirement above and may need updating if JitPack builds start failing.
- Dependency updates are managed by Dependabot (`.github/dependabot.yml`), weekly on Mondays, for both Maven and GitHub Actions ecosystems.
