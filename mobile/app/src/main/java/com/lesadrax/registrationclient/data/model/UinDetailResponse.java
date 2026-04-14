package com.lesadrax.registrationclient.data.model;

import java.util.List;

public class UinDetailResponse {

    private String status;
    private int statusCode;
    private String service;
    private List<ActorModel> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public List<ActorModel> getData() {
        return data;
    }

    public void setData(List<ActorModel> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UinDetailResponse{" +
                "status='" + status + '\'' +
                ", statusCode=" + statusCode +
                ", service='" + service + '\'' +
                ", data=" + data +
                '}';
    }
}
