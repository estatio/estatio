    package com.eurocommercialproperties.estatio.dom.asset;

import java.util.List;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.QueryOnly;

// @NotInServiceMenu ??? TODO: why doesn't this get picked up???
@Named("Units")
public interface Units {

    @QueryOnly
    @MemberOrder(sequence = "1")
    public Unit newUnit(@Named("Reference") String reference, @Named("Name") String name);

    @Hidden
    // for use by fixtures
    public Unit newUnit(String reference, String name, UnitType type);

    /**
     * @return
     */
    List<Unit> allInstances();

}
