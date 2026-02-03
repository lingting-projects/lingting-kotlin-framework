plugins {
    id("lingting_kmp")
    id("lingting_publish")
    alias(libs.plugins.kotlin.atomic)
}

kotlin {
    sourceSets.commonMain {
        dependencies {
            api(libs.kotlin.io.core)
            api(libs.kotlin.coroutines.core)
            api(libs.kotlin.datetime)
            api(libs.kotlin.logging)
            api(libs.kotlin.reflect)
        }
    }
}
