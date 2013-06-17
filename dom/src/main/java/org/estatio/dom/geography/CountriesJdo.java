package org.estatio.dom.geography;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;

public class CountriesJdo extends Countries {

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Country findByReference(String reference) {
        if (reference == null)
            return null;
        return firstMatch(queryForFindCountryByReference(reference));
    }

    private static QueryDefault<Country> queryForFindCountryByReference(String reference) {
        if (reference == null)
            return null;
        return new QueryDefault<Country>(Country.class, "countries_findCountryByReference", "r", reference);
    }
}
