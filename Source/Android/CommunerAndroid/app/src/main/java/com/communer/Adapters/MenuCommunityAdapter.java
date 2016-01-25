package com.communer.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.communer.Models.CommunityObj;
import com.communer.R;
import com.communer.Utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by יובל on 01/09/2015.
 */
public class MenuCommunityAdapter extends BaseAdapter {

    private ArrayList<CommunityObj> mData;
    private Context mContext;

    public MenuCommunityAdapter(ArrayList<CommunityObj> mData, Context mContext) {
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
        View rowView = inflater.inflate(R.layout.menu_community, parent, false);

        ImageView communityImg = (ImageView)rowView.findViewById(R.id.menu_community_profile_img);
        TextView communityName = (TextView)rowView.findViewById(R.id.menu_community_name);
        TextView communityLoc = (TextView)rowView.findViewById(R.id.menu_community_location);
        TextView communityUnreadMessages = (TextView)rowView.findViewById(R.id.community_menu_unread_messages);

        CommunityObj item = mData.get(position);

        String imageURL = item.getCommunityImageUrl();
        if (!imageURL.equals("")){
            Picasso.with(mContext)
                    .load(imageURL)
                    .error(R.drawable.placeholeder_lowres)
                    .transform(new CircleTransform())
                    .into(communityImg);
        }else{
            Picasso.with(mContext)
                    .load(R.drawable.placeholeder_lowres)
                    .transform(new CircleTransform())
                    .into(communityImg);
        }

        communityName.setText(item.getCommunityName());

        String location = item.getLocation().getTitle();
        if (location != null && !location.equals("null")){
            communityLoc.setText(location);
        }else{
            communityLoc.setText("");
        }

        SharedPreferences sp = mContext.getSharedPreferences("SmartCommunity", Activity.MODE_PRIVATE);
        int unreadMessagesCount = sp.getInt("communityUnread" + item.getCommunityID(),0);
        if (unreadMessagesCount == 0){
            communityUnreadMessages.setVisibility(View.GONE);
        }else{
            communityUnreadMessages.setText(String.valueOf(unreadMessagesCount));
            communityUnreadMessages.setVisibility(View.VISIBLE);
        }

        return rowView;
    }
}
