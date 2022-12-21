package com.miraz.helloju.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.miraz.helloju.R;
import com.miraz.helloju.interFace.FavouriteIF;
import com.miraz.helloju.interFace.OnClick;
import com.miraz.helloju.item.EventList;
import com.miraz.helloju.util.Method;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class HomeNearByEvent extends RecyclerView.Adapter<HomeNearByEvent.ViewHolder> {

    private Activity activity;
    private String type;
    private Method method;
    private int columnWidth;
    private List<EventList> eventLists;

    public HomeNearByEvent(Activity activity, String type, List<EventList> eventLists, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        this.eventLists = eventLists;
        method = new Method(activity, onClick);
        columnWidth = method.getScreenWidth();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activity).inflate(R.layout.event_adapter, parent, false);
        return new ViewHolder(view);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        if (eventLists.get(position).isIs_fav()) {
            holder.imageViewFav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav_hov));
        } else {
            holder.imageViewFav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav));
        }

        holder.imageView.setLayoutParams(new ConstraintLayout.LayoutParams(columnWidth, columnWidth / 2));
        holder.view.setLayoutParams(new ConstraintLayout.LayoutParams(columnWidth, columnWidth / 2));

        Glide.with(activity).load(eventLists.get(position).getEvent_banner_thumb())
                .placeholder(R.drawable.placeholder_banner).into(holder.imageView);

        holder.textViewTitle.setText(eventLists.get(position).getEvent_title());
        String date = eventLists.get(position).getEvent_date();
        String[] separated = date.split(",");
        holder.textViewDay.setText(separated[0]);
        holder.textViewMonth.setText(separated[1]);
        holder.textViewAdd.setText(eventLists.get(position).getEvent_address());

        holder.materialCardView.setOnClickListener(v -> method.click(position, type, eventLists.get(position).getEvent_title(), eventLists.get(position).getId()));

        holder.imageViewFav.setOnClickListener(view -> {
            if (method.isLogin()) {
                FavouriteIF favouriteIF = (isFavourite, message) -> {
                    if (isFavourite) {
                        holder.imageViewFav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav_hov));
                    } else {
                        holder.imageViewFav.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_fav));
                    }
                };
                method.addToFav(eventLists.get(position).getId(), method.userId(), type, position, favouriteIF);
            } else {
                Method.loginBack = true;
//                activity.startActivity(new Intent(activity, Login.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return eventLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private MaterialCardView materialCardView;
        private ImageView imageView, imageViewFav;
        private MaterialTextView textViewTitle, textViewDay, textViewMonth, textViewAdd;

        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView.findViewById(R.id.view_subCat_adapter);
            materialCardView = itemView.findViewById(R.id.cardView_event_adapter);
            imageView = itemView.findViewById(R.id.imageView_event_adapter);
            imageViewFav = itemView.findViewById(R.id.imageView_fav_event_adapter);
            textViewTitle = itemView.findViewById(R.id.textView_title_event_adapter);
            textViewDay = itemView.findViewById(R.id.textView_day_event_adapter);
            textViewMonth = itemView.findViewById(R.id.textView_month_event_adapter);
            textViewAdd = itemView.findViewById(R.id.textView_add_event_adapter);

        }
    }
}
