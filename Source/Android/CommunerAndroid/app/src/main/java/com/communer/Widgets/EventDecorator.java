package com.communer.Widgets;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.communer.Models.CalendarEventItem;
import com.communer.R;
import com.communer.Utils.MyCustomDotSpan;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;


public class EventDecorator implements DayViewDecorator {

    private Context mContext;
    private HashSet<CalendarDay> dates;
    private Map<CalendarDay,ArrayList<CalendarEventItem>> eventsMap;

    private boolean globalPrayers = false, globalActivities = false, globalEvents = false;

/*    public EventDecorator(Context mContext, Collection<CalendarDay> dates, Map<CalendarDay,ArrayList<CalendarEventItem>> eventsMap) {
        this.mContext = mContext;
        this.dates = new HashSet<>(dates);
        this.eventsMap = eventsMap;
    }   */

    public EventDecorator(Context mContext, Collection<CalendarDay> dates) {
        this.mContext = mContext;
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
        /*if (dates.contains(day)){
            ArrayList<CalendarEventItem> calendarItems = eventsMap.get(day);
            if (calendarItems.size() > 0){
                boolean hasPrayers = false;
                boolean hasActivities = false;
                boolean hasEvents = false;

                for (int i = 0; i <calendarItems.size(); i++) {
                    if (hasPrayers && hasActivities && hasEvents)
                        break;

                    CalendarEventItem tempCalendarItem = calendarItems.get(i);
                    String itemType = tempCalendarItem.getType();
                    if (itemType.equals("prayer") && !hasPrayers)
                        hasPrayers = true;

                    if (itemType.equals("activity") && !hasActivities)
                        hasActivities = true;

                    if (itemType.equals("event") && !hasEvents)
                        hasEvents = true;
                }

                globalPrayers = hasPrayers;
                globalActivities = hasActivities;
                globalEvents = hasEvents;
            }
            return true;
        }

        return false;*/
    }

    @Override
    public void decorate(DayViewFacade view) {
//        view.addSpan(new MyCustomDotSpan(6, ContextCompat.getColor(mContext, R.color.BlueDot),globalPrayers, globalActivities, globalEvents,mContext));
        view.addSpan(new DotSpan(6, ContextCompat.getColor(mContext, R.color.BlueDot)));
    }
}
