# LIFECOMPANION CHANGELOG

## 1.3.2 - 28/02/2022

### Fonctionnalités

- Les extensions sont maintenant mises à jour en même temps que l'application

### Corrections/ajustements

- Ticket #105 : correction problème lors de l'export d'une configuration
- Ticket #106 : correction d'un problème avec l'événement "Jour de la semaine"
- Correction des lanceurs sur les systèmes Unix/Mac
- Correction d'un problème qui pouvait bloquer LifeCompanion sur Unix
- Correction de la taille de certains éléments

## 1.3.1 - 22/02/2022

### Corrections/ajustements

- Correction d'un rare problème avec les listes de cases

## 1.3.0 - 21/02/2022

### Fonctionnalités

- Ticket #53 : nouveau **champs pour sélectionner des couleurs**
	- Meilleur panel de couleur par défaut
	- Couleurs de fonds/de contour différentes par défaut
	- Sélection de "transparent" directement
	- Configuration d'une couleur facilité
- Les **champs permettant de sélectionner des grilles, cases, configuration... font peau neuve**
	- Plus de problème d'affichage
	- Bien meilleure qualité des éléments affichés et intégration des photos
	- Recherche optimisée
- Il est maintenant possible de fermer la configuration courante
	- Optimisation de la mémoire
	- Proposition d'actions en fonction
	- Centralisation des actions au même endroit
- (Technique) Les mises à jour du lanceur et de l'application sont maintenant communes
- (Technique) Architecture et organisation : préparation à l'open-source, simplification de la maintenance/des évolutions

### Corrections/ajustements

- Optimisation de la mémoire
- Amélioration de la qualité de l'export PDF
- Ticket #20 : changement d'icône pour les cases dans les listes de case
- Ticket #21 : déplacement du champs de recherche dans les listes de cases
- Ticket #22 : lors du clic sur "Nouvelle configuration", cela ouvre maintenant la même fenêtre que lorsqu'on sélectionne "Ajouter"
- Ticket #29 : le déplacement du curseur dans l'éditeur de texte ne se fait plus en mode configuration
- Ticket #78 & #1 : correction de problèmes de chargement des images
- Ticket #86 : le mot "copie" n'est plus ajouté devant le texte d'une case copiée
- Ticket #23 : changement du nom de certaines actions
- Ticket #93 : le bouton retour lors de la création d'une configuration ne créer plus la configuration
- Ticket #95 : il est maintenant possible de cliquer sur toute la ligne dans les sélection d'action (export, supprimer, etc.)
- Ticket #5 : L'installeur de LifeCompanion intègre maintenant l'accord de licence
- Correction d'un problème lors de la suppression des listes de case
- Correction d'un problème d'affichage lors de l'export en PDF dans certains cas
- L'action "Sélectionner une liste choisie" est maintenant simulée en mode configuration
- Les images des configurations par défaut sont maintenant directement affichées
- Mise à jour des configurations par défaut


## 1.2.1 - 12/12/2021

### Corrections/ajustements

- Corrections mineures sur les listes de cases
- Corrections de quelques traductions

## 1.2.0 - 06/12/2021

### Fonctionnalités

- Refonte de l'interface des "Listes de cases"
	- Meilleure visualisation de l'ensemble du vocabulaire : sous forme d'arbre
	- Plus facile de modifier l'organisation : fonction remonter/couper/coller/etc... bien plus accessibles
	- Import/export facilité : tout le vocabulaire est exporté et importé d'un seul coup
	- Regroupement de la partie "Apparance de la case" afin de gagner en place
	- Ajout d'une fonction de recherche
- Ajout de la possibilité de choisir la couleur du contour de la souris virtuel

### Corrections/ajustements

- Ajout d'un message d'avertissement sur la fenêtre des paramètres généraux en cas d'annulation (évite les mauvaises manipulations)
- Correction de l'affichage lorsque le type de case change de "Liste de cases" à autre chose

## 1.1.0 - 05/11/2021

### Fonctionnalités

- Ajout d'une configuration de communication "CommuniKate 20"
	- Permet une communication pictographique complète
	- Contient également un clavier de lettres, chiffres et ponctuations...

