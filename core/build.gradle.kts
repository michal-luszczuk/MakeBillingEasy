plugins {
    id("makebillingeasy.android.library")
}

android {
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }
    namespace = "com.luszczuk.makebillingeasy.core"

}

dependencies {
    implementation(libs.billingclient.ktx)
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.core)
    implementation(libs.lifecycle.common)
    implementation(libs.lifecycle.process)
    implementation(libs.lifecycle.runtime)
}