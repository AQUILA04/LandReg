package com.lesadrax.registrationclient.data.model;

public class SynchroResponse {

    private String status;
    private double statusCode;
    private String message;
    private String service;
    private SynchroData data;

    // Getters et Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(double statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public SynchroData getData() {
        return data;
    }

    public void setData(SynchroData data) {
        this.data = data;
    }
}
