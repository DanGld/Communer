package com.communer.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import com.communer.Adapters.FacilitiesAdapter;
import com.communer.Models.CommunityActivityOrFacility;
import com.communer.R;
import com.communer.Utils.AppUser;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.ArrayList;

/**
 * Created by יובל on 02/09/2015.
 */
public class Facilities extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private FacilitiesAdapter myAdapter;

    private Toolbar toolbar;

    private String mixPanelProjectToken = "537b3567fe348d6c5dfa0d6e2ae688b3";
    private MixpanelAPI mixpanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facilities);

        mixpanel = MixpanelAPI.getInstance(Facilities.this, mixPanelProjectToken);
        mixpanel.track("Modules - Facilities Page");

        AppUser appUserInstance = AppUser.getInstance();
        ArrayList<CommunityActivityOrFacility> facilities = appUserInstance.getCurrentCommunity().getFacilities();

        mRecyclerView = (RecyclerView)findViewById(R.id.facilities_recycler);
        mLayoutManager = new LinearLayoutManager(Facilities.this);
        myAdapter = new FacilitiesAdapter(facilities,Facilities.this,Facilities.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(myAdapter);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);toolbar.setTitle(getResources().getString(R.string.facilities_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        LinearLayout linear = (LinearLayout)findViewById(R.id.facilities_root_layout);
        linear.setBackgroundResource(R.drawable.facilities_bg);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
