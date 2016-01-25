package com.communer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.GridView;

import com.communer.Adapters.CommunityContactsAdapter;
import com.communer.Adapters.CommunityMemberAdapter;
import com.communer.Models.CommunityContact;
import com.communer.Models.CommunityMember;
import com.communer.R;

import java.util.ArrayList;

/**
 * Created by יובל on 28/09/2015.
 */
public class MembersActivity extends AppCompatActivity {

    private ArrayList<CommunityContact> contacts;
    private ArrayList<CommunityMember> members;

    private CommunityContactsAdapter contactsAdapter;
    private CommunityMemberAdapter mmbrsAdapter;

    private GridView contactsList;
    private Toolbar toolbar;
    private EditText searchBar;

    private String cameFrom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.members_activity);

        Intent dataIntent = getIntent();
        cameFrom = dataIntent.getStringExtra("came_from");
        contactsList = (GridView)findViewById(R.id.members_listview);

        initToolbar();

        if (cameFrom.equals("eventPage")) {
            contacts = dataIntent.getParcelableArrayListExtra("participants");
            contactsAdapter = new CommunityContactsAdapter(contacts, MembersActivity.this);
            contactsList.setAdapter(contactsAdapter);
        }else if (cameFrom.equals("communityProfile")){
            members = dataIntent.getParcelableArrayListExtra("members");
            mmbrsAdapter = new CommunityMemberAdapter(members, MembersActivity.this);
            contactsList.setAdapter(mmbrsAdapter);
        }

        searchBar = (EditText)findViewById(R.id.mmbr_search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (cameFrom.equals("eventPage"))
                    contactsAdapter.getFilter().filter(s);
                else if (cameFrom.equals("communityProfile"))
                    mmbrsAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);

        if (cameFrom.equals("eventPage")) {
            toolbar.setTitle(getResources().getString(R.string.participants_title));
        }else if(cameFrom.equals("communityProfile")){
            toolbar.setTitle(getResources().getString(R.string.members_title));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
