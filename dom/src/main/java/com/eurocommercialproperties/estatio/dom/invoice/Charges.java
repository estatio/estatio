package com.eurocommercialproperties.estatio.dom.invoice;

import java.util.List;

import com.eurocommercialproperties.estatio.dom.utils.StringUtils;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.filter.Filter;


@Named("Charges")
public class Charges extends AbstractFactoryAndRepository {

    // {{ newCharge
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Charge newCharge(String reference) {
        Charge charge = findChargeByReference(reference);
        if (charge == null) {
            charge = newTransientInstance(Charge.class);
            persist(charge);
        }
        return charge;
    }
    // }}
    
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Charge findChargeByReference(final String reference) {
        final String regex = StringUtils.wildcardToRegex(reference);
        return firstMatch(Charge.class, new Filter<Charge>() {
            @Override
            public boolean accept(final Charge charge) {
                return charge.getReference().matches(regex);
            }
        });
    }

    // {{ AllCharges
    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public List<Charge> allCharges() {
        return allInstances(Charge.class);
    }
    // }}

    // {{ newCharge
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "4")
    public ChargeGroup newChargeGroup() {
        ChargeGroup chargeGroup = newTransientInstance(ChargeGroup.class);
        persist(chargeGroup);
        return chargeGroup;
    }
    // }}
    
    // {{ AllChargeGroups
    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "4")
    public List<ChargeGroup> allChargeGroups() {
        return allInstances(ChargeGroup.class);
    }
    // }}

}
