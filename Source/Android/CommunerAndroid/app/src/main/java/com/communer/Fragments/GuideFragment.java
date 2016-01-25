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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.communer.Activities.CategoryWorkersActivity;
import com.communer.Adapters.GuideAdapter;
import com.communer.Models.GuideCategory;
import com.communer.R;
import com.communer.Utils.AppUser;
import com.communer.Utils.RefreshUtil;

import java.util.ArrayList;


public class GuideFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private GuideAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private EditText searchBar;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BroadcastReceiver refreshReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guide_tab, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.guide_category_cards);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        AppUser appUserInstance = AppUser.getInstance();
        ArrayList<GuideCategory> guideCategories = appUserInstance.getCurrentCommunity().getGuideCategories();

        mAdapter = new GuideAdapter(guideCategories,getActivity(),getActivity());
        mRecyclerView.setAdapter(mAdapter);

        initRefreshLayout(view);
        initReceiver();

        searchBar = (EditText)view.findViewById(R.id.guide_search_bar);
        searchBar.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (searchBar.getText().toString().length() > 2){
                        Intent mIntent = new Intent(getActivity(), CategoryWorkersActivity.class);
                        mIntent.putExtra("category_name",searchBar.getText().toString());
                        startActivity(mIntent);
                        hideSoftKeyboard();
                        return true;
                    }else{
                        Toast.makeText(getActivity(),"Search input has to be more than 2 characters",Toast.LENGTH_SHORT).show();
                    }

                }
                return false;
            }
        });
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    private void initRefreshLayout(View v) {
        mSwipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.guide_refresh_layout);
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
                    ArrayList<GuideCategory> guideCategories = appUserInstance.getCurrentCommunity().getGuideCategories();
                    mAdapter = new GuideAdapter(guideCategories,getActivity(),getActivity());
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

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(refreshReceiver);
        super.onDestroy();
    }
}
