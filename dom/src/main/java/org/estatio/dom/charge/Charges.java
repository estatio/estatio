package org.estatio.dom.charge;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.utils.StringUtils;

public class Charges extends EstatioDomainService<Charge> {

    public Charges() {
        super(Charges.class, Charge.class);
    }
    
    // //////////////////////////////////////

    // TODO: this is actually idempotent?
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Charge newCharge(String reference) {
        Charge charge = findChargeByReference(reference);
        if (charge == null) {
            charge = newTransientInstance();
            charge.setReference(reference);
            persist(charge);
        }
        return charge;
    }

    // //////////////////////////////////////
    
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Charge findChargeByReference(@Named("Reference") String reference) {
        String regex = StringUtils.wildcardToRegex(reference);
        return firstMatch("charge_findChargeByReference", "r", regex);
    }

    // //////////////////////////////////////
    
    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "99")
    public List<Charge> allCharges() {
        return allInstances();
    }


}
