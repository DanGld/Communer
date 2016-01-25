package com.communer.Adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.communer.Activities.EventActivity;
import com.communer.Activities.PrayersTime;
import com.communer.Models.CalendarEventItem;
import com.communer.Models.LocationObj;
import com.communer.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by ���� on 05/08/2015.
 */
public class CalendarEventsAdapter extends BaseAdapter {

    private ArrayList<CalendarEventItem> mData;
    private Context mContext;
    private String dayName;
    private Activity activity;

    private boolean showPrayers, showActivities, showEvents;

    public CalendarEventsAdapter(ArrayList<CalendarEventItem> mData, Context mContext, String dayName, Activity activity, boolean showPrayers, boolean showActivities, boolean showEvents) {
        this.mData = mData;
        this.mContext = mContext;
        this.dayName = dayName;
        this.activity = activity;
        this.showPrayers = showPrayers;
        this.showActivities = showActivities;
        this.showEvents = showEvents;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.calendar_event_item, parent, false);

        TextView parasha = (TextView) rowView.findViewById(R.id.parasha);
        TextView time = (TextView)rowView.findViewById(R.id.calendar_event_time);
        TextView title = (TextView)rowView.findViewById(R.id.calendar_event_title);
        TextView location = (TextView)rowView.findViewById(R.id.calendar_event_location);
        ImageView magenDavid = (ImageView)rowView.findViewById(R.id.magen_david_img);
        RelativeLayout container = (RelativeLayout)rowView.findViewById(R.id.day_event_item_container);

        final CalendarEventItem item = mData.get(position);

        String eventTitle = item.getEventTitle();
        title.setText(eventTitle);
        location.setText(item.getEventLocation());

        final String type = item.getType();
        String eventTime = "";

        if (type.equals("prayer")){
            if (!item.getParasha().equals("") && dayName.equalsIgnoreCase("fri")){
                parasha.setVisibility(View.VISIBLE);
                parasha.setText(item.getParasha());
            }
            else {
                parasha.setVisibility(View.GONE);
            }
            eventTime = item.getEventTimeString();
        }else{
            long startTimeLong = item.getEventStartTime();
            long endTimeLong = item.getEventEndTime();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm", Locale.US);
            String startTime = getFormattedDateFromTimestamp(startTimeLong, dateFormatter);
            String endTime = getFormattedDateFromTimestamp(endTimeLong, dateFormatter);
            eventTime = startTime + " - " + endTime;
        }

        time.setText(eventTime);

        switch (type){
            case "event":
                if (showEvents)
                    container.setBackgroundResource(R.drawable.calendar_events_bg);
                else
                    container.setVisibility(View.GONE);
                break;

            case "activity":
                if (showActivities)
                    container.setBackgroundResource(R.drawable.calendar_activities_bg);
                else
                    container.setVisibility(View.GONE);
                break;

            case "prayer":
                if (showPrayers){
                    container.setBackgroundResource(R.drawable.calendar_prayers_bg);
                    if (eventTitle.contains("Shacharit") || eventTitle.contains("shacharit")){
                        magenDavid.setImageResource(R.drawable.calendar_morning_service);
                    }else if (eventTitle.contains("Mincha") || eventTitle.contains("mincha")){
                        magenDavid.setImageResource(R.drawable.calendar_mincha);
                    }else if (eventTitle.contains("Maariv") || eventTitle.contains("maariv")){
                        magenDavid.setImageResource(R.drawable.calendar_evening);
                    }

                    magenDavid.setVisibility(View.VISIBLE);
                }else
                    container.setVisibility(View.GONE);

                break;

            default:
                break;
        }

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("event") || type.equals("activity")){
                    Intent mIntent = new Intent(mContext, EventActivity.class);
                    mIntent.putExtra("ParentClassName","TabsActivity");
                    mIntent.putExtra("came_from","calendar_fragment");
                    mIntent.putExtra("event_id",item.getId());
                    mIntent.putExtra("event_buy_link",item.getBuyLink());
                    mIntent.putExtra("event_title",item.getEventTitle());
                    mIntent.putExtra("event_start_time", item.getEventStartTime());
                    mIntent.putExtra("event_end_time", item.getEventEndTime());
                    mIntent.putExtra("event_date", item.getEventStartTime());
                    mIntent.putExtra("participants", item.getParticipants());
                    mIntent.putExtra("isFree", item.isFree());
                    mIntent.putExtra("dailyEventDate", item.getDailyEventDate());

                    LocationObj locationObj = item.getLocObj();
                    if (locationObj != null){
                        String [] coords = item.getLocObj().getCords().split(",");
                        String latitude = coords[0];
                        String longtitudde = coords[1].trim();
                        mIntent.putExtra("event_lat", latitude);
                        mIntent.putExtra("event_long", longtitudde);
                        mIntent.putExtra("event_loc_title", longtitudde);
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mContext.startActivity(mIntent,
                                ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
                    }else{
                        mContext.startActivity(mIntent);
                        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.stay_anim);
                    }
                }else if (type.equals("prayer")){
                    Intent mIntent = new Intent(mContext, PrayersTime.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mContext.startActivity(mIntent,
                                ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
                    }else{
                        mContext.startActivity(mIntent);
                        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.stay_anim);
                    }
                }
            }
        });
        return rowView;
    }

    public static String getFormattedDateFromTimestamp(long timestampInMilliSeconds, SimpleDateFormat formatter)
    {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        formatter.setTimeZone(timeZone);
        Calendar c = Calendar.getInstance(timeZone);
        c.setTimeInMillis(timestampInMilliSeconds);
        String formattedDate = formatter.format(c.getTime());
        return formattedDate;
    }
}
