package com.miraz.helloju.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TicketDownloadRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private String success;

    @SerializedName("msg")
    private String msg;

    @SerializedName("string_data")
    private String string_data;

    @SerializedName("file_name")
    private String file_name;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

    public String getString_data() {
        return string_data;
    }

    public String getFile_name() {
        return file_name;
    }
}
