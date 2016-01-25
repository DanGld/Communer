package com.communer.Fragments;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.communer.Activities.EventActivity;
import com.communer.Adapters.MessagesAdapter;
import com.communer.Models.EventOrMessage;
import com.communer.Models.LocationObj;
import com.communer.R;
import com.communer.Utils.AppUser;
import com.communer.Utils.RefreshUtil;
import com.communer.Utils.UpcomingEventsUtil;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MessagesFragment extends Fragment {

    private LinearLayout personalAnnouncementsContainer, generalAnnouncementsContainer, occasionAnnouncementsContainer, eventsContainer;
    private TextView personalHeader, generalHeader, occasionHeader, eventsHeader;

    private UpcomingEventsUtil eventsUtil;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BroadcastReceiver refreshReceiver;
    private RecyclerView recyclerView;
    private MessagesAdapter messagesAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.messagestab, container, false);
        Log.i("Tag", "onCreateViewMessages");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        initRecyclerView(view);
        initWithoutRecycler(view);
    }

    private void initWithoutRecycler(View view) {
        personalAnnouncementsContainer = (LinearLayout)view.findViewById(R.id.personal_announ_container);
        generalAnnouncementsContainer = (LinearLayout)view.findViewById(R.id.general_announ_container);
        occasionAnnouncementsContainer = (LinearLayout)view.findViewById(R.id.occasions_announ_container);
        eventsContainer = (LinearLayout)view.findViewById(R.id.events_announ_container);

        personalHeader = (TextView)view.findViewById(R.id.messages_personal_header);
        generalHeader = (TextView)view.findViewById(R.id.messages_general_header);
        occasionHeader = (TextView)view.findViewById(R.id.messages_occasions_header);
        eventsHeader = (TextView)view.findViewById(R.id.messages_events_header);

        initRefreshLayout(view);
        initReceiver();

        eventsUtil = new UpcomingEventsUtil();
        addAnnouncements();
        addEvents();
    }

