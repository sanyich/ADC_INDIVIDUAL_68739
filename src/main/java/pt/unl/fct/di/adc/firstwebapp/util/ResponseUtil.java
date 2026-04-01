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

    // generic error (always 200 OK; error details in body)
    public static Response error(Response.Status httpStatus, String code, String message) {
        return Response.ok(
                g.toJson(new ApiResponse("error", new ApiError(code, message))),
                JSON
        ).build();
    }

    public static Response error(String code, String message) {
        return error(Response.Status.OK, code, message);
    }

    public static Response badRequest(String code, String message) {
        return error(Response.Status.BAD_REQUEST, code, message);
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
    public static class ApiError {
        public String code;
        public int number;
        public String message;

        public ApiError(String code, String message) {
            this.code = code;
            this.message = message;
            this.number = ErrorCode.fromCode(code).number;
        }
    }

    public enum ErrorCode {
        INVALID_CREDENTIALS("INVALID_CREDENTIALS", 9900, "The username-password pair is not valid"),
        USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", 9901, "Error in creating an account because the username already exists"),
        USER_NOT_FOUND("USER_NOT_FOUND", 9902, "The username referred in the operation doesn’t exist in registered accounts"),
        INVALID_TOKEN("INVALID_TOKEN", 9903, "The operation is called with an invalid token (wrong format for example)"),
        TOKEN_EXPIRED("TOKEN_EXPIRED", 9904, "The operation is called with a token that is expired"),
        UNAUTHORIZED("UNAUTHORIZED", 9905, "The operation is not allowed for the user role"),
        INVALID_INPUT("INVALID_INPUT", 9906, "The call is using input data not following the correct specification"),
        FORBIDDEN("FORBIDDEN", 9907, "The operation generated a forbidden error by other reason"),
        INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", 9999, "Internal server error");

        public final String code;
        public final int number;
        public final String defaultMessage;

        ErrorCode(String code, int number, String defaultMessage) {
            this.code = code;
            this.number = number;
            this.defaultMessage = defaultMessage;
        }

        public static ErrorCode fromCode(String code) {
            for (ErrorCode e : values()) {
                if (e.code.equals(code)) {
                    return e;
                }
            }
            return INTERNAL_SERVER_ERROR;
        }
    }

    public static class ApiResponse {
        public String status;
        public Object data;

        public ApiResponse(String status, Object data) {
            this.status = status;
            this.data = data;
        }
    }
}