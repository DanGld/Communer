package com.communer.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.style.LineBackgroundSpan;

import com.communer.R;

/**
 * Created by Yuval on 1/3/2016.
 */
public class MyCustomDotSpan implements LineBackgroundSpan {
    public static final float DEFAULT_RADIUS = 3;

    private final float radius;
    private final int color;

    private boolean hasPrayers = false, hasActivities = false, hasEvents = false;
    private Context mContext;

    public MyCustomDotSpan() {
        this.radius = DEFAULT_RADIUS;
        this.color = 0;
    }

    public MyCustomDotSpan(int color) {
        this.radius = DEFAULT_RADIUS;
        this.color = color;
    }

    public MyCustomDotSpan(float radius) {
        this.radius = radius;
        this.color = 0;
    }

    public MyCustomDotSpan(float radius, int color, boolean hasPrayers, boolean hasActivities, boolean hasEvents, Context context) {
        this.radius = radius;
        this.color = color;

        this.hasPrayers = hasPrayers;
        this.hasActivities = hasActivities;
        this.hasEvents = hasEvents;
        this.mContext = context;
    }

    @Override
    public void drawBackground(
            Canvas canvas, Paint paint,
            int left, int right, int top, int baseline, int bottom,
            CharSequence charSequence,
            int start, int end, int lineNum
    ) {
        int oldColor = paint.getColor();
        if (color != 0) {
            paint.setColor(color);
        }

        if (hasActivities)
            canvas.drawCircle((left + right) / 2, bottom + radius, radius, paint);

        if (hasPrayers){
            paint.setColor(ContextCompat.getColor(mContext, R.color.AppOrange));
            canvas.drawCircle((left + right) / 2 + 30, bottom + radius, radius, paint);
        }

        if (hasEvents){
            paint.setColor(ContextCompat.getColor(mContext, R.color.app_green));
            canvas.drawCircle((left + right) / 2 - 30, bottom + radius, radius, paint);
        }

        paint.setColor(oldColor);
    }
}
