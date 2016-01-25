package com.communer.Adapters;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.communer.Fragments.CalendarFragment;
import com.communer.Fragments.GalleryFragment;
import com.communer.Fragments.GuideFragment;
import com.communer.Fragments.HomeFragment;
import com.communer.Fragments.MessagesFragment;
import com.communer.R;

import java.util.HashMap;

public class TabsPagerAdapter extends FragmentStatePagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

    private int[] imageResId = {
            R.drawable.selector_home,
            R.drawable.selector_calendar,
            R.drawable.selector_messages,
            R.drawable.selector_galley,
            R.drawable.selector_guide
    };

    private Context context;
    private HashMap<Integer,Fragment> mPageReferenceMap;

    public TabsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        this.mPageReferenceMap = new HashMap<Integer,Fragment>();
    }

    @Override
    public Fragment getItem(int index) {
        switch (index){
            case 0:
                Fragment home = new HomeFragment();
                mPageReferenceMap.put(index, home);
                return home;
            case 1:
                Fragment prayer = new CalendarFragment();
                mPageReferenceMap.put(index, prayer);
                return prayer;
            case 2:
                Fragment messages = new MessagesFragment();
                mPageReferenceMap.put(index, messages);
                return messages;
            case 3:
                Fragment gallery = new GalleryFragment();
                mPageReferenceMap.put(index, gallery);
                return gallery;
            case 4:
                Fragment guide = new GuideFragment();
                mPageReferenceMap.put(index, guide);
                return guide;
        }
        return null;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public int getPageIconResId(int i) {
        return imageResId[i];
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        super.destroyItem(container, position, object);
        mPageReferenceMap.remove(position);
    }

    public Fragment getFragment(int key) {
        return mPageReferenceMap.get(key);
    }
}
