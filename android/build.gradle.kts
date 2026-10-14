allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

val newBuildDir: Directory =
    rootProject.layout.buildDirectory
        .dir("../../build")
        .get()
rootProject.layout.buildDirectory.value(newBuildDir)

subprojects {
    val newSubprojectBuildDir: Directory = newBuildDir.dir(project.name)
    project.layout.buildDirectory.value(newSubprojectBuildDir)
}
subprojects {
    project.evaluationDependsOn(":app")
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

// Some Android plugins (for example file_picker 10.3.x) compile Kotlin for JVM 1.8,
// while newer Android Gradle Plugin defaults their Java compile task to JVM 11.
// Keep plugin Java bytecode at 1.8 so each plugin's Java/Kotlin tasks agree.
// The app module is intentionally excluded: its own Gradle file targets Java/Kotlin 17.
subprojects {
    if (name != "app") {
        afterEvaluate {
            tasks.withType<org.gradle.api.tasks.compile.JavaCompile>().configureEach {
                sourceCompatibility = "1.8"
                targetCompatibility = "1.8"
            }
        }
    }
}
