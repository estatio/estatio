package com.eurocommercialproperties.estatio.dom.asset;

import java.util.List;

import com.eurocommercialproperties.estatio.dom.communicationchannel.PostalAddress;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;

@Named("Properties")
public interface Properties {

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public Property newProperty(@Named("Reference") String code, @Named("Name") String name);

    @ActionSemantics(Of.SAFE)
    @Hidden
    public Property lookupByReference(String reference);

    @Hidden
    // for use by fixtures
    public Property newProperty(String reference, String name, PropertyType propertyType);

    @Hidden
    public PostalAddress getPostalAddress(Property prop);
    
    @ActionSemantics(Of.SAFE)
    List<Property> allInstances();

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    List<Property> findAllByReference(@Named("Reference") String reference);

    /**
     * @param reference
     * @return
     */

}
