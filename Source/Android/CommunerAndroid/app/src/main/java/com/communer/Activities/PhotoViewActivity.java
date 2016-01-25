package com.communer.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ToxicBakery.viewpager.transforms.DepthPageTransformer;
import com.communer.Models.GalleryImage;
import com.communer.R;
import com.communer.Utils.HackyViewPager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by ���� on 27/08/2015.
 */
public class PhotoViewActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private HackyViewPager mViewPager;

    private String parentActivity = "";
    private int pickedPosition = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_view_activity);

        ArrayList<GalleryImage> imagesArray = getIntent().getParcelableArrayListExtra("Images");
        parentActivity = getIntent().getExtras().getString("ParentClassName");
        pickedPosition = getIntent().getExtras().getInt("picked_img_pos");

        initToolbar();

        mViewPager = (HackyViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new PhotosPagerAdapter(imagesArray,PhotoViewActivity.this));
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        mViewPager.setCurrentItem(pickedPosition);

    }

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        Intent newIntent=null;
        try {
            newIntent = new Intent(PhotoViewActivity.this,Class.forName("com.communer.Activities." + parentActivity));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return newIntent;
    }

    private void initToolbar(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle("Gallery");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    class PhotosPagerAdapter extends PagerAdapter {
        ArrayList<GalleryImage> images;
        Context mContext;

        public PhotosPagerAdapter(ArrayList<GalleryImage> images, Context mContext) {
            this.images = images;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public View instantiateItem(final ViewGroup container, int position) {
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View photoLayout = inflater.inflate(R.layout.single_image, null);

            ImageView image = (ImageView)photoLayout.findViewById(R.id.single_image);
            Picasso.with(mContext)
                    .load(images.get(position).getUrl())
                    .into(image);

            PhotoViewAttacher mAttacher = new PhotoViewAttacher(image);
            container.addView(photoLayout);
            return photoLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}
