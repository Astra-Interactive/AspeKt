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
    archiveBaseName.set(libs.versions.plugin.name.get())
    val folder = File(libs.versions.destination.velocity.get())
    if (!folder.exists())
        destinationDirectory.set(File("./jars"))
    else destinationDirectory.set(folder)
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
