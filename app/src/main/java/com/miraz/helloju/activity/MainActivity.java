package com.miraz.helloju.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.miraz.helloju.BuildConfig;
import com.miraz.helloju.R;
import com.miraz.helloju.fragment.CategoryFragment;
import com.miraz.helloju.fragment.EventFragment;
import com.miraz.helloju.fragment.HomeFragment;
import com.miraz.helloju.fragment.MyEventFragment;
import com.miraz.helloju.response.AppRP;
import com.miraz.helloju.rest.ApiClient;
import com.miraz.helloju.rest.ApiInterface;
import com.miraz.helloju.util.API;
import com.miraz.helloju.util.Constant;
import com.miraz.helloju.util.Events;
import com.miraz.helloju.util.GlobalBus;
import com.miraz.helloju.util.Method;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Method method;
    private String id, type = "", title;
    private DrawerLayout drawer;
    public static MaterialToolbar toolbar;
    private NavigationView navigationView;
    private LinearLayout linearLayout;
    private ConsentForm form;
    private ProgressBar progressBar;
    private boolean isAdMOb = false;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GlobalBus.getBus().register(this);

        method = new Method(MainActivity.this);
        method.forceRTLIfSupported();

        if (getIntent().hasExtra("id")) {
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            type = getIntent().getStringExtra("type");
        }

        toolbar = findViewById(R.id.toolbar_main);
        progressBar = findViewById(R.id.progressBar_main);
        linearLayout = findViewById(R.id.linearLayout_main);

        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationIcon(R.drawable.ic_side_nav);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        checkLogin();

        progressBar.setVisibility(View.GONE);


        Drawable drawer;
        if (method.isDarkMode()) {
            drawer = getResources().getDrawable(R.drawable.ic_upload_dark);
        } else {
            drawer = getResources().getDrawable(R.drawable.ic_upload);
        }



        if (method.isNetworkAvailable()) {
            appDetail();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    public void unCheck() {
        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    public void selectDrawerItem(int position) {
        navigationView.getMenu().getItem(position).setChecked(true);
    }

    public void deselectDrawerItem(int position) {
        navigationView.getMenu().getItem(position).setCheckable(false);
        navigationView.getMenu().getItem(position).setChecked(false);
    }

    public void backStackRemove() {
        for (int i = 0; i < getSupportFragmentManager().getFragments().size(); i++) {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
            } else {
                if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
                    String title = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount() - 1).getTag();
                    if (title != null) {
                        toolbar.setTitle(title);
                    }
                    super.onBackPressed();
                } else {
                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, getResources().getString(R.string.Please_click_BACK_again_to_exit), Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                }
            }
        }
    }

    //--------------Get Location permission-----------------//

    public void appDetail() {

        progressBar.setVisibility(View.VISIBLE);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(MainActivity.this));
        jsObj.addProperty("method_name", "get_app_details");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<AppRP> call = apiService.getAppData(API.toBase64(jsObj.toString()));


        call.enqueue(new Callback<AppRP>() {
            @Override
            public void onResponse(@NotNull Call<AppRP> call, @NotNull Response<AppRP> response) {
                Log.e("miraz", response.body().toString());
                try {
                    Constant.appRP = response.body();
                    assert Constant.appRP != null;
                    method.initializeAds();
                    if (Constant.appRP.getStatus().equals("1")) {
                        if (Constant.appRP.getApp_update_status().equals("true") && Constant.appRP.getApp_new_version() > BuildConfig.VERSION_CODE) {
                            showAdDialog(Constant.appRP.getApp_update_desc(),
                                    Constant.appRP.getApp_redirect_url(),
                                    Constant.appRP.getCancel_update_status());
                        }
                        if (Constant.appRP.getInterstitial_ad_click().equals("")) {
                            Constant.AD_COUNT_SHOW = 0;
                        } else {
                            Constant.AD_COUNT_SHOW = Integer.parseInt(Constant.appRP.getInterstitial_ad_click());
                        }


//                        if (Constant.appRP.isBanner_ad() || Constant.appRP.isInterstitial_ad()) {
//                            if (getBannerAdType() || Constant.appRP.getInterstitial_ad_type().equals("admob")) {
//                                if (getBannerAdType() && Constant.appRP.isBanner_ad()) {
//                                    isAdMOb = true;
//                                }
//                                if (!getBannerAdType() && Constant.appRP.isBanner_ad()) {
//                                    method.fbBannerAd(linearLayout);
//                                } else {
//                                    if (!Constant.appRP.isBanner_ad()) {
//                                        linearLayout.setVisibility(View.GONE);
//                                    }
//                                }
//                                  checkForConsent();
//                            } else {
//                                if (Constant.appRP.isBanner_ad()) {
//                                    method.fbBannerAd(linearLayout);
//                                } else {
//                                    linearLayout.setVisibility(View.GONE);
//                                }
//                            }
//                        } else {
//                            linearLayout.setVisibility(View.GONE);
//                        }

                        if(method.isAdmobFBAds()) {
                            checkForConsent();
                        } else {
                            method.bannerAd(linearLayout);
                        }

                        try {

                            if (type.equals("category")) {
                                EventFragment subCategoryFragment = new EventFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("id", id);
                                bundle.putString("type", type);
                                bundle.putString("title", title);
                                subCategoryFragment.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.frameLayout_main, subCategoryFragment, title)
                                        .commitAllowingStateLoss();
                            } else {
                                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new HomeFragment(),
                                        getResources().getString(R.string.home)).commitAllowingStateLoss();

                                selectDrawerItem(0);
//                                bottomNavBar.setCurrentItem(0);
                            }


                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.wrong),
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        method.alertBox(Constant.appRP.getMessage());
                    }

                } catch (Exception e) {
//                    Log.d("exception_error", e.toString());
//                    method.alertBox(getResources().getString(R.string.failed_try_again));


                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new HomeFragment(),
                            getResources().getString(R.string.home)).commitAllowingStateLoss();

                    selectDrawerItem(0);
//                    bottomNavBar.setCurrentItem(0);
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<AppRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("error_fail", t.toString());
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //Checking if the item is in checked state or not, if not make it in checked state
        item.setChecked(!item.isChecked());

        //Closing drawer on item click
        drawer.closeDrawers();

        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.home:
                backStackRemove();
                selectDrawerItem(0);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new HomeFragment(),
                        getResources().getString(R.string.home)).commitAllowingStateLoss();
                return true;

            case R.id.my_event:
                backStackRemove();
                selectDrawerItem(1);
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new MyEventFragment(),
                        getResources().getString(R.string.my_event)).commitAllowingStateLoss();
                return true;


            case R.id.favourite_event:
                backStackRemove();
                selectDrawerItem(3);
                callEvent("fav_event", getResources().getString(R.string.favourite_event));
                return true;

            case R.id.recent_view_event:
                backStackRemove();
                selectDrawerItem(4);
                callEvent("recentView", getResources().getString(R.string.recently_view_event));
                return true;

            default:
                return true;
        }
    }

    private void callEvent(String type, String title) {

        EventFragment eventFragment = new EventFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", "");
        bundle.putString("type", type);
        bundle.putString("title", title);
        eventFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout_main, eventFragment, title)
                .commitAllowingStateLoss();

    }


    public void checkForConsent() {

        ConsentInformation consentInformation = ConsentInformation.getInstance(MainActivity.this);
        String[] publisherIds = {Constant.appRP.getPublisher_id()};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                Log.d("consentStatus", consentStatus.toString());
                // User's consent status successfully updated.
                switch (consentStatus) {
                    case PERSONALIZED:
                        Method.personalizationAd = true;
                        method.bannerAd(linearLayout);
                        break;
                    case NON_PERSONALIZED:
                        Method.personalizationAd = false;
                        method.bannerAd(linearLayout);
                        break;
                    case UNKNOWN:
                        if (ConsentInformation.getInstance(getBaseContext()).isRequestLocationInEeaOrUnknown()) {
                            requestConsent();
                        } else {
                            Method.personalizationAd = true;
                            method.bannerAd(linearLayout);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
            }
        });

    }

    public void requestConsent() {
        URL privacyUrl = null;
        try {
            // TODO: Replace with your app's privacy policy URL.
            privacyUrl = new URL(Constant.appRP.getPrivacy_policy_link());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Handle error.
        }
        form = new ConsentForm.Builder(MainActivity.this, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        showForm();
                        // Consent form loaded successfully.
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.
                    }

                    @Override
                    public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        Log.d("consentStatus_form", consentStatus.toString());
                        switch (consentStatus) {
                            case PERSONALIZED:
                                Method.personalizationAd = true;
                                method.bannerAd(linearLayout);
                                break;
                            case NON_PERSONALIZED:
                            case UNKNOWN:
                                Method.personalizationAd = false;
                                method.bannerAd(linearLayout);
                                break;
                        }
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        Log.d("errorDescription", errorDescription);
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .build();
        form.load();
    }

    private void showForm() {
        if (form != null) {
            form.show();
        }
    }

    @Subscribe
    public void getLogin(Events.Login login) {
        if (method != null) {
            checkLogin();
        }
    }

    private void checkLogin() {
        if (method.isLogin()) {
            navigationView.getMenu().getItem(7).setIcon(R.drawable.ic_logout);
            navigationView.getMenu().getItem(7).setTitle(getResources().getString(R.string.logout));
        } else {
            navigationView.getMenu().getItem(7).setIcon(R.drawable.ic_login);
            navigationView.getMenu().getItem(7).setTitle(getResources().getString(R.string.login));
        }
    }


    private void showAdDialog(String description, String link, String isCancel) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_app);
        dialog.setCancelable(false);
        if (method.isRtl()) {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        MaterialTextView textView_description = dialog.findViewById(R.id.textView_description_dialog_update);
        MaterialButton buttonUpdate = dialog.findViewById(R.id.button_update_dialog_update);
        MaterialButton buttonCancel = dialog.findViewById(R.id.button_cancel_dialog_update);

        if (isCancel.equals("true")) {
            buttonCancel.setVisibility(View.VISIBLE);
        } else {
            buttonCancel.setVisibility(View.GONE);
        }
        textView_description.setText(description);

        buttonUpdate.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
            dialog.dismiss();
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        GlobalBus.getBus().unregister(this);
        super.onDestroy();
    }

}
