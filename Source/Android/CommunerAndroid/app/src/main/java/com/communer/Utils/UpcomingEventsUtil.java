package com.communer.Utils;

import com.communer.Models.DailyEvents;
import com.communer.Models.EventOrMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UpcomingEventsUtil {

    public UpcomingEventsUtil() {
    }

    public ArrayList<EventOrMessage> getUpcomingEvents(ArrayList<DailyEvents> allEvents, int MaxResults) throws ParseException {

        ArrayList<DailyEvents> tempEvents = new ArrayList<DailyEvents>();
        ArrayList<EventOrMessage> finalResults = new ArrayList<EventOrMessage>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (int i=0; i<allEvents.size(); i++){
            DailyEvents tempEvent = allEvents.get(i);
            String date = tempEvent.getDate();
            Date dayDate = sdf.parse(date);

            if (i==0)
                tempEvents.add(tempEvent);
            else{
                boolean eventAdded = false;
                int tmpCount = tempEvents.size();

                for (int j=0; j<tmpCount; j++){
                    Date dailyEvent = sdf.parse(tempEvents.get(j).getDate());
                    if (dayDate.before(dailyEvent)){
                        tempEvents.add(j,tempEvent);
                        eventAdded = true;
                        break;
                    }
                }

                if (!eventAdded){
                    tempEvents.add(tempEvent);
                }
            }
        }

        for (int i=0; i<tempEvents.size(); i++){
//            if (finalResults.size() < MaxResults){
            DailyEvents item = tempEvents.get(i);
            ArrayList<EventOrMessage> theEvents = item.getEvents();
            for (int j=0; j<theEvents.size(); j++){
                EventOrMessage singleEvent = theEvents.get(j);
                finalResults.add(singleEvent);
            }
//            }
        }

        return finalResults;
    }
}
