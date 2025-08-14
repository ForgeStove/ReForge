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
        val platformRoot = File("$sdkRoot/platforms/").listFiles()
            ?.sortedDescending()
            ?.find { f -> File(f, "android.jar").exists() }
        if (platformRoot == null) { throw GradleException("No android.jar found. Ensure that you have an Android platform installed.") }
        // collect dependencies needed for desugaring
        val dependencies = (configurations.compileClasspath.get().files +
                           configurations.runtimeClasspath.get().files +
                           listOf(File(platformRoot, "android.jar")))
            .joinToString(" ") { "--classpath ${it.path}" }

        val d8 = if (isWindows) "d8.bat" else "d8"
        // dex and desugar files - this requires d8 in your PATH
        ProcessBuilder("$d8 $dependencies --min-api 14 --output ${project.name}Android.jar ${project.name}Desktop.jar".split(" "))
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
