# THIS PAGE WILL BE WRITTEN LATER - WIP

# LifeCompanion - plugin dev

## Prerequisites

Before developing plugins for LifeCompanion, you must understand how LifeCompanion is working and what are its features.

Terms used in this documentation will be directly linked to LifeCompanion usages.

## Getting started

TODO : how to install plugin dev tools

## What's possible with plugins ?

Plugin are a good way to integrate specific features into LifeCompanion. This table presents the main integrated features.

| FEATURE           	| DESCRIPTION		| EXAMPLES		|
|-----------------------|-------------------|-------------------|
|**KeyOption** ("type de cases")|Key options are a good way to integrate keys that are modified on runtime by user.|Word predictions keys (keys are modified with current text) ; Quick com' keys (just a simpler way to configure LifeCompanion) ; etc. |

## App icons

A good pratice to integrate icons in LifeCompanion is to save SVG of your icons and to only integrate the good sized icons into your app resources. Recommanded size are always based on the max. width/height : if your icon ratio is 3/4, for a 32x32 size your icon should be 24*32.

Original LifeCompanion SVG icons can be found in **res/icons**

Here are the LifeCompanion expected icons size :

| TYPE												| SIZE (PX)			|
|---------------------------------------------------|-------------------|
|**use actions / use event generator**				|32x32				|
|**add component icon (in create tab)**				|32x32				|
