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
                api(libs.kotlin.ktor.client)
                implementation(project(":framework-json"))
            }
        }

        commonTest {
            dependencies {
                api(project(":framework-crypto"))
                api(libs.kotlin.ktor.server)
                api(libs.okio)
            }
        }
    }
}
