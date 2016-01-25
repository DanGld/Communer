package com.communer.Activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.communer.Fragments.LoginPhoneDetails;
import com.communer.Fragments.LoginSmsVerification;
import com.communer.Models.CommunityObj;
import com.communer.Parsers.LocalDataParser;
import com.communer.R;
import com.communer.Utils.AppUser;
import com.communer.Utils.CountriesPrefixes;
import com.communer.Utils.MyLocation;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.rey.material.widget.ProgressView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by ���� on 28/07/2015.
 */
public class MainActivity extends FragmentActivity {

    private FrameLayout subFrame;
    private int screenHeight;
    private ProgressView splash_progress;

    private final static int PREFIX_REQUEST = 55;
    private static final int RC_SIGN_IN = 0;

    private SharedPreferences prefs;
    private BroadcastReceiver JsonParserReceiver;

    private String mixPanelProjectToken = "537b3567fe348d6c5dfa0d6e2ae688b3";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, mixPanelProjectToken);
        mixpanel.track("MainActivity - onCreate called");

        prefs = getSharedPreferences("SmartCommunity", Activity.MODE_PRIVATE);

        splash_progress = (ProgressView)findViewById(R.id.splash_progressview);

        initLanguage();
        initReceiver();
        clearNotificationBar();

//        editor.clear().commit();

        int entranceCount = prefs.getInt("entrance_count", -1);
        if (entranceCount == -1){
            showLoginFrame(savedInstanceState);
        }else{
            splash_progress.setVisibility(View.VISIBLE);

            String userHash = prefs.getString("userHash","noData");
            Log.d("User Hash:", userHash);

            try{
                String latestCommunerData = prefs.getString("latestCommunerData","noData");
                JSONObject latestDataObj = new JSONObject(latestCommunerData);

                LocalDataParser localDataParser = new LocalDataParser(MainActivity.this, "MainActivity");
                localDataParser.callParser(latestDataObj);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private void clearNotificationBar() {
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("JSON_Parser_Done");

        JsonParserReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                splash_progress.setVisibility(View.GONE);

                String came_from = intent.getStringExtra("cameFrom");

                AppUser appUserInstance = AppUser.getInstance();
                ArrayList<CommunityObj> currentCommunities = appUserInstance.getUserCommunities();

                if (came_from.equals("MainActivity")){
                    if (currentCommunities != null) {
                        if (currentCommunities.size() > 0){
                            setEntranceCount();
                            Intent mIntent = new Intent(MainActivity.this, TabsActivity.class);
                            mIntent.putExtra("mainInit",true);
                            startActivity(mIntent);
                        }else{
                            Intent mIntent = new Intent(MainActivity.this, SearchCommunity.class);
                            startActivity(mIntent);
                        }
                    }else{
                        Intent mIntent = new Intent(MainActivity.this, SearchCommunity.class);
                        startActivity(mIntent);
                    }
                }else if(came_from.equals("login_sms")){
                    setEntranceCount();
                    if (currentCommunities != null){
                        if (currentCommunities.size() > 0){
                            Intent mIntent = new Intent(MainActivity.this, TabsActivity.class);
                            startActivity(mIntent);
                        }else{
                            LoginSmsVerification sms_frag = (LoginSmsVerification)getSupportFragmentManager().findFragmentByTag("login_sms");
                            sms_frag.showSocialLoginDialog();
                        }
                    }else{
                        LoginSmsVerification sms_frag = (LoginSmsVerification)getSupportFragmentManager().findFragmentByTag("login_sms");
                        sms_frag.showSocialLoginDialog();
                    }
                }
            }
        };
        registerReceiver(JsonParserReceiver, filter);
    }

    private void initLanguage(){
        String latestLanguage = prefs.getString("last_language_picked","noDefault");
        if (!latestLanguage.equals("noDefault")){
            Locale locale = new Locale(latestLanguage);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }else{
            Locale locale = new Locale("en");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }
    }

    private void getFBHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.communer",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    private void showLoginFrame(Bundle savedInstanceState){
        subFrame = (FrameLayout)findViewById(R.id.loginBottomFrameLayout);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenHeight = size.y;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, screenHeight / 2, 0, 0);
        subFrame.setLayoutParams(layoutParams);

        if (findViewById(R.id.loginBottomFrameLayout) != null) {
            if (savedInstanceState != null) {
                return;
            }
            LoginPhoneDetails loginFragment = new LoginPhoneDetails();
            getSupportFragmentManager().beginTransaction().add(R.id.loginBottomFrameLayout, loginFragment, "loginFrag").commit();
        }
    }

    private String getCountryFromLoc(Double lat, Double lng) throws IOException {
        Geocoder gcd = new Geocoder(MainActivity.this, Locale.US);
        List<Address> addresses = gcd.getFromLocation(lat, lng, 1);
        if (addresses.size() > 0)
            return addresses.get(0).getCountryName();
        else
            return "";
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case (RC_SIGN_IN) :
                    LoginSmsVerification sms_frag = (LoginSmsVerification)getSupportFragmentManager().findFragmentByTag("login_sms");
                    sms_frag.onActivityResult(requestCode, resultCode, data);
                    break;
                case (PREFIX_REQUEST) :
                    final String prefix = data.getStringExtra("prefix");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LoginPhoneDetails fragment = (LoginPhoneDetails)getSupportFragmentManager().findFragmentByTag("loginFrag");
                            fragment.changePrefixText(prefix);
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    private void handleLocationRequest(){
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
            @Override
            public void gotLocation(Location location){
                Double lat = location.getLatitude();
                Double lng = location.getLongitude();
                try {
                    final String country = getCountryFromLoc(lat,lng);
                    final LoginPhoneDetails fragment = (LoginPhoneDetails)getSupportFragmentManager().findFragmentByTag("loginFrag");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fragment.changePrefixText(CountriesPrefixes.getPhone(country));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(MainActivity.this, locationResult);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(JsonParserReceiver);
    }

    private void setEntranceCount(){
        SharedPreferences.Editor editor = prefs.edit();
        int entranceCount = prefs.getInt("entrance_count", -1);
        if (entranceCount == -1){
            editor.putInt("entrance_count", 1);
        }else{
            editor.putInt("entrance_count", entranceCount + 1);
        }
        editor.commit();
    }
}
