package org.apache.cordova.Plugin;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.CordovaActivity;

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
    Device.uuid = getUuid();
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

      CordovaActivity activity = ((CordovaActivity)context.getApplicationContext()).getCurrentActivity()
      CordovaWebView defaultWebView = (CordovaWebView) activity.makeWebView();

      cbContext = callbackContext;

        try {
          String jsForLocalStorage = '
          var items = [];
          for (var i = 0; i < localStorage.length; i++){
            items.push({key: localStorage.key(i), value: localStorage.getItem( localStorage.key(i) )});
          }';
          String jsCallback = 'JSInterface.passBackValues(items);';

          if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
              // In KitKat+ you should use the evaluateJavascript method
              defaultWebView.evaluateJavascript(jsForLocalStorage, new ValueCallback<String>() {
                  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                  @Override
                  public void onReceiveValue(String s) {
                      try {
                          JsonReader reader = new JsonReader(new StringReader(s));

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
                              cbContext.
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
              defaultWebView.loadUrl("javascript:"+javascript);
          }
            if(action.equals("retrieve")) {

            }
        } catch (JSONException e) {

        }
        return false;
    } //Closes execute

    public class JavaScriptInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        public void passBackValues(items)
        {
          LOG.e(TAG, "CordovaWebView: We got some localStorage items " + items.toString());
        }
    }
}
