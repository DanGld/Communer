package com.communer.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.communer.Models.CommunityContact;
import com.communer.R;
import com.communer.Utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by יובל on 28/09/2015.
 */
public class CommunityContactsAdapter extends BaseAdapter implements Filterable {

    private ArrayList<CommunityContact> allData;
    private ArrayList<CommunityContact> filteredData;
    private Context mContext;

    public CommunityContactsAdapter(ArrayList<CommunityContact> mData, Context mContext) {
        this.allData = mData;
        this.filteredData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
       return filteredData.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.participant_row_big, parent, false);

        TextView firstName =  (TextView)rowView.findViewById(R.id.participant_name);
        TextView firstPhone =  (TextView)rowView.findViewById(R.id.participant_phone);
        ImageView firstImg =  (ImageView)rowView.findViewById(R.id.participant_pic);

        CommunityContact item = filteredData.get(position);
        firstName.setText(item.getName());
        firstPhone.setText(item.getPhoneNumber());

        if (item.getImageUrl()!=null) {
            if (!item.getImageUrl().equals("")) {
                Picasso.with(mContext)
                        .load(item.getImageUrl())
                        .error(R.drawable.noprofilepic)
                        .transform(new CircleTransform())
                        .into(firstImg);
            } else {
                Picasso.with(mContext)
                        .load(R.drawable.noprofilepic)
                        .transform(new CircleTransform())
                        .into(firstImg);
            }
        }

        return rowView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (ArrayList<CommunityContact>)results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<CommunityContact> FilteredArray = new ArrayList<CommunityContact>();

                // perform your search here using the searchConstraint String.
                constraint = constraint.toString().toLowerCase();
                if(constraint == null || constraint.length() == 0){
                    results.values = allData;
                    results.count = allData.size();
                }else{
                    for (int i = 0; i < allData.size(); i++) {
                        String dataNames = allData.get(i).getName();
                        if (dataNames.toLowerCase().contains(constraint.toString())){
                            CommunityContact item = allData.get(i);
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
}
