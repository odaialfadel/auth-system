plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.jib)
    java
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

    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.oauth2.resource.server)
    implementation(libs.spring.boot.starter.web)

    implementation(libs.keycloak.admin.client)
    implementation(libs.commons.io)

    implementation(libs.flyway.core)
    implementation(libs.flyway.database.postgresql)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    developmentOnly(libs.spring.boot.devtools)
    developmentOnly(libs.spring.boot.docker.compose)
    runtimeOnly(libs.postgresql)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.h2)
    testRuntimeOnly(libs.junit.platform.launcher)
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