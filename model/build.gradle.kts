plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.jackson)
    implementation(kotlin("test"))
}

tasks {
    test {
        useJUnitPlatform()
    }
}
