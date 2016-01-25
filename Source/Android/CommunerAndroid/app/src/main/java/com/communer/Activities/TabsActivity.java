package com.communer.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.communer.Adapters.MenuAdapter;
import com.communer.Adapters.MenuCommunityAdapter;
import com.communer.Adapters.TabsPagerAdapter;
import com.communer.Fragments.MessagesFragment;
import com.communer.Models.CommentItem;
import com.communer.Models.CommunityContact;
import com.communer.Models.CommunityMember;
import com.communer.Models.CommunityObj;
import com.communer.Models.EventOrMessage;
import com.communer.Models.LocationObj;
import com.communer.Models.MenuIcon;
import com.communer.R;
import com.communer.Utils.AppUser;
import com.communer.Utils.CircleTransform;
import com.communer.Utils.RefreshUtil;
import com.facebook.appevents.AppEventsLogger;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pushwoosh.BasePushMessageReceiver;
import com.pushwoosh.BaseRegistrationReceiver;
import com.pushwoosh.PushManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ���� on 28/07/2015.
 */
public class TabsActivity extends AppCompatActivity {

    public ViewPager myViewPager;
    private TabsPagerAdapter myAdapter;
    private int[] titles;
    private Toolbar toolbar;
    private PagerSlidingTabStrip tabs;

    private ArrayList<MenuIcon> menuItems;
    private LinearLayout drawerContainer;
    DrawerLayout mDrawerLayout;

    private ListView mDrawerList;

    private MenuAdapter menuAdapter;
    private MenuCommunityAdapter communityAdapter;
    private ActionBarDrawerToggle mDrawerToggle;

    boolean doubleBackToExitPressedOnce = false;

    private boolean communityMenu = false;

    private ImageButton menuImage;
    private Button findBtn;
    private TextView community_name, community_location, isGuestText;

    private String Pusher_APP_KEY = "4e300240e08bd9fd936b";
    private int FIND_REQUEST_CODE = 1991;

    private BroadcastReceiver registrationReceiver,pushMessageReceiver;
    private BroadcastReceiver refreshReceiver;

    private String lastCommunityPicked = "";
    private HashMap<String,Integer> menuHash;
    private HashMap<String,Intent> intentsHash;

    private boolean isMainInit = false;

    private int[] menuLastItem = new int[]{
            R.drawable.icon_my_profile,
            R.drawable.icon_community_profile,
            R.drawable.icon_support
    };

    private String[] menuLastItemEnglish = new String[]{
            "My Profile",
            "Community Profile",
            "Support"
    };

    private String[] menuLastItemHebrew = new String[]{
"הפרופיל שלי"       ,
 "פרופיל קהילה"     ,
             "תמיכה"
    };

    public int unreadCount = 0;
    private MessagesFragment messages;

    private SharedPreferences prefs;

