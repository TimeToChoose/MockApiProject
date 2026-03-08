import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.shineofeidos.mockapiproject"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.shineofeidos.mockapiproject"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions.add("environment")
    productFlavors {
        create("genymotion") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"http://10.0.3.2:4523/m1/7815393-7563142-default/\"")
        }
        create("realdevice") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"http://192.168.2.128:4523/m1/7815393-7563142-default/\"")
        }
        create("emulator") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:4523/m1/7815393-7563142-default/\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.coil.compose)
    implementation(libs.markdown.compose)
    implementation(libs.eventbus)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

// APK 重命名任务
// 保持默认输出文件名，避免 Flutter CLI 扫描产物失败
tasks.register("renameReleaseApk") {
    doLast {
        val df = SimpleDateFormat("MM-dd-HH-mm")
        df.timeZone = TimeZone.getTimeZone("Asia/Shanghai")
        val ts = df.format(Date())
        val prefix = "mockapi"
        val ver = android.defaultConfig.versionName
        val newName = "${prefix}_${ver}-${ts}-release.apk"
        val outputsDir = file("${layout.buildDirectory.get()}/intermediates/apk/release")
        val apk = File(outputsDir, "app-release.apk")
        if (apk.exists()) {
            val projectRoot = rootProject.projectDir
            val destDir = File(projectRoot, "apk/release")
            if (!destDir.exists()) destDir.mkdirs()
            copy {
                from(apk)
                into(destDir)
            }
            val copied = File(destDir, "app-release.apk")
            val target = File(destDir, newName)
            if (target.exists()) target.delete()
            if (copied.exists()) {
                copied.renameTo(target)
                File(destDir, "app-release.apk").delete()
            }
            val meta = File(outputsDir, "output-metadata.json")
            val metaDest = File(destDir, "output-metadata.json")
            if (metaDest.exists()) metaDest.delete()
            if (meta.exists()) {
                copy {
                    from(meta)
                    into(destDir)
                }
            }
            destDir.listFiles { _, name ->
                name.startsWith("mockapi_") && name.endsWith("-release.apk") && name != newName
            }?.forEach { it.delete() }
            println("Release APK copied to: ${File(destDir, newName).absolutePath}")
        } else {
            println("Release APK not found at: ${apk.absolutePath}")
        }
    }
}

tasks.register("renameDebugApk") {
    doLast {
        val df = SimpleDateFormat("MM-dd-HH-mm")
        df.timeZone = TimeZone.getTimeZone("Asia/Shanghai")
        val ts = df.format(Date())
        val prefix = "mockapi"
        val ver = android.defaultConfig.versionName
        val expected = "${prefix}_${ver}-${ts}-debug.apk"

        val projectRoot = rootProject.projectDir
        val destDir = File(projectRoot, "apk/debug")
        if (!destDir.exists()) destDir.mkdirs()

        val outputsDir = file("${layout.buildDirectory.get()}/intermediates/apk/debug")
        val apk = File(outputsDir, "app-debug.apk")
        if (apk.exists()) {
            copy {
                from(apk)
                into(destDir)
            }
            val copied = File(destDir, "app-debug.apk")
            val target = File(destDir, expected)
            if (target.exists()) target.delete()
            if (copied.exists()) {
                copied.renameTo(target)
                File(destDir, "app-debug.apk").delete()
            }
            val meta = File(outputsDir, "output-metadata.json")
            val metaDest = File(destDir, "output-metadata.json")
            if (metaDest.exists()) metaDest.delete()
            if (meta.exists()) {
                copy {
                    from(meta)
                    into(destDir)
                }
            }
            println("Debug APK copied to: ${File(destDir, expected).absolutePath}")
        } else {
            println("Debug APK not found at: ${apk.absolutePath}")
        }

        destDir.listFiles { _, name ->
            name.startsWith("mockapi_") && name.endsWith("-debug.apk") && name != expected
        }?.forEach { it.delete() }
    }
}

tasks.whenTaskAdded {
    if (name == "assembleRelease") {
        finalizedBy("renameReleaseApk")
    }
    if (name == "assembleDebug") {
        finalizedBy("renameDebugApk")
    }
}