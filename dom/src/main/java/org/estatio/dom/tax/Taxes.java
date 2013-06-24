package org.estatio.dom.tax;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.utils.StringUtils;

public class Taxes extends EstatioDomainService<Tax> {

    public Taxes() {
        super(Taxes.class, Tax.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name="Other", sequence = "taxStuff.taxes.1")
    public Tax newTax(final @Named("Reference") String reference) {
        final Tax tax = newTransientInstance();
        tax.setReference(reference);
        persist(tax);
        return tax;
    }

    // //////////////////////////////////////
    
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Other", sequence = "taxStuff.taxes.2")
    public List<Tax> allTaxes() {
        return allInstances();
    }


    // //////////////////////////////////////

    @Hidden
    public Tax findTaxByReference(final String reference) {
        return firstMatch("findByReference", "reference", StringUtils.wildcardToRegex(reference));
    }

}
