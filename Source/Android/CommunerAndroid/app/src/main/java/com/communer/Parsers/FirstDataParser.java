package com.communer.Parsers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.communer.Application.AppController;
import com.communer.Models.CommunityActivityOrFacility;
import com.communer.Models.CommunityContact;
import com.communer.Models.CommunityMember;
import com.communer.Models.CommunityObj;
import com.communer.Models.DailyEvents;
import com.communer.Models.EventOrMessage;
import com.communer.Models.GuideCategory;
import com.communer.Models.ImageEvent;
import com.communer.Models.LocationObj;
import com.communer.Models.MainModel;
import com.communer.Models.Post;
import com.communer.Models.SecurityEvent;
import com.communer.Utils.AppUser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ���� on 06/08/2015.
 */
public class FirstDataParser {

    private Context mContext;
    private String dataUrl, cameFrom;
    private SharedPreferences prefs;

    public FirstDataParser(Context mContext, String user_hash, String source){
        this.mContext = mContext;
        AppUser appUserInstance = AppUser.getInstance();
        this.dataUrl = appUserInstance.getBaseUrl() + user_hash + "/com.solidPeak.smartcom.requests.application.getFirstTimeData.htm?";
        prefs = mContext.getSharedPreferences("SmartCommunity", Context.MODE_PRIVATE);
        this.cameFrom = source;
    }

