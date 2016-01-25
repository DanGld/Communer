package com.communer.Utils;

import com.communer.Models.DailyEvents;
import com.communer.Models.EventOrMessage;
import com.communer.Models.GalleryImage;
import com.communer.Models.ImageEvent;
import com.communer.Models.Post;
import com.communer.Models.SmallPrayer;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ���� on 26/08/2015.
 */
public class HomeDataUtil {

    public HomeDataUtil() {
    }

    public ArrayList<EventOrMessage> getUpcomingAnnouncements(ArrayList<EventOrMessage> allAnnouncements, int MaxResults) throws ParseException {
        ArrayList<EventOrMessage> tempAnnoun = new ArrayList<EventOrMessage>();
        ArrayList<EventOrMessage> finalAnnoun = new ArrayList<EventOrMessage>();

        for (int i = 0; i < allAnnouncements.size(); i++) {
            EventOrMessage tempEvent = allAnnouncements.get(i);
            boolean isActive = tempEvent.isActive();

            if (isActive){
                long sTime = tempEvent.getsTime();

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(sTime);
                Date eventSDate = cal.getTime();

                if (tempAnnoun.size() == 0){
                    tempAnnoun.add(tempEvent);
                }else {
                    int tempAnnounSize = tempAnnoun.size();
                    boolean addedItem = false;

                    for (int j = 0; j < tempAnnounSize; j++) {
                        EventOrMessage announ = tempAnnoun.get(j);

                        long announDateLong = announ.getsTime();
                        Calendar announCal = Calendar.getInstance();
                        announCal.setTimeInMillis(announDateLong);
                        Date announDate = announCal.getTime();

                        if (eventSDate.after(announDate)){
                            tempAnnoun.add(j, tempEvent);
                            addedItem = true;
                        }

                        if (addedItem)
                            break;
                    }

                    if (!addedItem)
                        tempAnnoun.add(tempEvent);
                }
            }
        }

        for (int i = 0; i < tempAnnoun.size(); i++){
            if (finalAnnoun.size() < MaxResults) {
                EventOrMessage item = tempAnnoun.get(i);
                finalAnnoun.add(item);
            }
        }

        return finalAnnoun;
    }

    public ArrayList<GalleryImage> getRandomPictures(ArrayList<ImageEvent> allGalleries, int MaxResults){
        ArrayList<GalleryImage> randomPics = new ArrayList<GalleryImage>();
        for (int i=0; i<allGalleries.size(); i++){
            ImageEvent gallery = allGalleries.get(i);
            ArrayList<GalleryImage> galleryPics = gallery.getMedia().getImages();
            for (int j=0; j<galleryPics.size();j++){
                if (randomPics.size() < MaxResults)
                    randomPics.add(galleryPics.get(j));
                else
                    break;
            }

            if (randomPics.size() >= MaxResults){
                break;
            }
        }

        return randomPics;
    }

    public ArrayList<SmallPrayer> getTodayServices(){
        ArrayList<String> services = new ArrayList<String>();

        AppUser appUserInstance = AppUser.getInstance();
        ArrayList<DailyEvents> dailyEvents = appUserInstance.getCurrentCommunity().getDailyEvents();

        Calendar calendar = Calendar.getInstance();
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String year = String.valueOf(calendar.get(Calendar.YEAR));

        if (Integer.valueOf(day) < 10)
            day = "0" + day;

        if (Integer.valueOf(month) < 10)
            month = "0" + month;

        String todayDateString = String.valueOf(String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year));

        for (int i=0; i<dailyEvents.size(); i++){
            DailyEvents singleDay = dailyEvents.get(i);
            if (todayDateString.equals(singleDay.getDate())){
                if (singleDay.getPrayers().size() > 0){
                    return singleDay.getPrayers().get(0).getSmallPrayers();
                }
            }
        }

        return null;
    }

    public Post getRecentQA(){
        AppUser appUserInstance = AppUser.getInstance();
        ArrayList<Post> posts = appUserInstance.getCurrentCommunity().getPublicQA();

        if (posts != null) {
            if (posts.size() > 0) {
                Post item = posts.get(0);
                return item;
            }
        }

        posts = appUserInstance.getCurrentCommunity().getPrivateQA();
        if (posts != null) {
            if (posts.size() > 0) {
                Post item = posts.get(0);
                return item;
            }
        }

        return null;
    }

    public Post getRecentConversation() {
        AppUser appUserInstance = AppUser.getInstance();
        ArrayList<Post> posts = appUserInstance.getCurrentCommunity().getCommunityPosts();
        if (posts != null) {
            if (posts.size() > 0) {
                Post item = posts.get(0);
                return item;
            }
        }
        return null;
    }
}
