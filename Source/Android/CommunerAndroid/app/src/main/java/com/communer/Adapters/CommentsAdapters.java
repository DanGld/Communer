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

import com.communer.Models.CommentItem;
import com.communer.R;
import com.communer.Utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by יובל on 01/09/2015.
 */
public class CommentsAdapters  extends RecyclerView.Adapter<CommentsAdapters.ViewHolder> {

    private ArrayList<CommentItem> commentsArray;
    private Context mContext;
    private int lastPosition = -1;

    public CommentsAdapters(ArrayList<CommentItem> commentsArray, Context mContext) {
        this.commentsArray = commentsArray;
        this.mContext = mContext;
    }

    @Override
    public CommentsAdapters.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CommentsAdapters.ViewHolder holder, int position) {
        CommentItem comment = commentsArray.get(position);
        holder.username.setText(comment.getMmbrCommented().getName());

        SimpleDateFormat timeFormatter = new SimpleDateFormat("MMM dd", Locale.US);
        String formmatedDate = getFormattedDateFromTimestamp(comment.getDate(), timeFormatter);

        timeFormatter = new SimpleDateFormat("HH:mm", Locale.US);
        formmatedDate = formmatedDate + " at " + getFormattedDateFromTimestamp(comment.getDate(), timeFormatter);

        holder.commentDate.setText("Posted At: " + formmatedDate);
        holder.commentContent.setText(comment.getContent());

        String imageURL = comment.getMmbrCommented().getImageUrl();
        if (!imageURL.equals("")){
            Picasso.with(mContext)
                    .load(imageURL)
                    .error(R.drawable.placeholeder_lowres)
                    .transform(new CircleTransform())
                    .into(holder.userPic);
        }else{
            Picasso.with(mContext)
                    .load(R.drawable.placeholeder_lowres)
                    .transform(new CircleTransform())
                    .into(holder.userPic);
        }

        setAnimation(holder.container,position);
    }

    @Override
    public int getItemCount() {
        return commentsArray.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username, commentDate, commentContent;
        public ImageView userPic;
        public LinearLayout container;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            username = (TextView) itemLayoutView.findViewById(R.id.commenter_name);
            commentDate = (TextView) itemLayoutView.findViewById(R.id.comment_date);
            commentContent = (TextView) itemLayoutView.findViewById(R.id.comment_content);

            userPic = (ImageView) itemLayoutView.findViewById(R.id.commenter_pic);
            container = (LinearLayout) itemLayoutView.findViewById(R.id.comment_row_container);
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
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.push_left_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
