package org.estatio.jdo;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.filter.Filter;
import org.apache.isis.applib.query.QueryDefault;

import org.estatio.dom.numerator.InvoiceNumberNumerator;
import org.estatio.dom.numerator.Numerator;
import org.estatio.dom.numerator.NumeratorType;
import org.estatio.dom.numerator.Numerators;

public class NumeratorsJdo extends Numerators {
    
    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Numerator find(
            final @Named("Type") NumeratorType type) {
        return firstMatch(queryForFind(type));
    }
    
    private static QueryDefault<Numerator> queryForFind(NumeratorType type) {
        return new QueryDefault<Numerator>(Numerator.class, "numerator_find", "type", type);
    }
    // }}
    
    public Numerator find2(final NumeratorType type) {
        return firstMatch(queryForFind2(type));
    }
    
    private static QueryDefault<InvoiceNumberNumerator> queryForFind2(NumeratorType type) {
        return new QueryDefault<InvoiceNumberNumerator>(InvoiceNumberNumerator.class, "numerator_find", "type", type);
    }

}
