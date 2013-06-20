package org.estatio.dom.charge;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.query.QueryDefault;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.utils.StringUtils;

@Named("Charges")
public class Charges extends EstatioDomainService {

    public Charges() {
        super(Charges.class, Charge.class);
    }
    
    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Charge newCharge(String reference) {
        Charge charge = findChargeByReference(reference);
        if (charge == null) {
            charge = newTransientInstance(Charge.class);
            charge.setReference(reference);
            persist(charge);
        }
        return charge;
    }

    // //////////////////////////////////////
    
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Charge findChargeByReference(@Named("Reference") String reference) {
        String rexeg = StringUtils.wildcardToRegex(reference);
        return firstMatch(queryForFindChargeByReference(rexeg));
    }
    
    private static QueryDefault<Charge> queryForFindChargeByReference(String pattern) {
        return new QueryDefault<Charge>(Charge.class, "charge_findChargeByReference", "r", pattern);
    }

    // //////////////////////////////////////
    
    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public List<Charge> allCharges() {
        return allInstances(Charge.class);
    }


}
