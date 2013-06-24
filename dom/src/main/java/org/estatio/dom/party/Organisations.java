package org.estatio.dom.party;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.utils.StringUtils;

public class Organisations extends EstatioDomainService<Organisation> {

    public Organisations() {
        super(Organisations.class, Organisation.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name="Parties", sequence = "2")
    public Organisation newOrganisation(final @Named("Reference") String reference, final @Named("Name") String name) {
        final Organisation organisation = newTransientInstance(Organisation.class);
        organisation.setReference(reference);
        organisation.setName(name);
        persist(organisation);
        return organisation;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Parties", sequence = "5")
    public Organisation findOrganisation(final @Named("Reference or Name") @DescribedAs("May include wildcards '*' and '?'") String searchArg) {
        return firstMatch("findByReferenceOrName", "searchArg", StringUtils.wildcardToCaseInsensitiveRegex(searchArg));
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Parties", sequence = "99.1")
    public List<Organisation> allOrganisations() {
        return allInstances();
    }

}
