plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.roosoars.taskflow"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.roosoars.taskflow"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
}

dependencies {

    implementation(libs.appcompat.v142)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material.v161)

    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    implementation(libs.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.lifecycle.common.java8)

    implementation(libs.room.runtime)
    implementation(libs.androidx.activity)
    annotationProcessor(libs.room.compiler)

    implementation(libs.dagger)
    annotationProcessor(libs.dagger.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.v113)
    androidTestImplementation(libs.espresso.core.v340)

    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}