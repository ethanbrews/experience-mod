plugins {
    id("fabric-loom")
    val kotlinVersion: String by System.getProperties()
    kotlin("jvm").version(kotlinVersion)
    id("com.modrinth.minotaur").version("2.+")
    id("org.jetbrains.dokka") version "1.7.20"
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("experience")
    versionNumber.set("1.0.0")
    versionType.set("alpha")
    //uploadFile.set(tasks.remapJar as (Provider<out Any>)) // With Loom, this MUST be set to `remapJar` instead of `jar`!
    gameVersions.addAll(listOf("1.19.2"))
    loaders.add("fabric")
    dependencies {
        // The scope can be `required`, `optional`, `incompatible`, or `embedded`
        required.project("fabric-api")
        required.project("fabric-language-kotlin")
    }
}

base {
    val archivesBaseName: String by project
    archivesName.set(archivesBaseName)
}


val modVersion: String by project
version = modVersion
val mavenGroup: String by project
group = mavenGroup
repositories {}


dependencies {
    val minecraftVersion: String by project
    minecraft("com.mojang", "minecraft", minecraftVersion)
    val yarnMappings: String by project
    mappings("net.fabricmc", "yarn", yarnMappings, null, "v2")
    val loaderVersion: String by project
    modImplementation("net.fabricmc", "fabric-loader", loaderVersion)
    val fabricVersion: String by project
    modImplementation("net.fabricmc.fabric-api", "fabric-api", fabricVersion)
    val fabricKotlinVersion: String by project
    modImplementation("net.fabricmc", "fabric-language-kotlin", fabricKotlinVersion)
}


tasks {
    val javaVersion = JavaVersion.VERSION_17
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release.set(javaVersion.toString().toInt())
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> { kotlinOptions { jvmTarget = javaVersion.toString() } }
    jar { from("LICENSE") { rename { "${it}_${base.archivesName}" } } }
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") { expand(mutableMapOf("version" to project.version)) }
    }
    java {
        toolchain { languageVersion.set(JavaLanguageVersion.of(javaVersion.toString())) }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        withSourcesJar()
    }
}
