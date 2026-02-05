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
                api(project(":framework-http"))
                implementation(project(":framework-crypto"))
            }
        }
    }
}
