package org.estatio.jdo;

import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.Agreements;
import org.estatio.dom.utils.StringUtils;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;

public class AgreementsJdo extends Agreements {

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Agreement findByReference(@Named("Reference") String reference) {
        return firstMatch(queryForFindByReference(reference));
    }

    private static QueryDefault<Agreement> queryForFindByReference(String reference) {
        return new QueryDefault<Agreement>(Agreement.class, "lease_findLeaseByReference", "r", StringUtils.wildcardToRegex(reference));
    }
}