/*    private void initRecyclerView(View view) {
        try {
            recyclerView = (RecyclerView) view.findViewById(R.id.message_recycler);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            AppUser appUserInstance = AppUser.getInstance();
            eventsUtil = new UpcomingEventsUtil();
            try {
                ArrayList<EventOrMessage> finalList = resolveAdapterData(appUserInstance);
                messagesAdapter = new MessagesAdapter(getActivity(), finalList, this);
                recyclerView.setAdapter(messagesAdapter);
            }catch (Exception e){
                Log.d("error", e.getMessage());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        initRefreshLayout(view);
        initReceiver();
    }*/

    private ArrayList<EventOrMessage> resolveAdapterData(AppUser appUserInstance) throws ParseException {
        ArrayList<EventOrMessage> finalList = new ArrayList<>();
        ArrayList<EventOrMessage> currentAnnouncements = orderAnnouncements(appUserInstance.getCurrentCommunity().getAnnouncements());
        ArrayList<EventOrMessage> genenralAnnouncements = new ArrayList<>();
        ArrayList<EventOrMessage> privateAnnouncements = new ArrayList<>();

        for (int i = 0; i < currentAnnouncements.size(); i++) {
            EventOrMessage item = currentAnnouncements.get(i);
            if (item.getType().equalsIgnoreCase("general")) {
                genenralAnnouncements.add(item);
            }else{
                privateAnnouncements.add(item);
            }
        }

        ArrayList<EventOrMessage> relevantEvents = eventsUtil.getUpcomingEvents(appUserInstance.getCurrentCommunity().getDailyEvents(), 15);
        if (privateAnnouncements.size()>0) {
            finalList.add(new EventOrMessage("dividerPersonal"));
            finalList.addAll(privateAnnouncements);
        }

        if (genenralAnnouncements.size()>0) {
            finalList.add(new EventOrMessage("dividerGeneral"));
            finalList.addAll(genenralAnnouncements);
        }

        finalList.add(new EventOrMessage("dividerEvent"));
        finalList.addAll(relevantEvents);
        return finalList;
    }

    private void addEvents() {
        try {
            AppUser appUserInstance = AppUser.getInstance();
            ArrayList<EventOrMessage> relevantEvents = eventsUtil.getUpcomingEvents(appUserInstance.getCurrentCommunity().getDailyEvents(), 15);
            for (int i = 0; i < relevantEvents.size(); i++) {
                if (eventsHeader.getVisibility() == View.GONE)
                    eventsHeader.setVisibility(View.VISIBLE);

                addEventView(relevantEvents.get(i));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void addEventView(final EventOrMessage item){
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.event_or_message, null);

        TextView eventTitle = (TextView) view.findViewById(R.id.event_title);
        TextView eventLocation = (TextView) view.findViewById(R.id.event_location);
        ImageView eventImage = (ImageView) view.findViewById(R.id.eventLocationMap);

        final TextView eventDate = (TextView) view.findViewById(R.id.event_date);
        final TextView eventHours = (TextView) view.findViewById(R.id.event_hours);

        LinearLayout eventItem = (LinearLayout) view.findViewById(R.id.messages_event_container);

        eventTitle.setText(item.getName());

        LocationObj eventLocationObj = item.getLocationObj();
        if (eventLocationObj != null)
            eventLocation.setText(eventLocationObj.getTitle());
        else
            eventLocation.setText("Unavailable");

        long startDate = item.getsTime();
        long endDate = item.geteTime();
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.US);
        String formmatedStartTime = getFormattedDateFromTimestamp(startDate, timeFormatter);
        String formmatedEndTime = getFormattedDateFromTimestamp(endDate, timeFormatter);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd yyyy", Locale.US);
        eventDate.setText(getFormattedDateFromTimestamp(startDate, dateFormatter));
        eventHours.setText(formmatedStartTime + " - " + formmatedEndTime);

        String latText = "";
        String longText = "";

        LocationObj locationObj = item.getLocationObj();
        if (locationObj != null) {
            String coords = locationObj.getCords();
            coords = coords.replace(" ", "");
            String[] tempCoord = coords.split(",");
            latText = tempCoord[0];
            longText = tempCoord[1];

            String mapURL = "http://maps.google.com/maps/api/staticmap?center=" + latText + "," + longText + "&zoom=17&markers=color:blue%7C" + latText + "," + longText + "&size=400x300&sensor=false";
            Picasso.with(getActivity())
                    .load(mapURL)
                    .error(R.drawable.communer_placeholder)
                    .into(eventImage);
        } else {
            Picasso.with(getActivity())
                    .load(R.drawable.communer_placeholder)
                    .into(eventImage);
        }

        String readStatus = item.getReadStatus();

        final String finalLatText = latText;
        final String finallongText = longText;

        eventItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getActivity(), EventActivity.class);
                mIntent.putExtra("ParentClassName", "TabsActivity");
                mIntent.putExtra("came_from", "messages_fragment");
                mIntent.putExtra("event_id", item.getEventID());
                mIntent.putExtra("event_title", item.getName());
                mIntent.putExtra("event_date", eventDate.getText().toString());
                mIntent.putExtra("event_hours", eventHours.getText().toString());
                mIntent.putExtra("event_lat", finalLatText);
                mIntent.putExtra("event_long", finallongText);
                mIntent.putExtra("event_start_time", item.getsTime());
                mIntent.putExtra("event_end_time", item.geteTime());
                mIntent.putExtra("long_date", item.getsTime());
                mIntent.putExtra("participants", item.getParticipants());
                mIntent.putExtra("dailyEventDate", item.getDailyEventDate());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getActivity().startActivity(mIntent,
                            ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                }else{
                    startActivity(mIntent);
                    getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.stay_anim);
                }
            }
        });

        eventsContainer.addView(view);
    }

    private void addAnnouncements(){
        AppUser appUserInstance = AppUser.getInstance();
        ArrayList<EventOrMessage> currentEvents = orderAnnouncements(appUserInstance.getCurrentCommunity().getAnnouncements());

        for (int i = 0; i < currentEvents.size(); i++){
            EventOrMessage announcement = currentEvents.get(i);
            if (announcement.getType().equals("personal")){
                if (personalHeader.getVisibility() == View.GONE)
                    personalHeader.setVisibility(View.VISIBLE);
                addAnnouncementView(announcement);
            }
        }

        for (int i = 0; i < currentEvents.size(); i++){
            EventOrMessage announcement = currentEvents.get(i);
            if (announcement.getType().equals("general")){
                if (generalHeader.getVisibility() == View.GONE)
                    generalHeader.setVisibility(View.VISIBLE);
                addAnnouncementView(announcement);
            }
        }
    }

    public void addAnnouncementView(final EventOrMessage announcement) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.announcement_item, null);

        LinearLayout announCntnr = (LinearLayout) view.findViewById(R.id.announcement_first_item_cntnr);

        TextView announTitle = (TextView) view.findViewById(R.id.annoucement_first_title);
        TextView announTime = (TextView) view.findViewById(R.id.announcement_first_time);
        final TextView announDesc = (TextView) view.findViewById(R.id.annoucement_first_desc);
        ImageView announImage = (ImageView) view.findViewById(R.id.annoucement_first_image);

        long announStartTime = announcement.getsTime();
        SimpleDateFormat timeFormatter = new SimpleDateFormat("MMM dd", Locale.US);
        String formmatedDate = getFormattedDateFromTimestamp(announStartTime, timeFormatter);

        timeFormatter = new SimpleDateFormat("HH:mm", Locale.US);
        String formmatedStartTime = getFormattedDateFromTimestamp(announStartTime, timeFormatter);

        announTitle.setText(announcement.getName());
        announTime.setText("Posted At: " + formmatedDate + " at " + formmatedStartTime);
        announDesc.setText(announcement.getContent());
