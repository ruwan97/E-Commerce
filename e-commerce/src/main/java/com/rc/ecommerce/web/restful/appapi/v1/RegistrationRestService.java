package com.rc.ecommerce.web.restful.appapi.v1;

import com.rc.ecommerce.dto.RegistrationRequest;
import com.rc.ecommerce.util.Constants;
import com.rc.ecommerce.web.restful.appapi.ApiConstants;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public interface RegistrationRestService {
    String SERVICE_PATH = "registration";
    String CONTEXT = Constants.MODULE_NAMESPACE + "/" + ApiConstants.API_NAME + "/" + VersionConstants.VERSION + "/" + SERVICE_PATH;

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(RegistrationRequest registrationRequest);
}
