import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.movielist"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.movielist"
        minSdk = 24
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    // Må bygge en ny google-services.json fil, som inneholder api-nøkkelen.
    // det vil si at google-services.json IKKE skal med i git, mens google-services-template.json
    // må være med i git.
    tasks.register("generateGoogleServicesJson") {
        doLast {
            // Lese API-nøkkel fra keys.properties
            val propertiesFile = file("../keys.properties")
            val properties = Properties().apply { load(propertiesFile.inputStream()) }
            val apiKey = properties.getProperty("FIREBASE_API_KEY") ?: "default_value"

            // Les malen
            val templateFile = file("google-services-template.json")
            var jsonText = templateFile.readText(Charsets.UTF_8)

            // Erstatt plassen for API-nøkkelen med den faktiske nøkkelen
            jsonText = jsonText.replace("REPLACE_WITH_API_KEY", apiKey)

            // Lagre som google-services.json
            val outputFile = file("google-services.json")
            outputFile.writeText(jsonText, Charsets.UTF_8)
        }
    }

    // Kjør oppgaven før preBuild
    tasks.preBuild {
        dependsOn("generateGoogleServicesJson")
    }
}

dependencies {

    implementation(("androidx.compose.ui:ui-text-google-fonts:1.7.2"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.coil.compose.v210)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")  // Legg til denne linjen for Moshi Kotlin Adapter
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.0")

    implementation("com.squareup.moshi:moshi-adapters:1.8.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0") // Kotlin adapter for Moshi

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.okhttp)

    implementation("io.coil-kt:coil-compose:2.2.2")

    //Camera
    implementation (libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)
    implementation ("com.google.accompanist:accompanist-permissions:0.25.1")
    implementation("androidx.exifinterface:exifinterface:1.3.6")

    //Phone Image
    implementation(libs.coil.compose)


    // mulig disse 3 kan fjernes
    implementation ("com.google.android.gms:play-services-base:18.1.0")
    implementation ("com.google.android.gms:play-services-base:18.5.0")
    implementation ("com.google.android.gms:play-services-auth:21.2.0")
    implementation ("com.google.android.gms:play-services-maps:19.0.0")

    implementation(("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.0"))
    implementation(("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.0"))

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))

    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")

    // Declare the dependency for the Cloud Firestore library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-firestore")

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")

    implementation(libs.coil.compose)

    //Youtube embed
    implementation ("com.pierfrancescosoffritti.androidyoutubeplayer:chromecast-sender:0.30")

}