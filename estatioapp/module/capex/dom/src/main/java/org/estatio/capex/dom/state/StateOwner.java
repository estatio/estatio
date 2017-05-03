package org.estatio.capex.dom.state;

import org.apache.isis.applib.annotation.Programmatic;

public interface StateOwner<DO extends StateOwner<DO, S>, S extends State<DO, S>> {

    @Programmatic
    S getState();

    @Programmatic
    void setState(S state);

}
