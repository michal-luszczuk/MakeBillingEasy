plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.vanniktech.mavenpublish)
    alias(libs.plugins.dokka)
    alias(libs.plugins.junit5) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.benmanes.versions)
}

tasks.named<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>("dependencyUpdates")
    .configure {
        rejectVersionIf {
            isNonStable(candidate.version) && !isNonStable(currentVersion)
        }
    }

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}


subprojects {

    plugins.withType(com.android.build.gradle.LibraryPlugin::class) {
        apply(plugin = "de.mannodermaus.android-junit5")
        androidLibrary {
            dependencies {
                "testImplementation"(libs.mockk)

                "testImplementation"(libs.junit.jupiter.api)
                "testRuntimeOnly"(libs.junit.jupiter.engine)
                "testImplementation"(libs.turbine)
                "testImplementation"(libs.coroutines.test)
                "testImplementation"(libs.junit)
                "testRuntimeOnly"(libs.junit.vintage.engine)
            }
        }
    }
}

fun Project.androidLibrary(configure: com.android.build.gradle.LibraryExtension.() -> Unit) =
    extensions.configure(com.android.build.gradle.LibraryExtension::class, configure)