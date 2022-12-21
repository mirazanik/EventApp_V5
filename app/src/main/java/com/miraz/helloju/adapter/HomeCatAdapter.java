package com.miraz.helloju.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.miraz.helloju.R;
import com.miraz.helloju.interFace.OnClick;
import com.miraz.helloju.item.CategoryList;
import com.miraz.helloju.util.Method;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeCatAdapter extends RecyclerView.Adapter<HomeCatAdapter.ViewHolder> {

    private Activity activity;
    private Method method;
    private String type;
    private List<CategoryList> categoryLists;

    public HomeCatAdapter(Activity activity, List<CategoryList> categoryLists, String type, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        this.categoryLists = categoryLists;
        method = new Method(activity, onClick);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activity).inflate(R.layout.home_cat_adapter, parent, false);

        return new HomeCatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        Glide.with(activity).load(categoryLists.get(position).getCategory_image_thumb())
                .placeholder(R.drawable.placeholder_logo).into(holder.imageView);

        Glide.with(activity).load(categoryLists.get(position).getCategory_icon())
                .placeholder(R.drawable.placeholder_logo).into(holder.imageViewLogo);

        holder.textView.setText(categoryLists.get(position).getCategory_name());

        holder.constraintLayout.setOnClickListener(v -> method.click(position, type, categoryLists.get(position).getCategory_name(), categoryLists.get(position).getCid()));

        String string = categoryLists.get(position).getCategory_bg();
        if (!string.equals("")) {
            try {
                GradientDrawable gd = new GradientDrawable(); // Initialize a new GradientDrawable
                gd.setShape(GradientDrawable.RECTANGLE); // Specify the shape of drawable
                gd.setColor(Color.parseColor(string)); // Set the fill colors of drawable
                gd.setCornerRadius(1000); //Make the border rounded border corner radius
                holder.view.setBackground(gd);
            } catch (Exception e) {
                Log.d("error_show", e.toString());
            }
        }

    }

    @Override
    public int getItemCount() {
        return categoryLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private CircleImageView imageView;
        private ImageView imageViewLogo;
        private MaterialTextView textView;
        private ConstraintLayout constraintLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_homeCat_adapter);
            imageViewLogo = itemView.findViewById(R.id.imageView_logo_homeCat_adapter);
            view = itemView.findViewById(R.id.view_homeCat_adapter);
            textView = itemView.findViewById(R.id.textView_homeCat_adapter);
            constraintLayout = itemView.findViewById(R.id.con_catHome_adapter);

        }
    }
}
