plugins {
    kotlin("jvm") version "1.9.25" apply false
    kotlin("plugin.spring") version "1.9.25" apply false
    id("org.springframework.boot") version "3.5.4" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    kotlin("plugin.jpa") version "1.9.25" apply false
}

group = "rounderall"
version = "0.0.1-SNAPSHOT"
description = "allrounder"

allprojects {
    group = "rounderall"
    version = "0.0.1-SNAPSHOT"
    
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.plugin.spring")
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
        plugin("org.jetbrains.kotlin.plugin.jpa")
    }
    
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
