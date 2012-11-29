package com.eurocommercialproperties.estatio.jdo;

import java.util.List;

import com.eurocommercialproperties.estatio.dom.asset.Properties;
import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.utils.StringUtils;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;

//@javax.jdo.annotations.Query(
//        name = "properties_findByReference", 
//        language = "JDOQL", 
//        value = "SELECT FROM com.eurocommercialproperties.estatio.dom.asset.Property WHERE reference.matches(:r)")
public class PropertiesJdo extends Properties {

    // {{ findPropertiesByReference, findPropertyByReference (hidden) 
    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public List<Property> findPropertiesByReference(
            final @Named("Reference") String reference) {
        return allMatches(queryForFindByReference(reference));
    }

    @Hidden
    public Property findPropertyByReference(
            final String reference) {
        return firstMatch(queryForFindByReference(reference));
    }
    
    private static QueryDefault<Property> queryForFindByReference(String reference) {
        final String regex = StringUtils.wildcardToRegex(reference);
        return new QueryDefault<Property>(Property.class, "properties_findByReference", "r", regex);
    }

    // }}

}
