package org.apache.cordova.Plugin;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.webkit.ValueCallback;

import java.io.IOException;
import java.io.StringReader;

import org.apache.cordova.AndroidWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class LocalStoragePlugin extends CordovaPlugin {

  public static final String TAG = "LocalStorage";

  /**
   * Constructor.
   */
  public LocalStoragePlugin() {
  }


  private CallbackContext cbContext;

  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    // Device.uuid = getUuid();
  }

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action        The action to execute.
     * @param args          JSONArry of arguments for the plugin.
     * @param callbackId    The callback id used when calling back into JavaScript.
     * @return              A PluginResult object with a status and message.
     */
  @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {

      CordovaActivity activity = ((CordovaActivity)this.cordova.getActivity());
      AndroidWebView defaultWebView = (AndroidWebView) activity.makeWebView();

      cbContext = callbackContext;

        try {
          String jsForLocalStorage = "" +
          "var items = [];" +
          "for (var i = 0; i < localStorage.length; i++){" +
            "items.push({key: localStorage.key(i), value: localStorage.getItem( localStorage.key(i) )});"+
          "}";
          String jsCallback = "JSInterface.passBackValues(items);";

          if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
              // In KitKat+ you should use the evaluateJavascript method
              defaultWebView.evaluateJavascript(jsForLocalStorage, new ValueCallback<String>() {
                  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                  @Override
                  public void onReceiveValue(String s) {
                    JsonReader reader = null;
                      try {
                          reader = new JsonReader(new StringReader(s));

                          // Must set lenient to parse single values
                          reader.setLenient(true);

                          if(reader.peek() != JsonToken.NULL) {
                              if(reader.peek() == JsonToken.STRING) {
                                  String msg = reader.nextString();
                              }
                          }
                      } catch (IOException e) {
                          Log.e("TAG", "MainActivity: IOException", e);
                      } finally {
                          try {
                              reader.close();
                              cbContext.success("json");
                          } catch (IOException e) {
                              // NOOP
                          }
                      }
                  }
              });
          } else {
              /**
               * For pre-KitKat+ you should use loadUrl("javascript:<JS Code Here>");
               * To then call back to Java you would need to use addJavascriptInterface()
               * and have your JS call the interface
               **/
              JavaScriptInterface jsInterface = new JavaScriptInterface(activity);
              defaultWebView.addJavascriptInterface(jsInterface, "JSInterface");
              defaultWebView.loadUrl("javascript:" + jsForLocalStorage + jsCallback);
          }
        } catch (Exception e) {

        }
        return false;
    } //Closes execute

    public class JavaScriptInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        public void passBackValues(String items)
        {
          Log.e(TAG, "CordovaWebView: We got some localStorage items " + items);
        }
    }
}
