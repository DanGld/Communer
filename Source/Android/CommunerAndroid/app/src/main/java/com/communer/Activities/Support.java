package com.communer.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.communer.Application.AppController;
import com.communer.R;
import com.communer.Utils.AppUser;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Support extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    Spinner topicSpinner;
    EditText descriptionInput, titleInput;
    ArrayList<String> topicList;
    TextView websiteUrl;

    ProgressView toolbarPB;

    private String mixPanelProjectToken = "537b3567fe348d6c5dfa0d6e2ae688b3";
    private MixpanelAPI mixpanel;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        mixpanel = MixpanelAPI.getInstance(Support.this, mixPanelProjectToken);
        mixpanel.track("Modules - Support Page");

        initToolbar();

        ImageButton sendBtn = (ImageButton) toolbar.findViewById(R.id.toolbarSendBtn);
        toolbarPB = (ProgressView)toolbar.findViewById(R.id.toolbar_login_pb);
        sendBtn.setOnClickListener(this);

        topicList = new ArrayList<String>();
        topicList.add("Report a bug/issue");
        topicList.add("Suggest an improvement");
        topicList.add("Contact my community");

        ArrayAdapter<String> topicAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,topicList);
        topicSpinner = (Spinner) findViewById(R.id.topic_spinner);
        topicSpinner.setAdapter(topicAdapter);

        descriptionInput = (EditText) findViewById(R.id.description_input);

        titleInput = (EditText) findViewById(R.id.title_input);
        websiteUrl = (TextView) findViewById(R.id.website_url);
        websiteUrl.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.support_toolbar);
        toolbar.setTitle(getResources().getString(R.string.support_title));
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbarSendBtn:
                toolbarPB.setVisibility(View.VISIBLE);

                String title = titleInput.getText().toString();
                String description = descriptionInput.getText().toString();
                String topic = topicList.get(topicSpinner.getSelectedItemPosition());

                boolean toSave = true;
                String message = "";

                if (title.equals("")){
                    toSave = false;
                    message = "Please enter a title";
                }

                if (description.equals("")){
                    toSave = false;
                    message = "Please enter description";
                }

                if (toSave == false){
                    Toast.makeText(getApplication(),message,Toast.LENGTH_SHORT).show();
                    toolbarPB.setVisibility(View.GONE);
                }else{
                    sendSupportRequest();
                }
                break;
        }
    }

    private void sendSupportRequest(){
        AppUser appUserInstance = AppUser.getInstance();
        String userHash = appUserInstance.getUserHash();
        String url = appUserInstance.getBaseUrl() + userHash + "/com.solidPeak.smartcom.requests.application.SendSupportMessage.htm?";

        JSONObject feedbackObj = new JSONObject();
        try {
            feedbackObj.put("type",topicSpinner.getSelectedItem().toString());
            feedbackObj.put("communityID",appUserInstance.getCurrentCommunity().getCommunityID());
            feedbackObj.put("topic","Handlers");
            feedbackObj.put("title",titleInput.getText().toString());
            feedbackObj.put("description",descriptionInput.getText().toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }

        String query = null;
        try{
            query = URLEncoder.encode(feedbackObj.toString(), "utf-8");
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }

        url = url + "feedback=" + query;
        String tag_json_obj = "send_support_call";

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    toolbarPB.setVisibility(View.GONE);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")){
                        Toast.makeText(Support.this, "Support request sent succesfully", Toast.LENGTH_SHORT).show();
                        NavUtils.navigateUpFromSameTask(Support.this);
                    }else{
                        Toast.makeText(Support.this, "Couldn't send support request", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(Support.this, "Couldn't send support request", Toast.LENGTH_SHORT).show();
                toolbarPB.setVisibility(View.GONE);
            }
        });

        jsonRequest.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_json_obj);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
