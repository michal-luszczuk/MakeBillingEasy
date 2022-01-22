import com.vanniktech.maven.publish.MavenPublishPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

buildscript {
    repositories {
        google()

    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:${Versions.dokka}")
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.18.0")
        classpath("de.mannodermaus.gradle.plugins:android-junit5:1.8.2.0")
    }
}

plugins {
    id("com.github.ben-manes.versions") version "0.41.0"
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.named<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>("dependencyUpdates")
    .configure {
        rejectVersionIf {
            isNonStable(candidate.version) && !isNonStable(currentVersion)
        }
    }

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}


subprojects {
    apply(plugin = "kotlin-android")
    val kotlin = project.extensions.getByName("kotlin") as KotlinAndroidProjectExtension
    kotlin.sourceSets.all {
        languageSettings {
            languageVersion = "1.6"
            progressiveMode = true
        }
    }

    pluginManager.withPlugin("com.android.library") {
        apply(plugin = "org.jetbrains.dokka")

        tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
            outputDirectory.set(buildDir.resolve("javadoc"))
        }

        apply(plugin = "com.vanniktech.maven.publish")
        (project.extensions.getByName("mavenPublish") as MavenPublishPluginExtension).apply {
            sonatypeHost = com.vanniktech.maven.publish.SonatypeHost.S01
        }
    }

    plugins.withType(com.android.build.gradle.LibraryPlugin::class) {
        apply(plugin = "de.mannodermaus.android-junit5")
        androidLibrary {
            dependencies {
                "testImplementation"(Libs.mockk)

                "testImplementation"("org.junit.jupiter:junit-jupiter-api:5.8.2")
                "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:5.8.2")
                "testImplementation"("app.cash.turbine:turbine:0.7.0")
                "testImplementation"(Libs.coroutinesTest)
                "testImplementation"("junit:junit:4.13.2")
                "testRuntimeOnly"("org.junit.vintage:junit-vintage-engine:5.8.2")
            }
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

fun Project.androidLibrary(configure: com.android.build.gradle.LibraryExtension.() -> Unit) =
    extensions.configure(com.android.build.gradle.LibraryExtension::class, configure)