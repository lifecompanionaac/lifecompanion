# Développement d'un plugin "phone control" pour l'application LifeCompanion

## Table des matières

- [LifeCompanion](#lifecompanion)
- [Description du Projet](#description-du-projet)
- [Qui Sommes Nous ?](#qui-sommes-nous-)
- [Technologies Utilisées](#technologies-utilisées)
- [Fonctionnalités](#fonctionnalités)
  - [Appel](#appel)
  - [Contacts](#contacts)
  - [Volume](#volume)
- [Utilisation du plugin](#utilisation-du-plugin)
- [Nouveautés - Version Juin 2024](#nouveautés---version-juin-2024)

## LifeCompanion

![](lc-logo.png)

LifeCompanion est un assistant numérique conçu pour améliorer la participation et l’autonomie dans la communication, l'accès à l'informatique ou l’éducation pour les personnes ayant des besoins spécifiques en matière de mobilité, de vision, d'audition ou de cognition.

LifeCompanion combine des fonctionnalités de logiciel d'aide à la communication grâce à sa synthèse vocale intégrée, ainsi que des options d'accès informatique variées, comme des claviers visuels, une assistance au déplacement de la souris, des raccourcis, facilitant ainsi l'accès aux outils numériques.

Ce logiciel, développé en partenariat avec des professionnels de la santé, vise à favoriser l'autonomie et la participation sociale tout en restant accessible au plus grand nombre grâce à sa diffusion en open source.

## Description du Projet

Ce projet consiste au développement d'un plugin pour l'application LifeCompanion créée par notre client M. THEBAUD (ingénieur au CMRRF de Kerpape). L'objectif de ce plugin est de permettre aux utilisateurs d'interagir avec un smartphone à travers l'interface de LifeCompanion.

## Qui Sommes Nous ?

Nous sommes une équipe de 4 étudiants en 2e année de BUT Informatique à l'IUT de Vannes. Ce projet est réalisé dans le cadre d'une SAE.

- PIERRE Noé [GitLab](https://gitlab.com/noepierre) [GitHub](https://github.com/noepierre) 
- HERMIER Maxime
- BELLIER Clément
- DERRIEN Anatole

## Technologies Utilisées

- **IntelliJ :** L'environnement de développement intégré que nous utilisons pour coder.
- **Gradle :** Notre outil d'automatisation de construction.
- **Java :** Le langage de programmation principal pour le projet.
- **JavaFX :** Le framework pour construire des interfaces utilisateur basées sur Java.
- **ADB (Android Debug Bridge) :** Un outil en ligne de commande qui facilite la communication avec les appareils Android.
- **Android Studio :** L'environnement de développement intégré pour Android que nous utilisons pour tester notre plugin.
- **GitLab :** Notre outil de gestion de version.
- **XML :** Le langage de balisage.

## Fonctionnalités

### Appel

**Passer un appel :**
- en tapant le numéro avec un pavé numérique inclus dans l'application
- en tapant le numéro sur le clavier de l'ordinateur

**Raccrocher un appel**

**Recevoir un appel**

### Contacts

**Afficher la liste des contacts du téléphone**

**Appeler un contact**

**Liste de favoris**

### Volume

**Augmenter ou diminuer le volume du téléphone**

## Utilisation du plugin

Un fichier [UTILISATION.md](Utilisation.md) est disponible à la racine du projet. Il contient les informations suivantes :
- Prérequis
- Mode Développeur + débogage USB
- Installation de l'extension
- Configuration
- Conseils d'utilisation
- Avant d'utiliser la configuration
- Vidéo de démonstration d'utilisation

## Nouveautés - Version Juin 2024

Correction de bugs et optimisation du code :
- **Corrigé :** Gestion de la connexion et de la déconnexion de l'appareil : le plugin
fonctionne correctement uniquement si le téléphone est connecté au
démarrage de l'application. Si le téléphone est connecté après, il sera
détecté dans Paramètres généraux > Contrôle du téléphone, mais les
fonctionnalités ne fonctionnent pas
- **Corrigé :** Parfois, lorsqu'on passe un appel en composant un numéro, l'appel est
bien lancé sur le téléphone, mais sur l'application, la fenêtre d'appel en
cours s'ouvre brièvement puis se ferme, alors que l'appel est toujours
en cours. Cela semble se produire juste après le lancement de
l'application, mais ce n'est pas certain.
- **Corrigé :** Certains utilisateurs ont rencontré des problèmes de latence dans
l’application.

Ajout de nouvelles fonctionnalités :
- **Ajouté :** Ajout de la durée de l’appel dans la fenêtre d’appel en cours.
- **Ajouté :** Ajout d'une barre de recherche dans la section "Contacts", permettant
à l'utilisateur de saisir le nom d'un contact. Les résultats de la
recherche seront actualisés en temps réel au fur et à mesure de la
saisie de l'utilisateur.
- **Ajouté :** Choix du téléphone à utiliser dans Paramètres généraux > Contrôle du
téléphone

Une vidéo de démonstration des nouvelles fonctionnalités est disponible : [Vidéo de démonstration](video_demonstration_v2.mp4)