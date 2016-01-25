package com.communer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.communer.Models.ServiceContact;
import com.communer.R;
import com.communer.Utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ���� on 27/08/2015.
 */
public class CategoryWorkersAdapter extends BaseAdapter {

    private ArrayList<ServiceContact> mData;
    private Context mContext;

    public CategoryWorkersAdapter(ArrayList<ServiceContact> mData, Context mContext) {
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
        View rowView = inflater.inflate(R.layout.category_worker_row, parent, false);

        ImageView profile_img = (ImageView)rowView.findViewById(R.id.worker_image);
        TextView name = (TextView)rowView.findViewById(R.id.worker_name);
        TextView location = (TextView)rowView.findViewById(R.id.worker_location);
        TextView phone = (TextView)rowView.findViewById(R.id.worker_phone);
        TextView rating = (TextView)rowView.findViewById(R.id.worker_rating);

        ServiceContact item = mData.get(position);
        name.setText(item.getName());
        location.setText(item.getLocationObj().getTitle());
        phone.setText(item.getPhoneNumber());
        rating.setText(String.valueOf(item.getRate()));

        String imageURL = item.getImageUrl();
        if (!imageURL.equals("")){
            Picasso.with(mContext)
                .load(item.getImageUrl())
                .transform(new CircleTransform())
                .error(R.drawable.placeholeder_lowres)
                .into(profile_img);
        }else {
            Picasso.with(mContext)
                .load(R.drawable.placeholeder_lowres)
                .transform(new CircleTransform())
                .error(R.drawable.placeholeder_lowres)
                .into(profile_img);
        }

        return rowView;
    }
}
