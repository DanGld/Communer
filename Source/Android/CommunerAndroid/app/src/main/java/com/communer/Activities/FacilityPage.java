package com.communer.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.communer.Adapters.NextEventAdapter;
import com.communer.Models.CalendarEventItem;
import com.communer.Models.CommunityContact;
import com.communer.Models.EventOrMessage;
import com.communer.Models.LocationObj;
import com.communer.R;
import com.communer.Utils.CircleTransform;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by יובל on 22/09/2015.
 */
public class FacilityPage extends AppCompatActivity implements OnMapReadyCallback {

    private TextView title, desc, calendar_month, incharge_name, incharge_location, incharge_phone, inharge_email,no_images;
    private LinearLayout images_container,leftArrow, rightArrow, dates_container;
    private ScrollView scrollView;
    private HorizontalScrollView dates_scroll, images_scroll;
    private ListView eventsList;
    private ImageButton phoneBtn;
    private ImageView contactPic;

    private Toolbar toolbar;

    private String [] monthesNames = {"January","February","March","April",
            "May","June","July","August","September",
            "October","November","December"};

    private String [] hebrewMonthesNames = {"ינואר","פברואר","מרץ","אפריל",
            "מאי","יוני","יולי","אוגוסט","ספטמבר",
            "אוקטובר","נובמבר","דצמבר"};

    private int currentMonth,currentYear,originalMonth,originalYear,todayPosition,latestCalendarposition,MONTH_DAY_COUNT, screenWidth;
    private Map<CalendarDay,ArrayList<CalendarEventItem>> eventsMap;
    private NextEventAdapter eventsAdapter;

    private ArrayList<EventOrMessage> facilityEvents;
    private CommunityContact facilityContact;
    private String facilityName,facilityContent;
    private ArrayList<String> facilityImages;

