package com.communer.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.communer.Adapters.SearchCommunityAdapter;
import com.communer.Application.AppController;
import com.communer.Models.CommunityObj;
import com.communer.Models.CommunitySearchObj;
import com.communer.Parsers.CommunityParser;
import com.communer.Parsers.SearchCommunityParser;
import com.communer.R;
import com.communer.Utils.AppUser;
import com.communer.Utils.CircleTransform;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by יובל on 21/09/2015.
 */
public class SearchCommunityMenu extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView comList;
    private SearchCommunityAdapter adapter;

    private EditText search_input;
    private ProgressBar search_pb;
    private LinearLayout bottomLeftCntnr,bottomContainer;
    private TextView bottomRightFirstLine,bottomRightSecondLine;

    private ImageView communityPic;
    private TextView comCity,comName;

    private ArrayList<CommunitySearchObj> communities;

    private final static String TAG = "SmartCommunity_TAG";
    private final static String VOLLEY_TAG = "VOLLEY_TAG";
    private int lastCommunityPosition = -1;

    private ImageButton connectButton, applyMember;
    private ImageView divider;
    private ProgressBar guestPB, memberPB;

    private String joinningMethod;

    private BroadcastReceiver parserReceiver;

    private String mixPanelProjectToken = "537b3567fe348d6c5dfa0d6e2ae688b3";
    private MixpanelAPI mixpanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_community);
        setupWindowAnimations();

        mixpanel = MixpanelAPI.getInstance(SearchCommunityMenu.this, mixPanelProjectToken);
        mixpanel.track("Search Community - Came from menu");

        initToolbar();

        comList = (ListView)findViewById(R.id.search_communities_list);
        search_input = (EditText)findViewById(R.id.search_community_input);
        search_pb = (ProgressBar)findViewById(R.id.search_community_pb);

        initReceiver();

        search_input.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String textToSearch = search_input.getText().toString();
                    if (!textToSearch.equals("") && textToSearch.length() > 0){
                        getCommunities(textToSearch);
                        hideSoftKeyboard();
                        lastCommunityPosition = -1;
                        bottomContainer.setVisibility(View.GONE);
                        return true;
                    }else{
                        Toast.makeText(SearchCommunityMenu.this,"Please enter a text to search",Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        connectButton = (ImageButton)findViewById(R.id.connect_as_guest_btn);
        applyMember = (ImageButton)findViewById(R.id.apply_as_member_btn);
        divider = (ImageView)findViewById(R.id.community_search_divider);
        guestPB = (ProgressBar)findViewById(R.id.as_a_guest_pb);
        memberPB = (ProgressBar)findViewById(R.id.apply_member_pb);

        bottomContainer = (LinearLayout)findViewById(R.id.search_community_bottom_cntnr);
        bottomLeftCntnr = (LinearLayout)findViewById(R.id.chosen_community_left_cntnr);
        bottomRightFirstLine = (TextView)findViewById(R.id.chosen_community_first_def);
        bottomRightSecondLine = (TextView)findViewById(R.id.chosen_community_second_def);
        bottomLeftCntnr.setVisibility(View.INVISIBLE);
        bottomRightFirstLine.setVisibility(View.INVISIBLE);
        bottomRightSecondLine.setVisibility(View.INVISIBLE);

        communityPic = (ImageView)findViewById(R.id.chosen_community_pic);
        comCity = (TextView)findViewById(R.id.chosen_community_city);
//        comCountry = (TextView)findViewById(R.id.chosen_community_country);
        comName = (TextView)findViewById(R.id.chosen_community_name);

        comList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (lastCommunityPosition != -1){
                    View prevView = comList.getChildAt(lastCommunityPosition);
                    if (prevView != null)
                        prevView.setBackgroundColor(ContextCompat.getColor(SearchCommunityMenu.this, R.color.white));
                }

                lastCommunityPosition = position;
                view.setBackgroundColor(ContextCompat.getColor(SearchCommunityMenu.this, R.color.Gray));
                handleBottomData(communities.get(position), view);
            }
        });

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastCommunityPosition != -1){
                    if (!checkCommunityExistance(communities.get(lastCommunityPosition)))
                        joinRequest(communities.get(lastCommunityPosition), "asGuest");
                    else
                        Toast.makeText(SearchCommunityMenu.this, "You are already connected with this community", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SearchCommunityMenu.this, "You have to pick a community first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        applyMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastCommunityPosition != -1){
                    if (!checkCommunityExistance(communities.get(lastCommunityPosition)))
                        joinRequest(communities.get(lastCommunityPosition), "asMember");
                    else
                        Toast.makeText(SearchCommunityMenu.this, "You are already connected with this community", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SearchCommunityMenu.this, "You have to pick a community first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle(getResources().getString(R.string.find_community_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void handleBottomData(CommunitySearchObj communityObj, View view){
        if(bottomContainer.getVisibility() == View.GONE){
            growingLayoutAnimation();
        }

        bottomLeftCntnr.setVisibility(View.VISIBLE);
//        bottomRightFirstLine.setVisibility(View.VISIBLE);
        bottomRightSecondLine.setVisibility(View.VISIBLE);
        divider.setVisibility(View.VISIBLE);

        String imageURL = communityObj.getImageURL();
        if (imageURL != null){
            if (imageURL.equals(""))
                imageURL ="badUrl.com";

            Picasso.with(SearchCommunityMenu.this)
                    .load(imageURL)
                    .transform(new CircleTransform())
                    .into(communityPic);
        }else {
            Picasso.with(SearchCommunityMenu.this)
                    .load(R.drawable.placeholeder_lowres)
                    .transform(new CircleTransform())
                    .into(communityPic);
        }

        comCity.setText(communityObj.getLocation().getTitle());
//        comCountry.setText(communityObj.getLocation().getTitle());
        comName.setText(communityObj.getName());

        bottomRightSecondLine.setText(communityObj.getType());


    }

    private void handleReturningFromParsing() {
        if (joinningMethod.equals("asGuest"))
            combineFade(guestPB,connectButton);
        else
            combineFade(memberPB,applyMember);

        Intent returnIntent = new Intent();
        returnIntent.putExtra("community_added", true);
        returnIntent.putExtra("mainInit", false);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private boolean checkCommunityExistance(CommunitySearchObj communitySearchObj) {
        AppUser appUserInstance = AppUser.getInstance();
        ArrayList<CommunityObj> allCommunities = appUserInstance.getUserCommunities();

        if (allCommunities != null){
            for (int i=0; i<allCommunities.size(); i++){
                String joinningComID = communitySearchObj.getId();
                String existingComID = allCommunities.get(i).getCommunityID();
                if (joinningComID.equals(existingComID)){
                    return true;
                }
            }
        }
        return false;
    }

    private void growingLayoutAnimation() {
        bottomContainer.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.growing_layout);
        animation.setDuration(500);
        bottomContainer.setAnimation(animation);
        bottomContainer.animate();
        animation.start();
    }

    private void getCommunities(String communityToSearch){
        AppUser appUserInstance = AppUser.getInstance();
        String userHash = appUserInstance.getUserHash();
        String url = appUserInstance.getBaseUrl() + userHash + "/com.solidPeak.smartcom.requests.application.getSrearchResultByQuery.htm?querry="+communityToSearch;

        url = url.replace(" ", "%20");

        String tag_json_obj = "search_community_tag";
        search_pb.setVisibility(View.VISIBLE);
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>(){

            @Override
            public void onResponse(JSONArray jsonArray) {
                search_pb.setVisibility(View.GONE);

                communities = SearchCommunityParser.parseCommunities(jsonArray);
                adapter = new SearchCommunityAdapter(communities,SearchCommunityMenu.this);
                comList.setAdapter(adapter);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                search_pb.setVisibility(View.GONE);
                Log.d(TAG, "Error getting communities");
            }
        });

        jsonArrReq.setShouldCache(false);
        jsonArrReq.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonArrReq, tag_json_obj);
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void joinRequest(final CommunitySearchObj community, final String method){
        disableButtons();
        boolean alreadyExist = false;
        joinningMethod = method;

        AppUser appUserInstance = AppUser.getInstance();
        ArrayList<CommunityObj> tempCommuinities = appUserInstance.getUserCommunities();

        for (int i=0; i<tempCommuinities.size(); i++){
            if (tempCommuinities.get(i).getCommunityID().equals(community.getId()))
                alreadyExist = true;
        }

        if (!alreadyExist){
            final String userHash = appUserInstance.getUserHash();
            String tempURL = appUserInstance.getBaseUrl() + userHash + "/com.solidPeak.smartcom.requests.application.SendJoinRequestMod.htm?";
            String tag_json_obj = "join_community";
            JSONObject obj = new JSONObject();
            try {
                obj.put("communityId", community.getId());
                if (method.equals("asGuest")){
                    combineFade(connectButton,guestPB);
                    obj.put("asGuest",true);
                }else {
                    combineFade(applyMember, memberPB);
                    obj.put("asGuest", false);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            String joinURL = tempURL + "join=" + obj.toString();
            joinURL = joinURL.replace(" ","%20");

            StringRequest stringReq = new StringRequest(Request.Method.GET, joinURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    Log.d(VOLLEY_TAG, "Joined community successfuly");
                    CommunityParser communityParser = new CommunityParser(SearchCommunityMenu.this);
                    communityParser.parseData(s);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    if (method.equals("asGuest"))
                        combineFade(guestPB,connectButton);
                    else
                        combineFade(memberPB,applyMember);

                    Toast.makeText(SearchCommunityMenu.this, "Error joinning community, try again...",Toast.LENGTH_SHORT).show();
                    Log.d(VOLLEY_TAG, "error joinning community");

                    enableButtons();
                }
            });

            stringReq.setShouldCache(false);
            stringReq.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(stringReq, tag_json_obj);
        }else{
            Toast.makeText(SearchCommunityMenu.this,"Already registered to " + community.getName() + " community", Toast.LENGTH_SHORT).show();
        }
    }

    private void combineFade(final View fadeOutView, final View fadeInView){
        fadeOutView.animate()
                .alpha(0f)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fadeOutView.setVisibility(View.INVISIBLE);
                        fadeInView.setVisibility(View.VISIBLE);
                        fadeInView.animate()
                                .alpha(1f)
                                .setDuration(300)
                                .setListener(null);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(parserReceiver);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        hideSoftKeyboard();
        super.onStop();
    }

    private void disableButtons() {
        connectButton.setClickable(false);
        connectButton.setEnabled(false);

        applyMember.setClickable(false);
        applyMember.setEnabled(false);

        search_input.setFocusable(false);
    }

    private void enableButtons() {
        connectButton.setClickable(true);
        connectButton.setEnabled(true);

        applyMember.setClickable(true);
        applyMember.setEnabled(true);

        search_input.setFocusable(true);
    }

    private void setupWindowAnimations(){
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = (Fade) TransitionInflater.from(SearchCommunityMenu.this).inflateTransition(R.transition.activity_fade);
            getWindow().setEnterTransition(fade);
        }
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("Community_Parsing_Done");
        parserReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra("Status");
                if (status.equals("Success")){
                    handleReturningFromParsing();
                }
            }
        };
        registerReceiver(parserReceiver, filter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
