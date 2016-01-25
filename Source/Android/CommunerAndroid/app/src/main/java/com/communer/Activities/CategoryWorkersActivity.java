package com.communer.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.communer.Adapters.CategoryWorkersAdapter;
import com.communer.Application.AppController;
import com.communer.Models.LocationObj;
import com.communer.Models.ServiceComment;
import com.communer.Models.ServiceContact;
import com.communer.R;
import com.communer.Utils.AppUser;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingLeftInAnimationAdapter;
import com.rey.material.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ���� on 27/08/2015.
 */
public class CategoryWorkersActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private ListView workersList;
    private CategoryWorkersAdapter workersAdapter;
    private ArrayList<ServiceContact> workersData;

    private String category_name;

    private TextView no_results;
    private LinearLayout filters_container;
    private ProgressBar search_pb;
    private Spinner category_spinner, location_spinner, rate_spinner, price_spinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setWindowAnimations();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_workers);

        initToolbar();

        no_results = (TextView)findViewById(R.id.category_no_results);

        workersList = (ListView)findViewById(R.id.category_workers_list);
        workersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView workerImage = (ImageView)view.findViewById(R.id.worker_image);
                TextView workerName = (TextView)view.findViewById(R.id.worker_name);
                TextView workerLocation = (TextView)view.findViewById(R.id.worker_location);
                TextView workerPhone = (TextView)view.findViewById(R.id.worker_phone);
                TextView workerRate = (TextView)view.findViewById(R.id.worker_rating);
                LinearLayout workerContiner = (LinearLayout)view.findViewById(R.id.category_worker_row_container);

/*                workerRate.animate()
                        .alpha(0)
                        .setDuration(150);*/

                ServiceContact item = workersData.get(position);
                Intent mIntent = new Intent(CategoryWorkersActivity.this, WorkerPage.class);
                mIntent.putExtra("workerID", item.getServiceContactID());
                mIntent.putExtra("name", item.getName());
                mIntent.putExtra("image", item.getImageUrl());
                mIntent.putExtra("phone", item.getPhoneNumber());
                mIntent.putExtra("location_title", item.getLocationObj().getTitle());
                mIntent.putExtra("location_coords", item.getLocationObj().getCords());
                mIntent.putExtra("rating", item.getRate());
                mIntent.putParcelableArrayListExtra("comments", item.getComments());

                Pair<View, String> p1 = Pair.create((View)workerImage,"guide_profile_transition");
                Pair<View, String> p2 = Pair.create((View)workerName,"guide_name_transition");
                Pair<View, String> p3 = Pair.create((View)workerLocation,"guide_location_transition");
                Pair<View, String> p4 = Pair.create((View)workerPhone,"guide_phone_transition");
                Pair<View, String> p5 = Pair.create((View)workerContiner,"guide_worker_container_transition");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(CategoryWorkersActivity.this, p1, p2, p3, p4,p5);
                    startActivity(mIntent,options.toBundle());
                }else{
                    startActivity(mIntent);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.stay_anim);
                }
            }
        });

        search_pb = (ProgressBar)findViewById(R.id.category_workers_pb);

        Intent data = getIntent();
        category_name = data.getStringExtra("category_name");

        filters_container = (LinearLayout)findViewById(R.id.search_filters_container);

