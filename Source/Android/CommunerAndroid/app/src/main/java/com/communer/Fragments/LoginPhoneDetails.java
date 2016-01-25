package com.communer.Fragments;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.communer.Activities.PrefixActivity;
import com.communer.Application.AppController;
import com.communer.R;
import com.communer.Utils.AppUser;
import com.communer.Utils.CountriesPrefixes;
import com.rey.material.widget.ProgressView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by ���� on 28/07/2015.
 */
public class LoginPhoneDetails extends Fragment {

    private ImageButton signInBtn;
    private Button prefixBtn;
    private EditText phoneNum;
    private ProgressView loginPB;

    private LocationManager mLocationManager;
    private boolean prefixPicked = false;

    private final static int PREFIX_REQUEST = 55;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.loginpage, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*View decorView = getActivity().getWindow().getDecorView();
        int uiOptions =   View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);*/

        prefixBtn = (Button)view.findViewById(R.id.prefixBtn);
        signInBtn = (ImageButton)view.findViewById(R.id.signInButton);
        phoneNum = (EditText)view.findViewById(R.id.signInPhoneNumber);
        loginPB = (ProgressView)view.findViewById(R.id.login_pb);

        Location location = getLastKnownLocation();
        if (location != null){
            String country = getCountryFromCoords(location.getLatitude(), location.getLongitude());
            String countryCode = CountriesPrefixes.getPhone(country);

            if (countryCode != null)
                prefixBtn.setText(countryCode);
        }

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prefix = prefixBtn.getText().toString();
                String num = phoneNum.getText().toString();
                if (prefix.length() > 0) {
                    if (num.length() > 0) {
                        loginCall(prefix, num);
                    }else {
                        Toast.makeText(getActivity(), "Phone number is empty, please enter the information", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getActivity(), "Prefix is empty, please pick one", Toast.LENGTH_SHORT).show();
                }
            }
        });

        prefixBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getActivity(), PrefixActivity.class);
                getActivity().startActivityForResult(mIntent, PREFIX_REQUEST);
            }
        });
    }

    public void changePrefixText(String prefix){
        prefixBtn.setText(prefix);
        prefixPicked = true;
    }

    private void loginCall(String pre, String phone){
        loginPB.setVisibility(View.VISIBLE);

        String tag_json_obj = "login_request";
        pre = pre.replace("+", "");
        String phone_number = pre+phone;
        phone_number = phone_number.trim();

        AppUser appUserInstance = AppUser.getInstance();
        String url = appUserInstance.getBaseUrl() + "mCBO5cFMLBCMVa9QFdIWmDgc/com.solidPeak.smartcom.requests.application.getLoginData.htm?phone=" + phone_number;

        ////0545766230
        final String finalPhone_number = phone_number;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley", response.toString());
                        try {
                            if (response.getString("status").equals("success")){
                                loginPB.setVisibility(View.GONE);

                                Bundle data = new Bundle();
                                data.putString("user_phone", finalPhone_number);

                                LoginSmsVerification smsFragment = new LoginSmsVerification();
                                smsFragment.setArguments(data);
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                                transaction.replace(R.id.loginBottomFrameLayout, smsFragment,"login_sms");
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }else{
                                loginPB.setVisibility(View.GONE);
                                Toast.makeText(getActivity(),"Wrong details, try again...",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley", "Error: " + error.getMessage());
                loginPB.setVisibility(View.GONE);
                Toast.makeText(getActivity(),"Server Error, try again soon...",Toast.LENGTH_SHORT).show();
                // hide the progress dialog
            }
        });
        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager)getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private String getCountryFromCoords(double latitude, double longtitude){
        try {
            Geocoder geocoder = new Geocoder(getActivity());
            List<Address> addresses;
            addresses = geocoder.getFromLocation(latitude, longtitude, 1);
            if(addresses.size() > 0){
                return addresses.get(0).getCountryName();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
