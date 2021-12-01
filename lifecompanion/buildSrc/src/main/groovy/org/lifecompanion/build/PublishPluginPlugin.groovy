package org.lifecompanion.build

import org.gradle.api.Plugin
import org.gradle.api.Project

class PublishPluginPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.task('publishPlugin', type: PublishPluginTask) {
            dependsOn 'clean'
            dependsOn 'jar'
            project.tasks.findByName('jar').mustRunAfter 'clean'
            group 'lifecompanion'
            description 'Clean, build and publish a plugin update'
        }
    }
}