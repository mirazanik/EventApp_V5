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
import com.miraz.helloju.interFace.OnClick;
import com.miraz.helloju.item.EventList;
import com.miraz.helloju.util.Method;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class HomeRecentAdapter extends RecyclerView.Adapter<HomeRecentAdapter.ViewHolder> {

    private Activity activity;
    private String type;
    private Method method;
    private List<EventList> eventLists;

    public HomeRecentAdapter(Activity activity, String type, List<EventList> eventLists, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        this.eventLists = eventLists;
        method = new Method(activity, onClick);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activity).inflate(R.layout.home_recent_adapter, parent, false);

        return new HomeRecentAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        Glide.with(activity).load(eventLists.get(position).getEvent_banner_thumb())
                .placeholder(R.drawable.placeholder_banner).into(holder.imageView);

        holder.materialCardView.setOnClickListener(v -> method.click(position, type, eventLists.get(position).getEvent_title(), eventLists.get(position).getId()));

    }

    @Override
    public int getItemCount() {
        return eventLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private MaterialCardView materialCardView;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_recent_adapter);
            materialCardView = itemView.findViewById(R.id.cardView_recent_adapter);

        }
    }
}
