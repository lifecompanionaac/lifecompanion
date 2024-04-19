# LIFECOMPANION CHANGELOG

## 1.6.1 - XX/XX/2024

### Fonctionnalités

- *(Oscar P.)* Ajout d'une fonction permettant l'export de toutes les actions, événements et variables de LifeCompanion (dans le menu Préférences & Infos)
- *(Oscar P.)* Ajout d'actions "Démarrer/arrêter le minuteur" afin de pouvoir utiliser les cases de type "Indicateur de progression"
- *(Oscar P.)* Ajout de la fonction de **changement dynamique de l'opacité** en mode utilisation : permet de modifier l'opacité de fenêtre lorsque la souris sort de LifeCompanion
- *(Oscar P.)* Ajout d'un événement **"Intervalle de temps pour un jour de la semaine"** : permet de générer un événement à une heure précise d'un jour, très utile pour faire un emploi du temps interactif
- *(Oscar P.)* Ajout de **nouveaux paramètres sur les images**
	- Retourner horizontalement/verticalement une image
	- Supprimer automatiquement l'arrière plan d'une image !
	- Convertir une image en noir et blanc
- *(Lisa H)* Ajout d'une fonction de "seuil de pertinence" sur la prédiction de mots pour ne conserver que les mots les plus pertinents dans la prédiction
- *(Lisa H)* Ajout de l'action "Supprimer la dernière prédiction de mots"
- Ajout de nouveaux services dans serveur de contrôle de LifeCompanion et de nouveaux arguments au lancement
- Ajout d'une **traduction en Anglais** des interfaces de LifeCompanion (version Beta générée par un traducteur automatique)
- Ajout de la possibilité de désactiver l'optimisation de la synthèse vocale
- Ajout d'une nouvelle action **Activer/désactiver le plein écran** (pour compléter l'ancienne action "Activer/désactiver la taille maximale")
- Ajout de nouveaux paramètres au lancement et  de nouveaux services sur le serveur de contrôle
- Migration des versions de Java (18 > 21) et JavaFX (18 > 22)

### Corrections/ajustements

- Les polices installées sur le système sont maintenant disponibles dans LifeCompanion
- L'avertissement lors de l'import d'une configuration de version plus récente est maintenant plus clair et propose de vérifier directement les versions
- Correction d'un rare problème avec la souris virtuelle
- *(Oscar P.)* Ticket #270 : ajout de la possibilité de glisser des images de l'ordinateur dans les listes de cases
- Correction qui faisait que le paramètre en défilement "Ne pas démarrer automatiquement" ne fonctionnait plus
- Ticket #324 : correction d'un problème lors d'une copie de case "Bloc note"
- Ticket #335 : correction d'un bug d'affichage sur les listes de case
- Ticket #321 : correction d'un rare bug à la suppression d'une case dans les listes de case
- Ticket #322 : il est maintenant possible de faire plusieurs copier/coller avec la même case dans les listes de cases
- Ticket #336 : ajustement des sélections dans la configuration des listes de case
- Optimisation des configurations avec de très nombreuses listes de cases
- Ticket #316 : lorsque le code pour passer en mode édition n'est pas entré, la souris est de nouveau placée sur la fenêtre ensuite
- Ticket #333 : le message d'avertissement de double lancement n'est plus affiché lorsque cela déclenche un import de configuration
- Ticket #317 : la fenêtre de création/mise à jour de profil affiche maintenant un titre plus explicite
- Ticket #338 : correction de rares problèmes sur les actions liées aux fenêtres

## 1.6.0 - 24/01/2024

### Fonctionnalités

- **Ajout des vidéos !**
	- Possibilité d'utiliser les vidéos à la place des images, donc sur les cases et les listes de cases
	- Les vidéos peuvent être lues dans la case ou en plein écran
	- La lecture est possible automatiquement (en continu), à l'activation ou au survol de la case
