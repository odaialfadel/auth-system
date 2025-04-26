// root settings
rootProject.name = "auth-system"
include("auth-service", "shared")

// Plugin management
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

// Dependency resolution management (repositories only)
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}