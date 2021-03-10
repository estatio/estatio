package org.incode.module.alias.dom.spi;

import org.apache.isis.applib.annotation.Programmatic;
import org.incode.module.alias.dom.impl.Alias;

import java.util.Collection;

/**
 * Mandatory SPI service that returns the set of available application tenancy paths for a given aliased.
 *
 * <p>
 *     This is <i>not</i> the same as the application tenancy path of the aliased, rather it is those application
 *     tenancy paths that are available to then find alias types in order to set up an {@link Alias}.
 * </p>
 */
public interface ApplicationTenancyRepository {

    @Programmatic
    Collection<String> atPathsFor(final Object domainObjectToAlias);

}
