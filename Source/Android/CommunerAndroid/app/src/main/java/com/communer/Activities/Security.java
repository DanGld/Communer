package com.communer.Activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.astuetz.PagerSlidingTabStrip;
import com.communer.Adapters.SecurityTabsAdapter;
import com.communer.R;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

public class Security extends AppCompatActivity {

    public static FragmentManager fragmentManager;
    private SecurityTabsAdapter myAdapter;
    private ViewPager myViewPager;
    private PagerSlidingTabStrip tabs;

    private Toolbar toolbar;

    private String mixPanelProjectToken = "537b3567fe348d6c5dfa0d6e2ae688b3";
    private MixpanelAPI mixpanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.security_activity);

        mixpanel = MixpanelAPI.getInstance(Security.this, mixPanelProjectToken);
        mixpanel.track("Modules - Security Page");

        fragmentManager = getSupportFragmentManager();
        myAdapter = new SecurityTabsAdapter(getSupportFragmentManager(), this);
        myViewPager = (ViewPager) findViewById(R.id.pager);
        myViewPager.setAdapter(myAdapter);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setTextColor(Color.WHITE);
        tabs.setDividerColor(Color.TRANSPARENT);
        tabs.setViewPager(myViewPager);

        initToolbar();

        Intent intent = getIntent();
        String type = intent.getStringExtra("Security_Type");
        if (type != null){
            if (type.equals("report")){
                Intent mIntent = new Intent(Security.this, NewReport.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(mIntent,
                            ActivityOptions.makeSceneTransitionAnimation(Security.this).toBundle());
                }else{
                    startActivity(mIntent);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.stay_anim);
                }
            }
        }
    }

    private void initToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle(getResources().getString(R.string.security_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
