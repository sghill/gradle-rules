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

class RemoveClassifierFromDependencyTest {
    @Test
    void shouldAllowResolvingJavaFaker(@TempDir Path root) throws IOException {
        Files.write(root.resolve("build.gradle"), asList(
                "plugins { ",
                "  id 'java-library'",
                "  id 'net.sghill.gradle.rules.acceptance'",
                "}",
                "repositories { mavenCentral() }",
                "dependencies {",
                "  implementation 'com.github.javafaker:javafaker:1.0.2'",
                "  implementation 'org.yaml:snakeyaml:1.30'",
                "}"
        ), UTF_8);
        Path classFile = root.resolve("src/main/java/A.java");
        Files.createDirectories(classFile.getParent());
        Files.write(classFile, singletonList("class A {}"), UTF_8);

        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(root.toFile())
                .withArguments("classes", "dependencyInsight", "--dependency=snakeyaml")
                .build();

        // failure without the rule:
        //    Execution failed for task ':compileJava'.
        //    > Could not resolve all files for configuration ':compileClasspath'.
        //       > Could not find snakeyaml-1.30-android.jar (org.yaml:snakeyaml:1.30).
        //         Searched in the following locations:
        //             https://repo.maven.apache.org/maven2/org/yaml/snakeyaml/1.30/snakeyaml-1.30-android.jar

        BuildTask classes = result.task(":classes");
        assertThat(classes).isNotNull();
        assertThat(classes.getOutcome()).isEqualTo(TaskOutcome.SUCCESS);

        String actual = result.getOutput();
        assertThat(actual).contains("- Was requested: plugin removed/added to drop classifier");
    }
}
