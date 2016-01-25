package com.communer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.communer.Models.Post;
import com.communer.R;
import com.communer.Utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by יובל on 30/08/2015.
 */
public class QuestionNAnswerAdapter extends BaseAdapter {

    private ArrayList<Post> mData;
    private Context mContext;

    public QuestionNAnswerAdapter(ArrayList<Post> mData, Context context) {
        this.mData = mData;
        this.mContext = context;
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
        View rowView = inflater.inflate(R.layout.question_layout, parent, false);

        TextView questionTitle = (TextView)rowView.findViewById(R.id.question_title);
        TextView questionAsker = (TextView)rowView.findViewById(R.id.question_asker_name);
        TextView questionContent = (TextView)rowView.findViewById(R.id.question_description);
        TextView commentCount = (TextView)rowView.findViewById(R.id.question_comment_count);
        TextView questionTime = (TextView)rowView.findViewById(R.id.question_time);
        TextView invisibleHeader = (TextView)rowView.findViewById(R.id.qa_header);

        ImageView profilePic = (ImageView)rowView.findViewById(R.id.question_asker_profile_image);

        Post item = mData.get(position);
        questionTitle.setText(item.getTitle());
        questionAsker.setText(item.getMember().getName());
        questionContent.setText(item.getContent());
        commentCount.setText(String.valueOf(item.getComments().size()) + " Comments");

        SimpleDateFormat timeFormatter = new SimpleDateFormat("MMM dd", Locale.US);
        String formmatedDate = getFormattedDateFromTimestamp(item.getCDate(), timeFormatter);

        timeFormatter = new SimpleDateFormat("HH:mm", Locale.US);
        formmatedDate = formmatedDate + " at " + getFormattedDateFromTimestamp(item.getCDate(), timeFormatter);
        questionTime.setText("Posted At: " + formmatedDate);

        Picasso.with(mContext)
                .load(item.getMember().getImageUrl())
                .error(R.drawable.placeholeder_lowres)
                .transform(new CircleTransform())
                .into(profilePic);

        invisibleHeader.setVisibility(View.GONE);
        return rowView;
    }

    public static String getFormattedDateFromTimestamp(long timestampInMilliSeconds, SimpleDateFormat formatter)
    {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        formatter.setTimeZone(timeZone);
        Calendar c = Calendar.getInstance(timeZone);
        c.setTimeInMillis(timestampInMilliSeconds);
        String formattedDate = formatter.format(c.getTime());
        return formattedDate;
    }
}
