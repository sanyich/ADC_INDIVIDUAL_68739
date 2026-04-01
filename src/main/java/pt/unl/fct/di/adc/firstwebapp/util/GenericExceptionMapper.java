package pt.unl.fct.di.adc.firstwebapp.util;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        String msg = exception.getMessage() != null ? exception.getMessage() : "Internal server error";
        // Optionally log exception on server side
        System.err.println("[GenericExceptionMapper] " + exception.toString());
        return ResponseUtil.error(ResponseUtil.ErrorCode.INTERNAL_SERVER_ERROR.code, msg);
    }
}
