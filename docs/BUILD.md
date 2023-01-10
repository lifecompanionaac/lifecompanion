# Building/running LifeCompanion

# Development

Development environment installed and tested on Windows 10/11 and Ubuntu.

Used JDK (in IntelliJ and builds) is [Eclipse Temurin™](https://adoptium.net/temurin/releases/)

## Build and run projects

### To use it directly on Gradle (optional)

1. Download **JDK 18** for your platform : [Eclipse Temurin™](https://adoptium.net/temurin/releases/)
1. Configuration Gradle to run with the installed JDK (**~/.gradle/gradle.properties** : add _org.gradle.java.home_)
1. This can sometimes be useful to configure **JAVA_HOME** env configuration

### To use it in IntelliJ

1. Install **[IntelliJ IDEA Community Edition](https://www.jetbrains.com/fr-fr/)** (> 2019.2.4)
1. Open **lifecompanion-framework** project : File > Open
1. Add module **lifecompanion** : File > New > Module from existing sources
1. Configure **IntelliJ project JDK** : File > Project Structure > Project settings / Project
   1. Language level : select 16 (not preview)

### How to build/run LifeCompanion application

1. Build and publish **lifecompanion-framework** libs : run task `gradlew publishToMavenLocal` in **lifecompanion-framework**
1. Copy **data** folder from an official LifeCompanion installation in **lifecompanion/lc-app/data** - (you can copy it from S3 resource bucket, if you have access to it)
1. Copy **installation.properties** from **res/default-data** to **lifecompanion/lc-app/data**
1. In **installation.properties** change **userDataDirectory** to your a wanted path (e.g. _MyDocumens/LifeCompanion-Dev_)
1. Run task `gradlew :lc-app:run` in **lifecompanion**

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

If you want to build a custom version : you can disable security certificate.
If you want to build a production version : you may need to import again the security certificate (password is in LifeCompanion KeePass)

### How to build/run Win Input GAP

1. Install **[AutoHotKey](https://www.autohotkey.com/)** (tested with 1.1.33.10)
1. Modify or compile your script (you can use LifeCompanion ico)

## Create offline application package

It is possible possible to create custom local images (offline) to test your image generation before creating offical updates.

Before, configure your local.env to add AWS access/secret properties (cf [update part](UPDATE.md))

Then, run `gradlew prepareOfflineApplication`. This will create in **offline** directory a subdirectory per system with all the needed element to run LifeCompanion on your computer.

## Troubleshooting

### Bad encoding on Windows dev env

1. For IntelliJ, in **idea64.exe.vmoptions** file, add two lines : `-Dfile.encoding=UTF-8` and `-Dconsole.encoding=UTF-8`
1. For Gralde, in **HOME/.gradle/gradle.properties**, add one line : `org.gradle.jvmargs=-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8`

### Note on Gradle command

Note that in the following document, when we say "run gradle task... in XXX" you should then run the gradle task in the given folder (lifecompanion or lifecompanion-framework) from your command line or IDE. For example, to run LifeCompanion from command line : `cd lifecompanion` then `gradle :lc-app:run`
