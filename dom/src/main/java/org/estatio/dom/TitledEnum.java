package org.estatio.dom;

import org.apache.isis.core.commons.lang.StringUtils;

/**
 * An enum that implements {@link Titled} and moreover its {@link #title()}
 * is derived according to a {@link StringUtils#enumTitle(String) standard algorithm}.
 */
public interface TitledEnum extends Titled {
    
}
