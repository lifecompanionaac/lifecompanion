# PhoneControl Plugin

Les personnes en situation de handicap utilisatrices de LifeCompanion souhaitent souvent piloter un **téléphone** afin de passer/recevoir des appels ou envoyer/recevoir des SMS. Les téléphones sont malheureusement trop **peu accessibles** et peuvent nécessiter la mise en place de lourdes solutions d'aide pour être contrôlés. Ce **plugin** consiste donc à utiliser l'interface de LifeCompanion pour piloter un téléphones **android**.

## Auteurs

Ce projet à été développé dans le cadre d'un projet informatique par des étudiants du BUT Informatique de Vannes.  

[Oscar PAVONE](https://github.com/OscarGitH), [Baptiste GUERNY](https://github.com/BatLeDev), Simon LE Chanu et Anthony HASCÖET

## Sommaire

[TOC]

## Prérequis

1. **Mode Dévelopeur + débogage USB (Android) :**

Ici la documentation android suivant le modele du téléphone:

https://developer.android.com/studio/debug/dev-options?hl=fr

        1- Allez dans les Paramètres de votre téléphone.
        2- Tapez sur À propos du téléphone.
        3- Tapez 7 fois sur Numéro de Build pour activer le mode développeur.
        4- Revenez dans Paramètres.
        5- Tapez sur le menu Options développeurs qui vient de s'afficher.
        6- Cocher la case du Debugage USB
        7- Si vous avez l'option "Verifier les applis via USB" désactivé la

## Application Android

1. **Télécharger l'application**

Pour le bon fonctionnement du plugin, une application est à télécharger, pour cela rendez vous dans les "**Paramètres généraux**" de Life Companion puis dans "**Controle du Téléphone**"

**Selectionner** ensuite votre téléphone, le téléphone doit etre brancher et l'autorisation du Debugage doit avoir été accépté.

Si vous ne voyer pas votré téléphone, cliquer sur actualiser. Si vous ne le voyer toujours pas essayer de le rebrancher.

Une fois selectionner, cliquer sur "**Installer l'app**" et patienter quelque secondes.

2. **Autorisations**

Pour fonctionner l'application doit avoir accès aux contacts, aux messages et aux appels. Il faut donc lui donner les droit :

- Lancer l'application "**LC Service**" et accepter les autorisation

- Si les autorisations ne sont pas demandés au lancement de l'application, allez dans vos "**Paramètres**" puis dans "**Application**" et trouver "**LC Service**". Une fois trouver, aller dans **Autorisations** et autoriser les appels, les contacts et les messages.

3. **Lancer la configuration de test**

Si vous avez suivis les étapes, vous devriez etre capable de lancer la configuration de test et d'uttiliser le plugin !

## Personalisation

**Haut parleur :** Si vous souhaiter toujours appeler en haut-parleur, cocher la case dans l'onglet "Controle du Téléphone" dans "Paramètres Généraux"

**Fréquence d'actualisation :** Les appels et les messages sont actualisés toutes les 5 secondes par défaut, mais vous pouvez modifier cette fréquence.

## Les Actions

### SMS 

- **Monter / Descendre dans la liste de SMS :** Ces actions sont de base configurés sur les fleches pour naviguer entre les différent sms dans les conversations

- **Selectionner une conversation :** tout ce plugin est basé sur un système de conversation sélectionné (numéro de téléphone choisi). Il y a plusieurs manières de selectionner une conversation : par la liste de conversation, par un numéro paramétré ou par un champ de texte.
Une conversation sélectionné active l'actualisation des SMS uniquement sur cette conversion.

- **Deselectionner une conversation :** Cette action permet d'arreter l'actualisation d'un numéro de téléphone précis.

- **Envoyer un message :** Une fois la conversation sélectionnée, cete action envoie un message a partir du champ de texte.

- **Envoyer un message à un numéro spécifique :** Action combiné, envoie le contenu du champ de texte au numéro paramétré dans l'action

### Conversations 

- **Monter / Descendre dans la liste de contact :** Ces actions sont de base configurés sur les flèches pour naviguer entre les différentes conversations

### Appels 

- **Appeler un contact :** Cet action permet d'appeler le numéro sélectionné.

- **Appeler un contact à un numéro spécifique  :** Cette action permet d'appeler le numéro directement paramétré dans l'action.

- **Décrocher :** Cet action permet une fois déclencher de décrocher un appels avec la personne qui vous appeler.

- **Raccrocher un appel :** Cet action met fin à l'appel en cour.

### Actualisations 

- **Actualiser la liste des conversations :** Cette action est déclenchée automatiquement selon la [**fréquence d'actualisation**](#fréquence-dactualisation).
Elle permet d'actualiser les conversation et de détecter/compter le nombre de conversation non lues.

- **Actualiser la liste des SMS :** Cette action est déclenchée automatiquement selon la [**fréquence d'actualisation**](#fréquence-dactualisation).
Elle permet d'actualiser les SMS de detecter si l'utilisateur a reçu un nouveau message du contact sélectionné.
