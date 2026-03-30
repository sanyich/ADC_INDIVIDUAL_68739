package pt.unl.fct.di.adc.firstwebapp.resources;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Key;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import pt.unl.fct.di.adc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.adc.firstwebapp.util.AuthUtil;
import pt.unl.fct.di.adc.firstwebapp.util.FirestoreUtil;
import pt.unl.fct.di.adc.firstwebapp.util.ResponseUtil;

@Path("/logout")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LogoutResource {

    private static final String TOKEN_KIND = "Token";

    private final Datastore datastore = FirestoreUtil.getDatastore();

    public LogoutResource() {}

    public static class LogoutRequest {
        public Input input;
        public AuthToken token;

        public static class Input {
            public String username;
        }
    }

    @POST
    public Response logout(LogoutRequest req) {

        // validate request
        if (req == null || req.input == null || req.token == null ||
            req.input.username == null || req.input.username.isBlank()) {

            return ResponseUtil.badRequest("INVALID_INPUT", "Missing username or token.");
        }

        // validate token
        AuthUtil.TokenCheckResult check = AuthUtil.validateToken(datastore, req.token);

        if (check.error != null) {
            return ResponseUtil.forbidden(check.error, "Invalid or expired token.");
        }

        String callerUsername = req.token.username;
        String callerRole = req.token.role;
        String targetUsername = req.input.username;

        // permission check
        if (!"ADMIN".equals(callerRole) && !callerUsername.equals(targetUsername)) {
            return ResponseUtil.forbidden("FORBIDDEN", "Cannot logout another user's session.");
        }

        // delete token
        Key tokenKey = datastore.newKeyFactory()
                .setKind(TOKEN_KIND)
                .newKey(req.token.tokenId);

        datastore.delete(tokenKey);

        return ResponseUtil.success("Logout successful");
    }
}