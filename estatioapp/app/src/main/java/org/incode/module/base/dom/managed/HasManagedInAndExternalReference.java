package org.incode.module.base.dom.managed;

/**
 * Extends the concept of {@link HasManagedIn}, for entities which are managed by an external system and that have some
 * sort of reference or identifier in that the external system.
 *
 * <p>
 * The meaning of this external reference is opaque to Estatio.  In many cases it could be globally unique, but it may
 * also be only locally unique with respect to some other attribute.  For example, with
 * <code>CommunicationChannel</code>s, those which are CODA managed use the external reference to hold the tag, unique
 * only with respect to the owning <code>Organisation</code>.
 * </p>
 */
public interface HasManagedInAndExternalReference extends HasManagedIn {

    String getManagedInExternalReference();

}
