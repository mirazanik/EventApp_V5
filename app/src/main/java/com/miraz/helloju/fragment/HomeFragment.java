package com.miraz.helloju.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miraz.helloju.R;
import com.miraz.helloju.activity.EventDetail;
import com.miraz.helloju.activity.MainActivity;
import com.miraz.helloju.adapter.HomeCatAdapter;
import com.miraz.helloju.adapter.HomeNearByEvent;
import com.miraz.helloju.adapter.HomeRecentAdapter;
import com.miraz.helloju.adapter.SliderAdapter;
import com.miraz.helloju.interFace.OnClick;
import com.miraz.helloju.response.HomeRP;
import com.miraz.helloju.rest.ApiClient;
import com.miraz.helloju.rest.ApiInterface;
import com.miraz.helloju.util.API;
import com.miraz.helloju.util.Constant;
import com.miraz.helloju.util.EnchantedViewPager;
import com.miraz.helloju.util.Events;
import com.miraz.helloju.util.GlobalBus;
import com.miraz.helloju.util.Method;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private View viewHome;
    private HomeRP homeRP;
    private ProgressBar progressBar;
    private TextInputEditText editTextSearch;
    private ImageView imageViewSearch;
    private EnchantedViewPager viewPager;
    private HomeCatAdapter homeCatAdapter;
    private HomeRecentAdapter homeRecentAdapter;
    private HomeNearByEvent homeNearByEvent;
    private SliderAdapter sliderAdapter;
    private InputMethodManager imm;
    private ConstraintLayout conMain, conNoData, conCat, conRecent, conNearBy;
    private RecyclerView recyclerViewCat, recyclerViewRecentView, recyclerViewNearBy;
    private MaterialTextView textViewCatSeeAll, textViewRecent, textViewNear;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.home_fragment, container, false);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.home));
        }

        GlobalBus.getBus().register(this);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        onClick = (position, type, title, id) -> {
            if (type.equals("home_cat")) {
                callEvent(id, type, title);
            } else {
                startActivity(new Intent(getActivity(), EventDetail.class)
                        .putExtra("id", id)
                        .putExtra("type", type)
                        .putExtra("position", position));
            }
        };
        method = new Method(getActivity());

        progressBar = view.findViewById(R.id.progressbar_home);
        conMain = view.findViewById(R.id.con_main_home);
        viewHome = view.findViewById(R.id.view_home);
        editTextSearch = view.findViewById(R.id.editText_home);
        imageViewSearch = view.findViewById(R.id.imageView_search_home);
        viewPager = view.findViewById(R.id.viewPager_home);
        textViewCatSeeAll = view.findViewById(R.id.textView_catView_home);
        textViewRecent = view.findViewById(R.id.textView_recent_home);
        textViewNear = view.findViewById(R.id.textView_nearby_home);
        conNoData = view.findViewById(R.id.con_noDataFound);
        recyclerViewCat = view.findViewById(R.id.recyclerView_cat_home);
        recyclerViewRecentView = view.findViewById(R.id.recyclerView_recent_home);
        recyclerViewNearBy = view.findViewById(R.id.recyclerView_nearby_home);
        conCat = view.findViewById(R.id.con_cat_home);
        conRecent = view.findViewById(R.id.con_recent_home);
        conNearBy = view.findViewById(R.id.con_nearby_home);

        conMain.setVisibility(View.GONE);
        conNoData.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        recyclerViewCat.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        recyclerViewCat.setLayoutManager(layoutManager);
        recyclerViewCat.setFocusable(false);
        recyclerViewCat.setNestedScrollingEnabled(false);

        recyclerViewNearBy.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerNearBy = new LinearLayoutManager(getActivity());
        recyclerViewNearBy.setLayoutManager(layoutManagerNearBy);
        recyclerViewNearBy.setFocusable(false);
        recyclerViewNearBy.setNestedScrollingEnabled(false);

        recyclerViewRecentView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerRecent = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewRecentView.setLayoutManager(layoutManagerRecent);
        recyclerViewRecentView.setFocusable(false);
        recyclerViewRecentView.setNestedScrollingEnabled(false);

        int columnWidth = method.getScreenWidth();
        viewPager.setLayoutParams(new ConstraintLayout.LayoutParams(columnWidth, columnWidth / 2));
        viewHome.setLayoutParams(new ConstraintLayout.LayoutParams(columnWidth, columnWidth / 2 - 60));

        viewPager.useScale();
        viewPager.removeAlpha();

        editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search();
            }
            return false;
        });

        imageViewSearch.setOnClickListener(v -> search());

        textViewCatSeeAll.setOnClickListener(v -> getActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frameLayout_main, new CategoryFragment(), getResources().getString(R.string.category))
                .addToBackStack(getResources().getString(R.string.category)).commitAllowingStateLoss());

        textViewRecent.setOnClickListener(v -> callEvent("", "recentView_home", getResources().getString(R.string.recently_view_event)));

        textViewNear.setOnClickListener(v -> callEvent("", "near", getResources().getString(R.string.nearby)));

        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                home(method.userId());
            } else {
                home("0");
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        return view;

    }

    private void search() {

        String search = editTextSearch.getText().toString();
        if (!search.isEmpty() || !search.equals("")) {
            editTextSearch.clearFocus();
            imm.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
            callEvent("", "search", search);
        } else {
            if (getActivity().getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
            method.alertBox(getResources().getString(R.string.please_enter_keyWord));
        }

    }

    private void callEvent(String id, String type, String title) {

        EventFragment subCategoryFragment = new EventFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("type", type);
        bundle.putString("title", title);
        subCategoryFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout_main, subCategoryFragment, title)
                .addToBackStack(title).commitAllowingStateLoss();

    }

    @Subscribe
    public void getNotify(Events.Favourite favourite) {
        if (homeNearByEvent != null && homeRP != null) {
            for (int i = 0; i < homeRP.getNearByLists().size(); i++) {
                if (homeRP.getNearByLists().get(i).getId().equals(favourite.getId())) {
                    homeRP.getNearByLists().get(i).setIs_fav(favourite.isIs_fav());
                    homeNearByEvent.notifyItemChanged(i);
                }
            }
        }
    }

    private void home(String userId) {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("user_lat", Constant.stringLatitude);
            jsObj.addProperty("user_long", Constant.stringLongitude);
            jsObj.addProperty("method_name", "get_home");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<HomeRP> call = apiService.getHome(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<HomeRP>() {
                @Override
                public void onResponse(@NotNull Call<HomeRP> call, @NotNull Response<HomeRP> response) {

                    if (getActivity() != null) {

                        try {
                            homeRP = response.body();
                            assert homeRP != null;

                            if (homeRP.getStatus().equals("1")) {

                                if (homeRP.getSliderLists().size() != 0) {
                                    sliderAdapter = new SliderAdapter(getActivity(), "slider", homeRP.getSliderLists(), onClick);
                                    viewPager.setAdapter(sliderAdapter);
                                }

                                if (homeRP.getCategoryLists().size() != 0) {
                                    homeCatAdapter = new HomeCatAdapter(getActivity(), homeRP.getCategoryLists(), "home_cat", onClick);
                                    recyclerViewCat.setAdapter(homeCatAdapter);
                                } else {
                                    conCat.setVisibility(View.GONE);
                                }

                                if (homeRP.getRecentViewLists().size() != 0) {
                                    homeRecentAdapter = new HomeRecentAdapter(getActivity(), "recentView_home", homeRP.getRecentViewLists(), onClick);
                                    recyclerViewRecentView.setAdapter(homeRecentAdapter);
                                } else {
                                    conRecent.setVisibility(View.GONE);
                                }

                                if (homeRP.getNearByLists().size() != 0) {
                                    homeNearByEvent = new HomeNearByEvent(getActivity(), "home_nearBy", homeRP.getNearByLists(), onClick);
                                    recyclerViewNearBy.setAdapter(homeNearByEvent);
                                } else {
                                    conNearBy.setVisibility(View.GONE);
                                }

                                conMain.setVisibility(View.VISIBLE);

                            } else if (homeRP.getStatus().equals("2")) {
                                method.suspend(homeRP.getMessage());
                            } else {
                                conNoData.setVisibility(View.VISIBLE);
                                method.alertBox(homeRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<HomeRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure_data", t.toString());
                    conNoData.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the registered event.
        GlobalBus.getBus().unregister(this);
    }

}
