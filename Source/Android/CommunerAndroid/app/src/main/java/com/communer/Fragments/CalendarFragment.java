package com.communer.Fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.communer.Adapters.CalendarEventsAdapter;
import com.communer.Models.CalendarEventItem;
import com.communer.Models.CommunityActivityOrFacility;
import com.communer.Models.CommunityContact;
import com.communer.Models.DailyEvents;
import com.communer.Models.EventOrMessage;
import com.communer.Models.LocationObj;
import com.communer.Models.PrayerDetails;
import com.communer.Models.SmallPrayer;
import com.communer.R;
import com.communer.Utils.AppUser;
import com.communer.Utils.ResizeAnimation;
import com.communer.Widgets.EventDecorator;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by ���� on 28/07/2015.
 */
public class CalendarFragment extends Fragment {

    private HorizontalScrollView horizontalScroll;
    private LinearLayout calendarContainer;
    private LinearLayout leftArrow, rightArrow, downArrow;
    private RelativeLayout topCalendarCntnr,weeklyCalendarCntnr;
    private CalendarEventsAdapter eventsAdapter;
    private ListView calendarEventsList;

    private int MONTH_DAY_COUNT;
    private int screenWidth;
    private int todayPosition=0;
    private int latestCalendarposition;
    private CalendarDay latestDayPicked;

    private HashSet<CalendarDay> eventsDays;
    private Map<CalendarDay,ArrayList<CalendarEventItem>> eventsMap;

    private String [] monthesNames = {"January","February","March","April",
                                    "May","June","July","August","September",
                                    "October","November","December"};

    private String [] hebrewMonthesNames = {"ינואר","פברואר","מרץ","אפריל",
            "מאי","יוני","יולי","אוגוסט","ספטמבר",
            "אוקטובר","נובמבר","דצמבר"};

    private TextView monthText;
    private TextView noEventsText;
    private int currentMonth;
    private int currentYear;
    private int originalMonth;
    private int originalYear;

    private BroadcastReceiver refreshReceiver;

    private com.github.clans.fab.FloatingActionButton prayers_fab, activities_fab, events_fab;
    private FloatingActionMenu fam;

    private boolean showActivities = true, showPrayers = true, showEvents = true;
    Integer [] selectedIndices = new Integer[] {0,1,2};

    private String currentLanguage;

    private boolean activities_on = true, prayers_on = true, events_on = true;

    /////Layout change
    private int topRelativeHeight = 0;
    private int calendarViewHeight = 0;
    private int originalCalendarHeight = 0;

