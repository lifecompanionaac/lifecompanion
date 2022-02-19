package org.lifecompanion.build

import org.gradle.api.Plugin
import org.gradle.api.Project

class PublishApplicationPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.task('publishApplication', type: PublishApplicationTask) {
            dependsOn 'clean'
            dependsOn ':lc-app-launcher:prepareLaunchers'
            dependsOn 'jlink'
            project.tasks.findByName('jlink').mustRunAfter 'clean'
            group 'lifecompanion'
            description 'Clean, build and publish an application update for all systems'
        }
    }
}