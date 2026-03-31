package pt.unl.fct.di.adc.firstwebapp.resources;

import com.google.cloud.datastore.*;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import pt.unl.fct.di.adc.firstwebapp.util.*;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {

    private static final String USER_KIND = "Account";
    private static final String TOKEN_KIND = "Token";

    private final Datastore datastore = FirestoreUtil.getDatastore();

    public LoginResource() {}

    public static class LoginRequest {
        public LoginData input;
    }

    static class LoginResponseData {
        AuthToken token;

        LoginResponseData(AuthToken token) {
            this.token = token;
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doLogin(LoginRequest req) {

        // validate input
        if (req == null || req.input == null ||
            req.input.username == null || req.input.password == null ||
            req.input.username.isBlank() || req.input.password.isBlank()) {

            return ResponseUtil.badRequest(
                    "INVALID_INPUT",
                    "Missing username or password."
            );
        }

        String username = req.input.username.trim().toLowerCase();

        Key userKey = datastore.newKeyFactory().setKind(USER_KIND).newKey(username);
        Entity user = datastore.get(userKey);

        // validate credentials
        if (user == null) {
            return ResponseUtil.forbidden(
                    "INVALID_CREDENTIALS",
                    "User not found or wrong password."
            );
        }

        String storedPwdHash = user.getString("password");
        String givenPwdHash = SecurityUtil.hashPassword(req.input.password);

        if (!storedPwdHash.equals(givenPwdHash)) {
            return ResponseUtil.forbidden(
                    "INVALID_CREDENTIALS",
                    "User not found or wrong password."
            );
        }

        String role = user.getString("role");

        // create token
        AuthToken token = new AuthToken(username, role);
        Key tokenKey = datastore.newKeyFactory().setKind(TOKEN_KIND).newKey(token.tokenId);

        Entity tokenEntity = Entity.newBuilder(tokenKey)
                .set("username", token.username)
                .set("role", token.role)
                .set("issuedAt", token.issuedAt)
                .set("expiresAt", token.expiresAt)
                .build();

        datastore.put(tokenEntity);

        return ResponseUtil.success(new LoginResponseData(token));
    }
}