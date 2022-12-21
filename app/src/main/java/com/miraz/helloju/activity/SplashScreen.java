package com.miraz.helloju.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.miraz.helloju.R;
import com.miraz.helloju.response.LoginRP;
import com.miraz.helloju.rest.ApiClient;
import com.miraz.helloju.rest.ApiInterface;
import com.miraz.helloju.util.API;
import com.miraz.helloju.util.Constant;
import com.miraz.helloju.util.Method;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity implements LocationListener {

    private Method method;
    private ProgressBar progressBar;
    private Boolean isCancelled = false;
    private String id = "0", title, type = "";
    private final int REQUEST_LOCATION = 199;
    private int REQUEST_CODE_PERMISSION = 101;
    private LocationManager locationManager;

    //Google login
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        method = new Method(SplashScreen.this);
        method.forceRTLIfSupported();
        method.login();
        switch (method.themMode()) {
            case "system":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                break;
        }

        // Making notification bar transparent
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        method.changeStatusBarColor();
        setContentView(R.layout.activity_splace_screen);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        progressBar = findViewById(R.id.progressBar_splash);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("id")) {
                id = intent.getStringExtra("id");
                title = intent.getStringExtra("title");
                type = intent.getStringExtra("type");
            } else {
                Uri data = intent.getData();
                if (data != null) {
                    String[] strings = data.toString().split("event_id=");
                    id = strings[strings.length - 1];
                    type = "deep_link";
                }
            }
        }

        if (!isCancelled) {
            if (!location()) {
                enableLoc();
            } else {
                locationPermission();
            }
        }


    }

    //--------------Get Location permission-----------------//

    private void locationPermission() {
        int WAIT = 1000;
        new Handler().postDelayed(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSION);
            } else {
                if (method.isNetworkAvailable()) {
                    getLocation();
                } else {
                    alertBox(getResources().getString(R.string.internet_connection));
                }
            }
        }, WAIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_LOCATION) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    locationPermission();
                    break;
                case Activity.RESULT_CANCELED:
                    alertBox(getResources().getString(R.string.please_location));
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (method.isNetworkAvailable()) {
                    getLocation();
                } else {
                    alertBox(getResources().getString(R.string.internet_connection));
                }
            } else {
                alertBox(getResources().getString(R.string.please_allow_location));
            }
        }
    }


    //--------------Get Location-----------------//

    public void getLocation() {
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1, this);
        } catch (SecurityException e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
            alertBox(getResources().getString(R.string.wrong));
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        Constant.stringLatitude = String.valueOf(location.getLatitude());
        Constant.stringLongitude = String.valueOf(location.getLongitude());

        removeUpdateLocation();
        progressBar.setVisibility(View.GONE);

        splash();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("app_data", "");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("app_data", "");
    }


    @Override
    public void onProviderDisabled(String provider) {
        progressBar.setVisibility(View.GONE);
        alertBox(getResources().getString(R.string.please_location));
    }

    private void removeUpdateLocation() {
        locationManager.removeUpdates(this);
    }

    //--------------Get Location-----------------//

    public void splash() {
        if (method.isLogin()) {
            login(method.userId(), method.getLoginType());
        } else {
            callActivity();
        }
    }

    public void login(String userId, String type) {

        progressBar.setVisibility(View.VISIBLE);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(SplashScreen.this));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("method_name", "user_status");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<LoginRP> call = apiService.getLoginDetail(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<LoginRP>() {
            @Override
            public void onResponse(@NotNull Call<LoginRP> call, @NotNull Response<LoginRP> response) {

                try {
                    LoginRP loginRP = response.body();
                    assert loginRP != null;

                    if (loginRP.getStatus().equals("1")) {

                        if (loginRP.getSuccess().equals("1")) {

                            if (type.equals("google")) {
                                if (GoogleSignIn.getLastSignedInAccount(SplashScreen.this) != null) {
                                    callActivity();
                                } else {
                                    method.editor.putBoolean(method.pref_login, false);
                                    method.editor.commit();
                                    startActivity(new Intent(SplashScreen.this, Login.class));
                                    finishAffinity();
                                }
                            } else if (type.equals("facebook")) {

                                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

                                if (isLoggedIn) {
                                    callActivity();
                                } else {

                                    LoginManager.getInstance().logOut();

                                    method.editor.putBoolean(method.pref_login, false);
                                    method.editor.commit();
                                    startActivity(new Intent(SplashScreen.this, Login.class));
                                    finishAffinity();

                                }

                            } else {
                                callActivity();
                            }
                        } else {

                            if (type.equals("google")) {

                                mGoogleSignInClient.signOut()
                                        .addOnCompleteListener(SplashScreen.this, task -> {

                                        });

                            } else if (type.equals("facebook")) {
                                LoginManager.getInstance().logOut();
                            }

                            Toast.makeText(SplashScreen.this, loginRP.getMsg(), Toast.LENGTH_SHORT).show();

                            method.editor.putBoolean(method.pref_login, false);
                            method.editor.commit();
                            startActivity(new Intent(SplashScreen.this, Login.class));
                            finishAffinity();
                        }

                    } else {
                        method.alertBox(loginRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<LoginRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    public void callActivity() {

        if (!isCancelled) {
            if (!type.equals("")) {
                if (type.equals("category")) {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class)
                            .putExtra("id", id)
                            .putExtra("title", title)
                            .putExtra("type", type));
                } else {
                    startActivity(new Intent(SplashScreen.this, EventDetail.class)
                            .putExtra("id", id)
                            .putExtra("type", type)
                            .putExtra("position", 0));
                }
            } else {
                if (method.pref.getBoolean(method.show_login, true)) {
                    method.editor.putBoolean(method.show_login, false);
                    method.editor.commit();
                    startActivity(new Intent(SplashScreen.this, Login.class));
                } else {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                }
            }
            finishAffinity();
        }

    }

    //alert message box
    public void alertBox(String message) {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SplashScreen.this, R.style.DialogTitleTextStyle);
        builder.setMessage(Html.fromHtml(message));
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.ok),
                (arg0, arg1) -> finishAffinity());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    //--------------Location---------//

    public boolean location() {

        // Todo Location Already on  ... start
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice()) {
            Log.e("location", "Gps already enabled");
            return true;
        } else {
            // Todo Location Already on  ... end
            return false;
        }

    }

    private boolean hasGPSDevice() {
        if (locationManager == null)
            return false;
        final List<String> providers = locationManager.getAllProviders();
        return providers.contains(LocationManager.NETWORK_PROVIDER);
    }

    public void enableLoc() {

        final int REQUEST_LOCATION = 199;

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        SettingsClient client = LocationServices.getSettingsClient(SplashScreen.this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(SplashScreen.this, locationSettingsResponse -> {
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
            Log.d("location_enable", "enable");
        });

        task.addOnFailureListener(SplashScreen.this, e -> {
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(SplashScreen.this, REQUEST_LOCATION);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });

    }


    //--------------Location---------//

    @Override
    protected void onDestroy() {
        isCancelled = true;
        super.onDestroy();
    }

}
