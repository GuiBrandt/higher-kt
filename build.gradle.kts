plugins {
    `java-library`
    kotlin("jvm") version "1.5.10"
    kotlin("kapt") version "1.5.10"
}

allprojects {
    group = "io.github.higherkt"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        google()
    }
}

dependencies {
    compileOnly(project(":annotations"))
    kapt(project(":processor"))
}
