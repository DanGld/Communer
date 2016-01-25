package com.communer.Adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.communer.Activities.CommunityConversation;
import com.communer.Activities.PhotoViewActivity;
import com.communer.Activities.PrayersTime;
import com.communer.Activities.QuestionsNAnswers;
import com.communer.Activities.Security;
import com.communer.Models.CalendarEventItem;
import com.communer.Models.CommentItem;
import com.communer.Models.CommunityActivityOrFacility;
import com.communer.Models.CommunityContact;
import com.communer.Models.DailyEvents;
import com.communer.Models.EventOrMessage;
import com.communer.Models.GalleryImage;
import com.communer.Models.LocationObj;
import com.communer.Models.Post;
import com.communer.Models.SmallPrayer;
import com.communer.R;
import com.communer.Utils.AppUser;
import com.communer.Utils.CircleTransform;
import com.communer.Utils.HomeDataUtil;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by ���� on 30/07/2015.
 */
public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private int screenWidth;
    private int MONTH_DAY;
    private int todayPosition;
    private int latestPosition;
    private ViewPager tabsViewPager;

    private ArrayList<EventOrMessage> latestAnnouncements;

    private Map<CalendarDay,ArrayList<CalendarEventItem>> eventsMap;
    private HomeDataUtil HDU;

    private ArrayList<String> widgetsList;
    private ArrayList<GalleryImage> galleryImages;

    private Activity activity;

    private String mixPanelProjectToken = "537b3567fe348d6c5dfa0d6e2ae688b3";
    private MixpanelAPI mixpanel;

    public HomeAdapter(Context mContext, int screenWidth, ArrayList<EventOrMessage> latestAnnouncements,ArrayList<String> widgets, Activity activity, ViewPager viewPager) {
        this.mContext = mContext;
        this.screenWidth = screenWidth;
        this.latestAnnouncements = latestAnnouncements;
        this.HDU = new HomeDataUtil();
        this.widgetsList = widgets;
        this.activity = activity;
        this.tabsViewPager = viewPager;
        mixpanel = MixpanelAPI.getInstance(mContext, mixPanelProjectToken);
    }

    public static class ServicesViewHolder extends RecyclerView.ViewHolder {
        public TextView morning;
        public TextView mincha;
        public TextView evening;
        public CardView theCard;

        public ServicesViewHolder(View v) {
            super(v);
            morning = (TextView)v.findViewById(R.id.services_morning_time);
            mincha = (TextView)v.findViewById(R.id.services_mincha_time);
            evening = (TextView)v.findViewById(R.id.services_evening_time);
            theCard = (CardView)v.findViewById(R.id.card_view);
        }
    }

    public static class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        public TextView firstItemTitle,secondItemTitle;
        public TextView firstItemDesc,secondItemDesc;
        public ImageView firstItemPic,secondItemPic;

        public LinearLayout firstItemCntnr,SecondItemCntnr;

        public CardView theCard;

        public AnnouncementViewHolder(View v) {
            super(v);
            firstItemTitle = (TextView)v.findViewById(R.id.annoucement_first_title);
            secondItemTitle = (TextView)v.findViewById(R.id.annoucement_second_title);

            firstItemDesc = (TextView)v.findViewById(R.id.annoucement_first_desc);
            secondItemDesc = (TextView)v.findViewById(R.id.annoucement_second_desc);

            firstItemPic = (ImageView)v.findViewById(R.id.annoucement_first_image);
            secondItemPic = (ImageView)v.findViewById(R.id.annoucement_second_image);

            firstItemCntnr = (LinearLayout)v.findViewById(R.id.announcement_first_item_cntnr);
            SecondItemCntnr = (LinearLayout)v.findViewById(R.id.announcement_second_item_cntnr);

            theCard = (CardView)v.findViewById(R.id.card_view);
        }
    }

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout imgsContainer;

        public GalleryViewHolder(View v) {
            super(v);
            imgsContainer = (LinearLayout)v.findViewById(R.id.gallery_card_images_container);
        }
    }

    public static class NextEventViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout HorizontallScrollView;
        public TextView eventTime,eventTitle,eventPlace;
        public TextView noEventsText;
        private HorizontalScrollView horizontalScrol;
        private ListView eventsList;

        public NextEventViewHolder(View v) {
            super(v);
            HorizontallScrollView = (LinearLayout)v.findViewById(R.id.nextevents_card_dates_container);
            horizontalScrol = (HorizontalScrollView)v.findViewById(R.id.next_event_horizontal_scroll);
            eventTime = (TextView)v.findViewById(R.id.next_event_time);
            eventTitle = (TextView)v.findViewById(R.id.next_event_title);
            eventPlace = (TextView)v.findViewById(R.id.next_event_place);
            noEventsText = (TextView)v.findViewById(R.id.no_events_text);
            eventsList = (ListView)v.findViewById(R.id.next_events_list);
        }
    }

    public static class SecurityViewHolder extends RecyclerView.ViewHolder {
        public ImageView reportBtn, viewReportsBtn;

        public SecurityViewHolder(View v) {
            super(v);
            reportBtn = (ImageView)v.findViewById(R.id.security_card_report);
            viewReportsBtn = (ImageView)v.findViewById(R.id.security_card_viewreports);
        }
    }

    public static class QuestionsViewHolder extends RecyclerView.ViewHolder {
        public TextView questionTitle,questionAskerName,questionDescription,questionTime,questionCommentCount;
        public ImageView question_asker_pic;
        public CardView theCard;

        public QuestionsViewHolder(View v) {
            super(v);
            question_asker_pic = (ImageView)v.findViewById(R.id.question_asker_profile_image);
            questionTitle = (TextView)v.findViewById(R.id.question_title);
            questionAskerName = (TextView)v.findViewById(R.id.question_asker_name);
            questionDescription = (TextView)v.findViewById(R.id.question_description);
            questionTime = (TextView)v.findViewById(R.id.question_time);
            questionCommentCount = (TextView)v.findViewById(R.id.question_comment_count);
            theCard = (CardView)v.findViewById(R.id.card_view);
        }
    }
    public static class CommunityViewHolder extends RecyclerView.ViewHolder {
        public TextView postTitle,posterName,postDescription,postTime,postCommentCount;
        public ImageView poster_pic;
        public CardView theCard;

        public CommunityViewHolder(View v) {
            super(v);
            poster_pic = (ImageView)v.findViewById(R.id.conversation_poster__profile_image);
            postTitle = (TextView)v.findViewById(R.id.conversation_post__title);
            posterName = (TextView)v.findViewById(R.id.conversation_poster_name);
            postDescription = (TextView)v.findViewById(R.id.conversation_post_description);
            postTime = (TextView)v.findViewById(R.id.conversation_post_time);
            postCommentCount = (TextView)v.findViewById(R.id.conversation_post_comment_count);
            theCard = (CardView)v.findViewById(R.id.card_view);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        String theWidget = widgetsList.get(i);

        switch (theWidget){
            case "Prayer":
                View servicesView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.next_services_card, viewGroup, false);
                ServicesViewHolder svh = new ServicesViewHolder(servicesView);
                return svh;
            case "Announcement":
                View announcementView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.announcement_card, viewGroup, false);
                AnnouncementViewHolder avh = new AnnouncementViewHolder(announcementView);
                return avh;
            case "Gallery":
                View galleryView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_card, viewGroup, false);
                GalleryViewHolder gvh = new GalleryViewHolder(galleryView);
                return gvh;
            case "Calendar":
                View nextEventView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.next_event_card, viewGroup, false);
                NextEventViewHolder nevh = new NextEventViewHolder(nextEventView);
                return nevh;
            case "Security":
                View securityView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.security_card, viewGroup, false);
                SecurityViewHolder secvh = new SecurityViewHolder(securityView);
                return secvh;
            case "QA":
                View questionView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.questions_card, viewGroup, false);
                QuestionsViewHolder qvh = new QuestionsViewHolder(questionView);
                return qvh;
            case "Conversation":
                View communityView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.community_card, viewGroup, false);
                CommunityViewHolder communityvh = new CommunityViewHolder(communityView);
                return communityvh;

            default:
                View defaultView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.community_card, viewGroup, false);
                CommunityViewHolder default_vh = new CommunityViewHolder(defaultView);
                return default_vh;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder != null){
            String theWidget = widgetsList.get(i);

            switch (theWidget){
                case "Prayer":
                    ServicesViewHolder holder = (ServicesViewHolder)viewHolder;
                    initTodayPrayers(holder);
                    break;
                case "Announcement":
                    AnnouncementViewHolder annHolder = (AnnouncementViewHolder)viewHolder;

                    if (latestAnnouncements.size() > 0){
                        EventOrMessage firstAnnoun = latestAnnouncements.get(0);
                        annHolder.firstItemTitle.setText(firstAnnoun.getName());
                        annHolder.firstItemDesc.setText(firstAnnoun.getContent());

                        String firstImgUrl = firstAnnoun.getImageUrl();
                        if (!firstImgUrl.equals("")){
                            Picasso.with(mContext)
                                    .load(firstImgUrl)
                                    .placeholder(R.drawable.placeholeder_lowres)
                                    .error(R.drawable.placeholeder_lowres)
                                    .into(annHolder.firstItemPic);
                        }else{
                            Picasso.with(mContext)
                                    .load(R.drawable.placeholeder_lowres)
                                    .into(annHolder.firstItemPic);
                        }

                        if (latestAnnouncements.size() > 1){
                            EventOrMessage secondAnnoun = latestAnnouncements.get(1);
                            annHolder.secondItemTitle.setText(secondAnnoun.getName());
                            annHolder.secondItemDesc.setText(secondAnnoun.getContent());

                            String secondImgUrl = secondAnnoun.getImageUrl();
                            if (!secondImgUrl.equals("")){
                                Picasso.with(mContext)
                                        .load(secondImgUrl)
                                        .placeholder(R.drawable.placeholeder_lowres)
                                        .error(R.drawable.placeholeder_lowres)
                                        .into(annHolder.secondItemPic);
                            }else {
                                Picasso.with(mContext)
                                        .load(R.drawable.placeholeder_lowres)
                                        .into(annHolder.secondItemPic);
                            }
                        }else{
                            annHolder.SecondItemCntnr.setVisibility(View.GONE);
                        }
                    }else{
                        annHolder.firstItemCntnr.setVisibility(View.GONE);
                        annHolder.SecondItemCntnr.setVisibility(View.GONE);
                    }


                    annHolder.firstItemCntnr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mixpanel.track("Home Widget - Announcement click");
                            tabsViewPager.setCurrentItem(2);
//                            showLongMessage(latestAnnouncements.get(0));
                        }
                    });

                    annHolder.SecondItemCntnr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mixpanel.track("Home Widget - Announcement click");
                            tabsViewPager.setCurrentItem(2);
