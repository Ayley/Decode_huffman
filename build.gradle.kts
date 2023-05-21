plugins {
    java
}

group = "me.kleidukos"

version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://eldonexus.de/repository/maven-public")
}

dependencies {
    //This needs the decoder from
    testImplementation("org.thshsh:struct:2.1.0")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}