<p align="center">
  <img src="https://github.com/LifeCompanionAAC/lifecompanion-fv/raw/master/resources/lifecompanion-logo/export/lifecompanion_title_icon_600px.png">
</p>

# LifeCompanion AAC

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

LifeCompanion mono repository containing LifeCompanion app, server, side projects, resources and documentation.

**DISCLAIMER : LifeCompanion was a running project long before opening its source code to community. Some parts may be : unoptimized, dirty, untested, weird... We are aware that some code, build, deployment could be improved, but as the development is heavely priorized and constrainted by costs, only some key features are focused :-) That said, you're welcome to contribute !**

# General information

### [lifecompanionaac.org](https://lifecompanionaac.org)

LifeCompanion is a **free custom-made digital assistant for Augmentative and Alternative Communication and computer access.** Highly customizable, it can be used as communication assistance software thanks to its speech synthesis, but also as computer access tool (visual/virtual keyboard, mouse movements, shortcuts, etc.)

LifeCompanion is developed since 2015 by [CMRRF Kerpape](http://kerpape.mutualite56.fr/fr) (rehabilitation center located in Lorient, France) and [CoWork'HIT](https://coworkhit.com/) (innovative center on autonomy and technology located in Lorient, France).
It has been used in various reasearch and innovation projects, and was recently supported by French ["Agence du Numérique en Santé"](https://esante.gouv.fr/) for [Structures 3.0 project](https://lifecompanionaac.org/categories/projects/lc-ms-structures-3-0)

LifeCompanion is currently available in french only, but we are seeking for partners to translate it.


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

