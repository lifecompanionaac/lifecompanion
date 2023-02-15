# Build detailed instructions

*While it was tested on a fresh Ubuntu 22.04.1 LTS most of this should works on others plateforms (including Windows if commands are adapted)*

## Tutorial

### Install and run LifeCompanion

1. Install Unix version, [download it here](https://lifecompanionaac.herokuapp.com/public/installer/lifecompanion/unix) and [read the documentation](https://lifecompanionaac.org/categories/documentations/complement-sur-linstallation-de-life-companion) if needed, leave default directory configuration
1. Try to run it, it should directly works (it apperas in available apps)
1. Install git `sudo apt-get install git`
1. Download [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/download/#section=linux)
1. Install it in **~/dev/** : just extract the zip
1. Open a terminal
    1. `cd ~/dev`
    1. `mkdir workspace && cd workspace`
    1. Check last app release in prod on [release page](https://github.com/lifecompanionaac/lifecompanion/tags) : search for *lifecompanion/lc-app/x.x.x-prod* tag, let take *lifecompanion/lc-app/1.4.4-prod* for this example
    1. Clone repo with `git clone https://github.com/lifecompanionaac/lifecompanion.git --branch lifecompanion/lc-app/1.4.4-prod` (replace the tag with the last production version)
1. Run IntelliJ (in its installation directory or add it to the path): `bin/idea.sh`
    1. Click on "Open"
    1. Select **~/dev/workspace/lifecompanion/lifecompanion-framework**
    1. Let the IDE initialize (downloading Gradle, JDK, dependencies, indexing...)
    1. In Gradle part of the IDE (right side), you can run **lifecompanion-framework > Tasks > publishing > publishToMavenLocal**, build should be successful
    1. Click "Menu > New > Module from Existing Sources..."
    1. Select **~/dev/workspace/lifecompanion/lifecompanion**
    1. Select "Import module from external model" and "Gradle", then click "Create"
1. Before running LifeCompanion, prepare
    1. Add data : `cp -r ~/LifeCompanion/data/ ~/dev/workspace/lifecompanion/lifecompanion/lc-app/data`
    1. Set configuration : `cp ~/dev/workspace/lifecompanion/res/default-data/installation.properties ~/dev/workspace/lifecompanion/lifecompanion/lc-app/data/installation.properties`
    1. Edit the configuration `gedit ~/dev/workspace/lifecompanion/lifecompanion/lc-app/data/installation.properties`
    1. Set the line with **userDataDirectory** to `userDataDirectory=data/user-data`
1. In IntelliJ
    1. In Gralde part
    1. Run LifeCompanion with **lifecompanion > lc-app > application > run**
    1. LifeCompanion is now running !

### Install and run a plugin

**The steps above should be done and working before adding a plugin.**

The example is done with **lc-spellgame-plugin** but should work with any plugin.

1. In IntelliJ
1. Create LifeCompanion API
    1. In Gradle part of the IDE (right side), you can run **lifecompanion > Tasks > publishing > publishToMavenLocal**, build should be successful
1. Create the plugin jar
    1. Click "Menu > New > Module from Existing Sources..."
    1. Select **~/dev/workspace/lifecompanion/lifecompanion-plugins/lc-spellgame-plugin**
    1. Select "Import module from external model" and "Gradle", then click "Create"
    1. Check that **~/dev/workspace/lifecompanion/lifecompanion-plugins/lc-spellgame-plugin/gradle.properties** has the same LifeCompanion version number in, for example `lifecompanionAppVersion=1.4.4` (this should match the selected version tag)
    1. In Gradle part of the IDE (right side), you can run **lc-spellgame-plugin > Tasks > build > jar**, build should be successful
1. Create your dev workflow
    1. In run configuration selector (top right part), click on "Edit Configurations"
    1. You should have a configuration named **lifecompanion:lc-app [run]** and another named **lc-spellgame-plugin [jar]**
    1. Select **lifecompanion:lc-app [run]**
    1. Click on "Modify options" > "Add before launch task" > "Run Another Configuration" and set "lc-spellgame-plugin [jar]"
    1. In "Environment variables" field, add "org.lifecompanion.dev.cp.arg=../../lifecompanion-plugins/lc-spellgame-plugin/build/libs/*"
    1. You can now run **lifecompanion:lc-app [run]** configuration, LifeCompanion is now running with your plugin !

## Troubleshooting

**[Check general docs troubleshooting](BUILD.md#troubleshooting)**