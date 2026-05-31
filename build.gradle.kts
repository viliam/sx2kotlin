plugins {
    kotlin("jvm") version "2.3.10"
}

kotlin {
    jvmToolchain(24)
}

//java {
//    toolchain {
//        languageVersion.set(JavaLanguageVersion.of(23))
//    }
//}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}