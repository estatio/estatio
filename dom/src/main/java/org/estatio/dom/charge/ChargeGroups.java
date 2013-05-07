package org.estatio.dom.charge;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

@Named("ChargeGroups")
public class ChargeGroups extends AbstractFactoryAndRepository {

    @Override
    public String getId() {
        return "chargeGroups";
    }

    public String iconName() {
        return "ChargeGroup";
    }

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public ChargeGroup newChargeGroup() {
        ChargeGroup chargeGroup = newTransientInstance(ChargeGroup.class);
        persist(chargeGroup);
        return chargeGroup;
    }
    
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public ChargeGroup findChargeGroupByReference(final String reference) {
        throw new NotImplementedException();
    }

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public List<ChargeGroup> allChargeGroups() {
        return allInstances(ChargeGroup.class);
    }
}
