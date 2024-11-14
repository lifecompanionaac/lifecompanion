import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cette classe regroupe les fonctions utiles pour le développement de l'extension phonecontrol de LifeCompanion
 */
public class connexion {

    public static void main(String[] args) throws IOException, InterruptedException {
        // Lancer le serveur ADB
        startADBServer();

        // On vérifie si un appareil est connecté et on affiche son ID
        String deviceID = checkConnectedDevice();
        System.out.println("Appareil connecté: " + deviceID + "\n");

        // On vérifie si un appareil est connecté et on affiche son nom
        String deviceName = checkConnectedDeviceName();
        System.out.println("Nom de l'appareil: " + deviceName + "\n");

        // ===== LES APPELS =====
        // Appeler un numéro
        // call("0123456789");

        // Décrocher un appel
        // acceptCall();

        // Raccrocher un appel ou refuser un appel entrant
        // hangUp();

        // ===== LES SMS =====
        // Envoyer un SMS
        // sms("0123456789", "Test"); // mais prepare le SMS, il faut appuyer sur envoyer

        // ===== LES CONTACTS =====
        // Créer une ArrayList contenant les contacts du téléphone (nom et numéro)
        ArrayList<String> contacts = listContacts();

        // Affiche la liste des contacts
        System.out.println(contacts);

        // Sauvegarde les contacts dans un fichier texte
        writeContactsToFile(contacts, "data/contacts.txt");

        // Donne le nom à partir du numéro de téléphone
        //System.out.println(findContactByPhoneNumber(contacts, "0123456789"));

        // Reformate le numéro de téléphone pour qu'il soit au format "XXXXXXXXXX"
        //System.out.println(formatPhoneNumber("+33 1 23 45 67 89"));

        // ===== LE STATUT DE L'APPEL =====
        // Donne le statut de l'appel en cours
        // System.out.println(getCallStatus());

        // Donne le numéro de téléphone de l'appelant
        // System.out.println(getCallerNumber());

        // Donne le statut toutes les secondes
        // Si il y a un appel entrant, affiche le numéro de téléphone de l'appelant et son nom si il est dans la liste des contacts
        //checkCallStatus(contacts);

        // On arrête le serveur ADB
        stopADBServer();
    }

    /**
     * Lance le serveur ADB
     */
    public static void startADBServer() {
        try {
            System.out.println("Serveur ADB lancé\n");
            Process process = Runtime.getRuntime().exec("adb start-server");
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Arrête le serveur ADB
     */
    public static void stopADBServer() {
        try {
            System.out.println("\nServeur ADB arrêté");
            Process process = Runtime.getRuntime().exec("adb kill-server");
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Vérifie si un appareil est connecté et renvoie son ID
     * @return l'ID de l'appareil connecté, ou "Aucun appareil connecté" si aucun appareil n'est connecté
     */
    public static String checkConnectedDevice() {
        try {
            Process process = Runtime.getRuntime().exec("adb devices");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.endsWith("device")) {
                    String[] parts = line.split("\t");
                    if (parts.length > 1) {
                        String deviceID = parts[0].trim();
                        process.waitFor();
                        reader.close();
                        return deviceID;
                    }
                }
            }

            process.waitFor();
            reader.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return "Aucun appareil connecté";
    }

    /**
     * Vérifie si un appareil est connecté et renvoie son nom
     * @return le nom de l'appareil connecté, ou "Aucun appareil connecté" si aucun appareil n'est connecté
     */
    public static String checkConnectedDeviceName() {
        try {
            Process process = Runtime.getRuntime().exec("adb shell settings get global device_name");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.equals("null")) {
                    process.waitFor();
                    reader.close();
                    return line;
                }
            }

            process.waitFor();
            reader.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return "Aucun appareil connecté";
    }

    /**
     * Permet de passer un appel
     * @param number le numéro de téléphone à appeler
     * @throws IOException si la commande n'a pas pu être exécutée
     */
    public static void call(String number) throws IOException {
        // On construit la commande
        String cmd = "adb shell am start -a android.intent.action.CALL -d tel:" + number;
        // Permet de lancer la commande cmd
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", cmd);

        builder.redirectErrorStream(true); // Pour afficher la sortie de la commande dans le terminal
        Process p = builder.start(); // On lance la commande
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream())); // On récupère la sortie de la commande
        String line; // On stocke la sortie de la commande
        // On affiche la sortie de la commande
        while ((line = r.readLine()) != null) {
            System.out.println(line);
        }

    }

