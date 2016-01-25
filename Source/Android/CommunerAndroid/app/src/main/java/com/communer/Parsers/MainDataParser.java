package com.communer.Parsers;

import com.communer.Models.CommentItem;
import com.communer.Models.CommunityActivityOrFacility;
import com.communer.Models.CommunityContact;
import com.communer.Models.CommunityMember;
import com.communer.Models.DailyEvents;
import com.communer.Models.EventOrMessage;
import com.communer.Models.GalleryImage;
import com.communer.Models.GuideCategory;
import com.communer.Models.ImageEvent;
import com.communer.Models.LocationObj;
import com.communer.Models.MediaData;
import com.communer.Models.Post;
import com.communer.Models.PrayerDetails;
import com.communer.Models.ReportItem;
import com.communer.Models.SecurityEvent;
import com.communer.Models.SmallPrayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by יובל on 01/11/2015.
 */
public class MainDataParser {

    public MainDataParser() {
    }

    public  ArrayList<EventOrMessage> parseEvents(JSONArray eventsArray, String daileEvenyDate) {
        ArrayList<EventOrMessage> communityEvents = new ArrayList<EventOrMessage>();
        for (int i = 0; i < eventsArray.length(); i++) {
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
                String eventByLink = communitySingleEvent.getString("byLink");
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
                        eventReadStatus, eventIsFree, eventByLink, eventAttendingStatus, commentsArray,"event", eventIsActive,daileEvenyDate));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return communityEvents;
    }

    public  ArrayList<EventOrMessage> parseAnnouncments(JSONArray announcementsArray) {
        ArrayList<EventOrMessage> communityAnnouncements = new ArrayList<EventOrMessage>();
        for (int i = 0; i < announcementsArray.length(); i++) {
            try {
                JSONObject communitySingleAnnouncement = announcementsArray.getJSONObject(i);
                String announID = communitySingleAnnouncement.getString("id");
                String announName = communitySingleAnnouncement.getString("name");
                long announStartTime = communitySingleAnnouncement.getLong("sTime");
                long announEndTime = communitySingleAnnouncement.getLong("eTime");
                String type = communitySingleAnnouncement.getString("type");

/*                JSONObject announLocation = communitySingleAnnouncement.getJSONObject("location");
                String locTitle = announLocation.getString("title");
                String locCoords = announLocation.getString("coords");
                LocationObj announLoc = new LocationObj(locTitle,locCoords);*/
                LocationObj announLoc = null;

                String announContent = communitySingleAnnouncement.getString("content");
                String announImageUrl = communitySingleAnnouncement.getString("imageURL");

                String announReadStatus = communitySingleAnnouncement.getString("readStatus");
                boolean announIsFree = communitySingleAnnouncement.getBoolean("isFree");
                boolean eventIsActive = communitySingleAnnouncement.getBoolean("isActive");
                String announByLink = communitySingleAnnouncement.getString("byLink");
                String announAttendingStatus = communitySingleAnnouncement.getString("attendingStatus");

                ArrayList<CommunityContact> participantsArray = new ArrayList<CommunityContact>();
                if (!communitySingleAnnouncement.isNull("participants")) {
                    JSONArray announParticipants = communitySingleAnnouncement.getJSONArray("participants");
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

                JSONArray announComments = communitySingleAnnouncement.getJSONArray("comments");
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

                communityAnnouncements.add(new EventOrMessage(announID, announName, announStartTime, announEndTime,
                        announLoc, participantsArray, announContent, announImageUrl,
                        announReadStatus, announIsFree, announByLink, announAttendingStatus, commentsArray, type, eventIsActive,""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ArrayList<EventOrMessage> orderedAnnoun = new ArrayList<EventOrMessage>();
        for (int i = 0; i < communityAnnouncements.size(); i++) {
            EventOrMessage tempAnnoun = communityAnnouncements.get(i);
            boolean isActive = tempAnnoun.isActive();

            if (isActive) {
                long sTime = tempAnnoun.getsTime();

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(sTime);
                Date eventSDate = cal.getTime();

                if (orderedAnnoun.size() == 0) {
                    orderedAnnoun.add(tempAnnoun);
                } else {
                    int tempAnnounSize = orderedAnnoun.size();
                    boolean addedItem = false;

                    for (int j = 0; j < tempAnnounSize; j++) {
                        EventOrMessage prevAnnoun = orderedAnnoun.get(j);

                        long announDateLong = prevAnnoun.getsTime();
                        Calendar announCal = Calendar.getInstance();
                        announCal.setTimeInMillis(announDateLong);
                        Date announDate = announCal.getTime();

                        if (eventSDate.after(announDate)) {
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

    public  ArrayList<CommunityMember> parseMembers(JSONArray membersJSON) {
        ArrayList<CommunityMember> mmbrs = new ArrayList<CommunityMember>();
        try {
            for (int j = 0; j < membersJSON.length(); j++) {
                JSONObject member = membersJSON.getJSONObject(j);
                String memberImg = member.getString("imageURL");
                String memberName = member.getString("name");
                String memberPhone = member.getString("phone");
                String mmbrID = member.getString("id");

                mmbrs.add(new CommunityMember(memberImg, memberName, memberPhone, mmbrID));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mmbrs;
    }

    public  ArrayList<CommunityContact> parseContacts(JSONArray contacts) {
        ArrayList<CommunityContact> cntctsArray = new ArrayList<CommunityContact>();
        try {
            for (int j = 0; j < contacts.length(); j++) {
                JSONObject contact = contacts.getJSONObject(j);
                String contactName = contact.getString("name");
                String contactPosition = contact.getString("position");
                String contactPhone = contact.getString("phoneNumber");
                String contactImage = contact.getString("imageURL");
                String contactMail = contact.getString("mail");
                String contactID = contact.getString("id");

                cntctsArray.add(new CommunityContact(contactName, contactPhone, contactPosition, contactImage, contactMail, contactID));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cntctsArray;
    }

    public  ArrayList<Post> parsePublicQuestions(JSONArray publicQA) {
        ArrayList<Post> publicQAs = new ArrayList<Post>();
        try {
            for (int i = 0; i < publicQA.length(); i++) {
                JSONObject qa = publicQA.getJSONObject(i);
                String qaTitle = qa.getString("title");
                String qaImage = qa.getString("imageURL");
                String qaContent = qa.getString("content");
                Long qaCD = qa.getLong("cd");
                String qaPostID = "";
                if (qa.has("id"))
                    qaPostID = qa.getString("id");

                String qaCommunityID = qa.getString("communityID");

                JSONObject qaMember = qa.getJSONObject("member");
                String mmbrName = qaMember.getString("name");
                String mmbrImage = qaMember.getString("imageURL");
                String mmbrPhone = qaMember.getString("phone");
                String mmbrID = qaMember.getString("id");

                CommunityMember posterMember = new CommunityMember(mmbrImage, mmbrName, mmbrPhone, mmbrID);

                JSONArray qaPostComments = qa.getJSONArray("comments");
                ArrayList<CommentItem> commentsArray = new ArrayList<CommentItem>();
                for (int j = 0; j < qaPostComments.length(); j++) {
                    JSONObject comment = qaPostComments.getJSONObject(j);
                    String description = comment.getString("description");
                    String imageData = comment.getString("imageData");
                    String postID = comment.getString("postID");
                    String isFromRav = comment.getString("isFromRav");
                    Long date = comment.getLong("cd");
//                    String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                    JSONObject member = comment.getJSONObject("member");
                    String name = member.getString("name");
                    String imageURL = member.getString("imageURL");
                    String phone = member.getString("phone");
                    String postMmbrID = member.getString("id");

                    CommunityMember commenter = new CommunityMember(imageURL, name, phone, postMmbrID);
                    commentsArray.add(new CommentItem(postID, imageData, date, description, commenter));
                }
                publicQAs.add(new Post(qaPostID, qaTitle, qaImage, posterMember, qaCD, qaContent, commentsArray));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return publicQAs;
    }

    public  ArrayList<Post> parseCommunityPosts(JSONArray comPosts) {
        ArrayList<Post> posts = new ArrayList<Post>();
        try {
            for (int i = 0; i < comPosts.length(); i++) {
                JSONObject post = comPosts.getJSONObject(i);
                String postTitle = post.getString("title");
                String postImage = post.getString("imageURL");
                String postContent = post.getString("content");
                Long postCD = post.getLong("cd");
                String postID = post.getString("id");

                String postCommunityID = post.getString("communityID");

                JSONObject postMember = post.getJSONObject("member");
                String mmbrName = postMember.getString("name");
                String mmbrImage = postMember.getString("imageURL");
                String mmbrPhone = postMember.getString("phone");
                String postMmbrID = postMember.getString("id");

                CommunityMember posterMember = new CommunityMember(mmbrImage, mmbrName, mmbrPhone, postMmbrID);

                JSONArray postPostComments = post.getJSONArray("comments");
                ArrayList<CommentItem> postsArray = new ArrayList<CommentItem>();
                for (int j = 0; j < postPostComments.length(); j++) {
                    JSONObject comment = postPostComments.getJSONObject(j);
                    String description = comment.getString("description");
                    String imageData = comment.getString("imageData");
                    String postIDComment = comment.getString("postID");
                    String isFromRav = comment.getString("isFromRav");
//                    String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    Long date = null;
                    if (!comment.isNull("cd"))
                        date = comment.getLong("cd");

                    JSONObject member = null;
                    if (!comment.isNull("member"))
                        member = comment.getJSONObject("member");

                    CommunityMember commenter = null;
                    if (member != null) {
                        String name = member.getString("name");
                        String imageURL = member.getString("imageURL");
                        String phone = member.getString("phone");
                        String mmbrID = postMember.getString("id");

                        commenter = new CommunityMember(imageURL, name, phone, mmbrID);
                    }

                    postsArray.add(new CommentItem(postIDComment, imageData, date, description, commenter));
                }
                posts.add(new Post(postID, postTitle, postImage, posterMember, postCD, postContent, postsArray));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return posts;
    }

    public  ArrayList<CommunityActivityOrFacility> parseFacilities(JSONArray facilities) {
        ArrayList<CommunityActivityOrFacility> tempFacilities = new ArrayList<CommunityActivityOrFacility>();

        try {
            for (int z = 0; z < facilities.length(); z++) {
                JSONObject communityFacility = facilities.getJSONObject(z);
                String facilityBigImage = communityFacility.getString("bigImageURL");
                String facilityName = communityFacility.getString("name");
                String facilityDescription = communityFacility.getString("description");
                String facilityID = communityFacility.getString("id");

                JSONObject facilityLocation = communityFacility.getJSONObject("location");
                String facilityLocationTitle = facilityLocation.getString("title");
                String facilityLocationCoords = facilityLocation.getString("coords");
                LocationObj facilityLoc = new LocationObj(facilityLocationTitle, facilityLocationCoords);

                JSONArray facilityContacts = communityFacility.getJSONArray("contact");
                CommunityContact communityCon = null;
                for (int h = 0; h < facilityContacts.length(); h++){
                    JSONObject facilityContact = facilityContacts.getJSONObject(h);
                    String facilityContactName = facilityContact.getString("name");
                    String facilityPositionName = facilityContact.getString("position");
                    String facilityPhoneNumber = facilityContact.getString("phoneNumber");
                    String facilityImageUrl = facilityContact.getString("imageURL");
                    String facilityContactMail = facilityContact.getString("mail");
                    String facilityContactID = facilityContact.getString("id");

                    communityCon = new CommunityContact(facilityContactName,facilityPhoneNumber,facilityPositionName,facilityImageUrl,facilityContactMail,facilityContactID);
                }

                ArrayList<EventOrMessage> facilEvents = new ArrayList<EventOrMessage>();
                JSONArray facilityEvents = communityFacility.getJSONArray("events");
                for (int h = 0; h < facilityEvents.length(); h++) {
                    JSONObject facilityEvent = facilityEvents.getJSONObject(h);
                    String eventID = facilityEvent.getString("id");
                    String eventName = facilityEvent.getString("name");
                    long eventStartTime = facilityEvent.getLong("sTime");
                    long eventEndTime = facilityEvent.getLong("eTime");

                    String eventLocation = facilityEvent.getString("location");
                    String eventContent = facilityEvent.getString("content");
                    String eventImageUrl = facilityEvent.getString("imageURL");

                    String eventReadStatus = facilityEvent.getString("readStatus");
                    boolean eventIsFree = facilityEvent.getBoolean("isFree");
                    boolean eventIsActive = facilityEvent.getBoolean("isActive");
                    String eventByLink = facilityEvent.getString("byLink");

                    String eventAttendingStatus = facilityEvent.getString("attendingStatus");
                    String eventParticipants = facilityEvent.getString("participants");
                    String eventComments = facilityEvent.getString("comments");

                    facilEvents.add(new EventOrMessage(eventID, eventName, eventStartTime, eventEndTime, null, null, eventContent,
                            eventImageUrl, eventReadStatus, eventIsFree, eventByLink, eventAttendingStatus, null,"event", eventIsActive,""));
                }

                ArrayList<String> facilImages = new ArrayList<String>();
                JSONArray facilityImages = communityFacility.getJSONArray("images");
                for (int h = 0; h < facilityImages.length(); h++) {
                    String image = facilityImages.getString(h);
                    facilImages.add(image);
                }

                tempFacilities.add(new CommunityActivityOrFacility(facilityBigImage, facilityName,
                        facilityDescription, null, facilityLoc, facilEvents, facilImages, communityCon, facilityID));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tempFacilities;
    }

    public  ArrayList<CommunityActivityOrFacility> parseActivities(JSONArray communityActivities, String date) {
        ArrayList<CommunityActivityOrFacility> tempActivities = new ArrayList<CommunityActivityOrFacility>();

        try {
            for (int z = 0; z < communityActivities.length(); z++) {
                JSONObject communityActivity = communityActivities.getJSONObject(z);

                String activityBigImage = communityActivity.getString("bigImageURL");
                String activityName = communityActivity.getString("name");
                String activityDescription = communityActivity.getString("description");
                String activityID = communityActivity.getString("id");

                LocationObj activityLoc = null;
                if (!communityActivity.isNull("location")) {
                    JSONObject communityActivityLocation = communityActivity.getJSONObject("location");
                    String activityLocationTitle = communityActivityLocation.getString("title");
                    String activityLocationCoords = communityActivityLocation.getString("coords");
                    activityLoc = new LocationObj(activityLocationTitle, activityLocationCoords);
                }

                JSONObject communityActivityContact;
                CommunityContact activityContact = null;
                if (!communityActivity.isNull("contact")) {
                    communityActivityContact = communityActivity.getJSONObject("contact");
                    String contactName = communityActivityContact.getString("name");
                    String contactPosition = communityActivityContact.getString("position");
                    String contactPhoneNumber = communityActivityContact.getString("phoneNumber");
                    String contactImageUrl = communityActivityContact.getString("imageURL");
                    String contactMail = communityActivityContact.getString("mail");
                    String contactID = communityActivityContact.getString("id");

                    activityContact = new CommunityContact(contactName, contactPhoneNumber, contactPosition,
                            contactImageUrl, contactMail, contactID);
                }


                ArrayList<EventOrMessage> activityEvents = new ArrayList<EventOrMessage>();
                JSONArray jsonActivityEvents = communityActivity.getJSONArray("events");
                for (int h = 0; h < jsonActivityEvents.length(); h++) {
                    JSONObject activityEvent = jsonActivityEvents.getJSONObject(h);
                    String eventID = activityEvent.getString("id");
                    String eventName = activityEvent.getString("name");
                    long eventStartTime = activityEvent.getLong("sTime");
                    long eventEndTime = activityEvent.getLong("eTime");

                    String eventLocation = activityEvent.getString("location");
                    String eventContent = activityEvent.getString("content");
                    String eventImageUrl = activityEvent.getString("imageURL");

                    String eventReadStatus = activityEvent.getString("readStatus");
                    boolean eventIsFree = activityEvent.getBoolean("isFree");
                    boolean eventIsActive = activityEvent.getBoolean("isActive");
                    String eventByLink = activityEvent.getString("byLink");

                    String eventAttendingStatus = activityEvent.getString("attendingStatus");
                    String eventParticipants = activityEvent.getString("participants");
                    String eventComments = activityEvent.getString("comments");

                    activityEvents.add(new EventOrMessage(eventID, eventName, eventStartTime, eventEndTime, activityLoc, null,eventContent,
                            eventImageUrl, eventReadStatus, eventIsFree, eventByLink, eventAttendingStatus, null,"event", eventIsActive,date));
                }

                ArrayList<String> activImages = new ArrayList<String>();
                JSONArray activityImages = communityActivity.getJSONArray("images");
                for (int h = 0; h < activityImages.length(); h++) {
                    String image = activityImages.getString(h);
                    activImages.add(image);
                }

                tempActivities.add(new CommunityActivityOrFacility(activityBigImage, activityName,
                        activityDescription, null, activityLoc, activityEvents, activImages, activityContact, activityID));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tempActivities;
    }

    public  ArrayList<ImageEvent> parseGalleryItems(JSONArray communityEventsImages) {
        ArrayList<ImageEvent> Galleries = new ArrayList<ImageEvent>();
        ArrayList<GalleryImage> galleryImagesArray;

        try {
            for (int i = 0; i < communityEventsImages.length(); i++) {
                galleryImagesArray = new ArrayList<GalleryImage>();
                JSONObject gallery = communityEventsImages.getJSONObject(i);
                String communityID = gallery.getString("galleryID");
                String galleryImg = gallery.getString("galleryImg");
                String eventTitle = gallery.getString("eventTitle");
                JSONArray galleryImages = gallery.getJSONArray("images");

                for (int j = 0; j < galleryImages.length(); j++) {
                    JSONObject imageItem = galleryImages.getJSONObject(j);
                    String id = imageItem.getString("id");
                    String imageLink = imageItem.getString("link");
                    JSONArray imageComments = imageItem.getJSONArray("imageComments");

                    galleryImagesArray.add(new GalleryImage(id,imageLink));
                }

                MediaData galleryMedia = new MediaData(galleryImagesArray, null);
                Galleries.add(new ImageEvent(galleryImg, eventTitle, communityID, galleryMedia));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Galleries;
    }


    public  ArrayList<SecurityEvent> parseSecurity(JSONArray securityEvents) {
        ArrayList<SecurityEvent> events = new ArrayList<SecurityEvent>();
        try {
            for (int i = 0; i < securityEvents.length(); i++) {
                JSONObject securityEvent = securityEvents.getJSONObject(i);
                String id = securityEvent.getString("id");
                String reporterName = securityEvent.getString("reporterName");

                JSONObject reportObj = securityEvent.getJSONObject("report");

                JSONObject reportLoc = reportObj.getJSONObject("location");
                String locTitle = reportLoc.getString("title");
                String coords = reportLoc.getString("coords");
                LocationObj reportLocaion = new LocationObj(locTitle, coords);

                String type = reportObj.getString("type");
                String description = reportObj.getString("description");
                String severity = reportObj.getString("severity");
                boolean isPoliceReport = reportObj.getBoolean("isPoliceReport");
                boolean isAnnonimus = reportObj.getBoolean("isAnnonimus");
                ReportItem theReport = new ReportItem(reportLocaion, type, description, isPoliceReport, isAnnonimus, severity);

                if (!coords.contains("null"))
                    events.add(new SecurityEvent(id,theReport, reporterName));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return events;
    }

    public  ArrayList<GuideCategory> parseGuide(JSONArray guideCategories) {
        ArrayList<GuideCategory> tempGuide = new ArrayList<GuideCategory>();
        try {
            for (int i = 0; i < guideCategories.length(); i++) {
                JSONObject singleGuide = guideCategories.getJSONObject(i);
                String title = singleGuide.getString("title");
                String imageURL = singleGuide.getString("imageURL");
                String id = singleGuide.getString("id");

                tempGuide.add(new GuideCategory(id,title, imageURL));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tempGuide;
    }

    public  ArrayList<PrayerDetails> parsePrayers(JSONArray prayers) {
        ArrayList<PrayerDetails> prayersArray = new ArrayList<PrayerDetails>();
        try {
            for (int i = 0; i < prayers.length(); i++) {
                ArrayList<SmallPrayer> smallPrayers = new ArrayList<SmallPrayer>();
                JSONObject prayerObj = prayers.getJSONObject(i);
                String communityID = prayerObj.getString("communityID");
                Long date = prayerObj.getLong("date");
                String parasha = prayerObj.getString("parasha");
                String holidayStart = prayerObj.getString("holidayStart");
                String holidayEnd = prayerObj.getString("holidayEnd");

                JSONObject todaySmallPrayers = prayerObj.getJSONObject("prayers");
                JSONArray prayerTimes = null;
                if (!todaySmallPrayers.isNull("times")){
                    prayerTimes = todaySmallPrayers.getJSONArray("times");
                    for (int j = 0; j < prayerTimes.length(); j++){
                        JSONObject singleItem = prayerTimes.getJSONObject(j);
                        String id = singleItem.getString("id");
                        String time = singleItem.getString("time");
                        String title = singleItem.getString("title");
                        String type = singleItem.getString("type");
                        Long prayerMillis = singleItem.getLong("timeL");

                        smallPrayers.add(new SmallPrayer(time,title,type,id,prayerMillis));
                    }
                }

                ArrayList<SmallPrayer> mainTodayPrayers = new ArrayList<SmallPrayer>();
                JSONArray shabatPrayers = null;
                if (!prayerObj.isNull("smallPrayers")){
                    shabatPrayers = prayerObj.getJSONArray("smallPrayers");
                    for (int j = 0; j < shabatPrayers.length(); j++) {
                        JSONObject tempPrayer = shabatPrayers.getJSONObject(j);
                        String id = tempPrayer.getString("id");
                        String time = tempPrayer.getString("time");
                        String title = tempPrayer.getString("title");
                        String type = tempPrayer.getString("type");
                        Long prayerMillis = tempPrayer.getLong("timeL");

                        mainTodayPrayers.add(new SmallPrayer(time, title,type,id,prayerMillis));
                    }
                }

                ArrayList<String> prayersAdditions = new ArrayList<String>();
                JSONArray additions = null;
                if(!prayerObj.isNull("additions")){
                    additions = prayerObj.getJSONArray("additions");
                    for (int j = 0; j < additions.length(); j++){
                        String addition = additions.getString(j);
                        prayersAdditions.add(addition);
                    }
                }

                prayersArray.add(new PrayerDetails(date, parasha, smallPrayers, holidayStart, holidayEnd, mainTodayPrayers,prayersAdditions));
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return prayersArray;
    }

    public  ArrayList<DailyEvents> parseDailyItems(JSONArray dailyItems) {
        ArrayList<DailyEvents> tempDaily = new ArrayList<DailyEvents>();
        try {
            for (int i = 0; i < dailyItems.length(); i++) {
                JSONObject dailyObj = dailyItems.getJSONObject(i);
                String date = dailyObj.getString("date");

                JSONArray dailyEvents = dailyObj.getJSONArray("events");
                ArrayList<EventOrMessage> events = parseEvents(dailyEvents,date);

                JSONArray dailyActivities = dailyObj.getJSONArray("activities");
                ArrayList<CommunityActivityOrFacility> activities = parseActivities(dailyActivities,date);

                JSONArray dailyPrayers = dailyObj.getJSONArray("prayers");
                ArrayList<PrayerDetails> prayers = parsePrayers(dailyPrayers);

                tempDaily.add(new DailyEvents(date, events, activities, prayers));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tempDaily;
    }

    public  ArrayList<String> parseWidgets(JSONArray widgets){
        ArrayList<String> communityWidgets = new ArrayList<String>();
        for (int i = 0; i < widgets.length(); i++) {
            try {
                String widgetName = widgets.getString(i);
                if (!widgetName.equals("Explore") && !widgetName.equals("Facility") && !widgetName.equals("Support"))
                    communityWidgets.add(widgetName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return communityWidgets;
    }

    public  ArrayList<String> parseVisibleModules(JSONArray visibleModules){
        ArrayList<String> communityModules = new ArrayList<String>();
        for (int i = 0; i < visibleModules.length(); i++) {
            try {
                String visibleModuleName = visibleModules.getString(i);
                communityModules.add(visibleModuleName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return communityModules;
    }
}