### Corrections/ajustements

- Ticket #453 : correction de la prononciation dans les listes de case qui pouvait parfois ne pas marcher
- L'action "Remonter au niveau supérieur" des listes de cases fonctionne maintenant correctement avec les liens entre les listes
- Ticket #444 : quand on combine un mode de sélection direct et un mode de défilement, le cadre se masque bien lorsqu'on utilise plus la sélection directe

## 1.0.0 - 27/09/2021

### Fonctionnalités

- Le clavier visuel est maintenant synchronisé avec toutes les saisies à l'extérieur (Windows uniquement)
	- LifeCompanion "sait" donc maintenant ce que vous entrez dans Word, LibreOffice, etc.
	- Cela permet d'utiliser LifeCompanion en prédiction de mots compatible avec tous les logiciels
- Ajout de deux configurations par défaut : prédiction de mots verticale et prédiction de mots horizontale

### Corrections/ajustements

- Configuration par défaut du clavier visuel : correction de deux touches qui ne fonctionnaient pas
- Optimisation sur le chargement des configurations et des images
- Ticket #433 : suppression de la fenêtre de double lancement automatiquement au bout de 30 secondes (pour éviter les blocages)
- Ticket #434 : les variables marchent maintenant indépendamment de la casse
- Ticket #435 : une notification s'affiche lors de la suppression d'une grille
- Ticket #436 : une notification s'affiche maintenant sur "Annuler/Refaire"
- Ticket #439 : après une mise à jour, la page des changements s'ouvre automatiquement
- Ticket #439 : au premier démarrage, la page du guide s'ouvre automatiquement
- Il est maintenant possible d'utiliser le bouton de sélection des éléments pour les déplacer

## 0.23.1-SNAPSHOT - 16/09/2021

### Fonctionnalités

- Ajout d'une configuration de base : exemple de séquences
- Meilleur champs pour sélectionner la durée des séquences
- Meilleure gestion de l'import d'une configuration qui en remplace une déjà présente
	- Permet de visualiser les dernières dates de modification sur les deux configurations
	- Permet de choisir de remplacer la configuration actuelle
	- Permet de choisir de conserver les deux versions

### Corrections/ajustements

- Correction sur l'envoi des sessions qui pouvait parfois ne pas fonctionner correctement
- Ticket #429 : correction d'un problème lorsque le mode de sélection d'une grille n'était pas le même que la configuration

## 0.23.0-SNAPSHOT - 10/08/2021

### Fonctionnalités

- Ticket #406 : **Nouvelle demande de confirmation** lorsqu'une configuration est modifiée : possibilité de sauvegarder avant l'action voulue
	- Avant l'ouverture d'une autre configuration
	- Avant la création d'une nouvelle configuration
	- Avant l'import d'une autre configuration
	- Avant la sélection d'un autre profil
- Ticket #298 : la configuration de la prédiction de mots en maintenant intégrée à la configuration et non au profil
- Ticket #235 : ajout de l'action **Simuler le survol d'une case**
- **Nouvelle visualisation des mises à jour et de leur vérification**
	- Plus informatif pour l'utilisateur sur la mise à jour en cours
	- Permet de vérifier manuellement une présence de mise à jour

### Corrections/ajustements

- Ticket #426 : correction d'une erreur lors de la suppression d'une erreur sur la prédiction de mots
- Ticket #425 : l'événement ajouté est sélectionné automatiquement
- Ticket #388 : ajout de notifications sur les copier/coller, et les ajouts d'éléments
- Ticket #422 : optimisation du chargement des images
- Ticket #413 : lors de la combinaison d'un mode de défilement ainsi qu'un mode direct, les paramètres de filtrage des entrées sont maintenant utilisés
- Ticket #415 : lors de la création d'une nouvelle configuration, l'affichage pouvait parfois être masqué
- Ticket #416 : correction d'un bug d'affichage lors de l'import de la configuration actuelle
- Ticket #404 : retour automatique à zéro lors de l'ouverture d'une liste de case
- Les configurations/profils supprimés puis ajoutés de nouveau pouvaient parfois disparaître

