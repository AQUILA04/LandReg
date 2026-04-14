package com.lesadrax.registrationclient.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UinDetailRequest {
    @SerializedName("uinList")
    private List<String> uinList;

    public UinDetailRequest(List<String> uinList) {
        this.uinList = uinList;
    }

    public List<String> getUinList() {
        return uinList;
    }

    public void setUinList(List<String> uinList) {
        this.uinList = uinList;
    }
}
