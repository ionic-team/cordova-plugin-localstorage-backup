package io.ionic.localstoragebackup;

import android.content.SharedPreferences;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * This class saves or loads localStorage to or from SharedPreferences.
 */
public class LocalStorageBackup extends CordovaPlugin {

    public static final String NAME = "LocalStorageBackup";

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action          The action to execute.
     * @param args            JSONArray of arguments for the plugin.
     * @param callbackContext The callback id used when calling back into JavaScript.
     * @return A PluginResult object with a status and message.
     */
    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

        final String id;
        final SharedPreferences prefs = cordova.getActivity().getSharedPreferences(NAME, 0);

        if ("save".equals(action)) {
            id = args.getString(0);
            final String localStorageString = args.getString(1);
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    boolean result = prefs.edit().putString(id, localStorageString).commit();

                    if (result) {
                        callbackContext.success();
                    } else {
                        callbackContext.error("There was an error saving localStorage to persistent storage.");
                    }
                }
            });

            return true;
        }

        if ("load".equals(action)) {
            id = args.getString(0);
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    String savedLocalStorageString = prefs.getString(id, "{}");
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, savedLocalStorageString));
                }
            });

            return true;
        }

        if ("clear".equals(action)) {
            id = args.getString(0);
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    boolean result = prefs.edit().remove(id).commit();

                    if (result) {
                        callbackContext.success();
                    } else {
                        callbackContext.error("There was an error clearing localStorage from persistent storage.");
                    }
                }
            });

            return true;
        }

        return false;
    }
}