## 0.22.2-SNAPSHOT - 11/07/2021

### Fonctionnalités

- Amélioration des performances de chargement des configurations
- Amélioration des indicateurs d'activité
- Ajout d'une fonction de debug utile
- Ajout des indicateurs pour le plugin PPP
- Ajout de notification à l'ajout d'une grille dans une pile

### Corrections/ajustements

- Ticket #405 : certains éléments des listes de case n'étaient parfois pas bien prononcés
- Ticket #386 : correction de certaines utilisations des variables qui pouvaient ne pas fonctionner

## 0.22.1-SNAPSHOT - 09/06/2021

### Corrections/ajustements

- Correction d'une fonction utile pour les emails

## 0.22.0-SNAPSHOT - 02/06/2021

### Corrections/ajustements

- Correction d'un problème de création de raccourcis
- Correction d'un paramètre du mode de sélection
- Correction d'un problème avec certains fichiers mis à jour

## 0.21.0-SNAPSHOT - 29/05/2021

### Fonctionnalités

- Correction de la **stabilité et de l'utilisation de la mémoire**
- Suppression des notions de style au niveau du profil (déplacées dans configuration)
- Ajout d'un bouton pour tout sélectionner/déselectionner lors de la création d'un profil
- Ticket #385 : ajout de la possibilité de **créer des raccourcis de lancement de configuration sur le bureau** pour lancer une configuration en direct
- Ticket #343 : Ajout de la possibilité d'utiliser un **mode de sélection direct** en plus d'un **mode en défilement**
- Possibilité de **différencier les boutons de souris** dans les modes de sélection
- Configurations par défaut
	- Remplacement dans la configuration picto du clavier phonétique par un clavier orthographique complet
	- Création d'une configuration phonétique

### Corrections/ajustements

- Ticket #390 : le nom d'une image importée correspond maintenant bien au nom de fichier
- Ajout d'une icône sur les paramètres du mode de sélection
- Correction d'un rare bug à la suppression d'un élément
- Correction d'un problème de chargement qui pouvait se produire si un son était associé à une catégorie
- Déplacement du menu des extensions
- Migration de la version JavaFX (plus stable)
- Ticket #368 : l'installeur affiche maintenant un message d'erreur si le système n'est pas 64bit

## 0.20.1-SNAPSHOT - 22/04/2021

### Corrections/ajustements

- Correction de problèmes avec les chargements/mises à jour des plugins

## 0.20.0-SNAPSHOT - 22/04/2021

### Fonctionnalités

- Ticket #371 : le clavier visuel du système s'affiche maintenant automatiquement dans les champs de texte en configuration quand clic depuis un écran tactile
- Ajout de la **fonction "Séquences"** afin de pouvoir réaliser des séquences/sous-séquences dans LifeCompanion
	- Les séquences sont divisées en étapes
	- Il est possible de définir certaines parties comme "sous-étapes" et avoir un affichage différent
	- Il est également possible d'avoir des étapes automatiques (avec timer)

### Corrections/ajustements

- Ticket #366 : correction sur une configuration par défaut
- Le clavier virtuel pouvait des fois envoyer l'espace au mauvais moment avec la prédiction de mots
- Correction d'un problème avec le clavier visuel
- Ticket #373 : réglage d'un problème avec les actions de temporisation
- Les extensions ne sont plus chargées lors de l'installation d'une mise à jour
- (interne) Meilleure gestion des extensions  : mises à jour automatiques, stats d'installation, vérifications, etc.

## 0.19.0-SNAPSHOT - 30/03/2021

### Fonctionnalités

- Ajout des **modèles de configuration**
- Ajout de la possibilité d'ajouter une configuration **à partir d'un modèle**
- Ticket #338 : il est maintenant possible d'importer plusieurs listes de cases en même temps
- Ticket #356 : les cases des listes de cases ajoutent maintenant l'image de la case dans l'éditeur (quand activé)
- Optimisation de la taille des configurations
- Ajout de **deux actions sur les listes de cases**
	- action "Cases suivantes sans boucler (liste courante)"
	- action "Cases précédentes sans boucle (liste courante)"
	- ces deux actions permettent de faire défiler des cases mais de ne pas recommencer lorsqu'on est au début/bout d'une liste : permet d'éviter de "boucler" à l'infini
