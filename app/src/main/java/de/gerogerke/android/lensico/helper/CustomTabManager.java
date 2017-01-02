package de.gerogerke.android.lensico.helper;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;

/**
 * Created by Deutron on 27.03.2016.
 */
public class CustomTabManager {

    public static void openInCustomTab(Activity activity, String url){

        Uri websiteUri;
        if(!url.contains("https://") && !url.contains("http://")){
            websiteUri = Uri.parse("http://" + url);
        } else {
            websiteUri = Uri.parse(url);
        }

        CustomTabsIntent.Builder customtabintent = new CustomTabsIntent.Builder();
        customtabintent.setToolbarColor(Color.parseColor("#3F51B5"));
        customtabintent.setShowTitle(true);

        if(chromeInstalled(activity)){
            customtabintent.build().intent.setPackage("com.android.chrome");
        }

        customtabintent.build().launchUrl(activity, websiteUri);
    }

    private static boolean chromeInstalled(Activity activity){
        try {
            activity.getPackageManager().getPackageInfo("com.android.chrome", 0);
            return true;
        } catch (Exception e){
            return false;
        }
    }

}
