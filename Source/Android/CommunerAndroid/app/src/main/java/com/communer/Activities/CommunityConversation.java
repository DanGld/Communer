package com.communer.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.communer.Adapters.CommentsAdapters;
import com.communer.Adapters.CommunityConversationAdapter;
import com.communer.Application.AppController;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by יובל on 02/09/2015.
 */
public class CommunityConversation extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private CommunityConversationAdapter myAdapter;

    private Toolbar toolbar;
    private FloatingActionButton fab;

    private final static int ADD_POST_INT = 55;

    private ArrayList<Post> community_posts;

    private String mixPanelProjectToken = "537b3567fe348d6c5dfa0d6e2ae688b3";
    private MixpanelAPI mixpanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_conversation);

        mixpanel = MixpanelAPI.getInstance(CommunityConversation.this, mixPanelProjectToken);
        mixpanel.track("Modules - Conversations Page");

        AppUser appUserInstance = AppUser.getInstance();
        community_posts = appUserInstance.getCurrentCommunity().getCommunityPosts();
        ArrayList<Post> arrangedPosts = arrangeUpcomingPosts(community_posts);

        mRecyclerView = (RecyclerView) findViewById(R.id.community_conversation_recycler);
        mLayoutManager = new LinearLayoutManager(CommunityConversation.this);
        myAdapter = new CommunityConversationAdapter(arrangedPosts, CommunityConversation.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(myAdapter);

        initToolbar();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(CommunityConversation.this, NewPost.class);
                startActivityForResult(mIntent, ADD_POST_INT);
            }
        });
    }

    private ArrayList<Post> arrangeUpcomingPosts(ArrayList<Post> community_posts) {
        ArrayList<Post> tempPosts = new ArrayList<Post>();

        for (int i = 0; i < community_posts.size(); i++){
            Post tempPost = community_posts.get(i);
            long postDate = tempPost.getCDate();

            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            Calendar cal = Calendar.getInstance(timeZone);
            cal.setTimeInMillis(postDate);
            Date newPostDate = cal.getTime();

            if (tempPosts.size() == 0){
                tempPosts.add(tempPost);
            }else{
                int tempPostsSize = tempPosts.size();
                boolean addedItem = false;

                for (int j = 0; j < tempPostsSize; j++) {
                    Post postItem = tempPosts.get(j);

                    long existingPostDateLong = postItem.getCDate();

                    Calendar postCal = Calendar.getInstance(timeZone);
                    postCal.setTimeInMillis(existingPostDateLong);
                    Date existingPostDate = postCal.getTime();

                    if (newPostDate.after(existingPostDate)){
                        tempPosts.add(j, tempPost);
                        addedItem = true;
                    }

                    if (addedItem)
                        break;
                }

                if (!addedItem)
                    tempPosts.add(tempPost);
            }
        }

        return tempPosts;
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle(getResources().getString(R.string.conversations_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case (ADD_POST_INT):
                    Post newPost = data.getParcelableExtra("New Post");
                    community_posts.add(0,newPost);
                    myAdapter.notifyDataSetChanged();
                    break;

                default:
                    break;
            }
        }
    }

    public void showCommentsDialog(final Post item){
        final MaterialDialog dialog = new MaterialDialog.Builder(CommunityConversation.this)
                .customView(R.layout.comments_dialog, false)
                .show();

        View view = dialog.getCustomView();
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.comments_recycler);

        final ArrayList<CommentItem> postComments = item.getComments();
        final CommentsAdapters myAdapter = new CommentsAdapters(postComments, CommunityConversation.this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final EditText commentText = (EditText) view.findViewById(R.id.comments_edittext);
        ImageButton cameraBtn = (ImageButton) view.findViewById(R.id.comments_camera_button);
        ImageButton backBtn = (ImageButton) view.findViewById(R.id.comments_back_button);
        ImageButton sendBtn = (ImageButton) view.findViewById(R.id.comment_send);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tempImg = "http://93.188.166.107/selfow/selfowSiteList/fileThumbnailPreview/90170000003907.jpg";
                AppUser appUserInstance = AppUser.getInstance();
                CommunityMember tempMmbr = appUserInstance.getUserAsMember();

                postComments.add(new CommentItem("123", tempImg, 1440073969378L, commentText.getText().toString(), tempMmbr));
                myAdapter.notifyItemInserted(postComments.size() - 1);
                recyclerView.smoothScrollToPosition(postComments.size() - 1);

                sendComment(commentText, item);

                commentText.setText("");

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void sendComment(EditText commentEditText, Post currentConversation) {
        AppUser appUserInstance = AppUser.getInstance();
        String userHash = appUserInstance.getUserHash();
        String url = appUserInstance.getBaseUrl() + userHash + "/com.solidPeak.smartcom.requests.application.SendComment.htm?";

        JSONObject comment = new JSONObject();

        String description = commentEditText.getText().toString();
        String postID = currentConversation.getPostID();
        JSONArray imageData = new JSONArray();

        CommunityMember mmbr = appUserInstance.getUserAsMember();
        Gson gson = new Gson();
        String mmbrString = gson.toJson(mmbr);

        Calendar calendar = Calendar.getInstance();
        long currentMillis = calendar.getTimeInMillis();

        final CommentItem commentItem = new CommentItem(postID,"",currentMillis,description,mmbr);

        try {
            comment.put("description",description);
            comment.put("postID",postID);
            comment.put("member",mmbrString);
            comment.put("imageData",imageData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String sendCommentUrl = url + "comment=" + comment.toString();
        sendCommentUrl = sendCommentUrl.replace(" ","%20");
        Log.d("Volley", "toString: " + sendCommentUrl);

        String query = null;
        try{
            query = URLEncoder.encode(comment.toString(), "UTF-8");
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }
        Log.d("Volley","Query: " + sendCommentUrl);

        String tag_json_obj = "community_conversation_send_comment";

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, sendCommentUrl,null ,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String status = "";
                try{
                    status = response.getString("status");
                    if (status.equals("success")){
                        addCommentToLocalArray(commentItem);
                    }else{
                        Toast.makeText(CommunityConversation.this, "Error sending comment", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CommunityConversation.this, "Error sending comment", Toast.LENGTH_SHORT).show();
            }
        });

        jsonRequest.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_json_obj);
    }

    private void addCommentToLocalArray(CommentItem commentItem){
        AppUser appUserInstance = AppUser.getInstance();
        ArrayList<Post> communityPosts = appUserInstance.getCurrentCommunity().getCommunityPosts();
        for (int i=0; i<communityPosts.size(); i++){
            Post tempPost = communityPosts.get(i);
            if (tempPost.getPostID().equals(commentItem.getPostID())){
                ArrayList<CommentItem> postComments = tempPost.getComments();
                postComments.add(commentItem);
                tempPost.setComments(postComments);
                break;
            }
        }
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
