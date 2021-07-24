plugins {
    id("org.jetbrains.kotlin.plugin.serialization") version "1.4.10"
    kotlin("jvm") version "1.5.10"
    id("application")
}

group = "ru.raysmith"
version = "1.0.2"

repositories {
    mavenCentral()
    jcenter()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/raysmith-ttc/utils")
        credentials {
            username = "RaySmith-ttc"
            password = "6de056c84dd8af5cea9f8404480ced46e3030be9"
        }
    }
}

dependencies {

    val retrofitCoroutinesAdapterVersion = "0.9.2"
    val kotlinxSerialization = "1.0.1"
    val kotlinxSerializationConverter = "0.8.0"

    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.2")

    implementation("ru.raysmith:utils:1.0.0-rc.1")

    implementation("log4j:log4j:1.2.17")
    implementation("org.slf4j:slf4j-api:1.7.26")
    implementation("org.slf4j:slf4j-log4j12:1.7.26")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerialization")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerialization")

    implementation("com.squareup.retrofit2:retrofit:2.7.0")
    implementation("com.squareup.retrofit2:converter-gson:2.7.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:$retrofitCoroutinesAdapterVersion")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:$kotlinxSerializationConverter")
}

tasks.register<Jar>("uberJar") {
    manifest {
        attributes(mapOf(
            "Main-Class" to "ru.raysmith.nemesisbot.MainKt",
            "Class-Path" to ".",
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        ))
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}