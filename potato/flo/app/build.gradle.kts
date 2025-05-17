plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")
    id("com.google.gms.google-services") // ✅ 여기서 plugin 적용
}

android {
    namespace = "com.cookandroid.flo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.cookandroid.flo"
        minSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        //compose = true
        viewBinding = true //뷰바인딩을 사용하기 위한 이유.
        //findviewbyid보다는 뷰 바인딩이 좋기 때문.
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation("com.google.code.gson:gson:2.10.1") //Gson 추가
    implementation("androidx.viewpager2:viewpager2:1.0.0") //뷰페이저2추가함.
    implementation("com.google.android.material:material:1.10.0") //하단 네비게이션
    implementation("me.relex:circleindicator:2.1.6'") //신디케이션
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") //콘스트레이트.. 추가
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.circleindicator)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //roomDB
    implementation("androidx.room:room-runtime:2.7.1")
    implementation("androidx.room:room-ktx:2.7.1")
    kapt("androidx.room:room-compiler:2.7.1")

    // Firebase Realtime Database
    implementation("com.google.firebase:firebase-database-ktx:20.3.0")

// Firebase Storage (이미지 저장할 경우)
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")

// Firebase Core (Analytics 포함 — 필수는 아님)
    implementation("com.google.firebase:firebase-analytics-ktx:21.5.0")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
}