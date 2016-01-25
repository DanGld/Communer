package com.communer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.communer.Models.SecurityEvent;
import com.communer.R;

import java.util.ArrayList;

/**
 * Created by יובל on 02/09/2015.
 */
public class SecurityReportAdapter extends BaseAdapter {

    private ArrayList<SecurityEvent> mData;
    private Context mContext;

    public SecurityReportAdapter(ArrayList<SecurityEvent> mData, Context mContext) {
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
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.security_report_item, parent, false);

        TextView reportType = (TextView) rowView.findViewById(R.id.report_type);
        TextView reporterName = (TextView) rowView.findViewById(R.id.report_reporter_name);
        TextView reportLocation = (TextView) rowView.findViewById(R.id.report_location);

        ImageView severityImage = (ImageView) rowView.findViewById(R.id.severity_indicator);

        SecurityEvent item = mData.get(position);

        String eventType = item.getReport().getType();
        if (eventType.equalsIgnoreCase("null")){
            reportType.setText("No Type");
        }else{
            reportType.setText(item.getReport().getType());
        }

        String reporter = item.getReporterName();
        if (!reporter.contains("null"))
            reporterName.setText(reporter);
        else
            reporterName.setText("Annonymous");

        reportLocation.setText("at " + item.getReport().getLocationObj().getTitle());

        resolveSeverity(item.getReport().getSeverity(), severityImage);
        return rowView;
    }

    private void resolveSeverity(String severity, ImageView severityImage) {
        switch (severity) {
            case "low":
                severityImage.setImageResource(R.drawable.green_report);
                break;

            case "medium":
                severityImage.setImageResource(R.drawable.yellow_report);
                break;

            case "high":
                severityImage.setImageResource(R.drawable.red_report);
                break;

            default:
                severityImage.setImageResource(R.drawable.green_report);
                break;
        }
    }
}
