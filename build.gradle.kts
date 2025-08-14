plugins { java }
version = p("mod_version")
repositories {
    mavenCentral()
    maven { url = uri("https://raw.githubusercontent.com/Zelaux/MindustryRepo/master/repository") }
    maven { url = uri("https://www.jitpack.io") }
}
tasks.processResources {
    outputs.upToDateWhen { false }
    filesMatching("mod.hjson") { expand(properties) }
}
java { targetCompatibility = JavaVersion.VERSION_1_8; sourceCompatibility = JavaVersion.VERSION_17 }
val mindustryVersion = "v150.1"
val jabelVersion = "93fde537c7"
val isWindows = System.getProperty("os.name").lowercase().contains("windows")
val sdkRoot: String? = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
allprojects { tasks.withType<JavaCompile>().configureEach { options.compilerArgs.addAll(listOf("--release", "8")) } }
dependencies {
    compileOnly("com.github.Anuken.Arc:arc-core:$mindustryVersion")
    compileOnly("com.github.Anuken.Mindustry:core:$mindustryVersion")
    annotationProcessor("com.github.Anuken:jabel:$jabelVersion")
}
configurations.configureEach {
    resolutionStrategy.eachDependency { if (requested.group == "com.github.Anuken.Arc") { useVersion(mindustryVersion) } }
}
tasks.register("jarAndroid") {
    dependsOn("jar")
    doLast {
        if (sdkRoot.isNullOrEmpty() || !File(sdkRoot).exists()) {
            throw GradleException("No valid Android SDK found. Ensure that ANDROID_HOME is set to your Android SDK directory.")
        }
        // 查找最新版本的 platform
        val platformRoot = File("$sdkRoot/platforms/").listFiles()
            ?.sortedDescending()
            ?.find { f -> File(f, "android.jar").exists() }
        if (platformRoot == null) { throw GradleException("No android.jar found. Ensure that you have an Android platform installed.") }
        // 查找最新版本的 build-tools
        val buildToolsDir = File("$sdkRoot/build-tools/").listFiles()?.maxOrNull()
            ?: throw GradleException("No build-tools found. Ensure that you have Android build-tools installed.")
        // 确定 d8 工具的完整路径
        val d8Path = if (isWindows)
            File(buildToolsDir, "d8.bat").path
        else
            File(buildToolsDir, "d8").path
        // 收集依赖
        val dependencies = (configurations.compileClasspath.get().files +
                         configurations.runtimeClasspath.get().files +
                         listOf(File(platformRoot, "android.jar")))
            .joinToString(" ") { "--classpath ${it.path}" }
        // 使用完整路径调用 d8 工具
        val command = "$d8Path $dependencies --min-api 14 --output ${project.name}Android.jar ${project.name}Desktop.jar"
        println("Executing command: $command")
        ProcessBuilder(command.split(" "))
            .directory(File("${layout.buildDirectory}/libs"))
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor()
    }
}
tasks.jar {
    archiveFileName.set("${base.archivesName.get()}Desktop.jar")
}
tasks.register<Jar>("deploy") {
    dependsOn("jarAndroid")
    dependsOn("jar")
    archiveFileName.set("${base.archivesName.get()}.jar")
    from(
        zipTree("${layout.buildDirectory}/libs/${project.name}Desktop.jar"),
        zipTree("${layout.buildDirectory}/libs/${project.name}Android.jar")
    )
    doLast {
        delete(
            "${layout.buildDirectory}/libs/${project.name}Desktop.jar",
            "${layout.buildDirectory}/libs/${project.name}Android.jar"
        )
    }
}
fun p(key: String) = extra[key].toString()
