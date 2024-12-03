plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("io.gitlab.arturbosch.detekt") version "1.19.0"
}

detekt {
    toolVersion = "1.19.0"
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
}

android {
    namespace = "com.example.pawtrack"
    compileSdk = 34

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        applicationId = "com.example.pawtrack"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            enableAndroidTestCoverage = true
            enableUnitTestCoverage = true
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}


/*

Coverage Report:

Run instrumented tests: gradlew connectedAndroidTest
Run unit tests: gradlew testDebugUnitTest

Generate coverage report: gradlew jacocoCombinedTestReport
Report in: APP/PawTrack/app/build/reports/

 */

dependencies {
    implementation("io.gitlab.arturbosch.detekt:detekt-api:1.19.0")
    implementation ("androidx.appcompat:appcompat:1.3.1")
    implementation ("com.google.code.gson:gson:2.8.8")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("org.osmdroid:osmdroid-android:6.1.1")
    implementation ("com.google.android.gms:play-services-location:21.2.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.mikhaellopez:circularprogressbar:3.1.0")
    implementation("commons-codec:commons-codec:1.15")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.security:security-crypto:1.0.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("androidx.security:security-crypto-ktx:1.1.0-alpha06")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("androidx.test:core-ktx:1.6.1")
    implementation("androidx.test.ext:junit-ktx:1.2.1")
    implementation("androidx.test:rules:1.6.1")
    implementation("androidx.test.espresso:espresso-intents:3.6.1")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("androidx.test.espresso:espresso-contrib:3.6.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    // Testing dependencies
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.23")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("io.mockk:mockk:1.13.2")
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.5.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    testImplementation("org.robolectric:robolectric:4.10.3")
    androidTestImplementation("com.android.support.test:rules:1.0.2")
    androidTestImplementation("com.android.support.test:runner:1.0.2")
    testImplementation ("com.squareup.okhttp3:mockwebserver:4.9.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:rules:1.6.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    implementation(kotlin("test"))
}

tasks.withType<Test> {
    useJUnit()
}