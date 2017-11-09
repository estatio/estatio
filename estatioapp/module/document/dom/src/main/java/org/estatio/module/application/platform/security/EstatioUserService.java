package org.estatio.module.application.platform.security;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.security.RoleMemento;
import org.apache.isis.applib.security.UserMemento;
import org.apache.isis.applib.services.user.UserService;

import org.estatio.module.application.platform.servletapi.HttpSessionProvider;

@DomainService(nature = NatureOfService.DOMAIN)
@DomainServiceLayout(menuBar = DomainServiceLayout.MenuBar.TERTIARY)
public class EstatioUserService implements UserService {

    public static final String HTTP_SESSION_KEY = EstatioUserService.class.getName() + "#" + "impersonatedUserMemento";

    @Programmatic
    @Override
    public UserMemento getUser() {
        final Optional<UserMemento> impersonatedUserIfAny = getImpersonatedUserIfAny();
        return impersonatedUserIfAny.orElse(delegateUserService().getUser());
    }

    @Programmatic
    public void setUser(final String userName) {
        setImpersonatedUser(new UserMemento(userName));
    }

    @Programmatic
    public void setUser(final String userName, final String... roles) {
        setUser(userName, Arrays.asList(roles));
    }

    @Programmatic
    public void setUser(final String userName, final List<String> roles) {
        setImpersonatedUser(new UserMemento(userName, roles.stream().map(RoleMemento::new).collect(Collectors.toList())));
    }

    @Programmatic
    public void reset() {
        setImpersonatedUser(null);
    }

    @Programmatic
    public boolean isImpersonating() {
        return getImpersonatedUserIfAny().isPresent();
    }

    @Programmatic
    public boolean isAvailable() {
        return httpSessionProvider.getHttpSession().isPresent();
    }


    private UserService delegateUserService;
    private UserService delegateUserService() {
        if (delegateUserService == null) {
            delegateUserService = userServiceList.stream().filter(x -> x != EstatioUserService.this).findFirst().get();
        }
        return delegateUserService;
    }

    private Optional<UserMemento> getImpersonatedUserIfAny() {
            return httpSessionProvider.getAttribute(HTTP_SESSION_KEY, UserMemento.class);
    }

    private void setImpersonatedUser(UserMemento overrideUser) {
        httpSessionProvider.setAttribute(HTTP_SESSION_KEY, overrideUser);
    }

    @javax.inject.Inject
    List<UserService> userServiceList;

    @Inject
    HttpSessionProvider httpSessionProvider;

}
