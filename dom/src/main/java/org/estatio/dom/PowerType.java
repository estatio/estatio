package org.estatio.dom;

import org.apache.isis.applib.DomainObjectContainer;

/**
 * For <tt>enum</tt>s that act as powertypes, in other words acting as a factory
 * for subtypes of some inheritance hierarchy.
 */
public interface PowerType<T> {

    T create(DomainObjectContainer container); 

}
