import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "ru.bashcony.followy.ui"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
}

//val artifactName = "followy-ui"
//val artifactGroup = "ru.bashcony"
//val artifactVersion = "0.0.1"
//
//publishing {
//    publications {
//        create<MavenPublication>("lib") {
//            run {
//                groupId = artifactGroup
//                artifactId = artifactName
//                version = artifactVersion
////                from(components["java"])
////                artifact(dokkaJar)
//                artifact("$buildDir/outputs/aar/FollowyUI-release.aar")
//            }
//        }
//    }
//
//    repositories {
//        mavenLocal()
//    }
//}
//
//tasks.register("universalPublish") {
//    dependsOn("assembleRelease")
//    dependsOn("publish")
//}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}