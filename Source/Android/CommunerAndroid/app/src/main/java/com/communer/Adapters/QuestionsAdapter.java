package com.communer.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.communer.Activities.QuestionsNAnswers;
import com.communer.Models.Post;
import com.communer.R;
import com.communer.Utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by יובל on 31/08/2015.
 */
public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

    private ArrayList<Post> mData;
    private Context mContext;
    private int lastPosition = -1;

    public QuestionsAdapter(ArrayList<Post> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public QuestionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final QuestionsAdapter.ViewHolder holder, int position) {
        final Post item = mData.get(position);
        holder.questionTitle.setText(item.getTitle());
        holder.questionAsker.setText(item.getMember().getName());
        holder.questionContent.setText(item.getContent());

        if (item.getComments() != null)
            holder.commentCount.setText(String.valueOf(item.getComments().size()) + " Comments");
        else
            holder.commentCount.setText(String.valueOf("0 Comments"));

        SimpleDateFormat timeFormatter = new SimpleDateFormat("MMM dd", Locale.US);
        String formmatedDate = getFormattedDateFromTimestamp(item.getCDate(), timeFormatter);

        timeFormatter = new SimpleDateFormat("HH:mm", Locale.US);
        formmatedDate = formmatedDate + " at " + getFormattedDateFromTimestamp(item.getCDate(), timeFormatter);
        holder.questionTime.setText("Posted At: " + formmatedDate);

        String mmbrUrl = item.getMember().getImageUrl();
        if (!mmbrUrl.equals("") && mmbrUrl!=null){
            Picasso.with(mContext)
                    .load(mmbrUrl)
                    .error(R.drawable.placeholeder_lowres)
                    .transform(new CircleTransform())
                    .into(holder.profilePic);
        }else{
            Picasso.with(mContext)
                    .load(R.drawable.placeholeder_lowres)
                    .transform(new CircleTransform())
                    .into(holder.profilePic);
        }

        holder.invisibleHeader.setVisibility(View.GONE);

        if (position == mData.size() - 1){
            holder.divider.setVisibility(View.GONE);
        }

        holder.commentCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mContext instanceof QuestionsNAnswers){
                    ((QuestionsNAnswers)mContext).showCommentsDialog(item);
                }
            }
        });


//        setAnimation(holder.question_container, position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView questionTitle, questionAsker, questionContent, commentCount, questionTime, invisibleHeader;
        public ImageView profilePic;
        public View divider;
        public LinearLayout question_container;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            questionTitle = (TextView)itemLayoutView.findViewById(R.id.question_title);
            questionAsker = (TextView)itemLayoutView.findViewById(R.id.question_asker_name);
            questionContent = (TextView)itemLayoutView.findViewById(R.id.question_description);
            commentCount = (TextView)itemLayoutView.findViewById(R.id.question_comment_count);
            questionTime = (TextView)itemLayoutView.findViewById(R.id.question_time);
            invisibleHeader = (TextView)itemLayoutView.findViewById(R.id.qa_header);

            profilePic = (ImageView)itemLayoutView.findViewById(R.id.question_asker_profile_image);
            divider = itemLayoutView.findViewById(R.id.divider_question);

            question_container = (LinearLayout)itemLayoutView.findViewById(R.id.question_layout_container);
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

    private void setAnimation(View viewToAnimate, int position)
    {
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

/*    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        holder.question_container.clearAnimation();
        super.onViewDetachedFromWindow(holder);
    }*/
}
