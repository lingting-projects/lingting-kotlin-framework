plugins {
    id("lingting_kmp")
    id("lingting_publish")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":framework-core"))
                api(libs.kotlin.ktor.client)
                api(libs.kotlin.serialization.core)
                api(libs.kotlin.serialization.json)
            }
        }

        commonTest {
            dependencies {
                api(libs.kotlin.ktor.server)
            }
        }
    }
}
