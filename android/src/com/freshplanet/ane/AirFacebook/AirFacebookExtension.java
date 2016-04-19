package com.freshplanet.ane.AirFacebook;

import android.content.Intent;
import android.util.Log;

import com.adobe.air.AndroidActivityWrapper;
import com.adobe.air.FacebookActivityResultCallback;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREExtension;

public class AirFacebookExtension implements FREExtension, FacebookActivityResultCallback
{

	public static String TAG = "AirFacebook";
	public static Boolean nativeLogEnabled = true;
	
	public static AirFacebookExtensionContext context;

    private AndroidActivityWrapper aaw = null;

	public FREContext createContext(String extId)
	{
		return context = new AirFacebookExtensionContext();
	}

	public void dispose()
	{
		context = null;

        if (aaw != null) {

            aaw.removeActivityResultListener(this);
            aaw = null;
        }
	}
	
	public void initialize() {

        log("initialize");
        aaw = AndroidActivityWrapper.GetAndroidActivityWrapper();
        aaw.addActivityResultListener(this);
    }
	
	public static void log(String message)
	{
		as3Log(message);
		nativeLog(message, "NATIVE");
	}

	public static void as3Log(String message)
	{
		if (context != null && message != null) {

			context.dispatchStatusEventAsync("LOGGING", message);
		}
	}

	public static void nativeLog(String message, String prefix)
	{
		if (nativeLogEnabled) {

			Log.i(TAG, "[" + prefix + "] " + message);
		}
	}
	
	public static int getResourceId(String name)
	{
		return context != null ? context.getResourceId(name) : 0;
	}

    public void onActivityResult(int var1, int var2, Intent var3) {
        log(var1 + " - " + var2 + " - " + var3.toString());
    }
}
