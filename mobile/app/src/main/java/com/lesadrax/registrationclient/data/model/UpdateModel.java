package com.lesadrax.registrationclient.data.model;

public class UpdateModel<T> {
    private String status;
    private int statusCode;
    private String message;
    private String service;
    private T data;

    // Getters et Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

}
