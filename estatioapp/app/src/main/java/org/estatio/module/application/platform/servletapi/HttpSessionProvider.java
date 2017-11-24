package org.estatio.module.application.platform.servletapi;

import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.servletapi.dom.HttpServletRequestProvider;

// TODO: move into servletapi module
@DomainService(nature = NatureOfService.DOMAIN)
public class HttpSessionProvider {

    @Programmatic
    public Optional<HttpSession> getHttpSession() {
        final HttpServletRequest servletRequest = httpServletRequestProvider.getServletRequest();
        final HttpSession session = servletRequest != null ? servletRequest.getSession() : null;
        return Optional.ofNullable(session);
    }

    @Programmatic
    public <T> Optional<T> getAttribute(
            final String httpSessionKey,
            final Class<T> userMementoClass) {
        try {
            return getHttpSession()
                    .filter(Objects::nonNull)
                    .map(httpSession -> httpSession.getAttribute(httpSessionKey))
                    .filter(userMementoClass::isInstance)
                    .map(userMementoClass::cast);
        } catch (Exception e) {
            // bit hacky; to clean this up will require changes to servletapi module.
            return Optional.empty();
        }
    }

    @Programmatic
    public <T> void setAttribute(
            final String httpSessionKey,
            final T value) {
        getHttpSession().ifPresent(httpSession -> httpSession.setAttribute(httpSessionKey, value));
    }

    @Programmatic
    public void removeAttribute(
            final String httpSessionKey) {
        getHttpSession().ifPresent(httpSession -> httpSession.removeAttribute(httpSessionKey));
    }

    @Inject
    HttpServletRequestProvider httpServletRequestProvider;

}
