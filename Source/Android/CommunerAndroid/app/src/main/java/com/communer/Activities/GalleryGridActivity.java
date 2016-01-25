package com.communer.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;

import com.communer.Adapters.GalleryGridAdapter;
import com.communer.Models.GalleryImage;
import com.communer.R;

import java.util.ArrayList;


public class GalleryGridActivity extends AppCompatActivity {

    private int[] items = {R.drawable.yoga_sample,R.drawable.yoga_sample,
          R.drawable.yoga_sample,R.drawable.yoga_sample};

    private StaggeredGridLayoutManager gaggeredGridLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallerygrid);

        String pageTitle = getIntent().getStringExtra("galleryTitle");
        ArrayList<GalleryImage> images = getIntent().getParcelableArrayListExtra("galleryImgs");

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.gallery_recycler_view);
        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(3, 1);
        recyclerView.setLayoutManager(gaggeredGridLayoutManager);

        GalleryGridAdapter rcAdapter = new GalleryGridAdapter(images,GalleryGridActivity.this);
        recyclerView.setAdapter(rcAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle(pageTitle);
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