//        announDesc.setText("Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla ");

        String imageUrl = announcement.getImageUrl();
        if (imageUrl != null && !imageUrl.equals("")) {
            Picasso.with(getActivity())
                    .load(imageUrl)
                    .placeholder(R.drawable.communer_placeholder)
                    .error(R.drawable.communer_placeholder)
                    .into(announImage);
        } else {
            Picasso.with(getActivity())
                    .load(R.drawable.communer_placeholder)
                    .into(announImage);
        }

        String readStatus = announcement.getReadStatus();

        String type = announcement.getType();
        switch (type) {
            case "personal":
                personalAnnouncementsContainer.addView(view);
                break;
            case "general":
                generalAnnouncementsContainer.addView(view);
                break;

            default:
                break;
        }

        announCntnr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLongMessage(announcement);
/*                Layout l = announDesc.getLayout();
                if (l != null) {
                    int lines = l.getLineCount();
                    if (lines > 0)
                        if (l.getEllipsisCount(lines-1) > 0)
                            showLongMessage(announcement);
                }*/
            }
        });
    }

    public static String getFormattedDateFromTimestamp(long timestampInMilliSeconds, SimpleDateFormat formatter) {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        formatter.setTimeZone(timeZone);
        Calendar c = Calendar.getInstance(timeZone);
        c.setTimeInMillis(timestampInMilliSeconds);
        String formattedDate = formatter.format(c.getTime());
        return formattedDate;
    }

    public void showLongMessage(EventOrMessage announcement) {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.long_message_dialog, false)
                .dividerColor(ContextCompat.getColor(getActivity(), R.color.DividerColor))
                .show();

        View view = dialog.getCustomView();
        TextView announTitle = (TextView) view.findViewById(R.id.annoucement_first_title);
        TextView announTime = (TextView) view.findViewById(R.id.announcement_first_time);
        TextView announDesc = (TextView) view.findViewById(R.id.annoucement_first_desc);
        ImageView announImage = (ImageView) view.findViewById(R.id.annoucement_first_image);

        long announStartTime = announcement.getsTime();
        SimpleDateFormat timeFormatter = new SimpleDateFormat("MMM dd", Locale.US);
        String formmatedDate = getFormattedDateFromTimestamp(announStartTime, timeFormatter);

        timeFormatter = new SimpleDateFormat("HH:mm", Locale.US);
        String formmatedStartTime = getFormattedDateFromTimestamp(announStartTime, timeFormatter);

        announTitle.setText(announcement.getName());
        announTime.setText(formmatedDate + " at " + formmatedStartTime);

