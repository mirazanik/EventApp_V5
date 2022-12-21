package com.miraz.helloju.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.miraz.helloju.R;
import com.miraz.helloju.response.GetTicketRP;
import com.miraz.helloju.response.TicketBookRP;
import com.miraz.helloju.rest.ApiClient;
import com.miraz.helloju.rest.ApiInterface;
import com.miraz.helloju.util.API;
import com.miraz.helloju.util.Method;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventBooking extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Method method;
    private MaterialToolbar toolbar;
    private MaterialButton button;
    private AppCompatSpinner spinner;
    private InputMethodManager imm;
    private ArrayList<String> arrayTicket;
    private ProgressDialog progressDialog;
    private String eventId, ticketPerson;
    private ConstraintLayout con, conNoData;
    private TextInputEditText editTextName, editTextEmail, editTextPhone, editTextMsg;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_booking);

        method = new Method(EventBooking.this);
        method.forceRTLIfSupported();

        arrayTicket = new ArrayList<>();

        Intent intent = getIntent();
        eventId = intent.getStringExtra("event_id");

        toolbar = findViewById(R.id.toolbar_event_booking);
        toolbar.setTitle(getResources().getString(R.string.booking_event));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        progressDialog = new ProgressDialog(EventBooking.this);

        con = findViewById(R.id.con_event_booking);
        conNoData = findViewById(R.id.con_noDataFound);
        editTextName = findViewById(R.id.editText_name_event_booking);
        editTextEmail = findViewById(R.id.editText_email_event_booking);
        editTextPhone = findViewById(R.id.editText_phoNo_event_booking);
        editTextMsg = findViewById(R.id.editText_message_event_booking);
        spinner = findViewById(R.id.spinner_event_booking);
        button = findViewById(R.id.button_event_booking);

        LinearLayout linearLayout = findViewById(R.id.linearLayout_event_booking);
        method.bannerAd(linearLayout);

        con.setVisibility(View.GONE);
        conNoData.setVisibility(View.GONE);

        spinner.setOnItemSelectedListener(this);

        if (method.isNetworkAvailable()) {
            userSelection(method.userId());
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void submit() {

        String name = editTextName.getText().toString();
        String email = editTextEmail.getText().toString();
        String phone = editTextPhone.getText().toString();
        String msg = editTextMsg.getText().toString();

        editTextName.setError(null);
        editTextEmail.setError(null);
        editTextPhone.setError(null);
        editTextMsg.setError(null);

        if (name.equals("") || name.isEmpty()) {
            editTextName.requestFocus();
            editTextName.setError(getResources().getString(R.string.please_enter_name));
        } else if (!isValidMail(email) || email.isEmpty()) {
            editTextEmail.requestFocus();
            editTextEmail.setError(getResources().getString(R.string.please_enter_email));
        } else if (phone.equals("") || phone.isEmpty()) {
            editTextPhone.requestFocus();
            editTextPhone.setError(getResources().getString(R.string.please_enter_phone));
        } else if (msg.equals("") || msg.isEmpty()) {
            editTextMsg.requestFocus();
            editTextMsg.setError(getResources().getString(R.string.please_enter_phone));
        } else if (ticketPerson.isEmpty() || ticketPerson.equals(getResources().getString(R.string.please_select))) {
            method.alertBox(getResources().getString(R.string.please_select_ticket));
        } else {

            editTextName.clearFocus();
            editTextEmail.clearFocus();
            editTextPhone.clearFocus();
            editTextMsg.clearFocus();
            imm.hideSoftInputFromWindow(editTextName.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextPhone.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextMsg.getWindowToken(), 0);

            if (method.isNetworkAvailable()) {
                booking(method.userId(), eventId, name, email, phone, ticketPerson, msg);
            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }

        }

    }

    public void userSelection(String userId) {

        arrayTicket.clear();

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(EventBooking.this));
        jsObj.addProperty("event_id", eventId);
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("method_name", "get_tickets");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<GetTicketRP> call = apiService.getEventTicket(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<GetTicketRP>() {
            @Override
            public void onResponse(@NotNull Call<GetTicketRP> call, @NotNull Response<GetTicketRP> response) {

                try {
                    GetTicketRP getTicketRP = response.body();
                    assert getTicketRP != null;

                    if (getTicketRP.getStatus().equals("1")) {

                        arrayTicket.add(getResources().getString(R.string.please_select));

                        for (int i = 1; i <= getTicketRP.getPerson_wise_ticket(); i++) {
                            arrayTicket.add(String.valueOf(i));
                        }

                        editTextName.setText(getTicketRP.getName());
                        editTextEmail.setText(getTicketRP.getEmail());
                        editTextPhone.setText(getTicketRP.getPhone());

                        //Creating the ArrayAdapter instance having the country list
                        ArrayAdapter adapter = new ArrayAdapter(EventBooking.this, android.R.layout.simple_spinner_item, arrayTicket);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //Setting the ArrayAdapter data on the Spinner
                        spinner.setAdapter(adapter);

                        button.setOnClickListener(v -> submit());

                        con.setVisibility(View.VISIBLE);

                    } else if (getTicketRP.getStatus().equals("2")) {
                        method.suspend(getTicketRP.getMessage());
                    } else {
                        conNoData.setVisibility(View.VISIBLE);
                        method.alertBox(getTicketRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<GetTicketRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("onFailure_data", t.toString());
                progressDialog.dismiss();
                conNoData.setVisibility(View.VISIBLE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    public void booking(String userId, String eventId, String name, String email, String phone, String ticket, String msg) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(EventBooking.this));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("event_id", eventId);
        jsObj.addProperty("user_name", name);
        jsObj.addProperty("user_email", email);
        jsObj.addProperty("user_phone", phone);
        jsObj.addProperty("total_tickets", ticket);
        jsObj.addProperty("user_message", msg);
        jsObj.addProperty("method_name", "event_booking");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<TicketBookRP> call = apiService.bookingEvent(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<TicketBookRP>() {
            @Override
            public void onResponse(@NotNull Call<TicketBookRP> call, @NotNull Response<TicketBookRP> response) {

                try {
                    TicketBookRP ticketBookRP = response.body();
                    assert ticketBookRP != null;

                    if (ticketBookRP.getStatus().equals("1")) {

                        if (ticketBookRP.getSuccess().equals("1")) {

                            arrayTicket.clear();
                            arrayTicket.add(getResources().getString(R.string.please_select));

                            for (int i = 1; i <= ticketBookRP.getPerson_wise_ticket(); i++) {
                                arrayTicket.add(String.valueOf(i));
                            }

                            //Creating the ArrayAdapter instance having the country list
                            ArrayAdapter adapter = new ArrayAdapter(EventBooking.this, android.R.layout.simple_spinner_item, arrayTicket);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            spinner.setAdapter(adapter);
                            spinner.setSelection(0);

                            editTextMsg.setText("");
                            ticketPerson = getResources().getString(R.string.please_select);

                        }
                        method.alertBox(ticketBookRP.getMsg());

                    } else if (ticketBookRP.getStatus().equals("2")) {
                        method.suspend(ticketBookRP.getMessage());
                    } else {
                        method.alertBox(ticketBookRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<TicketBookRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("onFailure_data", t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ticketPerson = arrayTicket.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
