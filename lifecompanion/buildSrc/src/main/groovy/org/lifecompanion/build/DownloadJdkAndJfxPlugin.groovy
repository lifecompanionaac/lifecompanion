package org.lifecompanion.build

import org.gradle.api.Plugin
import org.gradle.api.Project

class DownloadJdkAndJfxPlugin implements Plugin<Project> {
    void apply(Project project) {
        for (DownloadJdkAndJfxTask.DestPlatform platform : DownloadJdkAndJfxTask.DestPlatform.values()) {
            project.ext.set("jdk_" + platform.getId(), platform.jdkPathToInject)
            project.ext.set("jfx_" + platform.getId(), platform.jfxPathToInject)
        }
        project.task('downloadJdkAndJfx', type: DownloadJdkAndJfxTask) {
            group 'lifecompanion'
            description 'Download JDK and JFX from latest sources (only when not already on the system)'
        }
        project.tasks.jlink.dependsOn('downloadJdkAndJfx')

    }
}