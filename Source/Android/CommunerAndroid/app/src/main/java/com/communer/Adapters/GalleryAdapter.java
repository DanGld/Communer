package com.communer.Adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.communer.Activities.GalleryGridActivity;
import com.communer.Models.ImageEvent;
import com.communer.Models.MediaData;
import com.communer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by user on 9/24/2015.
 */
public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<ImageEvent> galleries;
    Activity activity;

    public GalleryAdapter(Context context, ArrayList<ImageEvent> galleries, Activity activity) {
        this.context = context;
        this.galleries = galleries;
        this.activity = activity;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return galleries.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View galleryItemHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_recycler_item, parent, false);
        final GalleryItemHolder nh = new GalleryItemHolder(galleryItemHolder);

        galleryItemHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = nh.getAdapterPosition();
                ImageEvent item = galleries.get(position);

                MediaData galleryMedia = item.getMedia();
                Intent mIntent = new Intent(context, GalleryGridActivity.class);
                mIntent.putExtra("galleryTitle", item.getGalleryTitle());
                mIntent.putParcelableArrayListExtra("galleryImgs", galleryMedia.getImages());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    context.startActivity(mIntent,
                            ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
                }else{
                    context.startActivity(mIntent);
                    activity.overridePendingTransition(R.anim.slide_in_up, R.anim.stay_anim);
                }
            }
        });

        return nh;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ImageEvent item = galleries.get(position);
        final GalleryItemHolder mHolder = (GalleryItemHolder) holder;

        Picasso.with(context)
                .load(item.getImageUrl())
                .error(R.drawable.placeholeder_lowres)
                .into(mHolder.itemImage);

        mHolder.itemTitle.setText(item.getGalleryTitle());
    }

    class GalleryItemHolder extends RecyclerView.ViewHolder {
        RelativeLayout container;
        ImageButton itemImage;
        TextView itemTitle;

        public GalleryItemHolder(View itemView) {
            super(itemView);
            container = (RelativeLayout) itemView.findViewById(R.id.gallery_recycler_container);
            itemImage = (ImageButton) itemView.findViewById(R.id.gallery_item_image);
            itemTitle = (TextView) itemView.findViewById(R.id.gallery_item_title);
        }
    }
}
