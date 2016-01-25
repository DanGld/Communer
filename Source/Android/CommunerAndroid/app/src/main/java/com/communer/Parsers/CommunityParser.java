package com.communer.Parsers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

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
import com.communer.Models.PrayerDetails;
import com.communer.Models.SecurityEvent;
import com.communer.Utils.AppUser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by יובל on 15/09/2015.
 */
public class CommunityParser {

    private Context mContext;
    private SharedPreferences prefs;

    public CommunityParser(Context mContext) {
        this.mContext = mContext;
        prefs = mContext.getSharedPreferences("SmartCommunity", Context.MODE_PRIVATE);
    }

    public class parseCommunities extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            String response = params[0];
            CommunityObj theCommuity = null;

            ArrayList<CommunityObj> communitiesArray = new ArrayList<CommunityObj>();

            ArrayList<EventOrMessage> communitiesEvents = new ArrayList<EventOrMessage>();
            ArrayList<EventOrMessage> communitiesAnnouncements = new ArrayList<EventOrMessage>();
            ArrayList<CommunityMember> communityMmbrs = new ArrayList<CommunityMember>();

            ArrayList<CommunityContact> communityContacts = new ArrayList<CommunityContact>();
            ArrayList<Post> communityPublicQuestions = new ArrayList<Post>();
            ArrayList<Post> communityPrivateQuestions = new ArrayList<Post>();
            ArrayList<Post> communityPosts = new ArrayList<Post>();

            ArrayList<CommunityActivityOrFacility> communityFacilitiesArray = new ArrayList<CommunityActivityOrFacility>();
            ArrayList<CommunityActivityOrFacility> communityActivitiesArray = new ArrayList<CommunityActivityOrFacility>();
            LocationObj communityLoc;

            ArrayList<ImageEvent> communityGalleries = new ArrayList<ImageEvent>();
            ArrayList<SecurityEvent> communitySecurityEvents = new ArrayList<SecurityEvent>();
            ArrayList<GuideCategory> communityGuideCategories = new ArrayList<GuideCategory>();
            ArrayList<PrayerDetails> communityParyers = new ArrayList<PrayerDetails>();
            ArrayList<String> communityVisibleModules = new ArrayList<>();
            ArrayList<DailyEvents> communityDailyEvents = new ArrayList<DailyEvents>();
            ArrayList<String> communityWidgets = new ArrayList<>();

            MainDataParser mainDataParser = new MainDataParser();
            try {
                JSONArray comArray = new JSONArray(response);
                JSONObject community = comArray.getJSONObject(0);

                String communityID = community.getString("communityID");
                String communityName = community.getString("communityName");
                String communityImageURL = community.getString("communityImageURL");

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

                String pushTag = "";
                JSONArray pushChannelsArray = community.getJSONArray("pushChannels");
                String pushChannel = "";
                if (pushChannelsArray.length() > 0)
                    pushChannel = pushChannelsArray.getString(0);

                int unreadCount = prefs.getInt("communityUnread" + communityID, 0);

                theCommuity = new CommunityObj(communityID, communityName, communityImageURL,
                        communityLoc, memberType, null, isSynagogue, communityMmbrs, communityContacts, communityFax,
                        communityEmail, communityWebsite, communityPrivateQuestions, communityPublicQuestions, communityPosts,
                        communityFacilitiesArray, hebrewSupport, communityGalleries, communitySecurityEvents,
                        communityGuideCategories, communitiesAnnouncements, communityDailyEvents, communityVisibleModules, communityWidgets,pushChannel,pushTag,unreadCount);

            }catch(JSONException e){
                e.printStackTrace();
            }

            AppUser appUserInstance = AppUser.getInstance();
            ArrayList<CommunityObj> currentCommunities = appUserInstance.getUserCommunities();
            if (currentCommunities == null){
                ArrayList<CommunityObj> communities = new ArrayList<CommunityObj>();
                communities.add(theCommuity);
                appUserInstance.setUserCommunities(communities);
            }else{
                currentCommunities.add(theCommuity);
                appUserInstance.setUserCommunities(currentCommunities);
            }

            appUserInstance.setCurrentCommunity(theCommuity);
            saveToLocalObj();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Intent i = new Intent("Community_Parsing_Done");
            i.putExtra("Status","Success");
            mContext.sendBroadcast(i);
        }
    }

    private void saveToLocalObj() {
        AppUser appUserInstance = AppUser.getInstance();

        String name = appUserInstance.getUserFirstName();
        String lastName = appUserInstance.getUserLastName();
        String gender = appUserInstance.getUserGender();
        String mStatus = appUserInstance.getUserStatus();
        String email = appUserInstance.getUserEmail();
        String imageURL = appUserInstance.getUserImageUrl();
        String supportMail = appUserInstance.getSupportMail();
        long bDay = appUserInstance.getUserBday();
        ArrayList<CommunityObj> communitiesArray = appUserInstance.getUserCommunities();
        boolean isRabai = appUserInstance.isRabai();
        ArrayList<String> inactiveItems = new ArrayList<String>();

        MainModel mainModel = new MainModel(name,lastName,gender,mStatus,email,imageURL,supportMail,bDay,communitiesArray,isRabai,inactiveItems);
        Gson gson = new Gson();
        String mainModelString = gson.toJson(mainModel);

        prefs.edit().putString("latestCommunerData", mainModelString).commit();
    }

    public void parseData(String result){
        new parseCommunities().execute(result);
    }

}
