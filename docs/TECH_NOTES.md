# LifeCompanion tech notes

Dev notes on LifeCompanion functional/technical key points.

## AAC Symbols dictionaries

`ImageDictionariesCreationScript` (in **lifecompanion/lc-tool-scripts**) is used to create symbols dictionaries. It
creates unique image dictionary with associated keywords (find duplicated images and associate their keywords). It also
resize images if needed and convert white background to transparent.

- **ARASAAC**
    - Database is installed locally (download
      from [ARASAAC PICTOGRAMMES - 6 april 2016](http://www.arasaac.org/descargas.php))
    - There is no modifications in the downloaded folder
- **SCLERA**
    - Database is extracted locally (download
      from  [Pictogrammes en français - 8 april 2020](https://www.sclera.be/fr/picto/telecharger))
    - Image in **tijd/FR** are renammed to *_nb*, *_gris* ou *_couleur* and moved to root folder
- **ParlerPictos**
    - Database is extracted locally (download from [ParlerPicto - 20 may 2020](http://recitas.ca/parlerpictos/))
    - Images are combined and label and NB images are removed
- **Mulberry Symbols**
    - Database is extracted locally (download from [Mulberry Symbols - 10 january 2021](https://mulberrysymbols.org/))
    - SVG are converted to PNG (400x400 px) with Inkscape in command line
    - Keywords and name are generated using automatic translations (using Google Translate API) -
      script `MulberrySymbolsCreationScript`
- **FontAwesome**
    - Database is extracted from FontAwesome font listing all the available icons
    - Keywords are generated using manual and automatic translations (using Google Translate API) -
      script : `FontAwesomeCreateScript`

## LifeCompanion application and launcher arguments

- **-updateDownloadFinished** : launch application from "update" directory
- **-updateFinished** : launch application normally, just delete the update directory
- **-enablePreviewUpdates** : enable preview versions for updates

## Update mechanism

1. Application is launched, running application detect if an update is available
1. If update available, copy previous **application** directory in **update** directory + download updated file
1. Once finished, update state is set to **DONE**
1. On next launcher launch, this launch application in **update** folder
1. Launched application knows it was updated (cmd arg flag) : block UI, copy its own content into **application** folder
1. Once content is copied, restart launcher with a fag to launch the up to date application in **application** folder
1. Launched application knows update is finished (cmd arg flag) : delete **update** directory

## Installation systems specific behavior

### MacOS (tested 10.15.4)

- **/Application/LifeCompanion.app/Contents** - directory *application* and *data*
- **[Home]/Documents/LifeCompanion** - user directory
- Folder **MacOS** : contains launch script (.sh script without extension) > think about having good relative path !
- Create **Info.plist** to describe app + add icon

### Windows (tested W10)

- **X:\ProgramData\LifeCompanion** - directory *application* and *data* *(from
  HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\Shell Folders reg key)*
- **X:\Users\Public\Documents\LifeCompanion** - user directory *(from PUBLIC env var)*
- File association : **lcc** and **lcp** *(with HKEY_CLASSES_ROOT reg key)*
- Desktop shortcut *(from PUBLIC env var)*
- Program shortcut *(from HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\Shell Folders reg key)*

### Ubuntu (tested Ubuntu 18.04.4 LTS)

- **/home/LifeCompanion/** - directory *application* and *data*
- **/home/Documents/LifeCompanion** - user directory
- Create **/home/.local/share/applications/LifeCompanion.desktop** to describe app + add icon