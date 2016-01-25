package com.communer.Parsers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

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
import com.communer.Models.MediaData;
import com.communer.Models.Post;
import com.communer.Models.PrayerDetails;
import com.communer.Models.ReportItem;
import com.communer.Models.SecurityEvent;
import com.communer.Models.SmallPrayer;
import com.communer.Utils.AppUser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by יובל on 25/10/2015.
 */
public class LocalDataParser {

    private Context mContext;
    private String cameFrom;
    private SharedPreferences prefs;

    public LocalDataParser(Context mContext, String came_from) {
        this.mContext = mContext;
        this.cameFrom = came_from;
        this.prefs = mContext.getSharedPreferences("SmartCommunity", Context.MODE_PRIVATE);
    }

    public class parseUserDataAndCommunities extends AsyncTask<JSONObject, Void, String> {

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

            try {
                String name = jObj.getString("fistName");
                String lastName = jObj.getString("lastName");
                String gender = jObj.getString("gender");
                String mStatus = jObj.getString("mStatus");
                long bDay = jObj.getLong("birthday");
                String supportMail = jObj.getString("supportMail");
                String email = jObj.getString("email");
                String imageURL = jObj.getString("imageURL");
                boolean isRabai = jObj.getBoolean("isRabai");
                String userHash = prefs.getString("userHash", "noData");
                ArrayList<String> inactiveItems = new ArrayList<String>();

                AppUser appUserInstance = AppUser.getInstance();
                appUserInstance.setUserHash(userHash);
                appUserInstance.setUserFirstName(name);
                appUserInstance.setUserLastName(lastName);
                appUserInstance.setUserGender(gender);
                appUserInstance.setUserStatus(mStatus);
                appUserInstance.setUserBday(bDay);
                appUserInstance.setUserEmail(email);
                appUserInstance.setUserImageUrl(imageURL);
                appUserInstance.setIsRabai(isRabai);
                appUserInstance.setSupportMail(supportMail);

                CommunityMember mmbr = new CommunityMember(imageURL,name + lastName,"0546337845", "123");
                appUserInstance.setUserAsMember(mmbr);

                JSONArray communitiesJSON = jObj.getJSONArray("communitiesArray");

                for (int i=0; i<communitiesJSON.length(); i++){
                    long startTime = System.currentTimeMillis();
                    JSONObject community = communitiesJSON.getJSONObject(i);

                    String communityID = community.getString("communityID");
                    String communityName = community.getString("communityName");
                    String communityImageURL = community.getString("communityImageUrl");

                    boolean isSynagogue = community.getBoolean("isSynagogue");

                    JSONObject location = community.getJSONObject("location");
                    String title = location.getString("title");
                    String coords = location.getString("cords");
                    communityLoc = new LocationObj(title,coords);

                    String communityEmail = community.getString("email");
                    String communityWebsite = community.getString("website");
                    String communityFax = community.getString("fax");
                    boolean hebrewSupport = community.getBoolean("hebrewSupport");

                    JSONArray announcements = community.getJSONArray("announcements");
                    communitiesAnnouncements = parseAnnouncments(announcements);

                    JSONArray membersJSON = community.getJSONArray("members");
                    communityMmbrs = parseMembers(membersJSON);

                    JSONArray contacts = community.getJSONArray("communityContacts");
                    communityContacts = parseContacts(contacts);

                    JSONArray privateQA = null;
                    if (community.has("privateQA")){
                        privateQA = community.getJSONArray("privateQA");
                        communityPrivateQuestions = parsePublicQuestions(privateQA);
                    }

                    JSONArray publicQA = community.getJSONArray("publicQA");
                    communityPublicQuestions = parsePublicQuestions(publicQA);

                    JSONArray comPosts = community.getJSONArray("communityPosts");
                    communityPosts = parseCommunityPosts(comPosts);

                    JSONArray facilities = community.getJSONArray("facilities");
                    communityFacilitiesArray = parseFacilities(facilities);

                    JSONArray communityEventsImages = community.getJSONArray("galleries");
                    communityGalleries = parseGalleryItems(communityEventsImages);

                    JSONArray securtyEvents = community.getJSONArray("securityEvents");
                    communitySecurityEvents = parseSecurity(securtyEvents);

                    JSONArray guideCategories = community.getJSONArray("guideCategories");
                    communityGuideCategories = parseGuide(guideCategories);

                    JSONArray dailyItems = community.getJSONArray("dailyEvents");
                    communityDailyEvents = parseDailyItems(dailyItems);

                    JSONArray visibleModules = community.getJSONArray("visibleModules");
                    communityVisibleModules = parseVisibleModules(visibleModules);

                    JSONArray widgets = community.getJSONArray("widgets");
                    communityWidgets = parseWidgets(widgets);

                    String memberType = "";
                    if (community.has("memberType"))
                        memberType = community.getString("memberType");

                    boolean isHebrewSupport = community.getBoolean("hebrewSupport");

                    String pushTag = "";
                    String pushChannel = community.getString("pusherChannel");

                    int unreadCount = prefs.getInt("communityUnread" + communityID, 0);

                    CommunityObj tempCommuity = new CommunityObj(communityID,communityName,communityImageURL,
                            communityLoc,memberType,null,isSynagogue,communityMmbrs,communityContacts,communityFax,
                            communityEmail,communityWebsite,communityPrivateQuestions,communityPublicQuestions,communityPosts,
                            communityFacilitiesArray,isHebrewSupport,communityGalleries,communitySecurityEvents,
                            communityGuideCategories,communitiesAnnouncements,communityDailyEvents,communityVisibleModules,communityWidgets,pushChannel,pushTag,unreadCount);
                    communitiesArray.add(tempCommuity);

                    long stopTime = System.currentTimeMillis();
                    Log.d("Local Data Parser", "Community parse took " + String.valueOf(stopTime - startTime) + " ms");
                    try{
                        Thread.sleep(100);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                SharedPreferences sp = mContext.getSharedPreferences("SmartCommunity", Activity.MODE_PRIVATE);
                String latestCom = sp.getString("last_community_picked","noData");

                if (latestCom.equals("noData") || latestCom.equals("")){
                    if (communitiesJSON.length() > 0)
                        appUserInstance.setCurrentCommunity(communitiesArray.get(0));
                }else{
                    for (int h=0; h<communitiesArray.size(); h++){
                        CommunityObj obj = communitiesArray.get(h);
                        String communityname = obj.getCommunityName();
                        if (communityname.equals(latestCom)){
                            appUserInstance.setCurrentCommunity(obj);
                            break;
                        }
                    }
                }

                appUserInstance.setUserCommunities(communitiesArray);
                MainModel mainModel = new MainModel(name,lastName,gender,mStatus,email,imageURL,supportMail,bDay,communitiesArray,isRabai,inactiveItems);
                saveMainModel(mainModel);

            }catch (JSONException e) {
                e.printStackTrace();
            }

            return "success";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Intent i = new Intent("JSON_Parser_Done");
            i.putExtra("cameFrom", cameFrom);
            mContext.sendBroadcast(i);
        }
    }


