# Building/running LifeCompanion

Development environment installed and tested on Windows 10/11 and Ubuntu.

Used JDK (in IntelliJ and builds) is [Eclipse Temurin™](https://adoptium.net/temurin/releases/)

## Build and run projects

### To use it directly on Gradle (optional)

1. Download **JDK 21** for your platform : [Eclipse Temurin™](https://adoptium.net/temurin/releases/)
1. Configuration Gradle to run with the installed JDK (**~/.gradle/gradle.properties** : add _org.gradle.java.home_)
1. This can sometimes be useful to configure **JAVA_HOME** env configuration

### To use it in IntelliJ

1. Install **[IntelliJ IDEA Community Edition](https://www.jetbrains.com/fr-fr/)** (> 2024.1)
1. Open **lifecompanion-framework** project : File > Open
1. Add module **lifecompanion** : File > New > Module from existing sources
1. Configure **IntelliJ project JDK** : File > Project Structure > Project settings / Project
   1. Language level : select 16 (not preview)

### How to build/run LifeCompanion application

1. Build and publish **lifecompanion-framework** libs : run task `gradlew publishToMavenLocal` in **lifecompanion-framework**
1. Copy **data** folder from an official LifeCompanion installation in **lifecompanion/lc-app/data** - (you can copy it from S3 resource bucket, if you have access to it)
1. Run task `gradlew :lc-app:run` in **lifecompanion**

If you need to pass argument to your instance, you can do it with `gradlew :lc-app:run --args="a configuration.lcc"`

### How to build/run LifeCompanion update server

1. Install **[PostgreSQL](https://www.postgresql.org/)** and run it
1. Configure **PostgreSQL** with a dedicated database
1. Check port and database in **lc-framework-server/build.gradle** (in _applicationDefaultJvmArgs_)
1. Run task `gradlew :lc-framework-server:run` in **lc-framework-server**
1. First run will create tables on database
1. To insert default data, see [Deploy LifeCompanion server update in production](#user-content-deploy-lifecompanion-server-update-in-production)

### How to build/run Win SAPI Voice Synthesizer GAP

1. Install **[Visual Studio](https://visualstudio.microsoft.com/fr/)**
1. Open project in Visual Studio

If you want to build a custom version : you can disable security certificate (Solution properties > Signature > Uncheck all)

If you want to build a production version : you may need to import again the security certificate (Solution properties > Signature > Strong encryption key > Change with **lifecompanion-sapi-gap.pfx**, the certificat password is in LifeCompanion KeePass).

### How to build/run Win Input GAP

1. Install **[AutoHotKey](https://www.autohotkey.com/)** (tested with 1.1.33.10)
1. Modify or compile your script (you can use LifeCompanion ico)

### [Detailed instructions to build/run LifeCompanion](BUILD_DETAIL.md)

## Create offline application package

It is possible possible to create custom local images (offline) to test your image generation before creating offical updates.

Before, configure your local.env to add AWS access/secret properties (cf [update part](UPDATE.md)). If you want your installation to be as close as possible as the production env, don't forget to also set the correct URL/keys to your env file !

Then, run `gradlew prepareOfflineApplication`. This will create in **offline** directory a subdirectory per system with all the needed element to run LifeCompanion on your computer. When running this task, you can add `lifecompanion.publish.application.persistent.data` Gradle property to avoid downloading fresh data from S3 on each build (run it `gradlew prepareOfflineApplication -Plifecompanion.publish.application.persistent.data`)

## Create offline debian package

It is possible to create custom local images packaged as *.deb* on Linux if needed.

This task is depends on the **prepareOfflineApplication** task, so read the documentation above to apply the requirements (env file).

Then, run `gradlew createDeb`. This will create in **debian** directory a *.deb* that can be used to install LifeCompanion on a Linux system and make it available in command line (with `lifecompanion`).

To install it, run (replace with the correct filename)

```shell
sudo apt install ./lifecompanion_1.5.0_x64.deb
```

The debian package will install LifeCompanion in :
- `/usr/local/bin/lifecompanion` : make the `lifecompanion` command available
- `/usr/share/lifecompanion/` : contains usual `application` and `data` directories
- `~/Documents/LifeCompanion/` : contains user data

## Troubleshooting

### Problem with accented char in code (CharacterToSpeechTranslation ...) : bad encoding on Windows dev env

1. For IntelliJ, in **idea64.exe.vmoptions** file, add two lines : `-Dfile.encoding=UTF-8` and `-Dconsole.encoding=UTF-8`
1. For Gralde, in **HOME/.gradle/gradle.properties**, add one line : `org.gradle.jvmargs=-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8`

### Cannot run Gradle commands

Note that in the following document, when we say "run gradle task... in XXX" you should then run the gradle task in the given folder (lifecompanion or lifecompanion-framework) from your command line or IDE. For example, to run LifeCompanion from command line : `cd lifecompanion` then `gradlew :lc-app:run`

Also note than `gradlew` command should be adapted to your system : `gradlew.bat` (Windows) and `gradlew` (Unix/Mac)

### Cannot run JLink on Unix

If you get the following error on Unix trying to create JLink build : `Error: java.io.IOException: Cannot run program "objcopy": error=2, No such file or directory`

You should install the following tool on the system : `sudo apt install binutils`

### LifeCompanion app run, but can't create any configuration or profile

You should check the `userDataDirectory` line in **lifecompanion/lc-app/data/installation.properties**. As this file is a classic Java property file, you should respect the [property file format](https://en.wikipedia.org/wiki/.properties#:~:text=properties%20is%20a%20file%20extension,known%20as%20Property%20Resource%20Bundles.)

This means that some char should be escaped, for example to define an absolute path on Windows, you line should be like : 
`userDataDirectory=E:\\temp\\lifecompanion-dev-user-data`

### Voice synthesizer not working on Unix systems

To run on Unix, the [PicoTTS](https://github.com/naggety/picotts) voice synthesizer should be installed (it is installed by the LifeCompanion Unix installer).

To install, run the following command in terminal : `sudo apt-get install libttspico-utils`
