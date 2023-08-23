# LifeCompanion user APIs

## LifeCompanion launch

## LifeCompanion command line arguments

LifeCompanion can be launched using command line arguments to configure some of its features. Arguments should be unique and respect the expected parameters.

|Configuration|Param. example|Description|
|-|-|-|
|`-directLaunchOn profileId configurationId`|`4aab2626-6b72-4e5e-8318-777c3684e8a3 9e94f3c0-e2de-4afb-8b65-8b07a994b3d4`|Try to launch LifeCompanion directly in use mode on a given profile and configuration combination. Profile and configuration should have already been loaded in LifeCompanion on a previous launch.|
|`-directImportAndLaunch configurationFilePath`|`C:\lifecompanion\my-configuration.lcc`|Try to import a configuration file and launch it directly in use mode. The given configuration will not be added to profile. This can be useful to run LifeCompanion as a "configuration reader only"|
|`-disableSwitchToEditMode`|*`NONE`*|Disable the switch to edit mode when the use mode is launched. This will hide the edit mode button and disable keyboard shortcuts or any action that could cause a switch to edit mode. Note that this doesn't disable the edit mode itself : on the first launch, LifeCompanion can be used in edit mode.|
|`-disableUpdates`|*`NONE`*|Will disable all the update checking process (for both app and plugins). Will not try to reach the update server at all.|
|`-disableVirtualKeyboard`|*`NONE`*|Disable every virtual keyboard mechanism, if enabled, will consider any configuration as a classic configuration even if the virtual keyboard parameter is enabled on it.|
|`-disableVirtualMouse`|*`NONE`*|Disable every virtual mouse mechanism, if enabled, will ignore any actions that could enable/show the virtual mouse.|
|`-disableFullscreen`|*`NONE`*|Disable the user ability to switch from decorated/fullscreen mode on the use mode window. Will disable the fullscreen button, but also the keyboard shortcut|
|`-forceWindowUndecorated`|*`NONE`*|Will force the use mode window to be "undecorated" as stated in [JavaFX documentation](https://openjfx.io/javadoc/18/javafx.graphics/javafx/stage/Stage.html) on stage style. |
|`-forceWindowSize width height`|`1200 800`|Will force the use mode window to be as the specified size (in pixel). The given size will respect the screen scaling. The user will then not be able to resize the use mode window.|
|`-forceWindowLocation x y`|`0 0`|Will force the use mode window to be at a specific location on the screen (in pixel, from top left corner). The given location will respect the screen scaling.|
|`-forceWindowOpacity opacity`|`0.8`|Will force the use mode window to keep a specific opacity regardless the configuration set on it for its opacity. Opacity should range between 0.0 (transparent) to 1.0 (opaque).|
|`-enableApiServer`|*`NONE`*|Will enable the API server to control LifeCompanion while running. To get details on control feature, check the "LifeCompanion control API" part of documentation.API server will run on its default port (8646) if enable expect if the port is specific with its own parameter.|
|`-apiServerPort port`|`8080`|The port for the API server to run. Will be ignored if the API server is not enabled (check the parameter above to enable it). If not specified, server will run on its default port.|
|`-updateDownloadFinished`|*`NONE`*|Inform LifeCompanion that the update download was finished on last LifeCompanion use. When launched with the arg, LifeCompanion will try to install the newly downloaded update and restart itself.|
|`-updateFinished`|*`NONE`*|Inform LifeCompanion that the update installation was done on the previous launch. Typically, this arg is added on LifeCompanion restart after update installation.|
|`-enablePreviewUpdates`|*`NONE`*|Enable LifeCompanion preview updates. This can be useful to test update before their production version to be ready.|

## LifeCompanion JVM properties

LifeCompanion can be configured using some JVM properties. As every properties in Java, they should be configured before argument with the **-D** syntax.
You can check [lc-app/build.gradle](../lifecompanion/lc-app/build.gradle) for an usage example (in *applicationDefaultJvmArgs*).

|Configuration|Param. example|Description|
|-|-|-|
|`-Dorg.lifecompanion.dev.mode`|*`NONE`*|A general configuration that can be used to check if we are running LifeCompanion in a dev context.This can be useful to add currently developed feature with this check, this will secure for an unfinished feature to be pushed in production.|
|`-Dorg.lifecompanion.disable.updates`|*`NONE`*|Will skip update checking on each LifeCompanion run (for app and plugins)|
|`-Dorg.lifecompanion.load.plugins.from.cp`|*`NONE`*|When enabled, will try to load plugins from classpath instead of the classpath configuration file. This is useful to make the plugin dev easier.|
|`-Dorg.lifecompanion.debug.loaded.images`|*`NONE`*|When enabled, a checking Thread is launched in background to display the loaded image count. This can be useful to detect memory leaks on images. See [`ImageDictionaries#startImageLoadingDebug()`](../lifecompanion/lc-app/src/main/java/org/lifecompanion/model/impl/imagedictionary/ImageDictionaries.java) for details|
|`-Dorg.lifecompanion.debug.loaded.configuration`|*`NONE`*|When enabled, a checking Thread is launched in background to display the loaded configuration count. This can be useful to detect memory leaks on configuration (for example, if a configuration is not released on configuration changed). See [`ConfigurationMemoryLeakChecker`](../lifecompanion/lc-app/src/main/java/org/lifecompanion/util/debug/ConfigurationMemoryLeakChecker.java) for details|

## Command line arguments and properties use from LifeCompanion

In Java code, values are tested using `GlobalRuntimeConfigurationController` regardless they are a command line arg or a JVM property. Elements without parameters are tested only on their presence with :

```java
if(GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.UPDATE_FINISHED)){
  // ...
}
```

When values should be used, other methods can be used :

```java
// Get the first value and second value (ordered just after the parameter)
String val1 = GlobalRuntimeConfigurationController.INSTANCE.getParameters(GlobalRuntimeConfiguration.DIRECT_LAUNCH_CONFIGURATION).get(0);
String val2 = GlobalRuntimeConfigurationController.INSTANCE.getParameters(GlobalRuntimeConfiguration.DIRECT_LAUNCH_CONFIGURATION).get(1);

// Or get directly the first value with
String firstVal = GlobalRuntimeConfigurationController.INSTANCE.getParameter(GlobalRuntimeConfiguration.DIRECT_LAUNCH_CONFIGURATION);
```

## LifeCompanion control API

LifeCompanion control API server allow you to control LifeCompanion while runing by calling specific HTTP services on localhost (or from a distant if port are opened). If not specified, the LifeCompanion control server is running on port 8646, but you can specify this with `-apiServerPort 8080` command line argument.

**Note that server will be running only if LifeCompanion is launched using `-enableApiServer` argument.**