plugins {
    id("com.android.library")
}

android {
    compileSdk = Config.compileSdk
    buildToolsVersion = Config.buildTools

    defaultConfig {
        minSdk = Config.minSdk
        targetSdk = Config.targetSdk
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }

}

dependencies {
    implementation(Libs.kotlinStdLib)
    implementation("com.android.billingclient:billing-ktx:4.0.0")
    implementation(Libs.coroutinesAndroid)
    implementation(Libs.coroutinesCore)
    implementation(Libs.lifecycleCommon)
    implementation(Libs.lifecycleProcess)
    implementation(Libs.lifecycleRuntime)
}