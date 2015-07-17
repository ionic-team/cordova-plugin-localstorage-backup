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

import android.webkit.WebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.webkit.JavascriptInterface;
import android.webkit.WebViewClient;

/**
 * LocalStoragePlugin
 *
 * This plugin can be used to fetch data from the native browser localStorage.
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
    }

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action     The action to execute.
     * @param args       JSONArry of arguments for the plugin.
     * @param callbackId The callback id used when calling back into JavaScript.
     * @return A PluginResult object with a status and message.
     */
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        CordovaActivity activity = ((CordovaActivity) this.cordova.getActivity());
        this.initNow(activity, callbackContext);
        return true;
    } //Closes execute

    /**
     * We execute this on the UiThread because of WebView. It gives a lot of warning that it should only be executed on main thread to prevent concurrent problems.
     */
    public void initNow(final Activity activity, final CallbackContext callbackContext) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                WebView defaultWebView = new WebView(activity);

                final CallbackContext cbContext = callbackContext;
                try {
                    String jsCallback = "JSInterface.passBackValues(JSON.stringify(localStorage));";

                    // In KitKat+ you should use the evaluateJavascript method.
                    // The commented code is kind of a preview of how you could do it. The code itself doesn't work.

                      /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        String function = ""; // http://stackoverflow.com/a/20377857/1587664
                        defaultWebView.evaluateJavascript(function, new ValueCallback<String>() {
                          @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                          @Override
                          public void onReceiveValue(String s) {
                            callbackContext.success(s);
                          }
                        });
                      } else {*/
                    
                    JavaScriptInterface jsInterface = new JavaScriptInterface(callbackContext);
                    defaultWebView.addJavascriptInterface(jsInterface, "JSInterface");
                    defaultWebView.getSettings().setJavaScriptEnabled(true);

                    // Find the correct database path for cordova.
                    String databasePath = defaultWebView.getContext().getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
                    defaultWebView.getSettings().setDatabaseEnabled(true);
                    defaultWebView.getSettings().setDomStorageEnabled(true);
                    defaultWebView.getSettings().setDatabasePath(databasePath);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        defaultWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
                        defaultWebView.getSettings().setAllowFileAccessFromFileURLs(true);
                    }

                    String summary = "<html><body><script>" + jsCallback + "</script></body></html>";
                    defaultWebView.loadDataWithBaseURL("file:///android_asset/test.html", summary, "text/html", "UTF-8", "about:blank");
                    //}
                } catch (Exception e) {
                    callbackContext.error(e.getMessage());
                }
            }
        });
    }

    /**
     * This is the class that interfaces with javascript.
     * From javascript we call passBackValues function.
     */
    public class JavaScriptInterface {
        CallbackContext mContext;

        /**
         * Instantiate the interface and set the context
         */
        JavaScriptInterface(CallbackContext callbackContext) {
            mContext = callbackContext;
        }

        @JavascriptInterface
        public void passBackValues(String items) {
            mContext.success(items);
        }
    }
}