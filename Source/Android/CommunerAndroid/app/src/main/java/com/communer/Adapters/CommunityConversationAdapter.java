package com.communer.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.communer.Activities.CommunityConversation;
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
 * Created by יובל on 02/09/2015.
 */
public class CommunityConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Post> communityPosts;
    private Context mContext;

    public CommunityConversationAdapter(ArrayList<Post> communityPosts, Context mContext) {
        this.communityPosts = communityPosts;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View normalPostView = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_normal_post, parent, false);
                NormalPostHolder nvh = new NormalPostHolder(normalPostView);
                return nvh;

            case 1:
                View imagePostView = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_image_post, parent, false);
                ImagePostHolder ivh = new ImagePostHolder(imagePostView);
                return ivh;

        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Post item = communityPosts.get(position);
        if (item.getImageUrl().equals("")){
            final NormalPostHolder mHolder = (NormalPostHolder)holder;
            mHolder.postTitle.setText(item.getTitle());
            mHolder.postUsername.setText(item.getMember().getName());

            SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd yyyy", Locale.US);
            String dateString = getFormattedDateFromTimestamp(item.getCDate(),dateFormatter);

            mHolder.postDate.setText(dateString);
            mHolder.postContent.setText(item.getContent());
            mHolder.postComments.setText(String.valueOf(item.getComments().size()) + " Comments");

            String userPic = item.getMember().getImageUrl();
            if (!userPic.equals("")){
                Picasso.with(mContext)
                        .load(userPic)
                        .error(R.drawable.placeholeder_lowres)
                        .transform(new CircleTransform())
                        .into(mHolder.profilePic);
            }else{
                Picasso.with(mContext)
                        .load(R.drawable.placeholeder_lowres)
                        .transform(new CircleTransform())
                        .into(mHolder.profilePic);
            }

            mHolder.postComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof CommunityConversation) {
                        ((CommunityConversation) mContext).showCommentsDialog(item);
                    }
                }
            });

        }else{
            final ImagePostHolder iHolder = (ImagePostHolder)holder;

            iHolder.postTitle.setText(item.getTitle());
            iHolder.postUsername.setText(item.getMember().getName());

            SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd yyyy", Locale.US);
            String dateString = getFormattedDateFromTimestamp(item.getCDate(),dateFormatter);

            iHolder.postDate.setText(dateString);
            iHolder.postContent.setText(item.getContent());
            iHolder.postComments.setText(String.valueOf(item.getComments().size()) + " Comments");
            Picasso.with(mContext)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.placeholeder_lowres)
                    .error(R.drawable.placeholeder_lowres)
                    .into(iHolder.postImg);

            iHolder.postComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof CommunityConversation) {
                        ((CommunityConversation) mContext).showCommentsDialog(item);
                    }
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return communityPosts.size();
    }

    class NormalPostHolder extends RecyclerView.ViewHolder {

        TextView postTitle, postUsername, postDate, postContent;
        Button postComments;
        ImageView profilePic;

        public NormalPostHolder(View itemView) {
            super(itemView);
            postTitle = (TextView)itemView.findViewById(R.id.conversation_post_title);
            postUsername = (TextView)itemView.findViewById(R.id.conversation_poster_name);
            postDate = (TextView)itemView.findViewById(R.id.conversation_post_time);
            postContent = (TextView)itemView.findViewById(R.id.conversation_post_description);
            postComments = (Button)itemView.findViewById(R.id.conversation_post_comment_count);
            profilePic = (ImageView)itemView.findViewById(R.id.conversation_poster__profile_image);
        }
    }

    class ImagePostHolder extends RecyclerView.ViewHolder {

        TextView postTitle, postUsername, postDate, postContent;
        ImageView postImg;
        Button postComments;

        public ImagePostHolder(View itemView) {
            super(itemView);
            postTitle = (TextView)itemView.findViewById(R.id.conversation_post_title);
            postUsername = (TextView)itemView.findViewById(R.id.conversation_poster_name);
            postDate = (TextView)itemView.findViewById(R.id.conversation_post_time);
            postContent = (TextView)itemView.findViewById(R.id.conversation_post_description);
            postComments = (Button)itemView.findViewById(R.id.conversation_post_comment_count);

            postImg = (ImageView)itemView.findViewById(R.id.post_image);
        }
    }

    @Override
    public int getItemViewType(int position) {
    Post item = communityPosts.get(position);
    if (item.getImageUrl().equals(""))
        return 0;
    else
        return 1;
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
