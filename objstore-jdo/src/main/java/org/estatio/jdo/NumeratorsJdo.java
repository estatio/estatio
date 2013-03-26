package org.estatio.jdo;

import org.estatio.dom.numerator.Numerator;
import org.estatio.dom.numerator.NumeratorType;
import org.estatio.dom.numerator.Numerators;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;

public class NumeratorsJdo extends Numerators {

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Numerator find(final @Named("Numerator Type") NumeratorType type) {
        return firstMatch(queryForFind(type));
    }

    private static QueryDefault<Numerator> queryForFind(NumeratorType type) {
        return new QueryDefault<Numerator>(Numerator.class, "numerator_find", "type", type);
    }
}
