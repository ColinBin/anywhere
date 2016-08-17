package com.colinlvbin.extreme.anywhere;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.yolanda.nohttp.NoHttp;

import java.util.List;
import java.util.Locale;

/**
 * Created by Colin on 2016/6/7.
 */
public class MyApplication extends Application{
    //LBS相关
    private LocationManager locationManager;
    private String provider;
    private double longitude;
    private double latitude;

    //语言相关
    private SharedPreferences sharedPreferences;
    String language;
    @Override
    public void onCreate() {
        super.onCreate();
        //LBS相关
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else {
            provider = null;
        }
        if (provider != null) {
            locationManager.requestLocationUpdates(provider, 5000, 1, locationListener);
        }
        sharedPreferences=getSharedPreferences("app_config", MODE_PRIVATE);
        //如果修改了语言，则读取并设置
        if(!sharedPreferences.getString("language","").equals("")){
            if(sharedPreferences.getString("language","").equals("zh")){
                SetLanguage(Locale.SIMPLIFIED_CHINESE);
            }else if(sharedPreferences.getString("language","").equals("en")){
                SetLanguage(Locale.ENGLISH);
            }
        }
        Locale locale=getResources().getConfiguration().locale;
        language=locale.getLanguage();

        //NoHttp初始化
        NoHttp.initialize(this);
    }
    //LBS相关
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public String GetProvider(){
        return provider;
    }
    public double GetLatitude(){
        return latitude;
    }
    public double GetLongitude(){
        return longitude;
    }
    public String GetLanguage(){
        return language;
    }
    public void SetLanguage(Locale language){
        Resources resources=getResources();
        Configuration config=resources.getConfiguration();
        DisplayMetrics dm=resources.getDisplayMetrics();
        config.locale=language;
        resources.updateConfiguration(config,dm);
    }
}
