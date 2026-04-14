package com.lesadrax.registrationclient.data.model;

import com.google.gson.annotations.SerializedName;

public class
AuthenticateResponse {
    private String status;
    private int statusCode;
    private String service;
    private Data data;

    // Getters et Setters
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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    // Classe interne Data
    public static class Data {
        private String status; // Valeur : MATCH, FINGERPRINT_NOT_MATCH, UIN_NOT_FOUND, ROLE_NOT_MATCH
        private ActorModel actor;

        // Getters et Setters
        public String getStatus() {
            return status;
        }

        public void setStatus(String result) {
            this.status = result;
        }

        public ActorModel getActor() {
            return actor;
        }

        public void setActor(ActorModel actor) {
            this.actor = actor;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "status='" + status + '\'' +
                    ", actor=" + actor +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AuthenticateResponse{" +
                "status='" + status + '\'' +
                ", statusCode=" + statusCode +
                ", service='" + service + '\'' +
                ", data=" + data +
                '}';
    }
}