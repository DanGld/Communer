package com.communer.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.communer.Adapters.WorkerCommentsAdapter;
import com.communer.Application.AppController;
import com.communer.Models.CommunityMember;
import com.communer.Models.ServiceComment;
import com.communer.R;
import com.communer.Utils.AppUser;
import com.communer.Utils.CircleTransform;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by יובל on 30/08/2015.
 */
public class WorkerPage extends AppCompatActivity  implements OnMapReadyCallback {

    private Toolbar toolbar;
    private FloatingActionButton fab;

    private String name, imageURL, locationText, phone, rating, coords, workerID;
    private ArrayList<ServiceComment> comments;

    private TextView worker_name, worker_location, worker_phone, worker_rating, worker_comments_count;
    private ImageView worker_pic;
    private ImageButton phone_btn;

    private RatingBar ratingBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setWindowAnimations();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.worker_page);

        initToolbar();
        handleExtras();
        setViewsReference();

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.workerCommentsRecycler);
        fab = (FloatingActionButton)findViewById(R.id.fab);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final WorkerCommentsAdapter mAdapter = new WorkerCommentsAdapter(comments,WorkerPage.this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if (!isFinishing()) {
                    MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(WorkerPage.this);
                }
            }
        }, 1000);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final OvershootInterpolator interpolator = new OvershootInterpolator();
                ViewCompat.animate(fab).rotation(360f).withLayer().setDuration(600).setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {

                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        fab.setRotation(0);
                        openRateDialog(mAdapter);
                    }

                    @Override
                    public void onAnimationCancel(View view) {

                    }
                }).setInterpolator(interpolator).start();
            }
        });

        phone_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
            }
        });
    }

    private void setWindowAnimations(){
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            TransitionInflater transInflater = TransitionInflater.from(WorkerPage.this);
            Transition enterTransitionSet = transInflater.inflateTransition(R.transition.enter_right_fade_transition);

            enterTransitionSet.excludeTarget(android.R.id.statusBarBackground, true);
            enterTransitionSet.excludeTarget(android.R.id.navigationBarBackground, true);
            getWindow().setEnterTransition(enterTransitionSet);

            Transition exitTransitionSet = transInflater.inflateTransition(R.transition.exit_fade_left_transition);

            exitTransitionSet.excludeTarget(android.R.id.statusBarBackground, true);
            exitTransitionSet.excludeTarget(android.R.id.navigationBarBackground, true);
            getWindow().setExitTransition(exitTransitionSet);
            getWindow().setReturnTransition(exitTransitionSet);
        }*/
    }

    private void openRateDialog(final WorkerCommentsAdapter mAdapter){
        final AppUser appUserInstance = AppUser.getInstance();
        MaterialDialog dialog = new MaterialDialog.Builder(WorkerPage.this)
                .customView(R.layout.worker_rate_dialog, false)
                .positiveText("Ok")
                .negativeText("Cancel")
                .negativeColor(ContextCompat.getColor(WorkerPage.this, R.color.AppOrange))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        View view = dialog.getCustomView();
                        ratingBar = (RatingBar) view.findViewById(R.id.worker_stars_bar);
                        EditText commentText = (EditText) view.findViewById(R.id.worker_comment_edittext);

                        if (commentText.length() > 0) {
                            String username = appUserInstance.getUserFirstName() + " " + appUserInstance.getUserLastName();
                            String userImage = appUserInstance.getUserImageUrl();
                            String comment = commentText.getText().toString();
                            double rate = ratingBar.getRating();
                            ServiceComment serviceComment = new ServiceComment(userImage, username, rate, comment);

                            sendComment(serviceComment);

                            comments.add(serviceComment);
                            mAdapter.notifyItemInserted(comments.size() - 1);
                        }else{
                            Toast.makeText(WorkerPage.this,"Empty comment",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }

    private void initToolbar(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle(getResources().getString(R.string.category_worker_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void handleExtras() {
        Intent extras = getIntent();
        workerID = extras.getStringExtra("workerID");
        name = extras.getStringExtra("name");
        imageURL = extras.getStringExtra("image");
        locationText = extras.getStringExtra("location_title");
        phone = extras.getStringExtra("phone");
        rating = String.valueOf(extras.getDoubleExtra("rating", 0));
        coords = extras.getStringExtra("location_coords");
        comments = extras.getParcelableArrayListExtra("comments");

        if (comments == null) {
            comments = new ArrayList<ServiceComment>();
        }
    }

    private void setViewsReference() {
        worker_name = (TextView)findViewById(R.id.worker_name);
        worker_location = (TextView)findViewById(R.id.worker_location);
        worker_phone = (TextView)findViewById(R.id.worker_phone);
        worker_rating = (TextView)findViewById(R.id.worker_rating);
        worker_comments_count = (TextView)findViewById(R.id.worker_votes_count);
        worker_pic = (ImageView)findViewById(R.id.worker_image);
        phone_btn = (ImageButton)findViewById(R.id.worker_phone_btn);

        worker_name.setText(name);
        worker_location.setText(locationText);
        worker_phone.setText(phone);
        worker_rating.setText(rating);
        worker_comments_count.setText(String.valueOf(comments.size()) + " Votes");

        if (!imageURL.equals("")){
            Picasso.with(WorkerPage.this)
                    .load(imageURL)
                    .error(R.drawable.placeholeder_lowres)
                    .transform(new CircleTransform())
                    .into(worker_pic);
        }else{
            Picasso.with(WorkerPage.this)
                    .load(R.drawable.placeholeder_lowres)
                    .transform(new CircleTransform())
                    .into(worker_pic);
        }
//        ratingBar.setRating(Integer.valueOf(rating));
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Double mapLat = null;
        Double mapLong = null;

        boolean foundCoords = false;
        if (coords != null){
            if (!coords.equals("")){
                coords = coords.replace(" ", "");
                String [] coordsSplit = coords.split(",");

                if (coordsSplit.length == 2){
                    foundCoords = true;
                    String latText = coordsSplit[0];
                    String longText = coordsSplit[1];

                    mapLat = Double.parseDouble(latText);
                    mapLong = Double.parseDouble(longText);
                }
            }
        }

        if (!foundCoords){
            mapLat = Double.parseDouble("32.069965");
            mapLong = Double.parseDouble("34.777464");
        }

        LatLng mapLocation = new LatLng(mapLat, mapLong);
        googleMap.addMarker(new MarkerOptions().position(mapLocation));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mapLocation, 13), 2000, null);
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
    }

    private void sendComment(ServiceComment comment){
        AppUser appUserInstance = AppUser.getInstance();
        String userHash = appUserInstance.getUserHash();
        String url = appUserInstance.getBaseUrl() + userHash + "/com.solidPeak.smartcom.requests.application.SendGuideRate.htm?";

        JSONObject ratingObj = new JSONObject();
        try {
            ratingObj.put("rate",String.valueOf(comment.getRate()));
            ratingObj.put("description",comment.getContent());
            ratingObj.put("id",workerID);

            CommunityMember member = appUserInstance.getUserAsMember();
            ratingObj.put("member",member.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }

        String sendRateURL = url + "rating=" + ratingObj.toString();
        sendRateURL = sendRateURL.replace(" ","%20");
        String tag_json_obj = "send_rate_call";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, sendRateURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String s) {
                if (s.equals("success")){

                }else{
                    Toast.makeText(WorkerPage.this, "Couldn't send rating to server", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(WorkerPage.this, "Couldn't send rating to server", Toast.LENGTH_SHORT).show();
            }
        });

        stringRequest.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        phone_btn.animate()
                .alpha(0f)
                .setDuration(150);
        supportFinishAfterTransition();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                phone_btn.animate()
                    .alpha(0f)
                    .setDuration(150);

                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
