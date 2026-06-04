<div align="center">

# geojson4j

**A lightweight, RFC 7946-compliant GeoJSON library for Java**

[![CI](https://img.shields.io/github/actions/workflow/status/nramc/geojson4j/ci-workflow.yml?branch=main&style=flat&label=CI&logo=github)](https://github.com/nramc/geojson4j/actions/workflows/ci-workflow.yml)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=nramc_geojson4j&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=nramc_geojson4j)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=nramc_geojson4j&metric=coverage)](https://sonarcloud.io/summary/new_code?id=nramc_geojson4j)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.nramc/geojson4j?style=flat&logo=apachemaven&logoColor=%23C71A36&color=%2303C75A)](https://mvnrepository.com/artifact/io.github.nramc/geojson4j)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/projects/jdk/21/)

[Getting Started](#installation) · [Usage](#usage) · [API Reference](#api-reference) · [Contributing](#contributing)

</div>

---

## What is geojson4j?

**geojson4j** is a Java domain library for working
with [GeoJSON (RFC 7946)](https://datatracker.ietf.org/doc/html/rfc7946) data. It lets you create, parse, validate, and
serialize all standard GeoJSON types using a fluent, type-safe API built on top of Jackson.

### Why geojson4j?

- ✅ **RFC 7946 compliant** — full specification coverage
- 🔒 **Immutable domain objects** — thread-safe by design
- ⚡ **Eager validation** — factory methods throw on invalid data; constructors stay deserialization-friendly
- 🔌 **Jackson-native** — drop-in serialization/deserialization with `@JsonCreator` / `@JsonSubTypes` polymorphism
- 🧪 **Well-tested** — serialization, deserialization, invalid states, and validation error keys all covered

---

## Supported GeoJSON Types

| GeoJSON Type         | Java Class           | RFC 7946 Section |
|----------------------|----------------------|------------------|
| `Point`              | `Point`              | §3.1.2           |
| `LineString`         | `LineString`         | §3.1.4           |
| `Polygon`            | `Polygon`            | §3.1.6           |
| `MultiPoint`         | `MultiPoint`         | §3.1.3           |
| `MultiLineString`    | `MultiLineString`    | §3.1.5           |
| `MultiPolygon`       | `MultiPolygon`       | §3.1.7           |
| `GeometryCollection` | `GeometryCollection` | §3.1.8           |
| `Feature`            | `Feature`            | §3.2             |
| `FeatureCollection`  | `FeatureCollection`  | §3.3             |

---

## Installation

**Requirements:** Java 21+, Maven or Gradle

### Maven

```xml
<dependency>
    <groupId>io.github.nramc</groupId>
    <artifactId>geojson4j</artifactId>
    <version>${geojson4j.version}</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.github.nramc:geojson4j:1.0.17'
```

> Check [Maven Central](https://mvnrepository.com/artifact/io.github.nramc/geojson4j) for the latest version.

---

## Usage

### Creating GeoJSON Objects

Use the static `of(...)` factory methods. They validate eagerly and throw `GeoJsonValidationException` if the data is
invalid.

```java
// Point — longitude, latitude
Point point = Point.of(102.0, 0.5);

// Point with altitude
Point point3d = Point.of(102.0, 0.5, 256.0);

// LineString — varargs of Position
LineString line = LineString.of(
        Position.of(102.0, 0.0),
        Position.of(103.0, 1.0),
        Position.of(104.0, 0.0)
);

// Polygon — exterior ring must have ≥ 4 positions; first == last (closed)
// PolygonCoordinates.of() accepts one or more List<Position> rings (first = exterior)
List<Position> exterior = List.of(
        Position.of(100.0, 0.0),
        Position.of(101.0, 0.0),
        Position.of(101.0, 1.0),
        Position.of(100.0, 1.0),
        Position.of(100.0, 0.0)   // close the ring
);
Polygon polygon = Polygon.of(exterior);

// Feature — id (nullable), geometry, properties
Map<String, Serializable> props = new HashMap<>();
props.put("name", "My Location");
props.put("category", "landmark");
Feature feature = Feature.of("location-1", point, props);

// FeatureCollection
FeatureCollection collection = FeatureCollection.of(List.of(feature));
```

### Serialization (Object → JSON)

```java
ObjectMapper mapper = new ObjectMapper();

Point point = Point.of(102.0, 0.5);
String json = mapper.writeValueAsString(point);
// {"type":"Point","coordinates":[102.0,0.5]}
```

### Deserialization (JSON → Object)

Deserialize to a concrete type:

```java
String json = """
        { "type": "Point", "coordinates": [102.0, 0.5] }
        """;

ObjectMapper mapper = new ObjectMapper();
Point point = mapper.readValue(json, Point.class);

double lon = point.getCoordinates().getLongitude(); // 102.0
double lat = point.getCoordinates().getLatitude();  // 0.5
```

Deserialize to the base type and let Jackson resolve the subtype via the `type` field:

```java
GeoJson geoJson = mapper.readValue(json, GeoJson.class);   // polymorphic root
Geometry geometry = mapper.readValue(json, Geometry.class);  // geometry-only polymorphic
```

### Validation

Constructors (used during deserialization) do **not** validate eagerly — this lets you inspect partially-populated
objects. Use `validate()` for detailed error information or `isValid()` for a quick check.

```java
// Quick boolean check
GeoJson geoJson = mapper.readValue(json, GeoJson.class);
boolean valid = geoJson.isValid();

// Detailed validation result
ValidationResult result = geoJson.validate();

if (result.hasErrors()) {
    result.getErrors().forEach(error ->
        System.out.printf("Field: %-30s Key: %-40s Message: %s%n",
            error.getField(), error.getKey(), error.getMessage())
    );
}
```

Stable validation error keys you can rely on in code:

| Key                                     | Meaning                                       |
|-----------------------------------------|-----------------------------------------------|
| `type.invalid`                          | `type` field is missing or wrong              |
| `coordinates.invalid.empty`             | `coordinates` is null or empty                |
| `coordinates.latitude.invalid`          | latitude is out of the valid range            |
| `coordinates.longitude.invalid`         | longitude is out of the valid range           |
| `coordinates.invalid.minimum.positions` | not enough positions (e.g., polygon ring < 4) |
| `coordinates.invalid.ring.closed`       | polygon ring is not closed (first ≠ last)     |
| `geometries.invalid.nested.geometry`    | `GeometryCollection` contains another one     |

#### Fail-fast with factory methods

```java
try {
    // Latitude 999 is out of range → throws immediately
    Point invalid = Point.of(102.0, 999.0);
} catch (GeoJsonValidationException ex) {
    // handle validation failure
}
```

---

## API Reference

### Class Hierarchy

```
GeoJson (sealed)
├── Geometry (sealed)
│   ├── Point
│   ├── LineString
│   ├── Polygon
│   ├── MultiPoint
│   ├── MultiLineString
│   ├── MultiPolygon
│   └── GeometryCollection
├── Feature
└── FeatureCollection
```

### Key Types

| Type                         | Description                                                  |
|------------------------------|--------------------------------------------------------------|
| `GeoJson`                    | Sealed root; Jackson polymorphism resolved via `type`        |
| `Geometry`                   | Sealed base for all geometry types                           |
| `Position`                   | Longitude / latitude / optional altitude coordinate tuple    |
| `PolygonCoordinates`         | Enforces ring rules (≥ 4 positions, closed)                  |
| `Validatable`                | Interface providing `validate()`, `isValid()`, `hasErrors()` |
| `ValidationResult`           | Holds a set of `ValidationError` instances                   |
| `GeoJsonValidationException` | Thrown by factory methods on invalid input                   |

---

## Examples

A runnable Spring MVC example is available in [`examples/spring-mvc-example`](examples/spring-mvc-example). It
demonstrates:

- **Echo endpoint** — round-trip serialization of any `GeoJson` object
- **Validation endpoint** — returns `ValidationResult` as JSON
- **CRUD endpoint** — persists and retrieves `GeoJson` using JPA + H2

---

## Build & Development

```bash
# Run tests only
mvn test

# Full build including Checkstyle
mvn verify

# With code-coverage report
mvn -Pcoverage verify

# Apply OpenRewrite recipes (license headers, static-analysis fixes …)
mvn -Popen-rewrite process-sources

# Dry-run OpenRewrite (check without applying)
mvn -Popen-rewrite prepare-package
```

Code style is enforced by Checkstyle (`config/checkstyle/geojson4j_checks.xml`):

- Google-style base
- 160-character line limit
- No wildcard imports

---

## Contributing

Contributions are very welcome! Here's how to get started:

1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feature/my-feature`
3. **Commit** your changes following the [Conventional Commits](https://www.conventionalcommits.org/) format
4. **Push** the branch: `git push origin feature/my-feature`
5. **Open a Pull Request** — the CI pipeline will run automatically

Please open an issue on [GitHub Issues](https://github.com/nramc/geojson4j/issues) to report bugs or request features
before starting a large change.

### Adding a New GeoJSON Type

Follow the pattern established by existing types:

1. Add the type constant in `GeoJsonType.java`
2. Register `@JsonSubTypes` entries in both `GeoJson` and `Geometry`
3. Implement the class: no-arg constructor + `@JsonCreator` constructor + `of(...)` factory + `validate()` +
   `equals/hashCode/toString`
4. Add tests covering serialization, base-type deserialization, invalid states, and eager-validation exceptions

---

## License

Distributed under the **Apache License 2.0**. See [`LICENSE`](./LICENSE) for full text.

---

## Acknowledgements

| Tool                                            | Purpose                              |
|-------------------------------------------------|--------------------------------------|
| [Jackson](https://github.com/FasterXML/jackson) | JSON serialization / deserialization |
| [SonarCloud](https://sonarcloud.io/)            | Static analysis & quality gate       |
| [OpenRewrite](https://docs.openrewrite.org/)    | Automated code refactoring           |
| [Renovate](https://docs.renovatebot.com/)       | Automated dependency updates         |

---

## Contact

**Ramachandran Nellaiyappan**

[![GitHub](https://img.shields.io/badge/GitHub-nramc-181717?logo=github)](https://github.com/nramc)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-connect-0A66C2?logo=linkedin)](https://www.linkedin.com/in/ramachandran-nellaiyappan/)
[![Email](https://img.shields.io/badge/Email-ramachandrannellai%40gmail.com-D14836?logo=gmail)](mailto:ramachandrannellai@gmail.com)

---

<div align="center">

⭐ If you find this project useful, please give it a star — it helps a lot!

</div>
