plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.capstone.mobiledevelopment.nutrilens"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.capstone.mobiledevelopment.nutrilens"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "ENDPOINT", "\"https://story-api.dicoding.dev/v1/\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig= true
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
            animationsDisabled = true

        }
    }
    lint {
        checkAllWarnings = true
        warningsAsErrors = false
        abortOnError = true
    }
}

dependencies {
    //default
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //room
    implementation(libs.androidx.room.ktx)
    ksp(libs.room.compiler)

    //datastore
    implementation(libs.datastore.preferences)

    //lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    //glide
    implementation(libs.glide)

    //retrofit api
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    //paging
    implementation(libs.androidx.paging.runtime.ktx)
    implementation (libs.androidx.room.paging)

    //testing
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)

    //special instrumentation testing
    androidTestImplementation(libs.androidx.core.testing) // InstantTaskExecutorRule
    androidTestImplementation(libs.kotlinx.coroutines.test) //TestDispatcher

}