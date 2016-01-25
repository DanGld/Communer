package com.communer.Activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.communer.Adapters.GooglePlacesAutocompleteAdapter;
import com.communer.Application.AppController;
import com.communer.Models.LocationObj;
import com.communer.Models.ReportItem;
import com.communer.Models.SecurityEvent;
import com.communer.R;
import com.communer.Utils.AppUser;
import com.google.android.gms.maps.MapFragment;
import com.rey.material.widget.ProgressView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by יובל on 02/09/2015.
 */
public class NewReport extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText description;
    private RadioGroup typeGroup, severityGroup;
    private CheckBox policeReported, isAnnonymous;

    private AutoCompleteTextView autoCompleteTextView;
    private LocationManager mLocationManager;

    private Button change_location;
    private ProgressView progressView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_report);

        initToolbar();

        description = (EditText)findViewById(R.id.new_report_description);
        typeGroup = (RadioGroup)findViewById(R.id.type_radio_group);
        severityGroup = (RadioGroup)findViewById(R.id.severity_radio_group);
        policeReported = (CheckBox)findViewById(R.id.police_reportted_cb);
        isAnnonymous = (CheckBox)findViewById(R.id.annonymus_report);
        change_location = (Button)findViewById(R.id.new_report_change_loc);

        autoCompleteTextView = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.setAdapter(new GooglePlacesAutocompleteAdapter(NewReport.this, R.layout.autocomplete_row));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    Location location = getLastKnownLocation();
                    if (location != null){
                        String locationTitle = getLocationTitleFromCoords(location.getLatitude(),location.getLongitude());
                        autoCompleteTextView.setText(locationTitle);
                        autoCompleteTextView.clearFocus();
                    }
                }
            }
        }, 1000);


        change_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextView.setText("");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_send, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_send:
                sendReport();
                break;

            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle(getResources().getString(R.string.new_report_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressView = (ProgressView)toolbar.findViewById(R.id.toolbar_new_report_pb);
    }

    private void sendReport() {
        progressView.setVisibility(View.VISIBLE);

        AppUser appUserInstance = AppUser.getInstance();
        String userHash = appUserInstance.getUserHash();
        String url = appUserInstance.getBaseUrl() + userHash + "/com.solidPeak.smartcom.requests.application.SecurityReportHandler.htm?";

        JSONObject report = new JSONObject();
        JSONObject loc = new JSONObject();

        String locationTitle = autoCompleteTextView.getText().toString();
        double[] locArray = getCoordsFromLocationString(locationTitle);
        String latText = String.valueOf(locArray[0]);
        String longText = String.valueOf(locArray[1]);
        String locationCoords = latText + "," + longText;

        try{
            loc.put("title",locationTitle);
            loc.put("coords",locationCoords);
        }catch (JSONException e){
            e.printStackTrace();
        }

        String type = "";
        if(typeGroup.getCheckedRadioButtonId()!=-1){
            int id= typeGroup.getCheckedRadioButtonId();
            View radioButton = typeGroup.findViewById(id);
            int radioId = typeGroup.indexOfChild(radioButton);
            RadioButton btn = (RadioButton)typeGroup.getChildAt(radioId);
            type = (String) btn.getText();
        }

        String desc = description.getText().toString();

        String severity = "";
        if(severityGroup.getCheckedRadioButtonId()!=-1){
            int id= severityGroup.getCheckedRadioButtonId();
            View radioButton = severityGroup.findViewById(id);
            int radioId = severityGroup.indexOfChild(radioButton);
            RadioButton btn = (RadioButton)severityGroup.getChildAt(radioId);
            type = (String)btn.getText();
        }

        boolean isPoliceReported = policeReported.isChecked();
        boolean isAnnonymousChecked = isAnnonymous.isChecked();

        try {
            report.put("location",loc);
            report.put("type",type);
            report.put("description",desc);
            report.put("severity",severity);
            report.put("isPoliceReport",isPoliceReported);
            report.put("isAnnonimus",isAnnonymousChecked);
        }catch(JSONException e){
            e.printStackTrace();
        }

        String username = appUserInstance.getUserFirstName() + " " + appUserInstance.getUserLastName();
        LocationObj locationObj = new LocationObj(locationTitle,locationCoords);
        ReportItem reportItem = new ReportItem(locationObj,type,desc,isPoliceReported,isAnnonymousChecked,severity);
        final SecurityEvent securityEvent = new SecurityEvent("",reportItem,username);

        String reportURL = url + "report=" + report.toString();
        reportURL = reportURL.replace(" ","%20");
        String tag_json_obj = "send_report_call";

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, reportURL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("success")){
                        saveReportLocal(securityEvent);
                        setResultIntent(securityEvent);
                        progressView.setVisibility(View.GONE);
                        Toast.makeText(NewReport.this, "Report sent succesfully", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(NewReport.this, "Error sending report to server", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(NewReport.this, "Error sending report to server", Toast.LENGTH_SHORT).show();
            }
        });

        jsonRequest.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_json_obj);
    }

    private void setResultIntent(SecurityEvent securityEvent) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("security_event",securityEvent);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    private void saveReportLocal(SecurityEvent securityEvent){
        AppUser appUserInstance = AppUser.getInstance();

        ArrayList<SecurityEvent> comSecurityEvents = appUserInstance.getCurrentCommunity().getSecurityEvents();
        comSecurityEvents.add(0, securityEvent);
        appUserInstance.getCurrentCommunity().setSecurityEvents(comSecurityEvents);
    }

    private double[] getCoordsFromLocationString(String address){
        try{
            Geocoder geocoder = new Geocoder(NewReport.this);
            List<Address> addresses;
            addresses = geocoder.getFromLocationName(address, 1);
            if(addresses.size() > 0){
                double latitude= addresses.get(0).getLatitude();
                double longitude= addresses.get(0).getLongitude();
                double[] locationArray = new double[]{latitude,longitude};
                return locationArray;
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getLocationTitleFromCoords(double latitude, double longtitude){
        try {
            Geocoder geocoder = new Geocoder(NewReport.this);
            List<Address> addresses;
            addresses = geocoder.getFromLocation(latitude, longtitude, 1);
            if(addresses.size() > 0) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Location getLastKnownLocation(){
        mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
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

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }
}