    public void getFirstTimeData(String latitude, String longtitude) {
        JSONObject obj = new JSONObject();
        try{
            obj.put("title", "Brooklyn");
            obj.put("coords", latitude + ":" + longtitude);
        }catch (JSONException e){
            e.printStackTrace();
        }

        final String url = dataUrl + "location=" + obj.toString();
        String tag_json_obj = "first_time_data";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        Log.d("Volley", "getFirstTimeData Success");

                        Calendar c = Calendar.getInstance();
                        long currentMilliseconds = c.getTimeInMillis();
                        prefs.edit().putLong("latestGetDataMilliseconds", currentMilliseconds).commit();

                        parseData(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley", "Error: " + error.getMessage());
                String latest_data = prefs.getString("latestCommunerData", "noData");
                if (!latest_data.equals("noData")) {
                    try{
                        Toast.makeText(mContext, "Timeout error, using old data", Toast.LENGTH_SHORT).show();
                        JSONObject obj = new JSONObject(latest_data);
                        parseData(obj);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(mContext, "Timeout error, trying again", Toast.LENGTH_SHORT).show();
                    getFirstTimeData("24.323288", "26.135633");
                }
            }
        });

        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(45000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void parseData(JSONObject result) {
        parseUserDataAndCommunities(result);
        Intent i = new Intent("JSON_Parser_Done");
        i.putExtra("cameFrom", cameFrom);
        mContext.sendBroadcast(i);
    }

    private void parseUserDataAndCommunities(JSONObject jObj) {
        ArrayList<CommunityObj> communitiesArray = new ArrayList<CommunityObj>();

        ArrayList<EventOrMessage> communitiesAnnouncements = new ArrayList<EventOrMessage>();
        ArrayList<CommunityMember> communityMmbrs = new ArrayList<CommunityMember>();

        ArrayList<CommunityContact> communityContacts = new ArrayList<CommunityContact>();
        ArrayList<Post> communityPublicQuestions = new ArrayList<Post>();
        ArrayList<Post> communityPrivateQuestions = new ArrayList<Post>();
        ArrayList<Post> communityPosts = new ArrayList<Post>();

        ArrayList<CommunityActivityOrFacility> communityFacilitiesArray = new ArrayList<CommunityActivityOrFacility>();
        LocationObj communityLoc;

        ArrayList<ImageEvent> communityGalleries = new ArrayList<ImageEvent>();
        ArrayList<SecurityEvent> communitySecurityEvents = new ArrayList<SecurityEvent>();
        ArrayList<GuideCategory> communityGuideCategories = new ArrayList<GuideCategory>();

        ArrayList<DailyEvents> communityDailyEvents = new ArrayList<DailyEvents>();
        ArrayList<String> communityVisibleModules = new ArrayList<>();
        ArrayList<String> communityWidgets = new ArrayList<>();

        MainDataParser mainDataParser = new MainDataParser();
        try {
//            JSONObject userModel = jObj.getJSONObject("userModel");
/*            JSONArray jsonArray = jObj.getJSONArray("Search");
            for (int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

            }*/
            String name = jObj.getString("name");
            String lastName = jObj.getString("lastName");
            String gender = jObj.getString("gender");
            String mStatus = jObj.getString("mStatus");
            String supportMail = jObj.getString("supportMail");
            ArrayList<String> inactiveItems = new ArrayList<String>();

            long bDay = 0;
            if (!jObj.isNull("bDay"))
                bDay = jObj.getLong("bDay");

            String email = jObj.getString("email");
            String imageURL = jObj.getString("imageURL");
            boolean isRabai = jObj.getBoolean("isRabai");

            String userHash = prefs.getString("userHash", "noData");
            if (!userHash.equals("noData")) {
                AppUser.setUserHash(userHash);
            }

            AppUser appUserInstance = AppUser.getInstance();
            appUserInstance.setUserFirstName(name);
            appUserInstance.setUserLastName(lastName);
            appUserInstance.setUserGender(gender);
            appUserInstance.setUserStatus(mStatus);
            appUserInstance.setUserBday(bDay);
            appUserInstance.setUserEmail(email);
            appUserInstance.setUserImageUrl(imageURL);
            appUserInstance.setIsRabai(isRabai);

            CommunityMember mmbr = new CommunityMember(imageURL, name + lastName, "0546337845", "123");
            appUserInstance.setUserAsMember(mmbr);

/*            JSONArray communitiesUrls = jObj.getJSONArray("communitiesUrl");
            for (int i = 0; i <communitiesUrls.length(); i++) {
                String communityDataURL = communitiesUrls.getString(i);
                getCommunityDataWithUrl(communityDataURL,mainDataParser);
            }*/

            JSONArray communitiesJSON = jObj.getJSONArray("communities");
            for (int i = 0; i < communitiesJSON.length(); i++){
                JSONObject community = communitiesJSON.getJSONObject(i);

                String communityID = community.getString("communityID");
                String communityName = community.getString("communityName");
                String communityImageURL = community.getString("communityImageURL");

//                String roofOrg = community.getString("roofOrg");
                String getPush = community.getString("getPush");
                boolean isSynagogue = community.getBoolean("isSynagogue");

                JSONObject location = community.getJSONObject("location");
                String title = location.getString("title");
                String coords = location.getString("coords");
                communityLoc = new LocationObj(title, coords);

                String communityEmail = community.getString("email");
                String communityWebsite = community.getString("website");

                String communityFax = "";
                if (community.has("fax"))
                    communityFax = community.getString("fax");

                String pushTag = "";
                JSONArray pushChannelsArray = community.getJSONArray("pushChannels");
                String pushChannel = "";
                if (pushChannelsArray.length() > 0)
                    pushChannel = pushChannelsArray.getString(0);

                boolean hebrewSupport = community.getBoolean("hebrewSupport");

                JSONArray announcements = community.getJSONArray("announcments");
                communitiesAnnouncements = mainDataParser.parseAnnouncments(announcements);

                JSONArray membersJSON = community.getJSONArray("members");
                communityMmbrs = mainDataParser.parseMembers(membersJSON);

                JSONArray contacts = community.getJSONArray("contacts");
                communityContacts = mainDataParser.parseContacts(contacts);

                JSONArray privateQA = community.getJSONArray("privateQA");
                communityPrivateQuestions = mainDataParser.parsePublicQuestions(privateQA);

                JSONArray publicQA = community.getJSONArray("publicQA");
                communityPublicQuestions = mainDataParser.parsePublicQuestions(publicQA);

                JSONArray comPosts = community.getJSONArray("communityPosts");
                communityPosts = mainDataParser.parseCommunityPosts(comPosts);

                JSONArray facilities = community.getJSONArray("facilities");
                communityFacilitiesArray = mainDataParser.parseFacilities(facilities);

                JSONArray communityEventsImages = community.getJSONArray("eventsImages");
                communityGalleries = mainDataParser.parseGalleryItems(communityEventsImages);

                JSONArray securtyEvents = community.getJSONArray("securityEvents");
                communitySecurityEvents = mainDataParser.parseSecurity(securtyEvents);

                JSONArray guideCategories = community.getJSONArray("guideCategories");
                communityGuideCategories = mainDataParser.parseGuide(guideCategories);
                JSONArray dailyItems = community.getJSONArray("daylyEvents");
                communityDailyEvents = mainDataParser.parseDailyItems(dailyItems);

                JSONArray visibleModules = community.getJSONArray("visibleModules");
                communityVisibleModules = mainDataParser.parseVisibleModules(visibleModules);

                JSONArray widgets = community.getJSONArray("widgets");
                communityWidgets = mainDataParser.parseWidgets(widgets);

                String memberType = "";
                if (community.has("memberType"))
                    memberType = community.getString("memberType");

                int unreadCount = prefs.getInt("communityUnread" + communityID,0);

                CommunityObj tempCommuity = new CommunityObj(communityID, communityName, communityImageURL,
                        communityLoc, memberType, null, isSynagogue, communityMmbrs, communityContacts, communityFax,
                        communityEmail, communityWebsite, communityPrivateQuestions, communityPublicQuestions, communityPosts,
                        communityFacilitiesArray, hebrewSupport, communityGalleries, communitySecurityEvents,
                        communityGuideCategories, communitiesAnnouncements, communityDailyEvents, communityVisibleModules, communityWidgets,pushChannel,pushTag,unreadCount);
                communitiesArray.add(tempCommuity);
            }

            if (!cameFrom.equals("Fragment")) {
                SharedPreferences sp = mContext.getSharedPreferences("SmartCommunity", Activity.MODE_PRIVATE);
                String latestCom = sp.getString("last_community_picked", "noData");

                if (latestCom.equals("noData") || latestCom.equals("")) {
                    if (communitiesJSON.length() > 0)
                        appUserInstance.setCurrentCommunity(communitiesArray.get(0));
                } else {
                    for (int h = 0; h < communitiesArray.size(); h++) {
                        CommunityObj obj = communitiesArray.get(h);
                        String communityname = obj.getCommunityName();
                        if (communityname.equals(latestCom)) {
                            appUserInstance.setCurrentCommunity(obj);
                            break;
                        }
                    }
                }

                appUserInstance.setUserCommunities(communitiesArray);

                MainModel mainModel = new MainModel(name, lastName, gender, mStatus, email, imageURL, supportMail, bDay, communitiesArray, isRabai, inactiveItems);
                saveMainModel(mainModel);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getCommunityDataWithUrl(final String communityDataURL, final MainDataParser mainDataParser) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, communityDataURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){
                Log.d("Volley", "Getting data for community with url " + communityDataURL + " Success");
                parseCommunityDataFromUrl(response,mainDataParser);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley", "Getting data for community with url " + communityDataURL + " Failure");
            }
        });
    }

