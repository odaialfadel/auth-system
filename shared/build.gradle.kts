plugins {
    java
}

group = "com.odai.shared"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get().toInt()))
    }
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    implementation(libs.jakarta.validation.api)
    implementation(libs.jackson)
}

tasks.test {
    useJUnitPlatform()
}