package com.communer.Fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.communer.Activities.SecurityReportPage;
import com.communer.Adapters.SecurityReportAdapter;
import com.communer.Models.SecurityEvent;
import com.communer.R;
import com.communer.Utils.AppUser;

import java.util.ArrayList;

/**
 * Created by יובל on 02/09/2015.
 */
public class SecurityReport extends Fragment {

    private ListView reportsList;
    private SecurityReportAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.security_report, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppUser appUserInstance = AppUser.getInstance();
        final ArrayList<SecurityEvent> securityEvents = appUserInstance.getCurrentCommunity().getSecurityEvents();

        reportsList = (ListView)view.findViewById(R.id.security_reports_list);
        mAdapter = new SecurityReportAdapter(securityEvents,getActivity());
        reportsList.setAdapter(mAdapter);

        reportsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SecurityEvent securityEvent = securityEvents.get(position);

                Intent mIntent = new Intent(getActivity(), SecurityReportPage.class);
                mIntent.putExtra("securityEvent",securityEvent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getActivity().startActivity(mIntent,
                            ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                }else{
                    getActivity().startActivity(mIntent);
                    getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.stay_anim);
                }
            }
        });
    }
}
