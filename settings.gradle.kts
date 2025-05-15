pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") // Dépôt JetBrains pour Compose
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS) // Préférence aux dépôts déclarés ici
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") // Dépôt JetBrains pour Compose
    }
}

// ✅ Définition du nom du projet
rootProject.name = "WMSScanner"

// ✅ Inclusion du module principal de l'application
include(":app")
