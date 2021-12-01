package org.lifecompanion.build

import org.gradle.api.Plugin
import org.gradle.api.Project

class PublishLauncherPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.task('publishLauncher') {
            dependsOn 'clean'
            dependsOn 'publishWindowsLauncher'
            dependsOn 'publishUnixLauncher'
            dependsOn 'publishMacLauncher'
            group 'lifecompanion'
            description 'Clean, build and publish a launcher update for all system'
        }
    }

}