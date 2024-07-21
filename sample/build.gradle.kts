plugins {
    id("makebillingeasy.android.application")
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    defaultConfig {
        applicationId = "com.luszczuk.makebillingseasy.sample"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    namespace = "com.example.myapplication"
}

dependencies {

    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.billingclient.ktx)
    implementation(libs.lifecycle.common)
    implementation(libs.lifecycle.process)
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.viewmodel)

    implementation(project(":core"))
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.core)

    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit.ktx)
    androidTestImplementation(libs.androidx.espresso.core)
}