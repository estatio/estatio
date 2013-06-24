package org.estatio.dom.numerator;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;

import org.estatio.dom.EstatioDomainService;

public class Numerators extends EstatioDomainService<Numerator> {

    public Numerators() {
        super(Numerators.class, Numerator.class);
    }

    // //////////////////////////////////////

    @Hidden
    public Numerator create(final NumeratorType type) {
        Numerator numerator = newTransientInstance();
        numerator.setType(type);
        persist(numerator);
        return numerator;
    }
    
    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Other", sequence = "numerators.1")
    public Numerator findNumeratorByType(final NumeratorType type) {
        return firstMatch("findByType", "type", type);
    }

    
    // //////////////////////////////////////

    // TODO: this is actually idempotent?
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name="Other", sequence = "numerators.2")
    public Numerator establish(NumeratorType type) {
        Numerator numerator = findNumeratorByType(type);
        return numerator == null ? create(type) : numerator;
    }
    
    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Other", sequence = "numerators.3")
    public List<Numerator> allNumerators() {
        return allInstances();
    }

}
