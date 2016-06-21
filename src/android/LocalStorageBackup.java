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
     * @param action            The action to execute.
     * @param args              JSONArray of arguments for the plugin.
     * @param callbackContext   The callback id used when calling back into JavaScript.
     * @return                  A PluginResult object with a status and message.
     */
  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
      String id, localStorageString;
      boolean result;
      SharedPreferences prefs = cordova.getActivity().getSharedPreferences(NAME, 0);

      if ("save".equals(action)) {
          id = args.getString(0);
          localStorageString = args.getString(1);
          result = prefs.edit().putString(id, localStorageString).commit();

          if (result) {
              callbackContext.success();
          } else {
              callbackContext.error("There was an error saving localStorage to persistent storage.");
          }

          return true;
      }

      if ("load".equals(action)) {
          id = args.getString(0);
          localStorageString = prefs.getString(id, "{}");
          callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, localStorageString));

          return true;
      }

      if ("clear".equals(action)) {
          id = args.getString(0);
          result = prefs.edit().remove(id).commit();

          if (result) {
              callbackContext.success();
          } else {
              callbackContext.error("There was an error clearing localStorage from persistent storage.");
          }

          return true;
      }

      return false;
    }
}
