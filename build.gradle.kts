plugins {
    java
}

group = providers.gradleProperty("group").getOrElse("com.jgxines.craftyourpapers")
version = providers.gradleProperty("version").getOrElse("0.0.1-SNAPSHOT")

val mcVersion: String by project // from gradle.properties (1.21.1)
val paperVersion = "$mcVersion-R0.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.mikeprimm.com/")
    maven("https://repo.bluecolored.de/releases/")
}

dependencies {
    // Compile against Paper API but use ONLY Bukkit API surface in core code
    // so the jar also runs on Spigot. Paper-only features must be optional
    // (reflection / soft-depend) in later phases (Dynmap, Folia, Adventure).
    compileOnly("io.papermc.paper:paper-api:$paperVersion")

    // Web map integrations (all optional at runtime via softdepend).
    compileOnly("us.dynmap:DynmapCoreAPI:3.7-beta-6")
    compileOnly("de.bluecolored:bluemap-api:2.7.7")
    compileOnly("xyz.jpenilla:squaremap-api:1.3.14")

    testImplementation(platform("org.junit:junit-bom:5.11.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.test {
    useJUnitPlatform()
}

// Expand version placeholders inside plugin.yml / config if needed later.
tasks.processResources {
    val props = mapOf("version" to project.version.toString())
    inputs.properties(props)
    filesMatching("plugin.yml") {
        expand(props)
    }
}
