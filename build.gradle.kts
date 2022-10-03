import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly

val kotlin_version = "1.7.20"
buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")
    }
}
plugins {

    kotlin("jvm") version "1.7.20"
    id("org.jetbrains.dokka") version "1.4.32"
    id("maven-publish")
    application
}

version = System.getenv("GITHUB_BUILD_NUMBER")?: project.version.toString()

apply(plugin = "kotlin")
apply(plugin = "com.github.johnrengelman.shadow")
tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveFileName.set("${rootProject.name}.jar")
}

val projectMainClass = "AppKt"
tasks.withType<Jar> {
    manifest {
        attributes(mapOf(
            "Main-Class" to projectMainClass
        ))
    }
}

application {
    mainClass.set(projectMainClass)
}


repositories {
    mavenCentral()
    maven { url = uri("https://repo.dmulloy2.net/repository/public/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/public/") }
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://libraries.minecraft.net/") }
    maven { url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/") }

}

dependencies {
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:${kotlin_version}")
    compileOnly("org.jetbrains.kotlin:kotlin-reflect:${kotlin_version}")
    compileOnly("org.jetbrains.kotlin:kotlin-test:${kotlin_version}")
    implementation("io.github.brucefreedy:mccommand:1.0.1")
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
}

lateinit var sourcesArtifact: PublishArtifact
lateinit var javadocArtifact: PublishArtifact

typealias Shadowjar = com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

tasks {
    val sourcesJar by creating(Shadowjar::class) {
        archiveClassifier.set("sources")
        from(kotlin.sourceSets["main"].kotlin.srcDirs)
    }
    val dokkaHtml by getting(org.jetbrains.dokka.gradle.DokkaTask::class)
    val javadocJar by creating(Shadowjar::class) {
        dependsOn(dokkaHtml)
        archiveClassifier.set("javadoc")
        from(dokkaHtml.outputDirectory)
    }
    artifacts {
        sourcesArtifact = archives(sourcesJar)
        javadocArtifact = archives(javadocJar)
    }
}

allprojects {
    apply(plugin = "maven-publish")


    publishing {
        val repo = System.getenv("GITHUB_REPOSITORY")
        if (repo === null) return@publishing
        repositories {
            maven {
                name = "GitHubPackages"

                url = uri("https://maven.pkg.github.com/$repo")
                credentials {

                    username = System.getenv("GITHUB_ACTOR") as? String
                    password = System.getenv("GITHUB_TOKEN") as? String
                }
            }
        }
        publications {
            register<MavenPublication>(project.name) {
                val githubUserName = repo.substring(0, repo.indexOf("/"))
                groupId = "io.github.${githubUserName.toLowerCaseAsciiOnly()}"
                artifactId = project.name
                version = project.version.toString()
                artifact(sourcesArtifact)
                artifact(javadocArtifact)
            }
        }

    }

    tasks {
        processResources {
            repeat(2) {
                filesMatching("**/*.yml") {
                    expand(HashMap(rootProject.properties)
                        .apply { put("version", project.version)})
                }
            }
        }
    }


}


