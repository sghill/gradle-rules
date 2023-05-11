plugins {
    `java-library`
}

group = "net.sghill"

dependencies {
    compileOnly(libs.gradle.api)
}

dependencyLocking {
    lockAllConfigurations()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}
