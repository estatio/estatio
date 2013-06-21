package org.estatio.dom.charge;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.utils.StringUtils;

public class ChargeGroups extends EstatioDomainService<ChargeGroup> {

    public ChargeGroups() {
        super(ChargeGroups.class, ChargeGroup.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name="Other", sequence = "chargeAndChargeGroups.chargeGroups.1")
    public ChargeGroup newChargeGroup(@Named("Reference") String reference, @Named("description") String description) {
        final ChargeGroup chargeGroup = newTransientInstance();
        chargeGroup.setReference(reference);
        chargeGroup.setDescription(description);
        persist(chargeGroup);
        return chargeGroup;
    }
    
    // //////////////////////////////////////
    
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Other", sequence = "chargeAndChargeGroups.chargeGroups.2")
    public ChargeGroup findChargeGroupByReference(@Named("Reference") String reference) {
        String regex = StringUtils.wildcardToRegex(reference);
        return firstMatch("charge_findChargeByReference", "r", regex);
    }

    // //////////////////////////////////////
    
    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Other", sequence = "chargeAndChargeGroups.chargeGroups.99")
    public List<ChargeGroup> allChargeGroups() {
        return allInstances();
    }


}
