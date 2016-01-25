package com.communer.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.communer.Adapters.CalendarEventsAdapter;
import com.communer.Models.CalendarEventItem;
import com.communer.R;
import com.communer.Utils.AppUser;
import com.communer.Widgets.EventDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by יובל on 11/11/2015.
 */
public class MonthlyCalendarActivity extends AppCompatActivity {

    private MaterialCalendarView materialCalendar;
    private Toolbar toolbar;

    private TextView noEventsText;
    private ListView eventsList;
    private CalendarEventsAdapter eventsAdapter;
    private Map<CalendarDay,ArrayList<CalendarEventItem>> eventsMap;

    private HashSet<CalendarDay> eventsDays;
    private LinearLayout calendarDownArrow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monthly_calendar);

        initToolbar();

        noEventsText = (TextView)findViewById(R.id.monthly_calendar_no_events);
        eventsList = (ListView)findViewById(R.id.monthly_calendar_events_list);
        calendarDownArrow = (LinearLayout)findViewById(R.id.monthly_down_arrow);

        AppUser appUserInstance = AppUser.getInstance();
        eventsMap = appUserInstance.getEventsMap();

        materialCalendar = (MaterialCalendarView)findViewById(R.id.calendarView);
        materialCalendar.setDateSelected(CalendarDay.today(),true);
        materialCalendar.setCurrentDate(CalendarDay.today());
        handleDatePick(CalendarDay.today());

        materialCalendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                handleDatePick(date);
            }
        });

        eventsDays = appUserInstance.getEventsDays();
        if (eventsDays != null){
            EventDecorator eventDecorator = new EventDecorator(MonthlyCalendarActivity.this,eventsDays);
            materialCalendar.addDecorator(eventDecorator);
        }

        calendarDownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void handleDatePick(CalendarDay date) {
        ArrayList<CalendarEventItem> dateEvents = eventsMap.get(date);
        if (dateEvents != null){
            eventsAdapter = new CalendarEventsAdapter(dateEvents,MonthlyCalendarActivity.this, "",MonthlyCalendarActivity.this,true,true,true);
            eventsAdapter.notifyDataSetChanged();
            eventsList.setAdapter(eventsAdapter);
            noEventsText.setVisibility(View.GONE);
        }else{
            eventsList.setAdapter(null);
            noEventsText.setVisibility(View.VISIBLE);
        }
    }

    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle(getResources().getString(R.string.monthly_events_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
