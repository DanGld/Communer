package com.communer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
 * Created by ���� on 27/08/2015.
 */
public class NextEventAdapter extends BaseAdapter {

    private ArrayList<CalendarEventItem> mData;
    private Context mContext;

    public NextEventAdapter(ArrayList<CalendarEventItem> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
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

        TextView time = (TextView)rowView.findViewById(R.id.calendar_event_time);
        TextView title = (TextView)rowView.findViewById(R.id.calendar_event_title);
        TextView location = (TextView)rowView.findViewById(R.id.calendar_event_location);
        RelativeLayout container = (RelativeLayout)rowView.findViewById(R.id.day_event_item_container);
        container.getLayoutParams().height = dpToPx(80);

        final CalendarEventItem item = mData.get(position);
        title.setText(item.getEventTitle());
        location.setText(item.getEventLocation());

        final String type = item.getType();
        String eventTime = "";

        if (type.equals("prayer")){
            eventTime = item.getEventTimeString();
        }else{
            long eventStartTime = item.getEventStartTime();
            long eventEndTime = item.getEventEndTime();

            SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm", Locale.US);
            String sTimeString = getFormattedDateFromTimestamp(eventStartTime, dateFormatter);
            String eTimeString = getFormattedDateFromTimestamp(eventEndTime, dateFormatter);
            eventTime = sTimeString + " - " + eTimeString;
        }

        time.setText(eventTime);

        switch (type){
            case "event":
                container.setBackgroundResource(R.drawable.calendar_events_bg);
                break;

            case "activity":
                container.setBackgroundResource(R.drawable.calendar_activities_bg);
                break;

            case "prayer":
                container.setBackgroundResource(R.drawable.calendar_prayers_bg);
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

                    LocationObj locationObj = item.getLocObj();
                    if (locationObj != null){
                        String [] coords = item.getLocObj().getCords().split(",");
                        String latitude = coords[0];
                        String longtitudde = coords[1].trim();
                        mIntent.putExtra("event_lat", latitude);
                        mIntent.putExtra("event_long", longtitudde);
                        mIntent.putExtra("event_loc_title", longtitudde);
                    }
                    mContext.startActivity(mIntent);
                }else if (type.equals("prayer")){
                    Intent mIntent = new Intent(mContext, PrayersTime.class);
                    mContext.startActivity(mIntent);
                }
            }
        });
        return rowView;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
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
