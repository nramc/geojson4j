[![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/nramc/geojson4j/ci-workflow.yml?branch=main&style=flat&logoColor=ff0)](https://github.com/nramc/geojson4j/actions/workflows/ci-workflow.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=nramc_geojson4j&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=nramc_geojson4j)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=nramc_geojson4j&metric=coverage)](https://sonarcloud.io/summary/new_code?id=nramc_geojson4j)
[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.nramc/geojson4j?style=flat&logo=apachemaven&logoColor=%23C71A36&color=%2303C75A)](https://mvnrepository.com/artifact/io.github.nramc/geojson4j)
[![Badge](https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=159&style=flat)](https://www.linkedin.com/in/ramachandran-nellaiyappan/)

# geojson4j [GeoJSON for Java]

GeoJSON4j is a lightweight, easy-to-use Java library for working with GeoJSON data, fully compliant
with [RFC-7946](https://datatracker.ietf.org/doc/html/rfc7946). It provides utilities to create, parse, and serialize
GeoJSON objects, such as Point, LineString, Polygon, and FeatureCollection, to simplify geospatial data handling in Java
applications.

---

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Documentation](#documentation)
- [Issues](#issues)
- [Contributing](#contributing)
- [License](#license)
- [Credits](#credits)
- [Contact](#contact)

## Introduction

This project provides a Java-based domain model for working with GeoJSON data, ensuring compliance with RFC 7946.
GeoJSON is a format for encoding a variety of geographic data structures using JSON. This domain model simplifies the
use of GeoJSON in Java applications by providing a strong, object-oriented approach to geospatial data.

The domain model supports GeoJSON geometry types, including:

- Point
- LineString
- Polygon
- MultiPoint
- MultiLineString
- MultiPolygon
- GeometryCollection
- Feature
- FeatureCollection

Additionally, it includes methods for validation, serialization, and conversion between GeoJSON and other geospatial
formats, while ensuring full compliance with [RFC 7946](https://datatracker.ietf.org/doc/html/rfc7946).

## Features

- RFC 7946 Compliance: Fully adheres to the [RFC 7946](https://datatracker.ietf.org/doc/html/rfc7946) GeoJSON standard.
- Support for Common GeoJSON Types: Including `Point`, `LineString`, `Polygon`, `MultiPoint`, `MultiLineString`,
  `MultiPolygon`, `Feature` and `FeatureCollection`.
- Serialization/Deserialization: Easily convert between Java objects and GeoJSON format using Jackson.
- Validation: Built-in validation to ensure that GeoJSON data is well-formed and complies with the specification.
- Extensible: The model is designed to be extensible for custom use cases and geospatial operations.

## Installation

To include this GeoJSON domain model in your Java project, follow these instructions:

### Requirements

- Java 8 or later
- Maven or Gradle as the build system

### Maven Installation

If you're using Maven, add the following dependency to your pom.xml file:

```xml

<dependency>
    <groupId>io.github.nramc</groupId>
    <artifactId>geojson4j</artifactId>
    <version>${geojson4j.version}</version>
</dependency>

```

### Gradle Installation

For Gradle, add this line to your build.gradle file:

```groovy
implementation 'io.github.nramc:geojson4j:1.0.0'

```

Then, run mvn install or gradle build to download the dependencies.

## Usage

Once the library is installed, you can start working with the GeoJSON domain model in your Java application.

### Serialization

```java
import com.github.nramc.geojson.domain.Point;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GeoJsonExample {
    public static void main(String[] args) throws Exception {
        // Create a Point object with longitude & latitude
        Point point = Point.of(60.8, 20.5);

        // Serialize the Point object to GeoJSON format
        ObjectMapper mapper = new ObjectMapper();
        String geoJson = mapper.writeValueAsString(point);

        // Print GeoJSON
        System.out.println(geoJson);
    }
}

```

### Deserialization

```java
import com.github.nramc.geojson.domain.Point;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GeoJsonExample {
    public static void main(String[] args) throws Exception {
        // Create a Point object with longitude & latitude
        String geoJson = """
                { "type": "Point", "coordinates": [60.8, 20.5] }""";

        // Deserialize the GeoJSON string to Point object
        ObjectMapper mapper = new ObjectMapper();
        Point point = mapper.readValue(geoJson, Point.class);

        // Print Coordinates
        System.out.println("long: %f lat:%f", point.getCoordinates().getLongitude(), point.getCoordinates().getLatitude());
    }
}

```

## Documentation

- Full API documentation is available in `todo`.
- Refer to the `examples directory` for more sample use cases.

## Issues

If you find any bugs or have a feature request, please open an issue
on [GitHub Issues](https://github.com/nramc/geojson4j/issues).

## Contributing

Any contributions you make are greatly appreciated.

If you like the project and have a suggestion that would make this better, please fork the repo and create a pull
request. You can also simply open an issue with the tag "enhancement".

1. Fork the Project
2. Create your Feature Branch (git checkout -b feature/AmazingFeature)
3. Commit your Changes (git commit -m 'feat: Add the AmazingFeature')
4. Push to the Branch (git push origin feature/AmazingFeature)
5. Open a Pull Request

## License

This project is licensed under the [Apache License](./LICENSE).

## Credits

Sincere Thanks to following open source community for their wonderful efforts to make our life much easier.

- [SonarCloud](https://sonarcloud.io/) - Static code analysis tool
- [Renovate](https://docs.renovatebot.com/) - Automated dependencies update
- [OpenRewrite](https://docs.openrewrite.org/) - Automated source code refactoring
- [Jackson](https://github.com/FasterXML/jackson) - JSON Parser Library

## Contact

Ramachandran
Nellaiyappan [Website](https://github.com/nramc) | [Twitter](https://twitter.com/ram_n_74) | [E-Mail](mailto:ramachandrannellai@gmail.com)

## Show your support

Give a ⭐️ if you like this project!
