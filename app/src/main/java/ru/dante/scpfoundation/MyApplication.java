package ru.dante.scpfoundation;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;
import com.yandex.metrica.YandexMetrica;


public class MyApplication extends Application {
    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
                // VKAccessToken is invalid
            }
        }
    };

    private static final String API_KEY = "2e96643a-62fe-40f1-a97e-d4dab932ce5c";

    @Override
    public void onCreate() {
        //Initialize YandexMetrika
        YandexMetrica.activate(getApplicationContext(), API_KEY);
        YandexMetrica.enableActivityAutoTracking(this);
        //VK SDK
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(getApplicationContext());
        super.onCreate();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.edit().remove(getString(R.string.pref_key_random_url)).apply();
    }
}