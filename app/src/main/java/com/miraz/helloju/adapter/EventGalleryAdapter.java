package com.miraz.helloju.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.miraz.helloju.R;
import com.miraz.helloju.interFace.ImageDelete;
import com.miraz.helloju.item.GalleryList;

import java.util.List;

public class EventGalleryAdapter extends RecyclerView.Adapter<EventGalleryAdapter.ViewHolder> {

    private Activity activity;
    private String type;
    private ImageDelete imageDelete;
    private List<GalleryList> galleryLists;

    public EventGalleryAdapter(Activity activity, List<GalleryList> galleryLists, String type, ImageDelete imageDelete) {
        this.activity = activity;
        this.type = type;
        this.galleryLists = galleryLists;
        this.imageDelete = imageDelete;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activity).inflate(R.layout.event_gallery_adapter, parent, false);

        return new EventGalleryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (galleryLists.get(position).getCover_id().equals("")) {
            Glide.with(activity).load("file://" + galleryLists.get(position).getCover_image())
                    .placeholder(R.drawable.placeholder_logo)
                    .into(holder.imageView);
        } else {
            Glide.with(activity).load(galleryLists.get(position).getCover_image())
                    .placeholder(R.drawable.placeholder_logo)
                    .into(holder.imageView);
        }

        holder.imageViewClose.setOnClickListener(view -> imageDelete.delete(galleryLists.get(position).getCover_id(), type, position));

    }

    @Override
    public int getItemCount() {
        return galleryLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView, imageViewClose;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_eg_adapter);
            imageViewClose = itemView.findViewById(R.id.imageView_close_eg_adapter);

        }
    }

}
