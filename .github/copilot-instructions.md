# Copilot Instructions for `geojson4j`

## Scope
- This repository is a Java 21 GeoJSON (RFC 7946) domain library.
- Prioritize correctness of GeoJSON modeling, validation behavior, and Jackson compatibility over broad refactors.

## Architecture Rules
- Core model lives in `src/main/java/com/github/nramc/geojson/domain`.
- Keep sealed hierarchy intact: `GeoJson` as root, `Geometry` for geometry subtypes.
- Preserve Jackson polymorphism using `type` with `@JsonTypeInfo`/`@JsonSubTypes` in both `GeoJson` and `Geometry`.
- Keep type literals centralized in `src/main/java/com/github/nramc/geojson/constant/GeoJsonType.java`.

## Validation Contract (Important)
- Constructors are deserialization-friendly and may produce invalid instances.
- Static `of(...)` factory methods must perform eager validation via `ValidationUtils.validateAndThrowErrorIfInvalid(...)`.
- `validate()` must return `ValidationResult` with stable `ValidationError` keys (do not rename existing keys).
- Keep `Validatable#isValid()` / `hasErrors()` behavior consistent.

## Domain Object Patterns
- Prefer immutable state: `final` fields and defensive copies (`List.copyOf`, `Map.copyOf`, unmodifiable wrappers).
- Keep no-arg constructors plus `@JsonCreator`/`@JsonProperty` constructors for Jackson.
- Implement and maintain `toString`, `equals`, and `hashCode` for each domain type.
- Keep existing constraints, including:
  - `GeometryCollection` must reject nested `GeometryCollection` (`geometries.invalid.nested.geometry`).
  - `PolygonCoordinates` ring rules (`>= 4` positions and first == last).

## Testing Expectations
- Add/adjust tests under `src/test/java/com/github/nramc/geojson/domain`.
- Cover serialization, deserialization via base types (`GeoJson`, `Geometry`), invalid states, and eager-validation exceptions.
- Assert error keys explicitly (examples: `type.invalid`, `coordinates.latitude.invalid`).

## Build and Quality
- Fast checks: `mvn test`
- Full local gate: `mvn verify` (includes Checkstyle)
- Coverage profile: `mvn -Pcoverage verify`
- OpenRewrite profile: `mvn -Popen-rewrite process-sources` and `mvn -Popen-rewrite prepare-package`

## Style Constraints
- Follow Checkstyle in `config/checkstyle/geojson4j_checks.xml` (Google-style base, 160-char line limit, no star imports).
- Keep Apache 2 headers; OpenRewrite (`rewrite.yml`) is configured to maintain them.

