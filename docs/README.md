# LifeCompanion documentation

## TL;DR

LifeCompanion app and components are built using Gralde and Java+JavaFX 18.

To build and run LifeCompanion, you should be familiar with these technologies.

## Structure

- **lifecompanion-framework** : framework that handle auto update and installation mechanisms
    - **lc-framework-commons** : commons API and lib for server, client, and apps
    - **lc-framework-commons-fx** : commons API and lib for client, and apps (with JavaFX dependency) - this lib was
      separated because server side doesn't need JavaFX
    - **lc-framework-client** : lib for server client (to call web services)
    - **lc-framework-server** : installation and update server (to deploy, update and publish updates on apps)
- **lifecompanion** : LifeCompanion application core, launcher and installer
    - **lc-app** : LifeCompanion JavaFX application
    - **lc-app-launcher** : launcher to run LifeCompanion (generate exe on Windows and script for Unix/Mac)
    - **lc-installer** : JavaFX application to install LifeCompanion
    - **lc-tool-scripts** : Java scripts to do various things (create prediction data, generate image dictionaries, some
      test/debug, etc.)
    - **buildSrc** : build scripts to help LifeCompanion builds and update publishing
- **lifecompanion-plugins** : contains one Gradle project per official plugin (content described in [plugin documentation](PLUGIN.md))
- **env** : contains build env configurations
- **libs** : external projects and libraries used by LifeCompanion or the framework
    - **win-sapi-voicesynthesizer-gap** : local server allowing voice synthesizer via Windows SAPI (C# project)
    - **win-input-gap** : two scripts made with AutoHotKey to send and receive global key event (useful for virtual
      keyboard implementation on Windows)
    - **win10-voices-to-sapi** : tool that copy Windows 10 voices to SAPI voice (quick Python script using Windows
      registry)
    - **old-code-snippet** : old code that we would like to keep for a later use
- **res** : commons resource for LifeCompanion (icons, licence, etc)
    - **app-icons** : svg source for icons used in applications
    - **lifecompanion-logo** : svg source for LifeCompanion and partners logos + exports from svg
    - **default-data** : contains default file to be copied in app data folder
    - **license** : code template for copyright notice
- **docs** : contains LifeCompanion app and framework documentation

**DISCLAIMER : LifeCompanion was a running project long before opening its source code to the community. Some parts may
be : unoptimized, dirty, untested, weird... We are aware that some code, build, deployment could be improved, but as the
development is heavely prioritized and constrained by costs, only some key features are focused on 😊 That said, you're
welcome to contribute !**

## Development

- [Building/running LifeCompanion](BUILD.md)
- [Publishing updates (official devs)](UPDATE.md)
- [Extending LifeCompanion with plugins](PLUGIN.md)

## Others

- [Read contributing guidelines](CONTRIBUTING.md)
- [Read interesting tech notes](TECH_NOTES.md)
