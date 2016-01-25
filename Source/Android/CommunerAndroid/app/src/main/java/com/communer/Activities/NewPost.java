package com.communer.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.communer.Application.AppController;
import com.communer.Models.CommentItem;
import com.communer.Models.CommunityMember;
import com.communer.Models.Post;
import com.communer.R;
import com.communer.Utils.AppUser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by יובל on 28/09/2015.
 */
public class NewPost extends AppCompatActivity {

    private EditText title, description;
    private ImageButton send_btn;

    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_post);

        initToolbar();

        title = (EditText)findViewById(R.id.new_post_title);
        description = (EditText)findViewById(R.id.new_post_description);
        send_btn = (ImageButton)toolbar.findViewById(R.id.toolbarSendBtn);

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titleText = title.getText().toString();
                String descriptionText = description.getText().toString();
                if (titleText.length() > 0){
                    if (descriptionText.length() > 0){
                        ArrayList<CommentItem> comments = new ArrayList<CommentItem>();
                        Calendar c = Calendar.getInstance();
                        long currentMilis = c.getTimeInMillis();

                        AppUser appUserInstance = AppUser.getInstance();
                        Post newPost = new Post("",titleText,"",appUserInstance.getUserAsMember(),currentMilis,descriptionText,comments);

                        sendPost(newPost);
                        resultIntent(newPost);
//                        onBackPressed();
                    }else{
                        Toast.makeText(NewPost.this,"Description is empty", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(NewPost.this,"Title is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle(getResources().getString(R.string.create_post_title));
        toolbar.setTitleTextColor(ContextCompat.getColor(NewPost.this, R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void resultIntent(Post newPost){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("New Post", newPost);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void sendPost(final Post newPost) {
        AppUser appUserInstance = AppUser.getInstance();

        String userHash = appUserInstance.getUserHash();
        String url = appUserInstance.getBaseUrl() + userHash + "/com.solidPeak.smartcom.requests.application.SendPost.htm?";

        JSONObject post = new JSONObject();

        String post_title = newPost.getTitle();
        String post_desc = newPost.getContent();
        String cd = String.valueOf(newPost.getCDate());
        String post_id = newPost.getPostID();
        String communityID = appUserInstance.getCurrentCommunity().getCommunityID();
        CommunityMember mmbr = newPost.getMember();
        JSONArray comments = new JSONArray(newPost.getComments());

        Gson gson = new Gson();
        String mmbrString = gson.toJson(mmbr);

        try{
            post.put("title",post_title);
            post.put("imageURL","");
            post.put("content",post_desc);
            post.put("cd",cd);
            post.put("postID",post_id);
            post.put("communityID",communityID);
            post.put("member",mmbrString);
            post.put("comments",comments.toString());
        }catch(JSONException e){
            e.printStackTrace();
        }

        String query = null;
        try{
            query = URLEncoder.encode(post.toString(), "utf-8");
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }

        String sendPostUrl = url + "post=" + query;
        String tag_json_obj = "send_post_call";

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, sendPostUrl, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("success")){
                        Toast.makeText(NewPost.this, "Post sent succesfully", Toast.LENGTH_SHORT).show();
                        addPostToLocalData(newPost);
                    }else{
                        Toast.makeText(NewPost.this, "Error sending post to server", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(NewPost.this, "Error sending post to server", Toast.LENGTH_SHORT).show();
            }
        });

        jsonRequest.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_json_obj);
    }

    private void addPostToLocalData(Post newPost){
        AppUser appUserInstance = AppUser.getInstance();

        ArrayList<Post> comPosts = appUserInstance.getCurrentCommunity().getCommunityPosts();
        comPosts.add(0,newPost);
        appUserInstance.getCurrentCommunity().setCommunityPosts(comPosts);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
