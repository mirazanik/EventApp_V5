package com.miraz.helloju.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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

public class CategoryAdapter extends RecyclerView.Adapter {

    private Activity activity;
    private Method method;
    private String type;
    private int columnWidth;
    private List<CategoryList> categoryLists;

    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM = 1;

    public CategoryAdapter(Activity activity, List<CategoryList> categoryLists, String type, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        this.categoryLists = categoryLists;
        method = new Method(activity, onClick);
        Resources r = activity.getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, r.getDisplayMetrics());
        columnWidth = (int) ((method.getScreenWidth() - ((6 + 3) * padding)));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.category_adapter, parent, false);
            return new ViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View v = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
            return new ProgressViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {

            final ViewHolder viewHolder = (ViewHolder) holder;

            ConstraintLayout.LayoutParams layoutParamsMain = new ConstraintLayout.LayoutParams(columnWidth / 3, columnWidth / 3);
            viewHolder.constraintLayout.setLayoutParams(layoutParamsMain);

            int imageHeight = columnWidth / 3;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageHeight / 3, imageHeight / 3);
            layoutParams.gravity = Gravity.CENTER;
            viewHolder.imageViewIcon.setLayoutParams(layoutParams);

            Glide.with(activity).load(categoryLists.get(position).getCategory_image_thumb())
                    .placeholder(R.drawable.placeholder_logo).into(viewHolder.imageView);

            Glide.with(activity).load(categoryLists.get(position).getCategory_icon())
                    .placeholder(R.drawable.placeholder_logo).into(viewHolder.imageViewIcon);

            viewHolder.textView.setText(categoryLists.get(position).getCategory_name());

            String items = activity.getResources().getString(R.string.items) + " " + "("
                    + categoryLists.get(position).getCat_count()
                    + ")";

            viewHolder.textViewCount.setText(items);

            viewHolder.constraintLayout.setOnClickListener(v -> method.click(position, type, categoryLists.get(position).getCategory_name(), categoryLists.get(position).getCid()));

            String string = categoryLists.get(position).getCategory_bg();
            if (!string.equals("")) {
                try {
                    GradientDrawable gd = new GradientDrawable();// Initialize a new GradientDrawable
                    gd.setShape(GradientDrawable.RECTANGLE);// Specify the shape of drawable
                    gd.setColor(Color.parseColor(string));// Set the fill colors of drawable
                    gd.setCornerRadius(1000);// Make the border rounded border corner radius
                    viewHolder.view.setBackground(gd);
                } catch (Exception e) {
                    Log.d("error_show", e.toString());
                }
            }

        }

    }

    @Override
    public int getItemCount() {
        return categoryLists.size() + 1;
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    private boolean isHeader(int position) {
        return position == categoryLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private CircleImageView imageView;
        private ImageView imageViewIcon;
        private MaterialTextView textView, textViewCount;
        private ConstraintLayout constraintLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_cat_adapter);
            imageViewIcon = itemView.findViewById(R.id.imageView_icon_cat_adapter);
            view = itemView.findViewById(R.id.view_cat_adapter);
            textView = itemView.findViewById(R.id.textView_cat_adapter);
            textViewCount = itemView.findViewById(R.id.textView_count_cat_adapter);
            constraintLayout = itemView.findViewById(R.id.con_cat_adapter);

        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public static ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar_loading);
        }
    }

}
