package org.estatio.dom.geography;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.filter.Filter;


@Named("Countries")
public class Countries extends AbstractFactoryAndRepository {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "countries";
    }

    public String iconName() {
        return "Country";
    }

    // }}

    // {{ NewCountry
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Country newCountry(
            final @Named("Reference") String reference, 
            final @Named("Name") String name) {
        final Country country = newTransientInstance(Country.class);
        country.setReference(reference);
        country.setName(name);
        persist(country);
        return country;
    }

    // }}

    // {{ findByReference
    /**
     * Returns the Country with given reference
     */
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Country findByReference(
            final @Named("Reference") String reference){
        return firstMatch(Country.class, new Filter<Country>() {
            @Override
            public boolean accept(final Country country) {
                return reference.equals(country.getReference());
            }
        });
    }

    // }}

    // {{ allCountries
    @Prototype
    @ActionSemantics(Of.SAFE)
    public List<Country> allCountries() {
        return allInstances(Country.class);
    }
    // }}

}
