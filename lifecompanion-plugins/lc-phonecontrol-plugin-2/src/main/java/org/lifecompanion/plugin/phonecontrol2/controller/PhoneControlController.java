package org.lifecompanion.plugin.phonecontrol2.controller;

import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.plugin.phonecontrol2.PhoneControlPlugin;
import org.lifecompanion.plugin.phonecontrol2.PhoneControlPluginProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

//IMPORTS POUR L'EXTENSION PHONE CONTROL
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.HashSet;


/**
 * Controller for PhoneControl plugin
 */
public enum PhoneControlController implements ModeListenerI {
    INSTANCE;
    private static final Logger LOGGER = LoggerFactory.getLogger(PhoneControlController.class);

    // POUR L'EXTENSION PHONE CONTROL

    public static final String VAR_ID_CONNECTED_DEVICE = "PhoneControlConnectedDevice";
    public static final String VAR_ID_CALL_NUMBER = "PhoneControlCallNumber";
    public static final String VAR_ID_CONTACTS_LIST = "PhoneControlContactsList";
    public static final String VAR_ID_CONTACTS_LETTRES = "PhoneControlContactsLettres";
    public static final String VAR_ID_NB_CONTACTS_LETTRES = "PhoneControlNbContactsLettres";
    public static final String VAR_ID_CONTACT = "PhoneControlContact";
    public static final String VAR_ID_CONTACT_PRECEDENT = "PhoneControlContactPrecedent";
    public static final String VAR_ID_CONTACT_SUIVANT = "PhoneControlContactSuivant";
    public static final String VAR_ID_CALL_TIME = "PhoneControlCallTime";
    public static final String VAR_ID_CONTACT_RECHERCHE = "PhoneControlContactRecherche";
    private PhoneControlPluginProperties currentPhoneControlPluginProperties;

    private LCConfigurationI configuration;

    PhoneControlController() {
    }

    @Override
    public void modeStart(LCConfigurationI configuration) {
        this.configuration = configuration;
        // Get plugin properties for current configuration
        currentPhoneControlPluginProperties = configuration.getPluginConfigProperties(PhoneControlPlugin.ID, PhoneControlPluginProperties.class);
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        this.currentPhoneControlPluginProperties = null;
        this.configuration = null;
    }

    // IO
    //========================================================================
    public static File getResultBasePath(LCConfigurationI configuration) {
        return new File(IOHelper.getConfigurationDirectoryPath(
                ProfileController.INSTANCE.currentProfileProperty().get().getID(), configuration.getID())
                + "plugins" + File.separator
                + PhoneControlPlugin.ID + File.separator + "results");
    }
    //========================================================================

    // UTILS
    //========================================================================

    private static String selectedDevice = "Aucun appareil connecté";

    public static void setSelectedDevice(String device) {
        selectedDevice = device;
    }

    public static String getSelectedDeviceId() {
        if (selectedDevice == null || selectedDevice.equals("Aucun appareil connecté")) {
            return "Aucun appareil connecté";
        } else {
            return selectedDevice.split(" - ")[0];
        }
    }

    public String getSelectedDeviceName() {
        System.out.println(selectedDevice);
        if (selectedDevice == null || selectedDevice.equals("Aucun appareil connecté")) {
            return "Aucun appareil connecté";
        } else {
            return selectedDevice.split(" - ")[1];
        }
    }

