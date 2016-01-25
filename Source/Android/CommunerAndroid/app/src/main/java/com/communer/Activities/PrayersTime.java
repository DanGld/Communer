package com.communer.Activities;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.communer.Adapters.PrayersTimeAdapter;
import com.communer.Models.DailyEvents;
import com.communer.Models.PrayerDetails;
import com.communer.Models.SmallPrayer;
import com.communer.R;
import com.communer.Utils.AppUser;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Created by יובל on 21/10/2015.
 */
public class PrayersTime extends AppCompatActivity {

    private Toolbar toolbar;

    private HorizontalScrollView horizontalScroll;
    private LinearLayout calendarContainer, holidayContainer;
    private LinearLayout leftArrow, rightArrow;

    private int MONTH_DAY_COUNT;
    private int screenWidth;
    private int todayPosition=0;
    private int latestCalendarposition;

//    Map<CalendarDay,ArrayList<SmallPrayer>> prayersMap;
    Map<CalendarDay,PrayerDetails> prayersMap;

    private String [] monthesNames = {"January","February","March","April",
            "May","June","July","August","September",
            "October","November","December"};

    private TextView monthText;
    private int currentMonth;
    private int currentYear;

    private int originalMonth;
    private int originalYear;

    private ListView prayersList;
    private PrayersTimeAdapter adapter;

    private TextView morning_service, mincha_service, evening_service, parasha_name, holiday_start, holiday_end;
    private LinearLayout additions_container, morningLayout, minchaLayout, eveningLayout;

/*    private HashMap<String,String> morningHash;
    private HashMap<String,String> minchaHash;
    private HashMap<String,String> eveningHash;*/
    private String morningServicesAsString = "";
    private String minchaServicesAsString = "";
    private String eveningServicesAsString = "";

    private String mixPanelProjectToken = "537b3567fe348d6c5dfa0d6e2ae688b3";
    private MixpanelAPI mixpanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prayers_time);

        mixpanel = MixpanelAPI.getInstance(PrayersTime.this, mixPanelProjectToken);
        mixpanel.track("Modules - Prayers times Page");

        initToolbar();

        leftArrow = (LinearLayout)findViewById(R.id.hsv_left_btn_container);
        rightArrow = (LinearLayout)findViewById(R.id.hsv_right_btn_container);
        prayersList = (ListView)findViewById(R.id.prayers_times_list);

        morning_service = (TextView)findViewById(R.id.morning_service);
        mincha_service = (TextView)findViewById(R.id.mincha_service);
        evening_service = (TextView)findViewById(R.id.evening_service);
        parasha_name = (TextView)findViewById(R.id.prayers_time_parasha_name);
        holiday_start = (TextView)findViewById(R.id.holiday_start_time);
        holiday_end = (TextView)findViewById(R.id.holiday_end_time);
        additions_container = (LinearLayout)findViewById(R.id.additions_container);
        morningLayout = (LinearLayout)findViewById(R.id.linearMorningLayout);
        minchaLayout = (LinearLayout)findViewById(R.id.linearMinchaLayout);
        eveningLayout = (LinearLayout)findViewById(R.id.linearEveningLayout);

        prayersMap = new HashMap<CalendarDay,PrayerDetails>();

        AppUser appUserInstance = AppUser.getInstance();
        ArrayList<DailyEvents> dailyEvents = appUserInstance.getCurrentCommunity().getDailyEvents();

        for (int i=0; i<dailyEvents.size(); i++){
            boolean hasPrayers = false;
            DailyEvents singleDay = dailyEvents.get(i);
            String singleDayDate [] = singleDay.getDate().split("/");

            int cDay = Integer.valueOf(singleDayDate[0]);
            int cMonth = Integer.valueOf(singleDayDate[1]) - 1;
            int cYear = Integer.valueOf(singleDayDate[2]);
            CalendarDay day = new CalendarDay(cYear,cMonth,cDay);

            if (singleDay.getPrayers().size() > 0){
                PrayerDetails dayPrayers = singleDay.getPrayers().get(0);
                prayersMap.put(day, dayPrayers);
            }else{
                prayersMap.put(day, null);
            }
        }

        calendarContainer  = (LinearLayout)findViewById(R.id.calendar_dates_container);
        holidayContainer  = (LinearLayout)findViewById(R.id.holiday_container);
        horizontalScroll = (HorizontalScrollView)findViewById(R.id.prayers_horizontal_scroll);
        monthText = (TextView)findViewById(R.id.prayers_month_text);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        createHorizontalCalendar();
        horizontalScroll.post(new Runnable() {
            @Override
            public void run() {
                int scrollCenter = horizontalScroll.getScrollX() + horizontalScroll.getWidth() / 2;
                View v = calendarContainer.getChildAt(todayPosition - 1);
                int scrollToX = (v.getLeft() + v.getWidth() / 2) - scrollCenter;
                horizontalScroll.smoothScrollTo(scrollToX, 0);
            }
        });

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

