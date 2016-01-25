package com.communer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.communer.R;
import com.communer.Utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by יובל on 21/12/2015.
 */
public class ShareIntentsAdapter extends BaseAdapter {

    private List<Intent> shareIntents;
    private Context mContext;

    public ShareIntentsAdapter(List<Intent> shareIntents, Context mContext) {
        this.shareIntents = shareIntents;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return shareIntents.size();
    }

    @Override
    public Object getItem(int position) {
        return shareIntents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.share_intent_row, parent, false);

        ImageView appIcon = (ImageView)rowView.findViewById(R.id.share_intent_icon);
        TextView appName = (TextView)rowView.findViewById(R.id.share_intent_name);

        Intent item = shareIntents.get(position);

        String appPackage = item.getStringExtra("packageName");
        String appPackageName = item.getStringExtra("appName");
        Drawable appPackageIcon = null;
        try {
            appPackageIcon = mContext.getPackageManager().getApplicationIcon(appPackage);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (appPackageIcon != null){
            appIcon.setImageDrawable(appPackageIcon);
        }else{
            Picasso.with(mContext)
                    .load(R.drawable.placeholeder_lowres)
                    .transform(new CircleTransform())
                    .into(appIcon);
        }

        appName.setText(appPackageName);
        return rowView;
    }
}
