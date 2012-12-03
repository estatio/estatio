package com.eurocommercialproperties.estatio.jdo;

import com.eurocommercialproperties.estatio.dom.geography.Countries;
import com.eurocommercialproperties.estatio.dom.geography.Country;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;

public class CountriesJdo extends Countries {

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Country findByReference(@Named("Reference") String reference) {
        return firstMatch(queryForFindCountryByReference(reference));
    }

    private static QueryDefault<Country> queryForFindCountryByReference(String reference) {
        return new QueryDefault<Country>(Country.class, "countries_findCountryByReference", "r", reference);
    }
}