    public static List<String> getConnectedDevices() {
        List<String> devicesList = new ArrayList<>();

        try {
            // Étape 1 : Obtenir la liste des appareils connectés
            Process process = Runtime.getRuntime().exec("adb devices");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.endsWith("device")) {
                        String[] parts = line.split("\t");
                        if (parts.length > 1) {
                            String deviceId = parts[0].trim();

                            // Étape 2 : Obtenir le nom de l'appareil
                            String deviceName = getDeviceName(deviceId);

                            // Ajouter l'ID et le nom combinés à la liste
                            devicesList.add(deviceId + " - " + deviceName);
                        }
                    }
                }
            }

            // Attendre que le processus se termine
            process.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return devicesList;
    }

    /**
     * Obtenir le nom de l'appareil via adb shell.
     * @param deviceId l'ID de l'appareil
     * @return le nom de l'appareil
     */
    private static String getDeviceName(String deviceId) {
        String deviceName = "Nom inconnu";

        try {
            // Exécuter la commande adb pour obtenir le nom de l'appareil
            Process process = Runtime.getRuntime().exec("adb -s " + deviceId + " shell settings get global device_name");

            // Lire la sortie du processus pour obtenir le nom de l'appareil
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line != null && !line.trim().isEmpty()) {
                    deviceName = line.trim();
                }
            }

            // Attendre que le processus se termine
            process.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return deviceName;
    }


    // APPELS =================================================================

    public String number = "";

    public String callTime = "00:00";
    private Thread callTimeUpdater; // Thread pour mettre à jour le temps de l'appel
    private boolean isUpdatingCallTime = false; // Pour contrôler l'exécution du thread

    /**
     * Permet de décrocher un appel entrant
     */
    public void pickUp() {
        String deviceId = getSelectedDeviceId();
        if (!deviceId.equals("Aucun appareil connecté")) {
            try {
                Process process = Runtime.getRuntime().exec("adb -s " + deviceId + " shell input keyevent KEYCODE_CALL");
                process.waitFor();
                this.onCall = true;
                startCallTimeUpdater(); // Démarrer la mise à jour du temps de l'appel
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Raccroche un appel en cours
     */
    public void HangUp() {
        String deviceId = getSelectedDeviceId();
        if (!deviceId.equals("Aucun appareil connecté")) {
            try {
                Process process = Runtime.getRuntime().exec("adb -s " + deviceId + " shell input keyevent KEYCODE_ENDCALL");
                process.waitFor();
                this.onCall = false;
                stopCallTimeUpdater(); // Arrêter la mise à jour du temps de l'appel
                callTime = "00:00";
                // On force la mise à jour de la variable
                UseVariableController.INSTANCE.requestVariablesUpdate();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Démarre un thread pour mettre à jour le temps de l'appel.
     */
    private void startCallTimeUpdater() {
        isUpdatingCallTime = true;
        callTimeUpdater = new Thread(() -> {
            int seconds = 0;
            while (isUpdatingCallTime) {
                try {
                    Thread.sleep(1000); // Attendre 1 seconde
                    seconds++;
                    int minutes = seconds / 60;
                    int secs = seconds % 60;
                    callTime = String.format("%02d:%02d", minutes, secs); // Mettre à jour le temps de l'appel
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        callTimeUpdater.start(); // Démarrer le thread
    }

    /**
     * Arrête le thread qui met à jour le temps de l'appel.
     */
    private void stopCallTimeUpdater() {
        isUpdatingCallTime = false;
        if (callTimeUpdater != null && callTimeUpdater.isAlive()) {
            callTimeUpdater.interrupt(); // Interrompre le thread si nécessaire
        }
    }

    /**
     * Récupère le numéro de téléphone de la personne qui essaie d'appeler
     * Si le numéro de téléphone est enregistré dans la liste des contacts,
     * on renvoie le nom et le numéro de téléphone du contact
     * sinon on renvoie le numéro de téléphone
     * @return le nom et le numéro de téléphone du contact ou le numéro de téléphone
     */
    public String getCallNumber() {
        if (!getContactByNumber(number).equals("Contact introuvable")) {
            return getContactByNumber(number);
        } else {
            return number;
        }
    }

    /**
     * Récupère le temps d'appel actuel
     * @return le temps d'appel sous forme de chaîne formatée
     */
    public String getCallTime() {
        return callTime;
    }

    /**
     * Appelle le numéro dans la zone de saisie
     */
    public void callTheNumberInTheInputBox() {
        this.number = WritingStateController.INSTANCE.currentTextProperty().get();
        String number = this.number;
        UseVariableController.INSTANCE.requestVariablesUpdate(); // Met à jour les variables

        // Vérifie si le numéro de téléphone est valide
        if (number.length() == 10 && number.matches("\\d+")) { // Matches seulement les chiffres
            String deviceId = getSelectedDeviceId();
            if (!deviceId.equals("Aucun appareil connecté")) {
                try {
                    Process process = Runtime.getRuntime().exec("adb -s " + deviceId + " shell am start -a android.intent.action.CALL -d tel:" + number);
                    process.waitFor();
                    this.onCall = true;
                    startCallTimeUpdater(); // Démarrer la mise à jour du temps de l'appel
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Si le numéro de téléphone n'est pas valide, on affecte la variable string "number" à "Numéro invalide"
            this.number = "Numéro invalide, appel annulé, veuillez raccrocher";
        }
    }

    /**
     * Appelle le numéro du nom dans la zone de saisie sans prendre en compte la majuscule de la première lettre
     * Si le nom n'est pas dans la liste des contacts, on renvoie "Le contact n'est pas dans la liste des contacts"
     */
    public void callTheNameInTheInputBox() {
        String name = WritingStateController.INSTANCE.currentTextProperty().get();
        String contacts = this.contactListString;

        // Si la liste n'est pas vide
        if (!contacts.isEmpty()) {
            // On récupère les contacts un par un
            String[] contactsArray = contacts.split("\n");
            for (String contact : contactsArray) {
                // On récupère le nom et le numéro de téléphone du contact
                String[] parts = contact.split(",");
                String name2 = parts[0].split(":")[1];
                String number = parts[1].split(":")[1];

                // On vérifie si le nom du contact est le même que celui passé en paramètre
                if (name2.equalsIgnoreCase(name)) {
                    // Si oui, on appelle le numéro de téléphone du contact
                    this.number = number;
                    UseVariableController.INSTANCE.requestVariablesUpdate(); // Met à jour les variables

                    String deviceId = getSelectedDeviceId();
                    if (!deviceId.equals("Aucun appareil connecté")) {
                        try {
                            Process process = Runtime.getRuntime().exec("adb -s " + deviceId + " shell am start -a android.intent.action.CALL -d tel:" + number);
                            process.waitFor();
                            this.onCall = true;
                            startCallTimeUpdater(); // Démarrer la mise à jour du temps de l'appel
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }
            }
        }

        // Si le nom n'est pas dans la liste des contacts, on renvoie "Le contact n'existe pas, appel annulé, veuillez raccrocher"
        this.number = "Le contact n'existe pas, appel annulé, veuillez raccrocher";
        UseVariableController.INSTANCE.requestVariablesUpdate(); // Met à jour les variables
    }

    /**
     * Appelle le contact de la variable contact (contient seulement le nom du contact)
     */
    public void callTheContact() {
        String contact = this.contact;
        String contacts = this.contactListString;

        // Si la liste n'est pas vide
        if (!contacts.isEmpty()) {
            // On récupère les contacts un par un
            String[] contactsArray = contacts.split("\n");
            for (String contact2 : contactsArray) {
                // On récupère le nom et le numéro de téléphone du contact
                String[] parts = contact2.split(",");
                String name = parts[0].split(":")[1];
                String number = parts[1].split(":")[1];

                // On vérifie si le nom du contact est le même que celui passé en paramètre
                if (name.equals(contact)) {
                    // Si oui, on appelle le numéro de téléphone du contact
                    this.number = number;
                    UseVariableController.INSTANCE.requestVariablesUpdate(); // Met à jour les variables

                    String deviceId = getSelectedDeviceId();
                    if (!deviceId.equals("Aucun appareil connecté")) {
                        try {
                            Process process = Runtime.getRuntime().exec("adb -s " + deviceId + " shell am start -a android.intent.action.CALL -d tel:" + number);
                            process.waitFor();
                            this.onCall = true;
                            startCallTimeUpdater(); // Démarrer la mise à jour du temps de l'appel
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }
            }
        }

        // Si le nom n'est pas dans la liste des contacts, on renvoie "Le contact n'existe pas, appel annulé, veuillez raccrocher"
        this.number = "Le contact n'existe pas, appel annulé, veuillez raccrocher";
        UseVariableController.INSTANCE.requestVariablesUpdate(); // Met à jour les variables
    }

    /**
     * Exécute une commande ADB pour une opération donnée
     * @param deviceId L'ID de l'appareil
     * @param command La commande ADB à exécuter
     */
    private void executeAdbCommand(String deviceId, String command) {
        if (!deviceId.equals("Aucun appareil connecté")) {
            try {
                Process process = Runtime.getRuntime().exec("adb -s " + deviceId + " shell " + command);
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Monte le volume audio du téléphone
     */
    public void VolumeUp() {
        String deviceId = getSelectedDeviceId();
        executeAdbCommand(deviceId, "input keyevent KEYCODE_VOLUME_UP");
    }

    /**
     * Baisse le volume audio du téléphone
     */
    public void VolumeDown() {
        System.out.println("VolumeDown");
        String deviceId = getSelectedDeviceId();
        executeAdbCommand(deviceId, "input keyevent KEYCODE_VOLUME_DOWN");
    }

    // CONTACTS ===============================================================

    // Liste des contacts du téléphone (nom et numéro)
    public String contactListString = listContactsString();

    // Lettres utilisées pour la recherche de contacts
    public String letters = "";

    // Dernières lettres utilisées pour la recherche de contacts
    public String LastLetters = "";

    // Liste des contacts du téléphone (nom et numéro) dont le nom commence par les lettres passées en paramètre
    public String listContactsByFirstLetter = "";

    // Liste des contacts du téléphone (nom et numéro)
    private ArrayList<String> contactsList = new ArrayList<>();

    // Vrai si la liste des contacts a déjà été chargée, faux sinon
    public boolean contactsListIsLoaded = false;

    // ID de l'appareil connecté lors de la dernière vérification
    private String lastDeviceId = "";

    // Numéro de la ligne du contact dans la liste des contacts
    public int numeroLigneContact = 0;

    // Nom du contact
    public String contact = "";

    /**
     * Renvoie le contact à la ligne spécifiée par numeroLigneContact.
     * Si les lettres ont changé, réinitialise le numéro de la ligne du contact.
     * @return le contact formaté avec son numéro de ligne.
     */
    public String getContact() {
        if (!this.letters.equals(this.LastLetters) && !this.letters.isEmpty()) {
            this.numeroLigneContact = 0;
        }
        setContact(numeroLigneContact);
        return (numeroLigneContact + 1) + ". " + this.contact;
    }

    /**
     * Renvoie le contact précédent ou suivant en fonction de l'offset.
     * @param offset l'offset par rapport au contact actuel (-1 pour précédent, +1 pour suivant).
     * @return le contact formaté avec son numéro de ligne.
     */
    private String getAdjacentContact(int offset) {
        String contacts = this.listContactsByFirstLetter;
        if (!contacts.isEmpty()) {
            String[] contactsArray = contacts.split("\n");
            int numContacts = contactsArray.length;
            int newContactIndex = (this.numeroLigneContact + offset + numContacts) % numContacts;

            String contact = contactsArray[newContactIndex];
            String[] parts = contact.split("\\|");
            String name = parts[0].trim();

            return (newContactIndex + 1) + ". " + name;
        } else {
            return "Contact introuvable";
        }
    }

    /**
     * Renvoie le contact précédent contact.
     * @return le contact précédent formaté avec son numéro de ligne.
     */
    public String getContactPrecedent() {
        return getAdjacentContact(-1);
    }

    /**
     * Renvoie le contact suivant contact.
     * @return le contact suivant formaté avec son numéro de ligne.
     */
    public String getContactSuivant() {
        return getAdjacentContact(1);
    }

    /**
     * Affecte le nom du contact à la ligne "ligne" dans la liste des contacts à la variable contact.
     * @param ligne la ligne du contact dans la liste des contacts (commence à 0).
     */
    public void setContact(int ligne) {
        String contacts = this.listContactsByFirstLetter;
        if (!contacts.isEmpty()) {
            String[] contactsArray = contacts.split("\n");
            String contact = contactsArray[ligne];

            String[] parts = contact.split("\\|");
            String name = parts[0].trim();

            this.contact = name;
        } else {
            this.contact = "Contact introuvable";
        }
    }

    /**
     * Augmente de 1 le numéro de la ligne du contact dans la liste des contacts.
     */
    public void incrementContact() {
        updateContactIndex(1);
    }

    /**
     * Diminue de 1 le numéro de la ligne du contact dans la liste des contacts.
     */
    public void decrementContact() {
        updateContactIndex(-1);
    }

    /**
     * Met à jour numeroLigneContact en ajoutant l'offset spécifié.
     * @param offset l'offset à ajouter à numeroLigneContact (peut être positif ou négatif).
     */
    private void updateContactIndex(int offset) {
        int numContacts = listContactsByFirstLetter.split("\n").length;
        this.numeroLigneContact = (this.numeroLigneContact + offset + numContacts) % numContacts;
        this.LastLetters = this.letters;
    }

    /**
     * Renvoie un contact au format "Nom | Numéro" à partir du numéro de téléphone.
     * @param number le numéro de téléphone.
     * @return le nom et le numéro de téléphone du contact, ou "Contact introuvable".
     */
    public String getContactByNumber(String number) {
        String contacts = this.contactListString;
        if (!contacts.isEmpty()) {
            for (String contact : contacts.split("\n")) {
                String[] parts = contact.split(",");
                String name = parts[0].split(":")[1].trim();
                String number2 = parts[1].split(":")[1].trim();

                if (number2.equals(number)) {
                    return name + " | " + number2;
                }
            }
        }
        return "Contact introuvable";
    }

    /**
     * Affecte les initiales des contacts contenues dans la zone de saisie à la variable letters.
     */
    public void affectLetters() {
        String letters = WritingStateController.INSTANCE.currentTextProperty().get();
        letters = letters.replaceAll("-", "");
        this.letters = letters;
        UseVariableController.INSTANCE.requestVariablesUpdate();
    }

    /**
     * Récupère ou recharge la liste des contacts à partir de l'appareil connecté.
     * Cette méthode doit être appelée pour mettre à jour les contacts lorsque l'appareil est connecté
     * ou lorsque le nom de l'appareil connecté change.
     * @return ArrayList contenant les contacts du téléphone (nom et numéro).
     */
    public ArrayList<String> listContacts() {
        String currentDeviceId = getSelectedDeviceId();
        if (!contactsListIsLoaded || !currentDeviceId.equals(lastDeviceId)) {
            System.out.println("Chargement des contacts...");
            loadContacts(currentDeviceId);
        }
        return contactsList;
    }

    /**
     * Charge la liste des contacts à partir de l'appareil connecté.
     * Cette méthode doit être appelée lorsque l'appareil est connecté ou lorsqu'il y a une mise à jour nécessaire des contacts.
     * @param deviceId l'ID de l'appareil connecté.
     */
    private void loadContacts(String deviceId) {
        if (!deviceId.equals("Aucun appareil connecté")) {
            System.out.println("Appareil connecté : " + deviceId);
            try {
                Process process = Runtime.getRuntime().exec("adb -s " + deviceId + " shell content query --uri content://contacts/phones/ --projection display_name:number");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                Pattern namePattern = Pattern.compile("display_name=(.*?),");
                Pattern numberPattern = Pattern.compile("number=(.*?)$");
                HashSet<String> uniqueContacts = new HashSet<>();
                ArrayList<String> newContactsList = new ArrayList<>();

                while ((line = reader.readLine()) != null) {
                    System.out.println("Ligne lue : " + line);
                    Matcher nameMatcher = namePattern.matcher(line);
                    Matcher numberMatcher = numberPattern.matcher(line);

                    if (nameMatcher.find() && numberMatcher.find()) {
                        String name = "name:" + nameMatcher.group(1).trim();
                        String originalNumber = numberMatcher.group(1).trim();
                        String formattedNumber = formatPhoneNumber(originalNumber);
                        String contact = name + ", number:" + formattedNumber;

                        if (uniqueContacts.add(contact)) {
                            newContactsList.add(contact);
                        }
                    }
                }
                Collections.sort(newContactsList, String.CASE_INSENSITIVE_ORDER);

                process.waitFor();
                reader.close();

                this.contactsList = newContactsList;
                contactsListIsLoaded = true;
                lastDeviceId = deviceId;

                System.out.println("Contacts chargés avec succès.");
                System.out.println("Nombre de contacts : " + contactsList.size());
                for (String contact : contactsList) {
                    System.out.println(contact);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            // Aucun appareil connecté, réinitialiser la liste des contacts
            System.out.println("Aucun appareil connecté. Réinitialisation de la liste des contacts.");
            this.contactsList = new ArrayList<>();
            contactsListIsLoaded = false;
            lastDeviceId = "";
        }
    }

    /**
     * Crée une chaîne de caractères contenant les contacts du téléphone (nom et numéro)
     * et l'affecte à la variable contactListString.
     * @return chaîne de caractères contenant les contacts du téléphone (nom et numéro).
     */
    public String listContactsString() {
        StringBuilder contactListString = new StringBuilder();
        ArrayList<String> contactsList = listContacts();
        for (String contact : contactsList) {
            contactListString.append(contact).append("\n");
        }
        this.contactListString = contactListString.toString().trim();
        return this.contactListString;
    }

    /**
     * Prend en paramètre une lettre ou une chaîne de lettre et renvoie le nom et le numéro de téléphone des contacts
     * dont le nom commence par une des lettres passées en paramètre.
     * @return le nom et le numéro de téléphone des contacts dont le nom commence par une des lettres passées en paramètre.
     */
    public String getContactsByFirstLetter() {
        String contacts = this.contactListString;
        StringBuilder result = new StringBuilder();

        if (!contacts.isEmpty()) {
            for (String contact : contacts.split("\n")) {
                String[] parts = contact.split(",");
                String name = parts[0].split(":")[1].trim();
                String number = parts[1].split(":")[1].trim();
                String firstLetter = name.substring(0, 1).toLowerCase();

                if (this.letters.toLowerCase().contains(firstLetter)) {
                    result.append(name).append(" | ").append(number).append("\n");
                }
            }
        }

        this.listContactsByFirstLetter = result.toString().trim();
        return this.listContactsByFirstLetter;
    }

    /**
     * Renvoie le nombre de contacts dont le nom commence par une des lettres passées en paramètre
     * @return le nombre de contacts
     */
    public String getNbContactsByFirstLetter() {
        String contactAvecOuSansS = "contacts";
        if (getContactsByFirstLetter().split("\n").length == 1) {
            contactAvecOuSansS = "contact";
        }
        return getContactsByFirstLetter().split("\n").length + " " + contactAvecOuSansS;
    }

    /**
     * Formate le numéro de téléphone pour qu'il soit au format "XXXXXXXXXX"
     * @param originalNumber le numéro de téléphone à formater
     * @return le numéro de téléphone formaté
     */
    private static String formatPhoneNumber(String originalNumber) {
        // Supprimer les caractères non numériques (espaces, +, etc.)
        String cleanedNumber = originalNumber.replaceAll("[^0-9]", "");

        // Si le numéro commence par "+33", remplacer "+33" par "0"
        if (cleanedNumber.startsWith("33") && cleanedNumber.length() >= 11) {
            return "0" + cleanedNumber.substring(2);
        }

        // Si le numéro commence par "0", a déjà le format "XXXXXXXXXX", laisser tel quel
        if (cleanedNumber.startsWith("0") && cleanedNumber.length() == 10) {
            return cleanedNumber;
        }

        // Si le numéro ne correspond à aucun des cas précédents, renvoyer le numéro original
        return originalNumber;
    }

    public String contactRecherche = "Aucun contact trouvé";

    /**
     * Recherche parmi les contacts le contact le plus proche de celui dans la zone
     * de saisie (si la zone de saisie est vide, renvoie "Commencez à taper pour rechercher un contact")
     * @return le nom du contact le plus proche
     */
    public String rechercheContact() {
        String contacts = this.contactListString;
        String contactRecherche = "Aucun contact trouvé";

        // Récupère la saisie de l'utilisateur et la convertit en minuscules pour une recherche insensible à la casse
        String saisieUtilisateur = WritingStateController.INSTANCE.currentTextProperty().get().trim().toLowerCase();

        // Si la zone de saisie est vide
        if (saisieUtilisateur.isEmpty()) {
            return "Commencez à taper pour rechercher un contact";
        }

        if (!contacts.isEmpty()) {
            List<String> matches = new ArrayList<>(); // Liste pour stocker les correspondances exactes (conservant la casse originale)
            List<String> partialMatches = new ArrayList<>(); // Liste pour stocker les correspondances partielles (conservant la casse originale)

            // Parcourir tous les contacts
            for (String contact : contacts.split("\n")) {
                String[] parts = contact.split(",");
                String name = parts[0].split(":")[1].trim(); // Conserve la casse originale
                String nameLowerCase = name.toLowerCase(); // Convertit en minuscules pour la comparaison

                // Vérifier si le nom commence par les lettres tapées
                if (nameLowerCase.startsWith(saisieUtilisateur)) {
                    matches.add(name); // Ajouter le nom original à la liste des correspondances exactes
                }
                // Vérifier si le nom contient les lettres tapées (recherche partielle)
                else if (nameLowerCase.contains(saisieUtilisateur)) {
                    partialMatches.add(name); // Ajouter le nom original à la liste des correspondances partielles
                }
            }

            // Prioriser les correspondances exactes
            if (!matches.isEmpty()) {
                contactRecherche = matches.get(0); // Prendre la première correspondance exacte
            } else if (!partialMatches.isEmpty()) {
                contactRecherche = partialMatches.get(0); // Sinon, prendre la première correspondance partielle
            }
        }

        return contactRecherche; // Retourner le contact trouvé ou "Aucun contact trouvé"
    }

    /**
     * Met à jour la variable contactRecherche
     */
    public void updateContactRecherche() {
        UseVariableController.INSTANCE.requestVariablesUpdate();
    }

    /**
     * Renvoie le nom du contact le plus proche de celui dans la zone de saisie
     * @return le nom du contact le plus proche
     */
    public String getContactRecherche() {
        contactRecherche = rechercheContact();
        return contactRecherche;
    }

    /**
     * Appelle le contact le plus proche du nom dans la zone de saisie
     */
    public void callContactRecherche() {
        String contacts = this.contactListString;
        String contactRecherche = this.contactRecherche;

        if (!contacts.isEmpty()) {
            for (String contact : contacts.split("\n")) {
                String[] parts = contact.split(",");
                String name = parts[0].split(":")[1].trim();
                String number = parts[1].split(":")[1].trim();

                if (name.equals(contactRecherche)) {
                    this.number = number;
                    UseVariableController.INSTANCE.requestVariablesUpdate(); // Met à jour les variables

                    String deviceId = getSelectedDeviceId();
                    if (!deviceId.equals("Aucun appareil connecté")) {
                        try {
                            Process process = Runtime.getRuntime().exec("adb -s " + deviceId + " shell am start -a android.intent.action.CALL -d tel:" + number);
                            process.waitFor();
                            this.onCall = true;
                            startCallTimeUpdater(); // Démarrer la mise à jour du temps de l'appel
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }
            }
        }

        // Si le nom n'est pas dans la liste des contacts, on renvoie "Le contact n'existe pas, appel annulé, veuillez raccrocher"
        this.number = "Le contact n'existe pas, appel annulé, veuillez raccrocher";
        UseVariableController.INSTANCE.requestVariablesUpdate();
    }



    // APPEL ENTRANT ==========================================================

    public int callState = 1;

    public boolean onCall = false;

    public boolean numeroTrouve = false;

    // CALLBACKS ===============================================================

    private Runnable callEnterCallback;
    private Runnable callEndedCallback;

    /**
     * Vérifie si un appel est en cours
     * Lance l'événement correspondant
     */
    public void incomingCall() {
        this.callState = getCallStatus();

        if (this.callState == 1 && !numeroTrouve) { // Si quelqu'un essaie d'appeler
            this.number = getCallerNumber();
            numeroTrouve = true;
            this.onCall = true;
            UseVariableController.INSTANCE.requestVariablesUpdate();
            callEnterCallback.run();
        }

        if (this.callState == 0 && this.onCall && callTimeSuperieurA3Secondes()) { // Si l'appel est terminé
            this.onCall = false;
            this.callTime = "00:00";
            numeroTrouve = false;
            UseVariableController.INSTANCE.requestVariablesUpdate();
            callEndedCallback.run();
        }
    }

    /**
     * Vérifie si l'appel est de plus de 3 secondes
     * @return true si l'appel est de plus de 3 secondes, false sinon
     */
    public boolean callTimeSuperieurA3Secondes() {
        return Integer.parseInt(callTime.split(":")[0]) * 60 + Integer.parseInt(callTime.split(":")[1]) >= 3;
    }

    /**
     * Donne le statut de l'appel en cours
     * 0 : aucun appel en cours
     * 1 : quelqu'un essaie d'appeler
     * 2 : appel en cours
     * @return le statut de l'appel en cours
     */
    public static int getCallStatus() {
        int callStatus = 0; // Par défaut, pas d'appel en cours

        try {
            String adbCommand = "adb -s " + getSelectedDeviceId() + " shell dumpsys telephony.registry";
            Process process = Runtime.getRuntime().exec(adbCommand);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            Pattern callStatePattern = Pattern.compile("mCallState=(\\d+)");

            boolean statusFound = false;

            while (!statusFound && (line = reader.readLine()) != null) {
                Matcher matcher = callStatePattern.matcher(line);
                if (matcher.find()) {
                    callStatus = Integer.parseInt(matcher.group(1));
                    statusFound = true;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return callStatus;
    }

    /**
     * Donne le numéro de téléphone de l'appelant
     * @return le numéro de téléphone de l'appelant, ou null si l'information n'est pas disponible
     */
    public static String getCallerNumber() {
        String callerNumber = null;

        try {
            String adbCommand = "adb shell dumpsys telephony.registry | findstr mCallIncomingNumber";
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", adbCommand);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String callerNumberLine = reader.readLine();
            if (callerNumberLine != null && callerNumberLine.contains("=")) {
                callerNumber = callerNumberLine.substring(callerNumberLine.indexOf("=") + 1).trim();
            }

            process.waitFor();
            reader.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        //si la personne à un numéro en +33, on le remplace par un 0
        if (callerNumber.startsWith("+33")) {
            callerNumber = callerNumber.replace("+33", "0");
        }

        return callerNumber;
    }

    /**
     * Affecte à callEnterCallback le callback passé en paramètre
     * @param callback le callback
     */
    public void addCallEnterCallback(final Runnable callback) {
        this.callEnterCallback = callback;
    }

    /**
     * Affecte à callEndedCallback le callback passé en paramètre
     * @param callback le callback
     */
    public void addCallEndedCallback(final Runnable callback) {
        // mettre à jour la variable de temps d'appel
        this.callEndedCallback = () -> {
            this.onCall = false;
            stopCallTimeUpdater();
            this.callTime = "00:00";
            UseVariableController.INSTANCE.requestVariablesUpdate();
            callback.run();
        };
    }
}
