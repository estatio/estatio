package org.estatio.dom.party.role;

import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;

/**
 * Each module that contributes a set of {@link IPartyRoleType}s should implement this SPI service.
 */
public interface PartyRoleTypeServiceSupport  {

    @Programmatic
    List<IPartyRoleType> listAll();
}
