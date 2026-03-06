plugins {
    id("lingting_kmp")
    id("lingting_publish")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlin.ktor.server)
            }
        }
    }
}
