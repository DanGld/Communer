package com.communer.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.communer.Models.ReportItem;
import com.communer.Models.SecurityEvent;
import com.communer.R;

/**
 * Created by יובל on 08/11/2015.
 */
public class SecurityReportPage extends AppCompatActivity {

    private SecurityEvent securityEvent;
    private TextView report_type, report_data, report_description, police_reported;

    private Toolbar toolbar;
    private LinearLayout policeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_page);

        initToolbar();
        handleExtras();
        setViewsReference();
        editData();
    }

    private void initToolbar(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle(getResources().getString(R.string.report_page_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void editData() {
        ReportItem report = securityEvent.getReport();

        String reporter = securityEvent.getReporterName();
        String description = report.getDescription();

        String type = securityEvent.getReport().getType();
        if (type.equals("null"))
            report_type.setText("No Type");
        else
            report_type.setText(type);

        if (reporter.equalsIgnoreCase("null"))
            reporter = "Annonymously Person";

        report_data.setText("Has been reported by " + reporter + " at " + securityEvent.getReport().getLocationObj().getTitle());
        report_description.setText(description);

        if (securityEvent.getReport().isPoliceReport()){
            police_reported.setText(reporter);
        }else{
            policeLayout.setVisibility(View.GONE);
        }
    }

    private void handleExtras(){
        Bundle data = getIntent().getExtras();
        securityEvent = data.getParcelable("securityEvent");
    }

    private void setViewsReference() {
        report_type = (TextView)findViewById(R.id.report_type);
        report_data = (TextView)findViewById(R.id.report_data);
        report_description = (TextView)findViewById(R.id.report_description);
        police_reported = (TextView)findViewById(R.id.report_reported_by);
        policeLayout = (LinearLayout)findViewById(R.id.report_page_police_cntnr);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
