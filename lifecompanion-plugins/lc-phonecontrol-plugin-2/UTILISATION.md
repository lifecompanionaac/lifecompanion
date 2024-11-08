# Utilisation du plugin

## Table des matières

- [Prérequis](#prérequis)
  - [ADB](#adb)
  - [Mode Développeur + débogage USB](#mode-développeur--débogage-usb)
- [Installation de l'extension](#installation-de-lextension)
- [Configuration](#configuration)
- [Conseils d'utilisation](#conseils-dutilisation)
- [Avant d'utiliser la configuration](#avant-dutiliser-la-configuration)
- [Vidéo de démonstration d'utilisation](#vidéo-de-démonstration-dutilisation)

## Prérequis

### ADB

Cette extension requiert que ADB soit installé sur votre ordinateur.
            
- Télécharger [Android platform tools](https://dl.google.com/android/repository/platform-tools-latest-windows.zip)
        
- Ensuite, extraire le contenu du fichier téléchargé dans un dossier sur votre ordinateur

- Rechercher "Modifier les variables d'environnement"

- Sélectionner la section "Variables d'environnement", sélectionner la variable "Path" dans  et cliquez sur "Modifier".

- Ajouter le chemin vers le dossier ADB en fin du champ "Valeur de la variable"

### Mode Développeur + débogage USB

Cette extension requiert que le mode développeur soit activé sur votre téléphone. Si vous ne savez pas comment faire, vous pouvez suivre ce [tutoriel](https://www.frandroid.com/comment-faire/tutoriaux/184906_comment-acceder-au-mode-developpeur-sur-android)

Lors de la première connexion USB de votre téléphone à votre ordinateur, vous devrez autoriser le débogage USB (une fenêtre pop-up devrait apparaître sur votre téléphone).

## Installation de l'extension

- Télécharger le fichier .jar de l'extension (version 2.0.0)
- Ouvrir LifeCompanion et ferme la fenêtre de configuration
- Appuyer sur le bouton en haut à gauche de l'application
- Sélectionner "Préférences et options"
- Sélectionner "Extensions" (supprimer l'extension "PhoneControl" si vous en avez déjà une)
- Cliquer sur "Ajouter par un fichier" et sélectionner le fichier .jar téléchargé précédemment

## Configuration

Une configuration fonctionelle et prête à l'utilisation a déjà été créée. Elle est disponible à cet emplacement : "lc-phonecontrol-plugin\src\main\resources\configurations" et se nomme "phonecontrol-configuration_principale.lcc".

Au lancemement de l'application, il vous suffit de cliquer sur "Ajouter une configuration" et de sélectionner le fichier "phonecontrol-configuration_principale.lcc" pour l'importer.

Nous vous conseillons d'utiliser cette configuration ou de l'utiliser comme base pour créer votre propre configuration car elle contient tous les éléments nécessaires pour utiliser l'extension.

## Conseils d'utilisation

Nous vous conseillons de connecter par USB votre téléphone **avant de lancer l'application** pour éviter de potentiels problèmes de connexion. Vous pouvez vérifier que votre téléphone est bien connecté en ouvrant les paramètres généraux de l'application et en allant dans la section "Contrôle du téléphone". Si votre téléphone est bien connecté, vous devriez voir son nom.

Nous vous conseillons également de **connecter votre téléphone à une enceinte, à un casque ou à des écouteurs** pour entendre votre interlocuteur même si votre téléphone n'est pas à proximité de vous. Des boutons pour augmenter ou diminuer le volume sont disponibles dans l'application.

## Avant d'utiliser la configuration

Avant d'utiliser la configuration, il peut être nécessaire de la modifier, notamment la page "Contacts Favoris" pour ajouter dans les cases les noms de vos contacts favoris. Pour cela, il vous suffit simplement de cliquer sur la case et de taper le **nom exact** de votre contact et d'affecter les actions "Supprimer tout le texte" et "Ecrire le texte de la case" à la case.

## Vidéo de démonstration d'utilisation

Une vidéo de démonstration d'utilisation de l'extension est disponible : [Vidéo de démonstration](video_demonstration.mp4)