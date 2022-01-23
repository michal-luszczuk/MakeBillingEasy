import org.gradle.api.JavaVersion

object Config {
    const val minSdk = 19
    const val compileSdk = 31
    const val targetSdk = 31
    const val buildTools = "31.0.0"
    val javaVersion = JavaVersion.VERSION_1_8
}


object Versions {
    const val junit = "4.13.2"
    const val mockk = "1.12.2"
    const val kotlinVersion = "1.6.10"
    const val robolectric = "4.7.3"
    const val kluent = "1.68"
    const val dokka = "1.6.10"
    const val dagger = "2.40.5"
    const val coroutines = "1.6.0"
    const val lifecycle = "2.4.0"
    const val billingLib = "4.0.0"
}

object Libs {
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinVersion}"
    const val billingLib = "com.android.billingclient:billing-ktx:${Versions.billingLib}"

    const val junit = "junit:junit:${Versions.junit}"
    const val mockk = "io.mockk:mockk:${Versions.mockk}"
    const val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"

    const val kluent = "org.amshove.kluent:kluent:${Versions.kluent}"
    const val kluentAndroid = "org.amshove.kluent:kluent-android:${Versions.kluent}"

    const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"

    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.dagger}"
    const val hiltCompiler = "com.google.dagger:hilt-android-compiler:${Versions.dagger}"

    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"

    const val lifecycleCommon = "androidx.lifecycle:lifecycle-common:${Versions.lifecycle}"
    const val lifecycleProcess = "androidx.lifecycle:lifecycle-process:${Versions.lifecycle}"
    const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"
    const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"


}