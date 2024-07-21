import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.luszczuk.makebillingeasy.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.dokka.gradlePlugin)
    compileOnly(libs.mavenpublish.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "makebillingeasy.android.application"
            implementationClass = "AndroidAppConventionPlugin"
        }
        register("androidLibrary") {
            id = "makebillingeasy.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
    }
}