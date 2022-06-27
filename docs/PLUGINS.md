# THIS PAGE WILL BE WRITTEN LATER - WIP

# LifeCompanion - plugin dev

## Prerequisites

Before developing plugins for LifeCompanion, you must understand how LifeCompanion is working and what are its features.

Terms used in this documentation will be directly linked to LifeCompanion usages.

## Getting started

TODO : how to install plugin dev tools

## General introduction

Global LifeCompanion architecture and behavior : TO DO

## What's possible with plugins ?

Plugin are a good way to integrate specific features into LifeCompanion. This table presents the main integrated features.

- [**Key option**](#key-option) *("type de cases")*
    - Key options are a good way to integrate keys that are modified on runtime by user : their text, image, action are handled by your implementation or you need a specific view for a key (custom JavaFX component).
- [**Word prediction**](#word-prediction) *("prédiction de mots")*
    - Word prediction are called on each editor text change (content or caret position). They should return prediction results (ordoned word list)
- [**Char prediction**](#char-prediction) *("prédiction de caractères")*
    - As the word prediction do, the char prediction should do the exact same thing but for characters : results are then used to fill a dynamic keyboard
- [**Use action**](#use-action) *("actions")*
    - They are the LifeCompanion's core : added to keys (or to global event) they will define the user interaction behavior. They link keys to global behavior.
- [**Voice synthesizer**](#voice-synthesizer) *("synthèse vocale")*
    - TODO
- [**Use event**](#use-event) *("événement")*
    - TODO

## How to

### Use action

TODO

### Key option

TODO

### Word prediction

TODO

### Char prediction

TODO

## General topics

### App icons

A good pratice to integrate icons in LifeCompanion is to save SVG of your icons and to only integrate the good sized icons into your app resources. Recommanded size are always based on the max. width/height : if your icon ratio is 3/4, for a 32x32 size your icon should be 24*32.

Original LifeCompanion SVG icons can be found in **res/icons**

Here are the LifeCompanion expected icons size :

| TYPE												| SIZE (PX)			|
|---------------------------------------------------|-------------------|
|**use actions / use event generator**				|32x32				|
|**add component icon (in create tab)**				|32x32				|
