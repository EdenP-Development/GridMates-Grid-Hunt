plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.compose")
}
android {
    namespace = "com.playereden.gridmates.gridhunt"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.playereden.gridmates.gridhunt"
        minSdk = 26
        targetSdk = 35
        // GH Actions run numbers increase for each CI build: versionCode must
        // rise for Android to allow upgrades without uninstalling.
        versionCode = 10000 + (System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 0)
        versionName = "0.3.0-gh02c"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    // A deliberately PUBLIC, disposable debug-only key. NEVER use this for release
    // or sensitive apps. QA package has no network/location permissions.
    signingConfigs {
        create("publicQa") {
            storeFile = file("../.github/qa-signing/gridhunt-public-qa.p12")
            storePassword = "gridhunt-qa-public"
            keyAlias = "gridhunt-qa"
            keyPassword = "gridhunt-qa-public"
            storeType = "pkcs12"
        }
    }
    buildTypes {
        debug {
            applicationIdSuffix = ".qa"
            versionNameSuffix = "-qa.${System.getenv("GITHUB_RUN_NUMBER") ?: "local"}"
            signingConfig = signingConfigs.getByName("publicQa")
        }
        release { isMinifyEnabled = false }
        create("beta") {
            initWith(getByName("release"))
            applicationIdSuffix = ".beta"
            versionNameSuffix = "-beta.${System.getenv("GITHUB_RUN_NUMBER") ?: "local"}"
            matchingFallbacks += listOf("release")
            // Intentionally unsigned: sign the reviewed APK in a separate trusted workflow.
            signingConfig = null
        }
    }
    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}
dependencies {
    implementation(project(":core"))
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation(platform("androidx.compose:compose-bom:2025.04.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.04.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
}
