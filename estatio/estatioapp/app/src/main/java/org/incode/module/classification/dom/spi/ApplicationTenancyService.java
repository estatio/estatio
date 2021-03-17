package org.incode.module.classification.dom.spi;

import org.apache.isis.applib.annotation.Programmatic;

/**
 * Mandatory SPI service that returns the application tenancy path for a domain object to be classified
 */
public interface ApplicationTenancyService {

    @Programmatic
    String atPathFor(final Object domainObjectToClassify);

}
