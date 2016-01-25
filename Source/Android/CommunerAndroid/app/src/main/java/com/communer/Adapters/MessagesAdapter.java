package com.communer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.communer.Activities.EventActivity;
import com.communer.Fragments.MessagesFragment;
import com.communer.Models.EventOrMessage;
import com.communer.Models.LocationObj;
import com.communer.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by user on 11/23/2015.
 */
public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int EVENT = 0;
    private static final int ANNOUNCEMENT = 1;
    private static final int DIVIDER = 2;
    Context context;
    ArrayList<EventOrMessage> items;
    MessagesFragment fragment;
    public MessagesAdapter(Context context, ArrayList<EventOrMessage> items,MessagesFragment fragment) {
        this.items = items;
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public int getItemViewType(int position) {
        EventOrMessage item = items.get(position);
        int type = -1;
        if (item.getType() != null) {
            if (item.getType().equals("event") || item.getType().contains("divider")) {
                if (item.getType().equals("event")) {
                    type = EVENT;
                }
                if (item.getType().contains("divider")){
                    type = DIVIDER;
                }
            }
            else {
                type = ANNOUNCEMENT;
            }
        }
        return type;
    }


    @Override
    public RecyclerView.ViewHolder  onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;
        switch (viewType){
            case EVENT:
                View v1 = inflater.inflate(R.layout.event_or_message,null);
                viewHolder = new EventViewHolder(v1);
                break;
            case ANNOUNCEMENT:
                View v2 = inflater.inflate(R.layout.announcement_item,null);
                viewHolder = new AnnounceViewHolder(v2);
                break;
            case DIVIDER:
                View v3 = inflater.inflate(R.layout.messages_divider,null);
                viewHolder = new DividerViewHolder(v3);
                break;
            default:return null;
        }
        return viewHolder;
    }


    @Override
    public int getItemCount() {
        return items.size();
    }




    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder  holder, int position) {
        switch (holder.getItemViewType()){
            case EVENT:
                EventViewHolder viewHolder = (EventViewHolder) holder;
                configureEventViewHolder(viewHolder,position);
                break;
            case ANNOUNCEMENT:
                AnnounceViewHolder viewHolder1 = (AnnounceViewHolder) holder;
                configureAnnouncementViewHolder(viewHolder1, position);
                break;
            case DIVIDER:
                DividerViewHolder viewHolder2 = (DividerViewHolder) holder;
                configureDividerViewHolder(viewHolder2,position);
                break;
        }
    }

    private void configureDividerViewHolder(DividerViewHolder viewHolder2, int position) {
        EventOrMessage item = items.get(position);
        if (item.getType().equalsIgnoreCase("dividerGeneral")){
            viewHolder2.text.setText("General");
        }
        if (item.getType().equalsIgnoreCase("dividerPersonal")){
            viewHolder2.text.setText("Personal");
        }
        if (item.getType().equalsIgnoreCase("dividerEvent")){
            viewHolder2.text.setText("Events");
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        viewHolder2.container.setLayoutParams(params);
        viewHolder2.container.setBackgroundColor(context.getResources().getColor(R.color.DividerColor));
    }

    private void configureEventViewHolder(final EventViewHolder viewHolder, int position) {

        final EventOrMessage item = items.get(position);

        viewHolder.firstTitle.setText(item.getName());

        LocationObj eventLocationObj = item.getLocationObj();

        if (eventLocationObj != null)
            viewHolder.firstLocation.setText(eventLocationObj.getTitle());
        else
            viewHolder.firstLocation.setText("Unavailable");

        long startDate = item.getsTime();
        long endDate = item.geteTime();
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String formmatedStartTime = getFormattedDateFromTimestamp(startDate, timeFormatter);
        String formmatedEndTime = getFormattedDateFromTimestamp(endDate, timeFormatter);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
        viewHolder.firstDate.setText(getFormattedDateFromTimestamp(startDate, dateFormatter));
        viewHolder.firstHours.setText(formmatedStartTime + " - " + formmatedEndTime);

        String latText = "";
        String longText = "";


        LocationObj locationObj = item.getLocationObj();
        if (locationObj != null){
            String coords = locationObj.getCords();
            coords = coords.replace(" ","");
            String [] tempCoord = coords.split(",");
            latText = tempCoord[0];
            longText = tempCoord[1];

            String mapURL = "http://maps.google.com/maps/api/staticmap?center=" + latText + "," + longText + "&zoom=17&markers=color:blue%7C" + latText + "," + longText + "&size=400x300&sensor=false";
            Picasso.with(context)
                    .load(mapURL)
                    .error(R.drawable.communer_placeholder)
                    .into(viewHolder.firstImage);
        }else{
            Picasso.with(context)
                    .load(R.drawable.communer_placeholder)
                    .into(viewHolder.firstImage);
        }

        final String finalLatText = latText;
        final String finallongText = longText;


        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(context, EventActivity.class);
                mIntent.putExtra("ParentClassName", "TabsActivity");
                mIntent.putExtra("came_from", "messages_fragment");
                mIntent.putExtra("event_id", item.getEventID());
                mIntent.putExtra("event_title", item.getName());
                mIntent.putExtra("event_date", viewHolder.firstDate.getText().toString());
                mIntent.putExtra("event_hours", viewHolder.firstHours.getText().toString());
                mIntent.putExtra("event_lat", finalLatText);
                mIntent.putExtra("event_long", finallongText);
                mIntent.putExtra("event_start_time", item.getsTime());
                mIntent.putExtra("event_end_time", item.geteTime());
                mIntent.putExtra("long_date", item.getsTime());
                mIntent.putExtra("participants", item.getParticipants());
                context.startActivity(mIntent);
            }
        });

    }

    private void configureAnnouncementViewHolder(AnnounceViewHolder viewHolder1, int position) {

        final EventOrMessage item = items.get(position);

        long announStartTime = item.getsTime();
        SimpleDateFormat timeFormatter = new SimpleDateFormat("MMM dd", Locale.getDefault());
        String formmatedDate = getFormattedDateFromTimestamp(announStartTime, timeFormatter);

        timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String formmatedStartTime = getFormattedDateFromTimestamp(announStartTime, timeFormatter);

        viewHolder1.firstTitle.setText(item.getName());
        viewHolder1.firstTime.setText("Posted At: " + formmatedDate + " at " + formmatedStartTime);
        viewHolder1.firstDesc.setText(item.getContent());

        String imageUrl = item.getImageUrl();
        if (imageUrl != null && !imageUrl.equals("")){
            Picasso.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.communer_placeholder)
                    .error(R.drawable.communer_placeholder)
                    .into(viewHolder1.firstImage);
        }else{
            Picasso.with(context)
                    .load(R.drawable.communer_placeholder)
                    .into(viewHolder1.firstImage);
        }

        viewHolder1.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.showLongMessage(item);
            }
        });


    }

    public class DividerViewHolder extends  RecyclerView.ViewHolder {
        protected TextView text;
        protected LinearLayout container;
        public DividerViewHolder(View itemView) {
            super(itemView);
            this.container = (LinearLayout) itemView;
            this.text = (TextView) itemView.findViewById(R.id.messages_divider_text);
        }
    }
    public class EventViewHolder extends RecyclerView.ViewHolder {
        protected ImageView firstImage;
        protected TextView firstTitle, firstLocation, firstDate, firstHours;
        protected LinearLayout container;
        public EventViewHolder (View view) {
            super(view);
            container = (LinearLayout) view;
            this.firstTitle = (TextView) view.findViewById(R.id.event_title);
            this.firstLocation = (TextView) view.findViewById(R.id.event_location);
            this.firstImage = (ImageView) view.findViewById(R.id.eventLocationMap);
            this.firstDate = (TextView) view.findViewById(R.id.event_date);
            this.firstHours = (TextView) view.findViewById(R.id.event_hours);
        }
    }

    public class AnnounceViewHolder extends RecyclerView.ViewHolder {
        protected ImageView firstImage;
        protected TextView firstTitle, firstTime, firstDesc;
        protected LinearLayout container;
        public AnnounceViewHolder (View view) {
            super(view);
            container = (LinearLayout) view;
            this.firstTitle = (TextView) view.findViewById(R.id.annoucement_first_title);
            this.firstTime = (TextView) view.findViewById(R.id.announcement_first_time);
            this.firstDesc = (TextView) view.findViewById(R.id.annoucement_first_desc);
            this.firstImage = (ImageView) view.findViewById(R.id.annoucement_first_image);
        }
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
