plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "1.21.1-neoforge"

stonecutter parameters {
    constants.match(node.metadata.project.substringAfterLast('-'), "fabric", "neoforge")
}

gradle.projectsEvaluated {
    subprojects.sortedBy { it.name }.zipWithNext { prev, curr ->
        curr.tasks.matching { it.name.startsWith("publish") }.configureEach {
            val taskName = name
            mustRunAfter(prev.tasks.matching { it.name == taskName })
        }
    }
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.parchmentmc.org")
        maven("https://thedarkcolour.github.io/KotlinForForge/")

        maven("https://api.modrinth.com/maven") {
            content { includeGroup("maven.modrinth") }
        }

        exclusiveContent {
            forRepository {
                maven("https://maven.ryanhcode.dev/releases")
            }
            filter { includeGroup("dev.ryanhcode.sable-companion") }
        }
    }
}
