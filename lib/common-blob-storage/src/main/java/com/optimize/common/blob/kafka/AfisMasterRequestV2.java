package com.optimize.common.blob.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AfisMasterRequestV2 {

    public static final int SCHEMA_VERSION = 2;

    private int schemaVersion = SCHEMA_VERSION;
    private String rid;
    private String actorType;
    private List<FingerRef> fingers = new ArrayList<>();

    public AfisMasterRequestV2() {}

    public AfisMasterRequestV2(String rid, String actorType, List<FingerRef> fingers) {
        this.rid = rid;
        this.actorType = actorType;
        this.fingers = fingers;
    }

    public int getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(int schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getActorType() {
        return actorType;
    }

    public void setActorType(String actorType) {
        this.actorType = actorType;
    }

    public List<FingerRef> getFingers() {
        return fingers;
    }

    public void setFingers(List<FingerRef> fingers) {
        this.fingers = fingers;
    }
}
