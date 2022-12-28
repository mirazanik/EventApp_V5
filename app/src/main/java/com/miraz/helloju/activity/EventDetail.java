package com.miraz.helloju.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.miraz.helloju.R;
import com.miraz.helloju.adapter.GalleryAdapter;
import com.miraz.helloju.interFace.OnClick;
import com.miraz.helloju.response.DataRP;
import com.miraz.helloju.response.EventDetailRP;
import com.miraz.helloju.response.UserTicketListRP;
import com.miraz.helloju.rest.ApiClient;
import com.miraz.helloju.rest.ApiInterface;
import com.miraz.helloju.util.API;
import com.miraz.helloju.util.Constant;
import com.miraz.helloju.util.Events;
import com.miraz.helloju.util.GlobalBus;
import com.miraz.helloju.util.Method;
import com.rd.PageIndicatorView;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import io.github.lizhangqu.coreprogress.ProgressHelper;
import io.github.lizhangqu.coreprogress.ProgressUIListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EventDetail extends AppCompatActivity {

    private Method method;
    private OnClick onClick;
    private MaterialToolbar toolbar;
    private ProgressBar progressBar;
    private Animation myAnim;
    private Menu menu;
    private WebView webView;
    private int position;
    public String id, type;
    private ViewPager viewPager;
    private boolean isMenu = false;
    private EventDetailRP eventDetailRP;
    private ProgressDialog progressDialog;
    private PageIndicatorView pageIndicatorView;
    private CoordinatorLayout coordinatorLayout;
    private ConstraintLayout conNoData;
    private View view;
    private int REQUEST_CODE_PERMISSION_PDF = 101, REQUEST_CODE_PERMISSION_USERLIST = 102;
    //    private MaterialButton button, buttonUserList, buttonViewTicket, buttonDownloadTicket;
    private ImageView imageView;
    //private ImageView  imageViewLogo, imageViewReport, imageViewFav, imageViewMap, imageViewPhone, imageViewEmail, imageViewWeb;
//    private MaterialTextView textViewTitle, textViewAddress, textViewEventDateTime, textViewRegisterEventDateTime, textViewTitleRemaining, textViewRemaining,
//            textViewPhone, textViewEmail, textViewWeb, textViewPerson, textViewPrice;

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        onClick = (position, type, title, id) ->
        {
            switch (type)
            {
                case "event_gallery":
                    if (eventDetailRP != null)
                    {
                        Constant.galleryLists.clear();
                        Constant.galleryLists.addAll(eventDetailRP.getGalleryLists());
                        startActivity(new Intent(EventDetail.this, GalleryView.class).putExtra("position", position));
                    }
                    break;
                case "event_image":
                    startActivity(new Intent(EventDetail.this, EventImageView.class).putExtra("url", eventDetailRP.getEvent_banner()));
                    break;
                default:
                    startActivity(new Intent(EventDetail.this, EventImageView.class)
                            .putExtra("url", eventDetailRP.getEvent_logo()));
                    break;
            }

        };
        method = new Method(EventDetail.this, onClick);
        method.forceRTLIfSupported();

        GlobalBus.getBus().register(this);

        progressDialog = new ProgressDialog(EventDetail.this);
        myAnim = AnimationUtils.loadAnimation(EventDetail.this, R.anim.bounce);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        type = intent.getStringExtra("type");
        position = intent.getIntExtra("position", 0);

        int columnWidth = method.getScreenWidth();

        toolbar = findViewById(R.id.toolbar_event_detail);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);

        coordinatorLayout = findViewById(R.id.main_content);
        coordinatorLayout.setVisibility(View.GONE);

        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    toolbar.setTitle(getResources().getString(R.string.event_detail));
                    isShow = true;
                } else if (isShow) {
                    toolbar.setTitle("");
                    isShow = false;
                }
            }
        });

        progressBar = findViewById(R.id.progressbar_ed);

        imageView = findViewById(R.id.imageView_ed);
        view = findViewById(R.id.view_ed);
        conNoData = findViewById(R.id.con_noDataFound);
        webView = findViewById(R.id.webView_ed);

