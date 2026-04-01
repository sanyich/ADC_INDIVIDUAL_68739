package pt.unl.fct.di.adc.firstwebapp;

import org.glassfish.jersey.server.ResourceConfig;

import pt.unl.fct.di.adc.firstwebapp.filters.AdditionalResponseHeadersFilter;
import pt.unl.fct.di.adc.firstwebapp.resources.ChangeUserPasswordResource;
import pt.unl.fct.di.adc.firstwebapp.resources.ChangeUserRoleResource;
import pt.unl.fct.di.adc.firstwebapp.resources.CreateAccountResource;
import pt.unl.fct.di.adc.firstwebapp.resources.DeleteAccountResource;
import pt.unl.fct.di.adc.firstwebapp.resources.LoginResource;
import pt.unl.fct.di.adc.firstwebapp.resources.LogoutResource;
import pt.unl.fct.di.adc.firstwebapp.resources.ModifyAccountResource;
import pt.unl.fct.di.adc.firstwebapp.resources.ShowAuthSessionsResource;
import pt.unl.fct.di.adc.firstwebapp.resources.ShowUserRoleResource;
import pt.unl.fct.di.adc.firstwebapp.resources.ShowUsersResource;
import pt.unl.fct.di.adc.firstwebapp.util.GenericExceptionMapper;

public class RestApplication extends ResourceConfig {

    public RestApplication() {
        register(AdditionalResponseHeadersFilter.class);
        register(GenericExceptionMapper.class);

        register(LoginResource.class);
        register(LogoutResource.class);
        register(CreateAccountResource.class);
        register(ShowUsersResource.class);
        register(ShowAuthSessionsResource.class);
        register(ShowUserRoleResource.class);
        register(ChangeUserRoleResource.class);
        register(DeleteAccountResource.class);
        register(ChangeUserPasswordResource.class);
        register(ModifyAccountResource.class);
    }
}
