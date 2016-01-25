package com.communer.Adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.communer.Activities.CategoryWorkersActivity;
import com.communer.Models.GuideCategory;
import com.communer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ���� on 25/08/2015.
 */
public class GuideAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private ArrayList<GuideCategory> allGuideArray;
    private ArrayList<GuideCategory> filteredGuideArray;
    private Context mContext;
    private Activity activity;

    public GuideAdapter(ArrayList<GuideCategory> guideArray, Context mContext, Activity activity) {
        this.allGuideArray = guideArray;
        this.filteredGuideArray = guideArray;
        this.mContext = mContext;
        this.activity = activity;
    }

    public static class GuideViewHolder extends RecyclerView.ViewHolder{

        public RelativeLayout firstCardCntnr, secondCardCntnr;
        public TextView firstTitle, secondTitle;
        public ImageButton firstImg, secondImg;
        public CardView firstCard, secondCard;

        public GuideViewHolder(View itemView) {
            super(itemView);
            firstCardCntnr = (RelativeLayout)itemView.findViewById(R.id.first_guide_card_container);
            firstTitle = (TextView)itemView.findViewById(R.id.first_card_title);
            firstImg = (ImageButton)itemView.findViewById(R.id.first_card_guide);

            secondCardCntnr = (RelativeLayout)itemView.findViewById(R.id.second_guide_card_container);
            secondTitle = (TextView)itemView.findViewById(R.id.second_card_title);
            secondImg = (ImageButton)itemView.findViewById(R.id.second_card_guide);

            firstCard = (CardView)itemView.findViewById(R.id.first_card_view);
            secondCard = (CardView)itemView.findViewById(R.id.second_card_view);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View guideView = LayoutInflater.from(parent.getContext()).inflate(R.layout.guide_card, parent, false);
        GuideViewHolder gvh = new GuideViewHolder(guideView);
        return gvh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        GuideViewHolder guideViewHolder = (GuideViewHolder)holder;
        if (position+1 != getItemCount()){
            GuideCategory firstItem = filteredGuideArray.get(position * 2);
            GuideCategory secondItem = filteredGuideArray.get(position * 2 + 1);

            guideViewHolder.firstTitle.setText(firstItem.getTitle());
            guideViewHolder.secondTitle.setText(secondItem.getTitle());

            String firstURL = firstItem.getImageUrl();
            String secondURL = secondItem.getImageUrl();
            if (firstURL.equals("")){
                firstURL = "empty";
            }else if (secondURL.equals("")){
                secondURL = "empty";
            }

            Picasso.with(mContext)
                    .load(firstURL)
                    .error(R.drawable.placeholeder_lowres)
                    .into(guideViewHolder.firstImg);

            Picasso.with(mContext)
                    .load(secondURL)
                    .error(R.drawable.placeholeder_lowres)
                    .into(guideViewHolder.secondImg);

            if (guideViewHolder.firstCard.getVisibility() == View.INVISIBLE)
                guideViewHolder.firstCard.setVisibility(View.VISIBLE);

            if (guideViewHolder.secondCard.getVisibility() == View.INVISIBLE)
                guideViewHolder.secondCard.setVisibility(View.VISIBLE);
        }else{
            if (filteredGuideArray.size() % 2 != 0){
                GuideCategory firstItem = filteredGuideArray.get(position * 2);

                guideViewHolder.firstTitle.setText(firstItem.getTitle());
                guideViewHolder.secondCard.setVisibility(View.INVISIBLE);

                String firstURL = firstItem.getImageUrl();
                if (firstURL.equals("")){
                    firstURL = "empty";
                }

                Picasso.with(mContext)
                        .load(firstURL)
                        .error(R.drawable.placeholeder_lowres)
                        .into(guideViewHolder.firstImg);
            }else{
                GuideCategory firstItem = filteredGuideArray.get(position * 2);
                GuideCategory secondItem = filteredGuideArray.get(position * 2 + 1);

                guideViewHolder.firstTitle.setText(firstItem.getTitle());
                guideViewHolder.secondTitle.setText(secondItem.getTitle());

                if (guideViewHolder.firstCard.getVisibility() == View.INVISIBLE)
                    guideViewHolder.firstCard.setVisibility(View.VISIBLE);

                if (guideViewHolder.secondCard.getVisibility() == View.INVISIBLE)
                    guideViewHolder.secondCard.setVisibility(View.VISIBLE);

                String firstURL = firstItem.getImageUrl();
                String secondURL = secondItem.getImageUrl();
                if (firstURL.equals("")){
                    firstURL = "empty";
                }

                if (secondURL.equals("")){
                    secondURL = "empty";
                }

                Picasso.with(mContext)
                        .load(firstURL)
                        .error(R.drawable.placeholeder_lowres)
                        .into(guideViewHolder.firstImg);

                Picasso.with(mContext)
                        .load(secondURL)
                        .error(R.drawable.placeholeder_lowres)
                        .into(guideViewHolder.secondImg);
            }
        }

        guideViewHolder.firstImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuideCategory guideCategory = filteredGuideArray.get(position * 2);
                Intent mIntent = new Intent(mContext, CategoryWorkersActivity.class);
                mIntent.putExtra("category_name", guideCategory.getTitle());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mContext.startActivity(mIntent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
                } else {
                    mContext.startActivity(mIntent);
                }
            }
        });

        guideViewHolder.secondImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuideCategory guideCategory = filteredGuideArray.get(position*2 + 1);
                Intent mIntent = new Intent(mContext, CategoryWorkersActivity.class);
                mIntent.putExtra("category_name", guideCategory.getTitle());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mContext.startActivity(mIntent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
                }else{
                    mContext.startActivity(mIntent);
                }

//                activity.overridePendingTransition(R.anim.slide_in_up, R.anim.stay_anim);
            }
        });
    }


    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredGuideArray = (ArrayList<GuideCategory>)results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<GuideCategory> FilteredArray = new ArrayList<GuideCategory>();

                // perform your search here using the searchConstraint String.
                constraint = constraint.toString().toLowerCase();
                if(constraint == null || constraint.length() == 0){
                    results.values = allGuideArray;
                    results.count = allGuideArray.size();
                }else{
                    for (int i = 0; i < allGuideArray.size(); i++) {
                        String dataNames = allGuideArray.get(i).getTitle();
                        if (dataNames.toLowerCase().contains(constraint.toString())){
                            GuideCategory item = allGuideArray.get(i);
                            FilteredArray.add(item);
                        }
                    }
                    results.count = FilteredArray.size();
                    results.values = FilteredArray;
                    Log.d("VALUES", results.values.toString());
                }
                return results;
            }
        };

        return filter;
    }

    @Override
    public int getItemCount() {
        if (filteredGuideArray.size() % 2 == 0)
            return filteredGuideArray.size()/2;
        else{
            if (filteredGuideArray.size()!=0)
                return filteredGuideArray.size()/2 + 1;
            else
                return 0;
        }
    }
}
