package org.incode.module.document.dom.spi;

import org.apache.isis.applib.annotation.Programmatic;

/**
 * Mandatory SPI service that returns the application tenancy path for a domain object
 */
public interface ApplicationTenancyService {

    @Programmatic
    String atPathFor(final Object domainObject);

}