- Ajout de **deux événements sur les listes de cases**
	- événement "Fin de la liste courante atteinte"
	- événement "Début de la liste courante atteint"
	- ces événements permettent d'effectuer que l'utilisateur sélectionne suivant/précédent alors qu'il est déjà en fin de liste
	- exemple d'utilisation : ajouter un retour sonore pour informer du fait qu'il n'y a plus d'autres pages ou encore remonter automatiquement au niveau supérieur

### Corrections/ajustements

- Les listes de cases ont maintenant un texte centré par défaut (sauf si présence d'une image)
- Ticket #361 : correction d'un problème avec la prédiction de mots des claviers visuels dans certains logiciels
- Ticket #364 : les données d'utilisation sont effacées à la duplication d'une configuration
- Ticket #363 : correction d'un rare problème lors de l'import d'un fichier de profil au lancement
- Correction d'un problème dans l'envoi des données d'utilisation
- Ticket #359 : les statistiques d'installation des versions sont maintenant mieux envoyées

## 0.18.0-SNAPSHOT - 23/03/2021

### Fonctionnalités

- Ajout de la possibilité d'envoyer des **statistiques d'utilisation** (désactivé par défaut)
- Le fait d'importer/supprimer une configuration créer une sauvegarde d'un fichier LCC de secours
- Le fait d'importer/supprimer un profil créer une sauvegarde d'un fichier LCP de secours
- Ticket #350 : ajout d'une action "Arrêter les sons en cours de lecture"
- Ticket #324 : ajout d'une action "Sélectionner une liste choisie"

### Corrections/ajustements

- Correction d'un bug d'initialisation sur la synthèse vocale
- Notification à la suppression d'une configuration
- Notification à la suppression d'un profil
- Correction du message de confirmation de suppression d'un profil
- Ticket #352 : les majuscules restaient actives même en mode configuration
- Correction d'un rare bug qui pouvait empêcher la lecture de sons

## 0.17.2-SNAPSHOT - 03/03/2021

### Fonctionnalités

- Sur la fenêtre de paramètre d'image : affichage de la rotation de l'image
- Ticket #326 : ajout de la **configuration d'image sur les listes de cases**
	- Possibilité de recadrer
	- Possibilité de remplacer une couleur
	- Possibilité de tourner l'image
- Ajout des **statistiques d'installation et de mise à jour**

### Corrections/ajustements

- Correction de la synthèse vocale sous système Mac

## 0.17.1-SNAPSHOT - 17/02/2021

### Fonctionnalités

- Ticket #331 : ajout de la possibilité de **dupliquer un profil**
- Ticket #337 : lorsqu'on importe en écrasant une configuration ou un profil, une **sauvegarde automatique est effectuée afin de pouvoir les récupérer en cas de problème**
- Les PDF sont ouverts après l'export des grilles

### Corrections/ajustements

- Ticket #340 : les images de importées dans les configurations sont maintenant dupliquées
- Ticket #334 : correction d'un bug rare de chargement des images
- Ticket #342 : la taille de la fenêtre par défaut est mieux calculée
- Ticket #336 : meilleur écran entre deux configurations
- Ticket #332 : suppression des profils fiabilisée
- Ticket #347 : limitation du nombre de notifications d'erreur affichées
- Correction de l'action "déplacer le curseur vers le haut"

## 0.17.0-SNAPSHOT - 10/02/2021

### Fonctionnalités

- Ticket #323 : Ajout de la **possibilité de déplacer des cases/listes de case**
	- Un drag on drop permet de réorganiser des cases/listes en les déplacant dans une sous-liste
	- Un drag on drop sur un niveau au dessus permet de les faire remonter dans une liste au dessus
	- La sélection multiple dans la liste permet de déplacer plusieurs cases/listes à la fois
- Ajout de la **fonction d'import ou d'export d'une liste de case**
	- Permet d'importer/exporter une case et ses éléments
	- Permet également d'importer/exporter une liste et tous ses sous éléments (arborescence complète)
- Ticket #296 : **Nouveau système de notifications et d'affichage des tâches en cours**
	- Augmente grandement la lisibilité
	- Améliore la détection et la gestion des erreurs possibles et inattendues

### Corrections/ajustements

- Le lien du site internet est masqué en attendant sa publication
- Diverses petites améliorations sur les listes de case
- Ticket #325 : les couleurs sont maintenant affichées sur les listes de case

## 0.16.3-SNAPSHOT - 03/02/2021

### Fonctionnalités

- Ajout de la possibilité de lire des sons sur les listes de case
- Ajout d'un événement pour réagir lorsque l'utilisateur remonte d'un niveau mais qu'il n'y a pas de niveau supérieur

### Corrections/ajustements

- Changement du contrôle pour enregistrer des sons
- Ticket #121 : meilleure qualité du son enregistré dans l'application
- Les sons enregistrés sont joués plus fort pour être égaux avec les sons de base de l'application
- Ticket #327 : problème de traductions en Français
- L'action "Écrire la prédiction de mots" est maintenant ajoutée en premier
- Correction d'un bug sur certains sélecteurs de fichiers

## 0.16.2-SNAPSHOT - 22/01/2021

### Corrections/ajustements

- Ticket #328 : supprimer le mot supprime tout le texte

## 0.16.1-SNAPSHOT - 21/01/2021

### Corrections/ajustements

- Ticket #317 : correction du défilement avec les listes de case
- Ticket #312 : corrections sur les listes de case
- Ticket #321 : corrections sur les listes de case
- Correction de bugs en défilement
- En mode utilisation, lorsqu'une action échoue, on execute quand même les suivantes
- Correction des actions aller dans la grille/case si aucune sélectionnée
- Ajout de logs en cas d'erreur de la synthèse vocale

## 0.16.0-SNAPSHOT - 19/01/2021

### Fonctionnalités

- Amélioration de la précision de la recherche des images
- Ajout de la bibliothèque de pictogrammes **Mulberry Symbols**
- Ticket #313 : ajout d'un écran de préchargement

### Corrections/ajustements

- Correction d'un problème avec certaines cases de liste de cases
- Ticket #289 : pagination des images lors de la recherche
- Meilleure taille des fenêtre de sélection d'image
- Correction de la sélection des images depuis la galerie
- Ticket #164 : correction de la souris virtuelle sur les hautes résolutions d'écran
- Ticket #250 : la souris virtuelle apparaît maintenant au centre
- Correction de la synthèse vocale qui pouvait parfois ne pas s'initialiser correctement
- Ticket #303 : correction de l'export PDF
- Ticket #302 : correction du défilement qui ne déclenchait pas les actions au survol dans certains cas
- Ticket #124 : utilisation du dernier dossier ouvert
- Ticket #308 : le dossier par défaut lors de l'export est le périphérique externe branché

## 0.15.1-SNAPSHOT - 07/01/2021

### Fonctionnalités

- Ajout de la **prévisualisation des listes de cases en mode configuration**
	- Navigation possible avec par des boutons sur les cases
	- Simulation des actions de pages suivantes, de retour à l'accueil, etc.
- Ticket #171 : ajout de la possibilité **d'exporter toutes les grilles en PDF**

### Corrections/ajustements

- Affichage du serveur de mise à jour utilisé dans les informations
- Amélioration du temps pour quitter LifeCompanion
- Correction de problèmes qui pouvaient potentiellement corrompre des fichiers

## 0.15.0-SNAPSHOT - 05/01/2021

### Fonctionnalités

- Ajout de la possibilité de lancer automatiquement LifeCompanion au démarrage du système
- Refonte de la fonctionnalité listes de catégories, en **Liste de cases**
	- Il est possible d'organiser du contenu de manière hiérarchique en cases et listes
	- Il est possible de faire des liens d'une liste à l'autre si besoin
	- Les hiérarchies ne sont pas limitées
	- Les listes sont maintenant sauvegardées dans un fichier à part (simplifiera les évolutions futures)
- Ajout de la possibilité **d'installer et de mettre à jour automatiquement les extensions** depuis le répertoire central
- Ticket #275 : Ajout de la possibilité d'installer des extensions dès l'installer de LifeCompanion

### Corrections/ajustements

- Optimisation de la taille des configurations
- Les images dans les listes de case sont maintenant optimisées
- Ticket #280 : en clavier virtuel, certains caractères sont envoyés plus efficacement aux applications en fond
- Ticket #292 : il est maintenant possible de retrouver les mots d'une image sélectionnée
- Ticket #291 : la recherche des images pouvait être corrompue après le chargement de certaines images
- Ticket #287 : l'action "Aller dans une grille et revenir" n'était pas prise en compte pour les raccourcis en mode configuration
- Ticket #268 : Correction de certains problèmes sur les systèmes Windows lorsque l'affichage est > 100%
- Le style des cases de liste de mots est maintenant correctement pris en compte
- La duplication d'une configuration efface maintenant l'historique des modifications d'une configuration

## 0.14.0-SNAPSHOT - 04/12/2020

### Fonctionnalités

- Ajout de la **fonctionnalité "Liste de catégories/liste de cases"**
	- Cette fonction permet de facilement créer des listes de cases dans une même grille
	- Il est possible de créer différentes listes appelée "catégories"
	- Ces listes et catégories "défile" dynamiquement en mode utilisation
	- Des aides à la saisie sont proposées lors de la création des listes afin de pouvoir saisir rapidement
- Ticket #271 : ajout de la possibilité **de prendre une image depuis la webcam** dans le selecteur d'images
- Ajout de la possibilité **d'utiliser une image du presse papier** dans le selecteur d'images
- Ticket #278 : ajout d'une variable, contenu du presse papier (permet de reproduire "Sélectionner pour prononcer" de Android)
- Ticket #223 : ajout d'une **traçabilité des modifications sur les configurations**
	- Ajouté dans la partie "Modifications/Informations" d'une configuration : "Historique des modifications de la configuration"
	- Permet de lister à chaque enregistrement d'une configuration : la date, le profil, le nombre de modification, le système et le nom de compte du système

### Corrections/ajustements

- Ajout de la possibilité de déclencher une vérification de mise à jour au prochain démarrage
- Information sur la mise à jour en cours
- Ticket #288 : les dimensions par défaut de la fenêtre n'étaient pas bonne lorsqu'on désactivait le mode plein écran
- Ticket #286 : correction d'un problème d'affichage de certains caractères spéciaux
- Amélioration des performances du sélecteur d'images
- Le bouton pour passer en plein écran en mode utilisation est maintenant en haut à droite
- Le style de l'éditeur est à nouveau sauvegardé (correction d'un bug)
- Les variables utilisées dans les actions sont maintenant à jour à chaque action (plutôt qu'à jour avant toute la case)
- Ticket #279 : les majuscules sont mieux prises en compte en clavier virtuel
- Ticket #264 : une configuration supprimée d'un profil pouvait parfois réapparaître
- Ticket #281 : la touche "Entrée" valide le nom d'un élément
- Correction de la taille du nom de l'élément en bas de fenêtre lorsque celui-ci est sur plusieurs lignes
- L'action "Grille suivante et recommencer" est maintenant prise en compte pour afficher un bouton de lien
- Ajout des informations de licences des bibliothèques de pictogrammes et d'icônes utilisées
- Résolution d'un problème sur les variables générées

# PLUGINS CHANGELOG

## lc-email-plugin

### 1.2.0 - XX/02/2021

- Les cases email n'affichent maintenant plus l'adresse si un nom de contact est présent

### 1.1.9 - 19/01/2021

- Correction de la lecture de certains mails mal formulés leurs clients

### 1.1.6 - 18/12/2020

- Première version après la refonte LifeCompanion
- Amélioration de la qualité d'affichage des photos en PJ
