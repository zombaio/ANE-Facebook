package com.freshplanet.ane.AirFacebook.functions;

import android.content.Intent;
import android.os.Bundle;
import com.adobe.fre.FREArray;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirFacebook.WebDialogActivity;

public class WebDialogFunction extends BaseFunction implements FREFunction
{
    public FREObject call(FREContext context, FREObject[] args)
    {
        super.call(context, args);

        String method = getStringFromFREObject(args[0]);
        Bundle parameters = getBundleOfStringFromFREArrays((FREArray) args[1], (FREArray) args[2]);
        String callback = getStringFromFREObject(args[3]);

        Intent i = new Intent(context.getActivity().getApplicationContext(), WebDialogActivity.class);
        i.putExtra(WebDialogActivity.extraPrefix + ".method", method);
        i.putExtra(WebDialogActivity.extraPrefix + ".parameters", parameters);
        i.putExtra(WebDialogActivity.extraPrefix + ".callback", callback);
        context.getActivity().startActivity(i);

        return null;
    }
}
