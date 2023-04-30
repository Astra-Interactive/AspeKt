plugins {
    java
    `java-library`
    id("org.jetbrains.kotlin.jvm")
    id("com.github.johnrengelman.shadow")
}
tasks.shadowJar {
    isReproducibleFileOrder = true
    mergeServiceFiles()
    relocate("org.bstats", libs.versions.plugin.group.get())
    listOf(
        "kotlin",
        "org.jetbrains",
        libs.minecraft.astralibs.ktxcore.get().module.group
    ).forEach {
        relocate(it, libs.versions.plugin.group.get() + ".$it")
    }
    dependsOn(configurations)
    archiveClassifier.set(null as String?)
    from(sourceSets.main.get().output)
    from(project.configurations.runtimeClasspath)
    minimize()
    archiveBaseName.set(libs.versions.plugin.name.get())
    val folder = File(libs.versions.destination.paper.get())
    if (!folder.exists())
        destinationDirectory.set(File("./jars"))
    else destinationDirectory.set(folder)
}
