package org.lifecompanion.phonecontrolapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * JSONProcessingService watches a specific directory for new JSON files and processes them.
 */
public class JSONProcessingService extends Service {

    private static final String TAG = "JSONProcessingService";
    private static final String INPUT_DIR = "/data/local/tmp/lifecompanion/phonecontrol/input"; // Directory to watch

    private FileObserver fileObserver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "JSONProcessingService created");

        // Create a FileObserver to monitor changes in the directory
        fileObserver = new FileObserver(INPUT_DIR, FileObserver.CREATE) {
            @Override
            public void onEvent(int event, String fileName) {
                if (fileName != null) {
                    File file = new File(INPUT_DIR, fileName);
                    processFile(file);
                }
            }
        };

        // Start watching the directory
        fileObserver.startWatching();
        Log.d(TAG, "FileObserver started watching: " + INPUT_DIR);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "JSONProcessingService started");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (fileObserver != null) {
            fileObserver.stopWatching();
        }

        Log.d(TAG, "JSONProcessingService destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Processes a JSON file by reading its content and delegating the action to the appropriate service.
     *
     * @param file The JSON file to process.
     */
    private void processFile(File file) {
        Log.d(TAG, "Processing file: " + file.getName());

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[(int) file.length()];
            fis.read(buffer);
            String content = new String(buffer);

            // Parse the JSON content
            JSONObject json = new JSONObject(content);
            String type = json.optString("type");
            String subtype = json.optString("subtype");

            // Delegate the action based on the type
            switch (type) {
                case "call":
                    handleCallActions(json);

                    break;
                case "sms":
                    handleSmsActions(json);

                    break;
                case "contacts":
                    handleContactActions(json);

                    break;
                case "system":
                    handleSystemActions(json);

                    break;
                default:
                    Log.w(TAG, "Unknown type in JSON: " + type);

                    break;
            }

            // Delete the file after processing
            if (file.delete()) {
                Log.d(TAG, "File deleted: " + file.getName());
            } else {
                Log.w(TAG, "Failed to delete file: " + file.getName());
            }

        } catch (IOException e) {
            Log.e(TAG, "Error reading file: " + file.getName(), e);
        } catch (Exception e) {
            Log.e(TAG, "Error processing JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Handles call-related actions based on the JSON data.
     */
    private void handleCallActions(JSONObject json) {
        String subtype = json.optString("subtype");
        JSONObject data = json.optJSONObject("data");
        Intent intent = new Intent(this, CallService.class);

        switch (subtype) {
            case "make_call":
                intent.putExtra("phoneNumber", data.optString("phone_number"));
                intent.putExtra("speaker", true);

                break;
            case "hang_up":
                intent.putExtra("stop", true);

                break;
            case "call_messagerie":
                intent.putExtra("phoneNumber", "voicemail");

                break;
            default:
                Log.w(TAG, "Unknown call subtype: " + subtype);

                return;
        }

        startService(intent);
    }

    /**
     * Handles SMS-related actions based on the JSON data.
     */
    private void handleSmsActions(JSONObject json) {
        String subtype = json.optString("subtype");
        JSONObject data = json.optJSONObject("data");
        Intent intent = new Intent(this, SMSSenderService.class);

        switch (subtype) {
            case "send_sms":
                intent.putExtra("phoneNumber", data.optString("recipient"));
                intent.putExtra("message", data.optString("message"));

                break;
            default:
                Log.w(TAG, "Unknown SMS subtype: " + subtype);

                return;
        }

        startService(intent);
    }

    /**
     * Handles contact-related actions based on the JSON data.
     */
    private void handleContactActions(JSONObject json) {
        // Implementation for handling contacts (e.g., fetching contacts) goes here.
        Log.d(TAG, "Contact action: " + json.toString());
    }

    /**
     * Handles system-related actions based on the JSON data.
     */
    private void handleSystemActions(JSONObject json) {
        // Implementation for handling system actions (e.g., volume control) goes here.
        Log.d(TAG, "System action: " + json.toString());
    }
}
