plugins {
    `java-library`
    `maven-publish`
    id("com.netflix.nebula.release")
}

group = "net.sghill.gradle"
description = "Reusable Gradle ComponentMetadataRules"

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
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            pom {
                url.set("https://github.com/sghill/gradle-rules")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("sghill")
                        name.set("Steve Hill")
                        email.set("sghill.dev@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/sghill/gradle-rules.git")
                    developerConnection.set("scm:git:git@github.com:sghill/gradle-rules.git")
                    url.set("https://github.com/sghill/gradle-rules")
                }
            }
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            artifactId = "gradle-rules"
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/sghill/gradle-rules")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
