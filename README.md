# gradle-rules
Reusable Gradle [Component Metadata Rules][rules].

[rules]: https://docs.gradle.org/current/userguide/component_metadata_rules.html

![GitHub Actions CI](https://github.com/sghill/gradle-rules/actions/workflows/ci.yaml/badge.svg)

## RemoveClassifierFromDependency

Gradle has a funny relationship with maven classifiers.

Consider this scenario:

1. A library depends on an old `snakeyaml` version with `<classifier>android</classifier>`
2. Another library depends on a more recent `snakeyaml`, which is no longer publishing the `android` classifier

Gradle conflict resolves to the highest version, but also wants the `android` classifier.
This fails, but only once you run a step that needs the jars downloaded.

```
Execution failed for task ':compileJava'.
    > Could not resolve all files for configuration ':compileClasspath'.
       > Could not find snakeyaml-1.30-android.jar (org.yaml:snakeyaml:1.30).
         Searched in the following locations:
             https://repo.maven.apache.org/maven2/org/yaml/snakeyaml/1.30/snakeyaml-1.30-android.jar
```

To correct this problem for all your projects, you could use this library as building blocks for your custom plugin.

```gradle
dependencies {
  components {
    withModule('com.github.javafaker:javafaker', net.sghill.gradle.rules.RemoveClassifierFromDependency) {
      params('org.yaml', 'snakeyaml', 'plugin removed/added to drop classifier')
    }
  }
  // other dependencies
}
```

The last parameter is a reason, which is shown when running [the `dependencyInsight` task][task].

```
$ ./gradlew dependencyInsight --dependency=org.yaml:snakeyaml

> Task :dependencyInsight
org.yaml:snakeyaml:1.30
  Variant compile:
    | Attribute Name                 | Provided | Requested    |
    |--------------------------------|----------|--------------|
    | org.gradle.status              | release  |              |
    | org.gradle.category            | library  | library      |
    | org.gradle.libraryelements     | jar      | classes      |
    | org.gradle.usage               | java-api | java-api     |
    | org.gradle.dependency.bundling |          | external     |
    | org.gradle.jvm.environment     |          | standard-jvm |
    | org.gradle.jvm.version         |          | 8            |
   Selection reasons:
      - Was requested: plugin removed/added to drop classifier     <-- this is our reason, many can be shown here

org.yaml:snakeyaml:1.30
\--- compileClasspath

org.yaml:snakeyaml -> 1.30
\--- com.github.javafaker:javafaker:1.0.2
     \--- compileClasspath
```

[task]: https://docs.gradle.org/current/userguide/viewing_debugging_dependencies.html#dependency_insights