/*        if (category_name.equals("Events") || category_name.equals("events")){
            filters_container.setVisibility(View.VISIBLE);
            setupFiltersLayout();
        }else{
            filters_container.setVisibility(View.GONE);
        }*/

        getWorkersList();
    }

    private void initToolbar(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle(getResources().getString(R.string.category_search_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void getWorkersList(){
        search_pb.setVisibility(View.VISIBLE);
        AppUser appUserInstance = AppUser.getInstance();
        String userHash = appUserInstance.getUserHash();
        String baseUrl = appUserInstance.getBaseUrl() + userHash + "/com.solidPeak.smartcom.requests.application.getGuidesBulk.htm?";

        String category = category_name;
        int startResults = 0;
        int maxResults = 20;
        String prefix = "";
        String rate = "";
        String price = "";

        JSONObject obj = new JSONObject();
        try{
            obj.put("title","gfdgdf");
            obj.put("coords","dfghjk");
        }catch(JSONException e){
            e.printStackTrace();
        }
        String location = obj.toString();

        String url = baseUrl + "category=" + category + "&prefix=" + prefix + "&startResult=" + startResults + "&numResults=" + maxResults + "&rate" + rate + "&price=" + price + "&location=" + location;
        url = url.replace(" ","%20");
        String tag_json_obj = "workers_call";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        search_pb.setVisibility(View.GONE);
                        Log.d("Volley", response.toString());
                        workersData = parseWorkers(response);
                        workersAdapter = new CategoryWorkersAdapter(workersData,CategoryWorkersActivity.this);
                        AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(workersAdapter);
                        SwingLeftInAnimationAdapter swingLeftInAnimationAdapter = new SwingLeftInAnimationAdapter(animationAdapter);
                        swingLeftInAnimationAdapter.setAbsListView(workersList);
                        workersList.setAdapter(swingLeftInAnimationAdapter);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                search_pb.setVisibility(View.GONE);
                VolleyLog.d("Volley", "Error: " + error.getMessage());
                Toast.makeText(CategoryWorkersActivity.this,"Error in getting workers data",Toast.LENGTH_SHORT).show();
                no_results.setVisibility(View.VISIBLE);
            }
        });

        jsonObjReq.setShouldCache(false);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private ArrayList<ServiceContact> parseWorkers(JSONObject result) {
        ArrayList<ServiceContact> categoryWorkers = new ArrayList<ServiceContact>();
        try {
            JSONArray contacts = result.getJSONArray("results");

            if (contacts.length() > 0){
                for (int i=0; i<contacts.length(); i++){
                    JSONObject singleContact = contacts.getJSONObject(i);
                    String image_url = singleContact.getString("imageURL");
                    String name = singleContact.getString("name");
                    String id = singleContact.getString("id");

                    JSONObject location = singleContact.getJSONObject("location");
                    String locTitle = location.getString("title");
                    String locCoords = location.getString("coords");
                    LocationObj finalLoc = new LocationObj(locTitle,locCoords);

                    String phone = singleContact.getString("phone");
                    Double rating = singleContact.getDouble("rating");
                    Double votes = singleContact.getDouble("votes");

                    JSONArray comments = null;
                    ArrayList<ServiceComment> workerComments = new ArrayList<ServiceComment>();
                    if (!singleContact.isNull("comments")){
                        comments = singleContact.getJSONArray("comments");
                        for (int j=0; j<comments.length(); j++){
                            ////TODO add comments parser
                        }
                    }

                    categoryWorkers.add(new ServiceContact(id,image_url,name,finalLoc,phone,rating, votes, workerComments));
                }
            }else{
                no_results.setVisibility(View.VISIBLE);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return categoryWorkers;
    }

    private void setupFiltersLayout(){
        category_spinner = (Spinner)findViewById(R.id.search_filters_category);
        location_spinner = (Spinner)findViewById(R.id.search_filters_location);
        rate_spinner = (Spinner)findViewById(R.id.search_filters_rate);
        price_spinner = (Spinner)findViewById(R.id.search_filters_price);

        String[] items = new String[20];
        for(int i = 0; i < items.length; i++)
            items[i] = "Item " + String.valueOf(i + 1);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CategoryWorkersActivity.this, R.layout.row_spn, items);
        adapter.setDropDownViewResource(R.layout.row_spn);

        category_spinner.setAdapter(adapter);
        location_spinner.setAdapter(adapter);
        rate_spinner.setAdapter(adapter);
        price_spinner.setAdapter(adapter);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setWindowAnimations(){
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            TransitionInflater inflater = TransitionInflater.from(CategoryWorkersActivity.this);
            Transition mTransitionSet = inflater.inflateTransition(R.transition.enter_right_fade_transition);

            mTransitionSet.excludeTarget(android.R.id.statusBarBackground, true);
            mTransitionSet.excludeTarget(android.R.id.navigationBarBackground, true);

            getWindow().setEnterTransition(mTransitionSet);
            getWindow().setExitTransition(mTransitionSet);
        }*/
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
