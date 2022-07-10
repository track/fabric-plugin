plugins {
	id ("fabric-loom") version "0.12-SNAPSHOT"
	id ("maven-publish")
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

base {
	val archivesBaseName: String by project
	archivesName.set(archivesBaseName)
}

val modVersion: String by project
version = modVersion

val mavenGroup: String by project
group = mavenGroup

dependencies {
	implementation("ninja.leaping.configurate:configurate-yaml:3.7.1")
	// To change the versions see the gradle.properties file
	val minecraftVersion: String by project
	minecraft("com.mojang:minecraft:${minecraftVersion}")

	val yarnMappings: String by project
	mappings("net.fabricmc:yarn:${yarnMappings}:v2")

	val loaderVersion: String by project
	modImplementation("net.fabricmc:fabric-loader:${loaderVersion}")

	val fabricVersion: String by project
	// Fabric API. This is technically optional, but you probably want it anyway.
	modImplementation("net.fabricmc.fabric-api:fabric-api:${fabricVersion}")

	// Uncomment the following line to enable the deprecated Fabric API modules.
	// These are included in the Fabric API production distribution and allow you to update your mod to the latest modules at a later more convenient time.

	// modImplementation "net.fabricmc.fabric-api:fabric-api-deprecated:${project.fabric_version}"
}
repositories {
	mavenCentral()
}

tasks {
	val javaVersion = JavaVersion.VERSION_17
	withType<JavaCompile> {
		options.encoding = "UTF-8"
		sourceCompatibility = javaVersion.toString()
		targetCompatibility = javaVersion.toString()
		options.release.set(javaVersion.toString().toInt())
	}
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