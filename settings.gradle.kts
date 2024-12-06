pluginManagement {
   repositories {
      google {
         content {
            includeGroupByRegex("com\\.android.*")
            includeGroupByRegex("com\\.google.*")
            includeGroupByRegex("androidx.*")
         }
      }
      maven { url = uri("https://jitpack.io") }
      mavenCentral()
   }
}
dependencyResolutionManagement {
   repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
   repositories {
      google()
      mavenCentral()
      maven { url = uri("https://www.jitpack.io") }
   }
}

rootProject.name = "Submission Aplikasi Story App"
include(":app")
 