    private String mixPanelProjectToken = "537b3567fe348d6c5dfa0d6e2ae688b3";
    private MixpanelAPI mixpanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabsactivity);

        mixpanel = MixpanelAPI.getInstance(TabsActivity.this, mixPanelProjectToken);
        mixpanel.track("Tabs - Creating tabs");
        titles = new int[]{R.string.home_title, R.string.calendar_title, R.string.messages_title, R.string.gallery_title, R.string.explore_title};

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        changeTitle(0);

        prefs = getSharedPreferences("SmartCommunity", Activity.MODE_PRIVATE);

        TextView communityName = (TextView)findViewById(R.id.toolbar_community_name);
        TextView communityLocation = (TextView)findViewById(R.id.toolbar_community_city);
        ImageView communityPic = (ImageView)findViewById(R.id.toolbar_community_pic);

        AppUser appUserInstance = AppUser.getInstance();
        CommunityObj currentCommunity = appUserInstance.getCurrentCommunity();
        communityName.setText(currentCommunity.getCommunityName());
        communityLocation.setText(currentCommunity.getLocation().getTitle());

        lastCommunityPicked = currentCommunity.getCommunityName();
        saveLastCommunity();

        String communityImg = currentCommunity.getCommunityImageUrl();
        if (!communityImg.equals("")){
            Picasso.with(TabsActivity.this)
                    .load(currentCommunity.getCommunityImageUrl())
                    .error(R.drawable.placeholeder_lowres)
                    .transform(new CircleTransform())
                    .into(communityPic);
        }else{
            Picasso.with(TabsActivity.this)
                    .load(R.drawable.placeholeder_lowres)
                    .transform(new CircleTransform())
                    .into(communityPic);
        }

        myAdapter = new TabsPagerAdapter(getSupportFragmentManager(), this);
        myViewPager = (ViewPager) findViewById(R.id.pager);
        myViewPager.setAdapter(myAdapter);
        myViewPager.setOffscreenPageLimit(5);

        resetCommunityUnreadCount();

        initHashesh();
        handleModulesOrder();

        initReceievers();
        registerReceivers();

        initPusher();
        initMenuItems();
        initPushwoosh();

        tabs = (PagerSlidingTabStrip)findViewById(R.id.tabs);
        tabs.setDividerColor(Color.TRANSPARENT);
        tabs.setViewPager(myViewPager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
/*                int alphaCurrent = (int) (255 - (128 * Math.abs(positionOffset)));
                int alphaNext = (int) (128 + (128 * Math.abs(positionOffset)));*/
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2){
                    unreadCount = 0;
                    handleUnreadCount();
                }

                changeTitle(position);
                hideSoftKeyboard();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerContainer = (LinearLayout) findViewById(R.id.drawerContainer);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        ArrayList<CommunityObj> commuinities = appUserInstance.getUserCommunities();

        menuAdapter = new MenuAdapter(menuItems, TabsActivity.this);
        communityAdapter = new MenuCommunityAdapter(commuinities, TabsActivity.this);
        mDrawerList.setAdapter(menuAdapter);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,toolbar, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                View view2 = TabsActivity.this.getCurrentFocus();
                if (view2 != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                }

                if (communityMenu){
                    replaceMenuContent();
                }
            }

            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                View view2 = TabsActivity.this.getCurrentFocus();
                if (view2 != null){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                }
            }
        };

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null){
            selectNormalItem(0,"");
        }

        ImageButton menu_dropdown_community = (ImageButton)findViewById(R.id.menu_dropdown_community);
        findBtn = (Button)findViewById(R.id.find_commnity_btn);

        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(TabsActivity.this, SearchCommunityMenu.class);
                startActivityForResult(mIntent, FIND_REQUEST_CODE);
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            isMainInit = extras.getBoolean("mainInit",false);
            if (isMainInit)
                callGetData();
        }

        handleUnreadCount();
    }

    private void initPushwoosh(){
        Map<String,Object> tagsMap = new HashMap<String, Object>();
        ArrayList<String> communitiesIDs = new ArrayList<String>();
        for (int i=0; i<AppUser.getUserCommunities().size(); i++){
            CommunityObj communityObj = AppUser.getUserCommunities().get(i);
            communitiesIDs.add(communityObj.getCommunityID());
        }
        tagsMap.put("communitiesIDs", communitiesIDs);

        PushManager pushManager = PushManager.getInstance(this);
        pushManager.sendTags(TabsActivity.this,tagsMap,null);
        try{
            pushManager.onStartup(this);
        }catch(Exception e){
            e.printStackTrace();
        }

        pushManager.registerForPushNotifications();
        checkMessage(getIntent());
    }

    private void initMenuItems(){
        menuImage = (ImageButton)findViewById(R.id.menu_community_image);
        community_name = (TextView)findViewById(R.id.menu_community_name);
        community_location = (TextView)findViewById(R.id.menu_community_location);
        isGuestText = (TextView)findViewById(R.id.menu_isguest_indicator);

        AppUser appUserInstance = AppUser.getInstance();
        CommunityObj comm = appUserInstance.getCurrentCommunity();
        String name = comm.getCommunityName();
        String location = comm.getLocation().getTitle();

        community_name.setText(name);
        community_location.setText(location);

        String imageURL = comm.getCommunityImageUrl();
        if (!imageURL.equals("")){
            Picasso.with(TabsActivity.this)
                    .load(imageURL)
                    .into(menuImage);
        }else{
            Picasso.with(TabsActivity.this)
                    .load(R.drawable.placeholeder_lowres)
                    .into(menuImage);
        }

        if (comm.getMemberType().equals("guest")){
            isGuestText.setVisibility(View.VISIBLE);
        }

        menuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceMenuContent();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!communityMenu){
                String menuItemName = menuAdapter.getItem(position).getItemName();
                String language = prefs.getString("last_language_picked", "noData");
                if (language.equals("iw")){
                    menuItemName = getEnglishMenuValues(menuItemName);
                }

                if (menuItemName.equalsIgnoreCase("prayer times"))
                    menuItemName = "Prayer";
                else if (menuItemName.equalsIgnoreCase("discussions"))
                    menuItemName = "Conversation";

                selectNormalItem(position,menuItemName);
            }else{
                selectCommunityItem(position);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FIND_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                boolean added = data.getBooleanExtra("community_added", false);
                if (added)
                    restartActivity();
            }
        }
    }

    private void selectCommunityItem(int position){
        AppUser appUserInstance = AppUser.getInstance();
        CommunityObj community = appUserInstance.getUserCommunities().get(position);

        if (!appUserInstance.getCurrentCommunity().getCommunityID().equals(community.getCommunityID())){
            appUserInstance.setCurrentCommunity(community);

            prefs.edit().putString("last_community_picked", community.getCommunityName()).commit();

            /////I have to disconnect from pusher
            // pusher.disconnect();

            restartActivity();
        }else{
            mDrawerLayout.closeDrawer(drawerContainer);
        }
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectNormalItem(final int position, String itemText) {
        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(drawerContainer);

        if (position != 0){
            final Intent menuIntent = intentsHash.get(itemText);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (menuIntent != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startActivity(menuIntent,
                                    ActivityOptions.makeSceneTransitionAnimation(TabsActivity.this).toBundle());
                        } else {
                            startActivity(menuIntent);
                            overridePendingTransition(R.anim.slide_in_up, R.anim.stay_anim);
                        }
                    }
                }
            }, 250);
        }
    }

    public void changeTitle(int pos) {
        TextView title = (TextView) toolbar.findViewById(R.id.main_toolbar_title);
        title.setText(titles[pos]);
    }

    public void replaceMenuContent(){
        if(!communityMenu){
            mDrawerList.animate()
                    .alpha(0f)
                    .setDuration(250)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mDrawerList.clearAnimation();
                            mDrawerList.setAdapter(null);
                            mDrawerList.setAdapter(communityAdapter);

                            mDrawerList.animate()
                                    .alpha(1f)
                                    .setDuration(250)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    mDrawerList.clearAnimation();
                                }
                            });

                            findBtn.setVisibility(View.VISIBLE);
                            findBtn.animate()
                            .alpha(1f)
                            .setDuration(250)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    findBtn.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
        }else{
            mDrawerList.animate()
                    .alpha(0f)
                    .setDuration(250)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mDrawerList.setAdapter(null);
                            mDrawerList.setAdapter(menuAdapter);

                            mDrawerList.animate()
                                    .alpha(1f)
                                    .setDuration(250)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            mDrawerList.clearAnimation();
                                        }
                                    });

                            findBtn.animate()
                                    .alpha(0f)
                                    .setDuration(250)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            findBtn.setVisibility(View.INVISIBLE);
                                        }
                                    });
                        }
                    });
        }

        communityMenu = !communityMenu;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent setIntent = new Intent(Intent.ACTION_MAIN);
            setIntent.addCategory(Intent.CATEGORY_HOME);
            setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(setIntent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void restartActivity(){
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("mainInit", false);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    private void hideSoftKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void initPusher(){
        Pusher pusher = new Pusher(Pusher_APP_KEY);

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                System.out.println("State changed to " + change.getCurrentState() +
                        " from " + change.getPreviousState());
            }

            @Override
            public void onError(String message, String code, Exception e) {
                System.out.println("There was a problem connecting!");
            }
        }, ConnectionState.ALL);

        AppUser appUserInstance = AppUser.getInstance();
        String pushChannel = appUserInstance.getCurrentCommunity().getPusherChannel();
        Channel channel = pusher.subscribe(pushChannel);

        channel.bind("new_event", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channel, String event, String data) {
                try {
                    Log.d("tabs new events",data);
                    unreadCount++;
                    handleUnreadCount();

                    JSONArray newDailyEvent = new JSONArray(data);
                    for (int i=0; i<newDailyEvent.length(); i++){
                        JSONObject newDailyEventObj = (JSONObject)newDailyEvent.get(i);

                        JSONArray dailyEvents = newDailyEventObj.getJSONArray("events");
                        ArrayList<EventOrMessage> events = parseEvents(dailyEvents);

                        for (int j=0; j<events.size(); j++){
                            final EventOrMessage newEvent = events.get(j);

                            if (messages == null)
                                messages = (MessagesFragment)myAdapter.getFragment(2);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    messages.addEventView(newEvent);
                                }
                            });
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

        channel.bind("New Announcement", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channel, String event, String data) {
                System.out.println("Received message with data: " + data);
                unreadCount++;
                handleUnreadCount();

                try {
                    Log.d("Pusher","Event received: " + data);
                    JSONObject announObj = new JSONObject(data);
                    final EventOrMessage newAnnoun = parseAnnouncement(announObj);

                    if (newAnnoun != null){
                        if (messages == null){
                            messages = (MessagesFragment)myAdapter.getFragment(2);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messages.addAnnouncementView(newAnnoun);
                            }
                        });
                    }

                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });

        pusher.connect();
    }

    private EventOrMessage parseAnnouncement(JSONObject announObj) {
        try {
            String announID = announObj.getString("id");
            String announName = announObj.getString("name");
            long announStartTime = announObj.getLong("sTime");
            long announEndTime = announObj.getLong("eTime");
            String type = announObj.getString("type");

/*                JSONObject announLocation = communitySingleAnnouncement.getJSONObject("location");
                String locTitle = announLocation.getString("title");
                String locCoords = announLocation.getString("coords");
                LocationObj announLoc = new LocationObj(locTitle,locCoords);*/
            LocationObj announLoc = null;

            String announContent = announObj.getString("content");
            String announImageUrl = announObj.getString("imageURL");

            String announReadStatus = announObj.getString("readStatus");
            boolean announIsFree = announObj.getBoolean("isFree");
            boolean eventIsActive = announObj.getBoolean("isActive");
            String announByLink = announObj.getString("byLink");
            String announAttendingStatus = announObj.getString("attendingStatus");

            ArrayList<CommunityContact> participantsArray = new ArrayList<CommunityContact>();
            if (!announObj.isNull("participants")) {
                JSONArray announParticipants = announObj.getJSONArray("participants");
                for (int j = 0; j < announParticipants.length(); j++) {
                    JSONObject participant = announParticipants.getJSONObject(j);
                    String name = participant.getString("name");
                    String position = participant.getString("position");
                    String phoneNumber = participant.getString("phoneNumber");
                    String imageURL = participant.getString("imageURL");
                    String mail = participant.getString("mail");
                    String id = participant.getString("id");

                    participantsArray.add(new CommunityContact(name, phoneNumber, position, imageURL, mail, id));
                }
            }

            JSONArray announComments = announObj.getJSONArray("comments");
            ArrayList<CommentItem> commentsArray = new ArrayList<CommentItem>();
            for (int j = 0; j < announComments.length(); j++) {
                JSONObject comment = announComments.getJSONObject(j);
                String description = comment.getString("description");
                String imageData = comment.getString("imageData");
                String postID = comment.getString("postID");
                String isFromRav = comment.getString("isFromRav");

                long date = 0;
                if (comment.isNull("cd"))
                    date = comment.getLong("cd");
//                    String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                JSONObject member = comment.getJSONObject("member");
                String name = member.getString("name");
                String imageURL = member.getString("imageURL");
                String phone = member.getString("phone");
                String mmbrID = member.getString("id");
                CommunityMember commenter = new CommunityMember(imageURL, name, phone, mmbrID);
                commentsArray.add(new CommentItem(postID, imageData, date, description, commenter));
            }

            EventOrMessage announcement = new EventOrMessage(announID, announName, announStartTime, announEndTime,
                    announLoc, participantsArray, announContent, announImageUrl,
                    announReadStatus, announIsFree, announByLink, announAttendingStatus, commentsArray, type, eventIsActive,"");

            return announcement;
        } catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public  ArrayList<EventOrMessage> parseEvents(JSONArray eventsArray) {
        ArrayList<EventOrMessage> communityEvents = new ArrayList<EventOrMessage>();
        for (int i = 0; i < eventsArray.length(); i++){
            try {
                JSONObject communitySingleEvent = eventsArray.getJSONObject(i);
                String eventID = communitySingleEvent.getString("id");
                String eventName = communitySingleEvent.getString("name");
                long eventStartTime = communitySingleEvent.getLong("sTime");
                long eventEndTime = communitySingleEvent.getLong("eTime");

                JSONObject eventLocation = communitySingleEvent.getJSONObject("location");
                String locTitle = eventLocation.getString("title");
                String locCoords = eventLocation.getString("coords");
                LocationObj eventLoc = new LocationObj(locTitle, locCoords);

                String eventContent = communitySingleEvent.getString("content");
                String eventImageUrl = communitySingleEvent.getString("imageURL");

                String eventReadStatus = communitySingleEvent.getString("readStatus");
                boolean eventIsFree = communitySingleEvent.getBoolean("isFree");
                boolean eventIsActive = communitySingleEvent.getBoolean("isActive");

                String eventByLink = "";
                if (communitySingleEvent.has("byLink"))
                    eventByLink = communitySingleEvent.getString("byLink");

                String eventAttendingStatus = communitySingleEvent.getString("attendingStatus");

                JSONArray eventParticipants = communitySingleEvent.getJSONArray("participants");
                ArrayList<CommunityContact> participantsArray = new ArrayList<CommunityContact>();
                for (int j = 0; j < eventParticipants.length(); j++) {
                    JSONObject participant = eventParticipants.getJSONObject(j);
                    String name = participant.getString("name");
                    String position = participant.getString("position");
                    String phoneNumber = participant.getString("phoneNumber");
                    String imageURL = participant.getString("imageURL");
                    String mail = participant.getString("mail");
                    String id = participant.getString("id");

                    participantsArray.add(new CommunityContact(name, phoneNumber, position, imageURL, mail, id));
                }

                JSONArray eventComments = communitySingleEvent.getJSONArray("comments");
                ArrayList<CommentItem> commentsArray = new ArrayList<CommentItem>();
                for (int j = 0; j < eventComments.length(); j++) {
                    JSONObject comment = eventComments.getJSONObject(j);
                    String description = comment.getString("description");
                    String imageData = comment.getString("imageData");
                    String postID = comment.getString("postID");
                    String isFromRav = comment.getString("isFromRav");
                    Long date = null;
                    if (!comment.isNull("cd"))
                        date = comment.getLong("cd");
//                    String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                    JSONObject member = comment.getJSONObject("member");
                    String name = member.getString("name");
                    String imageURL = member.getString("imageURL");
                    String phone = member.getString("phone");
                    String mmbrID = member.getString("id");
                    CommunityMember commenter = new CommunityMember(imageURL, name, phone, mmbrID);
                    commentsArray.add(new CommentItem(postID, imageData, date, description, commenter));
                }

                communityEvents.add(new EventOrMessage(eventID, eventName, eventStartTime, eventEndTime,
                        eventLoc, participantsArray, eventContent, eventImageUrl,
                        eventReadStatus, eventIsFree, eventByLink, eventAttendingStatus, commentsArray,"event", eventIsActive,""));
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return communityEvents;
    }

    public void handleUnreadCount(){
        View currentTabView = tabs.getTabsContainer().getChildAt(2);
        final TextView unreadView = (TextView)currentTabView.findViewById(R.id.tab_layout_text);

        final int Visibility;
        if (unreadCount == 0){
            Visibility = View.GONE;
        }else{
            Visibility = View.VISIBLE;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                unreadView.setText(String.valueOf(unreadCount));
                unreadView.setVisibility(Visibility);
            }
        });
    }

    public void registerReceivers(){
        IntentFilter intentFilter = new IntentFilter(getPackageName() + ".action.PUSH_MESSAGE_RECEIVE");
        registerReceiver(pushMessageReceiver, intentFilter, getPackageName() + ".permission.C2D_MESSAGE", null);
        registerReceiver(registrationReceiver, new IntentFilter(getPackageName() + "." + PushManager.REGISTER_BROAD_CAST_ACTION));

        IntentFilter filter = new IntentFilter();
        filter.addAction("refresh_done");
        registerReceiver(refreshReceiver, filter);
    }

    public void unregisterReceivers(){
        try {
            unregisterReceiver(registrationReceiver);
        }catch (Exception e) {
            e.printStackTrace();
        }

        try {
            unregisterReceiver(pushMessageReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            unregisterReceiver(refreshReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initReceievers(){
        registrationReceiver = new BaseRegistrationReceiver()
        {
            @Override
            public void onRegisterActionReceive(Context context, Intent intent)
            {
                Log.d("registrationReceiver","Message received");
                checkMessage(intent);
            }
        };

        //Push message receiver
        pushMessageReceiver = new BasePushMessageReceiver()
        {
            @Override
            protected void onMessageReceive(Intent intent)
            {
                Log.d("pushMessageReceiver","Message received");
                if (intent.getExtras().getString(JSON_DATA_KEY) != null){
                    try{
                        JSONObject payload = new JSONObject(intent.getExtras().getString(JSON_DATA_KEY));
                        String userdata = payload.getString("userdata");

                        JSONArray jsonArray = new JSONArray(userdata);
                        JSONObject communityDataObj = jsonArray.getJSONObject(0);

                        ////Change to communityId
                        String comID = communityDataObj.getString("custom");
                        handleInAppPush(comID);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }

                    showMessage("push message is " + intent.getExtras().getString(JSON_DATA_KEY));
            }
        };

        refreshReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra("Status");
                if (status.equals("Success")){
                    handleModulesOrder();
                    menuAdapter = new MenuAdapter(menuItems, TabsActivity.this);
                    mDrawerList.setAdapter(menuAdapter);
                }
            }
        };
    }

    private void handleInAppPush(String comID) {
        if (comID.equals(AppUser.getCurrentCommunity().getCommunityID())){
            unreadCount++;
            handleUnreadCount();
        }else if (prefs != null){
            int communityUnreadMsgsCount = prefs.getInt("communityUnread" + comID,0);
            communityUnreadMsgsCount++;
            prefs.edit().putInt("communityUnread" + comID,communityUnreadMsgsCount).commit();
        }
    }

    private void checkMessage(Intent intent)
    {
        if (null != intent)
        {
            if (intent.hasExtra(PushManager.PUSH_RECEIVE_EVENT))
            {
                showMessage("push message is " + intent.getExtras().getString(PushManager.PUSH_RECEIVE_EVENT));
            }
            else if (intent.hasExtra(PushManager.REGISTER_EVENT))
            {
                showMessage("register");
            }
            else if (intent.hasExtra(PushManager.UNREGISTER_EVENT))
            {
                showMessage("unregister");
            }
            else if (intent.hasExtra(PushManager.REGISTER_ERROR_EVENT))
            {
                showMessage("register error");
            }
            else if (intent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT))
            {
                showMessage("unregister error");
            }

            resetIntentValues();
        }
    }

    private void resetIntentValues()
    {
        Intent mainAppIntent = getIntent();

        if (mainAppIntent.hasExtra(PushManager.PUSH_RECEIVE_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.PUSH_RECEIVE_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.REGISTER_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.REGISTER_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.UNREGISTER_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.REGISTER_ERROR_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.REGISTER_ERROR_EVENT);
        }
        else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT))
        {
            mainAppIntent.removeExtra(PushManager.UNREGISTER_ERROR_EVENT);
        }

        setIntent(mainAppIntent);
    }

    private void resetCommunityUnreadCount() {
        prefs.edit().putInt("communityUnread" + AppUser.getCurrentCommunity().getCommunityID(), 0).commit();
    }

    private void showMessage(String message)
    {
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        checkMessage(intent);
    }

    private void saveLastCommunity(){
        prefs.edit().putString("last_community_picked", lastCommunityPicked);
    }

    private void initHashesh(){
        menuItems = new ArrayList<MenuIcon>();
        menuHash = new HashMap<String,Integer>();
        intentsHash = new HashMap<String,Intent>();

        menuHash.put("Home",R.drawable.icon_home);
        menuHash.put("Facilities",R.drawable.icon_commynity_center);
        menuHash.put("QA",R.drawable.icon_question_answers);
        menuHash.put("Security",R.drawable.icon_security);
        menuHash.put("Conversation",R.drawable.icon_community_conversation);
        menuHash.put("Prayer",R.drawable.icon_prayer);

        intentsHash.put("Home",new Intent(TabsActivity.this, Facilities.class));
        intentsHash.put("Facilities",new Intent(TabsActivity.this, Facilities.class));
        intentsHash.put("QA",new Intent(TabsActivity.this, QuestionsNAnswers.class));
        intentsHash.put("Security",new Intent(TabsActivity.this, Security.class));
        intentsHash.put("Conversation",new Intent(TabsActivity.this, CommunityConversation.class));
        intentsHash.put("My Profile",new Intent(TabsActivity.this, MyProfile.class));
        intentsHash.put("Community Profile",new Intent(TabsActivity.this, CommunityProfile.class));
        intentsHash.put("Support",new Intent(TabsActivity.this, Support.class));
        intentsHash.put("Prayer",new Intent(TabsActivity.this, PrayersTime.class));
    }

    private void handleModulesOrder(){
        if (menuItems.size() > 0)
            menuItems.clear();

        AppUser appUserInstance = AppUser.getInstance();
        ArrayList<String> modules = appUserInstance.getCurrentCommunity().getVisibleModules();

        String language = prefs.getString("last_language_picked", "noData");
        if (language.equals("iw")){
            menuItems.add(new MenuIcon(R.drawable.icon_home, "בית"));
        }else{
            menuItems.add(new MenuIcon(R.drawable.icon_home, "Home"));
        }

        for (int i = 0; i < modules.size(); i++) {
            String module = modules.get(i);
            Integer icon = menuHash.get(module);

            if (language.equals("iw")){
                module = getHebrewMenuValues(module);
            }

            if (module.equalsIgnoreCase("prayer"))
                module = "Prayer Times";
            else if (module.equalsIgnoreCase("conversation"))
                module = "Discussions";

            if (icon != null){
                MenuIcon menuIcon = new MenuIcon(icon,module);
                menuItems.add(menuIcon);
            }
        }

        if (language.equals("iw")){
            for (int i=0; i<menuLastItem.length; i++){
                MenuIcon menuIcon = new MenuIcon(menuLastItem[i],menuLastItemHebrew[i]);
                menuItems.add(menuIcon);
            }
        }else{
            for (int i=0; i<menuLastItem.length; i++){
                MenuIcon menuIcon = new MenuIcon(menuLastItem[i],menuLastItemEnglish[i]);
                menuItems.add(menuIcon);
            }
        }
    }

    private String getHebrewMenuValues(String module){
        switch (module){
            case "Security":
                module = "אבטחה";
                break;

            case "Support":
                module = "תמיכה";
                break;

            case "Prayer":
                module = "תפילות";
                break;

            case "Facilities":
                module = "שירותי קהילה";
                break;

            case "QA":
                module = "שאלות ותשובות";
                break;

            case "Conversation":
                module = "דיון קהילתי";
                break;

            case "My Profile":
                module = "הפרופיל שלי";
                break;

            case "Community Profile":
                module = "פרופיל קהילה";
                break;

            default:
                break;
        }
        return module;
    }

    private String getEnglishMenuValues(String module) {
        switch (module){
            case "אבטחה":
                module = "Security";
                break;

            case "תמיכה":
                module = "Support";
                break;

            case "תפילות":
                module = "Prayer";
                break;

            case "שירותי קהילה":
                module = "Facility";
                break;

            case "שאלות ותשובות":
                module = "QA";
                break;

            case "דיון קהילתי":
                module = "Conversation";
                break;

            case "הפרופיל שלי":
                module = "My Profile";
                break;


            case "פרופיל קהילה":
                module = "Community Profile";
                break;

            default:
                break;
        }
        return module;
    }

    private void callGetData(){
        RefreshUtil refreshUtil = new RefreshUtil(TabsActivity.this);
        refreshUtil.getData("24.323288", "26.135633");
    }

    @Override
    protected void onDestroy(){
        mixpanel.flush();
        unregisterReceivers();
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