- Ajout du **style de forme pour les cases**
- Ajout d'un type de case **liste des configurations** : permet d'afficher la liste des configurations du profil et ainsi basculer entre chacune en mode utilisation !
- Les configurations par défaut sont maintenant issues du [**répertoire officiel de configurations de LifeCompanion**](https://lifecompanionaac.org/repository) !
- Il est maintenant possible d'**utiliser une touche de clavier pour activer le défilement**, même en mode clavier virtuel (sur Windows uniquement)
- Il est maintenant possible de **créer une nouvelle grille directement depuis l'action "Aller dans la grille"**
- Il est maintenant possible de **copier plusieurs cases en même temps**
- Ajout d'un paramètre "Sélectionner automatiquement les images des cases" (dans Préférences & Infos) : permet que les images soient automatiquement sélectionnées lorsqu'un texte est entré sur une case, fait gagner du temps !

### Corrections/ajustements

- Amélioration de la recherche des images
- Correction de quelques problèmes sur les listes de cases
- Lorsqu'on supprime une liste dans les listes de case, les liens vers les listes supprimées sont automatiquement corrigés
- Sur l'événement "Touche appuyée" il est maintenant possible de choisir si la touche appuyée est bloquée ou non
- Traductions des noms des touches du clavier
- Ajustement sur les interfaces de liste de cases
- Ajout d'une nouvelle variable : nom du profil actuel
- L'action d'une case bloc note n'était pas supprimée lorsqu'elle repassait en case "classique"
- Ajout de nouvelles variables : numéro du jour du mois, mois de l'année, numéro du mois et année
- Ticket #260 : ajout du bouton "rouge" dans l'interface de configuration des listes de cases (comme dans la partie "édition")
- Ticket #282 : la configuration des listes de cases affiche maintenant le même nombre de cases que dans la configuration
- Ticket #267 : la configuration d'une image (rotation, recadrage, etc) est maintenant utilisée dans le champs de sélection
- Ticket #279 : le message pour écraser une configuration par celle importée est maintenant plus explicite
- Ticket #273 : l'image des listes de cases est maintenant toujours intégrée dans l'éditeur de texte
- Ticket #263 : amélioration de la qualité de l'export en PDF
- Ticket #266 : correction de l'affichage de l'alignement de texte sélectionné
- Ticket #271 : correction d'une erreur lors d'une vitesse de défilement était trop rapide
- Ticket #268 : quand une image est enlevée d'une case, le texte est automatiquement remis au centre
- Ticket #290 : correction d'un rare problème avec la prise de photo par la webcam
- Ticket #291 : correction d'un rare problème avec les éditeurs de texte qui pouvaient ne plus fonctionner
- Ticket #292 : correction d'un texte explicatif sur le code pour passer en édition
- Ticket #295 : correction d'un problème de détection de clavier dans certains cas

## 1.5.0 - 08/09/2023

### Fonctionnalités

- Mise à jour de **l'interface des listes de case, bien plus simple et plus rapide** pour ajouter du vocabulaire !
- Les icônes des fichiers de LifeCompanion (lcc, lcp) sont maintenant différentes des icônes de LifeCompanion afin de ne plus les confondre.
- Ajout d'une nouvelle action : écrire et prononcer le texte de la case
- Ajout d'une nouvelle action : appui sur la molette de souris
- Ajout d'une nouvelle action : remonter au niveau supérieur ou exécuter les actions suivantes ; permet de simplifier les configurations utilisant des listes de case et des grilles simultanément
- Ajout d'une nouvelle fonctionnalité : [les arguments de lancement de LifeCompanion](../docs/USER_API.md#lifecompanion-command-line-arguments), peuvent être utiles pour configurer LifeCompanion de manière particulière
- Ajout d'une nouvelle fonctionnalité : le [serveur de contrôle de LifeCompanion](../docs/USER_API.md#lifecompanion-control-server-api) afin de pouvoir contrôler LifeCompanion depuis d'autres applications
- Meilleure gestion lors de la présence de plusieurs écrans

### Corrections/ajustements

- La fenêtre de configuration s'adapte maintenant à la taille des petits écrans
- Ticket #253 : quand une couleur de fond est sélectionnée, la couleur de contour associée est automatiquement sélectionnée (si possible)
- Ticket #230 : il est maintenant possible d'explorer toutes les images des banques de pictogramme
- Ticket #245 : le code pour repasser en mode édition a été remplacé par une simple addition
- Ticket #229 : les majuscules n'étaient parfois pas retransmises correctement en clavier virtuel
- Ticket #243 : dans les actions "écrire et prononcer", "prononcer" est maintenant activé par défaut
- Ticket #255 : ajustement des valeurs maximales pour les arrondis
- Ticket #247 : ajustement de la valeur par défaut du remplacement des couleurs
- Ticket #252 : correction du test de la connexion internet à l'installation qui pouvait parfois ne pas fonctionner
- Ticket #237 : les configurations sont maintenant en plein écran par défaut
- Ticket #254 : dans les listes de case, lorsqu'une image est sélectionnée, la position du texte se règle automatiquement
- Ticket #249 : le bouton pour créer un modèle personnalisé a été supprimé de la vue de l'élément actuel. Elle est maintenant uniquement disponible dans l'onglet "Créer"
- Ticket #248 : ajout d'une action rapide sur les piles de grille pour ajouter à partir d'un modèle
- Ticket #246 : ajout de description au survol pour les actions rapides des piles de grille
- Ticket #251 : les configurations peuvent maintenant avoir une adresse de site internet associée
- Ticket #239 : ajout d'une action pour dupliquer la configuration depuis ses informations
- Ticket #240 : la suppression de la dernière grille d'une pile de grilles supprime maintenant la pile
- Ticket #258 : un message est maintenant affiché dans les événements lorsqu'il n'y en a pas de sélectionné
- Ticket #232 : il est maintenant possible de copier une image d'une case vers son ordinateur (dans un traitement de texte, un dossier, etc.)
- Ticket #250 : si un lien était fait sur un élément maintenant supprimé, un message d'avertissement s'affiche dans la description de l'action

## 1.4.9 - 30/03/2023

### Corrections/ajustements

- Optimisation importante des performances
- Ticket #224 : correction de certains problèmes avec les actions "Aller à"
- Ajout d'une fonction d'installation automatique des extensions
- Correction de l'image des configurations dans la liste (lors d'une création)

