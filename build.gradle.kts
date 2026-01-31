import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val projectGroup = "live.lingting.framework"
val projectVersion = "2026.01.30-Beta-1"

val catalogLibs = libs
val allProjects = subprojects.filter { it.name != "buildSrc" && it.name.startsWith("framework-") }
// 使用的java版本
val javaVersion = JavaVersion.VERSION_21
val targetVersion = JavaVersion.VERSION_21
// 字符集
val targetEncoding = "UTF-8"
val ideaLanguageLevel = IdeaLanguageLevel(targetVersion)

plugins {
    id("idea")
}

idea {
    project {
        languageLevel = ideaLanguageLevel
        targetBytecodeVersion = javaVersion
    }
}

allprojects {
    group = projectGroup
    version = projectVersion

    apply {
        plugin("idea")
    }

    idea {
        module {
            languageLevel = ideaLanguageLevel
            targetBytecodeVersion = javaVersion
        }
    }

}

configure(allProjects) {

    fun configureKotlin() {
        tasks.withType<KotlinCompile> {
            compilerOptions {
                javaParameters = true
                jvmTarget = JvmTarget.fromTarget(targetVersion.majorVersion)
                freeCompilerArgs.addAll(
                    "-Xjvm-default=all",
                    "-Xjsr305=strict",
                )
                optIn.addAll(
                    "kotlinx.coroutines.DelicateCoroutinesApi",
                    "kotlin.time.ExperimentalTime",
                )
            }
        }

        configure<KotlinProjectExtension> {
            jvmToolchain(javaVersion.majorVersion.toInt())
        }
    }

    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        configureKotlin()

        configure<KotlinMultiplatformExtension> {
            sourceSets.commonMain.dependencies {
                implementation(catalogLibs.bundles.implementation)
            }
            sourceSets.commonTest.dependencies {
                implementation(catalogLibs.bundles.test)
            }
        }
    }

}
