package org.incode.module.alias.dom.spi;

import java.util.Collection;

/**
 * Mandatory SPI service that returns the alias types.
 */
public interface AliasTypeRepository {

    Collection<AliasType> aliasTypesFor(final Object aliased, final String atPath);

}