    private ArrayList<EventOrMessage> parseAnnouncments(JSONArray announcementsArray) {
        ArrayList<EventOrMessage> communityAnnouncements = new ArrayList<EventOrMessage>();
        for (int i=0;i<announcementsArray.length();i++){
            try {
                JSONObject communitySingleAnnouncement = announcementsArray.getJSONObject(i);
                String announID = communitySingleAnnouncement.getString("eventID");
                String announName = communitySingleAnnouncement.getString("name");
                long announStartTime = communitySingleAnnouncement.getLong("sTime");
                long announEndTime = communitySingleAnnouncement.getLong("eTime");
                String type = communitySingleAnnouncement.getString("type");

                LocationObj announLoc = null;

                String announContent = communitySingleAnnouncement.getString("content");
                String announImageUrl = communitySingleAnnouncement.getString("imageUrl");

                String announReadStatus = communitySingleAnnouncement.getString("readStatus");
                boolean announIsFree = communitySingleAnnouncement.getBoolean("isFree");
                boolean eventIsActive = communitySingleAnnouncement.getBoolean("isActive");
                String announByLink = communitySingleAnnouncement.getString("buyLink");
                String announAttendingStatus = communitySingleAnnouncement.getString("attendingStatus");

                ArrayList<CommunityContact> participantsArray = new ArrayList<CommunityContact>();
                if (!communitySingleAnnouncement.isNull("participants")){
                    JSONArray announParticipants = communitySingleAnnouncement.getJSONArray("participants");
                    for (int j=0; j<announParticipants.length(); j++){
                        JSONObject participant = announParticipants.getJSONObject(j);
                        String name = participant.getString("name");
                        String position = participant.getString("position");
                        String phoneNumber = participant.getString("phoneNumber");
                        String imageURL = participant.getString("imageURL");
                        String mail = participant.getString("mail");
                        String id = participant.getString("id");

                        participantsArray.add(new CommunityContact(name,phoneNumber,position,imageURL,mail,id));
                    }
                }

                JSONArray announComments = communitySingleAnnouncement.getJSONArray("comments");
                ArrayList<CommentItem> commentsArray = new ArrayList<CommentItem>();
                for (int j=0; j<announComments.length(); j++){
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
                    CommunityMember commenter = new CommunityMember(imageURL,name,phone,mmbrID);
                    commentsArray.add(new CommentItem(postID,imageData,date,description,commenter));
                }

                communityAnnouncements.add(new EventOrMessage(announID,announName,announStartTime,announEndTime,
                        announLoc,participantsArray, announContent,announImageUrl,
                        announReadStatus,announIsFree,announByLink,announAttendingStatus,commentsArray,type,eventIsActive,""));
            }catch(JSONException e){
                e.printStackTrace();
            }
        }

        ArrayList<EventOrMessage> orderedAnnoun = new ArrayList<EventOrMessage>();
        for (int i=0; i<communityAnnouncements.size(); i++){
            EventOrMessage tempAnnoun = communityAnnouncements.get(i);
            boolean isActive = tempAnnoun.isActive();

            if (isActive){
                long sTime = tempAnnoun.getsTime();

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(sTime);
                Date eventSDate = cal.getTime();

                if (orderedAnnoun.size() == 0) {
                    orderedAnnoun.add(tempAnnoun);
                }else {
                    int tempAnnounSize = orderedAnnoun.size();
                    boolean addedItem = false;

                    for (int j = 0; j < tempAnnounSize; j++) {
                        EventOrMessage prevAnnoun = orderedAnnoun.get(j);

                        long announDateLong = prevAnnoun.getsTime();
                        Calendar announCal = Calendar.getInstance();
                        announCal.setTimeInMillis(announDateLong);
                        Date announDate = announCal.getTime();

                        if (eventSDate.after(announDate)){
                            orderedAnnoun.add(j, tempAnnoun);
                            addedItem = true;
                        }

                        if (addedItem)
                            break;
                    }

                    if (!addedItem)
                        orderedAnnoun.add(tempAnnoun);
                }
            }
        }

        communityAnnouncements = orderedAnnoun;
        return communityAnnouncements;
    }

