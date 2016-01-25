package com.communer.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.communer.Application.AppController;
import com.communer.Models.CommentItem;
import com.communer.Models.CommunityActivityOrFacility;
import com.communer.Models.CommunityContact;
import com.communer.Models.CommunityMember;
import com.communer.Models.CommunityObj;
import com.communer.Models.DailyEvents;
import com.communer.Models.EventOrMessage;
import com.communer.Models.GalleryImage;
import com.communer.Models.GuideCategory;
import com.communer.Models.ImageEvent;
import com.communer.Models.LocationObj;
import com.communer.Models.MainModel;
import com.communer.Models.Post;
import com.communer.Models.PrayerDetails;
import com.communer.Models.SecurityEvent;
import com.communer.Models.SmallPrayer;
import com.communer.Parsers.MainDataParser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by יובל on 20/10/2015.
 */
public class RefreshUtil {

    private Context mContext;
    private SharedPreferences prefs;

    public RefreshUtil(Context mContext) {
        this.mContext = mContext;
        this.prefs = mContext.getSharedPreferences("SmartCommunity", Context.MODE_PRIVATE);
    }

    public class parseUserDataAndCommunities extends AsyncTask <JSONObject,Void,String>{
        @Override
        protected String doInBackground(JSONObject... params) {
            JSONObject jObj = params[0];
            ArrayList<CommunityObj> communitiesArray = new ArrayList<CommunityObj>();

            ArrayList<EventOrMessage> communitiesAnnouncements = new ArrayList<EventOrMessage>();
            ArrayList<CommunityMember> communityMmbrs = new ArrayList<CommunityMember>();

            ArrayList<CommunityContact> communityContacts = new ArrayList<CommunityContact>();
            ArrayList<Post> communityPublicQuestions = new ArrayList<Post>();
            ArrayList<Post> communityPrivateQuestions = new ArrayList<Post>();
            ArrayList<Post> communityPosts = new ArrayList<Post>();

            ArrayList<CommunityActivityOrFacility> communityFacilitiesArray =  new ArrayList<CommunityActivityOrFacility>();
            LocationObj communityLoc;

            ArrayList<ImageEvent> communityGalleries = new ArrayList<ImageEvent>();
            ArrayList<SecurityEvent> communitySecurityEvents = new ArrayList<SecurityEvent>();
            ArrayList<GuideCategory> communityGuideCategories = new ArrayList<GuideCategory>();

            ArrayList<DailyEvents> communityDailyEvents = new ArrayList<DailyEvents>();
            ArrayList<String> communityVisibleModules = new ArrayList<>();
            ArrayList<String> communityWidgets = new ArrayList<>();

            MainDataParser mainDataParser = new MainDataParser();
            try {
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

                AppUser appUserInstance = AppUser.getInstance();
                String userHash = prefs.getString("userHash","noData");
                if (!userHash.equals("noData")){
                    appUserInstance.setUserHash(userHash);
                }

                appUserInstance.setUserFirstName(name);
                appUserInstance.setUserLastName(lastName);
                appUserInstance.setUserGender(gender);
                appUserInstance.setUserStatus(mStatus);
                appUserInstance.setUserBday(bDay);
                appUserInstance.setUserEmail(email);
                appUserInstance.setUserImageUrl(imageURL);
                appUserInstance.setIsRabai(isRabai);

                CommunityMember mmbr = new CommunityMember(imageURL,name + lastName,"0546337845","123");
                appUserInstance.setUserAsMember(mmbr);

                JSONArray communitiesJSON = jObj.getJSONArray("communities");

                for (int i=0; i<communitiesJSON.length(); i++){
                    long startTime = System.currentTimeMillis();
                    boolean communityExists = false;

                    JSONObject community = communitiesJSON.getJSONObject(i);
                    String communityID = community.getString("communityID");

                    for (int j=0; j<communitiesArray.size(); j++){
                        CommunityObj oldCommunity = communitiesArray.get(j);
                        if (oldCommunity.getCommunityID().equals(communityID)){
                            communityExists = true;
                            break;
                        }
                    }

                    if (!communityExists){
                        String communityName = community.getString("communityName");
                        String communityImageURL = community.getString("communityImageURL");

//                String roofOrg = community.getString("roofOrg");
                        String getPush = community.getString("getPush");
                        boolean isSynagogue = community.getBoolean("isSynagogue");

                        JSONObject location = community.getJSONObject("location");
                        String title = location.getString("title");
                        String coords = location.getString("coords");
                        communityLoc = new LocationObj(title,coords);

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

                        int unreadCount = prefs.getInt("communityUnread" + communityID,0);

                        CommunityObj tempCommuity = new CommunityObj(communityID,communityName,communityImageURL,
                                communityLoc,memberType,null,isSynagogue,communityMmbrs,communityContacts,communityFax,
                                communityEmail,communityWebsite,communityPrivateQuestions,communityPublicQuestions,communityPosts,
                                communityFacilitiesArray,hebrewSupport,communityGalleries,communitySecurityEvents,
                                communityGuideCategories,communitiesAnnouncements,communityDailyEvents,communityVisibleModules,communityWidgets,pushChannel,pushTag,unreadCount);
                        communitiesArray.add(tempCommuity);
                    }

                    long stopTime = System.currentTimeMillis();
                    Log.d("Refresh Util","Community parse took " + String.valueOf(stopTime - startTime) + " ms");

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                long startTime = System.currentTimeMillis();
                ArrayList<CommunityObj> mergedCommunities = addData(communitiesArray);
                refreshAppUser(mergedCommunities);
                long stopTime = System.currentTimeMillis();
                Log.d("Refresh Util", "Merging data took " + String.valueOf(stopTime - startTime) + " ms");

                startTime = System.currentTimeMillis();
                ArrayList<CommunityObj> updatedCommunities = appUserInstance.getUserCommunities();
                MainModel mainModel = new MainModel(name,lastName,gender,mStatus,email,imageURL,supportMail,bDay,updatedCommunities,isRabai,inactiveItems);
                saveMainModel(mainModel);
                stopTime = System.currentTimeMillis();
                Log.d("Refresh Util", "Saving data locally took " + String.valueOf(stopTime - startTime) + " ms");

            }catch(JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Intent i = new Intent("refresh_done");
            i.putExtra("Status","Success");
            mContext.sendBroadcast(i);
        }
    }

    private void refreshAppUser(ArrayList<CommunityObj> updatedCommunitiesData) {
        AppUser appUserInstance = AppUser.getInstance();
        ArrayList<CommunityObj> oldCommunitiesData = appUserInstance.getUserCommunities();
        int oldComSize = oldCommunitiesData.size();
        int removeIndex = 0;

        for (int j=0; j<oldComSize; j++){
            CommunityObj communityObj = oldCommunitiesData.get(j - removeIndex);

            for (int z=0; z<updatedCommunitiesData.size(); z++){
                if (updatedCommunitiesData.get(z).getCommunityID().equals(communityObj.getCommunityID())){
                    oldCommunitiesData.remove(j - removeIndex);
                    removeIndex++;
                    break;
                }
            }
        }

        oldCommunitiesData.addAll(updatedCommunitiesData);
        ArrayList<CommunityObj> finalMergedCommunities = oldCommunitiesData;

        appUserInstance.setUserCommunities(finalMergedCommunities);
        for (int j=0; j<updatedCommunitiesData.size(); j++){
            CommunityObj tempComObj = updatedCommunitiesData.get(j);
            if (tempComObj.getCommunityID().equals(appUserInstance.getCurrentCommunity().getCommunityID())){
                appUserInstance.setCurrentCommunity(tempComObj);
                break;
            }
        }
    }

    private void saveMainModel(MainModel mainModel){
        try {
            Gson gson = new Gson();
            String mainModelString = gson.toJson(mainModel);
            prefs.edit().putString("latestCommunerData", mainModelString).commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private ArrayList<CommunityObj> addData(ArrayList<CommunityObj> communitiesArray) {
        ArrayList<CommunityObj> updatedCommunitiesData = new ArrayList<CommunityObj>();
        String last_comm_data = prefs.getString("latestCommunerData", "noData");
        JSONObject obj = null;
        try {
            obj = new JSONObject(last_comm_data);
        }catch (JSONException e) {
            e.printStackTrace();
        }

        if (obj != null){
            for (int i=0; i<communitiesArray.size(); i++){
                CommunityObj oldDataCommunity = null;
                CommunityObj refreshedCommunity = communitiesArray.get(i);

                AppUser appUserInstance = AppUser.getInstance();
                ArrayList<CommunityObj> userCommunities = appUserInstance.getUserCommunities();
                for (int j=0; j<userCommunities.size(); j++){
                    CommunityObj tempCom = userCommunities.get(j);
                    if (tempCom.getCommunityID().equals(refreshedCommunity.getCommunityID())){
                        oldDataCommunity = tempCom;
                        break;
                    }
                }

                if (oldDataCommunity != null){
                    ArrayList<EventOrMessage> newAnnouncements = refreshedCommunity.getAnnouncements();
                    ArrayList<CommunityMember> newMembers = refreshedCommunity.getMembers();
                    ArrayList<CommunityContact> newCommunityContacts = refreshedCommunity.getCommunityContacts();
                    ArrayList<Post> newPublicQA = refreshedCommunity.getPublicQA();
                    ArrayList<Post> newPrivateQA = refreshedCommunity.getPrivateQA();
                    ArrayList<Post> newCommunityPosts = refreshedCommunity.getCommunityPosts();
                    ArrayList<CommunityActivityOrFacility> newFacilities = refreshedCommunity.getFacilities();
                    ArrayList<ImageEvent> newGalleries = refreshedCommunity.getGalleries();
                    ArrayList<SecurityEvent> newSecurityEvents = refreshedCommunity.getSecurityEvents();
                    ArrayList<GuideCategory> newGuideCategories = refreshedCommunity.getGuideCategories();
                    ArrayList<DailyEvents> newDailyEvents = refreshedCommunity.getDailyEvents();
                    ArrayList<String> newWidgets = refreshedCommunity.getWidgets();
                    ArrayList<String> newVisibleModules = refreshedCommunity.getVisibleModules();

                    if (newAnnouncements.size() > 0){
                        ArrayList<EventOrMessage> oldAnnoun = oldDataCommunity.getAnnouncements();
                        ArrayList<EventOrMessage> mergedAnnoun = mergeAnnouncements(oldAnnoun, newAnnouncements);
                        oldDataCommunity.setAnnouncements(mergedAnnoun);
                    }

                    if (newMembers.size() > 0){
                        ArrayList<CommunityMember> oldMembers = oldDataCommunity.getMembers();
                        ArrayList<CommunityMember> mergedMmbrs = mergeMembers(oldMembers, newMembers);
                        oldDataCommunity.setMembers(mergedMmbrs);
                    }

                    if (newCommunityContacts.size() > 0){
                        ArrayList<CommunityContact> oldContacts = oldDataCommunity.getCommunityContacts();
                        ArrayList<CommunityContact> mergedContacts = mergeContacts(oldContacts, newCommunityContacts);
                        oldDataCommunity.setCommunityContacts(mergedContacts);
                    }

                    if (newPublicQA.size() > 0){
                        ArrayList<Post> oldpublicQA = oldDataCommunity.getPublicQA();
                        ArrayList<Post> mergedPublicQA = mergePublicQA(oldpublicQA, newPublicQA);
                        oldDataCommunity.setPublicQA(mergedPublicQA);
                    }

                    if (newPrivateQA.size() > 0){
                        ArrayList<Post> oldPrivateQA = oldDataCommunity.getPrivateQA();
                        ArrayList<Post> mergedPrivateQA = mergePrivateQA(oldPrivateQA, newPrivateQA);
                        oldDataCommunity.setPrivateQA(mergedPrivateQA);
                    }

                    if (newCommunityPosts.size() > 0){
                        ArrayList<Post> oldPosts = oldDataCommunity.getCommunityPosts();
                        ArrayList<Post> mergedPosts = mergePosts(oldPosts, newCommunityPosts);
                        oldDataCommunity.setCommunityPosts(mergedPosts);
                    }

                    if (newFacilities.size() > 0){
                        ArrayList<CommunityActivityOrFacility> oldFacilities = oldDataCommunity.getFacilities();
                        ArrayList<CommunityActivityOrFacility> mergedFacilities = mergeFacilities(oldFacilities, newFacilities);
                        oldDataCommunity.setFacilities(mergedFacilities);
                    }

                    if (newGalleries.size() > 0){
                        ArrayList<ImageEvent> oldGalleries = oldDataCommunity.getGalleries();
                        ArrayList<ImageEvent> mergedGalleries = mergeGalleries(oldGalleries, newGalleries);
                        oldDataCommunity.setGalleries(mergedGalleries);

                    }

                    if (newSecurityEvents.size() > 0){
                        ArrayList<SecurityEvent> oldSecurityEvents = oldDataCommunity.getSecurityEvents();
                        ArrayList<SecurityEvent> mergedSecurityEvents = mergeSecurityEvents(oldSecurityEvents, newSecurityEvents);
                        oldDataCommunity.setSecurityEvents(mergedSecurityEvents);
                    }

                    if (newGuideCategories.size() > 0){
                        ArrayList<GuideCategory> oldGuideCategories = oldDataCommunity.getGuideCategories();
                        ArrayList<GuideCategory> mergedGuideCategories = mergeGuideCategories(oldGuideCategories, newGuideCategories);
                        oldDataCommunity.setGuideCategories(mergedGuideCategories);
                    }

                    if (newDailyEvents.size() > 0){
                        ArrayList<DailyEvents> oldDailyEvents = oldDataCommunity.getDailyEvents();
                        ArrayList<DailyEvents> mergedDailyEvents = mergeDailyEvents(oldDailyEvents, newDailyEvents);
                        oldDataCommunity.setDailyEvents(mergedDailyEvents);
                    }

                    if (newWidgets.size() > 0){
                        ArrayList<String> oldWidgets = oldDataCommunity.getWidgets();
                        ArrayList<String> mergedWidgets = mergeWidgets(oldWidgets, newWidgets);
                        oldDataCommunity.setWidgets(mergedWidgets);
                    }

                    if (newVisibleModules.size() > 0){
                        ArrayList<String> oldVisibleModules = oldDataCommunity.getVisibleModules();
                        ArrayList<String> mergedVisibleModules = mergeVisibleModules(oldVisibleModules, newVisibleModules);
                        oldDataCommunity.setVisibleModules(mergedVisibleModules);
                    }

                    updatedCommunitiesData.add(oldDataCommunity);
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d("Refresh Util","Refresh Done");
        }else{
            Toast.makeText(mContext,"Error refreshing data",Toast.LENGTH_SHORT).show();
        }
        return updatedCommunitiesData;
    }

    public void getData(String latitude, String longtitude){
        AppUser appUserInstance = AppUser.getInstance();
        String userHash = appUserInstance.getUserHash();
        String getDataUrl = appUserInstance.getBaseUrl() + userHash + "/com.solidPeak.smartcom.requests.application.getData.htm?";

        JSONObject obj = new JSONObject();
        try {
            obj.put("title","Brooklyn");
            obj.put("coords",latitude + ":" + longtitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        long lastVisit = prefs.getLong("latestGetDataMilliseconds", 0);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(lastVisit);
        String timeString = String.valueOf(c.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(c.get(Calendar.MINUTE));

        Log.d("Refresh Util","Last Data Request: " + timeString);
        final String url = getDataUrl + "location=" + obj.toString() + "&timestamp=" + String.valueOf(lastVisit);
        String tag_json_obj = "get_data";
        Log.d("Volley", url);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley", "Refreshing data successful");

                        Calendar current = Calendar.getInstance();
                        long currentMilliseconds = current.getTimeInMillis();
                        prefs.edit().putLong("latestGetDataMilliseconds", currentMilliseconds).commit();

                        new parseUserDataAndCommunities().execute(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley", "Error: " + error.getMessage());
                Toast.makeText(mContext,"Refresh failed, check your internet connection and try again",Toast.LENGTH_SHORT).show();

                Intent i = new Intent("refresh_done");
                i.putExtra("Status", "Fail");
                mContext.sendBroadcast(i);
            }
        });

        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private ArrayList<EventOrMessage> mergeAnnouncements(ArrayList<EventOrMessage> oldAnnoun, ArrayList<EventOrMessage> newAnnouncements){
        for (int j=0; j<newAnnouncements.size(); j++){
            EventOrMessage newOne = newAnnouncements.get(j);

            for (int z=0; z<oldAnnoun.size(); z++){
                EventOrMessage oldOne = oldAnnoun.get(z);
                if (oldOne.getEventID().equals(newOne.getEventID())){
                    oldAnnoun.remove(z);
                    break;
                }else{
                    Log.d("Diff:", oldOne.getEventID() + " --- " + newOne.getEventID());
                }
            }
        }

        oldAnnoun.addAll(newAnnouncements);
        return oldAnnoun;
    }

    private ArrayList<CommunityMember> mergeMembers(ArrayList<CommunityMember> oldMembers, ArrayList<CommunityMember> newMembers){
        for (int j=0; j<newMembers.size(); j++){
            CommunityMember newOne = newMembers.get(j);

            for (int z=0; z<oldMembers.size(); z++){
                CommunityMember oldOne = oldMembers.get(z);
                if (oldOne.getId().equals(newOne.getId())){
                    oldMembers.remove(z);
                    break;
                }
            }
        }

        oldMembers.addAll(newMembers);
        return oldMembers;
    }

    private ArrayList<Post> mergePosts(ArrayList<Post> oldPosts, ArrayList<Post> newCommunityPosts) {
        for (int j=0; j<newCommunityPosts.size(); j++){
            Post newOne = newCommunityPosts.get(j);

            for (int z=0; z<oldPosts.size(); z++){
                Post oldOne = oldPosts.get(z);
                if (oldOne.getPostID().equals(newOne.getPostID())){
                    oldPosts.remove(z);
                    break;
                }
            }
        }

        oldPosts.addAll(newCommunityPosts);
        return oldPosts;
    }

    private ArrayList<CommunityActivityOrFacility> mergeFacilities(ArrayList<CommunityActivityOrFacility> oldFacilities, ArrayList<CommunityActivityOrFacility> newFacilities) {
        for (int j=0; j<newFacilities.size(); j++){
            CommunityActivityOrFacility newOne = newFacilities.get(j);

            for (int z=0; z<oldFacilities.size(); z++){
                CommunityActivityOrFacility oldOne = oldFacilities.get(z);
                if (oldOne.getId().equals(newOne.getId())){
                    oldFacilities.remove(z);
                    break;
                }
            }
        }

        oldFacilities.addAll(newFacilities);
        return oldFacilities;
    }

    private ArrayList<CommunityContact> mergeContacts(ArrayList<CommunityContact> oldContacts, ArrayList<CommunityContact> newCommunityContacts) {
        for (int j=0; j<newCommunityContacts.size(); j++){
            CommunityContact newOne = newCommunityContacts.get(j);

            for (int z=0; z<oldContacts.size(); z++){
                CommunityContact oldOne = oldContacts.get(z);
                if (oldOne.getId().equals(newOne.getId())){
                    oldContacts.remove(z);
                    break;
                }
            }
        }

        oldContacts.addAll(newCommunityContacts);
        return oldContacts;
    }

    private ArrayList<Post> mergePublicQA(ArrayList<Post> oldpublicQA, ArrayList<Post> newPublicQA) {
        for (int j=0; j<newPublicQA.size(); j++){
            Post newOne = newPublicQA.get(j);

            for (int z=0; z<oldpublicQA.size(); z++){
                Post oldOne = oldpublicQA.get(z);
                if (oldOne.getPostID().equals(newOne.getPostID())){
                    oldpublicQA.remove(z);
                    break;
                }
            }
        }

        oldpublicQA.addAll(newPublicQA);
        return oldpublicQA;
    }

    private ArrayList<Post> mergePrivateQA(ArrayList<Post> oldPrivateQA, ArrayList<Post> newPrivateQA) {
        for (int j=0; j<newPrivateQA.size(); j++){
            Post newOne = newPrivateQA.get(j);

            for (int z=0; z<oldPrivateQA.size(); z++){
                Post oldOne = oldPrivateQA.get(z);
                if (oldOne.getPostID().equals(newOne.getPostID())){
                    oldPrivateQA.remove(z);
                    break;
                }
            }
        }

        oldPrivateQA.addAll(newPrivateQA);
        return oldPrivateQA;
    }

    private ArrayList<ImageEvent> mergeGalleries(ArrayList<ImageEvent> oldGalleries, ArrayList<ImageEvent> newGalleries) {
        for (int j=0; j<newGalleries.size(); j++){
            boolean foundIDMatch = false;
            ImageEvent newOne = newGalleries.get(j);

            for (int z=0; z<oldGalleries.size(); z++){
                ImageEvent oldOne = oldGalleries.get(z);

                if (oldOne.getGalleryID().equals(newOne.getGalleryID())){
                    foundIDMatch = true;

                    ArrayList<GalleryImage> oldImgs = oldOne.getMedia().getImages();
                    ArrayList<GalleryImage> newImgs = newOne.getMedia().getImages();

                    ArrayList<GalleryImage> finalImages = parseInnerImages(oldImgs,newImgs);
                    newOne.getMedia().setImages(finalImages);

                    oldGalleries.set(z, newOne);
                    break;
                }
            }

            if (!foundIDMatch)
                oldGalleries.add(newOne);
        }

        return oldGalleries;
    }

    private ArrayList<DailyEvents> mergeDailyEvents(ArrayList<DailyEvents> oldDailyEvents, ArrayList<DailyEvents> newDailyEvents) {
        for (int i=0; i<newDailyEvents.size(); i++){
            DailyEvents newOne = newDailyEvents.get(i);

            int oldDailyEventsSize = oldDailyEvents.size();
            boolean dailyEventFound = false;

            for (int j=0; j<oldDailyEventsSize; j++){
                DailyEvents oldOne = oldDailyEvents.get(j);

                if (oldOne.getDate().equals(newOne.getDate())){
//                    Log.d("Equal Date", oldOne.getDate());
                    dailyEventFound = true;
                    boolean oldEventChanged = false;

                    ArrayList<EventOrMessage> newEvents = newOne.getEvents();
                    ArrayList<EventOrMessage> oldEvents = oldOne.getEvents();
                    if (newEvents.size() > 0){
                        ArrayList<EventOrMessage> mergedEvents = parseInnerEvents(oldEvents, newEvents);
                        for (int l=0; l<mergedEvents.size(); l++){
                            EventOrMessage tempEvent = mergedEvents.get(l);

                            for (int f=0; f<oldOne.getEvents().size(); f++){
                                if (oldEvents.get(f).getEventID().equals(tempEvent.getEventID())){
                                    oldEvents.set(f, tempEvent);
                                    break;
                                }
                            }
                        }

                        oldEventChanged = true;
                        oldOne.setEvents(oldEvents);
                    }

                    ArrayList<CommunityActivityOrFacility> newActivies = newOne.getActivities();
                    ArrayList<CommunityActivityOrFacility> oldActivities = oldOne.getActivities();
                    if (newActivies.size() > 0){
                        ArrayList<CommunityActivityOrFacility> mergedActivities = parseInnerActivities(oldActivities, newActivies);
                        for (int l=0; l<mergedActivities.size(); l++){
                            CommunityActivityOrFacility tempActivity = mergedActivities.get(l);

                            for (int f=0; f<oldOne.getActivities().size(); f++){
                                if (oldActivities.get(f).getId().equals(tempActivity.getId())){
                                    oldActivities.set(f, tempActivity);
                                    break;
                                }
                            }
                        }

                        oldEventChanged = true;
                        oldOne.setActivities(oldActivities);
                    }

                    ArrayList<PrayerDetails> newPrayers = newOne.getPrayers();
                    ArrayList<PrayerDetails> oldPrayers = oldOne.getPrayers();
                    if (newPrayers.size() > 0){
                        if (newOne.getDate().equals("26/11/2015"))
                            Log.d("123","123");

                        ArrayList<PrayerDetails> mergedPrayers = parseInnerPrayers(oldPrayers, newPrayers);
                        for (int l=0; l<mergedPrayers.size(); l++){
                            PrayerDetails tempPrayers = mergedPrayers.get(l);

                            for (int f=0; f<oldOne.getPrayers().size(); f++){
                                if (oldPrayers.get(f).getDate() == tempPrayers.getDate()){
                                    oldPrayers.set(f, tempPrayers);
                                    break;
                                }
                            }
                        }
                        oldEventChanged = true;
                        oldOne.setPrayers(oldPrayers);
                    }

                    if (oldEventChanged){
                        oldDailyEvents.set(j,oldOne);
                        break;
                    }
                }
            }

            if (!dailyEventFound){
                oldDailyEvents.add(newOne);
            }
        }

        return oldDailyEvents;
    }

    private ArrayList<EventOrMessage> parseInnerEvents(ArrayList<EventOrMessage> oldEvents, ArrayList<EventOrMessage> newEvents){
        for (int h=0; h<newEvents.size();h++){
            EventOrMessage newEvent = newEvents.get(h);

            EventOrMessage finalEvent = null;
            boolean foundNewEvent = false;
            for (int z=0; z<oldEvents.size(); z++){
                EventOrMessage oldEvent = oldEvents.get(z);
                if (oldEvent.getEventID().equals(newEvent.getEventID())){
                    foundNewEvent = true;
                    finalEvent = newEvent;

                    ArrayList<CommunityContact> newParticipants = newEvent.getParticipants();
                    ArrayList<CommunityContact> oldParticipants = oldEvent.getParticipants();

                    ArrayList<CommentItem> newComments = newEvent.getComments();
                    ArrayList<CommentItem> oldComments = oldEvent.getComments();

                    if (oldParticipants!=null)
                        oldParticipants.addAll(newParticipants);
                    else
                        oldParticipants = newParticipants;

                    if (oldComments!=null)
                        oldComments.addAll(newComments);
                    else
                        oldComments = newComments;

                    finalEvent.setParticipants(oldParticipants);
                    finalEvent.setComments(oldComments);

                    oldEvents.set(z,finalEvent);
                    break;
                }
            }

            if (!foundNewEvent)
                oldEvents.add(newEvent);

        }

        return oldEvents;
    }

    private ArrayList<CommunityActivityOrFacility> parseInnerActivities(ArrayList<CommunityActivityOrFacility> oldActivities, ArrayList<CommunityActivityOrFacility> newActivities){
        for (int h=0; h<newActivities.size();h++) {
            CommunityActivityOrFacility newActivity = newActivities.get(h);

            boolean foundNewActivity = false;

            for (int z=0; z<oldActivities.size(); z++){
                CommunityActivityOrFacility oldActivity = oldActivities.get(z);
                if (oldActivity.getId().equals(newActivity.getId())){
                    foundNewActivity = true;

                    ArrayList<EventOrMessage> newActivityEvents = newActivity.getEvents();
                    ArrayList<EventOrMessage> oldActivityEvents = oldActivity.getEvents();
                    newActivity.setEvents(parseInnerEvents(oldActivityEvents, newActivityEvents));

                    oldActivities.set(z, newActivity);
                    break;
                }
            }

            if (!foundNewActivity)
                oldActivities.add(newActivity);
        }

        return oldActivities;
    }

    private ArrayList<PrayerDetails> parseInnerPrayers(ArrayList<PrayerDetails> oldPrayers, ArrayList<PrayerDetails> newPrayers){
        for (int i=0; i<newPrayers.size();i++) {
            PrayerDetails newPrayer = newPrayers.get(i);

            PrayerDetails finalPrayer = null;
            boolean foundNewPrayer = false;

            for (int j=0; j<oldPrayers.size(); j++){
                PrayerDetails oldPrayer = oldPrayers.get(j);
                if (oldPrayer.getDate().equals(newPrayer.getDate())){
                    foundNewPrayer = true;
                    finalPrayer = newPrayer;

                    ArrayList<SmallPrayer> oldPrayerSmalls = oldPrayer.getShabatPrayers();
                    ArrayList<SmallPrayer> newPrayerSmalls = newPrayer.getShabatPrayers();
                    ArrayList<SmallPrayer> finalSmallPrayers = parseSmallPrayers(oldPrayerSmalls, newPrayerSmalls);

                    ArrayList<SmallPrayer> oldPrimaryPrayers = oldPrayer.getSmallPrayers();
                    ArrayList<SmallPrayer> newPrimaryPrayers = newPrayer.getSmallPrayers();
                    ArrayList<SmallPrayer> finalPrimaryPrayers = parseSmallPrayers(oldPrimaryPrayers, newPrimaryPrayers);

                    finalPrayer.setShabatPrayers(finalSmallPrayers);
                    finalPrayer.setSmallPrayers(finalPrimaryPrayers);
                    break;
                }
            }

            if (!foundNewPrayer)
                oldPrayers.add(newPrayer);
        }

        return oldPrayers;
    }

    private ArrayList<SmallPrayer> parseSmallPrayers(ArrayList<SmallPrayer> oldPrayerSmalls, ArrayList<SmallPrayer> newPrayerSmalls) {
        for (int i=0; i<newPrayerSmalls.size();i++) {
            SmallPrayer newPrayer = newPrayerSmalls.get(i);

            boolean foundNewPrayer = false;
            for (int j = 0; j < oldPrayerSmalls.size(); j++) {
                SmallPrayer oldPrayer = oldPrayerSmalls.get(j);
                if (newPrayer.getId().equals(oldPrayer.getId())){
                    foundNewPrayer = true;
                    oldPrayerSmalls.set(j, newPrayer);

                    break;
                }
            }

            if (!foundNewPrayer)
                oldPrayerSmalls.add(newPrayer);
        }
        return oldPrayerSmalls;
    }

    private ArrayList<GalleryImage> parseInnerImages(ArrayList<GalleryImage> oldImgs, ArrayList<GalleryImage> newImgs) {
        for (int i=0; i<newImgs.size();i++) {
            GalleryImage newImg = newImgs.get(i);

            boolean foundGalleryImage = false;
            for (int j = 0; j < oldImgs.size(); j++) {
                GalleryImage oldImg = oldImgs.get(j);
                if (newImg.getId().equals(oldImg.getId())){
                    foundGalleryImage = true;

                    oldImgs.set(j,newImg);
                }
            }

            if (!foundGalleryImage)
                oldImgs.add(newImg);
        }
        return oldImgs;
    }

    private ArrayList<String> mergeWidgets(ArrayList<String> oldWidgets, ArrayList<String> newWidgets) {
        for (int i=0; i<newWidgets.size();i++) {
            String newWidget = newWidgets.get(i);

            boolean foundWidget = false;
            for (int j = 0; j < oldWidgets.size(); j++) {
                String oldWidget = oldWidgets.get(j);
                if (newWidget.equals(oldWidget)){
                    foundWidget = true;
                    oldWidgets.set(j,newWidget);
                }
            }

            if (!foundWidget)
                oldWidgets.add(newWidget);
        }
        return oldWidgets;
    }

    private ArrayList<String> mergeVisibleModules(ArrayList<String> oldVisibleModules, ArrayList<String> newVisibleModules) {
        for (int i=0; i<newVisibleModules.size();i++) {
            String newVisibleModule = newVisibleModules.get(i);

            boolean foundVisibleModule = false;
            for (int j = 0; j < oldVisibleModules.size(); j++) {
                String oldVisibleModule = oldVisibleModules.get(j);
                if (newVisibleModule.equals(oldVisibleModule)){
                    foundVisibleModule = true;
                    oldVisibleModules.set(j,newVisibleModule);
                }
            }

            if (!foundVisibleModule)
                oldVisibleModules.add(newVisibleModule);
        }
        return oldVisibleModules;
    }

    private ArrayList<GuideCategory> mergeGuideCategories(ArrayList<GuideCategory> oldGuideCategories, ArrayList<GuideCategory> newGuideCategories) {
        for (int i=0; i<newGuideCategories.size();i++) {
            GuideCategory newGuideCategory = newGuideCategories.get(i);

            boolean foundGuideCategory = false;
            for (int j = 0; j < oldGuideCategories.size(); j++) {
                GuideCategory oldGuideCategory = oldGuideCategories.get(j);
                if (newGuideCategory.getId().equals(oldGuideCategory.getId())) {
                    foundGuideCategory = true;
                    oldGuideCategories.set(j,newGuideCategory);
                }
            }

            if (!foundGuideCategory)
                oldGuideCategories.add(newGuideCategory);
        }

        return oldGuideCategories;
    }

    private ArrayList<SecurityEvent> mergeSecurityEvents(ArrayList<SecurityEvent> oldSecurityEvents, ArrayList<SecurityEvent> newSecurityEvents) {
        for (int i=0; i<newSecurityEvents.size();i++) {
            SecurityEvent newSecurityEvent = newSecurityEvents.get(i);

            boolean foundSecurityEvent = false;
            for (int j = 0; j < oldSecurityEvents.size(); j++) {
                SecurityEvent oldSecurityEvent = oldSecurityEvents.get(j);
                if (newSecurityEvent.getId().equals(oldSecurityEvent.getId())) {
                    foundSecurityEvent = true;
                    oldSecurityEvents.set(j,newSecurityEvent);
                }
            }

            if (!foundSecurityEvent)
                oldSecurityEvents.add(newSecurityEvent);
        }

        return oldSecurityEvents;
    }
}
