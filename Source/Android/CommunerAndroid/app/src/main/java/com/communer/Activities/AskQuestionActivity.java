package com.communer.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.communer.Application.AppController;
import com.communer.R;
import com.communer.Utils.AppUser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by יובל on 01/09/2015.
 */
public class AskQuestionActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CheckBox publicCB;

    private EditText postTitle, postContent;
    private ProgressBar askPB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ask_question);
//        setupWindowAnimations();

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle("Post");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        publicCB = (CheckBox)findViewById(R.id.ask_question_ispublic);
        publicCB.setChecked(true);

        postTitle = (EditText)findViewById(R.id.ask_question_title);
        postContent = (EditText)findViewById(R.id.ask_question_content);
        askPB = (ProgressBar)findViewById(R.id.ask_question_pb);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_send, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_send:
                boolean toSend = true;
                String message = "";
                if (postTitle.getText().toString().length()==0){
                    toSend = false;
                    message = "Please make sure you enter a title";
                }
                else {
                    if (postContent.getText().toString().length()==0){
                        toSend = false;
                        message = "Please make sure you enter a description";
                    }
                }

                if (toSend==true) {
                    askPB.setVisibility(View.VISIBLE);
                    hideSoftKeyboard();
                    sendQuestion();
                }
                else {
                    Toast.makeText(getApplication(),message,Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void sendQuestion(){
        AppUser appUserInstance = AppUser.getInstance();
        String userHash = appUserInstance.getUserHash();
        String url = appUserInstance.getBaseUrl() + userHash + "/com.solidPeak.smartcom.requests.application.SendQuastion.htm?";

        String title = postTitle.getText().toString();
        String content = postContent.getText().toString();
        boolean isPublic = publicCB.isChecked();
        String communityID = appUserInstance.getCurrentCommunity().getCommunityID();

        JSONObject question = new JSONObject();
        try {
            question.put("title",title);
            question.put("description",content);
            question.put("isPublic",isPublic);
            question.put("communityID",communityID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String askURL = url + "quastion=" + question.toString();
        askURL = askURL.replace(" ","%20");
        String tag_json_obj = "ask_question_call";

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, askURL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                askPB.setVisibility(View.GONE);
                try {
                    String status = response.getString("status");
                    String postID = response.getString("postID");
                    if (status.equals("success")){
                        addPost();
                    }else{
                        Toast.makeText(AskQuestionActivity.this, "Error sending question to server", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                askPB.setVisibility(View.GONE);
                Toast.makeText(AskQuestionActivity.this, "Error sending question to server", Toast.LENGTH_SHORT).show();
            }
        });

        jsonRequest.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonRequest, tag_json_obj);
    }

    public void addPost(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("Title", postTitle.getText().toString());
        resultIntent.putExtra("Content", postContent.getText().toString());
        resultIntent.putExtra("isPublic", publicCB.isChecked());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

/*    private void setupWindowAnimations(){
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = (Fade) TransitionInflater.from(AskQuestionActivity.this).inflateTransition(R.transition.activity_fade);
            getWindow().setEnterTransition(fade);
        }
    }*/

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
