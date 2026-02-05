plugins {
    id("lingting_kmp")
    id("lingting_publish")
}

val version = libs.versions.kotlin.crypto.get()
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":framework-core"))
                implementation("org.kotlincrypto.hash:sha1:$version")
                implementation("org.kotlincrypto.hash:sha2:$version")
                implementation("org.kotlincrypto.hash:md:$version")
                implementation("org.kotlincrypto.macs:hmac-sha2:$version")
            }
        }
    }
}
