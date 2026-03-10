package com.optimize.land.jms.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RegistrationProcessorFeedback {
    private String rid;
    private Boolean foundMatch;
    private String matchedRID;

    public RegistrationProcessorFeedback() {
    }

    public RegistrationProcessorFeedback(String rid, Boolean isFoundMatch, String matchedRID) {
        this.rid = rid;
        this.foundMatch = isFoundMatch;
        this.matchedRID = matchedRID;
    }

}
