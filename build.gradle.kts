plugins {
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    alias(libs.plugins.jib) apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
}