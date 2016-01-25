package com.communer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.communer.Models.SmallPrayer;
import com.communer.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by יובל on 21/10/2015.
 */
public class PrayersTimeAdapter extends BaseAdapter {

    private ArrayList<SmallPrayer> mData;
    private Context mContext;

    public PrayersTimeAdapter(ArrayList<SmallPrayer> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
        this.mData = arrangePrayersOrder();
    }

    private ArrayList<SmallPrayer> arrangePrayersOrder() {
        ArrayList<SmallPrayer> orderedPrayers = new ArrayList<SmallPrayer>();

        for (int i=0; i<mData.size(); i++){
            SmallPrayer tempPrayer = mData.get(i);

            if (i==0)
                orderedPrayers.add(tempPrayer);
            else{
                int orderedPrayersSize = orderedPrayers.size();
                boolean addedItem = false;

                for (int j = 0; j < orderedPrayersSize; j++) {
                    SmallPrayer existingPrayer = orderedPrayers.get(j);

                    long existingPrayerLong = existingPrayer.getPrayerInMillis();
                    Calendar existingPrayersCal = Calendar.getInstance();
                    existingPrayersCal.setTimeInMillis(existingPrayerLong);

                    Calendar checkingPrayersCal = Calendar.getInstance();
                    checkingPrayersCal.setTimeInMillis(tempPrayer.getPrayerInMillis());

                    if (checkingPrayersCal.before(existingPrayersCal)){
                        orderedPrayers.add(j, tempPrayer);
                        addedItem = true;
                        break;
                    }
                }

                if (!addedItem)
                    orderedPrayers.add(tempPrayer);
            }
        }

        return orderedPrayers;
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
        View rowView = inflater.inflate(R.layout.prayers_time_row, parent, false);

        TextView prayerTime = (TextView)rowView.findViewById(R.id.prayer_time);
        TextView prayerText = (TextView)rowView.findViewById(R.id.prayer_text);

        SmallPrayer item = mData.get(position);

        if (item.getType().equals("")){
            prayerTime.setText(item.getTime());
            prayerText.setText(item.getTitle());
        }

        return rowView;
    }
}
