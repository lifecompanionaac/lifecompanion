# LifeCompanion - dev notes

This page contains detailled information on LifeCompanion app and side projects developpements.

# Environments

There is three configured environment in LifeCompanion

| ID		| Description													|
|-----------|---------------------------------------------------------------|
|**local**	|developper local env on his own machine						|
|**dev**	|staging environment to test on production configuration		|
|**prod**	|production configuration										|

Each of these environment has its own configuration, to be runned and to be deployed on. It is possible to create a full running architecture for each env (update server, launcher, installer, app)

When calling specific Gradle script to deploy updates (see [Creating updates](#markdown-header-creating-updates)), you can inject env with `-Penv=prod` argument to Gradle commands. Default environment is always `local`

# Branches organization

TODO : main/develop/features


# Development

Development environment installed and tested on Windows 10 and Ubuntu.

## Note on Gradle command

### Gradle task designation

Note that in the following document, when we say "run gradle task..." you should then run the gradle task in the given folder (lifecompanion or lifecompanion-framework) from your command line or IDE.
For example, to run LifeCompanion from command line : `cd lifecompanion` then `gradle :lc-app:run`

### Gradle location

Note that should be carefull in calling your project Gradle execution vs your globally installed Gradle : when you call in command line `gradle run` this could call another Gradle env. than your local env.
To solve that, explicitly call your Gradle exe on Windows call : `.\gradlew.bat :myproject:command`

## Installing and running developpment environment

### How to install projects in an IDE

1. Download **JDK 16** for your platform : [AdoptOpenJDK](https://adoptopenjdk.net/)
1. Configuration Gradle to run with the installed JDK (**~/.gradle/gradle.properties** : add *org.gradle.java.home* see bellow for complete example file)
1. This can sometimes be useful to configure **JAVA_HOME** env configuration
1. Install **[IntelliJ IDEA Community Edition](https://www.jetbrains.com/fr-fr/)** (> 2019.2.4)
1. Open  **lifecompanion-framework** project : File > Open
1. Add module **lifecompanion** : File > New > Module from existing sources
1. Configure **IntelliJ project JDK** : File > Project Structure > Project settings / Project
	1. Language level : select 16 (not preview)
	1. Project SDK : add the downloaded JDK path
1. Configure **IntelliJ Gradle JDK** : File > Settings
	1. Build > Gradle
	1. Build JVM : Project SDK (16) (on both module)

### How to build/run LifeCompanion application

1. Build and publish **lifecompanion-framework** libs : run task `gradle publishToMavenLocal` in **lifecompanion-framework**
1. Copy **data** folder from an official LifeCompanion installation in **lifecompanion/lc-app/data** - if you're an official dev, you can copy it from S3
1. Copy **installation.properties** and **launcher.properties** from **resources/default-data** to **lifecompanion/lc-app/data** (in *installation.properties* change *userDataDirectory* to your wanted path)
1. Run task `gradle :lc-app:run` in **lifecompanion**

### How to build/run LifeCompanion update server

1. Install **[PostgreSQL](https://www.postgresql.org/)** and run it
1. Configure **PostgreSQL** with a dedicated database
1. Check port and database in **lc-framework-server/build.gradle** (in *applicationDefaultJvmArgs**)
1. Run task `gradle :lc-framework-server:run` in **lc-framework-server**
1. First run will create tables on database
1. If you want to insert default data, see [Deploy LifeCompanion server update in production](#markdown-header-deploy-lifecompanion-server-update-in-production)

### How to build/run Win SAPI Voice Synthesizer GAP

1. Install **[Visual Studio](https://visualstudio.microsoft.com/fr/)**
1. Open project in Visual Studio

### How to build/run Win Input GAP

1. Install **[AutoHotKey](https://www.autohotkey.com/)** (tested with 1.1.33.10)
1. Modify or compile your script (you can use LifeCompanion ico)

### Troubleshooting

#### Bad encoding on Windows dev env

1. For IntelliJ, in **idea64.exe.vmoptions** file, add two lines : `-Dfile.encoding=UTF-8` and `-Dconsole.encoding=UTF-8`
1. For Gralde, in **HOME/.gradle/gradle.properties**, add one line : `org.gradle.jvmargs=-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8`

## Installing update build environment

To build LifeCompanion updates (on app, installer, launcher, server...) you should have configured various Gradle properties.

### Base configuration

Most of the gradle properties are already configured in *lifecompanion/gradle.properties*

The properties that can be changed depending on your local machine are :

- **innosetup.path** : path to InnoSetup installation
- **lc.build.resources.directory** : path to the software resource directory (when publishing updates) - this path can be absolute or relative to *lifecompanion/lc-app*

Other properties are configured for default environments, but can be changed

### Target environment configuration

Target environment are configured in **env** directory. When you first run LifeCompanion, it will automatically create **env/.env.local** file from **.env.example**.

If you want to define other environment, you can create configuration file in **env** directory, for example **env/.env.prod** file.

An .env file contains following properties :

- **lifecompanion.framework.server.url** :path to update server to download/upload updates
- **lifecompanion.framework.server.login** : login to connect to update server and upload update
- **lifecompanion.framework.server.password** : password to connect to update server and upload update
- **lifecompanion.app.server.url** : path to LifeCompanion website
- **lifecompanion.app.server.public_key** : public key for LifeCompanion website API

### Env var configuration

#### On dev machine

- **HEROKU_API_KEY** : if you need to publish server builds (Heroku API key should be generated on your account)
- **LIFECOMPANION_JDK_JFX_PATH** : will contains platforms JDK and JavaFX binaries (to publish updates). This variable is optionnal, if not specified, default path is **~/.lifecompanion-jdk-jfx**

#### On server machine (you may need to create these on your dev. env when needed)

- **DATABASE_URL** : (generated by heroku ) database URL (example : *postgres://username:password@host:port/database_name*) or create by dev. on local machine
- **JWT_SECRET** : JWT secret to generate auth. tokens
- **LC_PLATFORM_API_TOKEN** : API token to call stats API
- **LC_PLATFORM_URL** : URL for stats API (example *https://lifecompanionaac.org*)
- **LC_PLATFORM_URL_PASSWORD** : (optionnal) query parameters to append to LC_PLATFORM_URL (example *?app_password=password*)
- **AMAZON_S3_BUCKET** : Amazon S3 bucket
- **AMAZON_S3_ACCES_KEY** : Amazon S3 access key
- **AMAZON_S3_SECRET** : Amazon S3 secret
- **AMAZON_S3_REGION** : Amazon S3 region (example *eu-west-3*)

## Creating updates

**This part is meant to be read by official developpers and is not needed by anyone who just want to contribute.**

All task bellow are depending on a destination environment and should be run with `-Penv=dev` or `-Penv=prod` argument (on Gradle task) if you want to target other env. than local.

Creating updates depends on custom Gradle task and plugin, all located in *lifecompanion/buildSrc*

### Create LifeCompanion update

*When file are too big for direct upload during update (e.g. image zips, file > 100 MB), file should be uploaded manually in file storage and then linked with `-presetStorageIds`*

1. Check `lifecompanion.app.version` in *lifecompanion/gradle.properties*
1. Check `visibility` in *lifecompanion/gradle.properties*, it can be PREVIEW, PUBLISHED or HIDDEN
1. Check path for file to unzip : `TO_UNZIP_PATH` in *PublishApplicationTask*
1. Check path for file with manual storage : `PRESET_STORAGE_IDS` in *PublishApplicationTask*
1. Commit and tag repo with **lifecompanion/lc-app/X.X.X**
1. Run `gradle :lc-app:publishApplication -Penv=prod` in **lifecompanion** with `-Penv=prod` filled with your targeted env

### Create LifeCompanion installer update

*Installer updates should be generated on Windows as InnoSetup is used to generated the installer exe*

1. Check `lifecompanion.installer.version` in *lifecompanion/gradle.properties*
1. Check `visibility` in *lifecompanion/gradle.properties*, it can be PREVIEW, PUBLISHED or HIDDEN
1. Commit and tag repo with **lifecompanion/lc-installer/X.X.X**
1. Run `gradle :lc-installer:publishInstaller -Penv=prod` in **lifecompanion** with `-Penv=prod` filled with your targeted env

### Create LifeCompanion launcher update

1. Check that `org.lifecompanion.launcher.LauncherApplication` and `lifecompanion/lc-app-launcher/build-src/lifecompanion.sh` are coherent with `sharedJvmArgAppTest` in `lifecompanion/lc-app/build.gradle`
1. Check `lifecompanion.launcher.version` in *lifecompanion/gradle.properties*
1. Check `visibility` in *lifecompanion/gradle.properties*, it can be PREVIEW, PUBLISHED or HIDDEN
1. Commit and tag repo with **lifecompanion/lc-app-launcher/X.X.X**
1. Run `gradle :lc-app-launcher:publishLauncher -Penv=prod` in **lifecompanion** with `-Penv=prod` filled with your targeted env

### Deploy LifeCompanion server update in production

**Configure a production server**

1. Create Heroku app and Amazon S3 storage bucket
1. Add postgres on Heroku
1. Configure Heroku app with env var (see env var part)
1. Generate your login and password hash (for password hash, use `System.out.println(BCrypt.hashpw("password",BCrypt.gensalt()));`)
```
INSERT INTO app_user(id,login,password,role) VALUES ('A_RANDOM_UUID','MY_LOGIN','MY_PASSWORD_HASHED','ADMIN');
INSERT INTO application(id) VALUES ('lifecompanion');
```
*Example for admin/password (you can use that for your local env)*
```
INSERT INTO app_user(id,login,password,role) VALUES ('dbb0fd9b-bc96-4395-ab4b-f3b583c5fddf','admin','$2a$10$lxaKVIrNPOm8vg7YODdVBuBnMUDSIPAsrEP6cUEei0hXN0romnSUW','ADMIN');
INSERT INTO application(id) VALUES ('lifecompanion');
```

**Update the production server**

1. Update server version in build.gradle if needed
1. Check your scripts in **src/main/resources/sql/migrations** and add them to `DataSource.MIGRATIONS_SCRIPT_NAMES`
1. Check that you have the correct **HEROKU_API_KEY** env variable
1. Commit and tag repo with **lifecompanion-framework/lc-framework-server/X.X.X**
1. Run `gradle :lc-framework-server:publishServerUpdate -Penv=prod` in **lifecompanion-framework** with `-Penv=prod` filled with your targeted env (you can target dev environment and then promote it to prod on Heroku, or directly target prod env.)
1. If you have migration scripts, you can check them in Heroku log

## Create AAC symbols dictionaries

`ImageDictionariesCreationScript` (in **lifecompanion/lc-tool-scripts**) is used to create symbols dictionaries. It creates unique image dictionary with associated keywords (find duplicated images and associate their keywords)
It also resize images if needed and convert white background to transparent.

- **ARASAAC**
    - Database is installed locally (download from [ARASAAC PICTOGRAMMES - 6 april 2016](http://www.arasaac.org/descargas.php))
	- There is no modifications in the downloaded folder
- **SCLERA**
    - Database is extracted locally (download from  [Pictogrammes en français - 8 april 2020](https://www.sclera.be/fr/picto/telecharger))
	- Image in **tijd/FR** are renammed to *_nb*, *_gris* ou *_couleur* and moved to root folder
- **ParlerPictos**
    - Database is extracted locally (download from [ParlerPicto - 20 may 2020](http://recitas.ca/parlerpictos/))
	- Images are combined and label and NB images are removed
- **Mulberry Symbols**
    - Database is extracted locally (download from [Mulberry Symbols - 10 january 2021](https://mulberrysymbols.org/))
	- SVG are converted to PNG (400x400 px) with Inkscape in command line
	- Keywords and name are generated using automatic translations (using Google Translate API) - script `MulberrySymbolsCreationScript`
- **FontAwesome**
    - Database is extracted from FontAwesome font listing all the available icons
	- Keywords are generated using manual and automatic translations (using Google Translate API) - script : `FontAwesomeCreateScript`

---

# Specifications - notes

## Application and launcher arguments

- **-updateDownloadFinished** : launch application in "update" directory
- **-updateFinished** : launch application normally, should just delete the update directory
- **-enablePreviewUpdates** : enable preview versions for updates

## Update mechanism

1. Application is launched, running application detect if an update is available
1. If update available, copy previous **application** directory in **update** directory + download updated file
1. Once finished, update state is set to **DONE**
1. On next launcher launch, this launch application in **update** folder
1. Launched application knows it was updated (cmd arg flag) : block UI, copy its own content into **application** folder
1. Once content is copied, restart launcher with a fag to launch the up to date application in **application** folder
1. Launched application knows update is finished (cmd arg flag) : delete **update** directory

## Installation systems specific behavior

### MacOS (tested 10.15.4)

- **/Application/LifeCompanion.app/Contents** - directory *application* and *data*
- **[Home]/Documents/LifeCompanion** - user directory
- Folder **MacOS** : contains launch script (.sh script without extension) > think about having good relative path !
- Create **Info.plist** to describe app + add icon

### Windows (tested W10)

- **X:\ProgramData\LifeCompanion** - directory *application* and *data* *(from HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\Shell Folders reg key)*
- **X:\Users\Public\Documents\LifeCompanion** - user directory *(from PUBLIC env var)*
- File association : **lcc** and **lcp** *(with HKEY_CLASSES_ROOT reg key)*
- Desktop shortcut *(from PUBLIC env var)*
- Program shortcut *(from HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\Shell Folders reg key)*

### Ubuntu (tested Ubuntu 18.04.4 LTS)

- **/home/LifeCompanion/** - directory *application* and *data*
- **/home/Documents/LifeCompanion** - user directory
- Create **/home/.local/share/applications/LifeCompanion.desktop** to describe app + add icon

## Misc technical notes

- To use LOGBACK  SL4J on JDK11 : `requires java.naming; requires ch.qos.logback.classic;`
- When having Gradle variables in resources `outputs.upToDateWhen { false }`
- PostgreSQL driver : should be manually registered : `if (!org.postgresql.Driver.isRegistered()) {	org.postgresql.Driver.register();`


# TODO - to integrate

- Plugin launch command in dev `org.lifecompanion.dev.cp.arg=../lc-plugins/lc-homeassistant-plugin/build/libs/*`