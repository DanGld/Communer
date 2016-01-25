package com.communer.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.communer.Fragments.QAPrivate;
import com.communer.Fragments.QAPublic;

import java.util.HashMap;

/**
 * Created by ���� on 09/08/2015.
 */
public class PrivatePublicAdapter extends FragmentStatePagerAdapter {

    private HashMap<Integer,Fragment> mPageReferenceMap;
    private Context context;

    public PrivatePublicAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        this.mPageReferenceMap = new HashMap<Integer,Fragment>();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Fragment qaPrivate = new QAPrivate();
                mPageReferenceMap.put(position, qaPrivate);
                return qaPrivate;
            case 1:
                Fragment qaPublic = new QAPublic();
                mPageReferenceMap.put(position, qaPublic);
                return qaPublic;
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
                return "Private";
            case 1:
                return "Public";
            default:
                break;
        }
        return super.getPageTitle(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mPageReferenceMap.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return mPageReferenceMap.get(position);
    }
}