//                            showLongMessage(latestAnnouncements.get(1));
                        }
                    });

                    break;
                case "Gallery":
                    AppUser appUserInstance = AppUser.getInstance();
                    GalleryViewHolder galleryHolder = (GalleryViewHolder)viewHolder;

                    LinearLayout myGallery  = galleryHolder.imgsContainer;
                    if (myGallery.getChildAt(0) == null) {
                        galleryImages = HDU.getRandomPictures(appUserInstance.getCurrentCommunity().getGalleries(), 10);

                        for (int j = 0; j < galleryImages.size(); j++) {
                            myGallery.addView(addImageToGallery(galleryImages.get(j).getUrl(),j));
                        }
                    }

                    break;
                case "Calendar":
                    NextEventViewHolder nextViewHolder = (NextEventViewHolder)viewHolder;
                    final LinearLayout imagesContainer  = nextViewHolder.HorizontallScrollView;
                    final HorizontalScrollView hs = nextViewHolder.horizontalScrol;

                    eventsMap = new HashMap<CalendarDay, ArrayList<CalendarEventItem>>();

                    if (imagesContainer.getChildAt(0) == null){
                        handleCalendarEvents();
                        initHorizontalCalendar(imagesContainer, hs, nextViewHolder);
                        hs.post(new Runnable() {
                            @Override
                            public void run() {
                                int scrollCenter = hs.getScrollX() + hs.getWidth() / 2 - dpToPx(32);
                                View v = imagesContainer.getChildAt(todayPosition - 1);
                                int scrollToX = (v.getLeft() + v.getWidth() / 2) - scrollCenter;
                                hs.smoothScrollTo(scrollToX, 0);
                            }
                        });
                    }

                    break;
                case "Security":
                    SecurityViewHolder securityViewHolder = (SecurityViewHolder)viewHolder;
                    securityViewHolder.reportBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mixpanel.track("Home Widget - Security new report click");
                            Intent mIntent = new Intent(mContext, Security.class);
                            mIntent.putExtra("Security_Type", "report");

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                                mContext.startActivity(mIntent,
                                        ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
                            }else{
                                mContext.startActivity(mIntent);
                                activity.overridePendingTransition(R.anim.slide_in_up, R.anim.stay_anim);
                            }
                        }
                    });

                    securityViewHolder.viewReportsBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mixpanel.track("Home Widget - Security view reports click");
                            Intent mIntent = new Intent(mContext, Security.class);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                mContext.startActivity(mIntent,
                                        ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
                            }else{
                                mContext.startActivity(mIntent);
                                activity.overridePendingTransition(R.anim.slide_in_up, R.anim.stay_anim);
                            }
                        }
                    });
                    break;
                case "QA":
                    QuestionsViewHolder questionViewHolder = (QuestionsViewHolder)viewHolder;
                    final Post item = HDU.getRecentQA();

                    if (item != null){
                        questionViewHolder.questionTitle.setText(item.getTitle());
                        questionViewHolder.questionAskerName.setText(item.getMember().getName());

                        SimpleDateFormat timeFormatter = new SimpleDateFormat("MMM dd", Locale.US);
                        String formmatedDate = getFormattedDateFromTimestamp(item.getCDate(), timeFormatter);

                        timeFormatter = new SimpleDateFormat("HH:mm", Locale.US);
                        formmatedDate = formmatedDate + " at " + getFormattedDateFromTimestamp(item.getCDate(), timeFormatter);
                        questionViewHolder.questionTime.setText("Posted At: " + formmatedDate);

                        questionViewHolder.questionDescription.setText(item.getContent());
                        ArrayList<CommentItem> comments = item.getComments();

                        if (comments!=null)
                            questionViewHolder.questionCommentCount.setText(comments.size() + " Comments");
                        else
                            questionViewHolder.questionCommentCount.setVisibility(View.INVISIBLE);

                        String imageUrl = item.getMember().getImageUrl();
                        if (!imageUrl.equals("")){
                            Picasso.with(mContext)
                                    .load(imageUrl)
                                    .error(R.drawable.placeholeder_lowres)
                                    .transform(new CircleTransform())
                                    .into(questionViewHolder.question_asker_pic);
                        }else{
                            Picasso.with(mContext)
                                    .load(R.drawable.placeholeder_lowres)
                                    .transform(new CircleTransform())
                                    .into(questionViewHolder.question_asker_pic);
                        }

                        questionViewHolder.questionCommentCount.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent qIntent = new Intent(mContext, QuestionsNAnswers.class);
                                qIntent.putExtra("came_from", "home");

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    mContext.startActivity(qIntent,
                                            ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
                                }else{
                                    mContext.startActivity(qIntent);
                                    activity.overridePendingTransition(R.anim.slide_in_up, R.anim.stay_anim);
                                }
                            }
                        });
                    }else{
                        questionViewHolder.questionTitle.setText("No Data");
                        questionViewHolder.questionAskerName.setText("");
                        questionViewHolder.questionTime.setText("");
                        questionViewHolder.questionDescription.setText("");
                        questionViewHolder.questionCommentCount.setText("");
                        Picasso.with(mContext)
                                .load(R.drawable.placeholeder_lowres)
                                .transform(new CircleTransform())
                                .into(questionViewHolder.question_asker_pic);
                    }

                    break;
                case "Conversation":
                    CommunityViewHolder communityViewHolder = (CommunityViewHolder)viewHolder;
                    Post conversation = HDU.getRecentConversation();

                    if (conversation != null){
                        communityViewHolder.postTitle.setText(conversation.getTitle());
                        communityViewHolder.posterName.setText(conversation.getMember().getName());

                        SimpleDateFormat time_formatter = new SimpleDateFormat("MMM dd", Locale.US);
                        String formmated_date = getFormattedDateFromTimestamp(conversation.getCDate(), time_formatter);

                        time_formatter = new SimpleDateFormat("HH:mm", Locale.US);
                        formmated_date = formmated_date + " at " + getFormattedDateFromTimestamp(conversation.getCDate(), time_formatter);
                        communityViewHolder.postTime.setText("At " + formmated_date);

                        communityViewHolder.postDescription.setText(conversation.getContent());

                        ArrayList<CommentItem> conver_comments = conversation.getComments();
                        if (conver_comments!=null)
                            communityViewHolder.postCommentCount.setText(conver_comments.size() + " Comments");
                        else
                            communityViewHolder.postCommentCount.setVisibility(View.INVISIBLE);

                        String posterImage = conversation.getMember().getImageUrl();
                        if (!posterImage.equals("")){
                            Picasso.with(mContext)
                                    .load(posterImage)
                                    .error(R.drawable.placeholeder_lowres)
                                    .transform(new CircleTransform())
                                    .into(communityViewHolder.poster_pic);
                        }else{
                            Picasso.with(mContext)
                                    .load(R.drawable.placeholeder_lowres)
                                    .transform(new CircleTransform())
                                    .into(communityViewHolder.poster_pic);
                        }
                    }else{
                        communityViewHolder.postTitle.setText("");
                        communityViewHolder.posterName.setText("");
                        communityViewHolder.postTime.setText("");
                        communityViewHolder.postDescription.setText("");
                        communityViewHolder.postCommentCount.setVisibility(View.INVISIBLE);
                        Picasso.with(mContext)
                                .load(R.drawable.placeholeder_lowres)
                                .transform(new CircleTransform())
                                .into(communityViewHolder.poster_pic);
                    }

                    communityViewHolder.theCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v){
                            mixpanel.track("Home Widget - Conversations click");
                            Intent mIntent = new Intent(mContext, CommunityConversation.class);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                mContext.startActivity(mIntent,
                                        ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
                            }else{
                                mContext.startActivity(mIntent);
                                activity.overridePendingTransition(R.anim.slide_in_up, R.anim.stay_anim);
                            }
                        }
                    });

                    break;
            }
        }
    }

    private void initTodayPrayers(ServicesViewHolder holder){
        HomeDataUtil homeData = new HomeDataUtil();
        ArrayList<SmallPrayer> todayPrayers = homeData.getTodayServices();

        ArrayList<SmallPrayer> shaharitPrayers = new ArrayList<SmallPrayer>();
        ArrayList<SmallPrayer> minchaPrayers = new ArrayList<SmallPrayer>();
        ArrayList<SmallPrayer> maarivPrayers = new ArrayList<SmallPrayer>();

        if (todayPrayers!=null) {
            if (todayPrayers.size() > 0) {
                for (int i = 0; i < todayPrayers.size(); i++){
                    SmallPrayer item = todayPrayers.get(i);
                    String type = item.getType();
                    if (type.equalsIgnoreCase("morning")) {
                        shaharitPrayers.add(item);
                    } else if (type.equalsIgnoreCase("mincha")) {
                        minchaPrayers.add(item);
                    } else if (type.equalsIgnoreCase("evening")) {
                        maarivPrayers.add(item);
                    }
                }
            }
        }

        String shaharitTimes = "--";
        long closeShaharit = 0;
        int relevantShaharitIndex = 0;

        for (int i=0; i<shaharitPrayers.size(); i++) {
            long prayerMillis = shaharitPrayers.get(i).getPrayerInMillis();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(closeShaharit);

            Calendar tempCal = Calendar.getInstance();
            tempCal.setTimeInMillis(prayerMillis);

            Calendar nowCal = Calendar.getInstance();

            if (tempCal.after(cal) && tempCal.after(nowCal)){
                closeShaharit = prayerMillis;
                relevantShaharitIndex = i;
            }
        }

        if (closeShaharit != 0)
            shaharitTimes = shaharitPrayers.get(relevantShaharitIndex).getTime();

        String minchaTimes = "--";
        long closeMincha = 0;
        int relevantMinchaIndex = 0;

        for (int i=0; i<minchaPrayers.size(); i++) {
            long prayerMillis = minchaPrayers.get(i).getPrayerInMillis();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(closeMincha);

            Calendar tempCal = Calendar.getInstance();
            tempCal.setTimeInMillis(prayerMillis);

            Calendar nowCal = Calendar.getInstance();

            if (tempCal.after(cal) && tempCal.after(nowCal)){
                closeMincha = prayerMillis;
                relevantMinchaIndex = i;
            }
        }

        if (closeMincha != 0)
            minchaTimes = minchaPrayers.get(relevantMinchaIndex).getTime();

        String maarivTimes = "--";
        long closeMaariv = 0;
        int relevantMaarivIndex = 0;

        for (int i=0; i<maarivPrayers.size(); i++) {
            long prayerMillis = maarivPrayers.get(i).getPrayerInMillis();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(closeMaariv);

            Calendar tempCal = Calendar.getInstance();
            tempCal.setTimeInMillis(prayerMillis);

            Calendar nowCal = Calendar.getInstance();

            if (tempCal.after(cal) && tempCal.after(nowCal)){
                closeMaariv = prayerMillis;
                relevantMaarivIndex = i;
            }
        }

        if (closeMaariv != 0)
            maarivTimes = maarivPrayers.get(relevantMaarivIndex).getTime();

        holder.morning.setText(shaharitTimes);
        holder.mincha.setText(minchaTimes);
        holder.evening.setText(maarivTimes);

        holder.theCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mixpanel.track("Home Widget - Prayers click");
                Intent mIntent = new Intent(mContext, PrayersTime.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mContext.startActivity(mIntent,
                            ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
                }else{
                    mContext.startActivity(mIntent);
                    activity.overridePendingTransition(R.anim.slide_in_up, R.anim.stay_anim);
                }
            }
        });
    }

    private void initHorizontalCalendar(LinearLayout imgsCntnr, HorizontalScrollView hsv, NextEventViewHolder viewHolder) {
        Calendar calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        todayPosition = day;

        Calendar mycal = new GregorianCalendar(year, month, day);
        MONTH_DAY = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int j=0; j<MONTH_DAY; j++){
            CalendarDay eachDay = new CalendarDay(year,month,j+1);
            imgsCntnr.addView(getNextEventDateItem(j,hsv,eachDay,todayPosition,viewHolder,imgsCntnr));
        }
    }

    @Override
    public int getItemCount() {
        return widgetsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private View addImageToGallery(final String url, final int position){
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.gallery_card_item, null);

        ImageButton imageBtn = (ImageButton)view.findViewById(R.id.gallery_image_item);
        Picasso.with(mContext)
                .load(url)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.placeholeder_lowres)
                .error(R.drawable.placeholeder_lowres)
                .into(imageBtn);

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mixpanel.track("Home Widget - Gallery image click");
                Intent mIntent = new Intent(mContext, PhotoViewActivity.class);
                mIntent.putParcelableArrayListExtra("Images", galleryImages);
                mIntent.putExtra("picked_img_pos", position);
                mIntent.putExtra("ParentClassName","TabsActivity");
                mContext.startActivity(mIntent);
            }
        });

        return view;
    }

    private View getNextEventDateItem(final int pos, final HorizontalScrollView scrollHorizontal, final CalendarDay day, int today, final NextEventViewHolder viewHolder, final LinearLayout imgsContainer){
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.next_event_date_item, null);

        final LinearLayout container = (LinearLayout)view.findViewById(R.id.date_item_container);
        final LinearLayout sub_container = (LinearLayout)view.findViewById(R.id.date_item_sub_container);
        final TextView dayText = (TextView)view.findViewById(R.id.date_item_daytext);
        final TextView dayNumber = (TextView)view.findViewById(R.id.date_item_daynum);

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
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day.getDay());
        calendar.set(Calendar.MONTH, day.getMonth());
        calendar.set(Calendar.YEAR, day.getYear());
        String dayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());

        dayText.setText(dayName);
        dayNumber.setText(String.valueOf(pos+1));

        if (today == day.getDay()){
            dayText.setTypeface(dayText.getTypeface(), Typeface.BOLD);
            dayNumber.setTypeface(dayNumber.getTypeface(), Typeface.BOLD);

            dayText.setTextColor(ContextCompat.getColor(mContext, R.color.AceentColor));
            dayNumber.setTextColor(ContextCompat.getColor(mContext, R.color.AceentColor));

            sub_container.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Gray));
            latestPosition = today;

            if (items != null){
                NextEventAdapter adapter = new NextEventAdapter(items,mContext);
                viewHolder.eventsList.setAdapter(adapter);
                setListViewHeightBasedOnChildren(viewHolder.eventsList);
                viewHolder.noEventsText.setVisibility(View.GONE);
                viewHolder.eventsList.setVisibility(View.VISIBLE);
            }else{
                viewHolder.eventsList.setAdapter(null);
                setListViewHeightBasedOnChildren(viewHolder.eventsList);
                viewHolder.eventsList.setVisibility(View.INVISIBLE);
                viewHolder.noEventsText.setVisibility(View.VISIBLE);
            }
        }

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latestPosition != day.getDay()){
                    View childView = imgsContainer.getChildAt(latestPosition-1);
                    TextView prevDayText = (TextView)childView.findViewById(R.id.date_item_daytext);
                    TextView prevDayNumber = (TextView)childView.findViewById(R.id.date_item_daynum);
                    LinearLayout prevCont = (LinearLayout)childView.findViewById(R.id.date_item_sub_container);

                    if (latestPosition != todayPosition){
                        prevDayText.setTextColor(mContext.getResources().getColor(R.color.SecondaryText));
                        prevDayNumber.setTextColor(mContext.getResources().getColor(R.color.SecondaryText));
                    }

                    prevCont.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));

                    prevDayText.setTypeface(prevDayText.getTypeface(), Typeface.NORMAL);
                    prevDayNumber.setTypeface(prevDayText.getTypeface(), Typeface.NORMAL);

                    View currentView = imgsContainer.getChildAt(day.getDay() - 1);
                    TextView DayText = (TextView)currentView.findViewById(R.id.date_item_daytext);
                    TextView DayNumber = (TextView)currentView.findViewById(R.id.date_item_daynum);
                    LinearLayout Cont = (LinearLayout)currentView.findViewById(R.id.date_item_sub_container);

                    if (day.getDay() == todayPosition){
                        DayText.setTextColor(mContext.getResources().getColor(R.color.AceentColor));
                        DayNumber.setTextColor(mContext.getResources().getColor(R.color.AceentColor));
                    }else{
                        DayText.setTextColor(mContext.getResources().getColor(R.color.PrimaryText));
                        DayNumber.setTextColor(mContext.getResources().getColor(R.color.PrimaryText));
                    }
                    Cont.setBackgroundColor(ContextCompat.getColor(mContext, R.color.Gray));

                    prevDayText.setTypeface(prevDayText.getTypeface(), Typeface.BOLD);
                    prevDayNumber.setTypeface(prevDayText.getTypeface(), Typeface.BOLD);

                    ArrayList<CalendarEventItem> dateEvents = eventsMap.get(day);
                    boolean hasEvents = false;

                    if (dateEvents != null){
                        if (dateEvents.size() > 0){
                            hasEvents = true;
                        }
                    }

                    if (hasEvents){
                        NextEventAdapter adapter = new NextEventAdapter(dateEvents,mContext);
                        viewHolder.eventsList.setAdapter(adapter);
                        setListViewHeightBasedOnChildren(viewHolder.eventsList);
                        viewHolder.noEventsText.setVisibility(View.GONE);
                        viewHolder.eventsList.setVisibility(View.VISIBLE);
                    }else{
                        viewHolder.eventsList.setAdapter(null);
                        setListViewHeightBasedOnChildren(viewHolder.eventsList);
                        viewHolder.eventsList.setVisibility(View.INVISIBLE);
                        viewHolder.noEventsText.setVisibility(View.VISIBLE);
                    }

                    int scrollX = (container.getLeft() - (screenWidth / 2)) + (container.getWidth() / 2) + dpToPx(32);
                    scrollHorizontal.smoothScrollTo(scrollX, 0);

                    latestPosition = day.getDay();
                }
            }
        });

        return view;
    }

    private void handleCalendarEvents(){
        AppUser appUserInstance = AppUser.getInstance();
        ArrayList<DailyEvents> dailyEvents = appUserInstance.getCurrentCommunity().getDailyEvents();

        if (eventsMap.size() > 0)
            eventsMap.clear();

        for (int i=0; i<dailyEvents.size(); i++){
            DailyEvents singleDay = dailyEvents.get(i);
            ArrayList<CalendarEventItem> calendarTodayEvents = new ArrayList<CalendarEventItem>();

            String dailyEventID = singleDay.getDate();
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

                calendarTodayEvents.add(new CalendarEventItem(id, startTime, endTime, title, "event", location,participants,buyLink,isFree, attendingStatus,dailyEventID));
            }

            ArrayList<CommunityActivityOrFacility> dayActivities = singleDay.getActivities();
            for (int j=0; j<dayActivities.size(); j++){
                CommunityActivityOrFacility singleActivity = dayActivities.get(j);
                String title = singleActivity.getName();
//                String location = singleActivity.getLocationObj().getTitle();

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

                    calendarTodayEvents.add(new CalendarEventItem(id,startTime,endTime,title,"activity",location,participants,buyLink,isFree,attendingStatus,dailyEventID));
                }
            }

