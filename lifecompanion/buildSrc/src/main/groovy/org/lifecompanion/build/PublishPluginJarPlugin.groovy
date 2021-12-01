package org.lifecompanion.build

import org.gradle.api.Plugin
import org.gradle.api.Project

class PublishPluginJarPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.task('publishPluginJar', type: PublishPluginJarTask) {
            group 'lifecompanion'
            description 'Try to publish a given plugin jar'
        }
    }
}