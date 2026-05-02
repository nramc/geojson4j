# Code Style Configuration

This directory contains all code-style tooling configuration for `geojson4j`.

## Overview

The project uses **Google Java Style** as its baseline, with a small set of deliberate
project-specific overrides applied consistently across all three enforcement layers:

| Layer        | File                                      | Role                                                    |
|--------------|-------------------------------------------|---------------------------------------------------------|
| Checkstyle   | `checkstyle/geojson4j_checks.xml`         | Build-time enforcement (`mvn verify`)                   |
| IntelliJ     | `intellij/intellij-java-google-style.xml` | Stock Google style (unchanged)                          |
| EditorConfig | `/.editorconfig`                          | IDE + editor overrides on top of the XML                |
| OpenRewrite  | `/rewrite.yml`                            | Auto-format during `mvn -Popen-rewrite process-sources` |

---

## Project-Specific Deviations from Google Java Style

### 1. Indentation — 4 spaces (Google default: 2)

| Property                  | Google default | geojson4j       |
|---------------------------|----------------|-----------------|
| `basicOffset`             | 2              | **4**           |
| `braceAdjustment`         | 2              | **0**           |
| `caseIndent`              | 2              | **4**           |
| `throwsIndent`            | 4              | 4 *(unchanged)* |
| `lineWrappingIndentation` | 4              | **8**           |
| `arrayInitIndent`         | 2              | **4**           |

**Why:** 4-space indentation is more readable for a domain library with deeply nested
generics, annotations, and sealed-class hierarchies.

**Where it is set:**

```xml
<!-- checkstyle/geojson4j_checks.xml -->
<module name="Indentation">
  <property name="basicOffset" value="4"/>
  <property name="braceAdjustment" value="0"/>
  <property name="caseIndent" value="4"/>
  <property name="throwsIndent" value="4"/>
  <property name="lineWrappingIndentation" value="8"/>
  <property name="arrayInitIndent" value="4"/>
</module>
```

```editorconfig
# .editorconfig
[*.java]
indent_size = 4
ij_java_continuation_indent_size = 8
```

```yaml
# rewrite.yml
- org.openrewrite.java.style.TabsAndIndentsStyle:
    indentSize: 4
    continuationIndent: 8
```

---

### 2. Line Length — 160 characters (Google default: 100)

**Why:** Modern wide-screen development, long generic type signatures, and fluent
assertion chains regularly exceed 100 characters without sacrificing readability.

**Where it is set:**

```xml
<!-- checkstyle/geojson4j_checks.xml -->
<module name="LineLength">
  <property name="max" value="160"/>
</module>
```

```editorconfig
# .editorconfig
[*.java]
max_line_length = 160
ij_java_right_margin = 160
```

---

### 3. Import Ordering — static imports first (Google default: static last)

The project uses `STATIC###THIRD_PARTY_PACKAGE` which means:

1. All `import static` statements, sorted alphabetically
2. One blank line
3. All regular imports in **one single alphabetical block** — no sub-grouping between
   `java.*`, `javax.*`, `com.*`, `org.*` etc.

**Why:** Consistent with the `$*, |, *` layout in `.editorconfig` and makes static
imports visually distinct from regular ones.

**Where it is set:**

```xml
<!-- checkstyle/geojson4j_checks.xml -->
<module name="CustomImportOrder">
  <property name="sortImportsInGroupAlphabetically" value="true"/>
  <property name="separateLineBetweenGroups" value="true"/>
  <property name="customImportOrderRules" value="STATIC###THIRD_PARTY_PACKAGE"/>
</module>
```

```editorconfig
# .editorconfig
ij_java_imports_layout = $*, |, *

ij_java_class_count_to_use_import_on_demand = 999

ij_java_names_count_to_use_import_on_demand = 999
```

```yaml
# rewrite.yml — ImportLayoutStyle consumed by OrderImports recipe
- org.openrewrite.java.style.ImportLayoutStyle:
    classCountToUseStarImport: 999
    nameCountToUseStarImport: 999
    layout:
      - "import 'static com.*'"
      - "import 'static java.*'"
      - "import 'static javax.*'"
      - "import 'static org.*'"
      - "import 'static all other imports'"
      - "<blank line>"
      - "import 'all other imports'"
```

---

### 4. Checkstyle Severity — `error` (Google default: `warning`)

Violations are treated as build errors; `mvn verify` fails on any violation.

```xml
<!-- checkstyle/geojson4j_checks.xml -->
<property name="severity" value="${org.checkstyle.google.severity}" default="error"/>
```

---

## Syncing with Upstream Google Checks

`geojson4j_checks.xml` is kept in sync with the canonical upstream file at
<https://github.com/checkstyle/checkstyle/blob/master/src/main/resources/google_checks.xml>.

To re-sync after a new Checkstyle release:

```bash
# 1. Pull the latest upstream
curl -s "https://raw.githubusercontent.com/checkstyle/checkstyle/master/src/main/resources/google_checks.xml" \
  -o /tmp/google_checks_latest.xml

# 2. Apply project overrides (script encodes exactly the four deviations above)
python3 config/checkstyle/apply_overrides.py

# 3. Verify the build still passes
mvn verify
```

---

## Javadoc Settings

The following Javadoc-related settings are aligned across Checkstyle and IntelliJ:

| Setting                                     | Value                                   | Checkstyle rule                                |
|---------------------------------------------|-----------------------------------------|------------------------------------------------|
| Align `@param` comments                     | off                                     | `JD_ALIGN_PARAM_COMMENTS = false`              |
| Align `@throws` comments                    | off                                     | `JD_ALIGN_EXCEPTION_COMMENTS = false`          |
| Insert `<p>` at blank lines                 | off                                     | `JavadocParagraph allowNewlineParagraph=false` |
| Keep empty `@param` / `@throws` / `@return` | off                                     | `NonEmptyAtclauseDescription`                  |
| Blank line before first block tag           | required                                | `RequireEmptyLineBeforeBlockTagGroup`          |
| Single-line Javadoc on one line             | required                                | `SingleLineJavadoc`                            |
| Tag order                                   | `@param, @return, @throws, @deprecated` | `AtclauseOrder`                                |

---

## File Reference

```
config/
├── README.md                              ← this file
├── checkstyle/
│   ├── geojson4j_checks.xml               ← upstream google_checks.xml + 4 overrides
│   ├── checkstyle-suppressions.xml        ← per-file/per-rule suppressions
│   └── google_checks.xml                  ← stock upstream (reference copy, not used by build)
└── intellij/
    └── intellij-java-google-style.xml     ← stock Google style (unchanged; overrides live in .editorconfig)
```