/*            ArrayList<PrayerDetails> dayPrayers = singleDay.getPrayers();
            for (int j=0; j<dayPrayers.size(); j++){
                PrayerDetails singlePrayer = dayPrayers.get(j);
                ArrayList<SmallPrayer> smallPrayers = singlePrayer.getSmallPrayers();

                for (int h=0; h<smallPrayers.size(); h++){
                    SmallPrayer smallPrayer = smallPrayers.get(h);
                    String time = smallPrayer.getTime();
                    String title = smallPrayer.getTitle();
                    String location = "";
                    String parasha = singlePrayer.getParashaName();
                    calendarTodayEvents.add(new CalendarEventItem(time,title,location,"prayer",parasha));
                }
            }*/

            if (calendarTodayEvents.size() > 0){
                ArrayList<CalendarEventItem> arrangedEvents = arrangeCalendarEvents(calendarTodayEvents);

                String singleDayDate [] = singleDay.getDate().split("/");
                int cDay = Integer.valueOf(singleDayDate[0]);
                int cMonth = Integer.valueOf(singleDayDate[1]) - 1;
                int cYear = Integer.valueOf(singleDayDate[2]);

                CalendarDay day = new CalendarDay(cYear,cMonth,cDay);
                eventsMap.put(day, arrangedEvents);
            }
        }
    }

    private ArrayList<CalendarEventItem> arrangeCalendarEvents(ArrayList<CalendarEventItem> calendarTodayEvents) {
        ArrayList<CalendarEventItem> tempEvents = new ArrayList<CalendarEventItem>();
        ArrayList<CalendarEventItem> finalEvents = new ArrayList<CalendarEventItem>();

        for (int i=0; i<calendarTodayEvents.size(); i++){
            CalendarEventItem tempEvent = calendarTodayEvents.get(i);
            long newItemLongDate = tempEvent.getEventStartTime();

            if (tempEvents.size() == 0){
                tempEvents.add(tempEvent);
            }else{
                boolean eventAdded = false;
                int tmpCount = tempEvents.size();

                for (int j=0; j<tmpCount; j++){
                    long oldDailyEventStart = tempEvents.get(j).getEventStartTime();

                    Calendar c = Calendar.getInstance();
                    Calendar now = Calendar.getInstance();

                    c.setTimeInMillis(newItemLongDate);
                    Date newItemDate = c.getTime();

                    c.setTimeInMillis(oldDailyEventStart);
                    Date oldItemDate = c.getTime();


                    if (newItemDate.after(oldItemDate) && newItemDate.after(now.getTime())){
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

        if (tempEvents.size() > 2){
            for (int i=0; i<tempEvents.size(); i++){
                if (i<2){
                    CalendarEventItem item = tempEvents.get(i);
                    finalEvents.add(item);
                }else{
                    break;
                }
            }
        }else{
            return tempEvents;
        }

        return finalEvents;
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

    private void showLongMessage(EventOrMessage announcement){
        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .customView(R.layout.long_message_dialog, false)
                .dividerColor(ContextCompat.getColor(mContext, R.color.DividerColor))
                .show();

        View view = dialog.getCustomView();
        TextView announTitle = (TextView)view.findViewById(R.id.annoucement_first_title);
        TextView announTime = (TextView)view.findViewById(R.id.announcement_first_time);
        TextView announDesc = (TextView)view.findViewById(R.id.annoucement_first_desc);
        ImageView announImage = (ImageView)view.findViewById(R.id.annoucement_first_image);

        long announStartTime = announcement.getsTime();
        SimpleDateFormat timeFormatter = new SimpleDateFormat("MMM dd", Locale.US);
        String formmatedDate = getFormattedDateFromTimestamp(announStartTime, timeFormatter);

        timeFormatter = new SimpleDateFormat("HH:mm", Locale.US);
        String formmatedStartTime = getFormattedDateFromTimestamp(announStartTime, timeFormatter);

        announTitle.setText(announcement.getName());
        announTime.setText(formmatedDate + " at " + formmatedStartTime);
        announDesc.setText(announcement.getContent());
//        announDesc.setText("Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla Duh Duh Bla Bla ");

        String imageUrl = announcement.getImageUrl();
        if (imageUrl != null && !imageUrl.equals("")){
            Picasso.with(mContext)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholeder_lowres)
                    .error(R.drawable.placeholeder_lowres)
                    .into(announImage);
        }else{
            Picasso.with(mContext)
                    .load(R.drawable.placeholeder_lowres)
                    .into(announImage);
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
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
}
