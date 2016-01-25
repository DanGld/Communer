package com.communer.Adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.communer.Activities.FacilityPage;
import com.communer.Models.CommunityActivityOrFacility;
import com.communer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by יובל on 02/09/2015.
 */
public class FacilitiesAdapter extends RecyclerView.Adapter<FacilitiesAdapter.ViewHolder> {

    private ArrayList<CommunityActivityOrFacility> mData;
    private Context mContext;
    private Activity activity;

    public FacilitiesAdapter(ArrayList<CommunityActivityOrFacility> mData, Context mContext, Activity activity) {
        this.mData = mData;
        this.mContext = mContext;
        this.activity = activity;
    }

    @Override
    public FacilitiesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.facility_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FacilitiesAdapter.ViewHolder holder, int position) {
        final CommunityActivityOrFacility item = mData.get(position);
        holder.facilityTitle.setText(item.getName());

        String bigUrl = item.getBigImageUrl();
        if (!bigUrl.equals("") && bigUrl!=null){
            Picasso.with(mContext)
                    .load(bigUrl)
                    .placeholder(R.drawable.placeholeder_lowres)
                    .error(R.drawable.placeholeder_lowres)
                    .into(holder.facilityImg);
        }else{
            Picasso.with(mContext)
                    .load(R.drawable.placeholeder_highres)
                    .placeholder(R.drawable.placeholeder_lowres)
                    .error(R.drawable.placeholeder_lowres)
                    .into(holder.facilityImg);
        }

        holder.cardCntnr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mContext, FacilityPage.class);
                mIntent.putExtra("facility_events",item.getEvents());
                mIntent.putExtra("facility_contact",item.getCommunityContact());
                mIntent.putExtra("facility_title",item.getName());
                mIntent.putExtra("facility_images",item.getImages());
                mIntent.putExtra("facility_desc",item.getDescription());

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

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView facilityTitle;
        public ImageView facilityImg;
        public CardView cardCntnr;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            facilityTitle = (TextView)itemLayoutView.findViewById(R.id.facility_title);
            facilityImg = (ImageView)itemLayoutView.findViewById(R.id.facility_image);
            cardCntnr = (CardView)itemLayoutView.findViewById(R.id.card_view);
        }
    }
}
