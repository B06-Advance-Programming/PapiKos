plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("java")
    id("org.sonarqube") version "6.0.1.5171"
}

group = "id.cs.ui.advprog"
version = "0.0.1-SNAPSHOT"

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

val junitJupiterVersion = "5.9.1"
val mockitoVersion = "5.2.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Spring Boot Starter for JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // PostgreSQL JDBC Driver
    implementation("org.postgresql:postgresql")
    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
    testImplementation("org.mockito:mockito-inline:$mockitoVersion")
    testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")

    // untuk user
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Spring Session JDBC
    implementation("org.springframework.session:spring-session-jdbc")
}

tasks.register<Test>("unitTest") {
    description = "Runs unit tests."
    group = "verification"
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.test{
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport{
    dependsOn(tasks.test)
}

tasks.bootJar {
    archiveClassifier.set("")
}

tasks.jar {
    enabled = false
}

sonar {
    properties {
        property("sonar.projectKey", "InTheKost")
        property("sonar.projectName", "InTheKost")
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        csv.required = false
        html.required = true
    }
}