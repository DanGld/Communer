package com.communer.Activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.communer.Application.AppController;
import com.communer.Models.CommunityContact;
import com.communer.Models.CommunityMember;
import com.communer.Models.CommunityObj;
import com.communer.Models.MainModel;
import com.communer.Parsers.CommunityParser;
import com.communer.R;
import com.communer.Utils.AppUser;
import com.communer.Utils.CircleTransform;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by יובל on 27/09/2015.
 */

public class CommunityProfile extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private ImageView communityPic;
    private TextView community_name, community_fax, community_email, community_website;
    private Button leave_btn, apply_mmbr_btn;
    private ProgressBar leave_pb,apply_mmbr_pb;
    private LinearLayout mmbrs_container, mmbrs_main_cntnr;

    private String communityID;
    private CommunityObj community;
    private GoogleMap map;
    private ImageView contactImage;
    private TextView contactName;
    private TextView contactLocation;
    private TextView contactPhoneNumber;
    private ImageButton contactPhoneBtn;
    private String contactPhoneNumString;

    private ImageView load_more_dots;
    private ArrayList<CommunityMember> communityMembers;

    private SharedPreferences prefs;
    private BroadcastReceiver parserReceiver;

    private String mixPanelProjectToken = "537b3567fe348d6c5dfa0d6e2ae688b3";
    private MixpanelAPI mixpanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_profile);

        mixpanel = MixpanelAPI.getInstance(CommunityProfile.this, mixPanelProjectToken);
        mixpanel.track("Modules - Community Profile Page");

        prefs = getSharedPreferences("SmartCommunity", Activity.MODE_PRIVATE);

        initToolbar();
        initReceiver();
        setViewReference();
        initMap();
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("Community_Parsing_Done");

        parserReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra("Status");
                if (status.equals("Success")){
                    combineFade(apply_mmbr_pb,apply_mmbr_btn);

                    AppUser appUserInstance = AppUser.getInstance();
                    appUserInstance.getCurrentCommunity().setMemberType("user");
                    restartActivity();
                }
            }
        };

        registerReceiver(parserReceiver, filter);
    }

    private void initMap(){
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        AppUser appUserInstance = AppUser.getInstance();
        String[] cords = appUserInstance.getCurrentCommunity().getLocation().getCords().trim().split(",");

        if (cords != null){
            if (!cords[0].equals("null") && !cords[0].equals("")){
                double lat = Double.valueOf(cords[0]);
                double lng = Double.valueOf(cords[1]);
                if (map != null){
                    try{
                        LatLng currentCommunityCords = new LatLng(Double.valueOf(cords[0]), Double.valueOf(cords[1]));
                        List<Address> addresses = new ArrayList<>();
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(lat, lng, 1);
                        String address = addresses.get(0).getAddressLine(0);

                        Marker kiel = map.addMarker(new MarkerOptions()
                                .position(currentCommunityCords)
                                .title("Your Community")
                                .snippet(address));

                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentCommunityCords, 15));


                        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }

        map.getUiSettings().setScrollGesturesEnabled(false);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getResources().getString(R.string.community_profile_title));
    }

    private void setViewReference() {
        communityPic = (ImageView) findViewById(R.id.community_profile_main_image);

        community_name = (TextView) findViewById(R.id.community_profile_name);
        community_fax = (TextView) findViewById(R.id.community_profile_fax);
        community_email = (TextView) findViewById(R.id.community_profile_email);
        community_website = (TextView) findViewById(R.id.community_profile_website);

        leave_btn = (Button) findViewById(R.id.community_profile_leave);
        apply_mmbr_btn = (Button) findViewById(R.id.community_apply_member);
        leave_pb = (ProgressBar) findViewById(R.id.leave_community_pb);
        apply_mmbr_pb = (ProgressBar) findViewById(R.id.apply_as_member_pb);

        mmbrs_container = (LinearLayout) findViewById(R.id.community_profile_mmbrs_cntnr);
        mmbrs_main_cntnr = (LinearLayout) findViewById(R.id.community_profile_mmbrs_main_cntnr);
        load_more_dots = (ImageView) findViewById(R.id.thrre_dots_load_mmbrs);

        contactImage = (ImageView) findViewById(R.id.com_profile_contact_image);
        contactName = (TextView) findViewById(R.id.com_profile_incharge_name);
        contactLocation = (TextView) findViewById(R.id.com_profile_incharge_location);
        contactPhoneNumber = (TextView) findViewById(R.id.com_profile_incharge_phone);
        contactPhoneBtn = (ImageButton) findViewById(R.id.com_profile_incharge_phone_btn);
        contactPhoneBtn.setOnClickListener(this);

        AppUser appUserInstance = AppUser.getInstance();
        community = appUserInstance.getCurrentCommunity();
        if (community.getCommunityContacts().size() != 0) {
            CommunityContact currentContact = community.getCommunityContacts().get(0);
            String contactImageUrl = currentContact.getImageUrl();

            String adminName = "";
            String currentContactName = currentContact.getName();
            if (currentContactName != null){
                if (!currentContactName.equals("null"))
                    adminName = currentContactName;
            }
            contactName.setText(adminName);

            String adminPhone = "";
            String currentontactPhone = currentContact.getPhoneNumber();
            if (currentontactPhone != null){
                if (!currentontactPhone.equals("null"))
                    adminPhone = currentontactPhone;
            }
            contactPhoneNumber.setText(adminPhone);
            contactPhoneNumString = adminPhone;

            if (!contactImageUrl.equals("")){
                Picasso.with(CommunityProfile.this)
                        .load(contactImageUrl)
                        .error(R.drawable.placeholeder_lowres)
                        .into(contactImage);
            }else{
                Picasso.with(CommunityProfile.this)
                        .load(R.drawable.placeholeder_lowres)
                        .into(contactImage);
            }
        }
        String communityImage = community.getCommunityImageUrl();
        if (!communityImage.equals("")) {
            Picasso.with(CommunityProfile.this)
                    .load(communityImage)
                    .error(R.drawable.placeholeder_lowres)
                    .into(communityPic);
        } else {
            Picasso.with(CommunityProfile.this)
                    .load(R.drawable.placeholeder_lowres)
                    .into(communityPic);
        }

        final String name = community.getCommunityName();
        String fax = community.getFax();
        final String email = community.getEmail();
        String website = community.getWebsite();

        community_name.setText(name);
        community_fax.setText(fax);
        community_email.setText(email);
        community_website.setText(website);
        contactLocation.setText("");

        communityID = community.getCommunityID();

        leave_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUser appUserInstance = AppUser.getInstance();
                if (appUserInstance.getUserCommunities().size() > 1)
                    leaveCommunity();
                else
                    Toast.makeText(CommunityProfile.this, "You can't leave " + name + " community, because this is the only community you are registered to", Toast.LENGTH_SHORT).show();
            }
        });

        apply_mmbr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUser appUserInstance = AppUser.getInstance();
                if (appUserInstance.getCurrentCommunity().getMemberType().equals("user")){
                    Toast.makeText(CommunityProfile.this,"Already registered to " + AppUser.getCurrentCommunity().getCommunityName() + " as a member", Toast.LENGTH_SHORT).show();
                }else{
                    applyAsaMember();
                }
            }
        });

        community_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setType("plain/text");
                sendIntent.setData(Uri.parse(email));
                sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
                startActivity(sendIntent);
            }
        });

        addParticipants();

        mmbrs_main_cntnr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(CommunityProfile.this, MembersActivity.class);
                mIntent.putExtra("came_from","communityProfile");
                mIntent.putParcelableArrayListExtra("members",communityMembers);
                startActivity(mIntent);
            }
        });
    }

    private void leaveCommunity() {
        combineFade(leave_btn, leave_pb);

        AppUser appUserInstance = AppUser.getInstance();
        String userHash = appUserInstance.getUserHash();
        String url = appUserInstance.getBaseUrl() + userHash + "/com.solidPeak.smartcom.requests.application.SendLeaveRequest.htm?";

        String leavingUrl = url + "comID=" + communityID;
        leavingUrl = leavingUrl.replace(" ", "%20");
        String tag_json_obj = "leaving_community_call";

        final String finalLeavingUrl = leavingUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, leavingUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String s) {
                Log.d("Volley", finalLeavingUrl);
                if (s.equals("success")) {
                    handleLeavingCommunity();
                }
                combineFade(leave_pb, leave_btn);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(CommunityProfile.this, "Error leaving community", Toast.LENGTH_SHORT).show();
                combineFade(leave_pb, leave_btn);
            }
        });

        stringRequest.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    private void applyAsaMember(){
        combineFade(apply_mmbr_btn, apply_mmbr_pb);

        AppUser appUserInstance = AppUser.getInstance();
        String userHash = appUserInstance.getUserHash();
        String baseUrl = appUserInstance.getBaseUrl();
        String communityID = appUserInstance.getCurrentCommunity().getCommunityID();
        String tempURL = baseUrl + userHash + "/com.solidPeak.smartcom.requests.application.SendJoinRequestMod.htm?";
        String tag_json_obj = "apply_as_a_member";

        JSONObject obj = new JSONObject();
        try {
            obj.put("communityId", communityID);
            obj.put("asGuest",false);
        }catch (JSONException e){
            e.printStackTrace();
        }

        String query = null;
        try{
            query = URLEncoder.encode(obj.toString(), "utf-8");
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }

        String joinURL = tempURL + "join=" + query;
        StringRequest stringReq = new StringRequest(Request.Method.GET, joinURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("Volley", "Apply as a member succesfully");
                CommunityParser communityParser = new CommunityParser(CommunityProfile.this);
                communityParser.parseData(s);
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(CommunityProfile.this, "Error applying as a member",Toast.LENGTH_SHORT).show();
                Log.d("Volley", "Error applying as a member");
            }
        });

        stringReq.setShouldCache(false);
        stringReq.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringReq, tag_json_obj);
    }

    private void combineFade(final View fadeOutView, final View fadeInView) {
        fadeOutView.animate()
                .alpha(0f)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fadeOutView.setVisibility(View.GONE);
                        fadeInView.setVisibility(View.VISIBLE);
                        fadeInView.animate()
                                .alpha(1f)
                                .setDuration(300)
                                .setListener(null);
                    }
                });
    }

    private void handleLeavingCommunity() {
        boolean removed = false;

        AppUser appUserInstance = AppUser.getInstance();
        ArrayList<CommunityObj> communities = appUserInstance.getUserCommunities();

        if (communities.size() - 1 > 0) {
            for (int i = 0; i < communities.size(); i++) {
                CommunityObj com = communities.get(i);
                if (com.getCommunityID().equals(communityID)) {
                    communities.remove(i);
                    removed = true;
                }

                if (removed) {
                    appUserInstance.setUserCommunities(communities);
                    appUserInstance.setCurrentCommunity(communities.get(0));

                    String name = appUserInstance.getUserFirstName();
                    String lastName = appUserInstance.getUserLastName();
                    String gender = appUserInstance.getUserGender();
                    String mStatus = appUserInstance.getUserStatus();
                    String email = appUserInstance.getUserEmail();
                    String imageURL = appUserInstance.getUserImageUrl();
                    String supportMail = appUserInstance.getSupportMail();
                    Long bDay = appUserInstance.getUserBday();
                    boolean isRabai = appUserInstance.isRabai();
                    ArrayList<String> inactiveItems = new ArrayList<String>();

                    SharedPreferences sp = getSharedPreferences("SmartCommunity", Activity.MODE_PRIVATE);
                    sp.edit().putString("last_community_picked",communities.get(0).getCommunityName()).commit();

                    MainModel mainModel = new MainModel(name,lastName,gender,mStatus,email,imageURL,supportMail,bDay,communities,isRabai,inactiveItems);
                    saveMainModel(mainModel);
                    break;
                }
            }
            restartActivity();
        } else {
            Intent mIntent = new Intent(CommunityProfile.this, SearchCommunity.class);
            startActivity(mIntent);
        }
    }

    private void restartActivity() {
        Intent intent = new Intent(CommunityProfile.this, TabsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    private void addParticipants() {
        communityMembers = community.getMembers();
        if (communityMembers.size() <= 5) {

            if (communityMembers.size() > 0)
                load_more_dots.setVisibility(View.VISIBLE);

            for (int i = 0; i < communityMembers.size(); i++) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.participant_signle_pic_small, null);

                ImageView userPic = (ImageView) view.findViewById(R.id.participant_pic);
                if (communityMembers.get(i).getImageUrl() != null) {
                    if (!communityMembers.get(i).getImageUrl().equals("")) {
                        Picasso.with(CommunityProfile.this)
                                .load(communityMembers.get(i).getImageUrl())
                                .error(R.drawable.noprofilepic)
                                .transform(new CircleTransform())
                                .into(userPic);
                    } else {
                        Picasso.with(CommunityProfile.this)
                                .load(R.drawable.noprofilepic)
                                .transform(new CircleTransform())
                                .into(userPic);
                    }
                } else {
                    Picasso.with(CommunityProfile.this)
                            .load(R.drawable.noprofilepic)
                            .transform(new CircleTransform())
                            .into(userPic);
                }
                mmbrs_container.addView(view);
            }
        } else {
            for (int i = 0; i < 5; i++) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.participant_signle_pic_small, null);

                ImageView userPic = (ImageView) view.findViewById(R.id.participant_pic);
                if (communityMembers.get(i).getImageUrl() != null) {
                    if (!communityMembers.get(i).getImageUrl().equals("")) {
                        Picasso.with(CommunityProfile.this)
                                .load(communityMembers.get(i).getImageUrl())
                                .error(R.drawable.noprofilepic)
                                .transform(new CircleTransform())
                                .into(userPic);
                    } else {
                        Picasso.with(CommunityProfile.this)
                                .load(R.drawable.noprofilepic)
                                .transform(new CircleTransform())
                                .into(userPic);
                    }
                } else {
                    Picasso.with(CommunityProfile.this)
                            .load(R.drawable.noprofilepic)
                            .transform(new CircleTransform())
                            .into(userPic);
                }
                mmbrs_container.addView(view);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.com_profile_incharge_phone_btn:
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactPhoneNumString));
                startActivity(intent);
                break;
        }
    }

    private void saveMainModel(MainModel mainModel){
        Gson gson = new Gson();
        String mainModelString = gson.toJson(mainModel);

        SharedPreferences prefs = getSharedPreferences("SmartCommunity", Context.MODE_PRIVATE);
        prefs.edit().putString("latestCommunerData", mainModelString).commit();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(parserReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
