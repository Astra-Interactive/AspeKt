plugins {
    java
    `java-library`
    id("org.jetbrains.kotlin.jvm")
    id("com.github.johnrengelman.shadow")
}

tasks.shadowJar {
    isReproducibleFileOrder = true
    mergeServiceFiles()
    dependsOn(configurations)
    archiveClassifier.set(null as String?)
    from(sourceSets.main.get().output)
    from(project.configurations.runtimeClasspath)
    minimize()
    archiveBaseName.set(libs.versions.project.name.get())
    File(libs.versions.destination.paper.get()).let {
        if (!it.exists()) {
            File(rootDir, "jars").also(File::mkdirs)
        } else {
            it
        }
    }.also(destinationDirectory::set)
}
tasks.build {
    dependsOn(tasks.shadowJar)
}
tasks.assemble {
    dependsOn(tasks.shadowJar)
}
artifacts {
    archives(tasks.shadowJar)
}