## 1.4.8 - 24/03/2023

### Fonctionnalités

- Ajout d'un bouton pour nettoyer les fichiers temporaires dans "Préférences & Infos" : permet de libérer de l'espace disque

### Corrections/ajustements

- Correction d'un problème qui pouvait parfois entraîner une synthèse vocale "ralentie" sous Windows
- Ticket #217 : correction d'un problème qui pouvait parfois "désactiver" certains liens après "Annuler/Refaire"
- Ticket #220 : meilleur comportement de la sélection en ligne/colonne ou colonne/ligne pour certains cas particuliers
- Ticket #223 : correction d'un rare problème lors de la sélection de grilles
- Correction de quelques textes
- Dans "Paramètres Généraux", une confirmation est maintenant demandée à l'annulation en cas de changements effectués non enregistrés

## 1.4.7 - 09/03/2023

### Fonctionnalités

- Ajout d'une action **Maintenir/relâcher une touche** pour les claviers visuels : permet par exemple de maintenir la touche CTRL, SHIFT, ALT, enfoncée

### Corrections/ajustements

- Correction du fonctionnement pour certaines combinaisons de touches en clavier virtuels (ex : Shift + Fin)
- Changement du comportement défilement ligne/colonne ou colonne/ligne lorsqu'il n'y a qu'une case dans la ligne ou colonne : une seconde sélection est maintenant nécessaire, cela est fait pour avoir un comportement cohérent indépendamment de l'affichage
- La souris ne se plaçait parfois pas au centre de la fenêtre au démarrage d'une configuration (sur les écrans avec une mise à l'échelle)
- Correction d'un problème qui pouvait parfois bloquer la synthèse vocale
- Ticket #216 : Les caractères accentués n'étaient pas correctement pris en compte lors de l'utilisation de la prédiction hors de LifeCompanion avec un clavier physique

## 1.4.6 - 01/03/2023

### Fonctionnalités

- Ajout de la **possibilité de faire des liens de liste de cases vers les grilles**
	- Permet depuis la liste de case d'aller vers une grille
	- Depuis une grille, l'action "Sélectionner une liste choisie" permet de faire l'inverse
