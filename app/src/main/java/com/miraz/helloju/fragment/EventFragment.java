package com.miraz.helloju.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.miraz.helloju.adapter.EventAdapter;
import com.miraz.helloju.interFace.OnClick;
import com.miraz.helloju.item.EventList;
import com.miraz.helloju.response.EventRP;
import com.miraz.helloju.rest.ApiClient;
import com.miraz.helloju.rest.ApiInterface;
import com.miraz.helloju.util.API;
import com.miraz.helloju.util.Constant;
import com.miraz.helloju.util.EndlessRecyclerViewScrollListener;
import com.miraz.helloju.util.Events;
import com.miraz.helloju.util.GlobalBus;
import com.miraz.helloju.util.Method;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private String id, type, title;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<EventList> eventLists;
    private ConstraintLayout conNoData;
    private MaterialButton buttonLogin;
    private ImageView imageViewData;
    private MaterialTextView textViewData;
    private Boolean isOver = false;
    private int paginationIndex = 1, totalArraySize = 0;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.my_event_fragment, container, false);

        GlobalBus.getBus().register(this);

        eventLists = new ArrayList<>();

        assert getArguments() != null;
        id = getArguments().getString("id");
        type = getArguments().getString("type");
        title = getArguments().getString("title");
        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(title);
        }

        progressBar = view.findViewById(R.id.progressbar_myEvent_fragment);
        conNoData = view.findViewById(R.id.con_not_login);
        imageViewData = view.findViewById(R.id.imageView_not_login);
        buttonLogin = view.findViewById(R.id.button_not_login);
        textViewData = view.findViewById(R.id.textView_not_login);
        recyclerView = view.findViewById(R.id.recyclerView_myEvent_fragment);

        data(false, false);
        progressBar.setVisibility(View.GONE);

        onClick = (position, type, title, id) -> startActivity(new Intent(getActivity(), EventDetail.class)
                .putExtra("id", id)
                .putExtra("type", type)
                .putExtra("position", position));
        method = new Method(getActivity(), onClick);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(() -> {
                        paginationIndex++;
                        callData();
                    }, 1000);
                } else {
                    if (eventAdapter != null) {
                        eventAdapter.hideHeader();
                    }
                }
            }
        });


        callData();

        return view;

    }

    @Subscribe
    public void getNotify(Events.Favourite favourite) {
        if (eventAdapter != null) {
            for (int i = 0; i < eventLists.size(); i++) {
                if (eventLists.get(i).getId().equals(favourite.getId())) {
                    eventLists.get(i).setIs_fav(favourite.isIs_fav());
                    eventAdapter.notifyItemChanged(i);
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void data(boolean isShow, boolean isLogin) {
        if (isShow) {
            if (isLogin) {
                buttonLogin.setVisibility(View.VISIBLE);
                textViewData.setText(getResources().getString(R.string.you_have_not_login));
                imageViewData.setImageDrawable(getResources().getDrawable(R.drawable.no_login));
            } else {
                buttonLogin.setVisibility(View.GONE);
                textViewData.setText(getResources().getString(R.string.no_data_found));
                imageViewData.setImageDrawable(getResources().getDrawable(R.drawable.no_data));
            }
            conNoData.setVisibility(View.VISIBLE);
        } else {
            conNoData.setVisibility(View.GONE);
        }
    }

    private void callData() {
        if (method.isNetworkAvailable()) {
            switch (type) {
                case "recentView":
                case "recentView_home":
                case "fav_event":
                    if (method.isLogin()) {
                        eventList(method.userId());
                    } else {
                        data(true, true);
                    }
                    break;
                default:
                    if (method.isLogin()) {
                        eventList(method.userId());
                    } else {
                        eventList("0");
                    }
                    break;
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }


    private void eventList(String userId) {

        if (getActivity() != null) {

            if (eventAdapter == null) {
                eventLists.clear();
                progressBar.setVisibility(View.VISIBLE);
            }

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            switch (type) {
                case "near":
                    jsObj.addProperty("user_lat", Constant.stringLatitude);
                    jsObj.addProperty("user_long", Constant.stringLongitude);
                    jsObj.addProperty("method_name", "get_nearby");
                    break;
                case "search":
                    jsObj.addProperty("event_search", title);
                    jsObj.addProperty("method_name", "search_event");
                    break;
                case "recentView":
                case "recentView_home":
                    jsObj.addProperty("method_name", "recent_views_all");
                    break;
                case "fav_event":
                    jsObj.addProperty("method_name", "get_favourite_list");
                    break;
                default:
                    jsObj.addProperty("cat_id", id);
                    jsObj.addProperty("method_name", "events_by_cat");
                    break;
            }
            jsObj.addProperty("page", paginationIndex);
            jsObj.addProperty("user_id", userId);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<EventRP> call = apiService.getEvent(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<EventRP>() {
                @Override
                public void onResponse(@NotNull Call<EventRP> call, @NotNull Response<EventRP> response) {

                    if (getActivity() != null) {

                        try {
                            EventRP eventRP = response.body();
                            assert eventRP != null;

                            if (eventRP.getStatus().equals("1")) {

                                if (eventRP.getEventLists().size() == 0) {
                                    if (eventAdapter != null) {
                                        eventAdapter.hideHeader();
                                        isOver = true;
                                    }
                                } else {
                                    totalArraySize = totalArraySize + eventRP.getEventLists().size();
                                    for (int i = 0; i < eventRP.getEventLists().size(); i++) {
                                        eventLists.add(eventRP.getEventLists().get(i));

                                        if (Constant.appRP.isNative_ad()) {
                                            int abc = eventLists.lastIndexOf(null);
                                            if (((eventLists.size() - (abc + 1)) % Integer.parseInt(Constant.appRP.getNative_ad_position()) == 0) && (eventRP.getEventLists().size() - 1 != i || totalArraySize != 1000)) {
                                                eventLists.add(null);
                                            }
                                        }
                                    }
                                }

                                if (eventAdapter == null) {
                                    if (eventLists.size() != 0) {
                                        eventAdapter = new EventAdapter(getActivity(), eventLists, "sub_cat", onClick);
                                        recyclerView.setAdapter(eventAdapter);
                                    } else {
                                        data(true, false);
                                    }
                                } else {
                                    eventAdapter.notifyDataSetChanged();
                                }

                            } else if (eventRP.getStatus().equals("2")) {
                                method.suspend(eventRP.getMessage());
                            } else {
                                data(true, false);
                                method.alertBox(eventRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<EventRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure_data", t.toString());
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
