package util

import org.gradle.api.Project
import org.gradle.api.tasks.Copy

/**
 * Makes XaeroLib available at compile time.
 *
 * <p>XaeroLib is nested inside Xaero's World Map as a JiJ jar, so it is not on the compile
 * classpath automatically. This extracts it from the World Map artifact so that mixins can target
 * its classes directly.</p>
 */
class XaeroLib {

    static void addCompileOnly(Project project, String worldMapVersion) {
        final cfgName = "xaeroWorldMap"
        if (project.configurations.findByName(cfgName) == null) {
            project.configurations.create(cfgName) {
                canBeConsumed = false
                canBeResolved = true
                transitive = false
            }
        }
        project.dependencies.add(cfgName, "maven.modrinth:NcUtCpym:${worldMapVersion}")

        final jarDir = project.layout.buildDirectory.dir("xaerolib-jar")
        final classesDir = project.layout.buildDirectory.dir("xaerolib-classes")

        final extractJar = project.tasks.register("extractXaeroLibJar", Copy) {
            from({ project.zipTree(project.configurations.getByName(cfgName).singleFile) }) {
                include "META-INF/jars/xaerolib-*.jar"
                include "META-INF/jarjar/xaerolib-*.jar"
                eachFile { path = "xaerolib.jar" }
                includeEmptyDirs = false
            }
            into(jarDir)
        }
        final extractClasses = project.tasks.register("extractXaeroLibClasses", Copy) {
            dependsOn(extractJar)
            from({ project.zipTree(jarDir.get().file("xaerolib.jar")) })
            into(classesDir)
        }

        final classpath = project.files(classesDir)
        classpath.builtBy(extractClasses)
        project.dependencies.add("compileOnly", classpath)
    }
}
