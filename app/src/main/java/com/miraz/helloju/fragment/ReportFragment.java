package com.miraz.helloju.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.miraz.helloju.R;
import com.miraz.helloju.response.DataRP;
import com.miraz.helloju.rest.ApiClient;
import com.miraz.helloju.rest.ApiInterface;
import com.miraz.helloju.util.API;
import com.miraz.helloju.util.Method;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportFragment extends BottomSheetDialogFragment {

    private Method method;
    private String eventId, typeRb;
    private RadioGroup radioGroup;
    private TextInputEditText editText;
    private MaterialButton button;
    private InputMethodManager imm;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.report_fragment, container, false);

        method = new Method(getActivity());
        if (method.isRtl()) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        progressDialog = new ProgressDialog(getActivity());

        Bundle bundle = getArguments();
        assert bundle != null;
        eventId = bundle.getString("event_id");

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        radioGroup = view.findViewById(R.id.radioGroup_report_fragment);
        editText = view.findViewById(R.id.editText_report_fragment);
        button = view.findViewById(R.id.button_send_report_fragment);
        radioGroup.clearCheck();

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = group.findViewById(checkedId);
            if (null != rb && checkedId > -1) {
                typeRb = rb.getText().toString();
            }
        });

        button.setOnClickListener(v -> {

            String message = editText.getText().toString();

            editText.setError(null);

            if (message.equals("") || message.isEmpty()) {
                editText.requestFocus();
                editText.setError(getResources().getString(R.string.please_enter_message));
            } else if (typeRb == null || typeRb.equals("") || typeRb.isEmpty()) {
                method.alertBox(getResources().getString(R.string.please_select_option));
            } else {

                editText.clearFocus();
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                reportSubmit(method.userId(), typeRb, message);

            }

        });

        return view;
    }

    private void reportSubmit(String userId, String reportType, String reportMessage) {

        if (getActivity() != null) {

            progressDialog.show();
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("report_user_id", userId);
            jsObj.addProperty("report_event_id", eventId);
            jsObj.addProperty("report_type", reportType);
            jsObj.addProperty("report_text", reportMessage);
            jsObj.addProperty("method_name", "event_report");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<DataRP> call = apiService.submitEventReport(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<DataRP>() {
                @Override
                public void onResponse(@NotNull Call<DataRP> call, @NotNull Response<DataRP> response) {

                    if (getActivity() != null) {

                        try {

                            DataRP dataRP = response.body();

                            assert dataRP != null;
                            if (dataRP.getStatus().equals("1")) {
                                if (dataRP.getSuccess().equals("1")) {
                                    editText.setText("");
                                    radioGroup.clearCheck();
                                    dismiss();
                                }
                                method.alertBox(dataRP.getMsg());
                            } else if (dataRP.getStatus().equals("2")) {
                                method.suspend(dataRP.getMessage());
                            } else {
                                method.alertBox(dataRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

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

    }

}