    private void parseCommunityDataFromUrl(JSONObject community, MainDataParser mainDataParser) {
        try {
            ArrayList<CommunityObj> communitiesArray = new ArrayList<CommunityObj>();

            ArrayList<EventOrMessage> communitiesAnnouncements = new ArrayList<EventOrMessage>();
            ArrayList<CommunityMember> communityMmbrs = new ArrayList<CommunityMember>();

            ArrayList<CommunityContact> communityContacts = new ArrayList<CommunityContact>();
            ArrayList<Post> communityPublicQuestions = new ArrayList<Post>();
            ArrayList<Post> communityPrivateQuestions = new ArrayList<Post>();
            ArrayList<Post> communityPosts = new ArrayList<Post>();

            ArrayList<CommunityActivityOrFacility> communityFacilitiesArray = new ArrayList<CommunityActivityOrFacility>();
            LocationObj communityLoc;

            ArrayList<ImageEvent> communityGalleries = new ArrayList<ImageEvent>();
            ArrayList<SecurityEvent> communitySecurityEvents = new ArrayList<SecurityEvent>();
            ArrayList<GuideCategory> communityGuideCategories = new ArrayList<GuideCategory>();

            ArrayList<DailyEvents> communityDailyEvents = new ArrayList<DailyEvents>();
            ArrayList<String> communityVisibleModules = new ArrayList<>();
            ArrayList<String> communityWidgets = new ArrayList<>();

            String communityID = community.getString("communityID");
            String communityName = community.getString("communityName");
            String communityImageURL = community.getString("communityImageURL");

//                String roofOrg = community.getString("roofOrg");
            String getPush = community.getString("getPush");
            boolean isSynagogue = community.getBoolean("isSynagogue");

            JSONObject location = community.getJSONObject("location");
            String title = location.getString("title");
            String coords = location.getString("coords");
            communityLoc = new LocationObj(title, coords);

            String communityEmail = community.getString("email");
            String communityWebsite = community.getString("website");

            String communityFax = "";
            if (community.has("fax"))
                communityFax = community.getString("fax");

            String pushTag = "";
            JSONArray pushChannelsArray = community.getJSONArray("pushChannels");
            String pushChannel = "";
            if (pushChannelsArray.length() > 0)
                pushChannel = pushChannelsArray.getString(0);

            boolean hebrewSupport = community.getBoolean("hebrewSupport");

            JSONArray announcements = community.getJSONArray("announcments");
            communitiesAnnouncements = mainDataParser.parseAnnouncments(announcements);

            JSONArray membersJSON = community.getJSONArray("members");
            communityMmbrs = mainDataParser.parseMembers(membersJSON);

            JSONArray contacts = community.getJSONArray("contacts");
            communityContacts = mainDataParser.parseContacts(contacts);

            JSONArray privateQA = community.getJSONArray("privateQA");
            communityPrivateQuestions = mainDataParser.parsePublicQuestions(privateQA);

            JSONArray publicQA = community.getJSONArray("publicQA");
            communityPublicQuestions = mainDataParser.parsePublicQuestions(publicQA);

            JSONArray comPosts = community.getJSONArray("communityPosts");
            communityPosts = mainDataParser.parseCommunityPosts(comPosts);

            JSONArray facilities = community.getJSONArray("facilities");
            communityFacilitiesArray = mainDataParser.parseFacilities(facilities);

            JSONArray communityEventsImages = community.getJSONArray("eventsImages");
            communityGalleries = mainDataParser.parseGalleryItems(communityEventsImages);

            JSONArray securtyEvents = community.getJSONArray("securityEvents");
            communitySecurityEvents = mainDataParser.parseSecurity(securtyEvents);

            JSONArray guideCategories = community.getJSONArray("guideCategories");
            communityGuideCategories = mainDataParser.parseGuide(guideCategories);
            JSONArray dailyItems = community.getJSONArray("daylyEvents");
            communityDailyEvents = mainDataParser.parseDailyItems(dailyItems);

            JSONArray visibleModules = community.getJSONArray("visibleModules");
            communityVisibleModules = mainDataParser.parseVisibleModules(visibleModules);

            JSONArray widgets = community.getJSONArray("widgets");
            communityWidgets = mainDataParser.parseWidgets(widgets);

            String memberType = "";
            if (community.has("memberType"))
                memberType = community.getString("memberType");

            int unreadCount = prefs.getInt("communityUnread" + communityID,0);

            CommunityObj tempCommuity = new CommunityObj(communityID, communityName, communityImageURL,
                    communityLoc, memberType, null, isSynagogue, communityMmbrs, communityContacts, communityFax,
                    communityEmail, communityWebsite, communityPrivateQuestions, communityPublicQuestions, communityPosts,
                    communityFacilitiesArray, hebrewSupport, communityGalleries, communitySecurityEvents,
                    communityGuideCategories, communitiesAnnouncements, communityDailyEvents, communityVisibleModules, communityWidgets,pushChannel,pushTag,unreadCount);
            communitiesArray.add(tempCommuity);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void saveMainModel(MainModel mainModel){
        Gson gson = new Gson();
        String mainModelString = gson.toJson(mainModel);
        prefs.edit().putString("latestCommunerData", mainModelString).commit();
    }
}