    private String currentLanguage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facility_page);

        getIntentExtras();
        initToolbar();
        setViewsReference();

        getScreenWidth();
        setScrollableImages();
        initEvents();
        createHorizontalCalendar();

        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        scrollView.setFocusable(true);
        scrollView.setFocusableInTouchMode(true);

        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle(facilityName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void getIntentExtras() {
        Intent extras = getIntent();
        facilityEvents = extras.getParcelableArrayListExtra("facility_events");
        facilityContact = extras.getParcelableExtra("facility_contact");
        facilityName = extras.getStringExtra("facility_title");
        facilityContent = extras.getStringExtra("facility_desc");
        facilityImages = extras.getStringArrayListExtra("facility_images");

        SharedPreferences prefs = getSharedPreferences("SmartCommunity", Activity.MODE_PRIVATE);
        String language = prefs.getString("last_language_picked", "noData");
        if (language.equals("iw")){
            currentLanguage = "hebrew";
        }else{
            currentLanguage = "english";
        }
    }

    private void initEvents() {
        eventsMap = new HashMap<CalendarDay,ArrayList<CalendarEventItem>>();
        for (int i=0; i<facilityEvents.size(); i++){
            EventOrMessage singleDay = facilityEvents.get(i);

            ArrayList<CalendarEventItem> calendarTodayEvents = new ArrayList<CalendarEventItem>();
            long sTime = singleDay.getsTime();
            long eTime = singleDay.geteTime();
            String title = singleDay.getName();
            String id = singleDay.getEventID();
            String buyLink = singleDay.getBuyLink();
            boolean isFree = singleDay.isFree();
            String attendingStatus = singleDay.getAttendingStatus();
            LocationObj location = singleDay.getLocationObj();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(sTime);

            int cDay = calendar.get(Calendar.DAY_OF_MONTH);
            int cMonth = calendar.get(Calendar.MONTH);
            int cYear = calendar.get(Calendar.YEAR);

            CalendarDay day = new CalendarDay(cYear,cMonth,cDay);
            if (eventsMap.get(day) != null){
                calendarTodayEvents = eventsMap.get(day);
            }

            ArrayList<CommunityContact> participants = singleDay.getParticipants();

            calendarTodayEvents.add(new CalendarEventItem(id,sTime,eTime,title,"event",location,participants,buyLink,isFree,attendingStatus,""));
            eventsMap.put(day, calendarTodayEvents);
        }
    }

    private void setViewsReference() {
        title = (TextView)findViewById(R.id.facility_title);
        desc = (TextView)findViewById(R.id.facility_content);
        calendar_month = (TextView)findViewById(R.id.facility_calendar_month);
        incharge_name = (TextView)findViewById(R.id.facility_incharge_name);
        incharge_location = (TextView)findViewById(R.id.facility_incharge_location);
        incharge_phone = (TextView)findViewById(R.id.facility_incharge_phone);
        inharge_email = (TextView)findViewById(R.id.facility_incharge_email);
        no_images = (TextView)findViewById(R.id.facility_page_no_images);
        contactPic = (ImageView)findViewById(R.id.facility_contact_image);

        images_container = (LinearLayout)findViewById(R.id.facility_images_container);
        eventsList = (ListView)findViewById(R.id.facility_events_list);

        leftArrow = (LinearLayout)findViewById(R.id.hsv_left_btn_container);
        rightArrow = (LinearLayout)findViewById(R.id.hsv_right_btn_container);
        dates_container = (LinearLayout)findViewById(R.id.facility_calendar_dates_container);
        dates_scroll = (HorizontalScrollView)findViewById(R.id.facility_horizontal_scroll);
        images_scroll = (HorizontalScrollView)findViewById(R.id.facility_page_images_horizontal_scroll);

        phoneBtn = (ImageButton)findViewById(R.id.facility_incharge_phone_btn);

        scrollView = (ScrollView)findViewById(R.id.facility_scrollview);

        title.setText(facilityName);
        desc.setText(facilityContent);

        if (facilityContact != null){
            incharge_name.setText(facilityContact.getName());
            incharge_location.setText(facilityContact.getPosition());
            incharge_phone.setText(facilityContact.getPhoneNumber());
            inharge_email.setText(facilityContact.getMail());

            String imgURL = facilityContact.getImageUrl();
            if (imgURL.equals("")){
                Picasso.with(FacilityPage.this)
                        .load(R.drawable.placeholeder_lowres)
                        .transform(new CircleTransform())
                        .into(contactPic);
            }else{
                Picasso.with(FacilityPage.this)
                        .load(imgURL)
                        .error(R.drawable.placeholeder_lowres)
                        .transform(new CircleTransform())
                        .into(contactPic);
            }
        }else{
            incharge_name.setText("");
            incharge_location.setText("");
            incharge_phone.setText("");
            inharge_email.setText("");

            Picasso.with(FacilityPage.this)
                    .load(R.drawable.placeholeder_lowres)
                    .transform(new CircleTransform())
                    .into(contactPic);
        }

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

        phoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + incharge_phone.getText().toString()));
                startActivity(intent);
            }
        });
    }

    private void setScrollableImages() {
        if (facilityImages.size() > 0){
            for (int j = 0; j < facilityImages.size(); j++) {
                images_container.addView(addImageToGallery(facilityImages.get(j),j));
            }
        }else{
            images_scroll.setVisibility(View.GONE);
            no_images.setVisibility(View.VISIBLE);
        }
    }

    private View addImageToGallery(final String url, final int position){
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.gallery_card_item, null);

        ImageButton imageBtn = (ImageButton)view.findViewById(R.id.gallery_image_item);
        Picasso.with(FacilityPage.this)
                .load(url)
                .placeholder(R.drawable.placeholeder_lowres)
                .error(R.drawable.placeholeder_lowres)
                .into(imageBtn);

/*        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(FacilityPage.this, PhotoViewActivity.class);
                mIntent.putParcelableArrayListExtra("Images", facilityImages);
                mIntent.putExtra("picked_img_pos", position);
                mIntent.putExtra("ParentClassName", "FacilityPage");
                startActivity(mIntent);
            }
        });*/

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Double mapLat = Double.parseDouble("32.069965");
        Double mapLong = Double.parseDouble("34.777464");

        LatLng mapLocation = new LatLng(mapLat,mapLong);
        googleMap.addMarker(new MarkerOptions().position(mapLocation));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mapLocation, 13), 2000, null);
