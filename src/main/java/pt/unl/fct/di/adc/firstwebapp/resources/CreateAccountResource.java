package pt.unl.fct.di.adc.firstwebapp.resources;

import java.util.Set;
import java.util.logging.Logger;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import pt.unl.fct.di.adc.firstwebapp.util.FirestoreUtil;
import pt.unl.fct.di.adc.firstwebapp.util.ResponseUtil;
import pt.unl.fct.di.adc.firstwebapp.util.SecurityUtil;

@Path("/createaccount")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CreateAccountResource {

    private static final Logger LOG = Logger.getLogger(CreateAccountResource.class.getName());
    private static final String ACCOUNT_KIND = "Account";

    private static final String ADMIN = "ADMIN";
    private static final String BOFFICER = "BOFFICER";
    private static final String USER = "USER";

    private static final Set<String> VALID_ROLES =
            Set.of(USER, BOFFICER, ADMIN);

    private final Datastore datastore = FirestoreUtil.getDatastore();

    public CreateAccountResource() {}

    public static class CreateAccountRequest {
        public Input input;

        public static class Input {
            public String username;
            public String password;
            public String confirmation;
            public String phone;
            public String address;
            public String role;
        }
    }

    public static class CreateAccountResponseData {
        public String username;
        public String role;

        public CreateAccountResponseData(String username, String role) {
            this.username = username;
            this.role = role;
        }
    }

    @POST
    public Response createAccount(CreateAccountRequest req) {

        // validate request structure
        if (req == null || req.input == null) {
            return ResponseUtil.badRequest(
                    "INVALID_INPUT",
                    "Missing input object."
            );
        }

        CreateAccountRequest.Input in = req.input;

        // validate required fields
        if (isBlank(in.username) || isBlank(in.password) || isBlank(in.confirmation)
                || isBlank(in.phone) || isBlank(in.address) || isBlank(in.role)) {
            return ResponseUtil.badRequest(
                    "INVALID_INPUT",
                    "All fields are required."
            );
        }

        String username = in.username.trim().toLowerCase();

        // validate email format
        if (!username.contains("@")) {
            return ResponseUtil.badRequest(
                    "INVALID_INPUT",
                    "Username must be in email format."
            );
        }

        // validate password confirmation
        if (!in.password.equals(in.confirmation)) {
            return ResponseUtil.badRequest(
                    "INVALID_INPUT",
                    "Password and confirmation do not match."
            );
        }

        // validate password strength
        if (in.password.length() < 6) {
            return ResponseUtil.badRequest(
                    "INVALID_PASSWORD",
                    "Password must be at least 6 characters."
            );
        }

        // validate role
        String roleUpper = in.role.trim().toUpperCase();

        if (!VALID_ROLES.contains(roleUpper)) {
            return ResponseUtil.badRequest(
                    "INVALID_INPUT",
                    "Role must be USER, BOFFICER, or ADMIN."
            );
        }

        // check if user exists
        Key userKey = datastore.newKeyFactory()
                .setKind(ACCOUNT_KIND)
                .newKey(username);

        Entity existing = datastore.get(userKey);

        if (existing != null) {
            return ResponseUtil.conflict(
                    "USER_ALREADY_EXISTS",
                    "An account with that username already exists."
            );
        }

        // hash password
        String hashedPassword = SecurityUtil.hashPassword(in.password);

        // create account
        Entity account = Entity.newBuilder(userKey)
                .set("username", username)
                .set("password", hashedPassword)
                .set("phone", in.phone.trim())
                .set("address", in.address.trim())
                .set("role", roleUpper)
                .build();

        datastore.put(account);

        LOG.info("Account created: " + username + " with role " + roleUpper);

        // success response
        return ResponseUtil.success(
                new CreateAccountResponseData(username, roleUpper)
        );
    }

    // helper method to check if a string is null or blank
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}