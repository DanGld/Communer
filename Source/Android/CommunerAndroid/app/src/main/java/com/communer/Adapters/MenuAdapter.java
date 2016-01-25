package com.communer.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.communer.Models.MenuIcon;
import com.communer.R;

import java.util.ArrayList;


public class MenuAdapter extends BaseAdapter {

    private ArrayList<MenuIcon> mData;
    private Context mContext;

    public MenuAdapter(ArrayList<MenuIcon> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public MenuIcon getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.menuicon, parent, false);

        View divider = rowView.findViewById(R.id.menu_items_divider);
        ImageView icon = (ImageView)rowView.findViewById(R.id.menu_item_icon);
        TextView itemTitle = (TextView)rowView.findViewById(R.id.menu_item_text);

        MenuIcon item =  getItem(position);
        if (position + 1 < mData.size()){
            MenuIcon nextItem = getItem(position + 1);
            if (nextItem.getItemName().equalsIgnoreCase("my profile") || nextItem.getItemName().equalsIgnoreCase("הפרופיל שלי"))
                divider.setVisibility(View.VISIBLE);
        }

        icon.setImageDrawable(ContextCompat.getDrawable(mContext, item.getItemIcon()));
        itemTitle.setText(item.getItemName());

        return rowView;
    }
}