    private ArrayList<CommunityMember> parseMembers(JSONArray membersJSON) {
        ArrayList<CommunityMember> mmbrs = new ArrayList<CommunityMember>();
        try {
            for (int j=0;j<membersJSON.length(); j++){
                JSONObject member = membersJSON.getJSONObject(j);
                String memberImg = member.getString("imageUrl");
                String memberName = member.getString("name");
                String memberPhone = member.getString("phone");
                String mmbrID = member.getString("id");

                mmbrs.add(new CommunityMember(memberImg,memberName,memberPhone,mmbrID));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return mmbrs;
    }

    private ArrayList<CommunityContact> parseContacts(JSONArray contacts) {
        ArrayList<CommunityContact> cntctsArray = new ArrayList<CommunityContact>();
        try {
            for (int j=0;j<contacts.length(); j++){
                JSONObject contact = contacts.getJSONObject(j);
                String contactName = contact.getString("Name");
                String contactPosition = contact.getString("Position");
                String contactPhone = contact.getString("PhoneNumber");
                String contactImage = contact.getString("ImageUrl");
                String contactMail = contact.getString("mail");
                String contactID = contact.getString("id");

                cntctsArray.add(new CommunityContact(contactName,contactPhone,contactPosition,contactImage,contactMail,contactID));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return cntctsArray;
    }

    private ArrayList<Post> parsePublicQuestions(JSONArray publicQA) {
        ArrayList<Post> publicQAs = new ArrayList<Post>();
        try {
            for (int i=0;i<publicQA.length(); i++){
                JSONObject qa = publicQA.getJSONObject(i);

                String qaTitle = qa.getString("title");
                String qaImage = qa.getString("imageUrl");
                String qaContent = qa.getString("content");
                Long qaCD = qa.getLong("CDate");
                String qaPostID = qa.getString("postID");

                JSONObject qaMember = qa.getJSONObject("member");
                String mmbrName = qaMember.getString("name");
                String mmbrImage = qaMember.getString("imageUrl");
                String mmbrPhone = qaMember.getString("phone");
                String mmbrID = qaMember.getString("id");

                CommunityMember posterMember = new CommunityMember(mmbrImage,mmbrName,mmbrPhone,mmbrID);

                JSONArray qaPostComments = qa.getJSONArray("comments");
                ArrayList<CommentItem> commentsArray = new ArrayList<CommentItem>();
                for (int j=0; j<qaPostComments.length(); j++){
                    JSONObject comment = qaPostComments.getJSONObject(j);
                    String description = comment.getString("content");
                    String imageData = comment.getString("imageUrl");
                    String postID = comment.getString("postID");
                    Long date = comment.getLong("date");

                    JSONObject member = comment.getJSONObject("mmbrCommented");
                    String name = member.getString("name");
                    String imageURL = member.getString("imageUrl");
                    String phone = member.getString("phone");
                    String postMmbrID = member.getString("id");

                    CommunityMember commenter = new CommunityMember(imageURL,name,phone,postMmbrID);
                    commentsArray.add(new CommentItem(postID,imageData,date,description,commenter));
                }
                publicQAs.add(new Post(qaPostID,qaTitle,qaImage,posterMember,qaCD,qaContent,commentsArray));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return publicQAs;
    }

    private ArrayList<Post> parseCommunityPosts(JSONArray comPosts) {
        ArrayList<Post> posts = new ArrayList<Post>();
        try {
            for (int i=0;i<comPosts.length(); i++) {
                JSONObject post = comPosts.getJSONObject(i);
                String postTitle = post.getString("title");
                String postImage = post.getString("imageUrl");
                String postContent = post.getString("content");
                Long postCD = post.getLong("CDate");

                String postID = post.getString("postID");

                JSONObject postMember = post.getJSONObject("member");
                String mmbrName = postMember.getString("name");
                String mmbrImage = postMember.getString("imageUrl");
                String mmbrPhone = postMember.getString("phone");
                String postMmbrID = postMember.getString("id");

                CommunityMember posterMember = new CommunityMember(mmbrImage, mmbrName, mmbrPhone,postMmbrID);

                JSONArray postPostComments = post.getJSONArray("comments");
                ArrayList<CommentItem> postsArray = new ArrayList<CommentItem>();
                for (int j = 0; j < postPostComments.length(); j++) {
                    JSONObject comment = postPostComments.getJSONObject(j);

                    String description = comment.getString("content");
                    String imageData = comment.getString("imageUrl");
                    String postIDComment = comment.getString("postID");
                    Long date = comment.getLong("date");

                    JSONObject member = null;
                    if (!comment.isNull("mmbrCommented"))
                        member = comment.getJSONObject("mmbrCommented");

                    CommunityMember commenter = null;
                    if (member!=null){
                        String name = member.getString("name");
                        String imageURL = member.getString("imageUrl");
                        String phone = member.getString("phone");
                        String mmbrID = postMember.getString("id");

                        commenter = new CommunityMember(imageURL, name, phone,mmbrID);
                    }

                    postsArray.add(new CommentItem(postIDComment, imageData, date, description, commenter));
                }
                posts.add(new Post(postID, postTitle, postImage, posterMember, postCD, postContent, postsArray));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return posts;
    }

    private ArrayList<CommunityActivityOrFacility> parseFacilities(JSONArray facilities) {
        ArrayList<CommunityActivityOrFacility> tempFacilities = new ArrayList<CommunityActivityOrFacility>();

        try {
            for (int z=0; z<facilities.length(); z++){
                JSONObject communityFacility = facilities.getJSONObject(z);
                String facilityBigImage = communityFacility.getString("bigImageUrl");
                String facilityName = communityFacility.getString("name");
                String facilityDescription = communityFacility.getString("description");
                String facilityID = communityFacility.getString("id");

                JSONObject facilityLocation = communityFacility.getJSONObject("locationObj");
                String facilityLocationTitle = facilityLocation.getString("title");
                String facilityLocationCoords = facilityLocation.getString("cords");
                LocationObj facilityLoc = new LocationObj(facilityLocationTitle,facilityLocationCoords);

                ArrayList<EventOrMessage> facilEvents = new ArrayList<EventOrMessage>();
                JSONArray facilityEvents = communityFacility.getJSONArray("events");
                for (int h=0;h<facilityEvents.length();h++){
                    JSONObject facilityEvent = facilityEvents.getJSONObject(h);
                    String eventID = facilityEvent.getString("eventID");
                    String eventName = facilityEvent.getString("name");
                    long eventStartTime = facilityEvent.getLong("sTime");
                    long eventEndTime = facilityEvent.getLong("eTime");

//                    String eventLocation = facilityEvent.getString("locationObj");
                    String eventContent = facilityEvent.getString("content");
                    String eventImageUrl = facilityEvent.getString("imageUrl");

                    String eventReadStatus = facilityEvent.getString("readStatus");
                    boolean eventIsFree = facilityEvent.getBoolean("isFree");
                    boolean eventIsActive = facilityEvent.getBoolean("isActive");
                    String eventByLink = facilityEvent.getString("buyLink");

                    String eventAttendingStatus = facilityEvent.getString("attendingStatus");
/*                    String eventParticipants = facilityEvent.getString("participants");
                    String eventComments = facilityEvent.getString("comments");*/

                    facilEvents.add(new EventOrMessage(eventID,eventName,eventStartTime,eventEndTime,null,null,eventContent,
                            eventImageUrl,eventReadStatus,eventIsFree,eventByLink,eventAttendingStatus,null,"event",eventIsActive,""));
                }

                CommunityContact communityCon = null;
                if (!communityFacility.isNull("communityContact")){
                    JSONObject facilityContact = communityFacility.getJSONObject("communityContact");
                    String facilityContactName = facilityContact.getString("Name");
                    String facilityPositionName = facilityContact.getString("Position");
                    String facilityPhoneNumber = facilityContact.getString("PhoneNumber");
                    String facilityImageUrl = facilityContact.getString("ImageUrl");
                    String facilityContactMail = facilityContact.getString("mail");
                    String facilityContactID = facilityContact.getString("id");
                    communityCon = new CommunityContact(facilityContactName,facilityPhoneNumber,facilityPositionName,facilityImageUrl,facilityContactMail,facilityContactID);
                }

                ArrayList<String> facilImages = new ArrayList<String>();
                JSONArray facilityImages = communityFacility.getJSONArray("images");
                for (int h = 0; h < facilityImages.length(); h++) {
                    String image = facilityImages.getString(h);
                    facilImages.add(image);
                }

                tempFacilities.add(new CommunityActivityOrFacility(facilityBigImage,facilityName,
                        facilityDescription,null,facilityLoc,facilEvents,facilImages,communityCon,facilityID));
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        return tempFacilities;
    }

    private ArrayList<ImageEvent> parseGalleryItems(JSONArray communityEventsImages) {
        ArrayList<ImageEvent> Galleries = new ArrayList<ImageEvent>();
        ArrayList<GalleryImage> galleryImagesArray;

        try {
            for (int i=0;i<communityEventsImages.length();i++){
                galleryImagesArray = new ArrayList<GalleryImage>();
                JSONObject gallery = communityEventsImages.getJSONObject(i);
                String communityID = gallery.getString("galleryID");
                String eventTitle = gallery.getString("galleryTitle");
                String galleryImage = gallery.getString("imageUrl");
                JSONObject galleryMedia = gallery.getJSONObject("media");
                JSONArray galleryImages = galleryMedia.getJSONArray("images");

                for (int j=0;j<galleryImages.length();j++){
                    JSONObject imageItem = galleryImages.getJSONObject(j);
                    String id = imageItem.getString("id");
                    String imageLink = imageItem.getString("url");
//                    JSONArray imageComments = imageItem.getJSONArray("imageComments");

                    galleryImagesArray.add(new GalleryImage(id,imageLink));
                }

                MediaData galleryFinalMedia = new MediaData(galleryImagesArray,null);
                Galleries.add(new ImageEvent(galleryImage,eventTitle,communityID,galleryFinalMedia));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return Galleries;
    }

    private ArrayList<SecurityEvent> parseSecurity(JSONArray securityEvents){
        ArrayList<SecurityEvent> events = new ArrayList<SecurityEvent>();
        try {
            for (int i=0;i<securityEvents.length();i++){
                JSONObject securityEvent = securityEvents.getJSONObject(i);
                String id = securityEvent.getString("id");
                String reporterName = securityEvent.getString("reporterName");
                JSONObject reportObj = securityEvent.getJSONObject("report");

                JSONObject reportLoc = reportObj.getJSONObject("locationObj");
                String locTitle = reportLoc.getString("title");
                String coords = reportLoc.getString("cords");
                LocationObj reportLocaion = new LocationObj(locTitle,coords);

                String type = reportObj.getString("type");
                String description = reportObj.getString("description");
                String severity = reportObj.getString("severity");
                boolean isPoliceReport = reportObj.getBoolean("isPoliceReport");
                boolean isAnnonimus = reportObj.getBoolean("isAnnonymous");
                ReportItem theReport = new ReportItem(reportLocaion,type,description,isPoliceReport,isAnnonimus,severity);

                if (!coords.contains("null"))
                    events.add(new SecurityEvent(id,theReport, reporterName));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return events;
    }

    private ArrayList<GuideCategory> parseGuide(JSONArray guideCategories) {
        ArrayList<GuideCategory> tempGuide = new ArrayList<GuideCategory>();
        try {
            for (int i=0;i<guideCategories.length();i++){
                JSONObject singleGuide = guideCategories.getJSONObject(i);
                String id = singleGuide.getString("id");
                String title = singleGuide.getString("title");
                String imageURL = singleGuide.getString("imageUrl");

                tempGuide.add(new GuideCategory(id,title,imageURL));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return tempGuide;
    }

    private ArrayList<DailyEvents> parseDailyItems(JSONArray dailyItems) {
        ArrayList<DailyEvents> tempDaily = new ArrayList<DailyEvents>();

        try {
            for (int i=0; i<dailyItems.length(); i++){
                JSONObject dailyObj = dailyItems.getJSONObject(i);
                String date = dailyObj.getString("date");

                JSONArray dailyEvents = dailyObj.getJSONArray("events");
                ArrayList<EventOrMessage> events = parseEvents(dailyEvents,date);

                JSONArray dailyActivities =  dailyObj.getJSONArray("activities");
                ArrayList<CommunityActivityOrFacility> activities = parseActivities(dailyActivities);

                JSONArray dailyPrayers =  dailyObj.getJSONArray("prayers");
                ArrayList<PrayerDetails> prayers = parsePrayers(dailyPrayers);

                tempDaily.add(new DailyEvents(date, events, activities,prayers));
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        return tempDaily;
    }

    private ArrayList<EventOrMessage> parseEvents(JSONArray eventsArray, String dailyEventDate){
        ArrayList<EventOrMessage> communityEvents = new ArrayList<EventOrMessage>();
        for (int i=0;i<eventsArray.length();i++){
            try {

                JSONObject communitySingleEvent = eventsArray.getJSONObject(i);
                String eventID = communitySingleEvent.getString("eventID");
                String eventName = communitySingleEvent.getString("name");
                long eventStartTime = communitySingleEvent.getLong("sTime");
                long eventEndTime = communitySingleEvent.getLong("eTime");

                LocationObj eventLoc = null;
                if (communitySingleEvent.has("locationObj")){
                    JSONObject eventLocation = communitySingleEvent.getJSONObject("locationObj");
                    String locTitle = eventLocation.getString("title");
                    String locCoords = eventLocation.getString("cords");
                    eventLoc = new LocationObj(locTitle,locCoords);
                }

                String eventContent = communitySingleEvent.getString("content");
                String eventImageUrl = communitySingleEvent.getString("imageUrl");

                String eventReadStatus = communitySingleEvent.getString("readStatus");
                boolean eventIsFree = communitySingleEvent.getBoolean("isFree");
                boolean eventIsActive = communitySingleEvent.getBoolean("isActive");
                String eventByLink = communitySingleEvent.getString("buyLink");
                String eventAttendingStatus = communitySingleEvent.getString("attendingStatus");

                JSONArray eventParticipants = communitySingleEvent.getJSONArray("participants");
                ArrayList<CommunityContact> participantsArray = new ArrayList<CommunityContact>();
                for (int j=0; j<eventParticipants.length(); j++){
                    JSONObject participant = eventParticipants.getJSONObject(j);
                    String name = participant.getString("Name");
                    String position = participant.getString("Position");
                    String phoneNumber = participant.getString("PhoneNumber");
                    String imageURL = participant.getString("ImageUrl");
                    String mail = participant.getString("mail");
                    String id = participant.getString("id");

                    participantsArray.add(new CommunityContact(name,phoneNumber,position,imageURL,mail,id));
                }

                JSONArray eventComments = communitySingleEvent.getJSONArray("comments");
                ArrayList<CommentItem> commentsArray = new ArrayList<CommentItem>();
                for (int j=0; j<eventComments.length(); j++){
                    JSONObject comment = eventComments.getJSONObject(j);
                    String description = comment.getString("content");
                    String imageData = comment.getString("imageUrl");
                    String postID = comment.getString("postID");
                    Long date = comment.getLong("date");

                    JSONObject member = comment.getJSONObject("mmbrCommented");
                    String name = member.getString("name");
                    String imageURL = member.getString("imageUrl");
                    String phone = member.getString("phone");
                    String mmbrID = member.getString("id");

                    CommunityMember commenter = new CommunityMember(imageURL,name,phone,mmbrID);
                    commentsArray.add(new CommentItem(postID,imageData,date,description,commenter));
                }

                communityEvents.add(new EventOrMessage(eventID, eventName, eventStartTime, eventEndTime,
                        eventLoc, participantsArray, eventContent, eventImageUrl,
                        eventReadStatus, eventIsFree, eventByLink, eventAttendingStatus, commentsArray, "event",eventIsActive,dailyEventDate));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return communityEvents;
    }

    private ArrayList<CommunityActivityOrFacility> parseActivities(JSONArray communityActivities) {
        ArrayList<CommunityActivityOrFacility> tempActivities = new ArrayList<CommunityActivityOrFacility>();

        try {
            for (int z=0; z<communityActivities.length(); z++) {
                JSONObject communityActivity = communityActivities.getJSONObject(z);
                String activityBigImage = communityActivity.getString("bigImageUrl");
                String activityName = communityActivity.getString("name");
                String activityDescription = communityActivity.getString("description");
                String activityID = communityActivity.getString("id");

                LocationObj activityLoc = null;
                if (!communityActivity.isNull("locationObj")){
                    JSONObject communityActivityLocation = communityActivity.getJSONObject("locationObj");
                    String activityLocationTitle = communityActivityLocation.getString("title");
                    String activityLocationCoords = communityActivityLocation.getString("cords");
                    activityLoc = new LocationObj(activityLocationTitle,activityLocationCoords);
                }

                JSONObject communityActivityContact;
                CommunityContact activityContact = null;
                if (!communityActivity.isNull("communityContact")){
                    communityActivityContact = communityActivity.getJSONObject("communityContact");
                    String contactName = communityActivityContact.getString("Name");
                    String contactPosition = communityActivityContact.getString("Position");
                    String contactPhoneNumber = communityActivityContact.getString("PhoneNumber");
                    String contactImageUrl = communityActivityContact.getString("ImageUrl");
                    String contactMail = communityActivityContact.getString("mail");
                    String contactID = communityActivityContact.getString("id");

                    activityContact = new CommunityContact(contactName,contactPhoneNumber,contactPosition,
                            contactImageUrl,contactMail,contactID);
                }

                ArrayList<EventOrMessage> activityEvents = new ArrayList<EventOrMessage>();
                JSONArray jsonActivityEvents = communityActivity.getJSONArray("events");
                for (int h=0;h<jsonActivityEvents.length();h++){
                    JSONObject activityEvent = jsonActivityEvents.getJSONObject(h);
                    String eventID = activityEvent.getString("eventID");
                    String eventName = activityEvent.getString("name");
                    long eventStartTime = activityEvent.getLong("sTime");
                    long eventEndTime = activityEvent.getLong("eTime");

                    String eventLocation = activityEvent.getString("locationObj");
                    String eventContent = activityEvent.getString("content");
                    String eventImageUrl = activityEvent.getString("imageUrl");

                    String eventReadStatus = activityEvent.getString("readStatus");
                    boolean eventIsFree = activityEvent.getBoolean("isFree");
                    boolean eventIsActive = activityEvent.getBoolean("isActive");
                    String eventByLink = activityEvent.getString("buyLink");

                    String eventAttendingStatus = activityEvent.getString("attendingStatus");
/*                    String eventParticipants = activityEvent.getString("participants");
                    String eventComments = activityEvent.getString("comments");*/

                    activityEvents.add(new EventOrMessage(eventID,eventName,eventStartTime,eventEndTime,activityLoc,null,eventContent,
                            eventImageUrl,eventReadStatus,eventIsFree,eventByLink,eventAttendingStatus,null,"event",eventIsActive,""));
                }

                tempActivities.add(new CommunityActivityOrFacility(activityBigImage,activityName,
                        activityDescription,null,activityLoc,activityEvents,null,activityContact,activityID));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return tempActivities;
    }

    private ArrayList<PrayerDetails> parsePrayers(JSONArray prayers) {
        ArrayList<PrayerDetails> prayersArray = new ArrayList<PrayerDetails>();
        try {
            for (int i=0;i<prayers.length();i++){
                ArrayList<SmallPrayer> smallPrayers = new ArrayList<SmallPrayer>();
                JSONObject prayerObj = prayers.getJSONObject(i);

                Long date = prayerObj.getLong("date");
                String parasha = prayerObj.getString("parashaName");
                String holidayStart = prayerObj.getString("holidayStart");
                String holidayEnd = prayerObj.getString("holidayEnd");

                JSONArray monthPrayers = prayerObj.getJSONArray("shabatPrayers");
                for (int j=0; j<monthPrayers.length();j++){
                    JSONObject singleItem = monthPrayers.getJSONObject(j);
                    String id = singleItem.getString("id");
                    String time = singleItem.getString("time");
                    String title = singleItem.getString("title");
                    String type = singleItem.getString("type");
                    long prayerMillis = singleItem.getLong("prayerInMillis");

                    smallPrayers.add(new SmallPrayer(time, title,type, id, prayerMillis));
                }

                ArrayList<SmallPrayer> shabatTimes = new ArrayList<SmallPrayer>();
                JSONArray shabatPrayers = null;
                if (!prayerObj.isNull("smallPrayers")){
                    shabatPrayers = prayerObj.getJSONArray("smallPrayers");
                    for (int j=0; j<shabatPrayers.length(); j++){
                        JSONObject tempPrayer = shabatPrayers.getJSONObject(j);
                        String id = tempPrayer.getString("id");
                        String time = tempPrayer.getString("time");
                        String title = tempPrayer.getString("title");
                        String type = tempPrayer.getString("type");
                        long prayerMillis = tempPrayer.getLong("prayerInMillis");

                        shabatTimes.add(new SmallPrayer(time, title, type,id, prayerMillis));
                    }
                }

                ArrayList<String> prayersAdditions = new ArrayList<String>();
                JSONArray additions = null;
                if (!prayerObj.isNull("additions")) {
                    additions = prayerObj.getJSONArray("additions");
                    for (int j = 0; j < additions.length(); j++) {
                        String addition = additions.getString(j);
                        prayersAdditions.add(addition);
                    }
                }

                prayersArray.add(new PrayerDetails(date,parasha,smallPrayers,holidayStart,holidayEnd,shabatTimes,prayersAdditions));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return prayersArray;
    }

    private ArrayList<String> parseWidgets(JSONArray widgets) {
        ArrayList<String> communityWidgets = new ArrayList<>();
        for (int i = 0; i < widgets.length(); i++) {
            try {
                String widgetName = widgets.getString(i);
                if (!widgetName.equals("Explore") && !widgetName.equals("Facility"))
                    communityWidgets.add(widgetName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return communityWidgets;
    }

    private ArrayList<String> parseVisibleModules(JSONArray visibleModules) {
        ArrayList<String> communityModules = new ArrayList<>();
        for (int i = 0; i < visibleModules.length(); i++) {
            try {
                String visibleModule = visibleModules.getString(i);
                communityModules.add(visibleModule);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return communityModules;
    }

    private void saveMainModel(MainModel mainModel) {
        Gson gson = new Gson();
        String mainModelString = gson.toJson(mainModel);
        prefs.edit().putString("latestCommunerData", mainModelString).commit();
    }

    public void callParser(JSONObject latestJSON){
        new parseUserDataAndCommunities().execute(latestJSON);
    }
}
