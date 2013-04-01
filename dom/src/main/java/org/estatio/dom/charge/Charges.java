package org.estatio.dom.charge;

import java.util.List;


import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.filter.Filter;
import org.estatio.dom.utils.StringUtils;

@Named("Charges")
public class Charges extends AbstractFactoryAndRepository {

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

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public List<Charge> allCharges() {
        return allInstances(Charge.class);
    }
}
