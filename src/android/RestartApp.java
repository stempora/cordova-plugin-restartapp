package org.apache.cordova.restartapp;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import android.app.PendingIntent;
import android.app.Activity;
import android.app.AlarmManager;

import android.content.SharedPreferences;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;

import com.stempora.cordova.boilerplate.MainActivity;

public class RestartApp extends CordovaPlugin {

   /**
     * Tag for debug log messages
     */
    public static final String TAG = "RestartApp";
    public static RestartApp instance = null;
    protected Context applicationContext;
    protected SharedPreferences sharedPref;
    protected CallbackContext currentContext;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        Log.d(TAG, "initialize()");
        instance = this;
        applicationContext = this.cordova.getActivity().getApplicationContext();
        sharedPref = cordova.getActivity().getSharedPreferences(TAG, Activity.MODE_PRIVATE);
        super.initialize(cordova, webView);
    }
    
    public static RestartApp getInstance(){
      return instance;
    }

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        currentContext = callbackContext;
        try {
            if (action.equals("restartApp")) {
              this.restart();
            }
        } catch(Exception e ) {
            handleError("Exception occurred: ".concat(e.getMessage()));
            return false;
        }
        return true;
    }

        protected void restart() throws Exception{
        String baseError = "Unable to cold restart application: ";
        try {
           // this works for me (line 24 modify required)
            Intent intent = new Intent(applicationContext, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            applicationContext.startActivity(intent);
            Runtime.getRuntime().exit(0);
        } catch (Exception ex) {
            handleError(baseError+ ex.getMessage());
        }
    }

    public String escapeDoubleQuotes(String string){
        String escapedString = string.replace("\"", "\\\"");
        escapedString = escapedString.replace("%22", "\\%22");
        return escapedString;
    }

    
    public void executeGlobalJavascript(final String jsString){
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:" + jsString);
            }
        });
    }
    /**
      * Handles an error while executing a plugin API method  in the specified context.
      * Calls the registered Javascript plugin error handler callback.
      * @param errorMsg Error message to pass to the JS error handler
      */
    public void handleError(String errorMsg, CallbackContext context){
        try {
            logError(errorMsg);
            context.error(errorMsg);
        } catch (Exception e) {
            logError(e.toString());
        }
    }

    /**
      * Handles an error while executing a plugin API method in the current context.
      * Calls the registered Javascript plugin error handler callback.
      * @param errorMsg Error message to pass to the JS error handler
      */
    public void handleError(String errorMsg) {
        handleError(errorMsg, currentContext);
    }

   public void logInfo(String msg){
        Log.i(TAG, msg);
        executeGlobalJavascript("console.info(\""+TAG+"[native]: "+escapeDoubleQuotes(msg)+"\")");
    }

    public void logError(String msg){
      Log.e(TAG, msg);
      executeGlobalJavascript("console.error(\""+TAG+"[native]: "+escapeDoubleQuotes(msg)+"\")");
    }
}
