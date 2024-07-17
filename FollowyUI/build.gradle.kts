import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
//    id("maven-publish")
    id("com.vanniktech.maven.publish")
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

/**
 * ./gradlew publishAndReleaseToMavenCentral --no-configuration-cache
 */
mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    configure(
        AndroidSingleVariantLibrary(
            sourcesJar = true,
            publishJavadocJar = true,
            variant = "release"
        )
    )

    coordinates("ru.bashcony", "followy-ui", "0.2.0")

    pom {
        name.set("FollowyUI")
        description.set("Library with graphic elements from Followy for Android")
        inceptionYear.set("2024")
        url.set("https://github.com/BrightOS/FollowyUI/")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("BrightOS")
                name.set("Denis Shaykhlbarin")
                url.set("https://github.com/BrightOS/")
            }
        }

        scm {
            url.set("https://github.com/BrightOS/FollowyUI/")
            connection.set("scm:git@github.com/BrightOS/FollowyUI.git")
            developerConnection.set("scm:git@github.com/BrightOS/FollowyUI.git")
        }
    }
}

dependencies {
    api(libs.androidx.core.ktx)
    api(libs.androidx.appcompat)
    api(libs.material)
}