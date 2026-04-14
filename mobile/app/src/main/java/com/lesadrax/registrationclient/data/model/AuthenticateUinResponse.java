package com.lesadrax.registrationclient.data.model;

public class AuthenticateUinResponse {
    private String status;
    private int statusCode;
    private String service;
    private ActorModel data;

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

    public ActorModel getData() {
        return data;
    }

    public void setData(ActorModel data) {
        this.data = data;
    }
}
