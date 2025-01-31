# `lc-phonecontrol-plugin` : Android app (`org.lifecompanion.phonecontrolapp`)

## Dev
You can use Android Studio if you have it. However, it takes a lot of space so you can just get started with CLI tools.  
This app doesn't have graphical elements nor an interface, its purpose is just to expose services/activities for us to call when needed.  
For this we will setup the dev environment on Windows and provide instructions for VS Code. Similar things have to be done for other distros and IDEs.

1. **Prepare VS Code**
    1. Follow the instructions in [BUILD_DETAIL.md](../../../docs/BUILD_DETAIL.md) to setup VS Code for the codebase AND to setup a working environment for working with plugins.
    2. Install the following extensions : `esafirm.kotlin-formatter` and `mathiasfrohlich.Kotlin`.
    3. Go on the three dots in the Extensions pane, "Install from VSIX" and install the `kotlin-0.3.0.vsix` file inside of `extra`. This is a custom compiled version of `fwcd.kotlin`, patched by [`@maksimr`](https://github.com/maksimr/vscode-kotlin) to work with latest versions of VS Code and fix some bugs, and patched by [`@EDM115`](https://github.com/EDM115) on top to fix an issue with spaces in the path. **Keep in mind** that VS Code may rollback this extension to a previous version, so you may have to reinstall it from time to time (mostly happens if you already had the extension installed prior to using the VSIX).
    4. In the `.vscode/settings.json` file, add the following lines :
        ```json
          "kotlin.compiler.jvm.target": "21",
          "kotlin.inlayHints.chainedHints": true,
          "kotlin.inlayHints.parameterHints": true,
        ```
        You also need to add `"lifecompanion-plugins/lc-phonecontrol-plugin/android",` to the array of the `gradle.nestedProjects` key for the gradle tasks to appear.
2. **Install the SDK**
    1. Create a new folder to hold the SDK (usually `C:\Android\`, however we don't agree with softwares creating their folders to the root. imagine if an app on linux created their folder at `/` ! This is why we wil go with `C:\Program Files\Android`, but you can change it to any folder, even on another drive).
    2. Download the command line tools from [here](https://developer.android.com/studio/index.html#command-line-tools-only). Extract it to the previously created folder so you have a directory structure like `C:\Program Files\Android\cmdline-tools\bin`.
    3. Open a PowerShell as admin in `C:\Program Files\Android\` and type the following :
        ```pwsh
        cmdline-tools/bin/sdkmanager --sdk_root="C:\Program Files\Android" --install "platform-tools" "build-tools;35.0.0" "platforms;android-26" "platforms;android-35"
        cmdline-tools/bin/sdkmanager --sdk_root="C:\Program Files\Android" --update
        ```
    4. Still in that PowerShell, type `systempropertiesadvanced`, click on Environment Variables, and add a new system variable named `ANDROID_HOME` with the value `C:\Program Files\Android`. Then double click on `PATH`, New, and add `%ANDROID_HOME%\platform-tools`, `%ANDROID_HOME%\cmdline-tools\bin` and `%ANDROID_HOME%\build-tools\35.0.0`.
3. **The final stretch**
    1. In `lifecompanion-plugins\lc-phonecontrol-plugin\android`, create a file called `local.properties` and add `sdk.dir=C:/Program Files/Android` inside (yes it's forward slashes).
    2. Restart VS Code. Once Gradle finished indexing the project, go the its pane and in `android > android > Tasks > other > assembleDebug`. You should see an artifact at `lifecompanion-plugins\lc-phonecontrol-plugin\android\app\build\outputs\apk\debug\app-debug.apk`. You'll see how to build the signed APK later on.
    3. IF VS Code still complains with squiggly lines in `.kt` files, copy the `.vscode` folder to `lifecompanion-plugins\lc-phonecontrol-plugin\android` and open that folder (<kbd>Ctrl</kbd>+<kbd>K</kbd>, <kbd>Ctrl</kbd>+<kbd>O</kbd>).

### Build
- For a quick build, you can use the `assembleDebug` task in the Gradle pane.  
- For a release build, you can use the `assembleRelease` task. However, you need to [sign the APK](https://developer.android.com/studio/publish/app-signing). Here's how to do it :

1. **Create a keystore**
    1. Open a PowerShell in `lifecompanion-plugins\lc-phonecontrol-plugin\android` and type the following :
        ```pwsh
        keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
        ```
    2. You will be prompted for 2 passwords (for the keystore and for the key alias itself), remember them. Fill in the required information.
    3. We highly recommend to move this file out of the repo and put it in its default location (`~\.keystore\`).
2. **Build and sign the APK**
    1. Open a PowerShell in `lifecompanion-plugins\lc-phonecontrol-plugin\android` and type the following :
        ```pwsh
        gradlew assembleRelease
        ```
    2. You will have an APK at `lifecompanion-plugins\lc-phonecontrol-plugin\android\app\build\outputs\apk\release\app-release-unsigned.apk`.
    3. Optimize the APK by running the following command :
        ```pwsh
        zipalign -v -p 4 app\build\outputs\apk\release\app-release-unsigned.apk app\build\outputs\apk\release\app-release-unsigned-aligned.apk
        ```
    4. Sign the APK by running the following command :
        ```pwsh
        apksigner sign --ks ~\.keystore\my-release-key.jks --out app\build\outputs\apk\release\app-release.apk app\build\outputs\apk\release\app-release-unsigned-aligned.apk
        ```
        You will be prompted for the keystore password and the key alias password.
    5. Verify the APK by running the following command :
        ```pwsh
        apksigner verify --verbose app\build\outputs\apk\release\app-release.apk
        ```
        The final APK is at `lifecompanion-plugins\lc-phonecontrol-plugin\android\app\build\outputs\apk\release\app-release.apk`.
3. **Build and sign the AAB (optional, for Play Store release)**
    1. Open a PowerShell in `lifecompanion-plugins\lc-phonecontrol-plugin\android` and type the following :
        ```pwsh
        gradlew bundleRelease
        ```
    2. You will have an AAB at `lifecompanion-plugins\lc-phonecontrol-plugin\android\app\build\outputs\bundle\release\app-release.aab`.
    3. Google will automatically optimize the AAB for you, so you don't need to run [`bundletool`](https://github.com/google/bundletool) on it.
    4. Sign the AAB by running the following command :
        ```pwsh
        jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 -keystore ~\.keystore\my-release-key.jks app\build\outputs\bundle\release\app-release.aab my-key-alias
        ```
        You will be prompted for the keystore password and the key alias password.
    5. Verify the AAB by running the following command :
        ```pwsh
        jarsigner -verify -verbose -certs app\build\outputs\bundle\release\app-release.aab
        ```
        The final AAB is at `lifecompanion-plugins\lc-phonecontrol-plugin\android\app\build\outputs\bundle\release\app-release.aab`.

#### Simplify the process : `keystore.properties`
You can create a `keystore.properties` file in `lifecompanion-plugins\lc-phonecontrol-plugin\android` with the following content :
```properties
storeFile=C:\Users\YourUserName\.keystore\my-release-key.jks
storePassword=your_keystore_password
keyAlias=my-key-alias
keyPassword=your_key_alias_password
```
Then, in your `build.gradle` file, add the following lines :
```gradle
def keystorePropertiesFile = rootProject.file("keystore.properties")
def keystoreProperties = new Properties()

if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(new FileInputStream(keystorePropertiesFile))
}

android {
    signingConfigs {
        release {
            keyAlias keystoreProperties['keyAlias']
            keyPassword keystoreProperties['keyPassword']
            storeFile file(keystoreProperties['storeFile'])
            storePassword keystoreProperties['storePassword']
        }
    }

    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```
You then only have to build the app (`gradlew assembleRelease` or `gradlew bundleRelease`) and verify it if you want (`apksigner verify --verbose app\build\outputs\apk\release\app-release.apk` or `jarsigner -verify -verbose -certs app\build\outputs\bundle\release\app-release.aab`).
