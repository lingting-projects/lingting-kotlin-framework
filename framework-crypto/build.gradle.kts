plugins {
    id("lingting_kmp")
    id("lingting_publish")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":framework-core"))
            }
        }
        webMain {
            dependencies {
                implementation(libs.kotlin.crypto)
            }
        }
    }
}
