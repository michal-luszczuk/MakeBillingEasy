import com.android.build.gradle.LibraryExtension
import com.luszczuk.makebillingeasy.buildlogic.configureKotlinAndroid
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.withType

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.dokka")
                apply("com.vanniktech.maven.publish")
            }

            tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
                outputDirectory.set(buildDir.resolve("javadoc"))
            }

            (project.extensions.getByName("mavenPublishing") as MavenPublishBaseExtension).apply {
                publishToMavenCentral(host = SonatypeHost.S01)
                signAllPublications()
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 34
            }
            dependencies {
                add("testImplementation", kotlin("test"))
            }
        }
    }
}