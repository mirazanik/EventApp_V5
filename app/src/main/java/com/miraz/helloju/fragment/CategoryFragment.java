package com.miraz.helloju.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miraz.helloju.R;
import com.miraz.helloju.activity.MainActivity;
import com.miraz.helloju.adapter.CategoryAdapter;
import com.miraz.helloju.interFace.OnClick;
import com.miraz.helloju.item.CategoryList;
import com.miraz.helloju.response.CategoryRP;
import com.miraz.helloju.rest.ApiClient;
import com.miraz.helloju.rest.ApiInterface;
import com.miraz.helloju.util.API;
import com.miraz.helloju.util.EndlessRecyclerViewScrollListener;
import com.miraz.helloju.util.Method;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ConstraintLayout conNoData;
    private List<CategoryList> categoryLists;
    private CategoryAdapter categoryAdapter;
    private Boolean isOver = false;
    private int paginationIndex = 1;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.category_fragment, container, false);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.category));
        }

        categoryLists = new ArrayList<>();

        progressBar = view.findViewById(R.id.progressbar_category_fragment);
        conNoData = view.findViewById(R.id.con_noDataFound);
        recyclerView = view.findViewById(R.id.recyclerView_category_fragment);

        conNoData.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        onClick = (position, type, title, id) -> {
            EventFragment subCategoryFragment = new EventFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id", id);
            bundle.putString("type", type);
            bundle.putString("title", title);
            subCategoryFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.frameLayout_main, subCategoryFragment, title)
                    .addToBackStack(title).commitAllowingStateLoss();
        };
        method = new Method(getActivity(), onClick);

        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (categoryAdapter.getItemViewType(position) == 0) {
                    return 3;
                }
                return 1;
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(() -> {
                        paginationIndex++;
                        callData();
                    }, 1000);
                } else {
                    if (categoryAdapter != null) {
                        categoryAdapter.hideHeader();
                    }
                }
            }
        });

        callData();

        return view;
    }

    private void callData() {
        if (method.isNetworkAvailable()) {
            category();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    private void category() {

        if (getActivity() != null) {

            if (categoryAdapter == null) {
                categoryLists.clear();
                progressBar.setVisibility(View.VISIBLE);
            }

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("page", paginationIndex);
            jsObj.addProperty("method_name", "get_category");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<CategoryRP> call = apiService.getCategory(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<CategoryRP>() {
                @Override
                public void onResponse(@NotNull Call<CategoryRP> call, @NotNull Response<CategoryRP> response) {

                    if (getActivity() != null) {

                        try {
                            CategoryRP categoryRP = response.body();
                            assert categoryRP != null;

                            if (categoryRP.getStatus().equals("1")) {

                                if (categoryRP.getCategoryLists().size() == 0) {
                                    if (categoryAdapter != null) {
                                        categoryAdapter.hideHeader();
                                        isOver = true;
                                    }
                                } else {
                                    categoryLists.addAll(categoryRP.getCategoryLists());
                                }

                                if (categoryAdapter == null) {
                                    if (categoryLists.size() != 0) {
                                        categoryAdapter = new CategoryAdapter(getActivity(), categoryLists, "cat", onClick);
                                        recyclerView.setAdapter(categoryAdapter);
                                    } else {
                                        conNoData.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    categoryAdapter.notifyDataSetChanged();
                                }

                            } else {
                                conNoData.setVisibility(View.VISIBLE);
                                method.alertBox(categoryRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<CategoryRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure_data", t.toString());
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });
        }
    }

}