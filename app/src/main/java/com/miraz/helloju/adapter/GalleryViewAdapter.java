package com.miraz.helloju.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.miraz.helloju.R;
import com.miraz.helloju.item.GalleryList;
import com.miraz.helloju.util.TouchImageView;

import java.util.List;

public class GalleryViewAdapter extends PagerAdapter {

    private Activity activity;
    private List<GalleryList> galleryLists;
    private LayoutInflater layoutInflater;

    public GalleryViewAdapter(Activity activity, List<GalleryList> galleryLists) {
        this.activity = activity;
        this.galleryLists = galleryLists;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.gallary_view_adapter, container, false);

        TouchImageView imageView = view.findViewById(R.id.imageView_gallery_adapter);

        Glide.with(activity).load(galleryLists.get(position).getCover_image())
                .placeholder(R.drawable.placeholder_banner).into(imageView);

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return galleryLists.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
        return view == obj;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
