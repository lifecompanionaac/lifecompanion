# LifeCompanion user APIs

## LifeCompanion launch

## LifeCompanion command line arguments

LifeCompanion can be launched using command line arguments to configure some of its features. Arguments should be unique and respect the expected parameters.

|Argument|Parameters|Description|
|-|-|-|
|`-directLaunchOn profileId configurationId`|`4aab2626-6b72-4e5e-8318-777c3684e8a3 9e94f3c0-e2de-4afb-8b65-8b07a994b3d4`|Try to launch LifeCompanion directly in use mode on a given profile and configuration combination. Profile and configuration should have already been loaded in LifeCompanion on a previous launch.|
|`-directImportAndLaunch configurationFilePath`|`C:\lifecompanion\my-configuration.lcc`|Try to import a configuration file and launch it directly in use mode. The given configuration will not be added to profile. This can be useful to run LifeCompanion as a "configuration reader only"|
|`-disableSwitchToEditMode`|*`NONE`*|Disable the switch to edit mode when the use mode is launched. This will hide the edit mode button and disable keyboard shortcuts or any action that could cause a switch to edit mode. Note that this doesn't disable the edit mode itself : on the first launch, LifeCompanion can be used in edit mode.|



## LifeCompanion control API

## LifeCompanion JVM properties

LifeCompanion can be configured using some JVM properties. As every properties in Java, they should be configured before argument with the **-D** syntax.
You can check [lc-app/build.gradle](../lifecompanion/lc-app/build.gradle) for an usage example (in *applicationDefaultJvmArgs*).

In Java code, properties are tested with `LangUtils.safeParseBoolean(System.getProperty("org.lifecompanion.debug.dev.env"))`.

|Property|Possible values|Description|
|-|-|-|
|`-Dorg.lifecompanion.debug.dev.env`|`true`, `false`, *`[EMPTY]`*|A general configuration that can be used to check if we are running LifeCompanion in a dev context. This can be useful to add currently developed feature with this check, this will secure for an unfinished feature to be pushed in production.|
|`-Dorg.lifecompanion.debug.skip.update.check`|`true`, `false`, *`[EMPTY]`*|Will skip update checking on each LifeCompanion run (for app and plugins)|
|`-Dorg.lifecompanion.debug.load.plugins.from.cp`|`true`, `false`, *`[EMPTY]`*|When enabled, will try to load plugins from classpath instead of the classpath configuration file. This is useful to make the plugin dev easier.|
|`-Dorg.lifecompanion.debug.loaded.images`|`true`, `false`, *`[EMPTY]`*|When enabled, a checking Thread is launched in background to display the loaded image count. This can be useful to detect memory leaks on images. See [`ImageDictionaries#startImageLoadingDebug()`](../lifecompanion/lc-app/src/main/java/org/lifecompanion/model/impl/imagedictionary/ImageDictionaries.java) for details|
|`-Dorg.lifecompanion.debug.configuration.memory.leak`|`true`, `false`, *`[EMPTY]`*|When enabled, a checking Thread is launched in background to display the loaded configuration count. This can be useful to detect memory leaks on configuration (for example, if a configuration is not released on configuration changed). See [`ConfigurationMemoryLeakChecker`](../lifecompanion/lc-app/src/main/java/org/lifecompanion/util/debug/ConfigurationMemoryLeakChecker.java) for details|