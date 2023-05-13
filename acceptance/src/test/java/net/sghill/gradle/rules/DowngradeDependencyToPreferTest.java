package net.sghill.gradle.rules;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class DowngradeDependencyToPreferTest {
    @Test
    void shouldDowngradeRetrofitOpinionToTakeSpringOpinion(@TempDir Path root) throws IOException {
        Files.write(root.resolve("build.gradle"), asList(
                "plugins { ",
                "  id 'java-library'",
                "  id 'net.sghill.gradle.rules.acceptance'",
                "}",
                "repositories { mavenCentral() }",
                "dependencies {",
                "  implementation 'com.squareup.retrofit2:converter-jackson:2.9.0'", // brings in jackson-databind:2.10.1
                "  implementation 'org.springframework.boot:spring-boot-starter-json:2.0.0.RELEASE'", // brings in 2.9.4
                "}"
        ), UTF_8);

        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(root.toFile())
                .withArguments("dependencyInsight", "--dependency=jackson-databind")
                .build();

        // failure without the rule:
        //    Execution failed for task ':compileJava'.
        //    > Could not resolve all files for configuration ':compileClasspath'.
        //       > Could not find snakeyaml-1.30-android.jar (org.yaml:snakeyaml:1.30).
        //         Searched in the following locations:
        //             https://repo.maven.apache.org/maven2/org/yaml/snakeyaml/1.30/snakeyaml-1.30-android.jar

        String actual = result.getOutput();
        assertThat(actual)
                .contains("- Was requested: downgraded to prefer by plugin")
                .contains("com.fasterxml.jackson.core:jackson-databind:{prefer 2.10.1} -> 2.9.4");
    }
}
