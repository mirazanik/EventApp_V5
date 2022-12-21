package com.miraz.helloju.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.miraz.helloju.R;
import com.miraz.helloju.util.Method;
import com.miraz.helloju.util.TouchImageView;
import com.google.android.material.appbar.MaterialToolbar;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class EventImageView extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_image_view);

        Method method = new Method(EventImageView.this);
        method.forceRTLIfSupported();

        String stringUrl = getIntent().getStringExtra("url");

        MaterialToolbar toolbar = findViewById(R.id.toolbar_event_image_view);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TouchImageView imageView = findViewById(R.id.imageView_event_image_view);
        LinearLayout linearLayout = findViewById(R.id.linearLayout_event_image_view);
        method.bannerAd(linearLayout);

        Glide.with(EventImageView.this).load(stringUrl)
                .placeholder(R.drawable.placeholder_banner)
                .into(imageView);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}