package com.communer.Utils;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by יובל on 15/12/2015.
 */
public class ResizeAnimation extends Animation {

    View view;
    int startH;
    int endH;
    int diff;

    String animType;

    public ResizeAnimation(View v, int newh, String animType)
    {
        view = v;
        startH = v.getHeight();
        Log.d("startH", String.valueOf(startH));
        endH = newh;
        diff = endH - startH;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t)
    {
        view.getLayoutParams().height = startH + (int)(diff*interpolatedTime);
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight)
    {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds()
    {
        return true;
    }

}
