<p align="center">
  <img src="https://github.com/LifeCompanionAAC/lifecompanion-fv/raw/master/resources/lifecompanion-logo/export/lifecompanion_title_icon_600px.png">
</p>

# LifeCompanion AAC

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

LifeCompanion mono repository containing LifeCompanion app, server, side projects, resources and documentation.

**DISCLAIMER : LifeCompanion was a running project long before opening its source code to community. Some parts may be : unoptimized, dirty, untested, weird... We are aweware that some code, build, deployment could be improved, but as the development is heavely priorized and constrainted by costs, only some key features are focused :-) That said, you're welcome to contribute !**

# General information

### [lifecompanionaac.org](https://lifecompanionaac.org)

**TODO : write short and clean general info for LC**

## Changelog

- [LifeCompanion feature changelog (french)](https://lifecompanionaac.org/categories/documentations/lifecompanion-changelog)
- [LifeCompanion dev updates changelog](documentation/CHANGELOG.md)

# Repo organization

- **lifecompanion-framework** : framework that handle auto update and installation mechanisms
    - **lc-framework-commons** : commons API and lib for server, client, and apps
	- **lc-framework-commons-fx** : commons API and lib for client, and apps (with JavaFX dependency) - this lib was separated because server side doesn't need JavaFX
	- **lc-framework-client** : lib for server client (to call web services)
	- **lc-framework-server** : installation and update server (to deploy, update and publish updates on apps)
- **lifecompanion** : LifeCompanion application core, launcher and installer
    - **lc-api** : contains core API and model for LifeCompanion (everything to run LifeCompanion in use mode)
	- **lc-config** : contains all the configuration UI and models
	- **lc-app** : JavaFX application that merge api and config and create LifeCompanion app - this is the project built to update LifeCompanion
	- **lc-app-launcher** : launcher to run LifeCompanion (generate exe on Windows and script for Unix/Mac)
	- **lc-installer** : JavaFX application to install LifeCompanion
	- **lc-tool-scripts** : Java scripts to do various things (create prediction data, generate image dictionaries, etc.)
	- **lc-plugins** : official LifeCompanion plugins (contains a sub project for each plugin)
	- **buildSrc** : build scripts to help LifeCompanion builds and update publishing
- **env** : contains build env configurations
- **libs** : external projects and libraries used by LifeCompanion or the framework
    - **win-sapi-voicesynthesizer-gap** : local server allowing voice synthesizer via Windows SAPI (C# project)
	- **win-input-gap** : two scripts made with AutoHotKey to send and receive global key event (useful for virtual keyboard implementation on Windows)
	- **win10-voices-to-sapi** : tool that copy Windows 10 voices to SAPI voice (quick Python script using Windows registry)
	- **old-code-snippet** : old code that we would like to keep for a later use
- **resources** : commons resource for LifeCompanion (icons, licence, etc)
    - **app-icons** : svg source for icons used in applications
	- **lifecompanion-logo** : svg source for LifeCompanion and partners logos
	- **default-data** : contains default file to be copied in app data folder
	- **license** : code template for License
	- **lifecompanion-logo** : contains all LifeCompanion communication logo
- **documentation** : contains LifeCompanion app and framework documentation

# Build and run from source

- [Dev notes page](documentation/DEV.md) : to understand LifeCompanion build/run/update process
- [Plugin API page](documentation/PLUGINS.md) : to develop LifeCompanion plugins

