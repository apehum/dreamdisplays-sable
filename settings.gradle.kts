pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.fabricmc.net/")
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.kikugie.stonecutter") version "0.9.7"
}

stonecutter {
    centralScript = "build.gradle.kts"

    create(rootProject) {
        fun mc(
            mcVersion: String,
            vararg loaders: String,
        ) = loaders.forEach { version("$mcVersion-$it", mcVersion) }

        mc("1.21.1", "neoforge")

        vcsVersion = "1.21.1-neoforge"
    }
}

rootProject.name = "dreamdisplays-sable"
