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
|`-disableExternalActions`|*`NONE`*|Disable any actions that could interact with the system : printing, opening web pages, opening files, running commands, etc.|
|`-disableExit`|*`NONE`*|Disable the ability for an user to exit LifeCompanion from use mode by itself. LifeCompanion will still be able to be closed by external events.|
|`-disableLoadingWindow`|*`NONE`*|Don't show any loading window on LifeCompanion startup. Even if this parameter is enabled, the first splashscreen on launch will still be displayed|
|`-disableSelectionAutostart`|*`NONE`*|If enabled, the selection mode will not automatically start on each started configuration. It will be able to be activated only when the control server is enabled and `selection/start` is called.|
|`-forceVolumeLevel level`|`0.5`|Will force the sound on speech synthesizer, medias, etc to be at a certain level (range 0.0 to 1.0).|
|`-disableFullscreen`|*`NONE`*|Disable the user ability to switch from decorated/fullscreen mode on the use mode window. Will disable the fullscreen button, but also the keyboard shortcut|
|`-forceWindowUndecorated`|*`NONE`*|Will force the use mode window to be "undecorated" as stated in [JavaFX documentation](https://openjfx.io/javadoc/18/javafx.graphics/javafx/stage/Stage.html) on stage style. |
|`-forceWindowSize width height`|`1200 800`|Will force the use mode window to be as the specified size (in pixel). The given size will respect the screen scaling. The user will then not be able to resize the use mode window.|
|`-forceWindowLocation x y`|`0 0`|Will force the use mode window to be at a specific location on the screen (in pixel, from top left corner). The given location will respect the screen scaling.|
|`-forceWindowOpacity opacity`|`0.8`|Will force the use mode window to keep a specific opacity regardless the configuration set on it for its opacity. Opacity should range between 0.0 (transparent) to 1.0 (opaque).|
|`-disableWindowAlwaysOnTop`|*`NONE`*|Will force the use mode window to not always be on top of the other window. This change the default LifeCompanion behavior that set the use mode window always on top.|
|`-forceWindowMinimized`|*`NONE`*|Will start use mode with the LifeCompanion window iconified. Useful if you don't want the LifeCompanion window to pop on start.|
|`-enableControlServer`|*`NONE`*|Will enable the API server to control LifeCompanion while running. To get details on control feature, check the "LifeCompanion control server API" part of documentation.API server will run on its default port (8648) if enable expect if the port is specific with its own parameter.|
|`-controlServerPort port`|`8080`|The port for the API server to run. Will be ignored if the API server is not enabled (check the parameter above to enable it). If not specified, server will run on its default port.|
|`-controlServerAuthToken token`|`AbCdEf123456`|If you want your control server to be secured with a `Authorization: Bearer <token>` header on each request. If enabled, any request without the same matching token will be rejected with 401 code|
|`-updateDownloadFinished`|*`NONE`*|Inform LifeCompanion that the update download was finished on last LifeCompanion use. When launched with the arg, LifeCompanion will try to install the newly downloaded update and restart itself.|
|`-updateFinished`|*`NONE`*|Inform LifeCompanion that the update installation was done on the previous launch. Typically, this arg is added on LifeCompanion restart after update installation.|
|`-enablePreviewUpdates`|*`NONE`*|Enable LifeCompanion preview updates. This can be useful to test update before their production version to be ready.|


## LifeCompanion JVM properties

LifeCompanion can be configured using some JVM properties. As every properties in Java, they should be configured before argument with the **-D** syntax.
You can check [lc-app/build.gradle](../lifecompanion/lc-app/build.gradle) for an usage example (in *applicationDefaultJvmArgs*).

|Configuration|Param. example|Description|
|-|-|-|
|`-Dorg.lifecompanion.dev.mode`|*`NONE`*|A general configuration that can be used to check if we are running LifeCompanion in a dev context.This can be useful to add currently developed feature with this check, this will secure for an unfinished feature to be pushed in production.|
|`-Dorg.lifecompanion.disable.updates`|*`NONE`*|Will disable all the update checking process (for both app and plugins). Will not try to reach the update server at all.|
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

## LifeCompanion control server API

LifeCompanion control API server allow you to control LifeCompanion while runing by calling specific HTTP services on localhost (or from a distant if port are opened). If not specified, the LifeCompanion control server is running on port 8648, but you can specify this with `-controlServerPort 8080` command line argument.

**Note that server will be running only if LifeCompanion is launched using `-enableControlServer` argument.**

Once the server is running, you can directly call service with any HTTP client that can reach the host :

```sh
curl localhost:8648/api/v1/app/status
curl -X POST localhost:8648/api/v1/window/minimize
```

If you enabled control server authentication with `-controlServerAuthToken <token>`, you should also the header `Authorization: Bearer <token>` to your requests.

### Errors

Control server will always try to create a JSON response to your request. When an error happen, you can get the following result depending on the error.

```json
{
    "errorId": "error.not.found",
    "errorMessage": "Requested URL not found, check the docs !"
}
```

```json
{
"errorId": "error.unknown",
"errorMessage": "IllegalStateException :\nThis operation is permitted on the event thread only;"
}
```

### Returns

Returns from server can depend on the sent request, but a lot of request will return an "action result". The result will indicate if the action was done and add a message to explain why the action wasn't done. This should be used for dev. purpose only as the messages returned could change over the time.

```json
{
    "done": false,
    "message": "Not in use mode"
}
```

```json
{
    "done": true,
    "message": "OK"
}
```

### Available services

- **[app/status](#appstatus)**
- **[window/minimize](#windowminimize)**
- **[window/show](#windowshow)**
- **[window/bounds](#windowbounds)**
- **[voice/stop](#voicestop)**
- **[selection/stop](#selectionstop)**
- **[selection/start](#selectionstart)**
- **[media/stop](#mediastop)**

### /app/status

**Description** : To get the LifeCompanion current status, will return containing information about the running instance (can be `STARTING`,`IN_USE_MODE`,`IN_EDIT_MODE` or `STOPPING`)

**Url structure** : `/api/v1/app/status`

**Method** : `GET`

**Parameters** :
```
NONE
```

**Returns** : 
```json
{
  "status": "STARTING"
}
```
```json
{
  "status": "IN_USE_MODE"
}
```
```json
{
  "status": "STOPPING"
}
```
### /window/minimize

**Description** : Minimize the current use mode window to hide it from user

**Url structure** : `/api/v1/window/minimize`

**Method** : `POST`

**Parameters** :
```
NONE
```

**Returns** : 
```json
{
  "done": true,
  "message": "OK"
}
```
### /window/show

**Description** : Show the current window on top of the others and try to focus it

**Url structure** : `/api/v1/window/show`

**Method** : `POST`

**Parameters** :
```
NONE
```

**Returns** : 
```json
{
  "done": true,
  "message": "OK"
}
```
### /window/bounds

**Description** : Change the window bounds to the wanted bounds (in pixel). Bounds contains the window location top left corner (x,y) from screen top left corner and size (width,height). Will not check that the given bounds respect screen bounds.

**Url structure** : `/api/v1/window/bounds`

**Method** : `POST`

**Parameters** :
```json
{
  "x": 0,
  "y": 124,
  "width": 1366,
  "height": 644
}
```

**Returns** : 
```json
{
  "done": true,
  "message": "OK"
}
```
### /voice/stop

**Description** : Stop the current speaking voice synthesizer and empty the voice synthesizer queue to clear the waiting speech. Later calls to voice synthesizer will work as usual.

**Url structure** : `/api/v1/voice/stop`

**Method** : `POST`

**Parameters** :
```
NONE
```

**Returns** : 
```json
{
  "done": true,
  "message": "OK"
}
```
### /selection/stop

**Description** : Stop the current selection mode (if applicable). Will disable any user interaction with LifeCompanion UI no matter the current selection mode type (scanning, direct, etc.). To restore a working selection mode, `selection/start` should be called.

**Url structure** : `/api/v1/selection/stop`

**Method** : `POST`

**Parameters** :
```
NONE
```

**Returns** : 
```json
{
  "done": true,
  "message": "OK"
}
```
### /selection/start

**Description** : Start the selection mode for the current used configuration. Will restore user interaction with LifeCompanion UI. Calling this service once while the selection mode is already started will have no effect.

**Url structure** : `/api/v1/selection/start`

**Method** : `POST`

**Parameters** :
```
NONE
```

**Returns** : 
```json
{
  "done": true,
  "message": "OK"
}
```
### /media/stop

**Description** : Stop any playing media (sound, video, etc.) and empty the media players queue to be sure that no media will be played without a new play request.

**Url structure** : `/api/v1/media/stop`

**Method** : `POST`

**Parameters** :
```
NONE
```

**Returns** : 
```json
{
  "done": true,
  "message": "OK"
}
```