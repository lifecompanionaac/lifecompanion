/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2021 CMRRF KERPAPE (Lorient, France)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package scripts.imagedictionaries;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.framework.commons.fx.control.SimpleDirectorySelector;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.utils.FluentHashMap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lifecompanion.framework.commons.utils.io.IOUtils.fileSha256HexToString;

/**
 * @author Mathieu THEBAUD
 */
public class FontAwesomeCreationScript extends Application {

    private static final int WIDTH = 400;
    private static final String LANGUAGE_CODE = "fr";

    private static final Gson GSON = new GsonBuilder()//
            //.setPrettyPrinting()//
            .create();


    public static void main(String[] args) throws IOException {
        launch(args);
    }

    private static void generateImageDictionary(DicInfo dictionaryInformation, Map<Character, String[]> icons) throws IOException {
        File outputDir = new File("D:\\ARASAAC\\OUT\\" + dictionaryInformation.dicId);
        outputDir.mkdirs();

        final Font FONT_AWESOME = Font.loadFont(SimpleDirectorySelector.class.getResourceAsStream("/framework/font/fontawesome-webfont.ttf"), 12.0D);

        List<ImageElement> images = new ArrayList<>();

        icons.forEach((icon, keywords) -> {
            // Create icon node
            Text iconNode = new Text(String.valueOf(icon));
            iconNode.setFont(FONT_AWESOME);

            Bounds boundsInParent = iconNode.getBoundsInParent();
            double scale = WIDTH / Math.max(boundsInParent.getWidth(), boundsInParent.getHeight());

            SnapshotParameters snapParams = new SnapshotParameters();
            snapParams.setTransform(new Scale(scale, scale));
            snapParams.setFill(Color.TRANSPARENT);

            // Write it
            WritableImage imageIcon = iconNode.snapshot(snapParams, null);
            BufferedImage buffImage = SwingFXUtils.fromFXImage(imageIcon, null);
            try {
                File tempOutputImage = File.createTempFile("lcimage", "." + dictionaryInformation.dictionary.imageExtension);
                ImageIO.write(buffImage, dictionaryInformation.dictionary.imageExtension, tempOutputImage);

                // Hash
                String sha256 = fileSha256HexToString(tempOutputImage);
                IOUtils.copyFiles(tempOutputImage, new File(outputDir.getPath() + File.separator + sha256 + "." + dictionaryInformation.dictionary.imageExtension));

                ImageElement imageElement = new ImageElement();
                imageElement.id = sha256;
                imageElement.keywords = FluentHashMap.map(LANGUAGE_CODE, keywords);
                imageElement.name = imageElement.keywords.get(LANGUAGE_CODE)[0];
                images.add(imageElement);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        dictionaryInformation.dictionary.images = images;

        // Save dictionary
        try (PrintWriter pw = new PrintWriter(new File(outputDir.getParentFile() + File.separator + dictionaryInformation.dicId + ".json"), "UTF-8")) {
            GSON.toJson(dictionaryInformation.dictionary, pw);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        DicInfo fontAwesome = new DicInfo(
                new ImageDictionary("fontawesome", "image.dictionary.description.fontawesome", "image.dictionary.author.fontawesome",
                        "png", "https://fontawesome.com/", false),
                new File("D:\\ARASAAC\\FR_Pictogrammes_couleur"), "fontawesome", true, true, true, false, false, false);

        Map<Character, String[]> icons = new HashMap<>();

        // MANUAL TRANSLATION
        icons(icons, FontAwesome.Glyph.TRASH, "poubelle", "effacer", "supprimer");
        icons(icons, FontAwesome.Glyph.ARROW_DOWN, "flèche bas", "bas", "descendre");
        icons(icons, FontAwesome.Glyph.ARROW_UP, "flèche haut", "haut", "monter");
        icons(icons, FontAwesome.Glyph.ARROW_RIGHT, "flèche droite", "droite");
        icons(icons, FontAwesome.Glyph.ARROW_LEFT, "flèche gauche", "gauche");
        icons(icons, FontAwesome.Glyph.BACKWARD, "effacer", "retour arrière", "reculer");
        icons(icons, FontAwesome.Glyph.CHEVRON_DOWN, "flèche bas", "bas", "descendre");
        icons(icons, FontAwesome.Glyph.CHEVRON_UP, "flèche haut", "haut", "monter");
        icons(icons, FontAwesome.Glyph.CHEVRON_RIGHT, "flèche droite", "droite");
        icons(icons, FontAwesome.Glyph.CHEVRON_LEFT, "flèche gauche", "gauche");
        icons(icons, FontAwesome.Glyph.BARS, "liste", "points", "lignes");
        icons(icons, FontAwesome.Glyph.BOOK, "livre");
        icons(icons, FontAwesome.Glyph.CALENDAR, "calendrier", "agenda", "rendez-vous");
        icons(icons, FontAwesome.Glyph.CHECK, "valider", "confirmer", "ok");
        icons(icons, FontAwesome.Glyph.COPY, "copier", "cloner", "dupliquer");
        icons(icons, FontAwesome.Glyph.DESKTOP, "bureau", "ordinateur", "machine", "écran");
        icons(icons, FontAwesome.Glyph.DATABASE, "liste", "données", "plusieurs");
        icons(icons, FontAwesome.Glyph.TIMES, "annuler", "effacer", "fermer");
        icons(icons, FontAwesome.Glyph.PENCIL, "écrire", "crayon", "stylo", "éditer");
        icons(icons, FontAwesome.Glyph.WARNING, "attention", "précaution", "exclamation");
        icons(icons, FontAwesome.Glyph.EXCLAMATION, "attention", "précaution", "exclamation");
        icons(icons, FontAwesome.Glyph.LINK, "lien", "aller", "partir");
        icons(icons, FontAwesome.Glyph.FOLDER, "dossier", "fichier", "catégorie");
        icons(icons, FontAwesome.Glyph.INFO, "informations", "aider");
        icons(icons, FontAwesome.Glyph.IMAGE, "image", "pictogramme", "afficher");
        icons(icons, FontAwesome.Glyph.HOTEL, "hôtel", "maison");
        icons(icons, FontAwesome.Glyph.SHARE, "partager", "diffuser", "donner");
        icons(icons, FontAwesome.Glyph.THUMBS_UP, "ok", "d'accord", "confimer", "aimer");

        generateAutoTranslation(icons);

        // AUTO GENERATED TRANSLATION
        icons(icons, FontAwesome.Glyph.ADJUST, "régler");
        icons(icons, FontAwesome.Glyph.ADN, "adn");
        icons(icons, FontAwesome.Glyph.ALIGN_CENTER, "Aligné au centre");
        icons(icons, FontAwesome.Glyph.ALIGN_JUSTIFY, "align justifier");
        icons(icons, FontAwesome.Glyph.ALIGN_LEFT, "alignez à gauche");
        icons(icons, FontAwesome.Glyph.ALIGN_RIGHT, "Aligné à droite");
        icons(icons, FontAwesome.Glyph.AMBULANCE, "ambulance");
        icons(icons, FontAwesome.Glyph.ANCHOR, "ancre");
        icons(icons, FontAwesome.Glyph.ANDROID, "Android");
        icons(icons, FontAwesome.Glyph.ANGELLIST, "AngelList");
        icons(icons, FontAwesome.Glyph.ANGLE_DOUBLE_DOWN, "double angle vers le bas");
        icons(icons, FontAwesome.Glyph.ANGLE_DOUBLE_LEFT, "double angle gauche");
        icons(icons, FontAwesome.Glyph.ANGLE_DOUBLE_RIGHT, "double angle droit");
        icons(icons, FontAwesome.Glyph.ANGLE_DOUBLE_UP, "angle double up");
        icons(icons, FontAwesome.Glyph.ANGLE_DOWN, "angle vers le bas");
        icons(icons, FontAwesome.Glyph.ANGLE_LEFT, "angle gauche");
        icons(icons, FontAwesome.Glyph.ANGLE_RIGHT, "d&#39;angle droit");
        icons(icons, FontAwesome.Glyph.ANGLE_UP, "angle haut");
        icons(icons, FontAwesome.Glyph.APPLE, "Pomme");
        icons(icons, FontAwesome.Glyph.ARCHIVE, "archiver");
        icons(icons, FontAwesome.Glyph.AREA_CHART, "Tableau de zone");
        icons(icons, FontAwesome.Glyph.ARROW_CIRCLE_DOWN, "flèche cercle vers le bas");
        icons(icons, FontAwesome.Glyph.ARROW_CIRCLE_LEFT, "flèche gauche cercle");
        icons(icons, FontAwesome.Glyph.ARROW_CIRCLE_O_DOWN, "flèche vers le bas cercle o");
        icons(icons, FontAwesome.Glyph.ARROW_CIRCLE_O_LEFT, "flèche gauche cercle o");
        icons(icons, FontAwesome.Glyph.ARROW_CIRCLE_O_RIGHT, "flèche droite cercle o");
        icons(icons, FontAwesome.Glyph.ARROW_CIRCLE_O_UP, "flèche cercle o up");
        icons(icons, FontAwesome.Glyph.ARROW_CIRCLE_RIGHT, "flèche droite du cercle");
        icons(icons, FontAwesome.Glyph.ARROW_CIRCLE_UP, "flèche cercle haut");
        icons(icons, FontAwesome.Glyph.ARROWS, "flèches");
        icons(icons, FontAwesome.Glyph.ARROWS_ALT, "flèches");
        icons(icons, FontAwesome.Glyph.ARROWS_H, "flèches h");
        icons(icons, FontAwesome.Glyph.ARROWS_V, "flèches v");
        icons(icons, FontAwesome.Glyph.ASTERISK, "astérisque");
        icons(icons, FontAwesome.Glyph.AT, "à");
        icons(icons, FontAwesome.Glyph.AUTOMOBILE, "voiture");
        icons(icons, FontAwesome.Glyph.BAN, "interdire");
        icons(icons, FontAwesome.Glyph.BANK, "banque");
        icons(icons, FontAwesome.Glyph.BAR_CHART, "diagramme à bandes");
        icons(icons, FontAwesome.Glyph.BAR_CHART_O, "diagramme à barres o");
        icons(icons, FontAwesome.Glyph.BARCODE, "code à barre");
        icons(icons, FontAwesome.Glyph.BEER, "Bière");
        icons(icons, FontAwesome.Glyph.BEHANCE, "behance");
        icons(icons, FontAwesome.Glyph.BEHANCE_SQUARE, "behance carré");
        icons(icons, FontAwesome.Glyph.BELL, "cloche");
        icons(icons, FontAwesome.Glyph.BELL_O, "cloche o");
        icons(icons, FontAwesome.Glyph.BELL_SLASH, "slash cloche");
        icons(icons, FontAwesome.Glyph.BELL_SLASH_O, "cloche slash o");
        icons(icons, FontAwesome.Glyph.BICYCLE, "vélo");
        icons(icons, FontAwesome.Glyph.BINOCULARS, "jumelles");
        icons(icons, FontAwesome.Glyph.BIRTHDAY_CAKE, "gâteau d&#39;anniversaire");
        icons(icons, FontAwesome.Glyph.BITBUCKET, "bitbucket");
        icons(icons, FontAwesome.Glyph.BITBUCKET_SQUARE, "bitbucket carré");
        icons(icons, FontAwesome.Glyph.BITCOIN, "Bitcoin");
        icons(icons, FontAwesome.Glyph.BOLD, "audacieux");
        icons(icons, FontAwesome.Glyph.BOLT, "boulon");
        icons(icons, FontAwesome.Glyph.BOMB, "bombe");
        icons(icons, FontAwesome.Glyph.BOOKMARK, "signet");
        icons(icons, FontAwesome.Glyph.BOOKMARK_ALT, "signet");
        icons(icons, FontAwesome.Glyph.BRIEFCASE, "mallette");
        icons(icons, FontAwesome.Glyph.BTC, "btc");
        icons(icons, FontAwesome.Glyph.BUG, "punaise");
        icons(icons, FontAwesome.Glyph.BUILDING, "bâtiment");
        icons(icons, FontAwesome.Glyph.BUILDING_ALT, "bâtiment");
        icons(icons, FontAwesome.Glyph.BULLHORN, "porte-voix");
        icons(icons, FontAwesome.Glyph.BULLSEYE, "Bullseye");
        icons(icons, FontAwesome.Glyph.BUS, "autobus");
        icons(icons, FontAwesome.Glyph.BUYSELLADS, "BuySellAds");
        icons(icons, FontAwesome.Glyph.CAB, "taxi");
        icons(icons, FontAwesome.Glyph.CALCULATOR, "calculatrice");
        icons(icons, FontAwesome.Glyph.CALENDAR_ALT, "calendrier");
        icons(icons, FontAwesome.Glyph.CAMERA, "caméra");
        icons(icons, FontAwesome.Glyph.CAMERA_RETRO, "rétro caméra");
        icons(icons, FontAwesome.Glyph.CAR, "voiture");
        icons(icons, FontAwesome.Glyph.CARET_DOWN, "caret vers le bas");
        icons(icons, FontAwesome.Glyph.CARET_LEFT, "caret gauche");
        icons(icons, FontAwesome.Glyph.CARET_RIGHT, "droit caret");
        icons(icons, FontAwesome.Glyph.CARET_SQUARE_ALT_DOWN, "Caret carré vers le bas");
        icons(icons, FontAwesome.Glyph.CARET_SQUARE_ALT_LEFT, "gauche caret carré");
        icons(icons, FontAwesome.Glyph.CARET_SQUARE_ALT_RIGHT, "droite caret carré");
        icons(icons, FontAwesome.Glyph.CARET_SQUARE_ALT_UP, "Caret up carré");
        icons(icons, FontAwesome.Glyph.CARET_UP, "caret up");
        icons(icons, FontAwesome.Glyph.CART_ARROW_DOWN, "panier flèche vers le bas");
        icons(icons, FontAwesome.Glyph.CART_PLUS, "panier plus");
        icons(icons, FontAwesome.Glyph.CC, "cc");
        icons(icons, FontAwesome.Glyph.CC_AMEX, "cc AMEX");
        icons(icons, FontAwesome.Glyph.CC_DISCOVER, "cc découvrir");
        icons(icons, FontAwesome.Glyph.CC_MASTERCARD, "cc mastercard");
        icons(icons, FontAwesome.Glyph.CC_PAYPAL, "cc paypal");
        icons(icons, FontAwesome.Glyph.CC_STRIPE, "bande cc");
        icons(icons, FontAwesome.Glyph.CC_VISA, "visa cc");
        icons(icons, FontAwesome.Glyph.CERTIFICATE, "certificat");
        icons(icons, FontAwesome.Glyph.CHAIN_BROKEN, "chaîne brisée");
        icons(icons, FontAwesome.Glyph.CHECK_CIRCLE, "cercle chèque");
        icons(icons, FontAwesome.Glyph.CHECK_CIRCLE_ALT, "cercle chèque");
        icons(icons, FontAwesome.Glyph.CHECK_SQUARE, "chèque carré");
        icons(icons, FontAwesome.Glyph.CHECK_SQUARE_ALT, "chèque carré");
        icons(icons, FontAwesome.Glyph.CHEVRON_CIRCLE_DOWN, "vers le bas cercle chevron");
        icons(icons, FontAwesome.Glyph.CHEVRON_CIRCLE_LEFT, "gauche cercle chevron");
        icons(icons, FontAwesome.Glyph.CHEVRON_CIRCLE_RIGHT, "droite cercle chevron");
        icons(icons, FontAwesome.Glyph.CHEVRON_CIRCLE_UP, "cercle en chevron");
        icons(icons, FontAwesome.Glyph.CHILD, "enfant");
        icons(icons, FontAwesome.Glyph.CIRCLE, "cercle");
        icons(icons, FontAwesome.Glyph.CIRCLE_ALT, "cercle");
        icons(icons, FontAwesome.Glyph.CIRCLE_ALT_NOTCH, "encoche de cercle");
        icons(icons, FontAwesome.Glyph.CIRCLE_THIN, "cercle mince");
        icons(icons, FontAwesome.Glyph.CLIPBOARD, "presse-papiers");
        icons(icons, FontAwesome.Glyph.CLOCK_ALT, "l&#39;horloge");
        icons(icons, FontAwesome.Glyph.CLOUD, "nuage");
        icons(icons, FontAwesome.Glyph.CLOUD_DOWNLOAD, "télécharger nuage");
        icons(icons, FontAwesome.Glyph.CLOUD_UPLOAD, "télécharger nuage");
        icons(icons, FontAwesome.Glyph.CNY, "CNY");
        icons(icons, FontAwesome.Glyph.CODE, "code");
        icons(icons, FontAwesome.Glyph.CODE_FORK, "Code fourche");
        icons(icons, FontAwesome.Glyph.CODEPEN, "codepen");
        icons(icons, FontAwesome.Glyph.COFFEE, "café");
        icons(icons, FontAwesome.Glyph.COG, "dent");
        icons(icons, FontAwesome.Glyph.COGS, "roues dentées");
        icons(icons, FontAwesome.Glyph.COLUMNS, "Colonnes");
        icons(icons, FontAwesome.Glyph.COMMENT, "commentaire");
        icons(icons, FontAwesome.Glyph.COMMENT_ALT, "commentaire");
        icons(icons, FontAwesome.Glyph.COMMENTS, "commentaires");
        icons(icons, FontAwesome.Glyph.COMMENTS_ALT, "commentaires");
        icons(icons, FontAwesome.Glyph.COMPASS, "boussole");
        icons(icons, FontAwesome.Glyph.COMPRESS, "compresse");
        icons(icons, FontAwesome.Glyph.CONNECTDEVELOP, "connectdevelop");
        icons(icons, FontAwesome.Glyph.COPYRIGHT, "droits d&#39;auteur");
        icons(icons, FontAwesome.Glyph.CREDIT_CARD, "carte de crédit");
        icons(icons, FontAwesome.Glyph.CROP, "surgir");
        icons(icons, FontAwesome.Glyph.CROSSHAIRS, "ligne de mire");
        icons(icons, FontAwesome.Glyph.CSS3, "css3");
        icons(icons, FontAwesome.Glyph.CUBE, "cube");
        icons(icons, FontAwesome.Glyph.CUBES, "cubes");
        icons(icons, FontAwesome.Glyph.CUT, "Couper");
        icons(icons, FontAwesome.Glyph.CUTLERY, "coutellerie");
        icons(icons, FontAwesome.Glyph.DASHBOARD, "tableau de bord");
        icons(icons, FontAwesome.Glyph.DASHCUBE, "dashcube");
        icons(icons, FontAwesome.Glyph.DEDENT, "dedent");
        icons(icons, FontAwesome.Glyph.DELICIOUS, "délicieux");
        icons(icons, FontAwesome.Glyph.DEVIANTART, "deviantart");
        icons(icons, FontAwesome.Glyph.DIAMOND, "diamant");
        icons(icons, FontAwesome.Glyph.DIGG, "digg");
        icons(icons, FontAwesome.Glyph.DOLLAR, "dollar");
        icons(icons, FontAwesome.Glyph.DOT_CIRCLE_ALT, "cercle dot");
        icons(icons, FontAwesome.Glyph.DOWNLOAD, "Télécharger");
        icons(icons, FontAwesome.Glyph.DRIBBBLE, "Dribbble");
        icons(icons, FontAwesome.Glyph.DROPBOX, "dropbox");
        icons(icons, FontAwesome.Glyph.DRUPAL, "drupal");
        icons(icons, FontAwesome.Glyph.EDIT, "Éditer");
        icons(icons, FontAwesome.Glyph.EJECT, "éjecter");
        icons(icons, FontAwesome.Glyph.ELLIPSIS_H, "h ellipsis");
        icons(icons, FontAwesome.Glyph.ELLIPSIS_V, "v ellipsis");
        icons(icons, FontAwesome.Glyph.EMPIRE, "Empire");
        icons(icons, FontAwesome.Glyph.ENVELOPE, "enveloppe");
        icons(icons, FontAwesome.Glyph.ENVELOPE_ALT, "enveloppe");
        icons(icons, FontAwesome.Glyph.ENVELOPE_SQUARE, "enveloppe carrée");
        icons(icons, FontAwesome.Glyph.ERASER, "la gomme");
        icons(icons, FontAwesome.Glyph.EUR, "EUR");
        icons(icons, FontAwesome.Glyph.EURO, "euro");
        icons(icons, FontAwesome.Glyph.EXCHANGE, "échange");
        icons(icons, FontAwesome.Glyph.EXCLAMATION_CIRCLE, "cercle d&#39;exclamation");
        icons(icons, FontAwesome.Glyph.EXPAND, "développer");
        icons(icons, FontAwesome.Glyph.EXTERNAL_LINK, "lien externe");
        icons(icons, FontAwesome.Glyph.EXTERNAL_LINK_SQUARE, "place du lien externe");
        icons(icons, FontAwesome.Glyph.EYE, "œil");
        icons(icons, FontAwesome.Glyph.EYE_SLASH, "slash oeil");
        icons(icons, FontAwesome.Glyph.EYEDROPPER, "pipette");
        icons(icons, FontAwesome.Glyph.FACEBOOK, "Facebook");
        icons(icons, FontAwesome.Glyph.FACEBOOK_F, "f facebook");
        icons(icons, FontAwesome.Glyph.FACEBOOK_ALTFFICIAL, "facebookfficial");
        icons(icons, FontAwesome.Glyph.FACEBOOK_SQUARE, "carré facebook");
        icons(icons, FontAwesome.Glyph.FAST_BACKWARD, "retour rapide");
        icons(icons, FontAwesome.Glyph.FAST_FORWARD, "avance rapide");
        icons(icons, FontAwesome.Glyph.FAX, "fax");
        icons(icons, FontAwesome.Glyph.FEMALE, "femelle");
        icons(icons, FontAwesome.Glyph.FIGHTER_JET, "avion de chasse");
        icons(icons, FontAwesome.Glyph.FILE, "fichier");
        icons(icons, FontAwesome.Glyph.FILE_ARCHIVE_ALT, "fichier archive");
        icons(icons, FontAwesome.Glyph.FILE_AUDIO_ALT, "audio de fichiers");
        icons(icons, FontAwesome.Glyph.FILE_CODE_ALT, "Code de fichier");
        icons(icons, FontAwesome.Glyph.FILE_EXCEL_ALT, "excel fichier");
        icons(icons, FontAwesome.Glyph.FILE_IMAGE_ALT, "fichier image");
        icons(icons, FontAwesome.Glyph.FILE_MOVIE_ALT, "film de fichiers");
        icons(icons, FontAwesome.Glyph.FILE_ALT, "fichier");
        icons(icons, FontAwesome.Glyph.FILE_PDF_ALT, "fichier pdf");
        icons(icons, FontAwesome.Glyph.FILE_PHOTO_ALT, "photo fichier");
        icons(icons, FontAwesome.Glyph.FILE_PICTURE_ALT, "fichier image");
        icons(icons, FontAwesome.Glyph.FILE_POWERPOINT_ALT, "fichier powerpoint");
        icons(icons, FontAwesome.Glyph.FILE_SOUND_ALT, "son fichier");
        icons(icons, FontAwesome.Glyph.FILE_TEXT, "fichier texte");
        icons(icons, FontAwesome.Glyph.FILE_TEXT_ALT, "fichier texte");
        icons(icons, FontAwesome.Glyph.FILE_VIDEO_ALT, "fichier vidéo");
        icons(icons, FontAwesome.Glyph.FILE_WORD_ALT, "mot de fichier");
        icons(icons, FontAwesome.Glyph.FILE_ZIP_ALT, "fichier zip");
        icons(icons, FontAwesome.Glyph.FILM, "film");
        icons(icons, FontAwesome.Glyph.FILTER, "filtre");
        icons(icons, FontAwesome.Glyph.FIRE, "Feu");
        icons(icons, FontAwesome.Glyph.FIRE_EXTINGUISHER, "extincteur d&#39;incendie");
        icons(icons, FontAwesome.Glyph.FLAG, "drapeau");
        icons(icons, FontAwesome.Glyph.FLAG_CHECKERED, "drapeau à damiers");
        icons(icons, FontAwesome.Glyph.FLAG_ALT, "drapeau");
        icons(icons, FontAwesome.Glyph.FLASH, "éclat");
        icons(icons, FontAwesome.Glyph.FLASK, "ballon");
        icons(icons, FontAwesome.Glyph.FLICKR, "flickr");
        icons(icons, FontAwesome.Glyph.FLOPPY_ALT, "souple");
        icons(icons, FontAwesome.Glyph.FOLDER_ALT, "dossier");
        icons(icons, FontAwesome.Glyph.FOLDER_OPEN, "dossier ouvert");
        icons(icons, FontAwesome.Glyph.FOLDER_OPEN_ALT, "dossier ouvert");
        icons(icons, FontAwesome.Glyph.FONT, "Police de caractère");
        icons(icons, FontAwesome.Glyph.FORUMBEE, "forumbee");
        icons(icons, FontAwesome.Glyph.FORWARD, "vers l&#39;avant");
        icons(icons, FontAwesome.Glyph.FOURSQUARE, "foursquare");
        icons(icons, FontAwesome.Glyph.FROWN_ALT, "froncer les sourcils");
        icons(icons, FontAwesome.Glyph.FUTBOL_ALT, "futbol");
        icons(icons, FontAwesome.Glyph.GAMEPAD, "gamepad");
        icons(icons, FontAwesome.Glyph.GAVEL, "marteau");
        icons(icons, FontAwesome.Glyph.GBP, "GBP");
        icons(icons, FontAwesome.Glyph.GE, "ge");
        icons(icons, FontAwesome.Glyph.GEAR, "équipement");
        icons(icons, FontAwesome.Glyph.GEARS, "engrenage");
        icons(icons, FontAwesome.Glyph.GENDERLESS, "asexués");
        icons(icons, FontAwesome.Glyph.GIFT, "cadeau");
        icons(icons, FontAwesome.Glyph.GIT, "git");
        icons(icons, FontAwesome.Glyph.GIT_SQUARE, "carré git");
        icons(icons, FontAwesome.Glyph.GITHUB, "github");
        icons(icons, FontAwesome.Glyph.GITHUB_ALT, "github");
        icons(icons, FontAwesome.Glyph.GITHUB_SQUARE, "github carré");
        icons(icons, FontAwesome.Glyph.GITTIP, "gittip");
        icons(icons, FontAwesome.Glyph.GLASS, "verre");
        icons(icons, FontAwesome.Glyph.GLOBE, "globe");
        icons(icons, FontAwesome.Glyph.GOOGLE, "Google");
        icons(icons, FontAwesome.Glyph.GOOGLE_PLUS, "Google Plus");
        icons(icons, FontAwesome.Glyph.GOOGLE_PLUS_SQUARE, "google plus carré");
        icons(icons, FontAwesome.Glyph.GOOGLE_WALLET, "portefeuille Google");
        icons(icons, FontAwesome.Glyph.GRADUATION_CAP, "graduation cap");
        icons(icons, FontAwesome.Glyph.GRATIPAY, "gratipay");
        icons(icons, FontAwesome.Glyph.GROUP, "groupe");
        icons(icons, FontAwesome.Glyph.H_SQUARE, "h carré");
        icons(icons, FontAwesome.Glyph.HACKER_NEWS, "nouvelles hacker");
        icons(icons, FontAwesome.Glyph.HAND_ALT_DOWN, "transmettre");
        icons(icons, FontAwesome.Glyph.HAND_ALT_LEFT, "main gauche");
        icons(icons, FontAwesome.Glyph.HAND_ALT_RIGHT, "de la main droite");
        icons(icons, FontAwesome.Glyph.HAND_ALT_UP, "main");
        icons(icons, FontAwesome.Glyph.HDD_ALT, "hdd");
        icons(icons, FontAwesome.Glyph.HEADER, "entête");
        icons(icons, FontAwesome.Glyph.HEADPHONES, "écouteurs");
        icons(icons, FontAwesome.Glyph.HEART, "cœur");
        icons(icons, FontAwesome.Glyph.HEART_ALT, "cœur");
        icons(icons, FontAwesome.Glyph.HEARTBEAT, "battement de coeur");
        icons(icons, FontAwesome.Glyph.HISTORY, "l&#39;histoire");
        icons(icons, FontAwesome.Glyph.HOME, "Accueil");
        icons(icons, FontAwesome.Glyph.HOSPITAL_ALT, "hôpital");
        icons(icons, FontAwesome.Glyph.HTML5, "html5");
        icons(icons, FontAwesome.Glyph.ILS, "NIT");
        icons(icons, FontAwesome.Glyph.INBOX, "boîte de réception");
        icons(icons, FontAwesome.Glyph.INDENT, "tiret");
        icons(icons, FontAwesome.Glyph.INFO_CIRCLE, "cercle d&#39;info");
        icons(icons, FontAwesome.Glyph.INR, "inr");
        icons(icons, FontAwesome.Glyph.INSTAGRAM, "instagram");
        icons(icons, FontAwesome.Glyph.INSTITUTION, "institution");
        icons(icons, FontAwesome.Glyph.IOXHOST, "ioxhost");
        icons(icons, FontAwesome.Glyph.ITALIC, "italique");
        icons(icons, FontAwesome.Glyph.JOOMLA, "joomla");
        icons(icons, FontAwesome.Glyph.JPY, "JPY");
        icons(icons, FontAwesome.Glyph.JSFIDDLE, "jsFiddle");
        icons(icons, FontAwesome.Glyph.KEY, "clé");
        icons(icons, FontAwesome.Glyph.KEYBOARD_ALT, "clavier");
        icons(icons, FontAwesome.Glyph.KRW, "krw");
        icons(icons, FontAwesome.Glyph.LANGUAGE, "Langue");
        icons(icons, FontAwesome.Glyph.LAPTOP, "portable");
        icons(icons, FontAwesome.Glyph.LASTFM, "LastFM");
        icons(icons, FontAwesome.Glyph.LASTFM_SQUARE, "carré LastFM");
        icons(icons, FontAwesome.Glyph.LEAF, "feuille");
        icons(icons, FontAwesome.Glyph.LEANPUB, "leanpub");
        icons(icons, FontAwesome.Glyph.LEGAL, "légal");
        icons(icons, FontAwesome.Glyph.LEMON_ALT, "citron");
        icons(icons, FontAwesome.Glyph.LEVEL_DOWN, "descendre d&#39;un niveau");
        icons(icons, FontAwesome.Glyph.LEVEL_UP, "niveau supérieur");
        icons(icons, FontAwesome.Glyph.LIFE_BOUY, "vie bouy");
        icons(icons, FontAwesome.Glyph.LIFE_BUOY, "bouée de sauvetage");
        icons(icons, FontAwesome.Glyph.LIFE_RING, "Bouee de sauvetage");
        icons(icons, FontAwesome.Glyph.LIFE_SAVER, "épargnant de vie");
        icons(icons, FontAwesome.Glyph.LIGHTBULB_ALT, "ampoule");
        icons(icons, FontAwesome.Glyph.LINE_CHART, "graphique en ligne");
        icons(icons, FontAwesome.Glyph.LINKEDIN, "linkedin");
        icons(icons, FontAwesome.Glyph.LINKEDIN_SQUARE, "linkedin carré");
        icons(icons, FontAwesome.Glyph.LINUX, "linux");
        icons(icons, FontAwesome.Glyph.LIST, "liste");
        icons(icons, FontAwesome.Glyph.LIST_ALT, "liste");
        icons(icons, FontAwesome.Glyph.LIST_OL, "liste ol");
        icons(icons, FontAwesome.Glyph.LIST_UL, "liste ul");
        icons(icons, FontAwesome.Glyph.LOCATION_ARROW, "emplacement flèche");
        icons(icons, FontAwesome.Glyph.LOCK, "fermer à clé");
        icons(icons, FontAwesome.Glyph.LONG_ARROW_DOWN, "longue flèche vers le bas");
        icons(icons, FontAwesome.Glyph.LONG_ARROW_LEFT, "longue flèche gauche");
        icons(icons, FontAwesome.Glyph.LONG_ARROW_RIGHT, "longue flèche droite");
        icons(icons, FontAwesome.Glyph.LONG_ARROW_UP, "longue flèche vers le haut");
        icons(icons, FontAwesome.Glyph.MAGIC, "la magie");
        icons(icons, FontAwesome.Glyph.MAGNET, "aimant");
        icons(icons, FontAwesome.Glyph.MAIL_REPLY, "mail de réponse");
        icons(icons, FontAwesome.Glyph.MAIL_REPLY_ALL, "mail de répondre à tous");
        icons(icons, FontAwesome.Glyph.MALE, "Masculin");
        icons(icons, FontAwesome.Glyph.MAP_MARKER, "marqueur de carte");
        icons(icons, FontAwesome.Glyph.MARS, "Mars");
        icons(icons, FontAwesome.Glyph.MARS_DOUBLE, "mars doubles");
        icons(icons, FontAwesome.Glyph.MARS_STROKE, "AVC mars");
        icons(icons, FontAwesome.Glyph.MARS_STROKE_H, "mars temps h");
        icons(icons, FontAwesome.Glyph.MARS_STROKE_V, "Mars course v");
        icons(icons, FontAwesome.Glyph.MAXCDN, "maxcdn");
        icons(icons, FontAwesome.Glyph.MEANPATH, "meanpath");
        icons(icons, FontAwesome.Glyph.MEDIUM, "moyen");
        icons(icons, FontAwesome.Glyph.MEDKIT, "medkit");
        icons(icons, FontAwesome.Glyph.MEH_ALT, "meh");
        icons(icons, FontAwesome.Glyph.MERCURY, "Mercure");
        icons(icons, FontAwesome.Glyph.MICROPHONE, "microphone");
        icons(icons, FontAwesome.Glyph.MICROPHONE_SLASH, "microphone barre oblique");
        icons(icons, FontAwesome.Glyph.MINUS, "moins");
        icons(icons, FontAwesome.Glyph.MINUS_CIRCLE, "cercle moins");
        icons(icons, FontAwesome.Glyph.MINUS_SQUARE, "carré moins");
        icons(icons, FontAwesome.Glyph.MINUS_SQUARE_ALT, "carré moins");
        icons(icons, FontAwesome.Glyph.MOBILE, "mobile");
        icons(icons, FontAwesome.Glyph.MOBILE_PHONE, "téléphone portable");
        icons(icons, FontAwesome.Glyph.MONEY, "argent");
        icons(icons, FontAwesome.Glyph.MOON_ALT, "lune");
        icons(icons, FontAwesome.Glyph.MORTAR_BOARD, "panneau de mortier");
        icons(icons, FontAwesome.Glyph.MOTORCYCLE, "moto");
        icons(icons, FontAwesome.Glyph.MUSIC, "la musique");
        icons(icons, FontAwesome.Glyph.NEUTER, "neutre");
        icons(icons, FontAwesome.Glyph.NEWSPAPER_ALT, "journal");
        icons(icons, FontAwesome.Glyph.OPENID, "Openid");
        icons(icons, FontAwesome.Glyph.OUTDENT, "outdent");
        icons(icons, FontAwesome.Glyph.PAGELINES, "PageLines");
        icons(icons, FontAwesome.Glyph.PAINT_BRUSH, "pinceau");
        icons(icons, FontAwesome.Glyph.PAPER_PLANE, "avion en papier");
        icons(icons, FontAwesome.Glyph.PAPER_PLANE_ALT, "avion en papier");
        icons(icons, FontAwesome.Glyph.PAPERCLIP, "trombone");
        icons(icons, FontAwesome.Glyph.PARAGRAPH, "paragraphe");
        icons(icons, FontAwesome.Glyph.PASTE, "pâte");
        icons(icons, FontAwesome.Glyph.PAUSE, "pause");
        icons(icons, FontAwesome.Glyph.PAW, "patte");
        icons(icons, FontAwesome.Glyph.PAYPAL, "Pay Pal");
        icons(icons, FontAwesome.Glyph.PENCIL_SQUARE, "carré crayon");
        icons(icons, FontAwesome.Glyph.PENCIL_SQUARE_ALT, "carré crayon");
        icons(icons, FontAwesome.Glyph.PHONE, "téléphone");
        icons(icons, FontAwesome.Glyph.PHONE_SQUARE, "carré de téléphone");
        icons(icons, FontAwesome.Glyph.PIE_CHART, "diagramme circulaire");
        icons(icons, FontAwesome.Glyph.PIED_PIPER, "joueur de fifre");
        icons(icons, FontAwesome.Glyph.PIED_PIPER_ALT, "joueur de fifre");
        icons(icons, FontAwesome.Glyph.PINTEREST, "pinterest");
        icons(icons, FontAwesome.Glyph.PINTEREST_P, "pinterest p");
        icons(icons, FontAwesome.Glyph.PINTEREST_SQUARE, "pinterest carré");
        icons(icons, FontAwesome.Glyph.PLANE, "avion");
        icons(icons, FontAwesome.Glyph.PLAY, "jouer");
        icons(icons, FontAwesome.Glyph.PLAY_CIRCLE, "cercle de jeu");
        icons(icons, FontAwesome.Glyph.PLAY_CIRCLE_ALT, "cercle de jeu");
        icons(icons, FontAwesome.Glyph.PLUG, "prise de courant");
        icons(icons, FontAwesome.Glyph.PLUS, "plus");
        icons(icons, FontAwesome.Glyph.PLUS_CIRCLE, "ainsi que le cercle");
        icons(icons, FontAwesome.Glyph.PLUS_SQUARE, "en plus carré");
        icons(icons, FontAwesome.Glyph.PLUS_SQUARE_ALT, "en plus carré");
        icons(icons, FontAwesome.Glyph.POWER_OFF, "éteindre");
        icons(icons, FontAwesome.Glyph.PRINT, "impression");
        icons(icons, FontAwesome.Glyph.PUZZLE_PIECE, "pièce de puzzle");
        icons(icons, FontAwesome.Glyph.QQ, "qq");
        icons(icons, FontAwesome.Glyph.QRCODE, "QR Code");
        icons(icons, FontAwesome.Glyph.QUESTION, "question");
        icons(icons, FontAwesome.Glyph.QUESTION_CIRCLE, "question cercle");
        icons(icons, FontAwesome.Glyph.QUOTE_LEFT, "citation gauche");
        icons(icons, FontAwesome.Glyph.QUOTE_RIGHT, "citation droite");
        icons(icons, FontAwesome.Glyph.RA, "ra");
        icons(icons, FontAwesome.Glyph.RANDOM, "Aléatoire");
        icons(icons, FontAwesome.Glyph.REBEL, "rebelle");
        icons(icons, FontAwesome.Glyph.RECYCLE, "recycler");
        icons(icons, FontAwesome.Glyph.REDDIT, "reddit");
        icons(icons, FontAwesome.Glyph.REDDIT_SQUARE, "carré reddit");
        icons(icons, FontAwesome.Glyph.REFRESH, "rafraîchir");
        icons(icons, FontAwesome.Glyph.RENREN, "Renren");
        icons(icons, FontAwesome.Glyph.REPEAT, "répéter");
        icons(icons, FontAwesome.Glyph.REPLY, "réponse");
        icons(icons, FontAwesome.Glyph.REPLY_ALL, "répondre à tous");
        icons(icons, FontAwesome.Glyph.RETWEET, "retweet");
        icons(icons, FontAwesome.Glyph.RMB, "rmb");
        icons(icons, FontAwesome.Glyph.ROAD, "route");
        icons(icons, FontAwesome.Glyph.ROCKET, "fusée");
        icons(icons, FontAwesome.Glyph.ROTATE_LEFT, "tourne à gauche");
        icons(icons, FontAwesome.Glyph.ROTATE_RIGHT, "tourner à droite");
        icons(icons, FontAwesome.Glyph.ROUBLE, "rouble");
        icons(icons, FontAwesome.Glyph.RSS, "rss");
        icons(icons, FontAwesome.Glyph.RSS_SQUARE, "carré rss");
        icons(icons, FontAwesome.Glyph.RUB, "frotter");
        icons(icons, FontAwesome.Glyph.RUBLE, "rouble");
        icons(icons, FontAwesome.Glyph.RUPEE, "roupie");
        icons(icons, FontAwesome.Glyph.SAVE, "enregistrer");
        icons(icons, FontAwesome.Glyph.SCISSORS, "les ciseaux");
        icons(icons, FontAwesome.Glyph.SEARCH, "chercher");
        icons(icons, FontAwesome.Glyph.SEARCH_MINUS, "Recherche moins");
        icons(icons, FontAwesome.Glyph.SEARCH_PLUS, "recherche, plus");
        icons(icons, FontAwesome.Glyph.SELLSY, "sellsy");
        icons(icons, FontAwesome.Glyph.SEND, "envoyer");
        icons(icons, FontAwesome.Glyph.SEND_ALT, "envoyer");
        icons(icons, FontAwesome.Glyph.SERVER, "serveur");
        icons(icons, FontAwesome.Glyph.SHARE_ALT, "partager");
        icons(icons, FontAwesome.Glyph.SHARE_ALT_SQUARE, "carré d&#39;actions");
        icons(icons, FontAwesome.Glyph.SHARE_SQUARE, "carré d&#39;actions");
        icons(icons, FontAwesome.Glyph.SHARE_SQUARE_ALT, "carré d&#39;actions");
        icons(icons, FontAwesome.Glyph.SHEKEL, "shekel");
        icons(icons, FontAwesome.Glyph.SHEQEL, "shekel");
        icons(icons, FontAwesome.Glyph.SHIELD, "bouclier");
        icons(icons, FontAwesome.Glyph.SHIP, "navire");
        icons(icons, FontAwesome.Glyph.SHIRTSINBULK, "shirtsinbulk");
        icons(icons, FontAwesome.Glyph.SHOPPING_CART, "Panier");
        icons(icons, FontAwesome.Glyph.SIGN_IN, "se connecter");
        icons(icons, FontAwesome.Glyph.SIGN_OUT, "Déconnexion");
        icons(icons, FontAwesome.Glyph.SIGNAL, "signal");
        icons(icons, FontAwesome.Glyph.SIMPLYBUILT, "simplybuilt");
        icons(icons, FontAwesome.Glyph.SITEMAP, "Plan du site");
        icons(icons, FontAwesome.Glyph.SKYATLAS, "skyatlas");
        icons(icons, FontAwesome.Glyph.SKYPE, "skype");
        icons(icons, FontAwesome.Glyph.SLACK, "mou");
        icons(icons, FontAwesome.Glyph.SLIDERS, "curseurs");
        icons(icons, FontAwesome.Glyph.SLIDESHARE, "SlideShare");
        icons(icons, FontAwesome.Glyph.SMILE_ALT, "sourire");
        icons(icons, FontAwesome.Glyph.SOCCER_BALL_ALT, "ballon de football");
        icons(icons, FontAwesome.Glyph.SORT, "Trier");
        icons(icons, FontAwesome.Glyph.SORT_ALPHA_ASC, "trier alpha asc");
        icons(icons, FontAwesome.Glyph.SORT_ALPHA_DESC, "trier alpha desc");
        icons(icons, FontAwesome.Glyph.SORT_AMOUNT_ASC, "sorte montant asc");
        icons(icons, FontAwesome.Glyph.SORT_AMOUNT_DESC, "sorte montant desc");
        icons(icons, FontAwesome.Glyph.SORT_ASC, "Trier asc");
        icons(icons, FontAwesome.Glyph.SORT_DESC, "Triage par ordre décroissant");
        icons(icons, FontAwesome.Glyph.SORT_DOWN, "trier vers le bas");
        icons(icons, FontAwesome.Glyph.SORT_NUMERIC_ASC, "tri numérique asc");
        icons(icons, FontAwesome.Glyph.SORT_NUMERIC_DESC, "tri numérique desc");
        icons(icons, FontAwesome.Glyph.SORT_UP, "Trier");
        icons(icons, FontAwesome.Glyph.SOUNDCLOUD, "SoundCloud");
        icons(icons, FontAwesome.Glyph.SPACE_SHUTTLE, "navette spatiale");
        icons(icons, FontAwesome.Glyph.SPINNER, "fileur");
        icons(icons, FontAwesome.Glyph.SPOON, "cuillère");
        icons(icons, FontAwesome.Glyph.SPOTIFY, "spotify");
        icons(icons, FontAwesome.Glyph.SQUARE, "carré");
        icons(icons, FontAwesome.Glyph.SQUARE_ALT, "carré");
        icons(icons, FontAwesome.Glyph.STACK_EXCHANGE, "échange pile");
        icons(icons, FontAwesome.Glyph.STACK_OVERFLOW, "débordement pile");
        icons(icons, FontAwesome.Glyph.STAR, "étoile");
        icons(icons, FontAwesome.Glyph.STAR_HALF, "étoile demi");
        icons(icons, FontAwesome.Glyph.STAR_HALF_EMPTY, "étoile à moitié vide");
        icons(icons, FontAwesome.Glyph.STAR_HALF_FULL, "étoile à moitié plein");
        icons(icons, FontAwesome.Glyph.STAR_HALF_ALT, "étoile demi");
        icons(icons, FontAwesome.Glyph.STAR_ALT, "étoile");
        icons(icons, FontAwesome.Glyph.STEAM, "vapeur");
        icons(icons, FontAwesome.Glyph.STEAM_SQUARE, "carré de vapeur");
        icons(icons, FontAwesome.Glyph.STEP_BACKWARD, "reculer");
        icons(icons, FontAwesome.Glyph.STEP_FORWARD, "s&#39;avancer");
        icons(icons, FontAwesome.Glyph.STETHOSCOPE, "stéthoscope");
        icons(icons, FontAwesome.Glyph.STOP, "Arrêtez");
        icons(icons, FontAwesome.Glyph.STREET_VIEW, "vue sur la rue");
        icons(icons, FontAwesome.Glyph.STRIKETHROUGH, "biffés");
        icons(icons, FontAwesome.Glyph.STUMBLEUPON, "stumbleupon");
        icons(icons, FontAwesome.Glyph.STUMBLEUPON_CIRCLE, "cercle stumbleupon");
        icons(icons, FontAwesome.Glyph.SUBSCRIPT, "indice");
        icons(icons, FontAwesome.Glyph.SUBWAY, "métro");
        icons(icons, FontAwesome.Glyph.SUITCASE, "valise");
        icons(icons, FontAwesome.Glyph.SUN_ALT, "Soleil");
        icons(icons, FontAwesome.Glyph.SUPERSCRIPT, "superscript");
        icons(icons, FontAwesome.Glyph.SUPPORT, "soutien");
        icons(icons, FontAwesome.Glyph.TABLE, "table");
        icons(icons, FontAwesome.Glyph.TABLET, "tablette");
        icons(icons, FontAwesome.Glyph.TACHOMETER, "tachymètre");
        icons(icons, FontAwesome.Glyph.TAG, "marque");
        icons(icons, FontAwesome.Glyph.TAGS, "Mots clés");
        icons(icons, FontAwesome.Glyph.TASKS, "Tâches");
        icons(icons, FontAwesome.Glyph.TAXI, "Taxi");
        icons(icons, FontAwesome.Glyph.TENCENT_WEIBO, "Tencent Weibo");
        icons(icons, FontAwesome.Glyph.TERMINAL, "Terminal");
        icons(icons, FontAwesome.Glyph.TEXT_HEIGHT, "hauteur du texte");
        icons(icons, FontAwesome.Glyph.TEXT_WIDTH, "largeur du texte");
        icons(icons, FontAwesome.Glyph.TH, "e");
        icons(icons, FontAwesome.Glyph.TH_LARGE, "e grand");
        icons(icons, FontAwesome.Glyph.TH_LIST, "e liste");
        icons(icons, FontAwesome.Glyph.THUMB_TACK, "punaise bleue");
        icons(icons, FontAwesome.Glyph.THUMBS_DOWN, "pouces vers le bas");
        icons(icons, FontAwesome.Glyph.THUMBS_ALT_DOWN, "pouces vers le bas");
        icons(icons, FontAwesome.Glyph.THUMBS_ALT_UP, "pouces vers le haut");
        icons(icons, FontAwesome.Glyph.TICKET, "billet");
        icons(icons, FontAwesome.Glyph.TIMES_CIRCLE, "cercle fois");
        icons(icons, FontAwesome.Glyph.TIMES_CIRCLE_ALT, "cercle fois");
        icons(icons, FontAwesome.Glyph.TINT, "teinte");
        icons(icons, FontAwesome.Glyph.TOGGLE_DOWN, "basculer vers le bas");
        icons(icons, FontAwesome.Glyph.TOGGLE_LEFT, "bascule à gauche");
        icons(icons, FontAwesome.Glyph.TOGGLE_OFF, "basculer hors");
        icons(icons, FontAwesome.Glyph.TOGGLE_ON, "Basculez sur");
        icons(icons, FontAwesome.Glyph.TOGGLE_RIGHT, "droite bascule");
        icons(icons, FontAwesome.Glyph.TOGGLE_UP, "basculer vers le haut");
        icons(icons, FontAwesome.Glyph.TRAIN, "train");
        icons(icons, FontAwesome.Glyph.TRANSGENDER, "transgenres");
        icons(icons, FontAwesome.Glyph.TRANSGENDER_ALT, "transgenres");
        icons(icons, FontAwesome.Glyph.TRASH_ALT, "poubelle");
        icons(icons, FontAwesome.Glyph.TREE, "arbre");
        icons(icons, FontAwesome.Glyph.TRELLO, "Trello");
        icons(icons, FontAwesome.Glyph.TROPHY, "trophée");
        icons(icons, FontAwesome.Glyph.TRUCK, "un camion");
        icons(icons, FontAwesome.Glyph.TRY, "essayer");
        icons(icons, FontAwesome.Glyph.TTY, "TTY");
        icons(icons, FontAwesome.Glyph.TUMBLR, "tumblr");
        icons(icons, FontAwesome.Glyph.TUMBLR_SQUARE, "carré tumblr");
        icons(icons, FontAwesome.Glyph.TURKISH_LIRA, "Livre turque");
        icons(icons, FontAwesome.Glyph.TWITCH, "tic");
        icons(icons, FontAwesome.Glyph.TWITTER, "Twitter");
        icons(icons, FontAwesome.Glyph.TWITTER_SQUARE, "carré twitter");
        icons(icons, FontAwesome.Glyph.UMBRELLA, "parapluie");
        icons(icons, FontAwesome.Glyph.UNDERLINE, "souligner");
        icons(icons, FontAwesome.Glyph.UNDO, "annuler");
        icons(icons, FontAwesome.Glyph.UNIVERSITY, "Université");
        icons(icons, FontAwesome.Glyph.UNLINK, "unlink");
        icons(icons, FontAwesome.Glyph.UNLOCK, "ouvrir");
        icons(icons, FontAwesome.Glyph.UNLOCK_ALT, "ouvrir");
        icons(icons, FontAwesome.Glyph.UNSORTED, "non triés");
        icons(icons, FontAwesome.Glyph.UPLOAD, "télécharger");
        icons(icons, FontAwesome.Glyph.USD, "USD");
        icons(icons, FontAwesome.Glyph.USER, "utilisateur");
        icons(icons, FontAwesome.Glyph.USER_MD, "utilisateur md");
        icons(icons, FontAwesome.Glyph.USER_PLUS, "en plus d&#39;utilisateurs");
        icons(icons, FontAwesome.Glyph.USER_SECRET, "secret utilisateur");
        icons(icons, FontAwesome.Glyph.USER_TIMES, "temps utilisateur");
        icons(icons, FontAwesome.Glyph.USERS, "utilisateurs");
        icons(icons, FontAwesome.Glyph.VENUS, "Vénus");
        icons(icons, FontAwesome.Glyph.VENUS_DOUBLE, "vénus doubles");
        icons(icons, FontAwesome.Glyph.VENUS_MARS, "mars vénus");
        icons(icons, FontAwesome.Glyph.VIACOIN, "viacoin");
        icons(icons, FontAwesome.Glyph.VIDEO_CAMERA, "caméra vidéo");
        icons(icons, FontAwesome.Glyph.VIMEO_SQUARE, "carré vimeo");
        icons(icons, FontAwesome.Glyph.VINE, "vigne");
        icons(icons, FontAwesome.Glyph.VK, "vk");
        icons(icons, FontAwesome.Glyph.VOLUME_DOWN, "baisser le volume");
        icons(icons, FontAwesome.Glyph.VOLUME_OFF, "hors du volume");
        icons(icons, FontAwesome.Glyph.VOLUME_UP, "monter le son");
        icons(icons, FontAwesome.Glyph.WECHAT, "WeChat");
        icons(icons, FontAwesome.Glyph.WEIBO, "Weibo");
        icons(icons, FontAwesome.Glyph.WEIXIN, "Weixin");
        icons(icons, FontAwesome.Glyph.WHATSAPP, "WhatsApp");
        icons(icons, FontAwesome.Glyph.WHEELCHAIR, "fauteuil roulant");
        icons(icons, FontAwesome.Glyph.WIFI, "Wifi");
        icons(icons, FontAwesome.Glyph.WINDOWS, "les fenêtres");
        icons(icons, FontAwesome.Glyph.WON, "a gagné");
        icons(icons, FontAwesome.Glyph.WORDPRESS, "wordpress");
        icons(icons, FontAwesome.Glyph.WRENCH, "clé");
        icons(icons, FontAwesome.Glyph.XING, "xing");
        icons(icons, FontAwesome.Glyph.XING_SQUARE, "xing carré");
        icons(icons, FontAwesome.Glyph.YAHOO, "yahoo");
        icons(icons, FontAwesome.Glyph.YELP, "japper");
        icons(icons, FontAwesome.Glyph.YEN, "yen");
        icons(icons, FontAwesome.Glyph.YOUTUBE, "Youtube");
        icons(icons, FontAwesome.Glyph.YOUTUBE_PLAY, "jouer sur youtube");
        icons(icons, FontAwesome.Glyph.YOUTUBE_SQUARE, "carré youtube");

        generateImageDictionary(fontAwesome, icons);
        primaryStage.show();
        primaryStage.close();
    }

    // CONFIGURATION : GOOGLE_APPLICATION_CREDENTIALS to credentials json (ex : E:\Desktop\temp\translation-test-36d46f42a839.json)
    static void generateAutoTranslation(Map<Character, String[]> icons) {
        //        FontAwesome.Glyph[] values = FontAwesome.Glyph.values();
        //        Translate translate = TranslateOptions.getDefaultInstance().getService();
        //        try (PrintWriter pw = new PrintWriter(new File("code.txt"))) {
        //            for (FontAwesome.Glyph glyph : values) {
        //                if (!icons.containsKey(glyph.getChar())) {
        //                    String input = StringUtils.stripToEmpty(glyph.name().toLowerCase().replace("_", " ").replace(" alt", ""));
        //                    Translation translation = translate.translate(
        //                            input,
        //                            Translate.TranslateOption.sourceLanguage("en"),
        //                            Translate.TranslateOption.targetLanguage("fr"),
        //                            Translate.TranslateOption.model("base"));
        //                    pw.println("icons(icons, FontAwesome.Glyph." + glyph.name() + ",\"" + translation.getTranslatedText() + "\");");
        //                }
        //            }
        //        } catch (FileNotFoundException e) {
        //            e.printStackTrace();
        //        }
    }


    private static void icons(Map<Character, String[]> icons, FontAwesome.Glyph glyph, String... keywords) {
        icons.put(glyph.getChar(), keywords);
    }
}
