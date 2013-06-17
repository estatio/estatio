package org.estatio.dom.asset;

import java.util.List;

import org.estatio.dom.utils.StringUtils;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;

public class PropertiesJdo extends Properties {

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public List<Property> findPropertiesByReference(final @Named("Reference") String reference) {
        return allMatches(queryForFindPropertiesByReference(reference));
    }

    @Hidden
    public Property findPropertyByReference(final String reference) {
        return firstMatch(queryForFindPropertiesByReference(reference));
    }

    private static QueryDefault<Property> queryForFindPropertiesByReference(String reference) {
        return new QueryDefault<Property>(Property.class, "properties_findPropertiesByReference", "r", StringUtils.wildcardToRegex(reference));
    }

}
