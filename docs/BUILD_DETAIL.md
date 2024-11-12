# Detailed build instructions

> [!NOTE]  
> While it was tested on a fresh Ubuntu 22.04.1 LTS most of this should work on others platforms  
> You will find here instructions to setup the dev environment for both Unix and Windows, and for both IntelliJ IDEA (recommended) and VS Code. These instructions are interchangable between softwares and platforms

## Tutorial

### Install and run LifeCompanion

#### Unix, IntelliJ IDEA

1. Install Unix version, [download it here](https://lifecompanionaac.herokuapp.com/public/installer/lifecompanion/unix) and [read the documentation](https://lifecompanionaac.org/categories/documentations/complement-sur-linstallation-de-life-companion) if needed, leave default directory configuration
1. Try to run it, it should directly works (it appears in available apps)
1. Install git `sudo apt-get install git`
1. Download [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/download/#section=linux)
1. Install it in **~/dev/** : just extract the zip
1. Open a terminal
    1. `cd ~/dev`
    1. `mkdir workspace && cd workspace`
    1. Check last app release in prod on [release page](https://github.com/lifecompanionaac/lifecompanion/tags) : search for *lifecompanion/lc-app/x.x.x-prod* tag, let's take *lifecompanion/lc-app/1.6.5-prod* for this example
    1. Clone repo with `git clone https://github.com/lifecompanionaac/lifecompanion.git --branch lifecompanion/lc-app/1.6.5-prod` (replace the tag with the last production version)
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
    1. Avoid committing the settings with `git update-index --skip-worktree ~/dev/workspace/lifecompanion/lifecompanion/lc-app/data/installation.properties`
1. In IntelliJ
    1. In Gradle part
    1. Run LifeCompanion with **lifecompanion > lc-app > application > run**
    1. LifeCompanion is now running !

#### Windows, VS Code

1. Install Windows version, [download it here](https://lifecompanionaac.herokuapp.com/public/installer/lifecompanion/windows) and [read the documentation](https://lifecompanionaac.org/categories/documentations/complement-sur-linstallation-de-life-companion) if needed, leave default directory configuration
1. Try to run it, it should directly works (it appears in available apps)
1. Install git [from here](https://git-scm.com/download/win)
1. Download [VS Code](https://code.visualstudio.com/download)
1. Open a terminal in your documents (latest PowerShell is recommended for tilde expansion and ease of running commands, either install with `winget install --id Microsoft.PowerShell --source winget` or get the MSI from [here](https://learn.microsoft.com/en-us/powershell/scripting/install/installing-powershell-on-windows#installing-the-msi-package); Windows Terminal is also recommended since it can hold multiple tabs, usually preinstalled, if not do <kbd>Win</kbd>+<kbd>R</kbd>
and paste `ms-windows-store://pdp/?ProductId=9n0dx20hk701`)
    1. Check latest app release in prod on the [release page](https://github.com/lifecompanionaac/lifecompanion/tags) : search for *lifecompanion/lc-app/x.x.x-prod* tag, let's take *lifecompanion/lc-app/1.6.5-prod* for this example
    1. Clone the repo with `git clone https://github.com/lifecompanionaac/lifecompanion.git --branch lifecompanion/lc-app/1.6.5-prod` (replace the tag with the last production version)
1. In VS Code
    1. Do <kbd>Ctrl</kbd>+<kbd>K</kbd>, <kbd>Ctrl</kbd>+<kbd>O</kbd> and select **~/Documents/lifecompanion**
    1. At the root of the project, create a folder called `.vscode`. Inside create 2 files, one called `settings.json` and the other called `extensions.json`. Paste the content shown just after. Then in the Extensions pane click on the three dots and "Install recommended extensions", and once done click on "Restart Extensions" to load them. Some tasks will run (check the Status Bar), let them finsh before working on the project
    1. In the Gradle pane, click on **lifecompanion-framework > lifecompanion-framework > Tasks > publishing > publishToMavenLocal**, build should be successful
1. Before running LifeCompanion, prepare
    1. Copy the app data from the installation directory (defaults to `C:\ProgramData\LifeCompanion\data`) to **~/Documents/lifecompanion/lifecompanion/lc-app/data**, replace files if needed
    1. Avoid committing the settings with `git update-index --skip-worktree ~/Documents/lifecompanion/lifecompanion/lc-app/data/installation.properties`
1. In VS Code
    1. In the Gradle pane, run LifeCompanion with **lifecompanion > lc-app > Tasks > application > run**
    1. LifeCompanion is now running !

<details>
<summary><code>settings.json</code></summary>

```json
{
  "[java]": {
    "editor.defaultFormatter": "redhat.java",
    "editor.suggest.snippetsPreventQuickSuggestions": false
  },
  "[json]": {
    "editor.defaultFormatter": "vscode.json-language-features"
  },
  "[markdown]": {
    "editor.defaultFormatter": "yzhang.markdown-all-in-one"
  },
  "diffEditor.experimental.useTrueInlineView": true,
  "diffEditor.hideUnchangedRegions.enabled": true,
  "diffEditor.maxComputationTime": 0,
  "editor.experimentalEditContextEnabled": true,
  "editor.formatOnPaste": false,
  "editor.formatOnSave": false,
  "editor.formatOnType": false,
  "editor.inlayHints.maximumLength": 0,
  "editor.inlineSuggest.enabled": true,
  "editor.inlineSuggest.showToolbar": "always",
  "editor.largeFileOptimizations": false,
  "editor.linkedEditing": true,
  "editor.maxTokenizationLineLength": 1000000,
  "editor.mouseWheelZoom": true,
  "editor.multiCursorModifier": "ctrlCmd",
  "editor.occurrencesHighlightDelay": 100,
  "editor.stickyTabStops": true,
  "editor.suggestSelection": "first",
  "editor.tabSize": 4,
  "editor.wordWrap": "on",
  "explorer.autoOpenDroppedFile": false,
  "explorer.compactFolders": false,
  "explorer.confirmDelete": false,
  "explorer.confirmDragAndDrop": false,
  "explorer.confirmPasteNative": false,
  "extensions.ignoreRecommendations": false,
  "files.autoSave": "afterDelay",
  "files.eol": "\n",
  "files.insertFinalNewline": true,
  "fixJson.indentationSpaces": 2,
  "git.allowForcePush": false,
  "git.autofetch": true,
  "git.confirmSync": false,
  "git.defaultBranchName": "main",
  "git.enableSmartCommit": true,
  "git.openRepositoryInParentFolders": "never",
  "gradle.autoDetect": "on",
  "gradle.nestedProjects": [
    "lifecompanion",
    "lifecompanion-framework"
  ],
  "gremlins.showInProblemPane": true,
  "java.autobuild.enabled": false,
  "java.codeGeneration.generateComments": true,
  "java.codeGeneration.useBlocks": true,
  "java.compile.nullAnalysis.mode": "automatic",
  "java.completion.chain.enabled": true,
  "java.completion.guessMethodArguments": "insertParameterNames",
  "java.configuration.updateBuildConfiguration": "automatic",
  "java.debug.settings.showQualifiedNames": true,
  "java.debug.settings.showStaticVariables": true,
  "java.dependency.packagePresentation": "hierarchical",
  "java.diagnostic.filter": [
    "**/.git",
    "**/aac4all-wp2-plugin",
    "**/lc-calendar-plugin",
    "**/lc-caa-ai-plugin",
    "**/lc-email-plugin",
    "**/lc-flirc-plugin",
    "**/lc-homeassistant-plugin",
    "**/lc-phonecontrol-plugin",
    "**/lc-ppp-plugin",
    "**/lc-predict4all-evaluation-plugin",
    "**/lc-spellgame-plugin"
  ],
  "java.edit.smartSemicolonDetection.enabled": true,
  "java.help.showReleaseNotes": false,
  "java.implementationsCodeLens.enabled": true,
  "java.inlayHints.parameterNames.enabled": "all",
  "java.quickfix.showAt": "problem",
  "java.referencesCodeLens.enabled": true,
  "java.saveActions.organizeImports": false,
  "java.signatureHelp.description.enabled": true,
  "java.symbols.includeSourceMethodDeclarations": true,
  "java.trace.server": "messages",
  "markdown-preview-enhanced.codeBlockTheme": "darcula.css",
  "markdown-preview-enhanced.enableExtendedTableSyntax": true,
  "markdown-preview-enhanced.enableHTML5Embed": true,
  "markdown-preview-enhanced.enableScriptExecution": true,
  "markdown-preview-enhanced.enableTypographer": true,
  "markdown-preview-enhanced.mermaidTheme": "dark",
  "markdown-preview-enhanced.previewTheme": "github-dark.css",
  "markdown-preview-enhanced.revealjsTheme": "solarized.css",
  "markdown.extension.print.absoluteImgPath": false,
  "markdown.extension.print.theme": "dark",
  "markdown.extension.toc.updateOnSave": false,
  "path-intellisense.autoSlashAfterDirectory": true,
  "path-intellisense.autoTriggerNextSuggestion": true,
  "path-intellisense.showHiddenFiles": true,
  "redhat.telemetry.enabled": false,
  "scm.defaultViewMode": "tree",
  "scm.graph.badges": "all",
  "scm.inputFontFamily": "editor",
  "scm.workingSets.enabled": true,
  "search.defaultViewMode": "tree",
  "search.quickAccess.preserveInput": true,
  "task.allowAutomaticTasks": "on",
  "terminal.integrated.accessibleViewPreserveCursorPosition": true,
  "terminal.integrated.cursorBlinking": true,
  "terminal.integrated.enableImages": true,
  "terminal.integrated.enableMultiLinePasteWarning": "never",
  "terminal.integrated.hideOnStartup": "always",
  "terminal.integrated.middleClickBehavior": "paste",
  "terminal.integrated.minimumContrastRatio": 1,
  "terminal.integrated.mouseWheelZoom": true,
  "terminal.integrated.rightClickBehavior": "copyPaste",
  "terminal.integrated.smoothScrolling": true,
  "terminal.integrated.suggest.enabled": true,
  "terminal.integrated.tabs.defaultColor": "terminal.ansiGreen",
  "workbench.editor.alwaysShowEditorActions": true
}
```

</details>
<details>
<summary><code>extensions.json</code></summary>

```json
{
  "recommendations": [
    "aaron-bond.better-comments",
    "atishay-jain.all-autocomplete",
    "christian-kohler.path-intellisense",
    "donjayamanne.githistory",
    "ecmel.vscode-html-css",
    "formulahendry.auto-rename-tag",
    "ibm.output-colorizer",
    "kisstkondoros.vscode-gutter-preview",
    "meezilla.json",
    "mhutchie.git-graph",
    "mikestead.dotenv",
    "naco-siren.gradle-language",
    "nhoizey.gremlins",
    "oliversturm.fix-json",
    "pranaygp.vscode-css-peek",
    "redhat.java",
    "redhat.vscode-xml",
    "shd101wyy.markdown-preview-enhanced",
    "visualstudioexptteam.intellicode-api-usage-examples",
    "visualstudioexptteam.vscodeintellicode",
    "vscjava.vscode-gradle",
    "vscjava.vscode-java-debug",
    "vscjava.vscode-java-dependency",
    "vscjava.vscode-java-test",
    "vscjava.vscode-maven",
    "yzhang.markdown-all-in-one",
    "zignd.html-css-class-completion"
  ]
}
```

</details>

> [!WARNING]  
> At the time of writing this, the `Gradle for Java` extension by Microsoft have an issue that prevents it from working correctly with this project. If after installing it the `lifecompanion` project doesn't appear in its pane, go in extensions, right click on it and "Install Specific Version", and choose the `v3.15.0` (last known working version).  
> In such case, keep in mind to **not** upgrade this extension.

### Install and run a plugin

**The steps above should be done and working before adding a plugin.**

The example is done with **lc-spellgame-plugin** but should work with any plugin.

#### IntelliJ

1. Create LifeCompanion API
    1. In Gradle part of the IDE (right side), you can run **lifecompanion > Tasks > publishing > publishToMavenLocal**, build should be successful
1. Create the plugin jar
    1. Click "Menu > New > Module from Existing Sources..."
    1. Select **~/dev/workspace/lifecompanion/lifecompanion-plugins/lc-spellgame-plugin**
    1. Select "Import module from external model" and "Gradle", then click "Create"
    1. Check that **~/dev/workspace/lifecompanion/lifecompanion-plugins/lc-spellgame-plugin/gradle.properties** has the same LifeCompanion version number in, for example `lifecompanionAppVersion=1.6.5` (this should match the selected version tag)
    1. In Gradle part of the IDE (right side), you can run **lc-spellgame-plugin > Tasks > build > jar**, build should be successful
1. Create your dev workflow
    1. In run configuration selector (top right part), click on "Edit Configurations"
    1. You should have a configuration named **lifecompanion:lc-app [run]** and another named **lc-spellgame-plugin [jar]**
    1. Select **lifecompanion:lc-app [run]**
    1. Click on "Modify options" > "Add before launch task" > "Run Another Configuration" and set "lc-spellgame-plugin [jar]"
    1. In "Environment variables" field, add "org.lifecompanion.dev.cp.arg=../../lifecompanion-plugins/lc-spellgame-plugin/build/libs/*"
    1. You can now run **lifecompanion:lc-app [run]** configuration, LifeCompanion is now running with your plugin !

#### VS Code

1. Create LifeCompanion API
    1. In the Gradle pane, run **lifecompanion > lifecompanion > Tasks > publishing > publishToMavenLocal**, build should be successful
1. Create the plugin jar
    1. You have 2 keys to change in `.vscode/settings.json`
       1. `gradle.nestedProjects` should contain also the path of the plugin you're going to develop, let's say here `lifecompanion-plugins/lc-spellgame-plugin`. Keep `lifecompanion` and `lifecompanion-framework`. This is to avoid gradle taking time to be ready and build projects you're not working with
       1. `java.diagnostic.filter` should have all the paths of the folders you're NOT working with. This is to avoid having the Java extension reportig issues for projects you're not working with. The default config above already exclude all plugins, so here we just have to remove `**/lc-spellgame-plugin`
    1. Check that **~/Documents/lifecompanion/lifecompanion-plugins/lc-spellgame-plugin/gradle.properties** has the same LifeCompanion version number in, for example `lifecompanionAppVersion=1.6.5` (this should match the selected version tag)
    1. In the Gradle pane, run **lc-spellgame-plugin > Tasks > build > jar**, build should be successful
1. Create your dev workflow
    1. We have to set an environment variable to tell LifeCompanion where to fetch the freshly created jar. Since the Gradle extension doesn't handle it yet (check https://github.com/microsoft/vscode-gradle/issues/1624), you have to set it yourself.
       1. Open a PowerShell in **~/Documents/lifecompanion/lifecompanion**, and type `[System.Environment]::SetEnvironmentVariable("org.lifecompanion.dev.cp.arg", "../../lifecompanion-plugins/lc-spellgame-plugin/build/libs/*", "User")` (if you work on multiple plugins, separate their path with a semicolon)
       1. Run `./gradlew.bat --stop && pwsh`. This ensures that the env var is indeed loaded correctly
    1. You can now run **lifecompanion > lc-app > Tasks > application > run**, LifeCompanion is now running with your plugin !
       1. You just have to redo the `jar` task every time you need it (and eventually change the env var if you switch project, note that it stays persistent between restarts), and then the `run` task
       1. If your plugin doesn't appear, in the Gradle pane click on "Gradle Daemons" at the bottom and the red button to stop the existing processes. If it still doen't work, open a PowerShell in **~/Documents/lifecompanion/lifecompanion** and type `./gradlew.bat :lc-app:run`

## Troubleshooting

**[Check general docs troubleshooting](BUILD.md#troubleshooting)**
