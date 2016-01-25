package com.communer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.communer.Models.ServiceHourInfo;
import com.communer.R;

import java.util.ArrayList;

public class ServicesHourAdapter extends BaseAdapter {

    private ArrayList<ServiceHourInfo> mData;
    private Context context;

    public ServicesHourAdapter(ArrayList<ServiceHourInfo> mData, Context context) {
        this.mData = mData;
        this.context = context;
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
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.prayer_service_hour, parent, false);

        TextView text = (TextView)rowView.findViewById(R.id.prayers_service_title);
        TextView hour = (TextView)rowView.findViewById(R.id.prayers_service_hour);

        ServiceHourInfo item = mData.get(position);
        text.setText(item.getTitle());
        hour.setText(item.getHour());

        return rowView;
    }
}
