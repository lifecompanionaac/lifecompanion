# LifeCompanion user APIs

## LifeCompanion launch

## LifeCompanion args

## LifeCompanion control API

## LifeCompanion JVM properties

These properties should be added directly to java command. They have been developed to be used directly in dev mode, see [lc-app/build.gradle](../lifecompanion/lc-app/build.gradle) for example (in *applicationDefaultJvmArgs*).

```
java -Dorg.lifecompanion.debug.dev.env=true [...]
```

In Java code, properties are tested on their presence with `LangUtils.safeParseBoolean(System.getProperty("org.lifecompanion.debug.dev.env"))`.

|NAME|VALUES|DESCRIPTION|
|-|-|-|
|`-Dorg.lifecompanion.debug.dev.env`|`true`, `false`, *`[EMPTY]`*|A general configuration that can be used to check if we are running LifeCompanion in a dev context. This can be useful to add currently developed feature with this check, this will secure for an unfinished feature to be pushed in production.|
|`-Dorg.lifecompanion.debug.skip.update.check`|`true`, `false`, *`[EMPTY]`*|Will skip update checking on each LifeCompanion run (for app and plugins)|
|`-Dorg.lifecompanion.debug.load.plugins.from.cp`|`true`, `false`, *`[EMPTY]`*|When enabled, will try to load plugins from classpath instead of the classpath configuration file. This is useful to make the plugin dev easier.|
|`-Dorg.lifecompanion.debug.loaded.images`|`true`, `false`, *`[EMPTY]`*|When enabled, a checking Thread is launched in background to display the loaded image count. This can be useful to detect memory leaks on images. See [ImageDictionaries#startImageLoadingDebug()](../lifecompanion/lc-app/src/main/java/org/lifecompanion/model/impl/imagedictionary/ImageDictionaries.java) for details|
|`-Dorg.lifecompanion.debug.configuration.memory.leak`|`true`, `false`, *`[EMPTY]`*|When enabled, a checking Thread is launched in background to display the loaded configuration count. This can be useful to detect memory leaks on configuration (for example, if a configuration is not released on configuration changed). See [ConfigurationMemoryLeakChecker](../lifecompanion/lc-app/src/main/java/org/lifecompanion/util/debug/ConfigurationMemoryLeakChecker.java) for details|