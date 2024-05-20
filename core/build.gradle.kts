plugins {
    id("com.android.library")
}

android {
    compileSdk = Config.compileSdk

    defaultConfig {
        minSdk = Config.minSdk
        lint.targetSdk = Config.targetSdk
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }

    compileOptions {
        sourceCompatibility(Config.javaVersion)
        targetCompatibility(Config.javaVersion)
    }

    kotlin {
        jvmToolchain(Config.javaVersionNumber)
    }

    kotlinOptions {
        jvmTarget = Config.javaVersionNumber.toString()
    }

    namespace = "com.luszczuk.makebillingeasy.core"
}

dependencies {
    implementation(Libs.kotlinStdLib)
    implementation(Libs.billingLib)
    implementation(Libs.coroutinesAndroid)
    implementation(Libs.coroutinesCore)
    implementation(Libs.lifecycleCommon)
    implementation(Libs.lifecycleProcess)
    implementation(Libs.lifecycleRuntime)
}