package com.rc.ecommerce.model.response.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Session {
    private String id;
    private Long validFrom;
    private Long validTo;
    private Long renewAllowedTill;
}
