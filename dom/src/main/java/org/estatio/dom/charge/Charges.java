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

    @ActionSemantics(Of.IDEMPOTENT)
    @MemberOrder(name="Other", sequence = "chargeAndChargeGroups.charges.1")
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
    @MemberOrder(name="Other", sequence = "chargeAndChargeGroups.charges.2")
    public Charge findChargeByReference(@Named("Reference") String reference) {
        String regex = StringUtils.wildcardToRegex(reference);
        return firstMatch("findByReference", "reference", regex);
    }

    // //////////////////////////////////////
    
    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Other", sequence = "chargeAndChargeGroups.charges.99")
    public List<Charge> allCharges() {
        return allInstances();
    }


}
