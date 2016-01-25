package com.communer.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.communer.Models.ServiceComment;
import com.communer.R;
import com.communer.Utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by יובל on 30/08/2015.
 */
public class WorkerCommentsAdapter extends RecyclerView.Adapter<WorkerCommentsAdapter.ViewHolder> {

    private ArrayList<ServiceComment> commentsData;
    private Context mContext;

    public WorkerCommentsAdapter(ArrayList<ServiceComment> commentsData, Context mContext) {
        this.commentsData = commentsData;
        this.mContext = mContext;
    }

    @Override
    public WorkerCommentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.worker_comment_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(WorkerCommentsAdapter.ViewHolder holder, int position) {
        ServiceComment comment = commentsData.get(position);
        holder.username.setText(comment.getName());
        holder.commentContent.setText(comment.getContent());

        String imageURL = comment.getImageUrl();
        if (!imageURL.equals("")){
            Picasso.with(mContext)
                    .load(comment.getImageUrl())
                    .error(R.drawable.placeholeder_lowres)
                    .transform(new CircleTransform())
                    .into(holder.userPic);
        }else{
            Picasso.with(mContext)
                    .load(R.drawable.placeholeder_lowres)
                    .transform(new CircleTransform())
                    .into(holder.userPic);
        }

        holder.workerRatingBar.setRating((float)comment.getRate());
    }

    @Override
    public int getItemCount() {
        if (commentsData!= null)
            return commentsData.size();
        else
            return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username, commentContent;
        public RatingBar workerRatingBar;
        public ImageView userPic;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            username = (TextView) itemLayoutView.findViewById(R.id.worker_comment_username);
            commentContent = (TextView) itemLayoutView.findViewById(R.id.worker_comment_content);

            workerRatingBar = (RatingBar) itemLayoutView.findViewById(R.id.worker_stars_bar);
            userPic = (ImageView) itemLayoutView.findViewById(R.id.worker_comment_user_image);
        }
    }
}
