package com.eurocommercialproperties.estatio.dom.asset;

import java.util.List;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.QueryOnly;

@Named("Properties")
public interface Properties {

    @QueryOnly
    @MemberOrder(sequence = "1")
    public Property newProperty(@Named("Reference") String code, @Named("Name") String name);

    @QueryOnly
    @MemberOrder(sequence = "2")
    public Property findByReference(@Named("Reference") String reference);

    @Hidden
    // for use by fixtures
    public Property newProperty(String reference, String name, PropertyType propertyType);

    @QueryOnly
    List<Property> allInstances();

    /**
     * @param reference
     * @return
     */

}
