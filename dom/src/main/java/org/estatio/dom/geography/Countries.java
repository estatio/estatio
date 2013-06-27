package org.estatio.dom.geography;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.utils.StringUtils;

public class Countries extends EstatioDomainService<Country> {

    public Countries() {
        super(Countries.class, Country.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name = "Other", sequence = "geography.countries.1")
    public Country newCountry(final @Named("Reference") String reference, final @Named("Name") String name) {
        final Country country = newTransientInstance();
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
    @MemberOrder(name = "Other", sequence = "geography.countries.2")
    public Country findCountryByReference(@Named("Reference") String reference) {
        if (reference == null) {
            return null;
        }
        return firstMatch("findByReference", "reference", StringUtils.wildcardToRegex(reference));
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name = "Other", sequence = "geography.countries.99")
    public List<Country> allCountries() {
        return allInstances();
    }

}
