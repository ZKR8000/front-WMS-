plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "com.tonentreprise.wms"
    compileSdk = 34 // ✅ Version stable

    defaultConfig {
        applicationId = "com.tonentreprise.wms"
        minSdk = 26
        targetSdk = 34
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

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11" // ✅ Compatible avec Kotlin 1.9.23
    }
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    // ✅ Core AndroidX
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("androidx.camera:camera-core:1.3.0")
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")


    // ✅ Lifecycle & ViewModel (Correction pour LifecycleOwner)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.8.3") // ✅ Obligatoire pour LifecycleOwner
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.3")

    // ✅ Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3:1.2.0") // Dernière version stable
    implementation ("androidx.compose.runtime:runtime-livedata:1.5.0") // Vérifie que c'est bien ajouté
    implementation("androidx.compose.material:material-icons-extended:1.5.11")


    // ✅ Navigation Compose (Correction de l'erreur)
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("androidx.navigation:navigation-runtime-ktx:2.7.5") // ✅ Corrigé

    // ✅ Compose Compiler (Optimisation des performances)
    implementation("androidx.compose.compiler:compiler:1.5.11")

    // ✅ Tests
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.01.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

// ✅ Correction des conflits de dépendances Kotlin et AndroidX
configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            useVersion("1.9.23") // ✅ Force la bonne version de Kotlin
        }
        if (requested.group == "androidx.lifecycle") {
            useVersion("2.8.3") // ✅ Corrige LifecycleOwner
        }
        if (requested.group == "androidx.navigation") {
            useVersion("2.7.5") // ✅ Corrige la navigation
        }
    }
}
