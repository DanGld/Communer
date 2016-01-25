package com.communer.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.communer.Activities.TabsActivity;
import com.communer.Adapters.HomeAdapter;
import com.communer.Models.EventOrMessage;
import com.communer.R;
import com.communer.Utils.AppUser;
import com.communer.Utils.HomeDataUtil;
import com.communer.Utils.RefreshUtil;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by ���� on 28/07/2015.
 */
public class HomeFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<EventOrMessage> recentAnnouncements;
    private HomeDataUtil homeData;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BroadcastReceiver refreshReceiver;

    private int screenWidth;
    private ViewPager mainPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hometab, container, false);
        return view;
    }

//    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.homeCardList);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        initRefreshLayout(view);
        initReceiver();

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        AppUser appUserInstance = AppUser.getInstance();
        ArrayList<String> widgetsList = appUserInstance.getCurrentCommunity().getWidgets();
        recentAnnouncements = getRecentAnnouncements(appUserInstance.getCurrentCommunity().getAnnouncements());

        mainPager = ((TabsActivity)getActivity()).myViewPager;
        mAdapter = new HomeAdapter(getActivity(),screenWidth,recentAnnouncements,widgetsList, getActivity(),mainPager);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void initRefreshLayout(View v) {
        mSwipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.home_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.progress_blue,
                R.color.progress_red,
                R.color.progress_yellow,
                R.color.progress_green);
//        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(getActivity(),R.color.PrimaryColor));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
    }

    private void initReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("refresh_done");
        refreshReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent){
                String status = intent.getStringExtra("Status");
                if (status.equals("Success")){
                    AppUser appUserInstance = AppUser.getInstance();
                    ArrayList<String> widgetsList = appUserInstance.getCurrentCommunity().getWidgets();
                    recentAnnouncements = getRecentAnnouncements(appUserInstance.getCurrentCommunity().getAnnouncements());

                    mainPager = ((TabsActivity)getActivity()).myViewPager;
                    mAdapter = new HomeAdapter(getActivity(),screenWidth,recentAnnouncements,widgetsList, getActivity(),mainPager);
                    mRecyclerView.setAdapter(mAdapter);
                }

                mSwipeRefreshLayout.setRefreshing(false);
            }
        };
        getActivity().registerReceiver(refreshReceiver, filter);
    }

    private void refreshContent() {
        RefreshUtil refreshUtil = new RefreshUtil(getActivity());
        refreshUtil.getData("24.323288", "26.135633");
    }

    private ArrayList<EventOrMessage> getRecentAnnouncements(ArrayList<EventOrMessage> announcements) {
        HomeDataUtil hdu = new HomeDataUtil();
        ArrayList<EventOrMessage> recentAnnoun = null;
        try {
            recentAnnoun = hdu.getUpcomingAnnouncements(announcements, 2);
        } catch (ParseException e){
            e.printStackTrace();
        }
        return recentAnnoun;
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(refreshReceiver);
        super.onDestroy();
    }
}