- Ajout d'une action "Changer de mode de sélection" afin de pouvoir passer d'un mode direct/défilement durant le mode utilisation
- Ajout de raccourcis clavier sur les listes d'actions, d'événements et de grilles :
  - Suppr. : supprime l'élément sélectionné
  - Entrée : modifie l'élément sélectionné
  - Flèche droite : décale l'élément sélectionné vers le bas
  - Flèche gauche : décale l'élément sélectionné vers le haut
  - N : créer un nouvel élément
- Ajout d'un bouton pour "suivre" les liens dans les listes de cases
- Ajout de la **synthèse vocale sous Unix** ([PicoTTS](https://github.com/naggety/picotts))
- **Amélioration de la fonction Imprimer** en mode utilisation
  - Intègre maintenant les pictogrammes de l'éditeur courant
  - Si le texte est trop long, s'imprime sur plusieurs pages
- Ajout d'une nouvelle variable : **batterie restante** (en pourcentage)
- Ajout d'une nouvelle variable : **temps restant de la batterie**
- Ajout d'une action : Passer en mode "Édition"

### Corrections/ajustements

- Ticket #172 : lorsque la configuration des listes de case est ouverte, le niveau courant est automatiquement sélectionné
- Ticket #211 : amélioration de la qualité de la synthèse vocale
- Correction d'un problème sur lors de la mise en pause du défilement
- Optimisation du défilement pour certains cas (ligne/colonne ou colonne/ligne particuliers)
- Les premières cases étaient "survolées" en défilement même lorsque celui-ci n'étais pas démarré automatiquement
- Ticket #213 : lors de l'édition des informations d'un profil/d'une configuration, l'appui sur Entrée valide les modifications
- Ticket #26 : ajout de la possibilité de désactiver la création de profils/configurations de secours à la suppression/l'import
- Ticket #212 : si l'installeur est lancé alors que LifeCompanion est déjà lancé, l'installation est bloquée
- Ticket #54 : si LifeCompanion est lancé une nouvelle fois avec une configuration à importer, alors l'import se déclenche dans l'application déjà ouverte
- Les traces d'éxécution sont conservées plus longtemps
- Ticket #215 : meilleure gestion des erreurs dans les extensions

## 1.4.5 - 21/02/2023

### Fonctionnalités

- Ajout d'une fonction **d'autocomplétion sur la création de clavier** : à l'entrée d'une suite de certains caractère, un remplissage automatique est proposé afin de gagner du temps
- La synthèse vocale est **bien plus "rapide"** en particulier sur les mots courts ou lettres uniques. Cela ajoute une sensation de réactivité générale, en particulier sur du retour sonore au survol

### Corrections/ajustements

- Ticket #156 : la récupération d'images depuis le presse-papier fonctionne maintenant depuis toutes les applications
- Ticket #201 : le paramètre du défilement "Ignorer les éléments vides" était ignoré en ligne/colonne ou colonne/ligne
- Ticket #164 : meilleure gestion des actions "haut/bas" du curseur dans les éditeurs de texte
- Ticket #199 : correction de certains alignements/tailles, en particulier sur Linux
- Ticket #208 : ajout d'une icône sur les paramètres de synthèse vocale
- Ticket #209 : correction du bouton "Copier" sur l'onglet Accueil
- Uniformisation de certains styles d'élément dans l'application
- Ajout d'une fenêtre d'information sur les [formations LifeCompanion](https://lifecompanionaac.org/pages/trainings)

## 1.4.4 - 27/01/2023

### Corrections/ajustements

- Ticket #198 : LifeCompanion pouvait parfois se retrouvé bloqué lors du lancement avec un fichier de config/profil
- Ticket #155 : les fichiers de configurations étaient marqués comme fichiers de profil sous Windows
- Ticket #187 : correction des licences des tiers

## 1.4.3 - 26/01/2023

### Fonctionnalités

- Meilleure gestion de **la mise à l'échelle** des systèmes : permet un affichage "plus grand" sur des écrans à haute résolution (ex : tablettes Surface)
- Ajout d'un paramètre dans les modes de sélection : **masquer le pointeur de souris** (très utile pour du headtracking/eyetracking !)
- Ajout d'un indicateur sur les cases "vides" en mode édition afin de ne pas oublier de mettre des actions
- Ajout du réglage de la couleur du texte pour les listes de cases
- Mise à jour de l'API d'extensions
- Mise à jour de la version de Java (version 18)

### Corrections/ajustements

- Ticket #69 : ajout d'une description sur chaque type de case
- Ticket #152 : les notifications ne prennent maintenant plus le focus de la fenêtre principale
- Ticket #182 : correction d'une traduction
- Ticket #185 : correction d'une erreur lors du remplacement d'une grille par une autre en copier/coller
- Ticket #186 : correction d'un problème qui faisait que le bouton "précedent" ne fonctionnait pas sur les listes de case
- Ticket #177 : ajout d'un scroll dans les paramètres de sélection spécifiques à une grille
- Ticket #195 : il était parfois impossible d'afficher la barre de progression en mode "Sélection avec temporisation"
- Ticket #181 : alignement d'éléments sur la vue de configuration des listes de cases
- Ticket #174 : ajout d'un bouton "Précédent" dans la recherche des listes de cases
- Ticket #173 : les éléments supprimés sont maintenant supprimés de la recherche dans les listes de cases
- Ticket #171 : la racine des listes de case est maintenant nommée différemment
- Ticket #178 : optimisation du défilement (changement automatique pour le bon mode pour les grilles avec uniquement 1 ligne/colonne)
- Ticket #166 : correction de l'affichage de bouton pour les règles de correction de la prédiction de mots
- Ticket #167 : insérer le caractère " avec un clavier virtuel fonctionne maintenant
- Correction de l'affichage de l'explication dans la section "Événements"
- Ticket #41 : LifeCompanion redémarre en cas d'échec de mise à jour
- Ticket #34 : meilleur affichage de l'avancement de l'installation
- Correction d'un problème qui empêchait le changement de couleur de case dans certaines situations

## 1.4.2 - 20/06/2022

### Fonctionnalités

- Ajout d'une fonctionnalité pour mettre des bibliothèques d'image en "favoris" afin d'avoir leurs résultats en premier à chaque recherche

### Corrections/ajustements

- Ticket #163 : correction d'un problème avec la souris virtuelle
- Ticket #162 : optimisation de la taille des configurations sur le disque
- Ticket #159 : correction de quelques éléments graphiques
- Ticket #140 : préparation pour l'ajout futur de bibliothèques d'images

## 1.4.1 - 02/06/2022

### Fonctionnalités

- Le paramètre de l'espace (horizontal et vertical) entre les cases a été deplacé dans le style des grilles
- Le paramètre de la position du texte dans les cases a été déplacé dans le style des cases

### Corrections/ajustements

- Ticket #143 : l'avertissement de double lancement est maintenant affiché 10 secondes (à la place de 30)
- Optmisation de la taille de certaines configurations (réduction jusqu'à 20%)
- Ticket #149 : les nouveaux paramètres de la version 1.4 "Demander un code pour passer en édition" et "Désactiver la possibilité de quitter LifeCompanion" sont maintenant sauvegardés
- Ticker #148 : quand le plein écran est désactivé, la fenêtre ne "déborde" plus
- Ticket #150 : le clavier virtuel pour la recherche d'image s'affiche maintenant correctement sur tablette

## 1.4.0 - 30/04/2022

### Fonctionnalités

- **Amélioration de l'arbre de sélection** (onglet "SÉLECTION")
	- Les cases des grilles ne sont plus affichées (mais toujours possible d'en rechercher)
	- L'ordre est maintenant correct
	- Un double clic ouvre maintenant la fenêtre pour changer le nom d'un élément
- **Changement du système de sélection en mode édition**
	- Un clic sélectionne maintenant toujours les cases
	- La sélection des éléments de base ne change pas (clic sur la "main" rouge en haut à gauche)
	- La sélection des grilles se fait : soit en sélectionnant la pile "parent" / soit en utilisant l'arbre des éléments de l'onglet "Sélection"
	- Les éléments sélectionnés sont maintenant identifiés par des couleurs : rouge pour les cases, bleue pour les éléments de base (pile, éditeur) et jaune pour les grilles
- **Optimisation de l'ajout d'éléments et de l'utilisation des modèles personnalisés**
	- Suppression de la partie gauche et déplacement dans un onglet "CRÉER" (gain de place)
	- Facilité d'utilisation : uniquement de bouton et plus de glisser/déposer nécessaire
	- Meilleure catégorisation et clarification en fonction de l'élément sélectionné
	- Les modèles sont maintenant filtrés par élément sélectionné
	- Facilité d'utilisation : par exemple bouton pour ajouter une grille ou copier celle existante
- Ajout d'une configuration par défaut : souris en défilement

### Corrections/ajustements

- Ticket #123 : Ajout de deux boutons sur les piles de case pour rapidement : ajouter une nouvelle ou copier la grille courante
- Ticket #137 : il est maintenant possible de choisir le mode "Fenêtre en plein écran complet" ainsi que le mode "Fenêtre réduite" (paramètres "Fenêtres et dimensions")
- Ticket #120 : la couleur actuellement sélectionnée est maintenant montrée dans un sélecteur de couleur
- Ticket #113 : lors de l'utilisation de "Shift+Tab" ou "Ctrl+Tab", le texte de la case suivante/précédente est maintenant sélectionné complètement par défaut (accélère la saisie)
- Ticket #110 : le raccourci "Shift+Tab" fait passer à la case précédente lors de l'entrée du texte d'une case
- Ticket #131 : amélioration de la commande "Lancer un programme" : meilleure gestion des arguments et log des erreurs
- Ticket #130 : la recherche d'image fonctionne maintenant avec tous les mots (même "de", "je", etc.)
- Ticket #129 : correction d'un rare problème pour les configurations de clavier/souris virtuels
- Ticket #136 : ajout de la possibilité de désactiver la fermeture de LifeCompanion en mode utilisation
- Ticket #138 : déplacement du paramètres "Demander un code pour passer en édition" dans les paramètres généraux (devient donc spécifique à la machine et non à la configuration)
- Ticket #119 : lors du retour du mode configuration au mode édition, les grilles et les listes de cases qui étaient affichées sont maintenant affichées (permet de gagner du temps pour corriger une erreur)
- Ticket #122 : ajout d'une action "Ouvrir avec l'application par défaut"
- Ticket #85 : ajout de raccourci clavier sur la vue des listes de cases : supprimer, copier/couper/coller, nouvelle case (CTRL+N), et descendre/monter (ALT+HAUT ou ALT+BAS)
- Ticket #67 : ajout d'une action "Épeler tout le texte" pour épeler le contenu de l'éditeur
- Ticket #67 : ajout d'une action "Épeler le texte de la case" pour épeler le texte de la case
- Ticket #67 : ajout d'une action "Épeler du texte" pour épeler du texte (choisi)
- Ticket #57 : ajout de la traduction des touches du clavier
- Ticket #42 : ajout d'une nouvelle action pour changer la taille de la fenêtre
- Ticket #72 : ajout d'une action pour réduire la fenêtre
- Ticket #56 : ajout d'un nouvel événement "Intervalle de temps dans la journée", permet d'effectuer une action entre deux heures de la journée
- Ticket #70 : les raccourcis "CTRL+C" ou "CTRL+V" en mode utilisation permettent de récupérer le texte courant ou de coller un texte copié dans l'éditeur
- Ticket #68 : ajout d'une action "Lancer une commande exécutable" pour lancer des commandes sur l'ordinateur
- Ticket #115 : correction de quelques fautes
- Correction du raccourci clavier pour copier/coller des styles : CTRL + SHIFT + C / CTRL + SHIFT + V
- Ajout d'un nouvel événement "Intervalle régulier", permet d'effectuer une action tous les X temps
- Correction d'un problème sur la souris virtuelle (la souris n'évitait pas la fenêtre courante)

## 1.3.3 - 04/03/2022

### Fonctionnalités

- Ajout d'une fonctionnalité "secrète" (tappez "Bonne retraite Jean-Paul" et prononcez le texte pour la découvrir)

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
