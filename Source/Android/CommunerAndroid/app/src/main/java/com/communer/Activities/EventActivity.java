package com.communer.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.communer.Adapters.ShareIntentsAdapter;
import com.communer.Application.AppController;
import com.communer.Models.CommunityContact;
import com.communer.Models.CommunityMember;
import com.communer.Models.DailyEvents;
import com.communer.Models.EventOrMessage;
import com.communer.Models.LocationObj;
import com.communer.R;
import com.communer.Utils.AppUser;
import com.communer.Utils.CircleTransform;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by ���� on 01/08/2015.
 */
public class EventActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String title,date,hours,mapLatExtra, mapLongExtra,event_ID,buyLink, dailyEventDate;
    private boolean isFree;
    private long theDateLong,startTime, endTime;
    private LocationObj locationObj;

    private TextView eventDate, eventTitle, eventHours, locationText;
    private LinearLayout participants_container,participants_main_container;
    private ImageButton attending_btn, calendar_btn;
    private FloatingActionButton share_fab;
    private Button buyTicket;
    private ImageView three_dots_mmbrs;

    private String came_from;
    private String parentActivity = "";
    private ArrayList<CommunityContact> participants;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity);

        Intent dataIntent = getIntent();
        handleExtras(dataIntent);
        initToolbar();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (!isFinishing()) {
                    MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(EventActivity.this);
                }
            }
        }, 1000);

        setViewReference();

        attending_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAttendingDialog();
            }
        });

        calendar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEventToCalendar();
            }
        });

        buyTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText = buyTicket.getText().toString();
                if (buyLink != null){
                    if (!btnText.equals("Free") && !buyLink.equals("") && !buyLink.equalsIgnoreCase("null")){
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(buyLink));
                        startActivity(browserIntent);
                    }
                }
            }
        });
    }

    private void handleExtras(Intent dataIntent){
        came_from = dataIntent.getStringExtra("came_from");
        parentActivity = getIntent().getStringExtra("ParentClassName");
        event_ID = dataIntent.getStringExtra("event_id");
        title = dataIntent.getStringExtra("event_title");
        buyLink = dataIntent.getStringExtra("event_buy_link");
        startTime = dataIntent.getLongExtra("event_start_time", -1);
        endTime = dataIntent.getLongExtra("event_end_time", -1);
        mapLatExtra = dataIntent.getStringExtra("event_lat");
        mapLongExtra = dataIntent.getStringExtra("event_long");
        isFree = dataIntent.getBooleanExtra("isFree", true);
        dailyEventDate = dataIntent.getStringExtra("dailyEventDate");
        getParticipants();
//        participants = dataIntent.getParcelableArrayListExtra("participants");

        if (came_from.equals("messages_fragment")){
            date = dataIntent.getStringExtra("event_date");
            hours = dataIntent.getStringExtra("event_hours");
            theDateLong = dataIntent.getLongExtra("long_date", -1);
        }else if (came_from.equals("calendar_fragment")) {
            theDateLong = dataIntent.getLongExtra("event_date", -1);
            locationObj = dataIntent.getParcelableExtra("location");
        }
    }

    private void openAttendingDialog() {

        new MaterialDialog.Builder(this)
                .title(R.string.attending_dialog)
                .items(R.array.attending_values)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (text != null){
                            String attendingStatus = text.toString();
                            setAttendingStatusCall(attendingStatus);

                            if (attendingStatus.equalsIgnoreCase("yes")){
                                addParticipantToArray();
                            }
                            return true;
                        }
                        return false;
                    }
                })
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .negativeColor(ContextCompat.getColor(EventActivity.this, R.color.AppOrange))
                .show();
    }

    private void addParticipantToArray(){
        if (!checkUserInParticipants()){
            CommunityMember mMember = AppUser.getUserAsMember();
            String mUsername = mMember.getName();
            String phone = mMember.getPhone();
            String id = mMember.getId();
            String imageURL = mMember.getImageUrl();

            CommunityContact mUser = new CommunityContact(mUsername,phone, "", imageURL, "", id);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.participant_signle_pic_small, null);

            ImageView userPic = (ImageView)view.findViewById(R.id.participant_pic);
            String userImageURL = mUser.getImageUrl();
            if (userImageURL != null) {
                if (!userImageURL.equals("")) {
                    Picasso.with(EventActivity.this)
                            .load(userImageURL)
                            .error(R.drawable.noprofilepic)
                            .transform(new CircleTransform())
                            .into(userPic);
                }else {
                    Picasso.with(EventActivity.this)
                            .load(R.drawable.noprofilepic)
                            .transform(new CircleTransform())
                            .into(userPic);
                }
            }else{
                Picasso.with(EventActivity.this)
                        .load(R.drawable.noprofilepic)
                        .transform(new CircleTransform())
                        .into(userPic);
            }
            participants_container.addView(view);
            participants.add(mUser);

            ArrayList<DailyEvents> communityEvents = AppUser.getCurrentCommunity().getDailyEvents();
            for (int j=0; j<communityEvents.size(); j++){
                DailyEvents tempDailyEvent = communityEvents.get(j);
                String tempDailyEventDate = tempDailyEvent.getDate();
                if (tempDailyEventDate.equalsIgnoreCase(dailyEventDate)){
                    for (int i=0; i< tempDailyEvent.getEvents().size(); i++){
                        EventOrMessage tempEvent = tempDailyEvent.getEvents().get(i);
                        String tempEventID = tempEvent.getEventID();
                        if (tempEventID.equalsIgnoreCase(event_ID)){
                            tempEvent.setParticipants(participants);
                            tempDailyEvent.getEvents().set(i,tempEvent);
                            break;
                        }
                    }

                    communityEvents.set(j, tempDailyEvent);
                    AppUser.getCurrentCommunity().setDailyEvents(communityEvents);
                    break;
                }
            }
        }
    }

    private boolean checkUserInParticipants() {
        boolean userExist = false;

        if (participants == null)
            participants = new ArrayList<CommunityContact>();

        for (CommunityContact participant : participants){
            if (participant.getId().equals(AppUser.getUserAsMember().getId())){
                userExist = true;
                break;
            }
        }
        return userExist;
    }

    private void getParticipants(){
        ArrayList<DailyEvents> communityEvents = AppUser.getCurrentCommunity().getDailyEvents();

        participantsMaimLoop:
        for (int j=0; j<communityEvents.size(); j++){
            DailyEvents tempDailyEvent = communityEvents.get(j);
            String tempDailyEventID = tempDailyEvent.getDate();
            if (tempDailyEventID.equalsIgnoreCase(dailyEventDate)){
                for (int i=0; i< tempDailyEvent.getEvents().size(); i++){
                    EventOrMessage tempEvent = tempDailyEvent.getEvents().get(i);
                    String tempEventID = tempEvent.getEventID();
                    if (tempEventID.equalsIgnoreCase(event_ID)){
                        participants = tempEvent.getParticipants();
                        break participantsMaimLoop;
                    }
                }
            }
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Double mapLat;
        Double mapLong;

        if (mapLatExtra != null){
            if (!mapLatExtra.equals("")){
                mapLat = Double.parseDouble(mapLatExtra);
                mapLong = Double.parseDouble(mapLongExtra);
            }else{
                mapLat = Double.parseDouble("32.069965");
                mapLong = Double.parseDouble("34.777464");
            }
        }else{
            mapLat = Double.parseDouble("32.069965");
            mapLong = Double.parseDouble("34.777464");
        }

        LatLng mapLocation = new LatLng(mapLat,mapLong);
        googleMap.addMarker(new MarkerOptions().position(mapLocation));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mapLocation, 13), 2000, null);
        googleMap.getUiSettings().setScrollGesturesEnabled(false);

        try {
            Geocoder geocoder = new Geocoder(this, Locale.US);
            List<Address> addresses = geocoder.getFromLocation(mapLat, mapLong, 1);
            String cityName = addresses.get(0).getAddressLine(0);
            String stateName = addresses.get(0).getAddressLine(1);
            String countryName = addresses.get(0).getAddressLine(2);
            locationText.setText(stateName + "," + cityName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setViewReference() {
        eventDate = (TextView)findViewById(R.id.event_page_date);
        eventTitle = (TextView)findViewById(R.id.event_page_title);
        eventHours = (TextView)findViewById(R.id.event_page_hours);
        locationText = (TextView)findViewById(R.id.event_page_loc_text);
        participants_container = (LinearLayout)findViewById(R.id.event_page_participans_cntnr);
        participants_main_container = (LinearLayout)findViewById(R.id.event_page_participans_main_cntnr);
        attending_btn = (ImageButton)findViewById(R.id.attending_btn);
        calendar_btn = (ImageButton)findViewById(R.id.calendar_btn);
        share_fab = (FloatingActionButton)findViewById(R.id.share_fab);
        buyTicket = (Button)findViewById(R.id.event_page_buy_ticket_btn);
        three_dots_mmbrs = (ImageView)findViewById(R.id.three_dots_load_mmbrs);

        if (isFree)
            buyTicket.setText("Free");

        if (came_from.equals("messages_fragment")){
            eventDate.setText(date);
            eventTitle.setText(title);
            eventHours.setText(hours);
        }else if (came_from.equals("calendar_fragment")){
            SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd yyyy", Locale.US);
            eventDate.setText(getFormattedDateFromTimestamp(theDateLong,dateFormatter));
            eventTitle.setText(title);

            dateFormatter = new SimpleDateFormat("HH:mm", Locale.US);
            eventHours.setText(getFormattedDateFromTimestamp(startTime,dateFormatter) + " - " + getFormattedDateFromTimestamp(endTime,dateFormatter));
        }

        share_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareIntent();
            }
        });

        addParticipants();

        participants_main_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(EventActivity.this, MembersActivity.class);
                mIntent.putExtra("came_from", "eventPage");
                mIntent.putParcelableArrayListExtra("participants", participants);
                startActivity(mIntent);
            }
        });
    }

    private void addEventToCalendar(){
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", startTime);
        intent.putExtra("endTime", endTime);
        intent.putExtra("title", title);
        intent.putExtra("eventLocation", locationText.getText().toString());
        startActivity(intent);
    }

    private void setAttendingStatusCall(String status){
        AppUser appUserInstance = AppUser.getInstance();
        String userHash = appUserInstance.getUserHash();
        String url = appUserInstance.getBaseUrl() + userHash + "/com.solidPeak.smartcom.requests.application.SendAttendingStatus.htm?";
        String attendingUrl = url + "event=" + event_ID + "&status=" + status;
        attendingUrl = attendingUrl.replace(" ","%20");

        String tag_json_obj = "attending_status_call";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, attendingUrl, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                if (!response.equals("success")){
                    Toast.makeText(EventActivity.this,"Error sending attending status", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        stringRequest.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(stringRequest
                , tag_json_obj);
    }

    public static String getFormattedDateFromTimestamp(long timestampInMilliSeconds, SimpleDateFormat formatter)
    {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        formatter.setTimeZone(timeZone);
        Calendar c = Calendar.getInstance(timeZone);
        c.setTimeInMillis(timestampInMilliSeconds);
        String formattedDate = formatter.format(c.getTime());
        return formattedDate;
    }

    private void shareIntent(){
        final String playstoreUrl = "https://play.google.com/store/apps/details?id=com.communer&hl=en";
        final List<Intent> targetedShareIntents = new ArrayList<Intent>();
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(shareIntent, 0);
        if (!resInfo.isEmpty()){
            for (ResolveInfo resolveInfo : resInfo) {
                String packageName = resolveInfo.activityInfo.packageName;
                String app_name = resolveInfo.activityInfo.loadLabel(getPackageManager()).toString();
                Intent targetedShareIntent = new Intent(android.content.Intent.ACTION_SEND);
                targetedShareIntent.setType("text/plain");
                targetedShareIntent.setPackage(packageName);
                targetedShareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, AppUser.getCurrentCommunity().getCommunityName() + " - " + title);
                targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, eventDate.getText().toString() + " at " + eventHours.getText().toString() + "\nLocation: " + locationText.getText().toString() + "\n\nDownload beta version of Communer at " + playstoreUrl);
                targetedShareIntent.putExtra("packageName", packageName);
                targetedShareIntent.putExtra("appName", app_name);
                targetedShareIntents.add(targetedShareIntent);
            }

            new MaterialDialog.Builder(this)
                    .title("Select app to share")
                    .adapter(new ShareIntentsAdapter(targetedShareIntents,EventActivity.this),
                            new MaterialDialog.ListCallback(){
                                @Override
                                public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                    Intent intent = targetedShareIntents.get(which);
                                    String selectedIntentPackage = intent.getPackage();

                                    if (selectedIntentPackage.equalsIgnoreCase("com.facebook.katana")){
                                        ShareDialog shareDialog = new ShareDialog(EventActivity.this);
                                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                                .setContentTitle(title)
                                                .setContentDescription("Download beta version of Communer")
                                                .setContentUrl(Uri.parse(playstoreUrl))
                                                .build();

                                        shareDialog.show(linkContent);
                                    }else{
                                        startActivity(intent);
                                    }
                                }
                            })
                    .show();

/*            Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Select app to share");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));*/

/*            startActivity(chooserIntent);
            ShareDialog shareDialog = new ShareDialog(this);
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(title)
                    .setContentDescription("Download beta version of Communer")
                    .setContentUrl(Uri.parse(playstoreUrl))
                    .build();

            shareDialog.show(linkContent);*/
        }
    }

    private void addParticipants(){
        if (participants != null){
            if (participants.size() <= 5){

                if (participants.size() > 0)
                    three_dots_mmbrs.setVisibility(View.VISIBLE);

                for (int i = 0; i < participants.size(); i++) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.participant_signle_pic_small, null);

                    ImageView userPic = (ImageView) view.findViewById(R.id.participant_pic);
                    if (participants.get(i).getImageUrl() != null) {
                        if (!participants.get(i).getImageUrl().equals("")) {
                            Picasso.with(EventActivity.this)
                                    .load(participants.get(i).getImageUrl())
                                    .error(R.drawable.noprofilepic)
                                    .transform(new CircleTransform())
                                    .into(userPic);
                        }else {
                            Picasso.with(EventActivity.this)
                                    .load(R.drawable.noprofilepic)
                                    .transform(new CircleTransform())
                                    .into(userPic);
                        }
                    }else{
                        Picasso.with(EventActivity.this)
                                .load(R.drawable.noprofilepic)
                                .transform(new CircleTransform())
                                .into(userPic);
                    }
                    participants_container.addView(view);
                }
            }else {
                for (int i = 0; i < 5; i++) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.participant_signle_pic_small, null);

                    ImageView userPic = (ImageView) view.findViewById(R.id.participant_pic);
                    if (participants.get(i).getImageUrl() != null) {
                        if (!participants.get(i).getImageUrl().equals("")) {
                            Picasso.with(EventActivity.this)
                                    .load(participants.get(i).getImageUrl())
                                    .error(R.drawable.noprofilepic)
                                    .transform(new CircleTransform())
                                    .into(userPic);
                        } else {
                            Picasso.with(EventActivity.this)
                                    .load(R.drawable.noprofilepic)
                                    .transform(new CircleTransform())
                                    .into(userPic);
                        }
                    } else {
                        Picasso.with(EventActivity.this)
                                .load(R.drawable.noprofilepic)
                                .transform(new CircleTransform())
                                .into(userPic);
                    }
                    participants_container.addView(view);
                }
                three_dots_mmbrs.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);

        if (title != null)
            toolbar.setTitle(title);
        else
            toolbar.setTitle("Messages");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
