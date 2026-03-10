package com.optimize.kopesa.afis.master.service.dto;

import java.util.List;

public class AfisMasterRequest {
    private String rid;
    private List<FingerprintStoreDTO> fingerprintStores;

    public AfisMasterRequest() {
    }

    public AfisMasterRequest(String rid, List<FingerprintStoreDTO> fingerprintStores) {
        this.rid = rid;
        this.fingerprintStores = fingerprintStores;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public List<FingerprintStoreDTO> getFingerprintStores() {
        return fingerprintStores;
    }

    public void setFingerprintStores(List<FingerprintStoreDTO> fingerprintStores) {
        this.fingerprintStores = fingerprintStores;
    }
}
