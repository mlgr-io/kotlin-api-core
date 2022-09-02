import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {

    // use this map to add or override versions
    extra["versions"] = mapOf(
        "example-lib" to "1.2.3",
    )

    apply(from = "01-versions.gradle.kts")
    val plugins: List<String> = extra["plugins"] as List<String>

    dependencies {
        plugins.forEach { classpath(it) }
        // add additional buildscript plugins here
    }
}

extra["kotlinVersion"] = plugins.getPlugin(KotlinPluginWrapper::class.java).pluginVersion
apply(from = "02-configuration.gradle.kts")

plugins {
    kotlin("jvm") version "1.7.10" // this should be the only place where the kotlin version is hard-coded
    // add additional plugins here
}

base {
    group = "io.mailguru"
    version = "0.3.0-SNAPSHOT"
    archivesName.set("mailguru-api-core")
}

dependencies {
    // add additional dependencies here
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

plugins.withId("com.vanniktech.maven.publish.base") {
    configure<MavenPublishBaseExtension> {
        publishToMavenCentral(SonatypeHost.S01)
        signAllPublications()
        pom {
            name.set(project.name)
            description.set("Kotlin model library for usage with various mailguru.io APIs.")
            url.set("https://github.com/mlgr-io/kotlin-api-core")
            licenses {
                license {
                    name.set("GNU General Public License, Version 3")
                    url.set("https://www.gnu.org/licenses/gpl-3.0.txt")
                }
            }
            developers {
                developer {
                    name.set("Sascha Weyers")
                    email.set("sw@mailguru.io")
                    organization.set("mailguru UG (haftungsbeschränkt)")
                    organizationUrl.set("https://mailguru.io")
                }
            }
            scm {
                connection.set("scm:git:git://github.com/mlgr-io/kotlin-api-core.git")
                developerConnection.set("scm:git:ssh://github.com:mlgr-io/kotlin-api-core.git")
                url.set("https://github.com/mlgr-io/kotlin-api-core")
            }
        }
        repositories {
            maven {
                url = if (version.toString().endsWith("SNAPSHOT")) {
                    uri(layout.buildDirectory.dir("repos/snapshots"))
                } else {
                    uri(layout.buildDirectory.dir("repos/releases"))
                }
            }
        }
    }
}