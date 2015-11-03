package com.freshplanet.ane.AirFacebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.adobe.fre.FREContext;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.internal.WebDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WebDialogActivity extends Activity implements WebDialog.OnCompleteListener
{
    public static String extraPrefix = "com.freshplanet.ane.AirFacebook.WebDialogActivity";

    private WebDialog webDialog;

    private String method;
    private Bundle parameters;
    private String callback;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        AirFacebookExtension.log("WebDialogActivity onCreate()");

        method = this.getIntent().getStringExtra(extraPrefix + ".method");
        parameters = this.getIntent().getBundleExtra(extraPrefix + ".parameters");
        callback = this.getIntent().getStringExtra(extraPrefix + ".callback");


        webDialog = new WebDialog(this, method, parameters, WebDialog.DEFAULT_THEME, this);
        webDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        AirFacebookExtension.log("WebDialogActivity.onActivityResult");
        finish();
    }

    @Override
    public void onComplete(Bundle values, FacebookException error)
    {
        FREContext context = AirFacebookExtension.context;
        if (context != null && callback != null)
        {
            // error and not cancelled error
            if (error != null && !(error.getMessage() == null || error instanceof FacebookOperationCanceledException))
            {
                AirFacebookExtension.log("WebDialogActivity.onComplete, error " + error.getMessage());
                context.dispatchStatusEventAsync(callback, AirFacebookError.makeJsonError(error.getMessage()));
                finish();
                return;
            }

            String postMessage = null;
            if (error == null)
            {
                // Content depends on type of dialog that was invoked (method)
                if(method.equalsIgnoreCase("feed"))
                {
                    // Check if feed gave us a post_id back, if not we cancelled
                    String postId = values.getString("post_id");
                    if (postId != null)
                    {
                        postMessage = "{ \"params\": \""+postId+"\" }";
                    }
                }
                else if(method.equalsIgnoreCase("apprequests"))
                {
                    // We get a request id, and a list of recepients if selected
                    String request = values.getString("request");
                    if (request != null)
                    {
                        // Give everything as URL encoded value to match iOS response
                        postMessage = "{ \"params\": \"" + bundleSetToURLEncoded(values) + "\" }";
                    }
                }
            }

            // If  message wasn't set by here, then we cancelled
            if(postMessage == null)
                postMessage = "{ \"cancel\": true }";

            AirFacebookExtension.log("WebDialogActivity.onComplete, postMessage " + postMessage);
            context.dispatchStatusEventAsync(callback, postMessage);
        }

        finish();
    }

    protected String bundleSetToURLEncoded(Bundle values)
    {
        StringBuilder sb = new StringBuilder();

        // Go through each key
        String[] keys = values.keySet().toArray(new String[0]);
        for (int i = 0; i < keys.length; i++)
        {
            if(i > 0)
                sb.append("&");
            try
            {
                sb.append(keys[i]).append("=").append(URLEncoder.encode(values.get(keys[i]).toString(), "utf-8"));
            }
            catch(UnsupportedEncodingException ex)
            {
                ex.printStackTrace();
            }
        }

        return sb.toString();
    }
}
