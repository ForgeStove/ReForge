plugins {
	java
	id("com.github.breadmoirai.github-release") version "+"
}
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
allprojects { tasks.withType<JavaCompile>().configureEach { options.compilerArgs.addAll(listOf("--release", "8")) } }
dependencies {
    compileOnly("com.github.Anuken.Arc:arc-core:${p("mindustry_version")}")
    compileOnly("com.github.Anuken.Mindustry:core:${p("mindustry_version")}")
    annotationProcessor("com.github.Anuken:jabel:${p("jabel_version")}")
}
configurations.configureEach {
    resolutionStrategy.eachDependency { if (requested.group == "com.github.Anuken.Arc") { useVersion(p("mindustry_version")) } }
}
tasks.register("jarAndroid") {
    dependsOn("jar")
    doLast {
        val sdkRoot: String? = System.getenv("ANDROID_HOME")
        if (sdkRoot.isNullOrEmpty() || !File(sdkRoot).exists()) {
            throw GradleException("No valid Android SDK found. Ensure that ANDROID_HOME is set to your Android SDK directory.")
        }
        // 查找最新版本的 platform
        val platformRoot = File("$sdkRoot/platforms/").listFiles()
            ?.sortedDescending()
            ?.find { f -> File(f, "android.jar").exists() }
            ?: throw GradleException("No android.jar found. Ensure that you have an Android platform installed.")
        // 查找最新版本的 build-tools
        val buildToolsDir = File("$sdkRoot/build-tools/").listFiles()?.maxOrNull()
            ?: throw GradleException("No build-tools found. Please install build-tools via Android SDK Manager.")
        // 确定d8工具的完整路径
        val isWindows = System.getProperty("os.name").lowercase().contains("windows")
        val d8Executable = if (isWindows) "d8.bat" else "d8"
        val d8Path = File(buildToolsDir, d8Executable).absolutePath
        // 收集反糖所需的依赖
        val dependencies = (configurations.compileClasspath.get().toList() +
                           configurations.runtimeClasspath.get().toList() +
                           listOf(File(platformRoot, "android.jar")))
            .joinToString(" ") { "--classpath ${it.path}" }
		// 执行dex和desugar文件 - 使用d8的完整路径
        val command = listOf(d8Path) +
                     dependencies.split(" ") +
                     listOf("--min-api", "14", "--output", "${project.name}Android.jar", "${project.name}Desktop.jar")
        ProcessBuilder(command)
            .directory(File("${layout.buildDirectory.get()}/libs"))
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
        zipTree("${layout.buildDirectory.get()}/libs/${project.name}Desktop.jar"),
        zipTree("${layout.buildDirectory.get()}/libs/${project.name}Android.jar")
    )
    doLast {
        delete(
            "${layout.buildDirectory.get()}/libs/${project.name}Desktop.jar",
            "${layout.buildDirectory.get()}/libs/${project.name}Android.jar"
        )
    }
}
githubRelease {
	token(System.getenv("GITHUB_TOKEN"))
	owner = "ForgeStove"
	repo = "ReForge"
	tagName = "v${p("mod_version")}"
	releaseName = tagName
	generateReleaseNotes = true
	prerelease = false
	releaseAssets(tasks.named("deploy").get().outputs.files)
	overwrite = true
}
fun p(key: String) = extra[key].toString()
