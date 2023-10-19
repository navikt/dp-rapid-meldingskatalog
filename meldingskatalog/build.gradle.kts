plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.10"
    `java-library`
}

repositories {
    mavenCentral()
    maven("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
}

dependencies {
    implementation(libs.rapids.and.rivers)
    implementation(libs.kotlin.logging)
    implementation(kotlin("test"))
}

tasks {
    test {
        useJUnitPlatform()
    }
}
