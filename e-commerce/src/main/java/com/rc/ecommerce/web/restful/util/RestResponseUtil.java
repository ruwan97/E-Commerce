package com.rc.ecommerce.web.restful.util;

import com.rc.ecommerce.model.response.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class RestResponseUtil {
    public static Response createResponse(int status, String message) {
        return Response.status(status)
                .entity(new ErrorResponse(message))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    public static Response getResponse(int status, String message) {
        return createResponse(status, message);
    }
}
