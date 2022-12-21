package com.miraz.helloju.util;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.multidex.MultiDex;

import com.miraz.helloju.activity.SplashScreen;
import com.miraz.helloju.R;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONException;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class YouApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        OneSignal.initWithContext(this);
        OneSignal.setAppId("b1b84f3a-1d55-422a-a78c-d2af912952cb");
        OneSignal.setNotificationOpenedHandler(new NotificationExtenderExample());

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/montserrat_regular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

    }

    class NotificationExtenderExample implements OneSignal.OSNotificationOpenedHandler {

        @Override
        public void notificationOpened(OSNotificationOpenedResult result) {

            try {

                String id = result.getNotification().getAdditionalData().getString("id");
                String title = result.getNotification().getAdditionalData().getString("title");
                String type = result.getNotification().getAdditionalData().getString("type");
                String url = result.getNotification().getAdditionalData().getString("external_link");

                Intent intent;
                if (id.equals("0") && !url.equals("false") && !url.trim().isEmpty()) {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse(url));
                } else {
                    intent = new Intent(YouApplication.this, SplashScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id", id);
                    intent.putExtra("title", title);
                    intent.putExtra("type", type);
                }
                startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
