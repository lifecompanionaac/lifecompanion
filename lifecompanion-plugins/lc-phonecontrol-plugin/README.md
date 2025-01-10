# `lc-phonecontrol-plugin`
An easy way to connect your Android phone to LifeCompanion for receiving and sending SMS, making calls, and more.

## Team
**Students from IUT Vannes, 2024** :
- Lussandre Lederrey ([`@EDM115`](https://github.com/EDM115))
- Allan Maccrez ([`@EuphoriaReal`](https://github.com/EuphoriaReal))
- Théophile Delattre ([`@TheophileDelattre`](https://github.com/TheophileDelattre))
- Nathan Secrétin ([`@MichelBanane`](https://github.com/MichelBanane))

This plugin is based off the work of the 2 other teams of students from IUT Vannes, 2023 :
- **Plugin 1** : [lifecompanion/lifecompanion@5fb8344/lifecompanion-plugins/lc-phonecontrol-plugin-1](https://github.com/lifecompanion/lifecompanion/tree/5fb83449e301e1850938433c139b520efed5a9e6/lifecompanion-plugins/lc-phonecontrol-plugin-1)
  - Oscar Pavoine ([`@OscarGitH`](https://github.com/OscarGitH))
  - Baptiste Guerny ([`@BatLeDev`](https://github.com/BatLeDev))
  - Simon Le Chanu ([`@Sh3spas`](https://github.com/Sh3spas))
  - Anthony Hascöet ([`@Anthony1406`](https://github.com/Anthony1406))
- **Plugin 2** : [lifecompanion/lifecompanion@5fb8344/lifecompanion-plugins/lc-phonecontrol-plugin-2](https://github.com/lifecompanion/lifecompanion/tree/5fb83449e301e1850938433c139b520efed5a9e6/lifecompanion-plugins/lc-phonecontrol-plugin-2)
  - Noé Pierre ([`@noepierre`](https://github.com/noepierre))
  - Maxime Hermier ([`@Siiiil3nt`](https://github.com/Siiiil3nt))
  - Clément Bellier
  - Anatole Derrien ([`@anatolederr`](https://github.com/anatolederr))

## Dev
Please check the [`lc-phonecontrol-plugin/DEV.md`](DEV.md) file.

## Usage
Once built, open LifeCompanion, and go into "Préférences & Infos" > "Extensions" > "Ajouter par un fichier" and select the JAR file.  
Relaunch LifeCompanion, and the plugin should be available.  

Our plugin is complete and customizable, but to ease its usage you can find a ready-made configuration that will cover all your needs. It is located at [`src/main/resources/configurations/fr_phonecontrol-config.lcc`](src/main/resources/configurations/fr_phonecontrol-config.lcc).  

Once the configuration loaded *(it is recommended to connect your phone to the PC, either via cable ~~or Bluetooth~~ before starting LifeCompanion)*, go into "Accueil" > "Paramètres généraux" > "Contrôle du téléphone".  
Here's a quick overview of the available options :  

### Paramètres généraux
- **Sélectionner un appareil** : Choose the device you want to control on the dropdown list. The "Actualiser la liste" button will refresh the list.
- **Activer automatiquement le haut parleur durant un appel** : Automatically enable the speakerphone when making a call.
- **Intervalle de rafraîchissement** : The refresh rate of the plugin, meaning how often it will make requests to the phone to check for the call status and new messages. It is recommended to keep it at 3 seconds to avoid overload and race conditions.

### Protocole de communication
- **Sélectionner le protocole de communication** : Choose the protocol you want to use to communicate with your phone. The plugin supports both USB ~~and Bluetooth~~.

### Installation de l'application
- **Installer l'application** : Click to install the app on the selected phone. If the app is already installed, it will update it to the latest bundled version. If the latest version is already installed, it will open the app.
