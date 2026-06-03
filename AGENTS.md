# AGENTS.md

## Purpose
- `geojson4j` is a Java 25 library that models RFC 7946 GeoJSON objects and validates them.
- AI agent rule files in this repo: `.github/copilot-instructions.md` (Copilot-specific), `AGENTS.md` (this file).
## Architecture (Read This First)
- Core model lives in `src/main/java/com/github/nramc/geojson/domain`.
- `GeoJson` is the sealed root; `Geometry` is a sealed sub-hierarchy for geometry-only types.
- Jackson polymorphism is driven by `type` via `@JsonTypeInfo`/`@JsonSubTypes` in both `GeoJson` and `Geometry`.
- GeoJSON type literals are centralized in `src/main/java/com/github/nramc/geojson/constant/GeoJsonType.java`.
- Validation contract is explicit: constructors are deserialization-friendly (may create invalid objects), while `of(...)` factories do eager validation and throw `GeoJsonValidationException`.
- Validation result flow: `Validatable#validate()` -> `ValidationResult` -> `ValidationError` set.
- Validator types (`Validatable`, `ValidationResult`, `ValidationError`, `ValidationUtils`, `GeoJsonValidationException`) live in `src/main/java/com/github/nramc/geojson/validator`.
## Project-Specific Patterns
- Keep domain objects immutable-ish: `final` fields + defensive copies (`List.copyOf`, `Map.copyOf`, unmodifiable wrappers).
- Preserve default no-arg constructors plus `@JsonCreator`/`@JsonProperty` constructors for Jackson compatibility.
- Keep error keys stable (`coordinates.latitude.invalid`, `type.invalid`, etc.); tests assert on these keys.
  - Full set of stable keys: `type.invalid`, `coordinates.invalid.empty`, `coordinates.latitude.invalid`, `coordinates.longitude.invalid`, `coordinates.invalid.minimum.positions`, `coordinates.invalid.ring.closed`, `geometries.invalid.nested.geometry`, `geometry.invalid.empty`.
- Keep `toString`, `equals`, and `hashCode` implemented for every domain type (tests validate behavior).
- `GeometryCollection` intentionally rejects nested `GeometryCollection` instances (`geometries.invalid.nested.geometry`).
- `PolygonCoordinates` enforces ring rules (>=4 positions and first == last) and is reused by `Polygon`/`MultiPolygon`.
- Use `@Generated` (from `com.github.nramc.geojson.internal.utils.Generated`) to annotate boilerplate/generated code that should be excluded from JaCoCo coverage reports.
## Build, Test, and Quality Workflows
- Fast local tests: `mvn test`
- Full local gate (includes Checkstyle at `verify`): `mvn verify`
- Coverage profile: `mvn -Pcoverage verify`
- OpenRewrite profile (recipes in `rewrite.yml`): `mvn -Popen-rewrite process-sources` and `mvn -Popen-rewrite prepare-package`
- Release/signing profile: `mvn -Pgithub verify`
- CI profile (`ci-profile`) auto-activates when `env.CI=true` and skips the OpenRewrite run execution; do not rely on OpenRewrite applying source changes in CI.
- If Maven dependency resolution fails with cert issues, check your local Maven mirror/proxy trust setup before changing project code.
## Style and Tooling Constraints
- Checkstyle rules are in `config/checkstyle/geojson4j_checks.xml` (Google-style base, 160-char line limit, no star imports).
- Suppressions are minimal and intentional in `config/checkstyle/checkstyle-suppressions.xml`.
- Apache 2 license headers are expected; OpenRewrite is configured to add/maintain them (`rewrite.yml`).
- **Import ordering is NOT managed by OpenRewrite** (OrderImports uses auto-detection, not the configured style). After running `mvn -Popen-rewrite process-sources`, use IntelliJ "Optimize Imports" (⌥⌘O) to restore correct static-first import order matching `geojson4j_checks.xml`.
## Integration Points
- External API surface is Jackson-based serialization/deserialization of `GeoJson` and `Geometry`.
- Jackson 3.x is used (`tools.jackson:jackson-bom:3.1.4`); when adding Jackson dependencies use the `tools.jackson.*` groupId (e.g., `tools.jackson.core:jackson-databind`).
- Tests use JUnit 6 (`org.junit:junit-bom:6.1.0`); write tests with JUnit 6 APIs.
- Example app in `examples/spring-mvc-example` (Spring MVC + JPA + H2): `GeoJsonEchoResource` (echo), `GeoJsonValidationResource` (returns `ValidationResult`), `GeoJsonCrudResource` (persist/read `GeoJson`).
- Root Maven build targets the library; example module is a separate sample project.
## When Adding a New GeoJSON Type
- Add the type constant to `GeoJsonType`.
- Add subtype mappings in both `GeoJson` and `Geometry` if it is a geometry.
- Implement class with constructor + `of(...)` factory + `validate()` + `toString/equals/hashCode`.
- Add focused tests in `src/test/java/com/github/nramc/geojson/domain` covering serialization, base-type deserialization, invalid cases, and eager validation exceptions.
