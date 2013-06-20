package org.estatio.dom.geography;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.query.QueryDefault;

import org.estatio.dom.EstatioDomainService;

@Named("Countries")
public class Countries extends EstatioDomainService {

    public Countries() {
        super(Countries.class, Country.class);
    }

    // //////////////////////////////////////
    
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Country newCountry(final @Named("Reference") String reference, final @Named("Name") String name) {
        final Country country = newTransientInstance(Country.class);
        country.setReference(reference);
        country.setName(name);
        persist(country);
        return country;
    }

    // //////////////////////////////////////

    /**
     * Returns the Country with given reference
     */
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Country findByReference(@Named("Reference") String reference) {
        if (reference == null)
            return null;
        return firstMatch(queryForFindCountryByReference(reference));
    }

    private static QueryDefault<Country> queryForFindCountryByReference(String reference) {
        if (reference == null)
            return null;
        return new QueryDefault<Country>(Country.class, "countries_findCountryByReference", "r", reference);
    }

    
    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    public List<Country> allCountries() {
        return allInstances(Country.class);
    }

}
