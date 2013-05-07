package org.estatio.jdo;


import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.utils.StringUtils;

@Named("Charges")
public class ChargesJdo extends Charges {

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Charge findChargeByReference(@Named("Reference") String reference) {
        String rexeg = StringUtils.wildcardToRegex(reference);
        return firstMatch(queryForFindChargeByReference(rexeg));
    }
    
    private static QueryDefault<Charge> queryForFindChargeByReference(String pattern) {
        return new QueryDefault<Charge>(Charge.class, "charge_findChargeByReference", "r", pattern);
    }

}