//        imageViewLogo = findViewById(R.id.imageView_logo_ed);
//        imageViewReport = findViewById(R.id.imageView_report_ed);
//        imageViewFav = findViewById(R.id.imageView_fav_ed);
//        imageViewMap = findViewById(R.id.imageView_map_ed);
//        imageViewPhone = findViewById(R.id.imageView_phone_ed);
//        imageViewEmail = findViewById(R.id.imageView_email_ed);
//        imageViewWeb = findViewById(R.id.imageView_web_ed);
//        textViewTitle = findViewById(R.id.textView_title_ed);
//        textViewPrice = findViewById(R.id.textView_price_ed);
//        textViewPerson = findViewById(R.id.textView_person_ed);
//        textViewAddress = findViewById(R.id.textView_address_ed);
//        textViewEventDateTime = findViewById(R.id.textView_eventDateTime_ed);
//        textViewRegisterEventDateTime = findViewById(R.id.textView_registerEvent_dateTime_ed);
//        textViewTitleRemaining = findViewById(R.id.textView_titleRemaining_ed);
//        textViewRemaining = findViewById(R.id.textView_remaining_ed);
//        textViewPhone = findViewById(R.id.textView_phone_ed);
//        textViewEmail = findViewById(R.id.textView_email_ed);
//        textViewWeb = findViewById(R.id.textView_web_ed);
//        buttonDownloadTicket = findViewById(R.id.button_downloadTicket_ed);
//        buttonViewTicket = findViewById(R.id.button_viewTicket_ed);
//        buttonUserList = findViewById(R.id.button_userList_ed);
//        button = findViewById(R.id.button_event_booking_ed);

        viewPager = findViewById(R.id.viewpager_ed);
        pageIndicatorView = findViewById(R.id.pageIndicatorView);

        LinearLayout linearLayout = findViewById(R.id.linearLayout_ed);
        method.bannerAd(linearLayout);

        conNoData.setVisibility(View.GONE);
