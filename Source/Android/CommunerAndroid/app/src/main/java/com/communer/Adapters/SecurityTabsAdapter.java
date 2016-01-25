package com.communer.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.communer.Fragments.SecurityMap;
import com.communer.Fragments.SecurityReport;

import java.util.HashMap;

/**
 * Created by יובל on 02/09/2015.
 */
public class SecurityTabsAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private HashMap<Integer,Fragment> mPageReferenceMap;

    public SecurityTabsAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        this.mPageReferenceMap = new HashMap<Integer,Fragment>();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Fragment map = new SecurityMap();
                mPageReferenceMap.put(position, map);
                return map;
            case 1:
                Fragment report = new SecurityReport();
                mPageReferenceMap.put(position, report);
                return report;
            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "MAP VIEW";
            case 1:
                return "REPORT LIST";
            default:
                break;
        }
        return super.getPageTitle(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        mPageReferenceMap.remove(position);
    }

    public Fragment getFragment(int key) {
        return mPageReferenceMap.get(key);
    }
}
