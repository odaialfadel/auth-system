plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.jib)
}

group = "com.odai.auth"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get().toInt()))
    }
}

dependencies {
    implementation(project(":shared"))

    // Core dependencies
    implementation(libs.bundles.spring.boot)
    implementation(libs.bundles.flyway)
    implementation(libs.bundles.jackson)

    implementation(libs.postgresql)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(libs.jakarta.validation.api)
    implementation(libs.commons.io)
    implementation(libs.commons.compress)
    implementation(libs.keycloak.admin.client)

    // Development and runtime
    developmentOnly(libs.spring.boot.devtools)
    runtimeOnly(libs.spring.boot.docker.compose)

    // Test dependencies
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.junit)
    testImplementation(libs.bundles.testcontainers)
    testImplementation(libs.h2)
    testImplementation(libs.green.mail)
}

tasks.test {
    useJUnitPlatform()
}

jib {
    from {
        image = "eclipse-temurin:21-jre"
    }
    to {
        image = "docker.io/auth-service"
        auth {
            username = System.getenv("DOCKER_USERNAME")
            password = System.getenv("DOCKER_PASSWORD")
        }
    }
    container {
        ports = listOf("8080")
        jvmFlags = listOf("-Xms512m", "-Xmx1024m")
    }
}