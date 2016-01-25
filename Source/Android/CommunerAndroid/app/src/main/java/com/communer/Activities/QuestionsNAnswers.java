package com.communer.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.astuetz.PagerSlidingTabStrip;
import com.communer.Adapters.CommentsAdapters;
import com.communer.Adapters.PrivatePublicAdapter;
import com.communer.Application.AppController;
import com.communer.Fragments.QAPrivate;
import com.communer.Fragments.QAPublic;
import com.communer.Models.CommentItem;
import com.communer.Models.CommunityMember;
import com.communer.Models.Post;
import com.communer.R;
import com.communer.Utils.AppUser;
import com.google.gson.Gson;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ���� on 09/08/2015.
 */
public class QuestionsNAnswers extends AppCompatActivity {

    private Toolbar toolbar;
    private PrivatePublicAdapter myAdapter;
    private ViewPager myViewPager;
    private PagerSlidingTabStrip tabs;

    private FloatingActionButton fab;

    private final static int ADD_POST_INT = 55;

    private String mixPanelProjectToken = "537b3567fe348d6c5dfa0d6e2ae688b3";
    private MixpanelAPI mixpanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questionsnanswers);

        mixpanel = MixpanelAPI.getInstance(QuestionsNAnswers.this, mixPanelProjectToken);
        mixpanel.track("Modules - Questions and Answers Page");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle(getResources().getString(R.string.questions_answers_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        myAdapter = new PrivatePublicAdapter(getSupportFragmentManager(), QuestionsNAnswers.this);
        myViewPager = (ViewPager) findViewById(R.id.pager);
        myViewPager.setAdapter(myAdapter);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setTextColor(Color.WHITE);
        tabs.setDividerColor(Color.TRANSPARENT);
        tabs.setViewPager(myViewPager);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(QuestionsNAnswers.this, AskQuestionActivity.class);
                startActivityForResult(mIntent, ADD_POST_INT);
            }
        });

//        openFirstComments();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (ADD_POST_INT) : {
                if (resultCode == Activity.RESULT_OK) {
                    String postTitle = data.getStringExtra("Title");
                    String postContent = data.getStringExtra("Content");
                    boolean isPublic = data.getBooleanExtra("isPublic", true);

                    AppUser appUserInstance = AppUser.getInstance();
                    CommunityMember userAsMmbr = appUserInstance.getUserAsMember();
                    long time = System.currentTimeMillis();
                    Post item = new Post("123",postTitle,"",userAsMmbr,time,postContent,null);

                    if (isPublic){
                        QAPublic qa_public = (QAPublic)myAdapter.getRegisteredFragment(1);
                        qa_public.addPublicPost(item);
                    }else{
                        QAPrivate qa_private = (QAPrivate)myAdapter.getRegisteredFragment(0);
                        qa_private.addPrivatePost(item);
                    }
                }
                break;
            }
        }
    }

    public void showCommentsDialog(final Post post){
        final MaterialDialog dialog = new MaterialDialog.Builder(QuestionsNAnswers.this)
                .customView(R.layout.comments_dialog, false)
                .show();

        final View view = dialog.getCustomView();
        final RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.comments_recycler);

        final ArrayList<CommentItem> comments = post.getComments();

        AppUser appUserInstance = AppUser.getInstance();
        final String userImg = appUserInstance.getUserImageUrl();
        final CommunityMember userAsMmbr = appUserInstance.getUserAsMember();
        final CommentsAdapters myAdapter = new CommentsAdapters(comments,QuestionsNAnswers.this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final EditText commentText = (EditText)view.findViewById(R.id.comments_edittext);
        ImageButton cameraBtn = (ImageButton)view.findViewById(R.id.comments_camera_button);
        ImageButton backBtn = (ImageButton)view.findViewById(R.id.comments_back_button);
        ImageButton sendBtn = (ImageButton)view.findViewById(R.id.comment_send);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postID = post.getPostID();
                if (!postID.equals("")){
                    long time = System.currentTimeMillis();
                    String description = commentText.getText().toString();

                    comments.add(new CommentItem(postID, userImg, time, description, userAsMmbr));
                    myAdapter.notifyItemInserted(comments.size() - 1);
                    recyclerView.smoothScrollToPosition(comments.size() - 1);
                    commentText.setText("");

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    addComment(description,postID);
                }else{
                    Toast.makeText(QuestionsNAnswers.this, "Post ID is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void addComment(String description, String postID){
        AppUser appUserInstance = AppUser.getInstance();

        String userHash = appUserInstance.getUserHash();
        String url = appUserInstance.getBaseUrl() + userHash + "/com.solidPeak.smartcom.requests.application.SendAnswer.htm?";

        JSONObject answer = new JSONObject();

        CommunityMember mmbr = appUserInstance.getUserAsMember();
        Gson gson = new Gson();
        String mmbrString = gson.toJson(mmbr);

        JSONArray imageData = new JSONArray();

        try {
            answer.put("description",description);
            answer.put("postID",postID);
            answer.put("member",mmbrString);
            answer.put("imageData",imageData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String sendCommentUrl = url + "answer=" + answer.toString();
        sendCommentUrl = sendCommentUrl.replace(" ","%20");
        String tag_json_obj = "send_answer_call";

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, sendCommentUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String status = "";
                try {
                    status = response.getString("status");
                    if (status.equals("success")){
                        Log.d("Comment", "Sent Successfuly");
                    }else{
                        Toast.makeText(QuestionsNAnswers.this, "Error sending comment", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(QuestionsNAnswers.this, "Error sending comment", Toast.LENGTH_SHORT).show();
            }
        });

        jsonRequest.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_json_obj);
    }

    private void openFirstComments() {
        Intent intent = getIntent();
        String camefrom = intent.getStringExtra("came_from");
        if (camefrom != null){
            FragmentStatePagerAdapter a = (FragmentStatePagerAdapter)myViewPager.getAdapter();
            QAPublic pub = (QAPublic) a.instantiateItem(myViewPager, 1);
            if (pub!=null){
                showCommentsDialog(pub.questionsArray.get(0));
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
