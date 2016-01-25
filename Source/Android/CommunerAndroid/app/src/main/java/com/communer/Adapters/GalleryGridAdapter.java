package com.communer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.communer.Activities.PhotoViewActivity;
import com.communer.Models.GalleryImage;
import com.communer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ���� on 05/08/2015.
 */
public class GalleryGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<GalleryImage> imgsArray;
    private Context context;

    public GalleryGridAdapter(ArrayList<GalleryImage> imgsArray, Context context) {
        this.imgsArray = imgsArray;
        this.context = context;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageButton imageItem;

        public ImageViewHolder(View v) {
            super(v);
            imageItem = (ImageButton)v.findViewById(R.id.gallery_grid_item);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View servicesView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_staggered_item, parent, false);
        ImageViewHolder ivh = new ImageViewHolder(servicesView);
        return ivh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ImageViewHolder imageHolder = (ImageViewHolder)holder;
        Picasso.with(context)
                .load(imgsArray.get(position).getUrl())
                .error(R.drawable.placeholeder_lowres)
                .into(imageHolder.imageItem);

        imageHolder.imageItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(context, PhotoViewActivity.class);
                mIntent.putParcelableArrayListExtra("Images",imgsArray);
                mIntent.putExtra("picked_img_pos", position);
                mIntent.putExtra("ParentClassName","GalleryGridActivity");
                context.startActivity(mIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imgsArray.size();
    }
}
