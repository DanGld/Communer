package com.communer.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.communer.Adapters.QuestionsAdapter;
import com.communer.Models.Post;
import com.communer.R;
import com.communer.Utils.AppUser;

import java.util.ArrayList;

/**
 * Created by ���� on 09/08/2015.
 */
public class QAPrivate extends Fragment {

    private RecyclerView qaPrivateRecycler;
    private QuestionsAdapter myAdapter;
    private ArrayList<Post> questionsArray;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.qa_private, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppUser appUserInstance = AppUser.getInstance();
        questionsArray = appUserInstance.getCurrentCommunity().getPrivateQA();

        qaPrivateRecycler = (RecyclerView)view.findViewById(R.id.qa_private_recycler);

        qaPrivateRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        myAdapter = new QuestionsAdapter(questionsArray,getActivity());
        qaPrivateRecycler.setAdapter(myAdapter);
        qaPrivateRecycler.setItemAnimator(new DefaultItemAnimator());
    }

    public void addPrivatePost(Post newPost){
        questionsArray.add(newPost);
        myAdapter.notifyItemInserted(questionsArray.size()-1);
    }
}
