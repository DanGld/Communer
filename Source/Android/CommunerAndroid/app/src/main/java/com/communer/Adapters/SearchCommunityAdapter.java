package com.communer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.communer.Models.CommunitySearchObj;
import com.communer.R;
import com.communer.Utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by יובל on 15/09/2015.
 */
public class SearchCommunityAdapter extends BaseAdapter {

    private ArrayList<CommunitySearchObj> mData;
    private Context mContext;

    public SearchCommunityAdapter(ArrayList<CommunitySearchObj> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.search_community_row, parent, false);

        ImageView communityPic = (ImageView)rowView.findViewById(R.id.search_community_pic);
        TextView communityName = (TextView)rowView.findViewById(R.id.search_community_name);
        TextView communityLoc = (TextView)rowView.findViewById(R.id.search_community_location);
        TextView communityDef = (TextView)rowView.findViewById(R.id.search_community_definition);

        CommunitySearchObj item = mData.get(position);

        String imageURL = item.getImageURL();
        if (!imageURL.equals("")){
            Picasso.with(mContext)
                    .load(imageURL)
                    .transform(new CircleTransform())
                    .into(communityPic);
        }else{
            Picasso.with(mContext)
                    .load(R.drawable.placeholeder_lowres)
                    .transform(new CircleTransform())
                    .into(communityPic);
        }

        communityName.setText(item.getName());
        communityLoc.setText(item.getLocation().getTitle());

        communityDef.setText(item.getType());

        return rowView;
    }
}
