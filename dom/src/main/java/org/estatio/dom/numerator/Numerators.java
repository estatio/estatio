package org.estatio.dom.numerator;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;

import org.estatio.dom.EstatioDomainService;

@Named("Numerators")
public class Numerators extends EstatioDomainService {

    public Numerators() {
        super(Numerators.class, Numerator.class);
    }

    // //////////////////////////////////////

    @Hidden
    public Numerator create(NumeratorType type) {
        Numerator numerator = newTransientInstance(Numerator.class);
        numerator.setType(type);
        persist(numerator);
        return numerator;
    }
    
    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Numerator find(final @Named("Numerator Type") NumeratorType type) {
        return firstMatch(queryForFind(type));
    }

    private static QueryDefault<Numerator> queryForFind(NumeratorType type) {
        return new QueryDefault<Numerator>(Numerator.class, "numerator_find", "type", type);
    }
    
    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    public Numerator establish(NumeratorType type) {
        Numerator numerator = find(type);
        return numerator == null ? create(type) : numerator;
    }
    
    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    public List<Numerator> allNumerators() {
        List<Numerator> allInstances = allInstances(Numerator.class);
        return allInstances;
    }

}
