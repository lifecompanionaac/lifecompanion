# Extends LifeCompanion with plugins

## Introduction

**Plugins are a good way to extends LifeCompanion features without having to modify its core**. It allows developpers to create individual plugin that can be updated out of the app update process. Before creating a plugin, you should be trained to use LifeCompanion (in both mode : edit and use).

Terms used in this documentation will be directly linked to LifeCompanion usages.

Feel free to fill an issue if you're having trouble with plugin development.

While plugin are easier to implement that modifying LifeCompanion core, feel free to create pull request if the plugin API is limited for your use.

## Prerequisites

Read the developpers documentation : [how to build and run LifeCompanion](BUILD.md)

Be familiar with Java and JavaFX development.

## Getting started

### Initialize your plugin project

1. First, clone LifeCompanion official repository **from the last release tag** `lifecompanion/lc-app/X.X.X-prod` and make it run locally : [use build documentation](BUILD.md)
1. Run `gradlew publishToMavenLocal` on **lifecompanion-framework** and **lifecompanion** projects (this will add LifeCompanion API to your local Maven repo)
1. Duplicate the [**lifecompanion-plugins/lc-spellgame-plugin**](../lifecompanion-plugins/lc-spellgame-plugin/) folder to a folder named with your plugin ID - let's call it `lc-example-plugin` for the next steps
1. Change Gradle project name in *settings.gradle* to `rootProject.name = 'lc-example-plugin'`
1. Enter plugin metadata in *build.gradle*, especially `"LifeCompanion-Plugin-Id": "lc-example-plugin"`
1. Check in *gradle.properties* that the LifeCompanion version is the same that the pulled version `lifecompanionAppVersion=1.6.3`
1. You can then build your plugin jar with `gradlew jar`

Follow the next section to be able to quickly develop your plugin in InteliJ

### Import and use the plugin in IntelliJ

1. Create your LifeCompanion project as described in [build documentation](BUILD.md)
1. Add your new plugin project as a module in IntelliJ (File > New > Module from existing sources)
1. Create a run configuration that build your plugin : `gradlew clean jar` (on your plugin project)
1. Create a run configuration that run LifeCompanion app `gradlew :lc-app:run`
1. On the LifeCompanion run configuration
    1. Add on **environment variables** : `org.lifecompanion.dev.cp.arg=../../lifecompanion-plugins/lc-spellgame-plugin/build/libs/*` (this should be adapted to your plugin folder path)
    1. Add : Before launch > Run Another Configuration with your run configuration that build your plugin (`clean jar`)
1. You can now use your LifeCompanion run configuration : this will first build your plugin and inject its last version to LifeCompanion !

### [Detailed instructions to build/run LifeCompanion](BUILD_DETAIL.md)

### Existing plugins

All the official existing plugins are stored in the [**lifecompanion-plugins**](../lifecompanion-plugins/) folder on the repo. You can check their code to understand how plugin can be created.

