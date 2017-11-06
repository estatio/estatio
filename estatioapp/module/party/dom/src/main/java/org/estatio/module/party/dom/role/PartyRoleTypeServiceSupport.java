package org.estatio.module.party.dom.role;

import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;

/**
 * Each module that contributes a set of {@link IPartyRoleType}s should implement this SPI service.
 */
public interface PartyRoleTypeServiceSupport<T extends IPartyRoleType>  {

    @Programmatic
    List<T> listAll();


}
