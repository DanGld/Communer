package com.communer.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.communer.Adapters.GalleryAdapter;
import com.communer.Models.CommunityObj;
import com.communer.Models.ImageEvent;
import com.communer.R;
import com.communer.Utils.AppUser;
import com.communer.Utils.RefreshUtil;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.ScaleInTopAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;

public class GalleryFragment extends Fragment {

    ArrayList<ImageEvent> galleries;

    private CommunityObj currentCommunity;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BroadcastReceiver refreshReceiver;

    private RecyclerView mRecyclerView;
    private GalleryAdapter galleryAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_tab, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppUser appUserInstance = AppUser.getInstance();
        currentCommunity = appUserInstance.getCurrentCommunity();
        galleries = currentCommunity.getGalleries();

        initReceiver();

        galleryAdapter = new GalleryAdapter(getActivity(),galleries,getActivity());
        mRecyclerView = (RecyclerView)view.findViewById(R.id.gallery_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new ScaleInTopAnimator());
        mRecyclerView.setAdapter(new AlphaInAnimationAdapter(galleryAdapter));

        initRefreshLayout(view);
    }

    private void initRefreshLayout(View v) {
        mSwipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.gallery_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.progress_blue,
                R.color.progress_red,
                R.color.progress_yellow,
                R.color.progress_green);
//        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(getActivity(), R.color.PrimaryColor));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("refresh_done");
        refreshReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra("Status");
                if (status.equals("Success")){
                    AppUser appUserInstance = AppUser.getInstance();

                    currentCommunity = appUserInstance.getCurrentCommunity();
                    galleries = currentCommunity.getGalleries();
                    galleryAdapter = new GalleryAdapter(getActivity(),galleries,getActivity());
                    mRecyclerView.setAdapter(new AlphaInAnimationAdapter(galleryAdapter));
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

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(refreshReceiver);
        super.onDestroy();
    }
}
