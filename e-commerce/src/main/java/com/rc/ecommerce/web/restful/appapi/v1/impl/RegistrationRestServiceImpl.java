package com.rc.ecommerce.web.restful.appapi.v1.impl;

import com.rc.ecommerce.model.dto.RegistrationRequestDto;
import com.rc.ecommerce.exception.EComException;
import com.rc.ecommerce.service.impl.UserServiceImpl;
import com.rc.ecommerce.web.restful.appapi.v1.RegistrationRestService;
import com.rc.ecommerce.web.restful.appapi.v1.VersionConstants;
import com.rc.ecommerce.web.restful.util.RestResponseUtil;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Path(RegistrationRestService.CONTEXT)
@Service("RegistrationRestServiceImpl" + VersionConstants.VERSION)
@RequiredArgsConstructor
public class RegistrationRestServiceImpl implements RegistrationRestService {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationRestServiceImpl.class);

    private UserServiceImpl userService;

    @Override
    public Response register(RegistrationRequestDto registrationRequest) {
        try {
            userService.registerUser(registrationRequest);
            return RestResponseUtil.createResponse(200, "Success");
        } catch (EComException e) {
            return RestResponseUtil.createResponse(400, "Fail");
        }
    }
}
