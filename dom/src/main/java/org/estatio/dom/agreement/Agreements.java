package org.estatio.dom.agreement;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.query.QueryDefault;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.utils.StringUtils;

@Named("Agreements")
@Hidden
public class Agreements extends EstatioDomainService {

    public Agreements() {
        super(Agreements.class, Agreement.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public Agreement newAgreement(@Named("Type") AgreementType type, final @Named("Reference") String reference, final @Named("Name") String name) {
        Agreement agreement = type.create(getContainer());
        return agreement;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Agreement findByReference(@Named("Reference") String reference) {
        return firstMatch(queryForFindByReference(reference));
    }

    private static QueryDefault<Agreement> queryForFindByReference(String reference) {
        return new QueryDefault<Agreement>(Agreement.class, "lease_findLeaseByReference", "r", StringUtils.wildcardToRegex(reference));
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    public List<Agreement> allAgreements() {
        return allInstances(Agreement.class);
    }

}