    private MaterialCalendarView materialCalendarView;
    private ImageView calendarUpDownArrow;
    private boolean calendarExpanded = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_tab, container, false);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setViewsReferences(view);

        eventsDays = new HashSet<CalendarDay>();
        eventsMap = new HashMap<CalendarDay,ArrayList<CalendarEventItem>>();

        detectLanguage();
        initReceiver();
        getScreenWidth();
        new createCalendarInBackground().execute();

        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonth();
            }
        });

        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousMonth();
            }
        });

        downArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!calendarExpanded)
                    expandCalendarLayout();
                else
                    collapseCalendarLayout();
            }
        });

        activities_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activities_on) {
                    activities_fab.setColorNormal(ContextCompat.getColor(getActivity(), R.color.SecondaryText));
                    showActivities = false;
                } else {
                    activities_fab.setColorNormal(ContextCompat.getColor(getActivity(), R.color.BlueDot));
                    showActivities = true;
                }

                activities_on = !activities_on;
                handleItemsFiltering();
            }
        });

        events_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (events_on) {
                    events_fab.setColorNormal(ContextCompat.getColor(getActivity(), R.color.SecondaryText));
                    showEvents = false;
                } else {
                    events_fab.setColorNormal(ContextCompat.getColor(getActivity(), R.color.app_green));
                    showEvents = true;
                }

                events_on = !events_on;
                handleItemsFiltering();
            }
        });

        prayers_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prayers_on) {
                    prayers_fab.setColorNormal(ContextCompat.getColor(getActivity(), R.color.SecondaryText));
                    showPrayers = false;
                } else {
                    prayers_fab.setColorNormal(ContextCompat.getColor(getActivity(), R.color.AppOrange));
                    showPrayers = true;
                }

                prayers_on = !prayers_on;
                handleItemsFiltering();
            }
        });
    }

    private void initMaterialCalendar() {
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                ArrayList<CalendarEventItem> dateEvents = eventsMap.get(date);
                if (dateEvents != null) {
                    Calendar c = date.getCalendar();
                    String dayName = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
                    eventsAdapter = new CalendarEventsAdapter(dateEvents, getActivity(), dayName, getActivity(), showPrayers, showActivities, showEvents);
                    eventsAdapter.notifyDataSetChanged();
                    calendarEventsList.setAdapter(eventsAdapter);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            noEventsText.setVisibility(View.GONE);
                            calendarEventsList.setVisibility(View.VISIBLE);
                        }
                    });

                } else {
                    calendarEventsList.setAdapter(null);
                    calendarEventsList.setVisibility(View.INVISIBLE);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            noEventsText.setVisibility(View.VISIBLE);
                        }
                    });
                }

                latestDayPicked = date;
            }
        });

        EventDecorator eventDecorator = new EventDecorator(getActivity(),eventsDays);
        materialCalendarView.addDecorator(eventDecorator);
    }

    private void expandCalendarLayout(){
/*        ObjectAnimator animator = ObjectAnimator.ofFloat(topCalendarCntnr,"translationY",topCalendarCntnr.getHeight(),dpToPx(320));
        animator.setDuration(200);
        animator.start();*/

        final Animation anim = new ResizeAnimation(topCalendarCntnr,dpToPx(320),"expand");
        anim.setDuration(200);
        anim.setRepeatCount(0);
        anim.setFillAfter(true);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Animation alpha_anim = new AlphaAnimation(1.0f, 0.0f);
                alpha_anim.setDuration(300);
                alpha_anim.setFillAfter(true);
                alpha_anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        Animation alpha_anim_up = new AlphaAnimation(0.0f, 1.0f);
                        alpha_anim_up.setDuration(300);
                        alpha_anim_up.setStartOffset(200);
                        alpha_anim_up.setFillAfter(true);
                        alpha_anim_up.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                materialCalendarView.clearAnimation();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        materialCalendarView.setVisibility(View.VISIBLE);
                        materialCalendarView.startAnimation(alpha_anim_up);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        weeklyCalendarCntnr.setVisibility(View.GONE);
                        weeklyCalendarCntnr.clearAnimation();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                weeklyCalendarCntnr.startAnimation(alpha_anim);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RotateAnimation rotateAnim = new RotateAnimation(0.0f, 180.0f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);

                rotateAnim.setDuration(300);
                rotateAnim.setFillAfter(true);
                calendarUpDownArrow.startAnimation(rotateAnim);

                calendarExpanded = !calendarExpanded;
                topCalendarCntnr.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        topCalendarCntnr.startAnimation(anim);
    }

    private void collapseCalendarLayout(){
/*        ObjectAnimator animator = ObjectAnimator.ofFloat(topCalendarCntnr,"translationY",topCalendarCntnr.getHeight(),originalCalendarHeight);
        animator.setDuration(200);
        animator.start();*/

        final Animation anim = new ResizeAnimation(topCalendarCntnr,originalCalendarHeight,"collapse");
        anim.setDuration(200);
        anim.setRepeatCount(0);
        anim.setFillAfter(true);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Animation alpha_anim = new AlphaAnimation(1.0f, 0.0f);
                alpha_anim.setDuration(300);
                alpha_anim.setFillAfter(true);
                alpha_anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        weeklyCalendarCntnr.setVisibility(View.VISIBLE);
                        Animation alpha_anim_up = new AlphaAnimation(0.0f, 1.0f);
                        alpha_anim_up.setDuration(300);
                        alpha_anim_up.setStartOffset(200);
                        alpha_anim_up.setFillAfter(true);
                        alpha_anim_up.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                weeklyCalendarCntnr.clearAnimation();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        weeklyCalendarCntnr.startAnimation(alpha_anim_up);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        materialCalendarView.setVisibility(View.GONE);
                        materialCalendarView.clearAnimation();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                materialCalendarView.startAnimation(alpha_anim);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                topCalendarCntnr.clearAnimation();

                RotateAnimation rotateAnim = new RotateAnimation(180.0f, 0.0f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);

                rotateAnim.setDuration(300);
                rotateAnim.setFillAfter(true);
                calendarUpDownArrow.startAnimation(rotateAnim);

                calendarExpanded = !calendarExpanded;

                adjustWeeklyCalendar();
            }

            @Override
            public void onAnimationRepeat(Animation animation){

            }
        });

        topCalendarCntnr.startAnimation(anim);
    }

    private void detectLanguage() {
        SharedPreferences prefs = getActivity().getSharedPreferences("SmartCommunity", Activity.MODE_PRIVATE);
        String language = prefs.getString("last_language_picked", "noData");
        if (language.equals("iw")){
            currentLanguage = "hebrew";
        }else{
            currentLanguage = "english";
        }
    }

    private void handleItemsFiltering() {
        ArrayList<CalendarEventItem> dateEvents = eventsMap.get(latestDayPicked);
        ArrayList<CalendarEventItem> filteredResults = new ArrayList<CalendarEventItem>();
        int removeIndex = 0;

        if (dateEvents != null){
            int eventsSize = dateEvents.size();
            for (int i=0; i<eventsSize; i++){
                CalendarEventItem item = dateEvents.get(i);
                if (item.getType().equals("prayer") && showPrayers){
                    filteredResults.add(item);
                }

                if (item.getType().equals("activity") && showActivities){
                    filteredResults.add(item);
                }

                if (item.getType().equals("event") && showEvents){
                    filteredResults.add(item);
                }
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, latestDayPicked.getDay());
            calendar.set(Calendar.MONTH, latestDayPicked.getMonth());
            calendar.set(Calendar.YEAR, latestDayPicked.getYear());
            String dayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);

            eventsAdapter = new CalendarEventsAdapter(filteredResults,getActivity(), dayName,getActivity(),showPrayers,showActivities,showEvents);
            eventsAdapter.notifyDataSetChanged();
            calendarEventsList.setAdapter(eventsAdapter);
        }
    }

    private void setViewsReferences(View view) {
        calendarEventsList = (ListView) view.findViewById(R.id.day_events_list);
        leftArrow = (LinearLayout)view.findViewById(R.id.hsv_left_btn_container);
        rightArrow = (LinearLayout)view.findViewById(R.id.hsv_right_btn_container);
        downArrow = (LinearLayout)view.findViewById(R.id.calendar_month_btn_container);
        topCalendarCntnr = (RelativeLayout)view.findViewById(R.id.top_calendar_container);
        weeklyCalendarCntnr = (RelativeLayout)view.findViewById(R.id.weekly_calendar_cntnr);

        calendarUpDownArrow = (ImageView)view.findViewById(R.id.down_arrow);

        calendarContainer  = (LinearLayout)view.findViewById(R.id.calendar_dates_container);
        horizontalScroll = (HorizontalScrollView)view.findViewById(R.id.prayers_horizontal_scroll);
        monthText = (TextView)view.findViewById(R.id.prayers_month_text);
        noEventsText = (TextView)view.findViewById(R.id.no_events_text);

//        fab = (FloatingActionButton)view.findViewById(R.id.fab);
        fam = (FloatingActionMenu)view.findViewById(R.id.fam);

        prayers_fab = (FloatingActionButton)view.findViewById(R.id.fab_menu_prayer);
        activities_fab = (FloatingActionButton)view.findViewById(R.id.fab_menu_activities);
        events_fab = (FloatingActionButton)view.findViewById(R.id.fab_menu_events);

        materialCalendarView = (MaterialCalendarView)view.findViewById(R.id.calendarView);
        materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        calendarViewHeight = dpToPx(270);
    }

    private void arrangeCalendarEvents(ArrayList<DailyEvents> dailyEvents){
        if (eventsDays.size() > 0)
            eventsDays.clear();

        if (eventsMap.size() > 0)
            eventsMap.clear();

        for (int i=0; i<dailyEvents.size(); i++){
            DailyEvents singleDay = dailyEvents.get(i);
            ArrayList<CalendarEventItem> calendarTodayEvents = new ArrayList<CalendarEventItem>();
            String dailyEventDate = singleDay.getDate();

            ArrayList <EventOrMessage> dayEvents = singleDay.getEvents();
            for (int j=0; j<dayEvents.size();j++){
                EventOrMessage singleEvent = dayEvents.get(j);
                String id = singleEvent.getEventID();
                long startTime = singleEvent.getsTime();
                long endTime = singleEvent.geteTime();
                String title = singleEvent.getName();
                String buyLink = singleEvent.getBuyLink();

                boolean isFree = singleEvent.isFree();
                String attendingStatus = singleEvent.getAttendingStatus();
                LocationObj location = singleEvent.getLocationObj();
                ArrayList<CommunityContact> participants = singleEvent.getParticipants();

                calendarTodayEvents.add(new CalendarEventItem(id, startTime, endTime, title, "event", location,participants,buyLink,isFree,attendingStatus,dailyEventDate));
            }

            ArrayList<CommunityActivityOrFacility> dayActivities = singleDay.getActivities();
            for (int j=0; j<dayActivities.size(); j++){
                CommunityActivityOrFacility singleActivity = dayActivities.get(j);
                String title = singleActivity.getName();

                ArrayList <EventOrMessage> events = singleActivity.getEvents();
                for (int h=0; h<events.size(); h++){
                    EventOrMessage activityEvent =  singleActivity.getEvents().get(h);
                    long startTime = activityEvent.getsTime();
                    long endTime = activityEvent.geteTime();
                    boolean isFree = activityEvent.isFree();

                    String id = activityEvent.getEventID();
                    String buyLink = activityEvent.getBuyLink();
                    String attendingStatus = activityEvent.getAttendingStatus();
                    LocationObj location = activityEvent.getLocationObj();
                    ArrayList<CommunityContact> participants = activityEvent.getParticipants();

                    calendarTodayEvents.add(new CalendarEventItem(id,startTime,endTime,title,"activity",location,participants,buyLink,isFree,attendingStatus,dailyEventDate));
                }
            }

            ArrayList<PrayerDetails> dayPrayers = singleDay.getPrayers();
            for (int j=0; j<dayPrayers.size(); j++){
                PrayerDetails singlePrayer = dayPrayers.get(j);
                ArrayList<SmallPrayer> smallPrayers = singlePrayer.getSmallPrayers();

                for (int h=0; h<smallPrayers.size(); h++){
                    SmallPrayer smallPrayer = smallPrayers.get(h);
                    long time = singlePrayer.getDate();
                    String timeString = smallPrayer.getTime();
                    String title = smallPrayer.getTitle();
                    String location = "";
                    String parasha = singlePrayer.getParashaName();
                    calendarTodayEvents.add(new CalendarEventItem(timeString,title,location,"prayer",parasha,time));
                }
            }

            String singleDayDate [] = singleDay.getDate().split("/");
            int cDay = Integer.valueOf(singleDayDate[0]);
            int cMonth = Integer.valueOf(singleDayDate[1]) - 1;
            int cYear = Integer.valueOf(singleDayDate[2]);

            CalendarDay day = new CalendarDay(cYear,cMonth,cDay);

            if (calendarTodayEvents.size() > 0){
                ArrayList<CalendarEventItem> arrangedEvents = calendarUpcomingOrderEvents(calendarTodayEvents);
                eventsDays.add(day);
                eventsMap.put(day, arrangedEvents);
            }
        }

        AppUser appUserInstance = AppUser.getInstance();
        appUserInstance.setEventsMap(eventsMap);
        appUserInstance.setEventsDays(eventsDays);
    }

    private ArrayList<CalendarEventItem> calendarUpcomingOrderEvents(ArrayList<CalendarEventItem> calendarTodayEvents) {
        ArrayList<CalendarEventItem> tempCalendarEvents = new ArrayList<CalendarEventItem>();

        for (int i = 0; i < calendarTodayEvents.size(); i++){
            CalendarEventItem tempCalendarEvent = calendarTodayEvents.get(i);
            long sTime;
            if (tempCalendarEvent.getType().equals("prayer"))
                sTime = getMillisFromPrayer(tempCalendarEvent);
            else
                sTime = tempCalendarEvent.getEventStartTime();

            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            Calendar cal = Calendar.getInstance(timeZone);
            cal.setTimeInMillis(sTime);
            Date newEventSDate = cal.getTime();

            if (tempCalendarEvents.size() == 0){
                tempCalendarEvents.add(tempCalendarEvent);
            }else{
                int tempCalendarEventsSize = tempCalendarEvents.size();
                boolean addedItem = false;

                for (int j = 0; j < tempCalendarEventsSize; j++) {
                    CalendarEventItem eventItem = tempCalendarEvents.get(j);

                    long eventDateLong;
                    if (!eventItem.getType().equals("prayer"))
                        eventDateLong = eventItem.getEventStartTime();
                    else
                        eventDateLong = eventItem.getEventTime();

                    Calendar eventCal = Calendar.getInstance(timeZone);
                    eventCal.setTimeInMillis(eventDateLong);
                    Date existingEventDate = eventCal.getTime();

                    if (newEventSDate.before(existingEventDate)){
                        tempCalendarEvents.add(j, tempCalendarEvent);
                        addedItem = true;
                    }

                    if (addedItem)
                        break;
                }

                if (!addedItem)
                    tempCalendarEvents.add(tempCalendarEvent);
            }
        }

        return tempCalendarEvents;
    }

    private long getMillisFromPrayer(CalendarEventItem prayer){
        int prayerHour;
        int prayerMinutes;

        long prayerTime = prayer.getPrayerMillis();
        String [] timeArray = prayer.getEventTimeString().split(":");

        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone);
        if (timeArray.length > 1){
            calendar.setTimeInMillis(prayerTime);

            prayerHour = Integer.valueOf(timeArray[0]);
            prayerMinutes = Integer.valueOf(timeArray[1]);

            calendar.set(Calendar.HOUR_OF_DAY, prayerHour);
            calendar.set(Calendar.MINUTE, prayerMinutes);
        }

        return calendar.getTimeInMillis();
    }

    private void getScreenWidth() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
    }

    private View getNextEventDateItem(final int pos, final HorizontalScrollView scrollHorizontal, final CalendarDay day, final int today, final String month_status){
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.next_event_date_item, null);

        final LinearLayout container = (LinearLayout)view.findViewById(R.id.date_item_container);
        final LinearLayout sub_container = (LinearLayout)view.findViewById(R.id.date_item_sub_container);
        TextView dayText = (TextView)view.findViewById(R.id.date_item_daytext);
        TextView dayNumber = (TextView)view.findViewById(R.id.date_item_daynum);

        ImageView orangeDot = (ImageView)view.findViewById(R.id.event_orange_dot);
        ImageView greenDot = (ImageView)view.findViewById(R.id.event_green_dot);
        ImageView blueDot = (ImageView)view.findViewById(R.id.event_blue_dot);

        final ArrayList<CalendarEventItem> items = eventsMap.get(day);
        if (items != null){
            boolean hasEvent = false;
            boolean hasActivities = false;
            boolean hasPrayers = false;
            for (int i=0; i<items.size(); i++){
                if (!(hasEvent && hasActivities && hasPrayers)){
                    CalendarEventItem singleItem = items.get(i);
                    switch (singleItem.getType()){
                        case "event":
                            hasEvent = true;
                            break;

                        case "activity":
                            hasActivities = true;
                            break;

                        case "prayer":
                            hasPrayers = true;
                            break;

                        default:
                            break;
                    }
                }else{
                    break;
                }
            }
            if (hasEvent)
                greenDot.setVisibility(View.VISIBLE);
            if (hasActivities)
                blueDot.setVisibility(View.VISIBLE);
            if (hasPrayers)
                orangeDot.setVisibility(View.VISIBLE);

            if (!hasActivities && !hasEvent && !hasPrayers){
                orangeDot.setVisibility(View.INVISIBLE);
            }
        }else{
            orangeDot.setVisibility(View.INVISIBLE);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day.getDay());
        calendar.set(Calendar.MONTH, day.getMonth());
        calendar.set(Calendar.YEAR, day.getYear());
        final String dayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());

        dayText.setText(dayName);
        dayNumber.setText(String.valueOf(pos + 1));

        if (today == day.getDay() && day.getMonth() == originalMonth && day.getYear() == originalYear){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    materialCalendarView.clearSelection();
                    materialCalendarView.setCurrentDate(day);
                    materialCalendarView.setDateSelected(day, true);
                }
            });

            dayText.setTypeface(dayText.getTypeface(), Typeface.BOLD);
            dayNumber.setTypeface(dayNumber.getTypeface(), Typeface.BOLD);

            dayText.setTextColor(ContextCompat.getColor(getActivity(), R.color.AceentColor));
            dayNumber.setTextColor(ContextCompat.getColor(getActivity(), R.color.AceentColor));

            sub_container.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.Gray));
            latestCalendarposition = today;
            latestDayPicked = day;

            if (items != null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        eventsAdapter = new CalendarEventsAdapter(items,getActivity(),dayName,getActivity(),showPrayers,showActivities,showEvents);
                        eventsAdapter.notifyDataSetChanged();
                        calendarEventsList.setAdapter(eventsAdapter);
                        noEventsText.setVisibility(View.GONE);
                        calendarEventsList.setVisibility(View.VISIBLE);
                    }
                });
            }else{
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run(){
                        calendarEventsList.setAdapter(null);
                        calendarEventsList.setVisibility(View.INVISIBLE);
                        noEventsText.setVisibility(View.VISIBLE);
                    }
                });
            }
        }

        if (month_status.equals("nextMonth")){
            dayText.setTypeface(Typeface.DEFAULT);
            dayNumber.setTypeface(Typeface.DEFAULT);

            dayText.setTextColor(getActivity().getResources().getColor(R.color.DividerColor));
            dayNumber.setTextColor(getActivity().getResources().getColor(R.color.DividerColor));
        }

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (month_status.equals("currentMonth")) {
                    if (latestCalendarposition != day.getDay()) {
                        weeklyClndrDayPick(day, dayName);

                        int scrollX = (container.getLeft() - (screenWidth / 2)) + (container.getWidth() / 2);
                        scrollHorizontal.smoothScrollTo(scrollX, 0);

                        latestCalendarposition = day.getDay();
                        latestDayPicked = day;
                    }
                } else {
                    nextMonth();
                }
            }
        });
        return view;
    }

    private void weeklyClndrDayPick(CalendarDay day, String dayName){
        materialCalendarView.clearSelection();
        materialCalendarView.setCurrentDate(day);
        materialCalendarView.setDateSelected(day, true);

        changeDateBG(day);

        ArrayList<CalendarEventItem> dateEvents = eventsMap.get(day);
        if (dateEvents != null){
            eventsAdapter = new CalendarEventsAdapter(dateEvents,getActivity(), dayName,getActivity(),showPrayers,showActivities,showEvents);
            eventsAdapter.notifyDataSetChanged();
            calendarEventsList.setAdapter(eventsAdapter);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    noEventsText.setVisibility(View.GONE);
                    calendarEventsList.setVisibility(View.VISIBLE);
                }
            });
        }else{
            calendarEventsList.setAdapter(null);
            calendarEventsList.setVisibility(View.INVISIBLE);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    noEventsText.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void changeDateBG(CalendarDay day) {
        View childView = calendarContainer.getChildAt(latestCalendarposition-1);
        TextView prevDayText = (TextView)childView.findViewById(R.id.date_item_daytext);
        TextView prevDayNumber = (TextView)childView.findViewById(R.id.date_item_daynum);
        LinearLayout prevCont = (LinearLayout)childView.findViewById(R.id.date_item_sub_container);

        if (latestCalendarposition != todayPosition){
            prevDayText.setTextColor(getActivity().getResources().getColor(R.color.SecondaryText));
            prevDayNumber.setTextColor(getActivity().getResources().getColor(R.color.SecondaryText));
        }

        prevCont.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));

        prevDayText.setTypeface(prevDayText.getTypeface(), Typeface.NORMAL);
        prevDayNumber.setTypeface(prevDayText.getTypeface(), Typeface.NORMAL);

        View currentView = calendarContainer.getChildAt(day.getDay() - 1);
        TextView DayText = (TextView)currentView.findViewById(R.id.date_item_daytext);
        TextView DayNumber = (TextView)currentView.findViewById(R.id.date_item_daynum);
        LinearLayout Cont = (LinearLayout)currentView.findViewById(R.id.date_item_sub_container);

        if (day.getDay() == todayPosition && day.getMonth() == originalMonth && day.getYear() == originalYear){
            DayText.setTextColor(getActivity().getResources().getColor(R.color.AceentColor));
            DayNumber.setTextColor(getActivity().getResources().getColor(R.color.AceentColor));
        }else{
            DayText.setTextColor(getActivity().getResources().getColor(R.color.PrimaryText));
            DayNumber.setTextColor(getActivity().getResources().getColor(R.color.PrimaryText));
        }
        Cont.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.Gray));

        prevDayText.setTypeface(prevDayText.getTypeface(), Typeface.BOLD);
        prevDayNumber.setTypeface(prevDayText.getTypeface(), Typeface.BOLD);
    }

    private void initReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("refresh_done");
        refreshReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra("Status");
                if (status.equals("Success")){
                    new createCalendarInBackground().execute();
                }
            }
        };
        getActivity().registerReceiver(refreshReceiver, filter);
    }

    private void adjustWeeklyCalendar(){
        int adjustedDay = latestDayPicked.getDay();
        int adjustedMonth = latestDayPicked.getMonth();
        int adjustedYear = latestDayPicked.getYear();

        if (adjustedMonth != currentMonth || adjustedYear != currentYear){
            currentMonth = adjustedMonth;
            currentYear = adjustedYear;

            if (currentLanguage.equalsIgnoreCase("english"))
                monthText.setText(monthesNames[currentMonth]);
            else
                monthText.setText(hebrewMonthesNames[currentMonth]);

            Calendar mycal = new GregorianCalendar(currentYear, currentMonth, 1);
            MONTH_DAY_COUNT = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            calendarContainer.removeAllViews();

            for (int j=0; j<MONTH_DAY_COUNT; j++){
                CalendarDay eachDay = new CalendarDay(currentYear,currentMonth,j+1);
                calendarContainer.addView(getNextEventDateItem(j,horizontalScroll,eachDay,day,"currentMonth"));
            }

            for (int j=0; j<3; j++){
                CalendarDay eachDay;
                if (currentMonth > 10){
                    eachDay = new CalendarDay(currentYear+1,0,j+1);
                }else{
                    eachDay = new CalendarDay(currentYear,currentMonth+1,j+1);
                }

                calendarContainer.addView(getNextEventDateItem(j,horizontalScroll,eachDay,day,"nextMonth"));
            }

            horizontalScroll.post(new Runnable(){
                @Override
                public void run(){
                    int scrollCenter = horizontalScroll.getWidth() / 2;
                    View v = calendarContainer.getChildAt(latestDayPicked.getDay() - 1);
                    int scrollToX = (v.getLeft() + v.getWidth() / 2) - scrollCenter;
                    horizontalScroll.smoothScrollTo(scrollToX, 0);
                }
            });
        }else{
            if (adjustedDay != latestCalendarposition){
                horizontalScroll.post(new Runnable(){
                    @Override
                    public void run(){
                        int scrollCenter = horizontalScroll.getWidth() / 2;
                        View v = calendarContainer.getChildAt(latestDayPicked.getDay() - 1);
                        int scrollToX = (v.getLeft() + v.getWidth() / 2) - scrollCenter;
                        horizontalScroll.smoothScrollTo(scrollToX, 0);
                    }
                });
            }
        }

        changeDateBG(latestDayPicked);
        latestCalendarposition = adjustedDay;
    }

    private void nextMonth() {
        latestCalendarposition = 1;
        if (currentMonth < 11){
            currentMonth = currentMonth + 1;
        }else{
            currentMonth = 0;
            currentYear = currentYear + 1;
        }

        if (currentLanguage.equalsIgnoreCase("english"))
            monthText.setText(monthesNames[currentMonth]);
        else
            monthText.setText(hebrewMonthesNames[currentMonth]);

        Calendar mycal = new GregorianCalendar(currentYear, currentMonth, 1);
        MONTH_DAY_COUNT = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        calendarContainer.removeAllViews();

        for (int j=0; j<MONTH_DAY_COUNT; j++){
            CalendarDay eachDay = new CalendarDay(currentYear,currentMonth,j+1);
            calendarContainer.addView(getNextEventDateItem(j,horizontalScroll,eachDay,day,"currentMonth"));
        }

        for (int j=0; j<3; j++){
            CalendarDay eachDay;
            if (currentMonth > 10){
                eachDay = new CalendarDay(currentYear+1,0,j+1);
            }else{
                eachDay = new CalendarDay(currentYear,currentMonth+1,j+1);
            }

            calendarContainer.addView(getNextEventDateItem(j,horizontalScroll,eachDay,day,"nextMonth"));
        }

        horizontalScroll.post(new Runnable(){
            @Override
            public void run(){
                if (currentMonth == originalMonth && currentYear == originalYear) {
                    int scrollCenter = horizontalScroll.getWidth() / 2;
                    View v = calendarContainer.getChildAt(todayPosition - 1);
                    int scrollToX = (v.getLeft() + v.getWidth() / 2) - scrollCenter;
                    horizontalScroll.smoothScrollTo(scrollToX, 0);
                }else{
                    horizontalScroll.smoothScrollTo(0, 0);
                }
            }
        });
    }

    private void previousMonth() {
        latestCalendarposition = 1;
        if (currentMonth > 0) {
            currentMonth = currentMonth - 1;
        }else {
            currentMonth = 11;
            currentYear = currentYear - 1;
        }

        if (currentLanguage.equalsIgnoreCase("english"))
            monthText.setText(monthesNames[currentMonth]);
        else
            monthText.setText(hebrewMonthesNames[currentMonth]);

        Calendar mycal = new GregorianCalendar(currentYear, currentMonth, 1);
        MONTH_DAY_COUNT = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        calendarContainer.removeAllViews();

        for (int j=0; j<MONTH_DAY_COUNT; j++){
            CalendarDay eachDay = new CalendarDay(currentYear, currentMonth,j+1);
            calendarContainer.addView(getNextEventDateItem(j,horizontalScroll,eachDay,day,"currentMonth"));
        }

        for (int j=0; j<3; j++){
            CalendarDay eachDay;
            if (currentMonth > 10){
                eachDay = new CalendarDay(currentYear+1,0,j+1);
            }else{
                eachDay = new CalendarDay(currentYear,currentMonth+1,j+1);
            }

            calendarContainer.addView(getNextEventDateItem(j,horizontalScroll,eachDay,day,"nextMonth"));
        }

        horizontalScroll.post(new Runnable() {
            @Override
            public void run() {
                if (currentMonth == originalMonth && currentYear == originalYear) {
                    int scrollCenter = horizontalScroll.getWidth() / 2;
                    View v = calendarContainer.getChildAt(todayPosition - 1);
                    int scrollToX = (v.getLeft() + v.getWidth() / 2) - scrollCenter;
                    horizontalScroll.smoothScrollTo(scrollToX, 0);
                } else {
                    horizontalScroll.smoothScrollTo(0, 0);
                }
            }
        });
    }

    private ArrayList<View> getCalendarViews(int year, int month, int day){
        ArrayList<View> finalViews = new ArrayList<View>();
        for (int j=0; j<MONTH_DAY_COUNT; j++){
            CalendarDay eachDay = new CalendarDay(year,month,j+1);
            finalViews.add(getNextEventDateItem(j, horizontalScroll, eachDay,day,"currentMonth"));
        }

        for (int j=0; j<3; j++){
            CalendarDay eachDay;
            if (month > 10){
                eachDay = new CalendarDay(year+1,0,j+1);
            }else{
                eachDay = new CalendarDay(year,month+1,j+1);
            }

            finalViews.add(getNextEventDateItem(j, horizontalScroll, eachDay, day, "nextMonth"));
        }

        return finalViews;
    }

    private class createCalendarInBackground extends AsyncTask<String, Void, ArrayList<View>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            calendarContainer.setVisibility(View.INVISIBLE);
            if (calendarContainer.getChildCount() > 0)
                calendarContainer.removeAllViews();
        }

        @Override
        protected ArrayList<View> doInBackground(String... params) {
            AppUser appUserInstance = AppUser.getInstance();
            ArrayList<DailyEvents> dailyEvents = appUserInstance.getCurrentCommunity().getDailyEvents();
            arrangeCalendarEvents(dailyEvents);

            Calendar calendar = Calendar.getInstance();
            final int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            todayPosition = day;
            currentMonth = month;
            currentYear = year;

            originalMonth = month;
            originalYear = year;

            Calendar mycal = new GregorianCalendar(year, month, day);
            MONTH_DAY_COUNT = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

            ArrayList<View> calendarViews = getCalendarViews(year, month, day);
            return calendarViews;
        }

        @Override
        protected void onPostExecute(ArrayList<View> viewsArray){
            super.onPostExecute(viewsArray);
            if (currentLanguage.equalsIgnoreCase("english"))
                monthText.setText(monthesNames[originalMonth]);
            else
                monthText.setText(hebrewMonthesNames[originalMonth]);

            for (int i=0; i<viewsArray.size(); i++){
                View dateView = viewsArray.get(i);
                calendarContainer.addView(dateView);
            }

            horizontalScroll.postDelayed(new Runnable() {
                @Override
                public void run(){
                    int scrollCenter = horizontalScroll.getScrollX() + horizontalScroll.getWidth() / 2;
                    View v = calendarContainer.getChildAt(todayPosition - 1);
                    if (v != null){
                        int scrollToX = (v.getLeft() + v.getWidth() / 2) - scrollCenter;
                        horizontalScroll.smoothScrollTo(scrollToX, 0);
                        calendarContainer.setVisibility(View.VISIBLE);
                    }

                    topRelativeHeight = topCalendarCntnr.getHeight();
                    originalCalendarHeight = topCalendarCntnr.getHeight();
                }
            }, 500);

            initMaterialCalendar();
        }
    }

    public int dpToPx(int dp){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    @Override
    public void onDestroy(){
        getActivity().unregisterReceiver(refreshReceiver);
        super.onDestroy();
    }

    public boolean contains(final Integer[] array, final Integer key) {
        return Arrays.asList(array).contains(key);
    }
}
