hql-query-builder
=================

A Java library for building [HQL](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#hql) queries dynamically through a **type-safe, generated DSL**, instead of hand-writing JPQL/HQL strings or wrestling with the JPA Criteria API.

Annotate your `@Entity` classes, let the annotation processor generate a typed query-builder class per entity, then compose restrictions, joins, ordering and pagination with plain Java method calls — the HQL string is built and executed for you against an `EntityManager`.

## Modules

The project is a two-module Maven build:

- **`hql-query-builder`** — the runtime library: `HQLQueryBuilder` (assembles and executes HQL), the `HQLRestrictions` DSL (eq, neq, in, like, isNull, isEmpty, memberOf, and/or/not, comparisons…), and the `HQLSafePath*` typed path wrappers used by generated code.
- **`hql-query-builder-generator`** — a Java annotation processor (derived from Hibernate's JPA static metamodel generator). At compile time it scans `@Entity`, `@MappedSuperclass` and `@Embeddable` classes and generates, for each one, a `<EntityName>_` class extending `HQLSafePathEntity`/`HQLSafePath` with one typed accessor method per persistent attribute. It also scans interfaces annotated with `@HQLQueries` and generates a CDI `@ApplicationScoped` `<Name>Provider` injecting `EntityManager` and exposing a factory method per generated `<EntityName>_` builder.

## Requirements

- Java 17+
- `jakarta.persistence-api` 3.2 (provided scope — bring your own JPA implementation)
- A Hibernate-based JPA provider at runtime (see [Hibernate-specific behavior](#hibernate-specific-behavior) below)

## Installation

Published via [JitPack](https://jitpack.io/#glandais/hql-query-builder):

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.glandais.hql-query-builder</groupId>
        <artifactId>hql-query-builder</artifactId>
        <version>VERSION</version>
    </dependency>
    <dependency>
        <groupId>com.github.glandais.hql-query-builder</groupId>
        <artifactId>hql-query-builder-generator</artifactId>
        <version>VERSION</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

`hql-query-builder-generator` only needs to be on the annotation-processing classpath (`provided` scope, or your compiler plugin's `annotationProcessorPaths`).

## Usage

Given an entity:

```java
@Entity
public class Person {
    @Id
    private Long id;
    private String name;
    private Integer age;
    private Date birthDate;
    @ManyToOne
    private Address address;
    @OneToMany
    private List<Pet> pets;
}
```

the annotation processor generates `Person_<Person>`, with one method per attribute returning a typed path object:

- `name()` → `HQLSafePathBasicString<String>`
- `age()` → `HQLSafePathBasicNumber<Integer>`
- `birthDate()` → `HQLSafePathBasicDate<Date>`
- `address()` → `Address_<Address>` (a nested `HQLSafePathEntity`, joined implicitly when traversed)
- `pets()` → `Pet_<Pet>` (collection association, also joined implicitly)

Build and run a query directly:

```java
Person_<Person> person = new Person_<>(entityManager);

person.name().like("Jo%", HQLRestrictionLikeMatchMode.START);
person.age().ge(18);
person.address().city().eq("Paris");
person.addRestriction(HQLRestrictions.or(
    person.pets().name().eqRestriction("Rex"),
    person.pets().isEmptyRestriction()
));
person.addOrder(person.name().getPath(), true);
person.setFirstResult(0);
person.setMaxResults(20);

List<Person> result = person.getList();
int count = person.getCount();
Person first = person.getUniqueResult();
```

Or expose builders through a CDI provider by declaring an `@HQLQueries` interface:

```java
@HQLQueries
public interface Queries {
    Person_<Person> person();
}
```

which generates `QueriesProvider`, an `@ApplicationScoped` bean injecting `EntityManager` and implementing `Queries`.

## Features

- **Type-safe path navigation**: every entity attribute (basic, string, number, date, association, collection) gets a dedicated typed accessor on the generated `<Entity>_` class, instead of raw string property names.
- **Implicit joins**: traversing an association path (e.g. `person.address().city()`) automatically registers the corresponding HQL join — `left join` by default, switching to `inner join` for subsequent traversals of the same collection-typed (bag) path to avoid duplicate-row blowup.
- **Fetch joins**: `addFetch(path)` adds a `fetch` to an already-joined association.
- **Restrictions DSL** (`HQLRestrictions` / per-path helpers): `eq`, `neq`, `eqIfValueNotNull`, `in`, `nin`, `like` (with `EXACT`/`START`/`END`/`ANYWHERE` match modes, always case-insensitive), `isNull`, `isNotNull`, `isEmpty`, `isNotEmpty` (collections), `ge`, `le`, `gt`, `lt` (numbers/dates), `memberOf`, equality between two paths, and logical composition with `and`/`or`/`not`.
- **Ordering**: `addOrder(path, ascending)`, with an optional case-insensitive variant (wraps the path in `lower(...)`).
- **Pagination**: `setFirstResult` / `setMaxResults`.
- **Counting**: `getCount()` (distinct root count), `getCountOnPath()`/`getCountDistinctOnPath()` on any path.
- **Multi-select**: `getMultiSelect(...)` projects one or several paths instead of the whole entity.
- **Query result caching**: `isCachable()`/`setCachable()` toggles the Hibernate `org.hibernate.cacheable` query hint (enabled by default).
- **Per-entity metadata caching**: alias and collection/`EntityType` metadata is computed once per entity class via the JPA `Metamodel` and cached statically, since that introspection is comparatively expensive.
- **Statistics helper**: `getStatistics(path)` / `getListWithStatistics(items)` / `getListWithStatisticsItems(...)` group-count distinct values of one or more paths (optionally each with its own extra restriction) against the current restriction set, useful for building facet/filter counts.
- **`computeListUsingIdSubQuery`**: an opt-in strategy where `getList()` first selects matching ids, then re-selects full entities by id — useful to work around HQL `DISTINCT` + pagination interactions on queries with collection joins.
- **CDI integration**: `@HQLQueries`-annotated interfaces get a generated `@ApplicationScoped` provider class injecting `EntityManager`.

## Unsupported HQL features

The library targets the common "select / where / order by / paginate / fetch" use case with a Criteria-like restriction DSL. The following HQL/JPQL constructs are **not** exposed and require dropping down to a plain `EntityManager.createQuery(...)`:

- **`HAVING`** — not exposed (the internal `group by` used by the statistics helpers has no filtered-aggregate equivalent).
- **Real subqueries** (`EXISTS`, `IN (subquery)`, `ANY`/`ALL`/`SOME`, correlated subqueries) — `computeListUsingIdSubQuery` only chains two independent queries in Java; it does not emit a nested HQL subquery.
- **Bulk `UPDATE` / `DELETE`** (`executeUpdate`) — the builder only ever generates read (`SELECT`) queries.
- **Constructor expressions** (`SELECT NEW Foo(...)`).
- **`CASE WHEN ... THEN ... ELSE ... END`** expressions.
- **`UNION` / `INTERSECT` / `EXCEPT`**.
- **Multiple root entities** (`from A, B`) or joins with an arbitrary `ON` predicate — joins are always derived implicitly from the root entity's association graph.
- **Aggregate functions beyond `count`** — no `sum`, `avg`, `min`, `max` in the DSL.
- **`BETWEEN`** — no dedicated operator, though it can be emulated with `HQLRestrictions.and(ge(...), le(...))`.
- **Case-sensitive or negated `LIKE`** — `like()` always lower-cases both sides and has no `NOT LIKE` / `ESCAPE` support.
- **Scalar HQL functions** (`upper`, `concat`, `substring`, `trim`, `length`, `coalesce`, date/time functions, `cast`, arithmetic expressions) — not exposed; only `lower(...)` is used internally (for `LIKE` and case-insensitive ordering).
- **`INDEX()` / `KEY()` / `VALUE()`** on lists/maps — not exposed.

## Hibernate-specific behavior

`HQLQueryBuilder` sets the Hibernate-specific `org.hibernate.cacheable` query hint on every query it builds, so it assumes **Hibernate** as the JPA provider at runtime even though it only depends on the standard `jakarta.persistence-api` at compile time.

## Building

```sh
mvn clean install        # build everything
mvn -B -ntp verify        # compile + verify, what CI runs on every PR
```

Requires Java 17+ (the project targets `jakarta.persistence-api` 3.2 / Jakarta EE 11). There are currently no automated tests in this repository.
