package org.estatio.dom.agreement;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.utils.StringUtils;

@Hidden
public class Agreements extends EstatioDomainService<Agreement> {

    public Agreements() {
        super(Agreements.class, Agreement.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    public Agreement newAgreement(@Named("Type") AgreementType agreementType, final @Named("Reference") String reference, final @Named("Name") String name) {
        return agreementType.create(getContainer());
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    public Agreement findByReference(@Named("Reference") String reference) {
        return firstMatch("lease_findLeaseByReference", "r", StringUtils.wildcardToRegex(reference));
    }


}
