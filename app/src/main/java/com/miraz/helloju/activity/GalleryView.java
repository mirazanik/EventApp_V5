package com.miraz.helloju.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.miraz.helloju.R;
import com.miraz.helloju.adapter.GalleryViewAdapter;
import com.miraz.helloju.util.Constant;
import com.miraz.helloju.util.Method;
import com.google.android.material.appbar.MaterialToolbar;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class GalleryView extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_view);

        Method method = new Method(GalleryView.this);
        method.forceRTLIfSupported();

        int position = getIntent().getIntExtra("position", 0);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_gallery_view);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ViewPager viewPager = findViewById(R.id.viewpager_gallery_view);
        LinearLayout linearLayout = findViewById(R.id.linearLayout_gallery_view);
        method.bannerAd(linearLayout);

        GalleryViewAdapter galleryViewAdapter = new GalleryViewAdapter(GalleryView.this, Constant.galleryLists);
        viewPager.setAdapter(galleryViewAdapter);
        viewPager.setCurrentItem(position, false);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}