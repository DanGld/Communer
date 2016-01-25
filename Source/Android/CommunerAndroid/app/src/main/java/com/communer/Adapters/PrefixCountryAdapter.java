package com.communer.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.communer.Models.PrefixCountry;
import com.communer.R;

import java.util.ArrayList;

/**
 * Created by יובל on 03/09/2015.
 */
public class PrefixCountryAdapter extends BaseAdapter implements Filterable {

    private ArrayList<PrefixCountry> allCountries;
    private ArrayList<PrefixCountry> filteredCountries;
    private Context mContext;

    public PrefixCountryAdapter(ArrayList<PrefixCountry> mData, Context mContext) {
        this.allCountries = mData;
        this.filteredCountries = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return filteredCountries.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredCountries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.country_prefix_row, parent, false);

        TextView countryTitle = (TextView)rowView.findViewById(R.id.country_prefix_title);
        TextView countryNumber = (TextView)rowView.findViewById(R.id.country_prefix_number);

        PrefixCountry item = filteredCountries.get(position);
        countryTitle.setText(item.getCountryName());
        countryNumber.setText(item.getCountryNum());

        return rowView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredCountries = (ArrayList<PrefixCountry>)results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<PrefixCountry> FilteredArray = new ArrayList<PrefixCountry>();

                // perform your search here using the searchConstraint String.
                constraint = constraint.toString().toLowerCase();
                if(constraint == null || constraint.length() == 0){
                    results.values = allCountries;
                    results.count = allCountries.size();
                }else{
                    for (int i = 0; i < allCountries.size(); i++) {
                        String dataNames = allCountries.get(i).getCountryName();
                        String dataNumbers = allCountries.get(i).getCountryNum();

                        if (dataNumbers == null)
                            dataNumbers = "";

                        try {
                            if (dataNames.toLowerCase().contains(constraint.toString()) || dataNumbers.contains(constraint.toString())){
                                PrefixCountry item = allCountries.get(i);
                                FilteredArray.add(item);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
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
