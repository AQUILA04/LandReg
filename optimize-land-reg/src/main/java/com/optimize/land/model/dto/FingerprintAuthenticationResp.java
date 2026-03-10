package com.optimize.land.model.dto;

import com.optimize.land.model.enumeration.BioAuthResponse;
import lombok.Data;

@Data
public class FingerprintAuthenticationResp {
    private BioAuthResponse status;
    private ActorModel actor;
}
