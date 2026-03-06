plugins {
    id("lingting_kmp")
    id("lingting_publish")
    alias(libs.plugins.kotlin.serialization)
}

evaluationDependsOn(":framework-aws")

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":framework-aws"))
                implementation(project(":framework-crypto"))
                implementation(project(":framework-json"))
                implementation(project(":framework-xml"))
            }
        }
        commonTest {
            val awsProjectDir = project(":framework-aws").projectDir
            kotlin.srcDir("$awsProjectDir/src/commonTest/kotlin")
        }
    }
}
