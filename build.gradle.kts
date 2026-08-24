plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.modstitch)
}

val platform = stonecutter.current.project.substringAfter('-')
val mcVersion = stonecutter.current.version
val javaTarget = (property("java_version") as String).toInt()

kotlin {
    jvmToolchain(javaTarget)
    coreLibrariesVersion = property("deps.kotlin_stdlib") as String
}

modstitch {
    minecraftVersion = mcVersion
    javaVersion = javaTarget

    parchment {
        mappingsVersion = property("deps.parchment") as String
    }

    moddevgradle {
        neoForgeVersion = property("deps.neoforge") as? String

        defaultRuns(client = true, server = false)

        configureNeoForge {
            runs.all { gameDirectory = rootProject.file("run") }
        }
    }

    metadata {
        modId = property("mod_id") as String
        modName = property("mod_name") as String
        modVersion = "${property("mod_version")}+$mcVersion"
        modGroup = property("maven_group") as String
        modDescription = "Adds support for Sable sub-levels for Dream Displays"
        modLicense = "LGPL-3.0"
        modAuthor = "apehum"

        replacementProperties.apply {
            put("kotlin_for_forge_range", property("range.kotlin_for_forge") as String)
            put("neoforge_loader_range", property("range.neoforge_loader") as String)
            put("minecraft_version_range", property("range.minecraft") as String)
            put("dreamdisplays_range", property("range.dreamdisplays") as String)
            put("sable_range", property("range.sable") as String)
        }
    }

    mixin {
        addMixinsToModManifest = true

        configs.register(property("mod_id") as String)
    }
}

base {
    archivesName = "${property("archives_base_name")}-$platform"
}

dependencies {
    modstitchCompileOnly("dev.ryanhcode.sable-companion:sable-companion-common-$mcVersion:${property("deps.sable_companion")}")

    modstitch.moddevgradle {
        modstitchModImplementation("thedarkcolour:kotlinforforge-neoforge:${property("deps.kotlin_for_forge")}")
        modstitchModCompileOnly("maven.modrinth:dreamdisplays:${property("deps.dreamdisplays")}")
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
