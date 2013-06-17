package org.estatio.dom.numerator;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.filter.Filter;
import org.apache.isis.applib.query.QueryDefault;

@Named("Numerators")
public class Numerators extends AbstractFactoryAndRepository {

    @Override
    public String getId() {
        return "numerators";
    }

    public String iconName() {
        return "Numerator";
    }

    // //////////////////////////////////////

    @Hidden
    public Numerator create(NumeratorType type) {
        Numerator numerator = newTransientInstance(Numerator.class);
        numerator.setType(type);
        persist(numerator);
        return numerator;
    }

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Numerator find(final @Named("Numerator Type") NumeratorType type) {
        return firstMatch(queryForFind(type));
    }

    private static QueryDefault<Numerator> queryForFind(NumeratorType type) {
        return new QueryDefault<Numerator>(Numerator.class, "numerator_find", "type", type);
    }

    @ActionSemantics(Of.NON_IDEMPOTENT)
    public Numerator establish(NumeratorType type) {
        Numerator numerator = find(type);
        return numerator == null ? create(type) : numerator;
    }

    @ActionSemantics(Of.SAFE)
    public List<Numerator> allNumerators() {
        List<Numerator> allInstances = allInstances(Numerator.class);
        return allInstances;
    }

}
