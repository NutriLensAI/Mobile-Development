plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.kotlin.parcelize)
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
        // Enable MultiDex
        multiDexEnabled = true
        buildConfigField(
            "String",
            "ENDPOINT",
            "\"https://nutrilens-capstone.et.r.appspot.com/api/\""
        )
        buildConfigField(
            "String",
            "PREDICT_API_ENDPOINT",
            "\"https://ml-api-sla6c4qvsq-et.a.run.app/\""
        )

        buildConfigField(
            "String",
            "API_BASE_URL",
            "\"https://nutrilensai.github.io/datadummy/datarecipe.json\""
        )

        buildConfigField(
            "String",
            "BASE_URL",
            "\"https://ml-api-sla6c4qvsq-et.a.run.app/\""
        )
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
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
        buildConfig = true
    }
    lint {
        checkAllWarnings = true
        warningsAsErrors = false
        abortOnError = true
    }
    buildToolsVersion = "34.0.0"
    ndkVersion = "27.0.11718014 rc1"
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
    implementation(libs.play.services.location)
    implementation(libs.androidx.fragment.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //datastore
    implementation(libs.datastore.preferences)

    //lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    //glide
    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    //retrofit api
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    //cardview
    implementation(libs.androidx.cardview)

    //viewpager
    implementation(libs.androidx.viewpager2)

    //CameraX
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)

    //android Image Cropper
    implementation(libs.android.image.cropper)

    //Step Counter
    implementation(libs.play.services.fitness)
    implementation(libs.androidx.work.runtime.ktx)

    //room
    implementation(libs.androidx.room.ktx)
    ksp(libs.room.compiler)

    //chart
    implementation (libs.core)
    implementation (libs.viz)
    coreLibraryDesugaring (libs.desugar.jdk.libs)
}