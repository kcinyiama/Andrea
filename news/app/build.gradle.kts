import com.github.triplet.gradle.androidpublisher.ReleaseStatus
import java.util.Properties
import java.io.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
    id("com.github.triplet.play").version("3.4.0-agp7.0")
}

// Values passed as parameters from Bitbucket pipelines
val keystoreFile: String? by project
val keystorePassword: String? by project
val aliasKey: String? by project
val passwordKey: String? by project
val googlePlayKeyFile: String? by project
val isDevelopBranch: String? by project

// Versioning properties
val versionThreshold = 99
val defaultVersion = "0.0.0"
val appVersion = "app.version"
val gradleProps = "gradle.properties"

android {
    compileSdk = 30
    buildToolsVersion = "30.0.3"

    val parts = computeVersionParts()

    defaultConfig {
        applicationId = "com.andrea.rss"
        minSdk = 21
        targetSdk = 30
        versionName = parts.first
        versionCode = parts.second
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        getByName("debug") {
            keyAlias = "key0"
            keyPassword = "x*V#xE0l0&&p"
            storeFile = file("../keys/debug.keystore")
            storePassword = "x*V#xE0l0&&p"
        }
        create("release") {
            keyAlias = aliasKey
            keyPassword = passwordKey
            storeFile = file(keystoreFile ?: "oblivion")
            storePassword = keystorePassword
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            isDebuggable = false
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = true
        }
    }
    buildFeatures {
        dataBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.5.0")

    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.1")

    // Android UI
    implementation("androidx.fragment:fragment-ktx:1.3.6")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")

    // ViewModel and LiveData (arch components)
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    // Room for database
    implementation("androidx.room:room-ktx:2.3.0")
    implementation("androidx.room:room-runtime:2.3.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.1.0")

    kapt("androidx.room:room-compiler:2.3.0")

    // Workmanager
    implementation("androidx.work:work-runtime-ktx:2.7.0")
    androidTestImplementation("androidx.work:work-testing:2.7.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")

    // Retrofit for networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")

    // Glide for images
    implementation("com.github.bumptech.glide:glide:4.10.0")

    // HTML Parser
    implementation("org.jsoup:jsoup:1.13.1")



    // Coroutines testing
//    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.0")

    //  Architecture Components testing libraries
    testImplementation("androidx.arch.core:core-testing:2.1.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

fun computeVersionParts(): Pair<String, Int> {
    val parts = (project.properties[appVersion] ?: defaultVersion).toString().split(".")
    var major = parts[0].toInt()
    var minor = parts[1].toInt()
    var patch = parts[2].toInt()

    if (patch >= versionThreshold) {
        patch = 1
        if (minor >= versionThreshold) {
            minor = 1
            ++major
        } else {
            ++minor
        }
    } else {
        ++patch
    }
    val versionName = "$major.$minor.$patch"

    when (isDevelopBranch.toBoolean()) {
        true -> saveVersion(versionName)
    }

    return Pair(versionName, (major * 1000) + (minor * 100) + (patch * 10))
}

fun saveVersion(version: String) {
    val fis = FileInputStream(gradleProps)
    val props = Properties().apply { load(fis) }
    fis.close()

    val fos = FileOutputStream(gradleProps)
    props.setProperty(appVersion, version)
    props.store(fos, null)
    fos.close()
}

play {
    serviceAccountCredentials.set(file(googlePlayKeyFile ?: "oblivion"))
    defaultToAppBundles.set(true)
    track.set("internal")
    releaseStatus.set(ReleaseStatus.DRAFT) // TODO Remove when app is no longer in DRAFT state
}