plugins {
    `java-gradle-plugin`
}

dependencies {
    implementation(project(":core"))
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit5.api)
    testRuntimeOnly(libs.junit5.engine)
}

dependencyLocking {
    lockAllConfigurations()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

gradlePlugin {
    plugins {
        create("acceptance") {
            id = "net.sghill.gradle.rules.acceptance"
            implementationClass = "net.sghill.gradle.rules.acceptance.AcceptancePlugin"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
