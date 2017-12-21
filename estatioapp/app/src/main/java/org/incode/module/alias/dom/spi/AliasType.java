package org.incode.module.alias.dom.spi;

import org.incode.module.alias.AliasModule;

/**
 * The type of an alias for an aliased domain object.  The combination of the
 * alias type and applicationTenancyPath</code> unique distinguish a particular alias reference.
 *
 * <p>
 *     For example, a party might one alias for ["AcctsRec", "/Italy"] and a different alias for ["AcctsPay", "/Italy"].
 *     In this, "AcctsRec" and "AcctsPay" are the alias types.
 * </p>
 */
public interface AliasType {

    String getId();

    abstract class PropertyDomainEvent<S,T> extends AliasModule.PropertyDomainEvent<S, T> { }
    abstract class CollectionDomainEvent<S,T> extends AliasModule.CollectionDomainEvent<S, T> { }
    abstract class ActionDomainEvent<S> extends AliasModule.ActionDomainEvent<S> { }

}
