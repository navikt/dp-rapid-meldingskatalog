import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint") version "12.2.0"
    alias(libs.plugins.shadow.jar)
    application
}

repositories {
    mavenCentral()
    maven("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
}

dependencies {
    implementation(project(":openapi"))
    implementation(libs.kotlin.logging)
    implementation(libs.rapids.and.rivers)
    implementation(libs.bundles.ktor.server)
    implementation("io.ktor:ktor-server-swagger:${libs.versions.ktor.get()}")
    implementation(libs.bundles.postgres)
    implementation(libs.bundles.jackson)

    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.postgres.test)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.assertions.json)
    testImplementation("io.ktor:ktor-server-test-host-jvm:2.3.7")
}

tasks {
    test {
        useJUnitPlatform()
    }
    jar {
        dependsOn(":openapi:jar")
    }
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("no.nav.dagpenger.meldingskatalog.AppKt")
}

tasks.withType<ShadowJar> {
    mergeServiceFiles()
}
