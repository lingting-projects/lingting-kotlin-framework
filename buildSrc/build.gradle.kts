plugins {
    `kotlin-dsl`
    alias(libs.plugins.publish) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.library) apply false
}

fun convert(p: Provider<PluginDependency>): String {
    val it = p.get()
    return "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}"
}

dependencies {
    // 使用 get().let { ... } 将插件对象转为 "group:id:version" 字符串
    implementation(convert(libs.plugins.publish))
    implementation(convert(libs.plugins.kotlin.ksp))
    implementation(convert(libs.plugins.kotlin.multiplatform))
    implementation(convert(libs.plugins.android.library))
}
