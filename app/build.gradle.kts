plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.windriver.pcgate"
    compileSdk = 36

    packaging {
        resources {
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/INDEX.LIST"
            excludes += "mozilla/public-suffix-list.txt"
            excludes += "dump_syms/linux/dump_syms.bin"
            excludes += "/META-INF/gradle/incremental.annotation.processors"
        }
    }
    defaultConfig {
        applicationId = "com.windriver.pcgate"
        minSdk = 35
        //noinspection OldTargetApi
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
    }
    buildToolsVersion = "35.0.0"
    ndkVersion = "29.0.13599879 rc2"
}

dependencies {

    // Retrofit for networking
    implementation(libs.retrofit) // Use the latest version
    implementation(libs.converter.gson) // Or another converter like Moshi

    // OkHttp (usually included by Retrofit, but good to be aware of)
    implementation(libs.okhttp) // Use the latest version
    implementation(libs.logging.interceptor) // For logging requests/responses (optional, for debugging)
    implementation(libs.google.genai)
    // Kotlin Coroutines for asynchronous operations
    implementation(libs.kotlinx.coroutines.android) // Use the latest version
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.mediarouter)
    implementation(libs.recyclerview)
    implementation(libs.firebase.database)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.annotation)
    implementation(libs.firebase.auth)
    implementation(libs.activity)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.ai)
    implementation(libs.glide)
    annotationProcessor(libs.compiler)
    implementation(libs.reactive.streams)
    implementation(libs.lombok)
    annotationProcessor(libs.lombok.v11832)
}

// Enable annotation processing
tasks.withType<JavaCompile> {
    options.annotationProcessorPath = configurations.annotationProcessor.get()
}