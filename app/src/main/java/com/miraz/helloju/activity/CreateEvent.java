package com.miraz.helloju.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;
import com.bumptech.glide.Glide;
import com.miraz.helloju.R;
import com.miraz.helloju.adapter.EventGalleryAdapter;
import com.miraz.helloju.interFace.ImageDelete;
import com.miraz.helloju.item.CategoryList;
import com.miraz.helloju.item.GalleryList;
import com.miraz.helloju.response.CatUploadRP;
import com.miraz.helloju.response.DataRP;
import com.miraz.helloju.response.EditEventRP;
import com.miraz.helloju.response.UploadEventRP;
import com.miraz.helloju.rest.ApiClient;
import com.miraz.helloju.rest.ApiInterface;
import com.miraz.helloju.util.API;
import com.miraz.helloju.util.Events;
import com.miraz.helloju.util.GetPath;
import com.miraz.helloju.util.GlobalBus;
import com.miraz.helloju.util.Method;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEvent extends AppCompatActivity {

    private Method method;
    private int position;
    private InputMethodManager imm;
    private Spinner spinner;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private List<CategoryList> categoryArrayList;
    private List<GalleryList> galleryLists;
    private ImageDelete imageDelete;
    private RecyclerView recyclerView;
    private EventGalleryAdapter eventGalleryAdapter;
    private MaterialButton button, buttonLogin;
    private SwitchMaterial switchMaterial;
    private ConstraintLayout conMain, conStatusCe;
    private MaterialCardView cardViewGallery;
    private TextInputLayout textInputLayoutLocation;
    private ConstraintLayout conNoData;
    private int REQUEST_GALLERY_PERMISSION = 101, REQUEST_BANNER_PERMISSION = 102, REQUEST_LOGO_PERMISSION = 103;
    private int REQUEST_GALLERY_IMAGE_PICKER = 110, REQUEST_BANNER_IMAGE_PICKER = 111, REQUEST_LOGO_IMAGE_PICKER = 112;
    private ImageView imageViewData, imageViewBanner, imageViewLogo, imageViewDate, imageViewTime, imageViewRegisterDate, imageViewRegisterTime;
    private MaterialTextView textViewNotLogin, textViewStatus, textViewDate, textViewTime, textViewSelectDate, textViewSelectTime,
            textViewRegisterEDate, textViewRegisterETime, textViewRegisterESelectDate, textViewRegisterESelectTime;
    private TextInputEditText editTextTitle, editTextEmail, editTextPhoneNo, editTextWebsite, editTextTicket, editTextPersonTicket,
            editTextPrice, editTextDescription, editTextAddress, editTextLocation;
    private String type, eventId, isGetEvent, pathBanner = "", pathLogo = "", categoryId, latitude, longitude, dateStarting, dateEnding, registerDateStarting,
            registerDateEnding, startTime, endTime, registerStartTime, registerEndTime;

    private int calYerStart, calMonthStart, calDayStart, calYerEnd, calMonthEnd, calDayEnd;
    private int calYerStartReg, calMonthStartReg, calDayStartReg, calYerEndReg, calMonthEndReg, calDayEndReg;
    private int calHoursStart, calMinStart, calHoursEnd, calMinEnd;
    private int calHoursStartReg, calMinStartReg, calHoursEndReg, calMinEndReg;

    private boolean isBanner = false, isLogo = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        assert type != null;
        if (type.equals("edit_event")) {
            eventId = intent.getStringExtra("id");
            position = intent.getIntExtra("position", 0);
        }

        method = new Method(CreateEvent.this);
        method.forceRTLIfSupported();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        categoryArrayList = new ArrayList<>();
        galleryLists = new ArrayList<>();

        progressDialog = new ProgressDialog(CreateEvent.this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_ce);
        if (type.equals("edit_event")) {
            toolbar.setTitle(getResources().getString(R.string.edit_event));
        } else {
            toolbar.setTitle(getResources().getString(R.string.create_an_event));
        }
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressBar = findViewById(R.id.progressBar_ce);
        conNoData = findViewById(R.id.con_not_login);
        buttonLogin = findViewById(R.id.button_not_login);
        imageViewData = findViewById(R.id.imageView_not_login);
        textViewNotLogin = findViewById(R.id.textView_not_login);
        conMain = findViewById(R.id.con_main_ce);
        conStatusCe = findViewById(R.id.con_status_ce);
        textViewStatus = findViewById(R.id.textView_status_ce);
        switchMaterial = findViewById(R.id.switch_ce);
        editTextTitle = findViewById(R.id.editText_Title_ce);
        editTextEmail = findViewById(R.id.editText_email_ce);
        editTextPhoneNo = findViewById(R.id.editText_phone_ce);
        editTextWebsite = findViewById(R.id.editText_website_ce);
        editTextTicket = findViewById(R.id.editText_ticket_ce);
        editTextPersonTicket = findViewById(R.id.editText_personTicket_ce);
        editTextPrice = findViewById(R.id.editText_price_ce);
        editTextDescription = findViewById(R.id.editText_des_ce);
        editTextAddress = findViewById(R.id.editText_address_ce);
        editTextLocation = findViewById(R.id.editText_location_ce);
        textInputLayoutLocation = findViewById(R.id.textInput_location_ce);
        imageViewBanner = findViewById(R.id.imageView_banner_ce);
        imageViewLogo = findViewById(R.id.imageView_logo_ce);
        recyclerView = findViewById(R.id.recyclerView_ce);
        cardViewGallery = findViewById(R.id.cardView_gallery_ce);
        spinner = findViewById(R.id.spinner_edit_event);
        button = findViewById(R.id.button_ce);

        if (type.equals("create_event")) {
            conStatusCe.setVisibility(View.GONE);
            button.setText(getResources().getString(R.string.create_event));
        } else {
            button.setText(getResources().getString(R.string.submit));
        }

        conMain.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        data(false, false);

        //event date and time select
        textViewDate = findViewById(R.id.textView_date_ce);
        textViewTime = findViewById(R.id.textView_time_ce);
        textViewSelectDate = findViewById(R.id.textView_select_date_ce);
        textViewSelectTime = findViewById(R.id.textView_select_time_ce);
        imageViewDate = findViewById(R.id.imageView_date_ce);
        imageViewTime = findViewById(R.id.imageView_time_ce);

        //register event date and time textView
        textViewRegisterEDate = findViewById(R.id.textView_registerEDate_ce);
        textViewRegisterETime = findViewById(R.id.textView_registerETime_ce);
        textViewRegisterESelectDate = findViewById(R.id.textView_select_registerEDate_ce);
        textViewRegisterESelectTime = findViewById(R.id.textView_select_registerETime_ce);
        imageViewRegisterDate = findViewById(R.id.imageView_registerEDate_ce);
        imageViewRegisterTime = findViewById(R.id.imageView_registerETime_ce);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CreateEvent.this, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);

        imageDelete = (id, type, position) -> {
            if (id.equals("")) {
                galleryLists.remove(position);
                eventGalleryAdapter.notifyDataSetChanged();
                if (galleryLists.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                }
            } else {
                deleteImage(id, position);
            }
        };

        button.setOnClickListener(v -> submitEvent());

        buttonLogin.setOnClickListener(v -> {
            startActivity(new Intent(CreateEvent.this, Login.class));
            finishAffinity();
        });

        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                if (type.equals("create_event")) {
                    categoryDropDown();
                } else {
                    getEventDetail(eventId);
                }
            } else {
                data(true, true);
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }


    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void data(boolean isShow, boolean isLogin) {
        if (isShow) {
            if (isLogin) {
                buttonLogin.setVisibility(View.VISIBLE);
                textViewNotLogin.setText(getResources().getString(R.string.you_have_not_login));
                imageViewData.setImageDrawable(getResources().getDrawable(R.drawable.no_login));
            } else {
                buttonLogin.setVisibility(View.GONE);
                textViewNotLogin.setText(getResources().getString(R.string.no_data_found));
                imageViewData.setImageDrawable(getResources().getDrawable(R.drawable.no_data));
            }
            conNoData.setVisibility(View.VISIBLE);
        } else {
            conNoData.setVisibility(View.GONE);
        }
    }

    private void clickView() {

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent != null) {
                    if (position == 0) {
                        ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.textView_sub_title_ce));
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.textView_app_color));
                    }
                    categoryId = categoryArrayList.get(position).getCid();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imageViewDate.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            DatePickerDialog.OnDateSetListener onDateSetListener = (view, year, monthOfYear, dayOfMonth, yearEnd, monthOfYearEnd, dayOfMonthEnd) -> {
                String checkDateStarting = method.monthYear(monthOfYear) + "/" + method.dayMonth(dayOfMonth) + "/" + year;
                String checkDateEnding = method.monthYear(monthOfYearEnd) + "/" + method.dayMonth(dayOfMonthEnd) + "/" + yearEnd;
                if (method.checkDatesBefore(checkDateStarting, checkDateEnding)) {
                    dateStarting = method.monthYear(monthOfYear) + "/" + method.dayMonth(dayOfMonth) + "/" + year;
                    dateEnding = method.monthYear(monthOfYearEnd) + "/" + method.dayMonth(dayOfMonthEnd) + "/" + yearEnd;
                    textViewSelectDate.setText(method.userViewDate(dayOfMonth, monthOfYear, year, dayOfMonthEnd, monthOfYearEnd, yearEnd));
                    textViewDate.setTextColor(getResources().getColor(R.color.red));
                } else {
                    method.alertBox(getResources().getString(R.string.please_enter_proper_eventStarting_date));
                }
            };
            DatePickerDialog dpd;
            if (type.equals("create_event")) {
                dpd = DatePickerDialog.newInstance(onDateSetListener,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));
            } else {
                dpd = DatePickerDialog.newInstance(onDateSetListener, calYerStart, calMonthStart, calDayStart,
                        calYerEnd, calMonthEnd, calDayEnd);
            }
            dpd.setMinDate(Calendar.getInstance());
            if (method.isDarkMode()) {
                dpd.setThemeDark(true);
            }
            dpd.show(CreateEvent.this.getFragmentManager(), "Datepickerdialog");
        });

        imageViewTime.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            @SuppressLint("SetTextI18n") TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute, hourOfDayEnd, minuteEnd) -> {
                String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                String minuteString = minute < 10 ? "0" + minute : "" + minute;
                String hourStringEnd = hourOfDayEnd < 10 ? "0" + hourOfDayEnd : "" + hourOfDayEnd;
                String minuteStringEnd = minuteEnd < 10 ? "0" + minuteEnd : "" + minuteEnd;

                textViewTime.setTextColor(getResources().getColor(R.color.red));

                startTime = method.timeFormat(hourString, minuteString);
                endTime = method.timeFormat(hourStringEnd, minuteStringEnd);

                textViewSelectTime.setText(startTime + " " + getResources().getString(R.string.to) + " " + endTime);

            };
            TimePickerDialog tpd;
            if (type.equals("create_event")) {
                tpd = TimePickerDialog.newInstance(timeSetListener, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
            } else {
                tpd = TimePickerDialog.newInstance
                        (timeSetListener, calHoursStart, calMinStart, false, calHoursEnd, calMinEnd);
            }
            tpd.show(CreateEvent.this.getFragmentManager(), "Timepickerdialog");
        });

        //----- register event ----------//

        imageViewRegisterDate.setOnClickListener(view -> {
            Calendar now = Calendar.getInstance();
            DatePickerDialog.OnDateSetListener onDateSetListener = (view1, year, monthOfYear, dayOfMonth, yearEnd, monthOfYearEnd, dayOfMonthEnd) -> {
                String checkRegisterDateStarting = method.monthYear(monthOfYear) + "/" + method.dayMonth(dayOfMonth) + "/" + year;
                String checkRegisterDateEnding = method.monthYear(monthOfYearEnd) + "/" + method.dayMonth(dayOfMonthEnd) + "/" + yearEnd;
                if (method.checkDatesBefore(checkRegisterDateStarting, checkRegisterDateEnding)) {
                    registerDateStarting = method.monthYear(monthOfYear) + "/" + method.dayMonth(dayOfMonth) + "/" + year;
                    registerDateEnding = method.monthYear(monthOfYearEnd) + "/" + method.dayMonth(dayOfMonthEnd) + "/" + yearEnd;
                    textViewRegisterESelectDate.setText(method.userViewDate(dayOfMonth, monthOfYear, year, dayOfMonthEnd, monthOfYearEnd, yearEnd));
                    textViewRegisterEDate.setTextColor(getResources().getColor(R.color.red));
                } else {
                    method.alertBox(getResources().getString(R.string.please_enter_proper_eventRegister_date));
                }
            };
            DatePickerDialog dpd;
            if (type.equals("create_event")) {
                dpd = DatePickerDialog.newInstance(onDateSetListener,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));
            } else {
                dpd = DatePickerDialog.newInstance(onDateSetListener, calYerStartReg, calMonthStartReg, calDayStartReg,
                        calYerEndReg, calMonthEndReg, calDayEndReg);
            }
            dpd.setMinDate(Calendar.getInstance());
            if (method.isDarkMode()) {
                dpd.setThemeDark(true);
            }
            dpd.show(CreateEvent.this.getFragmentManager(), "Datepickerdialog");
        });

        imageViewRegisterTime.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            @SuppressLint("SetTextI18n") TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute, hourOfDayEnd, minuteEnd) -> {

                String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                String minuteString = minute < 10 ? "0" + minute : "" + minute;
                String hourStringEnd = hourOfDayEnd < 10 ? "0" + hourOfDayEnd : "" + hourOfDayEnd;
                String minuteStringEnd = minuteEnd < 10 ? "0" + minuteEnd : "" + minuteEnd;

                textViewRegisterETime.setTextColor(getResources().getColor(R.color.red));

                registerStartTime = method.timeFormat(hourString, minuteString);
                registerEndTime = method.timeFormat(hourStringEnd, minuteStringEnd);

                textViewRegisterESelectTime.setText(registerStartTime + " " + getResources().getString(R.string.to) + " " + registerEndTime);

            };
            TimePickerDialog tpd;
            if (type.equals("create_event")) {
                tpd = TimePickerDialog.newInstance(timeSetListener, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
            } else {
                tpd = TimePickerDialog.newInstance(timeSetListener, calHoursStartReg, calMinStartReg, false, calHoursEndReg, calMinEndReg);
            }
            tpd.show(CreateEvent.this.getFragmentManager(), "Timepickerdialog");
        });

        textInputLayoutLocation.setOnClickListener(v -> latLong());

        editTextLocation.setOnClickListener(v -> latLong());

        imageViewBanner.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_BANNER_PERMISSION);
            } else {
                chooseBannerImage();
            }
        });

        imageViewLogo.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_LOGO_PERMISSION);
            } else {
                chooseLogoImage();
            }
        });

        cardViewGallery.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY_PERMISSION);
            } else {
                chooseGalleryImage();
            }
        });

    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BANNER_IMAGE_PICKER && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            try {
                String filePath = GetPath.getPath(CreateEvent.this, data.getData());
                if (filePath != null) {
                    isBanner = true;
                    pathBanner = filePath;
                    Glide.with(CreateEvent.this)
                            .load(Uri.fromFile(new File(pathBanner)))
                            .placeholder(R.drawable.placeholder_logo).into(imageViewBanner);
                } else {
                    method.alertBox(getResources().getString(R.string.upload_folder_error));
                }
            } catch (Exception e) {
                method.alertBox(getResources().getString(R.string.upload_folder_error));
            }
        }
        if (requestCode == REQUEST_LOGO_IMAGE_PICKER && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            try {
                String filePath = GetPath.getPath(CreateEvent.this, data.getData());
                if (filePath != null) {
                    isLogo = true;
                    pathLogo = filePath;
                    Glide.with(CreateEvent.this)
                            .load(Uri.fromFile(new File(pathLogo)))
                            .placeholder(R.drawable.placeholder_logo).into(imageViewLogo);
                } else {
                    method.alertBox(getResources().getString(R.string.upload_folder_error));
                }
            } catch (Exception e) {
                method.alertBox(getResources().getString(R.string.upload_folder_error));
            }
        }
        if (requestCode == REQUEST_GALLERY_IMAGE_PICKER && resultCode == Activity.RESULT_OK && data != null) {
            try {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        Uri uri = mClipData.getItemAt(i).getUri();
                        String string = GetPath.getPath(CreateEvent.this, uri);
                        galleryLists.add(i, new GalleryList("", string));
                    }
                    setGalleryImage();
                } else if (data.getData() != null) {
                    String string = GetPath.getPath(CreateEvent.this, data.getData());
                    galleryLists.add(0, new GalleryList("", string));
                    setGalleryImage();
                }
            } catch (Exception e) {
                method.alertBox(getResources().getString(R.string.upload_folder_error));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BANNER_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseBannerImage();
            } else {
                method.alertBox(getResources().getString(R.string.storage_permission));
            }
        } else if (requestCode == REQUEST_LOGO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseLogoImage();
            } else {
                method.alertBox(getResources().getString(R.string.storage_permission));
            }
        } else if (requestCode == REQUEST_GALLERY_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseGalleryImage();
            } else {
                method.alertBox(getResources().getString(R.string.storage_permission));
            }
        }
    }

    private void chooseBannerImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_BANNER_IMAGE_PICKER);
    }

    private void chooseLogoImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_LOGO_IMAGE_PICKER);
    }

    private void chooseGalleryImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY_IMAGE_PICKER);
    }

    private void setGalleryImage() {
        if (galleryLists.size() == 0) {
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }
        if (eventGalleryAdapter != null) {
            eventGalleryAdapter.notifyDataSetChanged();
        } else {
            if (galleryLists.size() != 0) {
                eventGalleryAdapter = new EventGalleryAdapter(CreateEvent.this, galleryLists, "upload_event", imageDelete);
                recyclerView.setAdapter(eventGalleryAdapter);
            }
        }
    }

    private void submitEvent() {

        String title = editTextTitle.getText().toString();
        String email = editTextEmail.getText().toString();
        String phoneNo = editTextPhoneNo.getText().toString();
        String website = editTextWebsite.getText().toString();
        String ticket = editTextTicket.getText().toString();
        String personTicket = editTextPersonTicket.getText().toString();
        String price = editTextPrice.getText().toString();
        String description = editTextDescription.getText().toString();
        String address = editTextAddress.getText().toString();

        editTextTitle.setError(null);
        editTextEmail.setError(null);
        editTextPhoneNo.setError(null);
        editTextWebsite.setError(null);
        editTextDescription.setError(null);
        editTextAddress.setError(null);

        if (type.equals("edit_event") && isGetEvent == null) {
            method.alertBox(getResources().getString(R.string.please_select_register));
        } else if (title.equals("") || title.isEmpty()) {
            editTextTitle.requestFocus();
            editTextTitle.setError(getResources().getString(R.string.please_enter_title));
        } else if (!isValidMail(email) || email.isEmpty()) {
            editTextEmail.requestFocus();
            editTextEmail.setError(getResources().getString(R.string.please_enter_email));
        } else if (phoneNo.equals("") || phoneNo.isEmpty()) {
            editTextPhoneNo.requestFocus();
            editTextPhoneNo.setError(getResources().getString(R.string.please_enter_phone));
        } else if (website.equals("") || website.isEmpty()) {
            editTextWebsite.requestFocus();
            editTextWebsite.setError(getResources().getString(R.string.please_enter_website));
        } else if (ticket.equals("") || ticket.isEmpty()) {
            editTextTicket.requestFocus();
            editTextTicket.setError(getResources().getString(R.string.please_enter_ticket));
        } else if (personTicket.equals("") || personTicket.isEmpty()) {
            editTextPersonTicket.requestFocus();
            editTextPersonTicket.setError(getResources().getString(R.string.please_enter_personTicket));
        } else if (Integer.parseInt(personTicket) > Integer.parseInt(ticket)) {
            method.alertBox(getResources().getString(R.string.please_enter_properTicket));
        } else if (price.equals("") || price.isEmpty()) {
            editTextPrice.requestFocus();
            editTextPrice.setError(getResources().getString(R.string.please_enter_price));
        } else if (description.equals("") || description.isEmpty()) {
            editTextDescription.requestFocus();
            editTextDescription.setError(getResources().getString(R.string.please_enter_website));
        } else if (address.equals("") || address.isEmpty()) {
            editTextAddress.requestFocus();
            editTextAddress.setError(getResources().getString(R.string.please_enter_website));
        } else if (latitude == null || latitude.equals("") || latitude.isEmpty()) {
            method.alertBox(getResources().getString(R.string.please_enter_latitude));
        } else if (longitude == null || longitude.equals("") || longitude.isEmpty()) {
            method.alertBox(getResources().getString(R.string.please_enter_longitude));
        } else if (categoryId == null || categoryId.equals("") || categoryId.isEmpty()) {
            method.alertBox(getResources().getString(R.string.please_select_category));
        } else if (pathBanner.equals("") || pathBanner.isEmpty()) {
            method.alertBox(getResources().getString(R.string.please_select_banner));
        } else if (pathLogo.equals("") || pathLogo.isEmpty()) {
            method.alertBox(getResources().getString(R.string.please_select_logo));
        } else if (galleryLists.size() == 0) {
            method.alertBox(getResources().getString(R.string.please_select_gallery));
        } else if (dateStarting == null || dateStarting.equals("") || dateStarting.isEmpty()) {
            method.alertBox(getResources().getString(R.string.please_enter_eventStarting_date));
        } else if (startTime == null || startTime.equals("") || startTime.isEmpty()) {
            method.alertBox(getResources().getString(R.string.please_enter_eventStarting_time));
        } else if (dateEnding == null || dateEnding.equals("") || dateEnding.isEmpty()) {
            method.alertBox(getResources().getString(R.string.please_enter_eventStarting_date));
        } else if (endTime == null || endTime.equals("") || endTime.isEmpty()) {
            method.alertBox(getResources().getString(R.string.please_enter_eventStarting_time));
        } else if (!method.checkDatesBefore(dateStarting, dateEnding)) {
            method.alertBox(getResources().getString(R.string.please_enter_proper_eventStarting_date));
        } else if (registerDateStarting == null || registerDateStarting.equals("") || registerDateStarting.isEmpty()) {
            method.alertBox(getResources().getString(R.string.please_enter_eventRegister_date));
        } else if (registerStartTime == null || registerStartTime.equals("") || registerStartTime.isEmpty()) {
            method.alertBox(getResources().getString(R.string.please_enter_eventRegister_time));
        } else if (registerDateEnding == null || registerDateEnding.equals("") || registerDateEnding.isEmpty()) {
            method.alertBox(getResources().getString(R.string.please_enter_eventRegister_date));
        } else if (registerEndTime == null || registerEndTime.equals("") || registerEndTime.isEmpty()) {
            method.alertBox(getResources().getString(R.string.please_enter_eventRegister_time));
        } else if (!method.checkDatesBefore(registerDateStarting, registerDateEnding)) {
            method.alertBox(getResources().getString(R.string.please_enter_proper_eventRegister_date));
        } else {
            if (method.isLogin()) {
                if (method.isNetworkAvailable()) {

                    editTextTitle.clearFocus();
                    editTextEmail.clearFocus();
                    editTextPhoneNo.clearFocus();
                    editTextWebsite.clearFocus();
                    editTextTicket.clearFocus();
                    editTextPersonTicket.clearFocus();
                    editTextPrice.clearFocus();
                    editTextDescription.clearFocus();
                    editTextAddress.clearFocus();
                    imm.hideSoftInputFromWindow(editTextTitle.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(editTextPhoneNo.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(editTextWebsite.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(editTextTicket.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(editTextPersonTicket.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(editTextPrice.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(editTextDescription.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(editTextAddress.getWindowToken(), 0);

                    textViewDate.setTextColor(getResources().getColor(R.color.textView_sub_title_ce));
                    textViewTime.setTextColor(getResources().getColor(R.color.textView_sub_title_ce));
                    textViewRegisterEDate.setTextColor(getResources().getColor(R.color.textView_sub_title_ce));
                    textViewRegisterETime.setTextColor(getResources().getColor(R.color.textView_sub_title_ce));

                    eventUpload(method.userId(), categoryId, title, description, email, phoneNo,
                            website, ticket, personTicket, price,
                            address, latitude, longitude, dateStarting, startTime,
                            dateEnding, endTime, pathLogo, pathBanner,
                            registerDateStarting, registerStartTime, registerDateEnding, registerEndTime);
                } else {
                    method.alertBox(getResources().getString(R.string.internet_connection));
                }
            } else {
                Method.loginBack = true;
                startActivity(new Intent(CreateEvent.this, Login.class));
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private void latLong() {

        Dialog dialog = new Dialog(CreateEvent.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_location);
        if (method.isRtl()) {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        TextInputEditText editTextLatitude = dialog.findViewById(R.id.editText_latitude_dialog_location);
        TextInputEditText editTextLongitude = dialog.findViewById(R.id.editText_longitude_dialog_location);
        MaterialButton buttonSubmit = dialog.findViewById(R.id.button_dialogBox_lat_long);
        MaterialTextView textView = dialog.findViewById(R.id.textView_find_lat_long);

        if (latitude != null) {
            editTextLatitude.setText(latitude);
        }
        if (longitude != null) {
            editTextLongitude.setText(longitude);
        }

        buttonSubmit.setOnClickListener(v -> {

            String latitude_dialog = editTextLatitude.getText().toString();
            String longitude_dialog = editTextLongitude.getText().toString();

            editTextLatitude.setError(null);
            editTextLongitude.setError(null);

            if (latitude_dialog.equals("") || latitude_dialog.isEmpty()) {
                editTextLatitude.requestFocus();
                editTextLatitude.setError(getResources().getString(R.string.please_enter_latitude));
            } else if (longitude_dialog.equals("") || longitude_dialog.isEmpty()) {
                editTextLongitude.requestFocus();
                editTextLongitude.setError(getResources().getString(R.string.please_enter_longitude));
            } else {

                editTextLatitude.setText(latitude_dialog);
                editTextLongitude.setText(longitude_dialog);

                editTextLatitude.clearFocus();
                editTextLongitude.clearFocus();
                imm.hideSoftInputFromWindow(editTextLatitude.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(editTextLongitude.getWindowToken(), 0);

                latitude = latitude_dialog;
                longitude = longitude_dialog;

                editTextLocation.setText(latitude_dialog + " , " + longitude_dialog);
                dialog.dismiss();

            }
        });

        textView.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.find_location_link))));
            } catch (Exception e) {
                method.alertBox(getResources().getString(R.string.wrong));
            }
        });

        dialog.show();

    }

    private void categoryDropDown() {

        categoryArrayList.clear();
        progressBar.setVisibility(View.VISIBLE);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(CreateEvent.this));
        jsObj.addProperty("method_name", "get_category_upload");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<CatUploadRP> call = apiService.getCatUpload(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<CatUploadRP>() {
            @Override
            public void onResponse(@NotNull Call<CatUploadRP> call, @NotNull Response<CatUploadRP> response) {


                try {
                    CatUploadRP catUploadRP = response.body();
                    assert catUploadRP != null;

                    if (catUploadRP.getStatus().equals("1")) {

                        categoryArrayList.add(new CategoryList("", getResources().getString(R.string.selected_category), "", "", "", "", ""));
                        categoryArrayList.addAll(catUploadRP.getCategoryLists());

                        // Spinner Drop down elements
                        List<String> categories = new ArrayList<>();
                        for (int i = 0; i < categoryArrayList.size(); i++) {
                            categories.add(categoryArrayList.get(i).getCategory_name());
                        }
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(CreateEvent.this, android.R.layout.simple_spinner_item, categories); // Creating adapter for spinner
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);// Drop down layout style - list view with radio button
                        spinner.setAdapter(dataAdapter);// attaching data adapter to spinner

                        clickView();

                        conMain.setVisibility(View.VISIBLE);

                    } else {
                        data(true, false);
                        method.alertBox(catUploadRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<CatUploadRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("onFailure_data", t.toString());
                progressBar.setVisibility(View.GONE);
                data(true, false);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    private void getEventDetail(String eventId) {

        progressBar.setVisibility(View.VISIBLE);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(CreateEvent.this));
        jsObj.addProperty("event_id", eventId);
        jsObj.addProperty("method_name", "get_edit_event");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<EditEventRP> call = apiService.editEventRP(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<EditEventRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<EditEventRP> call, @NotNull Response<EditEventRP> response) {

                try {
                    EditEventRP editEventRP = response.body();
                    assert editEventRP != null;

                    if (editEventRP.getStatus().equals("1")) {

                        if (editEventRP.isIs_event()) {
                            isGetEvent = "true";
                            switchMaterial.setChecked(true);
                            textViewStatus.setTextColor(getResources().getColor(R.color.red));
                            textViewStatus.setText(getResources().getString(R.string.register_close));
                        } else {
                            isGetEvent = "false";
                            switchMaterial.setChecked(false);
                            textViewStatus.setTextColor(getResources().getColor(R.color.green));
                            textViewStatus.setText(getResources().getString(R.string.register_open));
                        }

                        switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            if (isChecked) {
                                isGetEvent = "true";
                                textViewStatus.setTextColor(getResources().getColor(R.color.red));
                                textViewStatus.setText(getResources().getString(R.string.register_close));
                            } else {
                                isGetEvent = "false";
                                textViewStatus.setTextColor(getResources().getColor(R.color.green));
                                textViewStatus.setText(getResources().getString(R.string.register_open));
                            }
                        });

                        categoryArrayList.add(new CategoryList("", getResources().getString(R.string.selected_category), "", "", "", "", ""));
                        categoryArrayList.addAll(editEventRP.getCategoryLists());

                        // Spinner Drop down elements
                        List<String> categories = new ArrayList<>();
                        for (int i = 0; i < categoryArrayList.size(); i++) {
                            categories.add(categoryArrayList.get(i).getCategory_name());
                        }
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(CreateEvent.this, android.R.layout.simple_spinner_item, categories); // Creating adapter for spinner
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);// Drop down layout style - list view with radio button
                        spinner.setAdapter(dataAdapter);// attaching data adapter to spinner

                        for (int i = 0; i < categoryArrayList.size(); i++) {
                            if (editEventRP.getCat_id().equals(categoryArrayList.get(i).getCid())) {
                                spinner.setSelection(i);
                            }
                        }

                        galleryLists.addAll(editEventRP.getGalleryLists());
                        recyclerView.setVisibility(View.VISIBLE);
                        eventGalleryAdapter = new EventGalleryAdapter(CreateEvent.this, galleryLists, "edit_event", imageDelete);
                        recyclerView.setAdapter(eventGalleryAdapter);

                        startTime = editEventRP.getEdit_event_start_time();
                        endTime = editEventRP.getEdit_event_end_time();
                        registerStartTime = editEventRP.getEdit_registration_start_time();
                        registerEndTime = editEventRP.getEdit_registration_end_time();
                        pathLogo = editEventRP.getEvent_logo();
                        pathBanner = editEventRP.getEvent_banner();
                        latitude = editEventRP.getEvent_map_latitude();
                        longitude = editEventRP.getEvent_map_longitude();

                        editTextTitle.setText(editEventRP.getEvent_title());
                        editTextEmail.setText(editEventRP.getEvent_email());
                        editTextPhoneNo.setText(editEventRP.getEvent_phone());
                        editTextWebsite.setText(editEventRP.getEvent_website());
                        editTextTicket.setText(editEventRP.getEvent_ticket());
                        editTextPersonTicket.setText(editEventRP.getPerson_wise_ticket());
                        editTextPrice.setText(editEventRP.getTicket_price());
                        editTextDescription.setText(Html.fromHtml(editEventRP.getEvent_description()));
                        editTextAddress.setText(editEventRP.getEvent_address());
                        editTextLocation.setText(editEventRP.getEvent_map_latitude() + " , " + editEventRP.getEvent_map_longitude());

                        String timeFormat = editEventRP.getEvent_start_time() + " " + getResources().getString(R.string.to) + " " + editEventRP.getEvent_end_time();
                        textViewSelectTime.setText(timeFormat);

                        String timeFormatRegister = editEventRP.getRegistration_start_time() + " " + getResources().getString(R.string.to) + " " + editEventRP.getRegistration_end_time();
                        textViewRegisterESelectTime.setText(timeFormatRegister);

                        if (!editEventRP.getEdit_event_start_time().equals("")) {
                            String[] stringsStartTime = editEventRP.getEdit_event_start_time().split(":");
                            calHoursStart = Integer.parseInt(stringsStartTime[0]);
                            calMinStart = Integer.parseInt(stringsStartTime[1]);
                        }

                        if (!editEventRP.getEdit_event_end_time().equals("")) {
                            String[] stringsEndTime = editEventRP.getEdit_event_end_time().split(":");
                            calHoursEnd = Integer.parseInt(stringsEndTime[0]);
                            calMinEnd = Integer.parseInt(stringsEndTime[1]);
                        }

                        //event data

                        String showDate = editEventRP.getEdit_event_start_date() + " "
                                + getResources().getString(R.string.to) + " "
                                + editEventRP.getEdit_event_end_date();
                        textViewSelectDate.setText(showDate);

                        if (!editEventRP.getEdit_event_start_date().equals("")) {
                            String[] stringsStartDate = editEventRP.getEdit_event_start_date().split("/");
                            String startDay = stringsStartDate[0];
                            String startMont = stringsStartDate[1];
                            String startYear = stringsStartDate[2];
                            dateStarting = startMont + "/" + startDay + "/" + startYear;
                            calYerStart = Integer.parseInt(startYear);
                            calMonthStart = Integer.parseInt(startMont) - 1;
                            calDayStart = Integer.parseInt(startDay);
                        }

                        if (!editEventRP.getEdit_event_end_date().equals("")) {
                            String[] splitEndDate = editEventRP.getEdit_event_end_date().split("/");
                            String endDay = splitEndDate[0];
                            String endMont = splitEndDate[1];
                            String endYear = splitEndDate[2];
                            dateEnding = endMont + "/" + endDay + "/" + endYear;
                            calYerEnd = Integer.parseInt(endYear);
                            calMonthEnd = Integer.parseInt(endMont) - 1;
                            calDayEnd = Integer.parseInt(endDay);
                        }

                        //register start event data

                        String showDateRegister = editEventRP.getEdit_registration_start_date() + " "
                                + getResources().getString(R.string.to) + " "
                                + editEventRP.getEdit_registration_end_date();
                        textViewRegisterESelectDate.setText(showDateRegister);

                        if (!editEventRP.getEdit_registration_start_time().equals("")) {
                            String[] splitStart = editEventRP.getEdit_registration_start_time().split(":");
                            calHoursStartReg = Integer.parseInt(splitStart[0]);
                            calMinStartReg = Integer.parseInt(splitStart[1]);
                        }

                        if (!editEventRP.getEdit_registration_end_time().equals("")) {
                            String[] splitEnd = editEventRP.getEdit_registration_end_time().split(":");
                            calHoursEndReg = Integer.parseInt(splitEnd[0]);
                            calMinEndReg = Integer.parseInt(splitEnd[1]);
                        }

                        if (!editEventRP.getEdit_registration_start_date().equals("")) {
                            String[] splitStartDate = editEventRP.getEdit_registration_start_date().split("/");
                            String startDay = splitStartDate[0];
                            String startMont = splitStartDate[1];
                            String startYear = splitStartDate[2];
                            registerDateStarting = startMont + "/" + startDay + "/" + startYear;
                            calYerStartReg = Integer.parseInt(startYear);
                            calMonthStartReg = Integer.parseInt(startMont) - 1;
                            calDayStartReg = Integer.parseInt(startDay);
                        }

                        if (!editEventRP.getEdit_registration_end_date().equals("")) {
                            String[] splitEndDate = editEventRP.getEdit_registration_end_date().split("/");
                            String endDay = splitEndDate[0];
                            String endMont = splitEndDate[1];
                            String endYear = splitEndDate[2];
                            registerDateEnding = endMont + "/" + endDay + "/" + endYear;
                            calYerEndReg = Integer.parseInt(endYear);
                            calMonthEndReg = Integer.parseInt(endMont) - 1;
                            calDayEndReg = Integer.parseInt(endDay);
                        }

                        Glide.with(CreateEvent.this).load(editEventRP.getEvent_logo_thumb())
                                .placeholder(R.drawable.placeholder_logo)
                                .into(imageViewLogo);
                        Glide.with(CreateEvent.this).load(editEventRP.getEvent_banner_thumb())
                                .placeholder(R.drawable.placeholder_logo)
                                .into(imageViewBanner);

                        clickView();

                        conMain.setVisibility(View.VISIBLE);

                    } else {
                        data(true, false);
                        method.alertBox(editEventRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<EditEventRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("onFailure_data", t.toString());
                progressBar.setVisibility(View.GONE);
                data(true, false);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    private void eventUpload(String userId, String catId, String eventTitle, String eventDescription,
                             String eventEmail, String eventPhone, String eventWebsite,
                             String ticket, String personTicket, String price,
                             String eventAddress, String eventMapLatitude, String eventMapLongitude,
                             String eventStartDate, String eventStartTime, String eventEndDate, String eventEndTime,
                             String event_logo, String event_banner,
                             String registerStartDate, String registerStartTime, String registerEndDate, String registerEndTime) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(CreateEvent.this));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("cat_id", catId);
        jsObj.addProperty("event_title", eventTitle);
        jsObj.addProperty("event_description", eventDescription);
        jsObj.addProperty("event_email", eventEmail);
        jsObj.addProperty("event_phone", eventPhone);
        jsObj.addProperty("event_website", eventWebsite);
        jsObj.addProperty("event_ticket", ticket);
        jsObj.addProperty("person_wise_ticket", personTicket);
        jsObj.addProperty("ticket_price", price);
        jsObj.addProperty("event_address", eventAddress);
        jsObj.addProperty("event_map_latitude", eventMapLatitude);
        jsObj.addProperty("event_map_longitude", eventMapLongitude);
        jsObj.addProperty("event_start_date", eventStartDate);
        jsObj.addProperty("event_start_time", eventStartTime);
        jsObj.addProperty("event_end_date", eventEndDate);
        jsObj.addProperty("event_end_time", eventEndTime);
        jsObj.addProperty("registration_start_date", registerStartDate);
        jsObj.addProperty("registration_start_time", registerStartTime);
        jsObj.addProperty("registration_end_date", registerEndDate);
        jsObj.addProperty("registration_end_time", registerEndTime);
        if (type.equals("create_event")) {
            jsObj.addProperty("method_name", "add_event");
        } else {
            jsObj.addProperty("event_id", eventId);
            jsObj.addProperty("is_event", isGetEvent);
            jsObj.addProperty("method_name", "edit_event");
        }

        MultipartBody.Part body;
        List<MultipartBody.Part> list = new ArrayList<>();
        for (int i = 0; i < galleryLists.size(); i++) {
            if (galleryLists.get(i).getCover_id().equals("")) {
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), new File(galleryLists.get(i).getCover_image()));
                // MultipartBody.Part is used to send also the actual file name
                body = MultipartBody.Part.createFormData("event_cover[]", new File(galleryLists.get(i).getCover_image()).getName(), requestFile);
                list.add(body);
            }
        }

        if (isLogo) {
            RequestBody requestFile_logo = RequestBody.create(MediaType.parse("multipart/form-data"), new File(event_logo));
            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part body_logo = MultipartBody.Part.createFormData("event_logo", new File(event_logo).getName(), requestFile_logo);
            list.add(body_logo);
        }
        if (isBanner) {
            RequestBody requestFile_banner = RequestBody.create(MediaType.parse("multipart/form-data"), new File(event_banner));
            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part body_banner = MultipartBody.Part.createFormData("event_banner", new File(event_banner).getName(), requestFile_banner);
            list.add(body_banner);
        }

        RequestBody requestBody_data = RequestBody.create(MediaType.parse("multipart/form-data"), API.toBase64(jsObj.toString()));

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<UploadEventRP> call = apiService.uploadEvent(requestBody_data, list);
        call.enqueue(new Callback<UploadEventRP>() {
            @Override
            public void onResponse(@NotNull Call<UploadEventRP> call, @NotNull Response<UploadEventRP> response) {

                try {
                    UploadEventRP uploadEventRP = response.body();
                    assert uploadEventRP != null;

                    if (uploadEventRP.getStatus().equals("1")) {

                        editTextTitle.setText("");
                        editTextEmail.setText("");
                        editTextPhoneNo.setText("");
                        editTextWebsite.setText("");
                        editTextTicket.setText("");
                        editTextPersonTicket.setText("");
                        editTextPrice.setText("");
                        editTextDescription.setText("");
                        editTextAddress.setText("");
                        textViewSelectDate.setText("");
                        textViewSelectTime.setText("");
                        textViewRegisterESelectDate.setText("");
                        textViewRegisterESelectTime.setText("");

                        Glide.with(CreateEvent.this)
                                .load(R.drawable.placeholder_logo)
                                .into(imageViewBanner);
                        Glide.with(CreateEvent.this)
                                .load(R.drawable.placeholder_logo)
                                .into(imageViewLogo);
                        galleryLists.clear();
                        pathBanner = "";
                        pathLogo = "";
                        latitude = "";
                        longitude = "";
                        dateStarting = "";
                        dateEnding = "";
                        startTime = "";
                        endTime = "";
                        registerDateStarting = "";
                        registerDateEnding = "";
                        CreateEvent.this.registerStartTime = "";
                        CreateEvent.this.registerEndTime = "";
                        categoryId = getResources().getString(R.string.selected_category);
                        spinner.setSelection(0);
                        recyclerView.setVisibility(View.GONE);
                        eventGalleryAdapter.notifyDataSetChanged();

                        if (type.equals("edit_event")) {

                            Toast.makeText(CreateEvent.this, uploadEventRP.getMsg(), Toast.LENGTH_SHORT).show();

                            Events.EventUpdateDetail eventUpdateDetail = new Events.EventUpdateDetail("");
                            GlobalBus.getBus().post(eventUpdateDetail);

                            Events.EventUpdate eventUpdate = new Events.EventUpdate(eventId, eventTitle, uploadEventRP.getEvent_date(),
                                    uploadEventRP.getEvent_banner_thumb(), uploadEventRP.getEvent_address(), position);
                            GlobalBus.getBus().post(eventUpdate);

                            onBackPressed();

                        } else {
                            method.alertBox(uploadEventRP.getMsg());
                        }

                    } else if (uploadEventRP.getStatus().equals("2")) {
                        method.suspend(uploadEventRP.getMessage());
                    } else {
                        method.alertBox(uploadEventRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<UploadEventRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("onFailure_data", t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    private void deleteImage(String id, int position) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(CreateEvent.this));
        jsObj.addProperty("image_id", id);
        jsObj.addProperty("method_name", "remove_cover");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<DataRP> call = apiService.removeGalleryImage(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<DataRP>() {
            @Override
            public void onResponse(@NotNull Call<DataRP> call, @NotNull Response<DataRP> response) {

                try {
                    DataRP dataRP = response.body();
                    assert dataRP != null;

                    if (dataRP.getStatus().equals("1")) {
                        galleryLists.remove(position);
                        eventGalleryAdapter.notifyDataSetChanged();
                        if (galleryLists.size() == 0) {
                            recyclerView.setVisibility(View.GONE);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
