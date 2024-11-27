## phonecontrol-plugin : Android app

### Dev

You can use Android Studio if you have it. However, it takes a lot of space so you can just get started with CLI tools. For this we will setup the dev environment on Windows and provide instructions for VS Code. Similar things have to be done for other distros and IDEs.

1. **Prepare VS Code**
    1. Follow the instructions in [BUILD_DETAIL.md](../../../docs/BUILD_DETAIL.md) to setup VS Code for the codebase AND to setup a working environment for working with plugins.
    2. Install the following extensions : `esafirm.kotlin-formatter` and `mathiasfrohlich.Kotlin`.
    3. Go on the three dots in the Extensions pane, "Install from VSIX" and install the `kotlin-0.3.0.vsix` file inside of `extra`. This is a custom compiled version of `fwcd.kotlin`, patched by `@maksimr` to work with latest versions of VS Code and fix some bugs, and patched by `@EDM115` on top to fix an issue with spaces in the path. **Keep in mind** that VS Code may rollback this extension to a previous version, so you may have to reinstall it from time to time.
    4. In the `.vscode/settings.json` file, add the following lines :
        ```json
          "kotlin.compiler.jvm.target": "1.8",
          "kotlin.inlayHints.chainedHints": true,
          "kotlin.inlayHints.parameterHints": true,
        ```
        You also need to add `"lifecompanion-plugins/lc-phonecontrol-plugin-/android",` to the array of the `gradle.nestedProjects` key for the gradle tasks to appear.
2. **Install the SDK**
    1. Create a new folder to hold the SDK (usually `C:\Android\`, however we don't agree with softwares creating their folders to the root. imagine if an app on linux created their folder at `/` ! This is why we wil go with `C:\Program Files\Android`, but you can change it to any folder, even on another drive).
    2. Download the command line tools from [here](https://developer.android.com/studio/index.html#command-line-tools-only). Extract it to the previously created folder so you have a directory structure like `C:\Program Files\Android\cmdline-tools\bin`.
    3. Open a PowerShell in `C:\Program Files\Android\` and type the following :
        ```pwsh
        cmdline-tools/bin/sdkmanager --sdk_root="C:\Program Files\Android" --update
        cmdline-tools/bin/sdkmanager --sdk_root="C:\Program Files\Android" "platform-tools" "platforms;android-34" "build-tools;34.0.0"
        cmdline-tools/bin/sdkmanager --sdk_root="C:\Program Files\Android" "platforms;android-31"
        cmdline-tools/bin/sdkmanager --sdk_root="C:\Program Files\Android" --update
        ```
    4. Still in that PowerShell, type `systempropertiesadvanced`, click on Environment Variables, and add a new system variable named `ANDROID_HOME` with the value `C:\Program Files\Android`. Then double click on `PATH`, New, and add `%ANDROID_HOME%\platform-tools` and `%ANDROID_HOME%\cmdline-tools\bin`.
3. **The final stretch**
    1. In `lifecompanion-plugins\lc-phonecontrol-plugin\android`, create a file called `local.properties` and add `sdk.dir=C:/Program Files/Android` inside (yes it's forward slashes).
    2. Restart VS Code. Once Gradle finished indexing the project, go the its pane and in `android > android > Tasks > other > assembleDebug`. You should see an artifact at `lifecompanion-plugins\lc-phonecontrol-plugin\android\app\build\outputs\apk\debug\app-debug.apk`. You'll see how to build the signed APK later on.
    3. IF VS Code still complains with squiggly lines in `.kt` files, copy the `.vscode` folder to `lifecompanion-plugins\lc-phonecontrol-plugin\android` and open that folder (<kbd>Ctrl</kbd>+<kbd>K</kbd>, <kbd>Ctrl</kbd>+<kbd>O</kbd>).

### Build
