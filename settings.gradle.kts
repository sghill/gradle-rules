rootProject.name = "gradle-rules"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            val junit5 = version("junit5") {
                strictly("[5.8.0, 6.0.0[")
            }
            library("gradle-api", "dev.gradleplugins:gradle-api:4.9")
            library("assertj-core", "org.assertj", "assertj-core").version {
                strictly("[3.0.0, 4.0.0[")
            }
            library("junit5-api", "org.junit.jupiter", "junit-jupiter-api").versionRef(junit5)
            library("junit5-engine", "org.junit.jupiter", "junit-jupiter-engine").versionRef(junit5)
        }
    }
}

include("acceptance", "core")
