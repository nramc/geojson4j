# AGENTS.md

## Purpose
- `geojson4j` is a Java 21 library that models RFC 7946 GeoJSON objects and validates them.
- Primary source of existing agent guidance: `README.md` (no other agent-rule files were found).
## Architecture (Read This First)
- Core model lives in `src/main/java/com/github/nramc/geojson/domain`.
- `GeoJson` is the sealed root; `Geometry` is a sealed sub-hierarchy for geometry-only types.
- Jackson polymorphism is driven by `type` via `@JsonTypeInfo`/`@JsonSubTypes` in both `GeoJson` and `Geometry`.
- GeoJSON type literals are centralized in `src/main/java/com/github/nramc/geojson/constant/GeoJsonType.java`.
- Validation contract is explicit: constructors are deserialization-friendly (may create invalid objects), while `of(...)` factories do eager validation and throw `GeoJsonValidationException`.
- Validation result flow: `Validatable#validate()` -> `ValidationResult` -> `ValidationError` set.
## Project-Specific Patterns
- Keep domain objects immutable-ish: `final` fields + defensive copies (`List.copyOf`, `Map.copyOf`, unmodifiable wrappers).
- Preserve default no-arg constructors plus `@JsonCreator`/`@JsonProperty` constructors for Jackson compatibility.
- Keep error keys stable (`coordinates.latitude.invalid`, `type.invalid`, etc.); tests assert on these keys.
- Keep `toString`, `equals`, and `hashCode` implemented for every domain type (tests validate behavior).
- `GeometryCollection` intentionally rejects nested `GeometryCollection` instances (`geometries.invalid.nested.geometry`).
- `PolygonCoordinates` enforces ring rules (>=4 positions and first == last) and is reused by `Polygon`/`MultiPolygon`.
## Build, Test, and Quality Workflows
- Fast local tests: `mvn test`
- Full local gate (includes Checkstyle at `verify`): `mvn verify`
- Coverage profile: `mvn -Pcoverage verify`
- OpenRewrite profile (recipes in `rewrite.yml`): `mvn -Popen-rewrite process-sources` and `mvn -Popen-rewrite prepare-package`
- Release/signing profile: `mvn -Pgithub verify`
- If Maven dependency resolution fails with cert issues, check your local Maven mirror/proxy trust setup before changing project code.
## Style and Tooling Constraints
- Checkstyle rules are in `config/checkstyle/geojson4j_checks.xml` (Google-style base, 160-char line limit, no star imports).
- Suppressions are minimal and intentional in `config/checkstyle/checkstyle-suppressions.xml`.
- Apache 2 license headers are expected; OpenRewrite is configured to add/maintain them (`rewrite.yml`).
## Integration Points
- External API surface is Jackson-based serialization/deserialization of `GeoJson` and `Geometry`.
- Example app in `examples/spring-mvc-example` (Spring MVC + JPA + H2): `GeoJsonEchoResource` (echo), `GeoJsonValidationResource` (returns `ValidationResult`), `GeoJsonCrudResource` (persist/read `GeoJson`).
- Root Maven build targets the library; example module is a separate sample project.
## When Adding a New GeoJSON Type
- Add the type constant to `GeoJsonType`.
- Add subtype mappings in both `GeoJson` and `Geometry` if it is a geometry.
- Implement class with constructor + `of(...)` factory + `validate()` + `toString/equals/hashCode`.
- Add focused tests in `src/test/java/com/github/nramc/geojson/domain` covering serialization, base-type deserialization, invalid cases, and eager validation exceptions.
