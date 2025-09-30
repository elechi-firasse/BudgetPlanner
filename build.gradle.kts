plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}

repositories { mavenCentral() }

dependencies {
    implementation("org.xerial:sqlite-jdbc:3.46.0.0")
    implementation("org.apache.commons:commons-csv:1.11.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.graphics")
}

application {
    mainClass.set("app.MainApp")
}

tasks.test { useJUnitPlatform() }
