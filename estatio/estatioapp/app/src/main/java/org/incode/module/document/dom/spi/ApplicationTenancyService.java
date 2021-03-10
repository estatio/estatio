package org.incode.module.document.dom.spi;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.security.dom.tenancy.HasAtPath;

/**
 * Mandatory SPI service that returns the application tenancy path for a domain object
 */
public interface ApplicationTenancyService {

    @Programmatic
    String atPathFor(final Object domainObject);

    @DomainService(nature = NatureOfService.DOMAIN, menuOrder = "" + Integer.MAX_VALUE)
    public static class ForHasAtPath implements ApplicationTenancyService {

        @Override public String atPathFor(final Object domainObject) {
            if(domainObject instanceof HasAtPath) {
                final HasAtPath hasAtPath = (HasAtPath) domainObject;
                return hasAtPath.getAtPath();
            }
            return null;
        }
    }
}
