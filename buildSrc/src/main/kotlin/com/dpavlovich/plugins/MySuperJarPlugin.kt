package com.dpavlovich.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.jvm.tasks.Jar
import org.gradle.plugin.use.PluginDependenciesSpec

private const val RUNTIME_CLASSPATH = "runtimeClasspath"
private const val COPY_ALL_DEPENDENCIES_TASK = "copyAllDependencies"

/**
 * plugin id: my-super-jar-plugin-id
 *
 * we set the id in: META-INF/gradle-plugins/my-super-jar-plugin-id.properties
 * this is the name of the property file.
 *
 * add ref to your entrypoint class
 *
 * tasks.getByName("jar", Jar::class) {
 *     manifest {
 *         attributes["Main-Class"] = "com.example.EntryPointClassKt"
 *     }
 * }
 */
class MySuperJarPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.tasks.register(COPY_ALL_DEPENDENCIES_TASK, Copy::class.java) {
            it.from(target.configurations.getByName(RUNTIME_CLASSPATH))
                .into("${target.buildDir}/libs/lib")
        }

        target.tasks.withType(Jar::class.java) { task ->
            val jars = target.configurations.getByName(RUNTIME_CLASSPATH)
                .files.stream()
                .map {"lib/${it.name}" }
                .toList()

            task.manifest {
//                it.attributes["Main-Class"] = "com.example.EntryPointClassKt"
                it.attributes["Class-Path"] = jars.joinToString(" ")
            }

            task.dependsOn(COPY_ALL_DEPENDENCIES_TASK)
        }
    }
}