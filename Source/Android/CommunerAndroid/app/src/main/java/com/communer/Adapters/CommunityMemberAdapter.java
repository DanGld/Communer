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

import com.communer.Models.CommunityMember;
import com.communer.R;
import com.communer.Utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by יובל on 02/11/2015.
 */
public class CommunityMemberAdapter extends BaseAdapter implements Filterable {

    private ArrayList<CommunityMember> allData;
    private ArrayList<CommunityMember> filteredData;
    private Context mContext;

    public CommunityMemberAdapter(ArrayList<CommunityMember> mData, Context mContext) {
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

        TextView mmbrName =  (TextView)rowView.findViewById(R.id.participant_name);
        TextView mmbrPhone =  (TextView)rowView.findViewById(R.id.participant_phone);
        ImageView mmbrImg =  (ImageView)rowView.findViewById(R.id.participant_pic);

        CommunityMember item = filteredData.get(position);
        mmbrName.setText(item.getName());
        mmbrPhone.setText(item.getPhone());

        String imageUrl = item.getImageUrl();
        if (imageUrl != null){
            if (!imageUrl.equals("")){
                Picasso.with(mContext)
                        .load(imageUrl)
                        .error(R.drawable.noprofilepic)
                        .transform(new CircleTransform())
                        .into(mmbrImg);
            }else{
                Picasso.with(mContext)
                        .load(R.drawable.noprofilepic)
                        .transform(new CircleTransform())
                        .into(mmbrImg);
            }
        }else{
            Picasso.with(mContext)
                    .load(R.drawable.noprofilepic)
                    .transform(new CircleTransform())
                    .into(mmbrImg);
        }

        return rowView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (ArrayList<CommunityMember>)results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<CommunityMember> FilteredArray = new ArrayList<CommunityMember>();

                // perform your search here using the searchConstraint String.
                constraint = constraint.toString().toLowerCase();
                if(constraint == null || constraint.length() == 0){
                    results.values = allData;
                    results.count = allData.size();
                }else{
                    for (int i = 0; i < allData.size(); i++) {
                        String dataNames = allData.get(i).getName();
                        if (dataNames.toLowerCase().contains(constraint.toString())){
                            CommunityMember item = allData.get(i);
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