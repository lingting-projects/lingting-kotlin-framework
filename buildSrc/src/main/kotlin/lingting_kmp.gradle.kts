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
            "kotlinx.serialization.ExperimentalSerializationApi",
            "kotlin.contracts.ExperimentalContracts",
            "kotlin.uuid.ExperimentalUuidApi",
            "kotlin.concurrent.atomics.ExperimentalAtomicApi",
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

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
        binaries.executable()
    }

    targets.all {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        val nonJvmMain by creating {
            dependsOn(commonMain.get())
        }

        val nonJvmTargetSourceSets = listOf("nativeMain", "webMain")

        configure(nonJvmTargetSourceSets.mapNotNull { findByName(it) }) {
            dependsOn(nonJvmMain)
        }

        val jvmCommonMain by creating {
            dependsOn(commonMain.get())
        }

        val jvmTargetSourceSets = listOf("jvmMain","androidMain")

        configure(jvmTargetSourceSets.mapNotNull { findByName(it) }) {
            dependsOn(jvmCommonMain)
        }
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
