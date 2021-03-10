package org.incode.module.base.dom.managed;

/**
 * Implemented by entities where the management (which system is in the &quot;lead&quot;) can vary on a case-by-case basis.
 *
 * <p>
 *     We intend to bring this concept in piecemeal, starting with <code>CommunicationChannel</code>s.
 * </p>
 */
public interface HasManagedIn {

    ManagedIn getManagedIn();

}
