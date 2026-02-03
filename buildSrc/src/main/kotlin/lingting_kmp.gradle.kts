import gradle.kotlin.dsl.accessors._94a8446e5dc95e7c37c14d903979c335.implementation
import org.gradle.kotlin.dsl.assign
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("signing")
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
}

kotlin {
    compilerOptions {
        optIn.addAll(
            "kotlinx.coroutines.DelicateCoroutinesApi",
            "kotlin.time.ExperimentalTime",
            "kotlinx.coroutines.ExperimentalCoroutinesApi",
            "kotlinx.serialization.InternalSerializationApi",
            "io.ktor.utils.io.InternalAPI",
        )
    }

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    macosX64()
    macosArm64()
    iosArm64()
    iosSimulatorArm64()

    jvm {

    }

    js {
        browser()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    targets.all {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }

    sourceSets {

        commonMain.dependencies {
            // common
        }
        commonTest.dependencies {
            // test
        }

        val commonMain by getting

        val jvmMain by getting {
            dependsOn(commonMain)
        }

        val nonJvmMain by creating {
            dependsOn(commonMain)
        }

        val androidMain by getting {
            // dependsOn(nonJvmMain)
        }

        val webMain by creating {
            dependsOn(nonJvmMain)
        }
        val jsMain by getting { dependsOn(webMain) }
        val wasmJsMain by getting { dependsOn(webMain) }

        val nativeMain by creating {
            dependsOn(nonJvmMain)
        }

        val appleMain by creating {
            dependsOn(nativeMain)
        }

        val macosMain by creating {
            dependsOn(appleMain)
        }
        val macosX64Main by getting { dependsOn(macosMain) }
        val macosArm64Main by getting { dependsOn(macosMain) }

        val iosMain by creating {
            dependsOn(appleMain)
        }
        val iosArm64Main by getting { dependsOn(iosMain) }
        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }


        val commonTest by getting

        val jvmTest by getting {
            dependsOn(commonTest)
        }

        val nonJvmTest by creating {
            dependsOn(commonTest)
        }

        val androidUnitTest by getting {
            dependsOn(nonJvmTest)
        }

        val webTest by creating {
            dependsOn(nonJvmTest)
        }
        val jsTest by getting { dependsOn(webTest) }
        val wasmJsTest by getting { dependsOn(webTest) }

        val nativeTest by creating {
            dependsOn(nonJvmTest)
        }

        val appleTest by creating {
            dependsOn(nativeTest)
        }

        val macosTest by creating {
            dependsOn(appleTest)
        }
        val macosX64Test by getting { dependsOn(macosTest) }
        val macosArm64Test by getting { dependsOn(macosTest) }

        val iosTest by creating {
            dependsOn(appleTest)
        }
        val iosArm64Test by getting { dependsOn(iosTest) }
        val iosSimulatorArm64Test by getting { dependsOn(iosTest) }
    }

}

dependencies {
    "coreLibraryDesugaring"("com.android.tools:desugar_jdk_libs:2.1.5")
}

android {
    namespace = project.group.toString()
    compileSdk = 34
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    defaultConfig {
        minSdk = 21
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }

}