//        googleMap.getUiSettings().setScrollGesturesEnabled(false);
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

        if (currentLanguage.equalsIgnoreCase("english"))
            calendar_month.setText(monthesNames[month]);
        else
            calendar_month.setText(hebrewMonthesNames[month]);

        Calendar mycal = new GregorianCalendar(year, month, day);
        MONTH_DAY_COUNT = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int j=0; j<MONTH_DAY_COUNT; j++){
            CalendarDay eachDay = new CalendarDay(year,month,j+1);
            dates_container.addView(getNextEventDateItem(j,dates_scroll,eachDay,day));
        }

        dates_scroll.postDelayed(new Runnable() {
            @Override
            public void run() {
                int scrollCenter = dates_scroll.getScrollX() + dates_scroll.getWidth() / 2;
                View v = dates_container.getChildAt(todayPosition - 1);
                int scrollToX = (v.getLeft() + v.getWidth() / 2) - scrollCenter;
                dates_scroll.smoothScrollTo(scrollToX, 0);
            }
        }, 500);
    }

    private View getNextEventDateItem(final int pos, final HorizontalScrollView scrollHorizontal, final CalendarDay day, final int today){
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.next_event_date_item, null);

        final LinearLayout container = (LinearLayout)view.findViewById(R.id.date_item_container);
        final LinearLayout sub_container = (LinearLayout)view.findViewById(R.id.date_item_sub_container);
        TextView dayText = (TextView)view.findViewById(R.id.date_item_daytext);
        TextView dayNumber = (TextView)view.findViewById(R.id.date_item_daynum);

        ImageView orangeDot = (ImageView)view.findViewById(R.id.event_orange_dot);
        ImageView greenDot = (ImageView)view.findViewById(R.id.event_green_dot);
        ImageView blueDot = (ImageView)view.findViewById(R.id.event_blue_dot);

        ArrayList<CalendarEventItem> items = eventsMap.get(day);
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
        String dayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());

        dayText.setText(dayName);
        dayNumber.setText(String.valueOf(pos + 1));

        if (today == day.getDay() && day.getMonth() == originalMonth && day.getYear() == originalYear){
            dayText.setTypeface(dayText.getTypeface(), Typeface.BOLD);
            dayNumber.setTypeface(dayNumber.getTypeface(), Typeface.BOLD);

            dayText.setTextColor(ContextCompat.getColor(FacilityPage.this, R.color.AceentColor));
            dayNumber.setTextColor(ContextCompat.getColor(FacilityPage.this, R.color.AceentColor));

            sub_container.setBackgroundColor(ContextCompat.getColor(FacilityPage.this, R.color.Gray));
            latestCalendarposition = today;

            if (items != null){
                eventsAdapter = new NextEventAdapter(items,FacilityPage.this);
                eventsAdapter.notifyDataSetChanged();
                eventsList.setAdapter(eventsAdapter);
            }else{
                eventsList.setAdapter(null);
            }
        }

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latestCalendarposition != day.getDay()) {
                    View childView = dates_container.getChildAt(latestCalendarposition - 1);
                    TextView prevDayText = (TextView) childView.findViewById(R.id.date_item_daytext);
                    TextView prevDayNumber = (TextView) childView.findViewById(R.id.date_item_daynum);
                    LinearLayout prevCont = (LinearLayout) childView.findViewById(R.id.date_item_sub_container);

                    if (latestCalendarposition != todayPosition) {
                        prevDayText.setTextColor(ContextCompat.getColor(FacilityPage.this, R.color.SecondaryText));
                        prevDayNumber.setTextColor(ContextCompat.getColor(FacilityPage.this, R.color.SecondaryText));
                    }

                    prevCont.setBackgroundColor(ContextCompat.getColor(FacilityPage.this, R.color.white));

                    prevDayText.setTypeface(prevDayText.getTypeface(), Typeface.NORMAL);
                    prevDayNumber.setTypeface(prevDayText.getTypeface(), Typeface.NORMAL);

                    View currentView = dates_container.getChildAt(day.getDay() - 1);
                    TextView DayText = (TextView) currentView.findViewById(R.id.date_item_daytext);
                    TextView DayNumber = (TextView) currentView.findViewById(R.id.date_item_daynum);
                    LinearLayout Cont = (LinearLayout) currentView.findViewById(R.id.date_item_sub_container);

                    if (day.getDay() == todayPosition && day.getMonth() == originalMonth && day.getYear() == originalYear) {
                        DayText.setTextColor(ContextCompat.getColor(FacilityPage.this, R.color.AceentColor));
                        DayNumber.setTextColor(ContextCompat.getColor(FacilityPage.this, R.color.AceentColor));
                    } else {
                        DayText.setTextColor(ContextCompat.getColor(FacilityPage.this, R.color.PrimaryText));
                        DayNumber.setTextColor(ContextCompat.getColor(FacilityPage.this, R.color.PrimaryText));
                    }
                    Cont.setBackgroundColor(ContextCompat.getColor(FacilityPage.this, R.color.Gray));

                    prevDayText.setTypeface(prevDayText.getTypeface(), Typeface.BOLD);
                    prevDayNumber.setTypeface(prevDayText.getTypeface(), Typeface.BOLD);

                    ArrayList<CalendarEventItem> dateEvents = eventsMap.get(day);
                    if (dateEvents != null) {
                        eventsAdapter = new NextEventAdapter(dateEvents, FacilityPage.this);
                        eventsAdapter.notifyDataSetChanged();
                        eventsList.setAdapter(eventsAdapter);
                    } else {
                        eventsList.setAdapter(null);
                    }

                    int scrollX = (container.getLeft() - (screenWidth / 2)) + (container.getWidth() / 2);
                    scrollHorizontal.smoothScrollTo(scrollX, 0);

                    latestCalendarposition = day.getDay();
                }
            }
        });
        return view;
    }

    private void getScreenWidth(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
    }

    private void nextMonth() {
        if (currentMonth < 11){
            currentMonth = currentMonth + 1;
        }else{
            currentMonth = 0;
            currentYear = currentYear + 1;
        }

        if (currentLanguage.equalsIgnoreCase("english"))
            calendar_month.setText(monthesNames[currentMonth]);
        else
            calendar_month.setText(hebrewMonthesNames[currentMonth]);

        Calendar mycal = new GregorianCalendar(currentYear, currentMonth, 1);
        MONTH_DAY_COUNT = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        dates_container.removeAllViews();

        for (int j=0; j<MONTH_DAY_COUNT; j++){
            CalendarDay eachDay = new CalendarDay(currentYear,currentMonth,j+1);
            dates_container.addView(getNextEventDateItem(j,dates_scroll,eachDay,day));
        }

        dates_scroll.post(new Runnable() {
            @Override
            public void run() {
                if (currentMonth == originalMonth && currentYear == originalYear){
                    int scrollCenter = dates_scroll.getScrollX() + dates_scroll.getWidth() / 2;
                    View v = dates_container.getChildAt(todayPosition - 1);
                    int scrollToX = (v.getLeft() + v.getWidth() / 2) - scrollCenter;
                    dates_scroll.smoothScrollTo(scrollToX, 0);
                }else{
                    dates_scroll.smoothScrollTo(0,0);
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

        if (currentLanguage.equalsIgnoreCase("english"))
            calendar_month.setText(monthesNames[currentMonth]);
        else
            calendar_month.setText(hebrewMonthesNames[currentMonth]);

        Calendar mycal = new GregorianCalendar(currentYear, currentMonth, 1);
        MONTH_DAY_COUNT = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        dates_container.removeAllViews();

        for (int j=0; j<MONTH_DAY_COUNT; j++){
            CalendarDay eachDay = new CalendarDay(currentYear,currentMonth,j+1);
            dates_container.addView(getNextEventDateItem(j,dates_scroll,eachDay,day));
        }

        dates_scroll.post(new Runnable() {
            @Override
            public void run() {
                if (currentMonth == originalMonth && currentYear == originalYear) {
                    int scrollCenter = dates_scroll.getScrollX() + dates_scroll.getWidth() / 2;
                    View v = dates_container.getChildAt(todayPosition - 1);
                    int scrollToX = (v.getLeft() + v.getWidth() / 2) - scrollCenter;
                    dates_scroll.smoothScrollTo(scrollToX, 0);
                } else {
                    dates_scroll.smoothScrollTo(0, 0);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
