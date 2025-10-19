plugins {
  kotlin("jvm") version "1.9.25"
  kotlin("plugin.spring") version "1.9.25"
  kotlin("plugin.jpa") version "1.9.25"
  id("org.springframework.boot") version "3.5.6"
  id("io.spring.dependency-management") version "1.1.7"
}

group = "com.kyungmin"
version = "0.0.1-SNAPSHOT"
description = "Spring-Batch-Study"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-jdbc")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")

  // Spring Batch
  implementation("org.springframework.boot:spring-boot-starter-batch")

  // Kotlin JDSL
//  implementation("com.linecorp.kotlin-jdsl:jpql-dsl:3.5.7")
//  implementation("com.linecorp.kotlin-jdsl:jpql-render:3.5.7")
//  implementation("com.linecorp.kotlin-jdsl:spring-data-jpa-support:3.5.7")

  // Kotlin Loging
  implementation("io.github.oshai:kotlin-logging-jvm:7.0.13")
  implementation("org.slf4j:slf4j-simple:2.0.3")

  compileOnly("org.projectlombok:lombok")
  runtimeOnly("com.mysql:mysql-connector-j")
  annotationProcessor("org.projectlombok:lombok")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testImplementation("org.springframework.batch:spring-batch-test")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict")
  }
}

allOpen {
  annotation("jakarta.persistence.Entity")
  annotation("jakarta.persistence.MappedSuperclass")
  annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
  useJUnitPlatform()
}
