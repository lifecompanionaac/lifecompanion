package org.lifecompanion.build

import org.gradle.api.Plugin
import org.gradle.api.Project

class PublishInstallerPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.task('publishInstaller') {
            dependsOn 'clean'
            dependsOn 'publishWindowsInstaller'
            dependsOn 'publishUnixInstaller'
            dependsOn 'publishMacInstaller'
            group 'lifecompanion'
            description 'Clean, build and publish a installer update for all systems'
        }
    }
}