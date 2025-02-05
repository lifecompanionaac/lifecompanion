package org.lifecompanion.build

import org.gradle.api.Plugin
import org.gradle.api.Project

class PublishApplicationPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.tasks.register('prepareOfflineApplication', PublishApplicationTask) {
            dependsOn 'clean'
            dependsOn ':lc-app-launcher:prepareLaunchers'
            dependsOn 'jlink'
            project.tasks.findByName('jlink').mustRunAfter 'clean'
            offline = true
            group 'lifecompanion'
            description 'Create an offline installation of the app for all systems'
        }
        project.tasks.register('publishApplication', PublishApplicationTask) {
            dependsOn 'clean'
            dependsOn ':lc-app-launcher:prepareLaunchers'
            dependsOn 'jlink'
            project.tasks.findByName('jlink').mustRunAfter 'clean'
            offline = false
            group 'lifecompanion'
            description 'Clean, build and publish an application update for all systems'
        }
        project.tasks.register('prepareOfflineInstaller', PrepareOfflineInstallerTask) {
            dependsOn 'prepareOfflineApplication'
            system = 'WINDOWS'
            description 'Prepare the offline installer task (copy offline to installer resources)'
        }
        project.tasks.register('createWindowsOfflineInstaller') {
            dependsOn 'prepareOfflineInstaller'
            dependsOn ':lc-installer:createInnoSetupPackage'
            group 'lifecompanion'
            description 'Clean and create a full windows offline installer (will use prepareOfflineApplication task)'
        }
    }
}