    /**
     * Permet d'envoyer un SMS
     * @param number le numéro de téléphone du destinataire
     * @param message le message à envoyer
     * @throws IOException si la commande n'a pas pu être exécutée
     */
    public static void sms(String number, String message) throws IOException {
        // Prépare le SMS en utilisant la commande ADB
        String prepareCommand = "adb shell am start -a android.intent.action.SENDTO -d sms:" + number
                + " --es sms_body \"" + message + "\"";
        try {
            Process prepareProcess = Runtime.getRuntime().exec(prepareCommand);
            prepareProcess.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet d'accepter un appel entrant, fonctionne uniquement si l'appel est en cours
     * @throws IOException si la commande n'a pas pu être exécutée
     */
    public static void acceptCall() throws IOException {
        // On construit la commande
        String cmd = "adb shell input keyevent KEYCODE_CALL";
        // Permet de lancer la commande cmd
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", cmd);

        builder.redirectErrorStream(true); // Pour afficher la sortie de la commande dans le terminal
        Process p = builder.start(); // On lance la commande
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream())); // On récupère la sortie de la commande
        String line; // On stocke la sortie de la commande
        // On affiche la sortie de la commande
        while ((line = r.readLine()) != null) {
            System.out.println(line);
        }

    }

    /**
     * Permet de raccrocher un appel en cours ou de refuser un appel entrant
     * @throws IOException si la commande n'a pas pu être exécutée
     */
    public static void hangUp() throws IOException {
        // On construit la commande
        String cmd = "adb shell input keyevent KEYCODE_ENDCALL";
        // Permet de lancer la commande cmd
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", cmd);

        builder.redirectErrorStream(true); // Pour afficher la sortie de la commande dans le terminal
        Process p = builder.start(); // On lance la commande
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream())); // On récupère la sortie de la commande
        String line; // On stocke la sortie de la commande
        // On affiche la sortie de la commande
        while ((line = r.readLine()) != null) {
            System.out.println(line);
        }
    }

    /**
     * Créer une ArrayList contenant les contacts du téléphone (nom et numéro)
     * @throws IOException si la commande n'a pas pu être exécutée
     * @return ArrayList contenant les contacts du téléphone (nom et numéro)
     */
    public static ArrayList<String> listContacts() throws IOException {
        ArrayList<String> contactsList = new ArrayList<>();

        // Utilisez la commande adb pour extraire la liste des contacts
        String cmd = "adb shell content query --uri content://contacts/phones/ --projection display_name:number";
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", cmd);

        builder.redirectErrorStream(true);
        Process process = builder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        // Utilisez des expressions régulières pour extraire le nom et le numéro
        Pattern namePattern = Pattern.compile("display_name=(.*?),");
        Pattern numberPattern = Pattern.compile("number=(.*?)$");

        while ((line = reader.readLine()) != null) {
            Matcher nameMatcher = namePattern.matcher(line);
            Matcher numberMatcher = numberPattern.matcher(line);

            if (nameMatcher.find() && numberMatcher.find()) {
                String name = "name:" + nameMatcher.group(1);
                String originalNumber = numberMatcher.group(1);
                String formattedNumber = formatPhoneNumber(originalNumber); // Format the phone number
                contactsList.add(name + ", number:" + formattedNumber);
            }
        }

        return contactsList;
    }

    /**
     * Sauvegarde les contacts dans un fichier texte
     * @param contactsList la liste des contacts
     * @param filePath le chemin du fichier
     */
    public static void writeContactsToFile(ArrayList<String> contactsList, String filePath) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

            for (String contact : contactsList) {
                writer.write(contact);
                writer.newLine(); // Ajouter une nouvelle ligne entre les contacts
            }

            writer.close();
            System.out.println("Contacts sauvegardés dans : " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la sauvegarde des contacts dans le fichier.");
        }
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

    /**
     * Donne le nom à partir du numéro de téléphone
     * @param contactsList la liste des contacts
     * @param phoneNumber le numéro de téléphone
     * @return le nom du contact
     */
    public static String findContactByPhoneNumber(ArrayList<String> contactsList, String phoneNumber) {
        for (String contact : contactsList) {
            // Sépare le nom et le numéro du contact
            String[] parts = contact.split(", ");
            for (String part : parts) {
                if (part.startsWith("number:")) {
                    // Extrait le numéro de téléphone
                    String contactNumber = part.substring("number:".length());
                    if (contactNumber.equals(phoneNumber)) {
                        // Si le numéro correspond, renvoie le nom
                        for (String part2 : parts) {
                            if (part2.startsWith("name:")) {
                                return part2.substring("name:".length());
                            }
                        }
                    }
                }
            }
        }
        // Si le numéro n'est pas trouvé, retourne null
        return null;
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
            String adbCommand = "adb shell dumpsys telephony.registry";
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

            // On affiche la signification du statut
            if (callStatus == 0) {
                System.out.println("Aucun appel en cours");
            } else if (callStatus == 1) {
                System.out.println("Quelqu'un essaie d'appeler");
            } else if (callStatus == 2) {
                System.out.println("Appel en cours");
            }

        } catch (IOException e) {
            e.printStackTrace();
            // En cas d'erreur, le statut reste à sa valeur par défaut (0)
        }

        return callStatus;
    }

    /**
     * Donne le numéro de téléphone de l'appelant
     * @return le numéro de téléphone de l'appelant, ou null si l'information n'est pas disponible
     * @throws IOException si la commande n'a pas pu être exécutée
     */
    public static String getCallerNumber() throws IOException {
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
     * Verifie si quelqu'un essaie d'appeller ou si un appel est en cours chaque seconde et affiche le numéro de téléphone de l'appelant et son nom si il est dans la liste des contacts
     * Chaques 60 secondes, demande à l'utilisateur s'il veut arrêter le programme
     * @param contacts la liste des contacts
     * @throws InterruptedException si le thread est interrompu
    */
    public static void checkCallStatus(ArrayList<String> contacts) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        boolean shouldStop = false;

        long lastCheckTime = System.currentTimeMillis();

        String callerNumber = null;
        String callerName = null;

        while (!shouldStop) {
            int callStatus = getCallStatus();

            if (callStatus == 1) {
                try {
                    callerNumber = getCallerNumber();
                    callerName = findContactByPhoneNumber(contacts, callerNumber);
                    System.out.println("Caller Number: " + callerNumber);
                    if (callerName != null) {
                        System.out.println("Caller Name: " + callerName);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (callStatus == 2) {
                System.out.println("Call Status: " + callStatus);
                System.out.println("Caller Number: " + callerNumber);
                if (callerName != null) {
                    System.out.println("Caller Name: " + callerName);
                }
            }

            // Attendre 1 seconde avant de vérifier à nouveau
            Thread.sleep(1000);

            // Vérifier si l'utilisateur veut arrêter le programme toutes les 60 secondes
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastCheckTime >= 60000) {
                lastCheckTime = currentTime;

                // Demander à l'utilisateur s'il veut arrêter le programme
                System.out.println("Voulez-vous arrêter le programme ? (oui/non)");
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("oui")) {
                    shouldStop = true;
                }
            }
        }

        scanner.close();
    }
}
