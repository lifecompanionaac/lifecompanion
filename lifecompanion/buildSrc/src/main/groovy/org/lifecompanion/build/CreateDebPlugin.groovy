package org.lifecompanion.build

import org.gradle.api.Plugin
import org.gradle.api.Project

class CreateDebPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.tasks.register('createDeb', CreateDebTask) {
            dependsOn 'prepareOfflineApplication'
            group 'lifecompanion'
            description 'Create an offline deb package for LifeCompanion on Unix'
        }
    }
}