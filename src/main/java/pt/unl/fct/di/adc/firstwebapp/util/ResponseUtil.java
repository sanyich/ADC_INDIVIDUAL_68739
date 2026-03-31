package pt.unl.fct.di.adc.firstwebapp.util;

import com.google.gson.Gson;
import jakarta.ws.rs.core.Response;

public class ResponseUtil {

    private static final Gson g = new Gson();
    private static final String JSON = "application/json; charset=utf-8";

    // success
    public static Response success(Object data) {
        return Response.ok(
                g.toJson(new ApiResponse("success", data)),
                JSON
        ).build();
    }

    // generic error
    public static Response error(Response.Status httpStatus, String status, String message) {
        return Response.status(httpStatus)
                .entity(g.toJson(new ApiResponse(status, message)))
                .type(JSON)
                .build();
    }

    public static Response badRequest(String status, String message) {
        return error(Response.Status.BAD_REQUEST, status, message);
    }

    public static Response forbidden(String status, String message) {
        return error(Response.Status.FORBIDDEN, status, message);
    }

    public static Response conflict(String status, String message) {
        return error(Response.Status.CONFLICT, status, message);
    }

    public static Response notFound(String status, String message) {
        return error(Response.Status.NOT_FOUND, status, message);
    }

    public static Response unauthorized(String status, String message) {
        return error(Response.Status.UNAUTHORIZED, status, message);
    }

    // wrapper
    public static class ApiResponse {
        public String status;
        public Object data;

        public ApiResponse(String status, Object data) {
            this.status = status;
            this.data = data;
        }
    }
}