- [**spell game plugin**](../lifecompanion-plugins/lc-spellgame-plugin/) : plugin that create a game where the user should spell words
- [**simple email plugin**](../lifecompanion-plugins/lc-email-plugin/) : plugin that allow the user to read and send email from traditionnal email server
- [**calendar plugin**](../lifecompanion-plugins/lc-calendar-plugin/) : plugin to help user to plan their days (with alarms, events and sequences)
- [**ppp plugin**](../lifecompanion-plugins/lc-ppp-plugin/) : plugin to trace prediatric pain profil scale for an user
- [**homeassistant plugin**](../lifecompanion-plugins/lc-homeassistant-plugin/) : plugin to interact with a HomeAssistant server
- [**predict4all evaluation plugin**](../lifecompanion-plugins/lc-predict4all-evaluation-plugin/) : plugin to evaluate word prediction
- [**IR remote plugin**](../lifecompanion-plugins/lc-flirc-plugin/) : plugin to use LifeCompanion as an IR remote with [FLIRC](https://flirc.tv/products/flirc-usb-receiver?variant=43513067569384)

This documentation is based on [**lc-spellgame-plugin**](../lifecompanion-plugins/lc-spellgame-plugin/) to rely on a working example. This plugin is the reference plugin implementation.

## LifeCompanion fundamentals

### Code organization

Core LifeCompanion code is located in [**lifecompanion/lc-app**](../lifecompanion/lc-app/) directory. Model classes are organized with interface/implementation principle : most of the interfaces are named with "I" at the end, theses interfaces are mainly the names used in documentation. For example : `LCConfigurationI` is the interface describing the configuration model, and `LCConfigurationComponent`.

Interfaces are also mostly used as "contracts" to add features to components : for example every component `RootGraphicComponentI` will implement `MovableComponentI, ResizableComponentI, DisplayableComponentI, SelectableComponentI, ConfigurationChildComponentI` interfaces. These coding principle allow LifeCompanion to be simply extended.

You can see the repo organization in [root documentation](README.md)

**As it is difficult to be exhaustive on all LifeCompanion API features, we strongly encourage developpers to explore the package and the calling hierarchy of each method to fully understand how things work.**

### General principles

#### Modes

LifeCompanion works with two modes :
- **EDIT** : mode for professionnals or people that want to edit the software behavior. In this mode, the UI is much more complex to be able to edit every elements.
- **USE** : mode for final users to use LifeCompanion : text editors are working, speech synthesis to, etc.

#### Organization

![LifeCompanion base classes](res/classes-main-org.png)

LifeCompanion is working in the following way :
- Any LifeCompanion user creates **profile** (`LCProfile`) that will stores **configurations** (`LCConfigurationDescriptionComponentI` and `LCConfigurationComponent`)
- In **profile**, **configuration** list is displayed thanks to `LCConfigurationDescriptionComponentI` that store configuration information (author, description, id, etc.)
- When a user wants to modify or use a **configuration**, this configuration is loaded with `LCConfigurationComponent` : this component stores all LifeCompanion configuration
- Once a **configuration** is opened : **base elements** (`RootGraphicComponent` implementations) are added to it. Theses components are movable, resizable, etc. to create the user interface. Two main implementation are possible : **grid stacks** (`StackComponent`) and **text editors** (`TextEditorComponent`)
- In **grid stacks**, user will then add **grids** (`GridComponentI`) : these **grids** are the "pages" possible for this stack. Each grid can have its own layout : a number of row/column and sub elements
- In **grids**, regarding to row/column counts, a certain number of **keys** (`GridPartKeyComponent`) are available (eg : 5*4 = 20 keys). Each of these **keys** is located in the grid (thanks to row/column variable) and can span to multiple row/column.
- On **keys**, user will define
    - the **key type** (`KeyOptionI`) : this allow the key to be automatically filled/used by LifeCompanion in use mode (e.g `WordPredictionKeyOption` are filled automatically on runtime)
    - the **key actions** (`BaseUseActionI`) : this define what will be the key behavior on selection. Actions are organized by events (**activation** or **over**) and in ordered list : on action fired (by selection or by key "hover"), action are then executed in order (sequentially)
- In configuration, **events** (`UseEventGeneratorI`) can also be added
    - they are global "listeners" to configuration events : each listener implements an event detection, and then call **actions** list if fired
    - example implementation : event generator "keyboard key pressed" is fired on each key pressed on the keyboard : it can be useful to create keyboard shortcut for example
- In **grids**, keys can be replaced by other implementation (implementing `GridPartComponentI`) : for example, it's possible to replace a key with **sub grid**, **text editor** or **grid stack**. The only difference between their "root" implementation is just that they are not resizable/movable as they location depends on key position/span.

### What's possible with plugins ?

Plugin are a good way to integrate specific features into LifeCompanion. This table presents the main integrated features.

- [**Use action**](#use-action) *("actions")*
    - They are the LifeCompanion's core : added to keys (or to global event) they will define the user interaction behavior. They link keys to global behavior.
- [**Key option**](#key-option) *("type de cases")*
    - Key options are a good way to integrate keys that are modified on runtime by user : their text, image, action are handled by your implementation or you need a specific view for a key (custom JavaFX component).
    - Combined with specific use actions, they are the best way to create custom applications
- [**Use event**](#use-event) *("événement")*
    - Use events are a way to associate global events happening in use mode to specific use actions. Events can also generate use variables that can be then used by use actions.
    - Use events can be used to create generic behavior on global events, to "react" on something happening.
- [**General configuration view**](#general-configuration-view) *("paramètres généraux")*
    - General configuration view will be directly added to the configuration view and allows you to create custom configuration views to do anything
- [**Use variable**](#use-variable) *("variable")*
    - Use variable have two main usage : in `VariableInformationKeyOption` they are "injected" to key text every 1 second in use mode to create keys with variable information. Anywhere in use mode, they can be used : the common usage is to generate variable text in `WriteTextAction` or `SpeakTextAction`
- [**Word prediction**](#word-prediction) *("prédiction de mots")*
    - Word prediction are called on each editor text change (content or caret position). They should return prediction results (ordoned word list)
- [**Char prediction**](#char-prediction) *("prédiction de caractères")*
    - As the word prediction do, the char prediction should do the exact same thing but for characters : results are then used to fill a dynamic keyboard
- [**Voice synthesizer**](#voice-synthesizer) *("synthèse vocale")*
    - Voice synthesizer converts text to speech. Common parameters are shared with all implementation : volume, rate and pitch. Implementation can be system dependant.

## How to

### Plugin

Plugin are loaded by LifeCompanion on startup and their implementation can be used all accross the components.

#### Organization and technical notes

A plugin is composed of a single jar file that is **loaded on classpath**. LifeCompanion plugins don't make any use of Java modules (jigsaw) while LifeCompanion app do. This is made to make it easier to create plugin with their own dependencies.

Plugin projects are built using Gradle like LifeCompanion core. The generated jar file for a plugin contains the plugin general metadata (id, author, version, name, etc) in the jar manifest file. These metadata are used for LifeCompanion to prepare a plugin loading (on next start) and to ensure compatibility and updates.

As plugin are based on LifeCompanion, they are depending of LifeCompanion version. If LifeCompanion is updated with breaking changes, the plugins should be updated. To see the API changes, check [dev changelog documentation](DEV-CHANGELOG.md)

#### Information - `PluginI`

#### Properties

Plugin can have properties associated to configuration. These properties are directly saved on configuration if it is detected that the configuration use the plugin (an action, an event, etc).

To implement properties, class has to extends `AbstractPluginConfigProperties` and the plugin implementation should return the property implementation via (see also [Serialization part](#serialization) to understand how properties are stored)

```java
@Override
public PluginConfigPropertiesI newPluginConfigProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
    return new SpellGamePluginProperties(parentConfiguration);
}
```

If you need to use plugin properties, you can request them on a configuration with

```java
SpellGamePluginProperties pluginConfigProperties = configuration.getPluginConfigProperties(SpellGamePlugin.ID, SpellGamePluginProperties.class);
```

#### Lifecycle

Plugin are directly loaded on LifeCompanion start by the launcher, they are injected to LifeCompanion thanks to classpath arg on Java command.

Even if you can rely on classic Java mechanism to initialize your plugin, it's better to rely on LifeCompanion lifecycle to ensure your plugin to be correctly initialized.

##### App start/stop

To detect LifeCompanion starting/stopping (global app), you can implement two methods in your plugin.

```java
@Override
public void start(File dataDirectory) {
    // Plugin global init here
}

@Override
public void stop(File dataDirectory) {
    // Plugin global stop here
}
```

Start/stop methods will **be called once** on each LifeCompanion run.

The given data directory can be used to store plugin data (it is shared between all profile/configuration). The folder is unique with the plugin ID, so it's kept between every run and between updates.

As the start method is called out of the FX Thread and while app loading message is displayed to user, you can make long running synchronous initialization tasks here.

##### Use mode start/stop

Contrary to app lifecycle, mode lifecycle can occur multiple times on a same app run. They will be called when :
- **the user go from edit to use mode OR when app is started with a default configuration** : `modeStart`
- **the user go from use to edit mode OR app is stopped** : `modeStop`

You can rely on these hooks to ensure initialization that should be done regarding the used configuration as it is given to the methods.

The methods will run async (out of the FX Thread) so there is no problem doing long running tasks here.

```java
@Override
public void modeStart(LCConfigurationI configuration) {
    SpellGameController.INSTANCE.modeStart(configuration);
}

@Override
public void modeStop(LCConfigurationI configuration) {
    SpellGameController.INSTANCE.modeStop(configuration);
}
```

#### Resource

##### Language file

Even if LifeCompanion is currently available only in french, plugin can have multiple translation files. This is useful to separate code from string data.

The plugin should return the path (jar path) to the language files via :

```java
@Override
public String[] getLanguageFiles(final String languageCode) {
    return new String[]{"/text/" + languageCode + "_spellgame_plugin.xml"};
}
```
The injected `languageCode` parameter is the ISO 639-1 code for language ("fr" for french).

See [Translations](#translations) for more details.

##### JavaFX stylesheets

It's possible to inject custom JavaFX stylesheets to edit mode Scene from the plugin.

```java
 @Override
public String[] getJavaFXStylesheets() {
    return new String[]{"/style/spellgame_plugin.css"};
}
```

##### Default configuration

![LifeCompanion default configuration](res/default_configuration.png)

As it can be difficult for a final user to create a configuration from strach using your plugin, it is important to provide default configuration for user to start their own. Your provided configuration will be added to default configuration list available when the user create a new profile or when he wants to create configuration from model.

The configuration will be then duplicated to be added in the profile. AS the current language is injected to the implementation, it is possible to create different configuration for each language.

```java
@Override
public String[] getDefaultConfigurations(String languageCode) {
    return new String[]{"/configurations/" + languageCode + "_spellgame-config1.lcc"};
}
```

**As the configuration will be visible by users : ensure it does not contains any private data**

### Use action

![LifeCompanion use action](res/use_action.png)

Actions are the best way to implement specific behavior in LifeCompanion. Most of the action implementation interact with LifeCompanion [existing controllers](#controllers) or with custom controllers. For example, an email plugin will interact with its own controller to manage inbox, sent messages, etc.

Actions can have parameters that are configured by user in edit mode (optional). If the action have parameters, they will be serialized with the action (check [Serialization part](#serialization)).

Actions will be attached to two components in LifeCompanion (each of these component extending `UseActionTriggerComponentI`)
- **keys** : actions are attached to keys and are generated by the current selection mode, they can be generating by "selecting" the key or by "hovering" it - `GridPartKeyComponentI`
- **events** : action are triggered by general events that can be external to selection mode (e.g. : hour of the day, global key press, etc.) - `UseEventGeneratorI`

When you're implementing an action, you can chose that the action be can added to both of these components or to one of them only. Generally, allowing adding an action to both component is good but sometimes it is not possible : for example the action `WriteLabelAction` can be added to keys only as it will inspect the attached key to write its label (and events don't have labels).

#### Categories

Actions are categorized with main categories (`UseActionMainCategoryI`) and sub categories (`UseActionSubCategoryI`) : each main categories can contains multiple sub categories. The categories are only useful to display the extension in the edit mode UI. It is a good practice to define its own categories for a plugin but a plugin action can be attached to an existing category using `DefaultUseActionSubCategories` enum.

#### Types

Action can be implemented with two ways :
- **Simple use action** : this the most used action type in LifeCompanion. It totally deleguate "when" the action should be executed to LifeCompanion selection system. It the most common way as it's simple : the action can be implemented handling a single "execute" method.
- **Adavanced use action** : this is much less used be can be useful when you need to handle the "start" and the "end" of an action depending on its selection. For example, it's use to create an action deleting char while the key is pressed.

Generally, you should alway implement your action extending `SimpleUseActionImpl`. If you need to create an advanced action, you can directly extend `BaseUseActionImpl` or check `RepeatActionBaseImpl` that already implement a "repeat" action behavior.

#### Edit mode information

Actions have multiple information that are used only in edit mode to drive the user adding/editing them.
These informations should be filled in the action constructor extending `BaseUseActionImpl` (setting the protected attribute to the requested values).

Please refer to each attribute Javadoc to find the expected value in each attribute. (**TODO : CREATE THE JAVADOC**)

Note that `variableDescriptionProperty` can be used in two ways :
- static way : just set the value to the static version of the description : `this.variableDescriptionProperty().set(this.getStaticDescription());`
- dynamic way : if your action have parameters (eg te text to write like `WriteTextAction` action), you can bind the variable description to change when the action parameters change : see [Translations](#translations) and `TranslationFX` part.

#### Configuration view

![LifeCompanion use action configuration view](res/use_action_config_view.png)

Action can have configuration view if there is a need to configure some properties. For example, `WriteTextAction` can have its text to write configured. To configure it, the action should set its attribute `parameterizableAction` to true. This will cause LifeCompanion to search for a configuration view.

To create an action configuration view, you should extend `UseActionConfigurationViewI` and make sure to return the correct type :
```java
@Override
public Class<StartSpellGameAction> getConfiguredActionType() {
    return StartSpellGameAction.class;
}
```

The implementation should also handle the action instance/UI binding : 

```java
@Override
public void editStarts(final StartSpellGameAction element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
}

@Override
public void editEnds(final StartSpellGameAction element) {
}

@Override
public void editCancelled(StartSpellGameAction element) {
}
```

As action can handle [use variable](#use-variable), the list of available variable is injected to `editStarts(...)`. You can then use them in a `UseVariableTextArea` for example by calling its `setAvailableUseVariable(...)` method. This is important to do as use action be plugged on keys but also on [use events](#use-event) that can generate their own use variables.

#### Execution

Action are executed on a specific Thread (not on the FX Thread) so you should be aware that you can't modify the UI directly : see [Threading part about it](#threading).

**Actions added to a key or to an event are executed sequentially** : the first action should ends before executing the second. This means that if your action is blocking the execution thread, the next action could be never executed. However, LifeCompanion allow 4 components to run action in parallel. If possible, it's better that your action don't create any new thread, however if needed, be aware that use mode could end "outside" your new thread : if you need to interact with general controllers after a while, it's better to check if you're still in use mode (with `AppModeController.isUseMode()`)

To implement your action behavior, implement :
```java
@Override
public void execute(final UseActionEvent event, final Map<String, UseVariableI<?>> variables) {
}
```

### Key option

![LifeCompanion key option](res/key_option.png)

Key option are useful to implement specific/dynamic content on keys. It allows developper to create a key with a dynamic text, image, style, actions and also custom JavaFX node views.
Most of the time, key options are used by specific controllers that handle updating their content in use mode.

Here are a short list of some example implementations (you can find existing implementation in `org.lifecompanion.model.impl.configurationcomponent.keyoption`)
- **Quick communication key** (`QuickComKeyOption`) : a key that only define a related action and configuration view to make editing quicker
- **Word prediction key** (`WordPredictionKeyOption`) : a key that is filled on runtime by word prediction controller, that also have an associated action
- **Progress display key** (`ProgressDisplayKeyOption`) : a key that display a progress bar or indicator for user sequences

The following documentation will use the `CurrentWordDisplayKeyOption` implementation as reference.

#### Implement a key option

If you want to create a new key option, you have to extends `AbstractKeyOption`, in the key option constructor :

```java
public CurrentWordDisplayKeyOption() {
    this.optionNameId = "spellgame.plugin.current.word.key.option.name";
    this.optionDescriptionId = "spellgame.plugin.current.word.key.option.description";
    this.iconName = "current_word.png";
}
```

Note that key option have specific properties to define default behavior : `disableTextContent`, `disableImage`, `considerKeyEmpty`, `maxTextLength`. These option are mainly useful in edit mode to configure the edit mode UI (by disabling some buttons/fields).

#### Key option and its parent key

Key option instances are always attached to a single key. Note that the key option is attached to the key in both edit and use mode. You can implement hooks to modify the attached/detached key.
You can find attach/detach example in `WordPredictionKeyOption`.

```java
@Override
public void attachToImpl(final GridPartKeyComponentI key) {
}

@Override
public void detachFromImpl(final GridPartKeyComponentI key) {
}
```

The attached key is available in the key option in `attachedKey` property. You can get this property value to access the attached key instance.

#### Custom view

A key option can be used to define a custom view that will be attached to the key view. The defined view has to be a subclass of `javafx.scene.layout.Region`.
If you set a Node to the `keyViewAddedNode` property, this will add the given view to the key view (on top of it), binding the given `Region` its `prefWidthProperty()` and `prefHeightProperty()`.

Be careful, as modifiying this property can change the JavaFX graphic tree, you should alway modify it on FX Thread, check [Threading part](#threading).

Good example of custom view can be found in `ProgressDisplayKeyOption`.

Note that for key options contrary to use event/action, a binding is done between model and UI field. To make it work correctly, you should create your binding using `EditActionUtils` and a custom a `UndoRedoActionI` : this will allow your key option property changes to be handled correctly in edit mode UI.

#### Configuration view

Key option can have their own edit mode view that will be added to "Selected" tab in edit mode. These view can be useful to edit the key option configuration. Configuration can be directly available in this configuration view, but implementer is free to implement a simple button to open a global configuration dialog.

To implement a key option configuration view, you should extend `BaseKeyOptionConfigView`, you should especially take care of binding you custom key option properties 


#### Using key options in controllers

Most of the time, controllers will need to get one or all the keys/key options in use mode to handle their content. For example, `WordPredictionController` will keep a list of all available word prediction key to update them on text change. To do so, an helper method exists :

```java
Map<GridComponentI, List<CurrentWordDisplayKeyOption>> keys = new HashMap<>();
ConfigurationComponentUtils.findKeyOptionsByGrid(CurrentWordDisplayKeyOption.class, configuration, keys, null);
```

This will return a map with all the found key options per grid. Be aware that an user in edit mode can set your key option to none or to multiple keys, so you have to handle these cases too.

### Use event

![LifeCompanion use event](res/use_event.png)

Use event in LifeCompanion allow the user to define actions after a global event is detected. Basically, this allow the user to define behavior that don't depend on key activation (like done with use actions). For example, in LifeCompanion core, event are used to "react" to key pressed, to a specific time reach, to use variable changes, etc.

It can be usefull to implement use event if you want to allow the user to define a global behavior. For example, in spell game, we create events that are fired on game starts/ends. It allows the final user to "react" on them, and for example to display the correct grid on each event.

Note that when you're implementing use events, **you're implementing the event generator**. The event "handling" is then done by LifeCompanion depending of the use actions defined by users.

#### Implement a use event generator

To implement an use event, you should extends `BaseUseEventGeneratorImpl`. Basically, the use event is implemented only handling `modeStart(...)` and `modeStop(...)` methods that should pluging-in the correct hook to then generate events (refer to [lifecycle part](#use-mode-startstop)). As use event also extends `CategorizedElementI` like use action do, same attributes can be found in it to define the icon, name, description, etc.

To generate events, you should call `useEventListener.fireEvent(...)` on the use event generator instance. For example, in spell game, the starts event looks like this :
```java
// In constructor or somewhere else
listener = () -> {
    if (AppModeController.INSTANCE.isUseMode()) {
        this.useEventListener.fireEvent(this, null, null);
    }
};

 @Override
public void modeStart(final LCConfigurationI configuration) {
    SpellGameController.INSTANCE.addGameStartedListener(listener);
}

@Override
public void modeStop(final LCConfigurationI configuration) {
    SpellGameController.INSTANCE.removeGameStartedListener(listener);
}
```

By calling `useEventListener.fireEvent(...)`, LifeCompanion will then execute use actions associated by the configuration editor in use event main configuration view.

#### Generating variables

Use events can have [use variables](#use-variable) associated when fired. This can be useful as use variable can be then used by the configuration editor in use action associated to the event. For example, in `KeyTypedKeyboardEventGenerator`, an event is fired on each typed key. An use variable containing the key typed is associated with event. Thanks to this, the user can for example associate the `SpeakTextAction` to speak the typed key.

For your event to generate variable, you should first add a `UseVariableDefinitionI` to the `generatedVariables` attribute :
```java
// In constructor
this.answerContentDefinition = new UseVariableDefinition("SpellGameAnswerContent",
        "spellgame.plugin.use.variable.event.answer.given.content.var.name",
        "spellgame.plugin.use.variable.event.answer.given.content.var.description",
        "spellgame.plugin.use.variable.event.answer.given.content.var.example");
this.generatedVariables.add(this.answerContentDefinition);
```

Then, on each event generation, you should inject your use variable value (using the same definition) :
```java
this.useEventListener.fireEvent(this, List.of(new StringUseVariable(this.answerContentDefinition, answer.input())), null);
```

#### Configuration view

Like use action, use event can be configured. For that you should implements `UseEventGeneratorConfigurationViewI`. Refers to [use action documentation on configuration views](#use-action) to understand how configuration views should be implemented.

### General configuration view

![LifeCompanion general configuration view](res/general_config_view.png)

General configuration view are configuration not directly linked to a selected component that allow the configuration of global parameter for the current edited configuration. Generally, the general configuration view is used to modify [plugin properties](#properties)

#### Implementing view

To implement a plugin configuration view, you have to implement `GeneralConfigurationStepViewI`. This will help you creating a configuration view for your plugin : see each method javadoc to understand how each element should be implemented.

#### Handling view lifecycle

Each view implementation should take care of lifecycle to bind the configuration to the plugin properties. This can be done implementing :
```java
@Override
public void bind(LCConfigurationI model) {
}
@Override
public void unbind(LCConfigurationI model) {
}
```

Each view can be added to the menu or accessed from another view. For example, in spell game plugin, the main view `SpellGameGeneralConfigView` is added to the menu so `shouldBeAddedToMainMenu()` return true. However, `SpellGameWordListConfigView` is just called by the main view so `shouldBeAddedToMainMenu()` return false. The link is made using :
```java
GeneralConfigurationController.INSTANCE.showStep(SpellGameWordListConfigView.STEP_ID, selectedItem);
```

If you create sub configuration view, take care about correctly implementing :
```java
@Override
public String getPreviousStep() {
    return SpellGameGeneralConfigView.STEP_ID;
}
@Override
public String getMenuStepToSelect() {
    return SpellGameGeneralConfigView.STEP_ID;
}
```

If parameters are given to `GeneralConfigurationController.INSTANCE.showStep(...)`, you can access them using (if you have multiple `showStep(...)` calls, you should handle the different cases)
```java
@Override
public void beforeShow(Object[] stepArgs) {
    editedWordList = (SpellGameWordList) stepArgs[0];
    fieldListName.textProperty().bindBidirectional(editedWordList.nameProperty());
}
```
If you need the changes to be directly applied to the model, you can implement so the model will be changed on step change and not on "save".
```java
@Override
public void afterHide() {
    fieldListName.textProperty().unbindBidirectional(editedWordList.nameProperty());
}
```

#### Handling view cancel/save

For the save/cancel button to be working, you should take care of never modifying directly your configuration. You then have two choices :
- You can move every changes from view to model in `saveChanges()` method : the instance is then modified only when save button is clicked
- You can duplicate your model and then bind the view to it : this can be easier but requires your model to be easely duplicated. In `saveChanges()`you then just have to replace your old model with the new version. If you need this version, you can check how to implement `DuplicableComponentI` interface in examples.

### Use variable

![LifeCompanion general configuration view](res/use_variable.png)

Use variable can be used in multiple LifeCompanion components :
- **Keys** associated with "Information variable" key option : the text content of the key will be changed on runtime (combination of text and variables). It allow for example to display the current time in a key or an user score.
- **Actions** : any action using text can use variables to combine a input text with a variable input. For this, the action configuration view can use `UseVariableTextArea` field to make the variable insertion easier in UI and `UseVariableController.INSTANCE.createText(...)` to create the text with current variables
- **Events** : event can generate their own variable that will be added to the global variable context. This allow an event to generate a variable that will be then used by the associated actions. For example, the event `KeyTypedKeyboardEventGenerator` generates a variable containing the pressed key. If an action is associated to it, it can for example speak the pressed key (with `SpeakTextAction`)

Variable can also be used also to communicate between actions : for example the `FlagUseVariable` can be used to cancel an action following in the execution list if needed : by simply adding the variable to execution context on a first action, and then checking for it in a second action.

#### Defining a global variable

To define a global variable, your plugin should implement :

```java
@Override
public List<UseVariableDefinitionI> getDefinedVariables() {
    return Arrays.asList(//
            new UseVariableDefinition(
                    SpellGameController.VAR_ID_USER_SCORE,
                    "spellgame.use.variable.user.score.name",
                    "spellgame.plugin.use.variable.user.score.description",
                    "spellgame.plugin.use.variable.user.score.example"
            )
    );
}
```
This is just the definition of use variable making it available in the user UI to add a variable to various elements (text, use info keys, etc.).

You can adjust your variable definition to tune how your variable should be update : for that, use other `UseVariableDefinition` constructor that will allow you to set values for `getCacheLifetime()` and `isCacheForced()`.

#### Update variable value

When use mode is running, the plugin method `getSupplierForUseVariable` will be called on variable updates (and the result can be cached internally by LifeCompanion). Note that the variable value will not be then generated on each call depending on the active variable value cache in LifeCompanion.

```java
@Override
public Function<UseVariableDefinitionI, UseVariableI<?>> getSupplierForUseVariable(String id) {
    return switch (id) {
        case SpellGameController.VAR_ID_USER_SCORE -> def -> new IntegerUseVariable(def, SpellGameController.INSTANCE.getUserScore());
        case SpellGameController.VAR_ID_WORD_INDEX -> def -> new IntegerUseVariable(def, SpellGameController.INSTANCE.getWordIndex());
        case SpellGameController.VAR_ID_WORD_COUNT -> def -> new IntegerUseVariable(def, SpellGameController.INSTANCE.getWordCount());
        case SpellGameController.VAR_ID_CURRENT_STEP_INSTRUCTION -> def -> new StringUseVariable(def, SpellGameController.INSTANCE.getCurrentStepInstruction());
        case SpellGameController.VAR_ID_CURRENT_STEP_INSTRUCTION_WITH_WORD -> def -> new StringUseVariable(def, SpellGameController.INSTANCE.getCurrentStepInstructionWithWord());
        default -> null;
    };
}
```

Sometimes it can be usefull to update a variable earlier than the next automatic update : for example you want the user score to be updated immediatly after the correct action (instead of waiting ~1 second). For this, you can call `UseVariableController.INSTANCE.requestVariablesUpdate()` and the update will be done as soon as possible. Note that this method will try to generate the variables without getting their values from the cache, but if your variable definition return `true` to `isCacheForced()` you may also need to call `UseVariableController.INSTANCE.clearFromCache(...)` before requesting an update.

*Some plugins may use `generateVariables` method to generate their variable values, but for newer implementation, you should stick to `getSupplierForUseVariable`.*

### Word prediction

![LifeCompanion predictors](res/predictors.png)

New word predictor can be implemented extending `WordPredictorI`. Word predictors can have their own configuration view, for this, they should implement `getConfigStepId()` with an existing (General configuration view)[#general_config_view].

### Char prediction

New char predictor can be implemented extending `CharPredictorI`. Like the word predictor, they extend `BasePredictorI` that respect the same principles. The difference in char predictor is that they can implement two prediction method : one for automatic char prediction and one for "manual" char prediction (check key type for this).

## General topics

### Controllers

Most of LifeCompanion controllers are implemented using singleton pattern (using enum implementation). They are located in `org.lifecompanion.controller` package. **Controllers are the correct way to interact with LifeCompanion features : selection mode, voice synthesizer, current configuration, text editor, etc.** Plugin are encouraged implementing their own controllers to manage their global state.

Some controllers can be used in both edit and use mode, while some can't. Check each one documentation to known.

#### `AppModeController` : get current mode and configuration - *EDIT, USE*

This controller handle current mode (edit or use) and their context `UseModeContext` and `EditModeContext`. This controller should be mainly use to access current configuration or to test a current mode.

```java
if(AppModeController.INSTANCE.isUseMode())// test current mode
// Works with both getEditModeContext() and getUseModeContext()
AppModeController.INSTANCE.getEditModeContext().getStage();
AppModeController.INSTANCE.getEditModeContext().getConfiguration();
AppModeController.INSTANCE.getEditModeContext().getConfigurationDescription();
```

**IMPORTANT** : even if it is possible to get the current configuration in a running context (eg in use action or use event), it is better to get the current configuration from parent component when possible. For example, in use action, do :
```java
UseActionTriggerComponentI parentComponent = this.parentComponentProperty().get();
LCConfigurationI configuration = parentComponent.configurationParentProperty().get();
```

#### `ProfileController` : get current profile - *EDIT, USE*

This controller handle the current profile (for both edit and use mode). Note that the profil is mandatory and it filled if your configuration is running.
```java
ProfileController.INSTANCE.currentProfileProperty().get();// Get current profile
```

#### `ResourceHelper` : get classpath resources - *EDIT, USE*

Useful to get classpath resources easely :
```java
ResourceHelper.getInputStreamForPath("/app.properties") // will get it from src/main/resources/app.properties
```

#### `GeneralConfigurationController` :  navigate between general configuration views - *EDIT*

Controller that allow you display or navigate between [general configuration views](#general_config_view).
```java
GeneralConfigurationController.INSTANCE.showStep("STEP_ID"); // will navigate to general config view with this ID, show the stage if hidden
```

#### `AsyncExecutorController` : execute async task in edit mode - *EDIT*

This controller is the correct way to run async tasks in edit mode. It allows you to register this task for task execution panel and to block UI while task is running if needed. It will also handle the task failure displaying an error dialog when needed. The best way to use it is to implement custom `LCTask` like shown bellow but you can also use it providing `Runnable` instance.

```java
LCTask<String> longRunningTask = new LCTask<>("mytask.translation") {
    @Override
    protected String call() throws Exception {
        // Heavy async computing here
        return "ok";
    }
};
longRunningTask.setOnSucceeded(e -> {
    String result = longRunningTask.getValue(); // do something with the result after run
});
AsyncExecutorController.INSTANCE.addAndExecute(true, false, longRunningTask);
```

#### `ConfigActionController` : handle undo/redo action - *EDIT*

Using this controller is useful if you want to trace your edit mode actions and if you want your user to be able to use undo/redo action. This can be done executing `UndoRedoActionI` implementation with the `executeAction(...)` method (or to trace them with `addAction(...)` if they are already executed). Undo, redo will then be called with `undo()` or `redo()` methods.

```java
// Define the action
class ChangeProfileNameAction implements UndoRedoActionI {
    private final String newProfileName;
    private String oldProfileName;
    private ChangeProfileNameAction(String newProfileName) {
        this.newProfileName = newProfileName;
    }
    @Override
    public void doAction() throws LCException {
        LCProfileI profile = ProfileController.INSTANCE.currentProfileProperty().get();
        oldProfileName = profile.nameProperty().get();
        profile.nameProperty().set(newProfileName);
    }
    @Override
    public String getNameID() {
        return null;
    }
    @Override
    public void undoAction() throws LCException {
        ProfileController.INSTANCE.currentProfileProperty().get().nameProperty().set(oldProfileName);
    }
    @Override
    public void redoAction() throws LCException {
        doAction();
    }
}

//Elsewhere use it
ConfigActionController.INSTANCE.executeAction(new ChangeProfileNameAction("toto"));
```

If your action change a property value, you can also extends `BasePropertyChangeAction` that already implement a base behavior.

#### `ErrorHandlingController` : handle errors - *EDIT*

This controller can be used to display error message to user. It can be used with :
```java
 } catch (Throwable t) {
    // Display an error notification
    ErrorHandlingController.INSTANCE.showErrorNotificationWithExceptionDetails("User friendly message", t);

    // Directly an Exception Dialog
    ErrorHandlingController.INSTANCE.showExceptionDialog(t);
}
```
Note that if you want to "user friendly exception" you can use `LCException` and `LCExceptionBuilder` to create exception that will not be displayed "raw" to user.

#### `LCFileChoosers` :  display file/directory choosers - *EDIT*

This allows you to show file or directory chooser to user. You should always give a source node to correctly display the dialog.
```java
File selectedFileToImport = LCFileChoosers.getOtherFileChooser(
                Translation.getText("spellgame.plugin.config.field.import.list.chooser.title"),
                new FileChooser.ExtensionFilter(Translation.getText("spellgame.plugin.config.import.list.format"), Collections.singletonList("*.txt")),
                FileChooserType.OTHER_MISC_EXTERNAL)
        .showOpenDialog(FXUtils.getSourceWindow(buttonImportWordList));
```

#### `SelectionController` :  manage selection in edit mode - *EDIT*

This controller allow you to manage selection in edit mode. It can be useful to select a specific component as it will also display it in the edit view.
```java
ObservableList<GridPartKeyComponentI> selectedKeys = SelectionController.INSTANCE.getSelectedKeys();
if (!selectedKeys.isEmpty()) {
    SelectionController.INSTANCE.selectDisplayableComponent(selectedKeys.get(selectedKeys.size() - 1).gridParentProperty().get(), true);
}
```

#### `WritingStateController` : write and manage current text - *USE*

This is the controller to call if you want to write text in the current editor or with the virtual keyboard if the configuration is in a virtual keyboard. It also allows you to get the current written text.

```java
String totalText = WritingStateController.INSTANCE.currentTextProperty().get();
WritingStateController.INSTANCE.removeAll(WritingEventSource.SYSTEM);
WritingStateController.INSTANCE.insertText(WritingEventSource.SYSTEM,"Bonjour");
```

Note that you should provide on each call a `WritingEventSource` that will be used if logging is enabled.

Note that the behavior can be different depending if `LCConfigurationI.virtualKeyboardProperty()` is enabled or disabled.

#### `VoiceSynthesizerController` : use text to speech engine - *USE*

This controller can be used to use the text to speech capabilities of LifeCompanion. Most of the time, you should use it in a sync way as the use action execution context is already async.
```java
VoiceSynthesizerController.INSTANCE.speakSync("This is spoken with a text to speech");
```

Note that the current parameters for text to speech engine can be found on current configuration with `LCConfigurationI.getVoiceSynthesizerParameter()`

#### `SelectionModeController` : manage current selection mode - *USE*

This controller is useful to manage the selection mode in use mode and also the displayed element. This is the controller to use if you want to display a grid as it will take care of both displaying and changing the selection mode to the given element.
```java
GridPartComponentI part = ...;
SelectionModeController.INSTANCE.goToGridPart(part);
```
This controller can also be used to manage the selection state especially with scanning (to pause, restart, etc.). For example, you can pause the scanning with :
```java
SelectionModeController.INSTANCE.pauseCurrentScanningUntilNextSelection(restart ->{
    if(restart)myCustomSelectionMode.restart();
    else myCustomSelectionMode.resume();
});
```

This can also be used to change the running configuration in use mode without going back to edit mode.
```java
LCConfigurationDescriptionI configurationDescription = ...;
SelectionModeController.INSTANCE.changeConfigurationInUseMode(configurationDescription);
```

#### `SoundPlayerController` :  play sounds - *USE*

This controller can be used to play classic media sounds from LifeCompanion. It is compatible with every playable sounds with JavaFX as it use the media framework.
```java
SoundPlayerController.INSTANCE.playSoundSync(new File("mysound.mp3"), false);
```
You can use it both async/sync way depending on your needs. Note that if you need quick playing of your sound file, you should manually cache your own `MediaPlayer` as the current implementation create a new player on each call.

#### `UseModeProgressDisplayerController` : show progress bar - *USE*

This can be useful if your plugin need to use `ProgressDisplayKeyOption` to display a async task in use mode. This will update all the available `ProgressDisplayKeyOption` in the current used configuration.

```java
 UseModeProgressDisplayerController.INSTANCE.launchTimer(3000,()->{
    // Will be run after 3 seconds
});
```
If you need to hide the progress, you can call `hideAllProgress()` later.

#### `GlobalKeyEventController` : listen for key events - *USE*

This can be useful if your plugin need to listen for key events and eventually block them. You can use both `addKeyEventListenerForCurrentUseMode(...)` and `addKeyCodeToBlockForCurrentUseMode(...)` for that.
Note that element added are cleared on each use mode stop.

```java
GlobalKeyEventController.INSTANCE.addKeyEventListenerForCurrentUseMode(keyEvent -> {
    if (keyEvent.getEventType() == GlobalKeyEventController.LCKeyEventType.RELEASED && keyEvent.getKeyCode() == KeyCode.ENTER) {
        // Do something on enter
    }
});
```

#### `UseVariableController` : get use variable values and convert text - *USE*

This controller is useful to handle [use variables](#use-variable) for example to create texts that combined variable values and raw text :
```java
UseVariableController.INSTANCE.createText("Test {MyCustomVar}",varMap);
```
This can also be useful to request a variable update if one of your variable has been updated twice in less than one second.
```java
UseVariableController.INSTANCE.requestVariablesUpdate();
```

### Utils

Most of Utils in LifeCompanion are located in `org.lifecompanion.util` package. Utils can contains common utils methods but also LifeCompanion or JavaFX methods.

- `EditActionUtils` : really useful utils to implements your JavaFX controls listeners that will create `UndoRedoActionI`
- `CopyUtils` : can be used to duplicate any `XMLSerializable` component using [(de)serialization](#serialization) mechanisms. Can be used to quickly implement a `DuplicableComponentI`
- `ConfigurationComponentUtils` : some methods to find components in a configuration (key option, keys, etc)
- `FXControlUtils` : quickly create most used FX controls
- `DialogUtils` :  quickly create any dialogs

### Threading

Note that action and event are mostly generated out of the JavaFX Thread. This means that if you have to modify UI components, you should explicitly do it on the FXThread (you can use `FXThreadUtils.runOnFXThread()` to do so)

### Translations

#### Files

Each language file is a basic XML containing key/value for translation. It is recommended to prefix all your keys with your plugin context, this will avoid collision with other translations.

```xml
<texts>
	<text key="example.plugin.demo.text">J'adore intégrer des {}\nbasiques</text>
</texts>
```

The value for each translation can use `{}` to inject variable on runtime in Translation. It can also use `\n` or `\t` to insert newlines or tabs. Note that newlines or tabs injected without this specific way are not kept in resulting translation.

#### Code API

Translation will then be used later in the code like this :

```java
// Use as static value
Object firstParamObj = 42; // will be converted to String with "toString()"
String resultingTranslationString = Translation.getText("example.plugin.demo.text", firstParamObj);
// Use as JavaFX property value (changing with injected parameters)
IntegerProperty firstParamInt = new SimpleIntegerProperty(42);
StringBinding resultingTranslationProperty = TranslationFX.getTextBinding("example.plugin.demo.text", firstParamInt);
firstParamInt.set(0);
// resultingTranslationProperty has changed
```

Injected parameters can mix :
- JavaFX properties, any `ObservableValue` where the resulting value is got from `ObservableValue.getValue()`
- Raw values (primitive or object) where the resulting value is got from `Object.toString()`

It's recommanded to use `TranslationFX` only when you need changing translation as this relies on binding and change listener consuming more performance than simple translation generated once by `Translation`.

### Serialization

#### Principle

When you create new element with plugin API (actions, key options, etc...), most of these elements implements the `XMLSerializable` interface indicating they can be saved with the configuration. Serialization is done with [JDOM library](http://www.jdom.org/) and `XMLObjectSerializer` utils class.

Classes implementing this interface should define two methods :

```java
@Override
public Element serialize(final IOContextI contextP) {
    return XMLObjectSerializer.serializeInto(StartSpellGameAction.class, this, super.serialize(contextP));
}

@Override
public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
    super.deserialize(nodeP, contextP);
    XMLObjectSerializer.deserializeInto(StartSpellGameAction.class, this, nodeP);
}
```

#### Basic (de)serialization

`XMLObjectSerializer.serializeInto(...)` and `XMLObjectSerializer.deserializeInto(...)` will try to (de)serialize all the object attribute (that are not static/final/transient) to/from a given JDOM element.

Each attribute is **serialized based on its name**, so be careful changing names! Also, a lot of element implemented by plugin (actions, key options, etc.) are instanciated using reflection, so they **should have a empty parameter default constructor**. Reflection also use the class simple name (`Class.getSimpleName()`) so changing a element type name can also be dangerous.

**Compatible types**
- primitive types : `int`, `double`, `long`, `float`, `boolean`
- primitive wrapper : `Integer`, `Double`, `Long`, `Float`, `Boolean`
- string : `String`
- JavaFX property wrapper (these can final) : `IntegerProperty`, `DoubleProperty`, `LongProperty`, `FloatProperty`, `BooleanProperty`, `StringProperty`
- `ObjectProperty` if combined with `@XMLGenericProperty`
- `javafx.scene.paint.Color`
- `java.util.Date`
- `java.io.File`
- enums : based on `name()` and `Enum.valueOf(...)`, so changing the name can cause problem

#### Advanced (de)serialization

If an element has to (de)serialize children, it should be done manually by calling its children `XMLSerializable` methods.

Generic type in `ObjectProperty` can be serialized using `@XMLGenericProperty`

Custom serialization mechanism can be implemented using `@XMLCustomProperty` and `CustomPropertyConverter`.

#### Backward compatibility

Missing attributes from DOM element will not throw Exception (but will log a message in WARN) so it is not a problem to add new attributes as they will be added to the next serialization.

However, if your attribute name change or your behavior, you should take care of handling the backward compatibility manually in `deserialize(...)`. In the following example, we renamed attribute `enableReplaceColorByTransparent` to `enableReplaceColor` so in the deserialize method we handle manually the mapping.

```java
if (element.getAttribute("enableReplaceColorByTransparent") != null) {
    XMLUtils.read(enableReplaceColor, "enableReplaceColorByTransparent", element);
}
```

#### Use mode (de)serialization

Most of the (de)serialization mechanism is only used to save modified data in edit mode.

However, it can be interesting to save information that will should be read/write in use mode. It is possible to do it with element implementing `UseInformationSerializableI` (which is the case of most the plugin element). Here is the example of the note key option action implementation :

```java
@Override
public void serializeUseInformation(Map<String, Element> elements) {
    super.serializeUseInformation(elements);
    GridPartKeyComponentI parentKey = this.parentComponentProperty().get();
    if (parentKey != null) {
        Element elementNodeContent = new Element(NODE_NOTE_CONTENT);
        elementNodeContent.setText(getTextToSerialize());
        elements.put(parentKey.getID(), elementNodeContent);
    }
}

@Override
public void deserializeUseInformation(Map<String, Element> elements) throws LCException {
    super.deserializeUseInformation(elements);
    GridPartKeyComponentI parentKey = this.parentComponentProperty().get();
    if (parentKey != null) {
        Element existingNote = elements.get(parentKey.getID());
        if (existingNote != null) {
            FXThreadUtils.runOnFXThread(() -> this.savedText.set(existingNote.getText()));
        }
    }
}
```

Note that use information should be saved with a unique ID that will not change between two run, so using a component ID is most of the time a good idea (or a static ID if the saved information is global).

### App icons

A good pratice to integrate icons in LifeCompanion is to save SVG of your icons and to only integrate the good sized icons into your app resources. Recommanded size are always based on the max. width/height : if your icon ratio is 3/4, for a 32x32 size your icon should be 24*32.

Original LifeCompanion SVG icons can be found in [**res/icons**](../res/icons/)

Most of the icons are created with the help of [SVGRepo website](https://www.svgrepo.com/)

Here are the LifeCompanion expected icons size :

| TYPE|SIZE (PX)|EXAMPLE|
|-|-|-|
|use actions / use event				      |32x32|![Image use action/event](../lifecompanion/lc-app/src/main/resources/icons/use-actions/categories/show/icon_next_page_stack.png)|
|add component icon (in create tab)	|32x32|![Image add component](../lifecompanion/lc-app/src/main/resources/icons/component/add_grid.png)|
|key options indicator  				    |32x32|![Image add component](../lifecompanion/lc-app/src/main/resources/icons/component/options/icon_type_variable_info.png)|


### Colors

LifeCompanion try to respect a global graphic theme. Here are the colors used in differents components.

| DESCRIPTION/COMPONENT|COLOR|CODE|
|-|-|-|
|use actions/event main       |![#d2d2d2](https://placehold.co/15x15/d2d2d2/d2d2d2.png)|`#d2d2d2`|
|use actions/event constrast  |![#aeaeae](https://placehold.co/15x15/aeaeae/aeaeae.png)|`#aeaeae`|
|key options indicator        |![#f44336](https://placehold.co/15x15/f44336/f44336.png)|`#f44336`|
|primary color - base         |![#0395f4](https://placehold.co/15x15/0395f4/0395f4.png)|`#0395f4`|
|primary color - dark         |![#0277bd](https://placehold.co/15x15/0277bd/0277bd.png)|`#0277bd`|
|primary color - light        |![#03bdf4](https://placehold.co/15x15/03bdf4/03bdf4.png)|`#03bdf4`|
|second color - base          |![#f44336](https://placehold.co/15x15/f44336/f44336.png)|`#f44336`|
|second color - dark          |![#c62828](https://placehold.co/15x15/c62828/c62828.png)|`#c62828`|
|second color - light         |![#ef9a9a](https://placehold.co/15x15/ef9a9a/ef9a9a.png)|`#ef9a9a`|
|third color - base           |![#ff9800](https://placehold.co/15x15/ff9800/ff9800.png)|`#ff9800`|
