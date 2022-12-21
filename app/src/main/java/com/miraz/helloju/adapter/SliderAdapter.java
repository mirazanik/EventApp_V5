package com.miraz.helloju.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.miraz.helloju.R;
import com.miraz.helloju.interFace.OnClick;
import com.miraz.helloju.item.SliderList;
import com.miraz.helloju.util.EnchantedViewPager;
import com.miraz.helloju.util.Method;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class SliderAdapter extends PagerAdapter {

    private Method method;
    private Activity activity;
    private String type;
    private List<SliderList> sliderLists;
    private LayoutInflater inflater;

    public SliderAdapter(Activity activity, String type, List<SliderList> sliderLists, OnClick onClick) {
        this.activity = activity;
        this.sliderLists = sliderLists;
        this.type = type;
        // TODO Auto-generated constructor stub
        inflater = activity.getLayoutInflater();
        method = new Method(activity, onClick);
    }


    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        String sliderType = sliderLists.get(position).getEvent_type();
        View view;
        if (!sliderType.equals("external")) {
            view = inflater.inflate(R.layout.slider_adapter, container, false);
            MaterialCardView cardView = view.findViewById(R.id.cardView_slider_adapter);
            ImageView imageView = view.findViewById(R.id.imageView_slider_adapter);
            MaterialTextView textViewTitle = view.findViewById(R.id.textView_title_slider_adapter);
            MaterialTextView textViewSubTitle = view.findViewById(R.id.textView_subTitle_slider_adapter);

            Glide.with(activity).load(sliderLists.get(position).getEvent_banner_thumb())
                    .placeholder(R.drawable.placeholder_banner).into(imageView);

            textViewTitle.setText(sliderLists.get(position).getEvent_title());
            textViewSubTitle.setText(sliderLists.get(position).getEvent_address());

            cardView.setOnClickListener(v -> method.click(position, type, sliderLists.get(position).getEvent_title(), sliderLists.get(position).getEvent_id()));

            cardView.setTag(EnchantedViewPager.ENCHANTED_VIEWPAGER_POSITION + position);

        } else {

            view = inflater.inflate(R.layout.slider_external_adapter, container, false);
            MaterialCardView cardView = view.findViewById(R.id.cardView_slider_external_adapter);
            ImageView imageView = view.findViewById(R.id.imageView_slider_external_adapter);
            MaterialTextView textViewTitle = view.findViewById(R.id.textView_slider_external_adapter);

            Glide.with(activity).load(sliderLists.get(position).getEvent_banner_thumb())
                    .placeholder(R.drawable.placeholder_banner).into(imageView);

            textViewTitle.setText(sliderLists.get(position).getEvent_title());

            cardView.setOnClickListener(v -> {
                try {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sliderLists.get(position).getExternal_link())));
                } catch (Exception e) {
                    method.alertBox(activity.getResources().getString(R.string.wrong));
                }
            });

            cardView.setTag(EnchantedViewPager.ENCHANTED_VIEWPAGER_POSITION + position);

        }
        container.addView(view, 0);
        return view;
    }


    @Override
    public int getCount() {
        return sliderLists.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        (container).removeView((View) object);
    }
}

