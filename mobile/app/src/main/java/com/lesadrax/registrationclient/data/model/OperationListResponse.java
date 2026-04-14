package com.lesadrax.registrationclient.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OperationListResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("statusCode")
    private int statusCode;

    @SerializedName("message")
    private String message;

    @SerializedName("service")
    private String service;

    @SerializedName("data")
    private Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    // Classe Data
    public static class Data {

        @SerializedName("content")
        private List<Content> content;

        @SerializedName("last")
        private boolean last;

        @SerializedName("totalPages")
        private int totalPages;

        @SerializedName("totalElements")
        private int totalElements;

        @SerializedName("size")
        private int size;

        @SerializedName("number")
        private int number;

        @SerializedName("first")
        private boolean first;

        @SerializedName("numberOfElements")
        private int numberOfElements;

        @SerializedName("empty")
        private boolean empty;

        public boolean isLast() {
            return last;
        }

        public void setLast(boolean last) {
            this.last = last;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(int totalElements) {
            this.totalElements = totalElements;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }


        public boolean isFirst() {
            return first;
        }

        public void setFirst(boolean first) {
            this.first = first;
        }

        public int getNumberOfElements() {
            return numberOfElements;
        }

        public void setNumberOfElements(int numberOfElements) {
            this.numberOfElements = numberOfElements;
        }

        public boolean isEmpty() {
            return empty;
        }

        public void setEmpty(boolean empty) {
            this.empty = empty;
        }
    }


    // Classe Content
    public static class Content {

        @SerializedName("id")
        private int id;

        @SerializedName("nup")
        private String nup;

        @SerializedName("region")
        private String region;

        @SerializedName("prefecture")
        private String prefecture;

        @SerializedName("commune")
        private String commune;

        @SerializedName("canton")
        private String canton;

        @SerializedName("locality")
        private String locality;

        @SerializedName("personType")
        private String personType;

        @SerializedName("uin")
        private String uin;

        @SerializedName("hasConflict")
        private boolean hasConflict;

        @SerializedName("firstCheckListOperation")
        private Checklist firstCheckListOperation;

        @SerializedName("lastCheckListOperation")
        private Checklist lastCheckListOperation;

        @SerializedName("conflict")
        private Conflict conflict;

        // Getters et Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNup() {
            return nup;
        }

        public void setNup(String nup) {
            this.nup = nup;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getPrefecture() {
            return prefecture;
        }

        public void setPrefecture(String prefecture) {
            this.prefecture = prefecture;
        }

        public String getCommune() {
            return commune;
        }

        public void setCommune(String commune) {
            this.commune = commune;
        }

        public String getCanton() {
            return canton;
        }

        public void setCanton(String canton) {
            this.canton = canton;
        }

        public String getLocality() {
            return locality;
        }

        public void setLocality(String locality) {
            this.locality = locality;
        }

        public String getPersonType() {
            return personType;
        }

        public void setPersonType(String personType) {
            this.personType = personType;
        }

        public String getUin() {
            return uin;
        }

        public void setUin(String uin) {
            this.uin = uin;
        }

        public boolean isHasConflict() {
            return hasConflict;
        }

        public void setHasConflict(boolean hasConflict) {
            this.hasConflict = hasConflict;
        }

        public Checklist getFirstCheckListOperation() {
            return firstCheckListOperation;
        }

        public void setFirstCheckListOperation(Checklist firstCheckListOperation) {
            this.firstCheckListOperation = firstCheckListOperation;
        }

        public Checklist getLastCheckListOperation() {
            return lastCheckListOperation;
        }

        public void setLastCheckListOperation(Checklist lastCheckListOperation) {
            this.lastCheckListOperation = lastCheckListOperation;
        }

        public Conflict getConflict() {
            return conflict;
        }

        public void setConflict(Conflict conflict) {
            this.conflict = conflict;
        }
    }


}
