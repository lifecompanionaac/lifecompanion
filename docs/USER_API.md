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
|`-disableErrorNotification`|*`NONE`*|If enabled, error notification will never be showed in use mode (but will be still showed in edit mode).|
|`-disableFullscreen`|*`NONE`*|Disable the user ability to switch from decorated/fullscreen mode on the use mode window. Will disable the fullscreen button, but also the keyboard shortcut|
|`-forceWindowUndecorated`|*`NONE`*|Will force the use mode window to be "undecorated" as stated in [JavaFX documentation](https://openjfx.io/javadoc/21/javafx.graphics/javafx/stage/Stage.html) on stage style. |
|`-forceWindowSize width height`|`1200 800`|Will force the use mode window to be as the specified size (in pixel). The given size will respect the screen scaling. The user will then not be able to resize the use mode window.|
|`-forceWindowLocation x y`|`0 0`|Will force the use mode window to be at a specific location on the screen (in pixel, from top left corner). The given location will respect the screen scaling.|
|`-forceWindowOpacity opacity`|`0.8`|Will force the use mode window to keep a specific opacity regardless the configuration set on it for its opacity. Opacity should range between 0.0 (transparent) to 1.0 (opaque).|
|`-resetWindowVirtualKeyboard`|*`NONE`*|Will try to reset window size and location when the configuration is a virtual keyboard configuration. Can be useful on slow computers or computers managing the system windows in a custom way.|
|`-disableWindowAlwaysOnTop`|*`NONE`*|Will force the use mode window to not always be on top of the other window. This change the default LifeCompanion behavior that set the use mode window always on top.|
|`-forceWindowMinimized`|*`NONE`*|Will start use mode with the LifeCompanion window iconified. Useful if you don't want the LifeCompanion window to pop on start.|
|`-forceScreenIndex screenIndex`|`1`|Will force LifeCompanion to be used on the specified screen. 0 represents the primary computer screen and 1 the secondary screen.|
|`-enableControlServer`|*`NONE`*|Will enable the API server to control LifeCompanion while running. To get details on control feature, check the "LifeCompanion control server API" part of documentation.API server will run on its default port (8648) if enable expect if the port is specific with its own parameter.|
|`-controlServerPort port`|`8080`|The port for the API server to run. Will be ignored if the API server is not enabled (check the parameter above to enable it). If not specified, server will run on its default port.|
|`-controlServerAuthToken token`|`AbCdEf123456`|If you want your control server to be secured with a `Authorization: Bearer <token>` header on each request. If enabled, any request without the same matching token will be rejected with 401 code|
|`-controlServerEnableCors`|*`NONE`*|If you want that the control server allows request from all origin|
|`-updateDownloadFinished`|*`NONE`*|Inform LifeCompanion that the update download was finished on last LifeCompanion use. When launched with the arg, LifeCompanion will try to install the newly downloaded update and restart itself.|
|`-updateFinished`|*`NONE`*|Inform LifeCompanion that the update installation was done on the previous launch. Typically, this arg is added on LifeCompanion restart after update installation.|
|`-enablePreviewUpdates`|*`NONE`*|Enable LifeCompanion preview updates. This can be useful to test update before their production version to be ready.|
|`-hubUrl url`|`https://hub.lifecompanionaac.org`|The hub URL for syncing features. When not specified, the default LifeCompanion hub will be used.|
|`-hubAuthToken token`|`AbCdEf123456`|The auth token to be used when connecting to the LifeCompanion hub. Will overwrite any token that could be used while using the app (even if the user connects manually).|
|`-deviceSyncMode`|*`NONE`*|Enable the "device synchronization mode" : will launch directly LifeCompanion in use mode and will try to sync the current used configuration with the device default configuration from LifeCompanion HUB. This should be used only the HUB is connected and the device ID is injected.|
|`-deviceSyncAutoRefresh`|*`NONE`*|When the `deviceSyncMode` is enabled, will launch an auto sync Thread that will for a new selected device configuration every 10 seconds. If not enabled, the update should be manually triggered with the control server service.|
|`-deviceLocalId deviceLocalId`|`foobar123`|Set the device local ID to be used by the `deviceSyncMode` when enabled. Allow launching LifeCompanion with a device local ID already set.|
|`-useHubImages`|*`NONE`*|When enabled, LifeCompanion images will be downloaded on runtime from the hub and not from local image dictionaries (except for user dictionary). This can only be enabled if the hub URL has been provided.|

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
- **[app/exit](#appexit)**
- **[window/minimize](#windowminimize)**
- **[window/show](#windowshow)**
- **[window/bounds](#windowbounds)**
- **[voice/stop](#voicestop)**
- **[selection/stop](#selectionstop)**
- **[selection/start](#selectionstart)**
- **[selection/simulate/press](#selectionsimulatepress)**
- **[selection/simulate/release](#selectionsimulaterelease)**
- **[selection/config](#selectionconfig)**
- **[selection/virtual-cursor/info](#selectionvirtual-cursorinfo)**
- **[selection/virtual-cursor/move/relative](#selectionvirtual-cursormoverelative)**
- **[selection/virtual-cursor/move/absolute](#selectionvirtual-cursormoveabsolute)**
- **[selection/virtual-cursor/press](#selectionvirtual-cursorpress)**
- **[selection/virtual-cursor/release](#selectionvirtual-cursorrelease)**
- **[media/stop](#mediastop)**
- **[hub/update/device-local-id](#hubupdatedevice-local-id)**
- **[mouse/move/absolute](#mousemoveabsolute)**
- **[mouse/move/relative](#mousemoverelative)**
- **[mouse/info](#mouseinfo)**
- **[mouse/activation/primary](#mouseactivationprimary)**
- **[mouse/activation/secondary](#mouseactivationsecondary)**
- **[indication/target/show/location](#indicationtargetshowlocation)**
- **[indication/target/show/random](#indicationtargetshowrandom)**
- **[indication/target/hide](#indicationtargethide)**
- **[indication/activation/show](#indicationactivationshow)**
- **[indication/activation/hide](#indicationactivationhide)**

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
  "status": "STARTING",
  "selectionModeStatus": "PAUSED",
  "mainCurrentGrid": null,
  "currentOverPart": null
}
```
```json
{
  "status": "IN_USE_MODE",
  "selectionModeStatus": "PLAYING",
  "mainCurrentGrid": {
    "name": "Clavier",
    "id": "1fee441b-b261-4fe4-85fd-13572f0a1aa3",
    "rowCount": 4,
    "columnCount": 6,
    "firstKeyCenterX": 150,
    "firstKeyCenterY": 100,
    "keysCenterXSpacing": 65,
    "keysCenterYSpacing": 85
  },
  "currentOverPart": {
    "name": "A",
    "id": "003f6ba7-ff0e-4d1e-893e-8a7d7df880b0",
    "row": 1,
    "colum": 2
  }
}
```
```json
{
  "status": "STOPPING",
  "selectionModeStatus": "PAUSED",
  "mainCurrentGrid": null,
  "currentOverPart": null
}
```
### /app/exit

**Description** : To stop LifeCompanion. This call will return before the exit is real.

**Url structure** : `/api/v1/app/exit`

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
### /selection/simulate/press

**Description** : Simulate the selection press if the current selection mode is a scanning selection. The caller is responsible for later calling `selection/simulate/release`. Calling this service on a direct selection mode will have no effect.

**Url structure** : `/api/v1/selection/simulate/press`

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
```json
{
  "done": false,
  "message": "Current selection mode is not a scanning selection mode"
}
```
### /selection/simulate/release

**Description** : Simulate the selection release if the current selection mode is a scanning selection. Should be called only after calling `selection/simulate/press`. Calling this service on a direct selection mode will have no effect.

**Url structure** : `/api/v1/selection/simulate/release`

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
```json
{
  "done": false,
  "message": "Current selection mode is not a scanning selection mode"
}
```
### /selection/config

**Description** : Configure the current selection mode and restart it with the new configuration. Allow configuring the selection mode (direct, scanning, etc.) and some selection mode parameters (scanning loops, time...). Available mode : MOUSE_CLIC, AUTO_MOUSE_CLIC, VIRTUAL_DIRECTIONAL_CURSOR, SCAN_KEY_HORIZONTAL, SCAN_ROW_COLUMN, SCAN_KEY_VERTICAL, SCAN_COLUMN_ROW

**Url structure** : `/api/v1/selection/config`

**Method** : `POST`

**Parameters** :
```json
{
  "mode": "MOUSE_CLIC",
  "scanLoop": null,
  "scanTime": null,
  "disableAutoStart": null
}
```
```json
{
  "mode": "SCAN_ROW_COLUMN",
  "scanLoop": 2,
  "scanTime": 2500,
  "disableAutoStart": true
}
```
```json
{
  "mode": null,
  "scanLoop": 2,
  "scanTime": 1800,
  "disableAutoStart": true
}
```
```json
{
  "mode": "SCAN_KEY_HORIZONTAL",
  "scanLoop": 1,
  "scanTime": 1500,
  "disableAutoStart": false
}
```

**Returns** : 
```json
{
  "done": true,
  "message": "OK"
}
```
### /selection/virtual-cursor/info

**Description** : Return information about the virtual cursor position and selection zone size. Note that the size should be used to define the virtual cursor bounds. It does not depend from the real size on screen (as the stage size can change if the stage is resized/moved).

**Url structure** : `/api/v1/selection/virtual-cursor/info`

**Method** : `GET`

**Parameters** :
```
NONE
```

**Returns** : 
```json
{
  "selectionZoneWidth": 745.0,
  "selectionZoneHeight": 552.0,
  "cursorX": 50.0,
  "cursorY": 150.0
}
```
### /selection/virtual-cursor/move/relative

**Description** : If the selection mode is set to virtual cursor, move the virtual cursor relative to its current position

**Url structure** : `/api/v1/selection/virtual-cursor/move/relative`

**Method** : `POST`

**Parameters** :
```json
{
  "dx": 20,
  "dy": -10
}
```
```json
{
  "dx": null,
  "dy": 80
}
```

**Returns** : 
```json
{
  "done": true,
  "message": "OK"
}
```
### /selection/virtual-cursor/move/absolute

**Description** : If the selection mode is set to virtual cursor, move the virtual cursor to the given position

**Url structure** : `/api/v1/selection/virtual-cursor/move/absolute`

**Method** : `POST`

**Parameters** :
```json
{
  "x": 56,
  "y": 76
}
```

**Returns** : 
```json
{
  "done": true,
  "message": "OK"
}
```
### /selection/virtual-cursor/press

**Description** : If the selection mode is set to virtual cursor, start press simulation for the cursor. Note that it's the caller responsibility to then release the virtual cursor.

**Url structure** : `/api/v1/selection/virtual-cursor/press`

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
### /selection/virtual-cursor/release

**Description** : If the selection mode is set to virtual cursor, end press simulation for the cursor. Note that this should be called after the press call if you want to simulate activations.

**Url structure** : `/api/v1/selection/virtual-cursor/release`

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
### /hub/update/device-local-id

**Description** : Request the local device ID update to be used to sync the used configuration with default configuration for this device set on LifeCompanion Hub. Note that this should be combined with the `deviceSyncMode` parameter. The method will always immediately returns even if the change can be later considered by the app (config synchronization is async). This can also be called to update the configuration for the device when `deviceSyncAutoRefresh` parameter is not enabled.

**Url structure** : `/api/v1/hub/update/device-local-id`

**Method** : `POST`

**Parameters** :
```json
{
  "deviceLocalId": "foobar123"
}
```

**Returns** : 
```json
{
  "done": true,
  "message": "OK"
}
```
### /mouse/move/absolute

**Description** : Move the mouse to an absolute position on the used screen to display LifeCompanion. The given position should be absolute no matter the screen scaling factor. Coordinates are relative to the top left corner.

**Url structure** : `/api/v1/mouse/move/absolute`

**Method** : `POST`

**Parameters** :
```json
{
  "x": 689,
  "y": 383
}
```

**Returns** : 
```json
{
  "done": true,
  "message": "OK"
}
```
```json
{
  "done": false,
  "message": "Incorrect position"
}
```
### /mouse/move/relative

**Description** : Move the mouse by a given x and y difference that can be positive/negative/null. The given values should be absolute pixel values no matter the screen scaling factor. Positive values means a move to right or bottom, negative values means a move to left or top. The mouse will be blocked to avoid going "out" of screen bounds.

**Url structure** : `/api/v1/mouse/move/relative`

**Method** : `POST`

**Parameters** :
```json
{
  "dx": 15,
  "dy": -15
}
```
```json
{
  "dx": -60,
  "dy": null
}
```

**Returns** : 
```json
{
  "done": true,
  "message": "OK"
}
```
### /mouse/info

**Description** : Return information about the current mouse position and screen size.

**Url structure** : `/api/v1/mouse/info`

**Method** : `GET`

**Parameters** :
```
NONE
```

**Returns** : 
```json
{
  "screenWidth": 1920.0,
  "screenHeight": 1080.0,
  "mouseX": 564.0,
  "mouseY": 855.0
}
```
### /mouse/activation/primary

**Description** : Immediately active the mouse primary (eg left button) button to the current mouse position.

**Url structure** : `/api/v1/mouse/activation/primary`

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
### /mouse/activation/secondary

**Description** : Immediately active the mouse secondary (eg right button) button to the current mouse position.

**Url structure** : `/api/v1/mouse/activation/secondary`

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
### /indication/target/show/location

**Description** : Show the target indication to a specific location in the current main grid. If the target is reached, an specific use event is generated (Cible de déplacement atteinte). The behavior can be : HIDE_ON_REACHED (the target is hidden when reached on moving), HIDE_ON_ACTIVATION (the target will be hidden if the cursor is on the target and activated), KEEP (the target will always be showing until hide is called). Default is HIDE_ON_REACHED.

**Url structure** : `/api/v1/indication/target/show/location`

**Method** : `POST`

**Parameters** :
```json
{
  "row": 2,
  "column": 4,
  "color": "#008000ff",
  "strokeSize": 5.0,
  "targetBehavior": "HIDE_ON_REACHED"
}
```
```json
{
  "row": 1,
  "column": 3,
  "color": null,
  "strokeSize": null,
  "targetBehavior": null
}
```

**Returns** : 
```json
{
  "done": true,
  "message": "OK"
}
```
### /indication/target/show/random

**Description** : Show the target indication to a random location in the current main grid. If the target is reached, an specific use event is generated (Cible de déplacement atteinte).  The behavior can be : HIDE_ON_REACHED (the target is hidden when reached on moving), HIDE_ON_ACTIVATION (the target will be hidden if the cursor is on the target and activated), KEEP (the target will always be showing until hide is called). Default is HIDE_ON_REACHED.

**Url structure** : `/api/v1/indication/target/show/random`

**Method** : `POST`

**Parameters** :
```json
{
  "color": "#008000ff",
  "strokeSize": 5.0,
  "targetBehavior": "HIDE_ON_ACTIVATION"
}
```
```json
{
  "color": null,
  "strokeSize": null,
  "targetBehavior": null
}
```

**Returns** : 
```json
{
  "done": true,
  "message": "OK"
}
```
### /indication/target/hide

**Description** : Hide the currently showing target indication. Noop if no target is showing.

**Url structure** : `/api/v1/indication/target/hide`

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
### /indication/activation/show

**Description** : Show the activation indication on the current overed part of the selection mode. The activation indication will "follow" the selection mode indicator.  If the selection mode is activated while showing this indication, an specific use event is generated (Demande d'activation effectuée) and the indication is hidden.

**Url structure** : `/api/v1/indication/activation/show`

**Method** : `POST`

**Parameters** :
```json
{
  "color": "#2517c263"
}
```

**Returns** : 
```json
{
  "done": true,
  "message": "OK"
}
```
### /indication/activation/hide

**Description** : Hide the currently showing activation indication. Noop if no target is showing.

**Url structure** : `/api/v1/indication/activation/hide`

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
