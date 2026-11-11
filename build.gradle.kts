plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

// change to your nickname eg dev.pepitoperez
group = "dev.integrador"
version = "1.0-SNAPSHOT"

application {
    // the group must match here
    mainClass.set("dev.integrador.university.FXApplication")
}

repositories {
    mavenCentral()
}

java {
    // fija la versión de Java en el proyecto
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.mongodb:mongodb-driver-sync:5.11.1")
    implementation("org.mongojack:mongojack:5.1.0")
    implementation("org.slf4j:slf4j-nop:2.0.19")

}

javafx {
    // version anterior al proyecto es la "24" cerrado
    version = "21.0.6"
    modules("javafx.controls")
}

tasks.test {
    useJUnitPlatform()
}