/*        button.setVisibility(View.GONE);

        if (type.equals("book_event")) {
            buttonDownloadTicket.setVisibility(View.VISIBLE);
            buttonViewTicket.setVisibility(View.VISIBLE);
        } else {
            buttonDownloadTicket.setVisibility(View.GONE);
            buttonViewTicket.setVisibility(View.GONE);
        }*/

        imageView.setLayoutParams(new ConstraintLayout.LayoutParams(columnWidth, (int) (columnWidth / 1.5)));
        viewPager.setLayoutParams(new ConstraintLayout.LayoutParams(columnWidth, (int) (columnWidth / 1.5)));
        view.setLayoutParams(new ConstraintLayout.LayoutParams(columnWidth, (int) (columnWidth / 1.5)));

        callData();

    }

    @Subscribe
    public void getNotify(Events.EventUpdateDetail eventUpdateDetail) {
        coordinatorLayout.setVisibility(View.GONE);
        conNoData.setVisibility(View.GONE);
//        button.setVisibility(View.GONE);
        callData();
    }

    @Subscribe
    public void getLogin(Events.Login login) {
        coordinatorLayout.setVisibility(View.GONE);
        conNoData.setVisibility(View.GONE);
//        button.setVisibility(View.GONE);
        callData();
    }

    private void callData() {
        new Handler().postDelayed(() -> {
            if (method.isNetworkAvailable()) {
                if (method.isLogin()) {
                    eventDetail(method.userId(), id);
                } else {
                    eventDetail("0", id);
                }
            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }
        }, 500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_detail, menu);
        this.menu = menu;
        if (!isMenu) {
            isMenu = true;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // action with ID action_refresh was selected
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void eventDetail(String userId, String id) {

        progressBar.setVisibility(View.VISIBLE);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(EventDetail.this));
        jsObj.addProperty("event_id", id);
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("method_name", "single_event");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<EventDetailRP> call = apiService.getEventDetail(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<EventDetailRP>() {
            @SuppressLint({"SetJavaScriptEnabled", "UseCompatLoadingForDrawables"})
            @Override
            public void onResponse(@NotNull Call<EventDetailRP> call, @NotNull Response<EventDetailRP> response) {

                try
                {
                    eventDetailRP = response.body();
                    assert eventDetailRP != null;

                    if (eventDetailRP.getStatus().equals("1"))
                    {

                        if (eventDetailRP.getSuccess().equals("1"))
                        {
//                            if (type.equals("book_event")) {
//                                button.setVisibility(View.GONE);
//                            } else {
//                                if (eventDetailRP.isIs_booking()) {
//                                    button.setVisibility(View.VISIBLE);
//                                } else {
//                                    button.setVisibility(View.GONE);
//                                }
//                            }

//                            if (eventDetailRP.isIs_userList()) {
//                                buttonUserList.setVisibility(View.VISIBLE);
//                                textViewTitleRemaining.setVisibility(View.VISIBLE);
//                                textViewRemaining.setVisibility(View.VISIBLE);
//                                textViewRemaining.setText(eventDetailRP.getRemain_tickets());
//                            } else {
//                                buttonUserList.setVisibility(View.GONE);
//                                textViewTitleRemaining.setVisibility(View.GONE);
//                                textViewRemaining.setVisibility(View.GONE);
//                            }
                            if (isMenu)
                            {

//                                MenuItem share = menu.findItem(R.id.action_share);
//                                share.setOnMenuItemClickListener(item ->
//                                {
//
//                                    Intent intent = new Intent(Intent.ACTION_SEND);
//                                    intent.setType("text/plain");
//                                    intent.putExtra(Intent.EXTRA_TEXT, eventDetailRP.getShare_link());
//                                    startActivity(Intent.createChooser(intent, getResources().getString(R.string.choose_one)));
//
//                                    return false;
//                                });

                                MenuItem more = menu.findItem(R.id.action_more_option);
                                if (eventDetailRP.isIs_userList())
                                {
                                    more.setVisible(true);
                                    MenuItem menuEdit = more.getSubMenu().findItem(R.id.edit_event);
                                    MenuItem menuDelete = more.getSubMenu().findItem(R.id.delete_event);
//                                    menuEdit.setOnMenuItemClickListener(item -> {
//                                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(EventDetail.this, R.style.DialogTitleTextStyle);
//                                        builder.setMessage(getResources().getString(R.string.edit_event_message));
//                                        builder.setCancelable(false);
//                                        builder.setPositiveButton(getResources().getString(R.string.yes),
//                                                (arg0, arg1) -> startActivity(new Intent(EventDetail.this, CreateEvent.class)
//                                                        .putExtra("type", "edit_event")
//                                                        .putExtra("id", eventDetailRP.getId())
//                                                        .putExtra("position", position)));
//                                        builder.setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> {
//
//                                        });
//
//                                        AlertDialog alertDialog = builder.create();
//                                        alertDialog.show();
//                                        return false;
//                                    });
                                    menuDelete.setOnMenuItemClickListener(item ->
                                    {
                                        deleteDialog(eventDetailRP.getId(), position);
                                        return false;
                                    });

                                }
                                else
                                {
                                    more.setVisible(false);
                                }
                            }
//                            if (eventDetailRP.isIs_fav()) {
//                                imageViewFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav_hov));
//                            } else {
//                                imageViewFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav));
//                            }
//
//                            Glide.with(EventDetail.this).load(eventDetailRP.getEvent_logo_thumb())
//                                    .placeholder(R.drawable.placeholder_logo)
//                                    .into(imageViewLogo);
//
//                            imageViewLogo.setOnClickListener(v -> {
//                                method.click(0, "event_logo", "", "");
//                            });
//
//                            textViewTitle.setText(eventDetailRP.getEvent_title());
//                            textViewAddress.setText(eventDetailRP.getEvent_address());
//                            textViewEventDateTime.setText(eventDetailRP.getEvent_date_time());
//                            textViewRegisterEventDateTime.setText(eventDetailRP.getEvent_registration_date_time());
//                            textViewPhone.setText(eventDetailRP.getEvent_phone());
//                            textViewEmail.setText(eventDetailRP.getEvent_email());
//                            textViewWeb.setText(eventDetailRP.getEvent_website());
//                            textViewPrice.setText(eventDetailRP.getTicket_price());
//                            textViewPerson.setText(eventDetailRP.getEvent_ticket());
                            webView.setBackgroundColor(Color.TRANSPARENT);
                            webView.setFocusableInTouchMode(false);
                            webView.setFocusable(false);
                            webView.getSettings().setDefaultTextEncodingName("UTF-8");
                            webView.getSettings().setJavaScriptEnabled(true);
                            String mimeType = "text/html";
                            String encoding = "utf-8";

                            String text = "<html dir=" + method.isWebViewTextRtl() + "><head>" + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/montserrat_regular.ttf\")}body{font-family: MyFont;color: " + method.webViewText() + "line-height:1.6}" + "a {color:" + method.webViewLink() + "text-decoration:none}" + "</style>" + "</head>" + "<body>" + eventDetailRP.getEvent_description() + "</body></html>";

                            webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);

                            if (eventDetailRP.getGalleryLists().size() == 0)
                            {
                                imageView.setVisibility(View.VISIBLE);
                                viewPager.setVisibility(View.GONE);
                                pageIndicatorView.setVisibility(View.GONE);
                                Glide.with(EventDetail.this).load(eventDetailRP.getEvent_banner()).placeholder(R.drawable.placeholder_banner).into(imageView);
                                imageView.setOnClickListener(v ->
                                {
                                    method.click(0, "event_image", "", "");
                                });
                            }
                            else
                            {
                                imageView.setVisibility(View.GONE);
                                viewPager.setVisibility(View.VISIBLE);
                                pageIndicatorView.setVisibility(View.VISIBLE);
                                GalleryAdapter galleryDetailAdapter = new GalleryAdapter(EventDetail.this, eventDetailRP.getGalleryLists(), "event_gallery", onClick);
                                viewPager.setAdapter(galleryDetailAdapter);
                            }

                            coordinatorLayout.setVisibility(View.VISIBLE);

                            /*imageViewReport.setOnClickListener(v -> {
                                if (method.isLogin()) {
                                    BottomSheetDialogFragment bottomSheetDialogFragment = new ReportFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("event_id", id);
                                    bottomSheetDialogFragment.setArguments(bundle);
                                    bottomSheetDialogFragment.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
                                } else {
                                    Method.loginBack = true;
                                    startActivity(new Intent(EventDetail.this, Login.class));
                                }
                            });

                            imageViewFav.setOnClickListener(v -> {
                                if (method.isLogin()) {
                                    method.addToFav(eventDetailRP.getId(), method.userId(), "detail", 0, (isFavourite, message) -> {
                                        if (isFavourite) {
                                            imageViewFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav_hov));
                                        } else {
                                            imageViewFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_fav));
                                        }
                                    });
                                } else {
                                    Method.loginBack = true;
                                    startActivity(new Intent(EventDetail.this, Login.class));
                                }
                            });

                            imageViewWeb.setOnClickListener(v -> {
                                imageViewWeb.startAnimation(myAnim);
                                try {
                                    String url = eventDetailRP.getEvent_website();
                                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                        url = "http://" + url;
                                    }
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    startActivity(browserIntent);
                                } catch (Exception e) {
                                    method.alertBox(getResources().getString(R.string.wrong));
                                }
                            });

                            imageViewPhone.setOnClickListener(v -> {
                                imageViewPhone.startAnimation(myAnim);
                                try {
                                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                    callIntent.setData(Uri.parse("tel:" + eventDetailRP.getEvent_phone()));
                                    startActivity(callIntent);
                                } catch (Exception e) {
                                    method.alertBox(getResources().getString(R.string.wrong));
                                }
                            });

                            imageViewEmail.setOnClickListener(v -> {
                                imageViewEmail.startAnimation(myAnim);
                                try {
                                    Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{eventDetailRP.getEvent_email()});
                                    emailIntent.setData(Uri.parse("mailto:"));
                                    startActivity(emailIntent);
                                } catch (ActivityNotFoundException ex) {
                                    method.alertBox(getResources().getString(R.string.wrong));
                                }

                            });

                            imageViewMap.setOnClickListener(v -> {
                                if (method.isAppInstalled()) {
                                    try {
                                        String latitude = eventDetailRP.getEvent_map_latitude();
                                        String longitude = eventDetailRP.getEvent_map_longitude();
                                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                                Uri.parse("http://maps.google.com/maps?daddr=" + latitude + "," + longitude));
                                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        method.alertBox(getResources().getString(R.string.wrong));
                                    }
                                } else {
                                    method.alertBox(getResources().getString(R.string.map_not_install));
                                }
                            });

                            buttonUserList.setOnClickListener(view -> {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_USERLIST);
                                } else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_USERLIST);
                                    } else {
                                        userList(eventDetailRP.getId());
                                    }
                                }
                            });

                            buttonViewTicket.setOnClickListener(view -> {
                                if (method.isNetworkAvailable()) {
                                    viewTicket(eventDetailRP.getBooking_id());
                                } else {
                                    method.alertBox(getResources().getString(R.string.internet_connection));
                                }
                            });

                            buttonDownloadTicket.setOnClickListener(view -> {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_PDF);
                                } else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_PDF);
                                    } else {
                                        ticketPdf(eventDetailRP.getBooking_id());
                                    }
                                }
                            });

                            button.setOnClickListener(v -> {
                                if (method.isNetworkAvailable()) {
                                    if (method.isLogin()) {
                                        startActivity(new Intent(EventDetail.this, EventBooking.class)
                                                .putExtra("event_id", id));
                                    } else {
                                        Method.loginBack = true;
                                        startActivity(new Intent(EventDetail.this, Login.class));
                                    }
                                } else {
                                    method.alertBox(getResources().getString(R.string.internet_connection));
                                }
                            });*/

                        } else {
                            conNoData.setVisibility(View.VISIBLE);
                            method.alertBox(eventDetailRP.getMsg());
                        }

                    } else if (eventDetailRP.getStatus().equals("2")) {
                        method.suspend(eventDetailRP.getMessage());
                    } else {
                        conNoData.setVisibility(View.VISIBLE);
                        method.alertBox(eventDetailRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<EventDetailRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("onFailure_data", t.toString());
                conNoData.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION_PDF) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (eventDetailRP != null) {
//                    ticketPdf(eventDetailRP.getBooking_id());
                } else {
                    method.alertBox(getResources().getString(R.string.wrong));
                }
            } else {
                method.alertBox(getResources().getString(R.string.storage_permission));
            }
        }
        if (requestCode == REQUEST_CODE_PERMISSION_USERLIST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (eventDetailRP != null) {
                    userList(eventDetailRP.getId());
                } else {
                    method.alertBox(getResources().getString(R.string.wrong));
                }
            } else {
                method.alertBox(getResources().getString(R.string.storage_permission));
            }
        }
    }

    public void deleteDialog(String eventId, int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EventDetail.this, R.style.DialogTitleTextStyle);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setMessage(getResources().getString(R.string.delete_event_message));
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.yes),
                (arg0, arg1) -> {
                    if (method.isNetworkAvailable()) {
                        deleteEvent(eventId, position);
                    } else {
                        method.alertBox(getResources().getString(R.string.internet_connection));
                    }
                });
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.no),
                (dialogInterface, i) -> {

                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }

    public void deleteEvent(String id, int position) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(EventDetail.this));
        jsObj.addProperty("event_id", id);
        jsObj.addProperty("method_name", "delete_event");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<DataRP> call = apiService.deleteEvent(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<DataRP>() {
            @Override
            public void onResponse(@NotNull Call<DataRP> call, @NotNull Response<DataRP> response) {

                try {

                    DataRP dataRP = response.body();

                    assert dataRP != null;
                    if (dataRP.getStatus().equals("1")) {
                        if (dataRP.getSuccess().equals("1")) {
                            Toast.makeText(EventDetail.this, dataRP.getMsg(), Toast.LENGTH_SHORT).show();
                            Events.EventDelete eventDelete = new Events.EventDelete("delete", position);
                            GlobalBus.getBus().post(eventDelete);
                            onBackPressed();
                        }
                    } else {
                        method.alertBox(dataRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<DataRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("onFailure_data", t.toString());

                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

/*    public void viewTicket(String ticketId) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(EventDetail.this));
        jsObj.addProperty("booking_id", ticketId);
        jsObj.addProperty("is_dark", method.isDarkMode());
        jsObj.addProperty("method_name", "view_ticket");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<TicketViewRP> call = apiService.viewTicket(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<TicketViewRP>() {
            @Override
            public void onResponse(@NotNull Call<TicketViewRP> call, @NotNull Response<TicketViewRP> response) {

                try {

                    TicketViewRP ticketViewRP = response.body();

                    assert ticketViewRP != null;
                    if (ticketViewRP.getStatus().equals("1")) {
                        if (ticketViewRP.getSuccess().equals("1")) {
                            startActivity(new Intent(EventDetail.this, ViewTicket.class)
                                    .putExtra("url", ticketViewRP.getUrl()));
                        }
                    } else {
                        method.alertBox(ticketViewRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<TicketViewRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("onFailure_data", t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }*/

/*    public void ticketPdf(String ticketId) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(EventDetail.this));
        jsObj.addProperty("booking_id", ticketId);
        jsObj.addProperty("method_name", "download_ticket");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<TicketDownloadRP> call = apiService.downloadTicket(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<TicketDownloadRP>() {
            @Override
            public void onResponse(@NotNull Call<TicketDownloadRP> call, @NotNull Response<TicketDownloadRP> response) {

                try {

                    TicketDownloadRP ticketDownloadRP = response.body();

                    assert ticketDownloadRP != null;
                    if (ticketDownloadRP.getStatus().equals("1")) {
                        if (ticketDownloadRP.getSuccess().equals("1")) {

                            new CreatePdf(EventDetail.this)
                                    .setPdfName(ticketDownloadRP.getFile_name())
                                    .openPrintDialog(false)
                                    .setContentBaseUrl("file:///android_asset/image/")
                                    .setPageSize(PrintAttributes.MediaSize.ISO_A4)
                                    .setContent(ticketDownloadRP.getString_data())
                                    .setFilePath(Constant.appStorage + "/")
                                    .setCallbackListener(new CreatePdf.PdfCallbackListener() {
                                        @Override
                                        public void onFailure(@NotNull String s) {
                                            // handle error
                                            method.alertBox(getResources().getString(R.string.failed_try_again));
                                        }

                                        @Override
                                        public void onSuccess(@NotNull String s) {
                                            // do your stuff here
                                            showMedia(Constant.appStorage, ticketDownloadRP.getFile_name() + ".pdf");
                                            method.alertBox(getResources().getString(R.string.ticket_download));
                                        }
                                    })
                                    .create();
                        }
                    } else {
                        method.alertBox(ticketDownloadRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<TicketDownloadRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("onFailure_data", t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }*/

    public void userList(String eventId) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(EventDetail.this));
        jsObj.addProperty("event_id", eventId);
        jsObj.addProperty("method_name", "event_user_list");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<UserTicketListRP> call = apiService.getUserTicketList(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<UserTicketListRP>() {
            @Override
            public void onResponse(@NotNull Call<UserTicketListRP> call, @NotNull Response<UserTicketListRP> response) {

                try {

                    UserTicketListRP userTicketListRP = response.body();

                    assert userTicketListRP != null;
                    if (userTicketListRP.getStatus().equals("1")) {
                        if (userTicketListRP.getSuccess().equals("1")) {
                            downloadUserList(userTicketListRP.getUrl(), userTicketListRP.getFile_name());
                        }
                    } else {
                        method.alertBox(userTicketListRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<UserTicketListRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("onFailure_data", t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    private void downloadUserList(String downloadUrl, String fileName) {

        final String CANCEL_TAG = "c_tag";

        progressDialog.setMessage(getResources().getString(R.string.downloading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder()
                .url(downloadUrl)
                .addHeader("Accept-Encoding", "identity")
                .get()
                .tag(CANCEL_TAG);

        okhttp3.Call call = client.newCall(builder.build());

        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                Log.e("TAG", "=============onFailure===============");
                e.printStackTrace();
                Log.d("error_downloading", e.toString());
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                Log.e("TAG", "=============onResponse===============");
                Log.e("TAG", "request headers:" + response.request().headers());
                Log.e("TAG", "response headers:" + response.headers());
                assert response.body() != null;
                ResponseBody responseBody = ProgressHelper.withProgress(response.body(), new ProgressUIListener() {

                    //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                    @Override
                    public void onUIProgressStart(long totalBytes) {
                        super.onUIProgressStart(totalBytes);
                        Log.e("TAG", "onUIProgressStart:" + totalBytes);
                        method.alertBox(getResources().getString(R.string.userList_download));
                    }

                    @Override
                    public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                        Log.e("TAG", "=============start===============");
                        Log.e("TAG", "numBytes:" + numBytes);
                        Log.e("TAG", "totalBytes:" + totalBytes);
                        Log.e("TAG", "percent:" + percent);
                        Log.e("TAG", "speed:" + speed);
                        Log.e("TAG", "============= end ===============");
                    }

                    //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                    @Override
                    public void onUIProgressFinish() {
                        super.onUIProgressFinish();
                        progressDialog.dismiss();
                        showMedia(Constant.appStorage, fileName);
                        Log.e("TAG", "onUIProgressFinish:");
                    }
                });


                try {

                    BufferedSource source = responseBody.source();
                    File outFile = new File(Constant.appStorage + "/" + fileName);
                    BufferedSink sink = Okio.buffer(Okio.sink(outFile));
                    source.readAll(sink);
                    sink.flush();
                    source.close();

                } catch (Exception e) {
                    progressDialog.dismiss();
                    Log.d("show_data", e.toString());
                }

            }
        });

    }

    public void showMedia(String filePath, String fileName) {
        try {
            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{filePath + "/" + fileName},
                    null,
                    (path, uri) -> {

                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the registered event.
        GlobalBus.getBus().unregister(this);
    }
}