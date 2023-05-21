plugins {
    java

    id("de.chojo.publishdata") version "1.2.4"
    `maven-publish`
}

group = "me.kleidukos"

version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    //This needs the decoder from the test
    testImplementation("org.thshsh:struct:2.1.0")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

publishData {
    useEldoNexusRepos()

    publishComponent("java")
}

publishing {
    publications.create<MavenPublication>("maven") {
        publishData.configurePublication(this)
    }

    repositories {
        maven {
            authentication {

            }
        }

        maven {
            authentication {
                credentials(PasswordCredentials::class) {
                    username = System.getenv("NEXUS_USERNAME")
                    password = System.getenv("NEXUS_PASSWORD")
                }
            }

            setUrl(publishData.getRepository())
        }
    }
}

tasks.test {
    useJUnitPlatform()
}