/*        Display display = getActivity().getWindowManager().getDefaultDisplay();
        FlowTextHelper.tryFlowText(announcement.getContent(), announImage, announDesc, display);*/

        announDesc.setText(announcement.getContent());
//        announDesc.setText("Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla ");

        String imageUrl = announcement.getImageUrl();
        if (imageUrl != null && !imageUrl.equals("")) {
            Picasso.with(getActivity())
                    .load(imageUrl)
                    .placeholder(R.drawable.communer_placeholder)
                    .error(R.drawable.communer_placeholder)
                    .into(announImage);
        } else {
            Picasso.with(getActivity())
                    .load(R.drawable.communer_placeholder)
                    .into(announImage);
        }
    }

    private void initRefreshLayout(View v) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.messages_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.progress_blue,
                R.color.progress_red,
                R.color.progress_yellow,
                R.color.progress_green);
//        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(getActivity(), R.color.PrimaryColor));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("refresh_done");
        refreshReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra("Status");
                if (status.equals("Success")) {
/*                    clearAnnouncements();
                    clearEvents();
                    addAnnouncements();
                    addEvents();*/
                }

                mSwipeRefreshLayout.setRefreshing(false);
            }
        };
        getActivity().registerReceiver(refreshReceiver, filter);
    }

    private void refreshContent() {
        RefreshUtil refreshUtil = new RefreshUtil(getActivity());
        refreshUtil.getData("24.323288", "26.135633");
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(refreshReceiver);
        super.onDestroy();
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    private void clearEvents() {
        eventsContainer.removeAllViews();
    }

    private void clearAnnouncements() {
        personalAnnouncementsContainer.removeAllViews();
        generalAnnouncementsContainer.removeAllViews();
        occasionAnnouncementsContainer.removeAllViews();
    }

    private ArrayList<EventOrMessage> orderAnnouncements(ArrayList<EventOrMessage> unorderedAnnouncements) {
        ArrayList<EventOrMessage> orderedAnnoun = new ArrayList<EventOrMessage>();
        for (int i = 0; i < unorderedAnnouncements.size(); i++) {
            EventOrMessage tempAnnoun = unorderedAnnouncements.get(i);
            boolean isActive = tempAnnoun.isActive();

            if (isActive) {
                long sTime = tempAnnoun.getsTime();

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(sTime);
                Date eventSDate = cal.getTime();

                if (orderedAnnoun.size() == 0) {
                    orderedAnnoun.add(tempAnnoun);
                } else {
                    int tempAnnounSize = orderedAnnoun.size();
                    boolean addedItem = false;

                    for (int j = 0; j < tempAnnounSize; j++) {
                        EventOrMessage prevAnnoun = orderedAnnoun.get(j);

                        long announDateLong = prevAnnoun.getsTime();
                        Calendar announCal = Calendar.getInstance();
                        announCal.setTimeInMillis(announDateLong);
                        Date announDate = announCal.getTime();

                        if (eventSDate.after(announDate)) {
                            orderedAnnoun.add(j, tempAnnoun);
                            addedItem = true;
                        }

                        if (addedItem)
                            break;
                    }

                    if (!addedItem)
                        orderedAnnoun.add(tempAnnoun);
                }
            }
        }

        return orderedAnnoun;
    }
}