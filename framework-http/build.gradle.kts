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
                api(libs.kotlin.ktor.http)
                implementation(libs.kotlin.ktor.client)
                implementation(libs.kotlin.serialization.core)
                implementation(libs.kotlin.serialization.json)
            }
        }

        commonTest {
            dependencies {
                api(libs.kotlin.ktor.server)
            }
        }
    }
}
