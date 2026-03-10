package com.optimize.land.jms.model;



import com.optimize.land.model.entity.FingerprintStore;
import lombok.Data;

import java.util.Set;

@Data
public class AfisMasterRequest {
    private String rid;
    private Set<FingerprintStore> fingerprintStores;

    public AfisMasterRequest() {
    }

    public AfisMasterRequest(String rid, Set<FingerprintStore> fingerprintStores) {
        this.rid = rid;
        this.fingerprintStores = fingerprintStores;
    }



}
