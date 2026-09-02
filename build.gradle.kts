plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.modstitch)
    alias(libs.plugins.modpublish)
}

fun stringProperty(name: String): String = property(name) as String

val platform = stonecutter.current.project.substringAfter('-')
val mcVersion = stonecutter.current.version
val javaTarget = stringProperty("java_version").toInt()

kotlin {
    jvmToolchain(javaTarget)
}

ktlint {
    version = libs.versions.ktlint.asProvider()
}

modstitch {
    minecraftVersion = mcVersion
    javaVersion = javaTarget

    parchment {
        mappingsVersion = stringProperty("deps.parchment")
    }

    moddevgradle {
        neoForgeVersion = property("deps.neoforge") as? String

        defaultRuns(client = true, server = false)

        configureNeoForge {
            runs.all { gameDirectory = rootProject.file("run") }
        }
    }

    metadata {
        modId = stringProperty("mod_id")
        modName = stringProperty("mod_name")
        modVersion = "${property("mod_version")}+$mcVersion"
        modGroup = stringProperty("maven_group")
        modDescription = "Addon for Dream Displays that makes displays work in Sable sub-levels"
        modLicense = "LGPL-3.0"
        modAuthor = "apehum"

        replacementProperties.apply {
            put("kotlin_for_forge_range", stringProperty("range.kotlin_for_forge"))
            put("neoforge_loader_range", stringProperty("range.neoforge_loader"))
            put("minecraft_version_range", stringProperty("range.minecraft"))
            put("dreamdisplays_range", stringProperty("range.dreamdisplays"))
            put("sable_range", stringProperty("range.sable"))
        }
    }

    mixin {
        addMixinsToModManifest = true

        configs.register(stringProperty("mod_id"))
    }
}

base {
    archivesName = "${property("archives_base_name")}-$platform"
}

dependencies {
    compileOnly(libs.kotlinx.coroutines.core)
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly("dev.ryanhcode.sable-companion:sable-companion-common-$mcVersion:${property("deps.sable_companion")}")
    compileOnly("dev.ryanhcode.sable:sable-common-$mcVersion:${property("deps.sable")}") {
        isTransitive = false
    }

    modstitch.moddevgradle {
        modstitchJiJ("dev.ryanhcode.sable-companion:sable-companion-common-$mcVersion:[${property("deps.sable_companion")},)") {
            version { prefer(stringProperty("deps.sable_companion")) }
        }

        modstitchModImplementation("thedarkcolour:kotlinforforge-neoforge:${property("deps.kotlin_for_forge")}")
        modstitchModImplementation("maven.modrinth:dreamdisplays:${property("deps.dreamdisplays")}")
        modstitchModImplementation("maven.modrinth:sable:${property("deps.sable")}+mc$mcVersion-neoforge")
    }
}

val outputJarTask = modstitch.finalJarTask

tasks {
    jar {
        from(rootProject.file("LICENSE")) {
            rename { "${it}_${project.property("archives_base_name")}" }
        }
    }

    val copyToRoot =
        register<Copy>("copyToRoot") {
            description = "Copies final jar to root project build directory"

            dependsOn(outputJarTask)
            from(outputJarTask.map { it.archiveFile.get() })
            into(rootProject.layout.buildDirectory.dir("libs"))
        }

    build {
        dependsOn(copyToRoot)
    }
}

publishMods {
    changelog =
        rootProject.layout.projectDirectory
            .file("changelog.md")
            .asFile
            .readText()

    val releaseType =
        providers
            .gradleProperty("release_type")
            .getOrElse("release")
            .lowercase()

    type =
        when (releaseType) {
            "release", "stable" -> STABLE
            "beta" -> BETA
            "alpha" -> ALPHA
            else -> throw IllegalStateException("Unsupported release type $releaseType")
        }

    modLoaders.add(platform)

    val loaderDisplayName =
        when (platform) {
            "fabric" -> "Fabric"
            "neoforge" -> "NeoForge"
            else -> throw IllegalStateException("Unsupported platform $platform")
        }

    displayName = "[$loaderDisplayName ${property("minecraft_version")}] ${property("mod_name")} ${property("mod_version")}"
    file = outputJarTask.get().archiveFile

    val modrinthToken =
        providers
            .gradleProperty("modrinth_token")
            .orElse(
                providers.environmentVariable("MODRINTH_TOKEN").orNull ?: "",
            ).orNull
            ?.takeIf { it.isNotBlank() }

    val curseforgeToken =
        providers
            .gradleProperty("curseforge_token")
            .orElse(
                providers.environmentVariable("CURSEFORGE_TOKEN").orNull ?: "",
            ).orNull
            ?.takeIf { it.isNotBlank() }

    val dryRunProperty =
        providers
            .gradleProperty("dry_run")
            .getOrElse("false")
            .toBoolean()

    dryRun = modrinthToken == null || curseforgeToken == null || dryRunProperty

    modrinth {
        projectId = ""
        accessToken = modrinthToken

        environment = CLIENT_AND_SERVER

        minecraftVersionRange {
            start = stringProperty("range.minecraft.start")
            end = stringProperty("range.minecraft.end")
        }

        requires("dreamdisplays", "kotlin-for-forge")
    }

    curseforge {
        projectId = ""
        accessToken = curseforgeToken
        changelogType = "markdown"

        client = true
        server = true

        minecraftVersionRange {
            start = stringProperty("range.minecraft.start")
            end = stringProperty("range.minecraft.end")
        }

        requires("dreamdisplays", "kotlin-for-forge")
    }
}
