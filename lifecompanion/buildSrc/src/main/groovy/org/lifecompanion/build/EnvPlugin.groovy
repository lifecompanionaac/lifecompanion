package org.lifecompanion.build

import org.gradle.api.Plugin
import org.gradle.api.Project

class EnvPlugin implements Plugin<Project> {
    void apply(Project project) {
        BuildToolUtils.injectEnvPropertiesToProject(project)
    }
}