/*        prayersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SmallPrayer item = (SmallPrayer) adapter.getItem(position);
                openServiceDialog(item.getTitle(), item.getTime());
            }
        });*/

        morningLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openServiceDialog("Shacharit Services");
            }
        });

        minchaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openServiceDialog("Mincha Services");
            }
        });

        eveningLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openServiceDialog("Maariv Services");
            }
        });
    }

    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle(getResources().getString(R.string.prayers_time_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    public void createHorizontalCalendar() {
        Calendar calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        todayPosition = day;
        currentMonth = month;
        currentYear = year;

        originalMonth = month;
        originalYear = year;

        monthText.setText(monthesNames[month]);

        Calendar mycal = new GregorianCalendar(year, month, day);
        MONTH_DAY_COUNT = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int j=0; j<MONTH_DAY_COUNT; j++){
            CalendarDay eachDay = new CalendarDay(year,month,j+1);
            calendarContainer.addView(getNextEventDateItem(j,horizontalScroll,eachDay,day,"currentMonth"));
        }

        for (int j=0; j<3; j++){
            CalendarDay eachDay;
            if (month > 10){
                eachDay = new CalendarDay(year+1,0,j+1);
            }else{
                eachDay = new CalendarDay(year,month+1,j+1);
            }

            calendarContainer.addView(getNextEventDateItem(j,horizontalScroll,eachDay,day,"nextMonth"));
        }
    }

    private void nextMonth() {
        if (currentMonth < 11){
            currentMonth = currentMonth + 1;
        }else{
            currentMonth = 0;
            currentYear = currentYear + 1;
        }

        monthText.setText(monthesNames[currentMonth]);

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

        horizontalScroll.post(new Runnable() {
            @Override
            public void run() {
                if (currentMonth == originalMonth && currentYear == originalYear) {
                    int scrollCenter = horizontalScroll.getScrollX() + horizontalScroll.getWidth() / 2;
                    View v = calendarContainer.getChildAt(todayPosition - 1);
                    int scrollToX = (v.getLeft() + v.getWidth() / 2) - scrollCenter;
                    horizontalScroll.smoothScrollTo(scrollToX, 0);
                } else {
                    horizontalScroll.smoothScrollTo(0, 0);
                }
            }
        });
    }

    private void previousMonth() {
        if (currentMonth > 0) {
            currentMonth = currentMonth - 1;
        }else {
            currentMonth = 11;
            currentYear = currentYear - 1;
        }

        monthText.setText(monthesNames[currentMonth]);

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

        horizontalScroll.post(new Runnable() {
            @Override
            public void run() {
                if (currentMonth == originalMonth && currentYear == originalYear) {
                    int scrollCenter = horizontalScroll.getScrollX() + horizontalScroll.getWidth() / 2;
                    View v = calendarContainer.getChildAt(todayPosition - 1);
                    int scrollToX = (v.getLeft() + v.getWidth() / 2) - scrollCenter;
                    horizontalScroll.smoothScrollTo(scrollToX, 0);
                } else {
                    horizontalScroll.smoothScrollTo(0, 0);
                }
            }
        });
    }

    private View getNextEventDateItem(final int pos, final HorizontalScrollView scrollHorizontal, final CalendarDay day, final int today, final String month_status){
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.next_event_date_item, null);

        final LinearLayout container = (LinearLayout)view.findViewById(R.id.date_item_container);
        final LinearLayout sub_container = (LinearLayout)view.findViewById(R.id.date_item_sub_container);
        TextView dayText = (TextView)view.findViewById(R.id.date_item_daytext);
        TextView dayNumber = (TextView)view.findViewById(R.id.date_item_daynum);

        ImageView orangeDot = (ImageView)view.findViewById(R.id.event_orange_dot);

        PrayerDetails prayerDetails = prayersMap.get(day);
        ArrayList<SmallPrayer> items = null;
        if (prayerDetails != null){
            items = prayerDetails.getSmallPrayers();
            if (items != null){
                if (items.size() > 0) {
                    orangeDot.setVisibility(View.VISIBLE);
                }else {
                    orangeDot.setVisibility(View.INVISIBLE);
                }
            }else{
                orangeDot.setVisibility(View.INVISIBLE);
            }
        }else{
            orangeDot.setVisibility(View.INVISIBLE);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day.getDay());
        calendar.set(Calendar.MONTH, day.getMonth());
        calendar.set(Calendar.YEAR, day.getYear());
        String dayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);

        dayText.setText(dayName);
        dayNumber.setText(String.valueOf(pos + 1));

        if (today == day.getDay() && day.getMonth() == originalMonth && day.getYear() == originalYear){
            dayText.setTypeface(dayText.getTypeface(), Typeface.BOLD);
            dayNumber.setTypeface(dayNumber.getTypeface(), Typeface.BOLD);

            dayText.setTextColor(ContextCompat.getColor(PrayersTime.this, R.color.AceentColor));
            dayNumber.setTextColor(ContextCompat.getColor(PrayersTime.this, R.color.AceentColor));

            sub_container.setBackgroundColor(ContextCompat.getColor(PrayersTime.this, R.color.Gray));
            latestCalendarposition = today;

            if (items != null){
                holiday_start.setText(prayerDetails.getHolidayStart());
                holiday_end.setText(prayerDetails.getHolidayEnd());
                parasha_name.setText(prayerDetails.getParashaName());

                ArrayList<SmallPrayer> todayUnimportantPrayers = prayerDetails.getShabatPrayers();
                adapter = new PrayersTimeAdapter(todayUnimportantPrayers,PrayersTime.this);
                prayersList.setAdapter(adapter);

                adjustSmallPrayers(items);
            }else{
                prayersList.setAdapter(null);
                morning_service.setText("");
                mincha_service.setText("");
                evening_service.setText("");
                parasha_name.setText("");

                holidayContainer.setVisibility(View.GONE);
            }

            if (prayerDetails != null){
                ArrayList<String> additions = prayerDetails.getAdditions();
                if (additions.size() %2 == 0){
                    for (int j=0; j<additions.size()/2; j++){
                        LayoutInflater inflr = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View inflrView = inflr.inflate(R.layout.prayers_times_additions, null);

                        TextView additionRight = (TextView)inflrView.findViewById(R.id.prayers_addition_right);
                        TextView additionLeft = (TextView)inflrView.findViewById(R.id.prayers_addition_left);

                        String firstAddition = additions.get(j*2);
                        String secondAddition = additions.get(j*2 + 1);

                        additionRight.setText(firstAddition);
                        additionLeft.setText(secondAddition);

                        if(additions_container.getParent()!=null)
                            ((ViewGroup)additions_container.getParent()).removeView(additions_container);

                        additions_container.addView(inflrView);
                    }
                }else{
                    for (int j=0; j<additions.size()/2 + 1; j++){
                        LayoutInflater inflr = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View inflrView = inflr.inflate(R.layout.prayers_times_additions, null);

                        TextView additionRight = (TextView)inflrView.findViewById(R.id.prayers_addition_right);
                        TextView additionLeft = (TextView)inflrView.findViewById(R.id.prayers_addition_left);

                        String firstAddition = "";
                        String secondAddition = "";

                        if (j < additions.size()/2){
                            firstAddition = additions.get(j*2);
                            secondAddition = additions.get(j*2 +1);
                        }else{
                            firstAddition = additions.get(j*2);
                        }

                        additionRight.setText(firstAddition);
                        additionLeft.setText(secondAddition);

                        additions_container.addView(inflrView);
                    }
                }
            }

            setListViewHeightBasedOnChildren(prayersList);
        }

        if (month_status.equals("nextMonth")){
            dayText.setTypeface(Typeface.DEFAULT);
            dayNumber.setTypeface(Typeface.DEFAULT);

            dayText.setTextColor(ContextCompat.getColor(PrayersTime.this, R.color.DividerColor));
            dayNumber.setTextColor(ContextCompat.getColor(PrayersTime.this, R.color.DividerColor));
        }

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (month_status.equals("currentMonth")) {
                    if (latestCalendarposition != day.getDay()) {
                        View childView = calendarContainer.getChildAt(latestCalendarposition - 1);
                        TextView prevDayText = (TextView) childView.findViewById(R.id.date_item_daytext);
                        TextView prevDayNumber = (TextView) childView.findViewById(R.id.date_item_daynum);
                        LinearLayout prevCont = (LinearLayout) childView.findViewById(R.id.date_item_sub_container);

                        if (latestCalendarposition != todayPosition) {
                            prevDayText.setTextColor(PrayersTime.this.getResources().getColor(R.color.SecondaryText));
                            prevDayNumber.setTextColor(PrayersTime.this.getResources().getColor(R.color.SecondaryText));
                        }

                        prevCont.setBackgroundColor(ContextCompat.getColor(PrayersTime.this, R.color.white));

                        prevDayText.setTypeface(prevDayText.getTypeface(), Typeface.NORMAL);
                        prevDayNumber.setTypeface(prevDayText.getTypeface(), Typeface.NORMAL);

                        View currentView = calendarContainer.getChildAt(day.getDay() - 1);
                        TextView DayText = (TextView) currentView.findViewById(R.id.date_item_daytext);
                        TextView DayNumber = (TextView) currentView.findViewById(R.id.date_item_daynum);
                        LinearLayout Cont = (LinearLayout) currentView.findViewById(R.id.date_item_sub_container);

                        if (day.getDay() == todayPosition && day.getMonth() == originalMonth && day.getYear() == originalYear) {
                            DayText.setTextColor(ContextCompat.getColor(PrayersTime.this, R.color.AceentColor));
                            DayNumber.setTextColor(ContextCompat.getColor(PrayersTime.this, R.color.AceentColor));
                        } else {
                            DayText.setTextColor(ContextCompat.getColor(PrayersTime.this, R.color.PrimaryText));
                            DayNumber.setTextColor(ContextCompat.getColor(PrayersTime.this, R.color.PrimaryText));
                        }
                        Cont.setBackgroundColor(ContextCompat.getColor(PrayersTime.this, R.color.Gray));

                        prevDayText.setTypeface(prevDayText.getTypeface(), Typeface.BOLD);
                        prevDayNumber.setTypeface(prevDayText.getTypeface(), Typeface.BOLD);

                        PrayerDetails details = prayersMap.get(day);
                        if (details != null) {
                            holiday_start.setText(details.getHolidayStart());
                            holiday_end.setText(details.getHolidayEnd());

                            ArrayList<SmallPrayer> todayMainPrayers = details.getSmallPrayers();
                            ArrayList<SmallPrayer> todayUnimportantPrayers = details.getShabatPrayers();

                            if (todayUnimportantPrayers != null){
                                adapter = new PrayersTimeAdapter(todayUnimportantPrayers, PrayersTime.this);
                                adapter.notifyDataSetChanged();
                                adjustSmallPrayers(todayMainPrayers);
                            }else{
                                prayersList.setAdapter(null);
                                morning_service.setText("");
                                mincha_service.setText("");
                                evening_service.setText("");
                            }

                            String parasha = details.getParashaName();
                            parasha_name.setText(parasha);
                        }else{
                            prayersList.setAdapter(null);
                            parasha_name.setText("");
                            morning_service.setText("");
                            mincha_service.setText("");
                            evening_service.setText("");
                            holiday_start.setText("");
                            holiday_end.setText("");
                            additions_container.removeAllViews();
                            holidayContainer.setVisibility(View.GONE);
                        }

                        setListViewHeightBasedOnChildren(prayersList);

                        int scrollX = (container.getLeft() - (screenWidth / 2)) + (container.getWidth() / 2);
                        scrollHorizontal.smoothScrollTo(scrollX, 0);

                        latestCalendarposition = day.getDay();
                    }
                } else {
                    nextMonth();
                }
            }
        });
        return view;
    }

    private void adjustSmallPrayers(ArrayList<SmallPrayer> prayers) {
        ArrayList<String> morningPrayers = new ArrayList<String>();
        ArrayList<String> minchaPrayers = new ArrayList<String>();
        ArrayList<String> eveningPrayers = new ArrayList<String>();

        if (!morningServicesAsString.equals("")){
            morningServicesAsString = "";
            minchaServicesAsString = "";
            eveningServicesAsString = "";
        }

        for (int i=0; i<prayers.size(); i++){
            SmallPrayer item = prayers.get(i);
            String serviceTime = item.getTime();
            String serviceTitle = item.getTitle();
            String serviceType = item.getType();

            switch (serviceType){
                case "morning":
                    morningServicesAsString = morningServicesAsString + serviceTitle + " - " + serviceTime + "\n";
//                    morningHash.put(serviceTitle,serviceTime);
                    morningPrayers.add(serviceTime);
                    break;

                case "mincha":
                    minchaServicesAsString = minchaServicesAsString + serviceTitle + " - " + serviceTime + "\n";
//                    minchaHash.put(serviceTitle,serviceTime);
                    minchaPrayers.add(serviceTime);
                    break;

                case "evening":
                    eveningServicesAsString = eveningServicesAsString + serviceTitle + " - " + serviceTime + "\n";
//                    eveningHash.put(serviceTitle,serviceTime);
                    eveningPrayers.add(serviceTime);
                    break;

                default:
                    break;
            }
        }

        String morningText = "";
        String minchaText = "";
        String eveningText = "";

        for (int i=0; i<morningPrayers.size(); i++){
            if (i==0)
                morningText = morningPrayers.get(i);
            else
                morningText = morningText + "," + morningPrayers.get(i);
        }

        for (int i=0; i<minchaPrayers.size(); i++){
            if (i==0)
                minchaText = minchaPrayers.get(i);
            else
                minchaText = minchaText + "," + minchaPrayers.get(i);
        }

        for (int i=0; i<eveningPrayers.size(); i++){
            if (i==0)
                eveningText = eveningPrayers.get(i);
            else
                eveningText = eveningText + "," + eveningPrayers.get(i);
        }

        morning_service.setText(morningText);
        mincha_service.setText(minchaText);
        evening_service.setText(eveningText);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView){
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null){
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = 30;
            listView.setLayoutParams(params);
            listView.requestLayout();
            return;
        }


        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void openServiceDialog(String title){
        String content = "";
        Iterator myVeryOwnIterator = null;

        switch (title){
            case "Shacharit Services":
                content = morningServicesAsString;
/*                myVeryOwnIterator = morningHash.keySet().iterator();
                while(myVeryOwnIterator.hasNext()) {
                    String key=(String)myVeryOwnIterator.next();
                    String value=(String)morningHash.get(key);
                    content = content + key + " - " + value + "\n";
                }*/
                break;

            case "Mincha Services":
                content = minchaServicesAsString;
/*                myVeryOwnIterator = minchaHash.keySet().iterator();
                while(myVeryOwnIterator.hasNext()) {
                    String key=(String)myVeryOwnIterator.next();
                    String value=(String)minchaHash.get(key);
                    content = content + key + " - " + value + "\n";
                }*/
                break;

            case "Maariv Services":
                content = eveningServicesAsString;
/*                myVeryOwnIterator = eveningHash.keySet().iterator();
                while(myVeryOwnIterator.hasNext()) {
                    String key=(String)myVeryOwnIterator.next();
                    String value=(String)eveningHash.get(key);
                    content = content + key + " - " + value + "\n";
                }*/
                break;

            default:
                break;
        }

        new MaterialDialog.Builder(PrayersTime.this)
                .positiveText("Ok")
                .positiveColor(ContextCompat.getColor(PrayersTime.this, R.color.AceentColor))
                .title(title)
                .content(content)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
