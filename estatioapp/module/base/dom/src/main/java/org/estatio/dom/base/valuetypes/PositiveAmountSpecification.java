package org.estatio.dom.base.valuetypes;

import java.math.BigDecimal;

import org.apache.isis.applib.spec.AbstractSpecification;

public class PositiveAmountSpecification extends AbstractSpecification<BigDecimal> {

    @Override
    public String satisfiesSafely(final BigDecimal proposedValue) {
        if(proposedValue == null) return null;
        return proposedValue.compareTo(BigDecimal.ZERO) <= 0 ? "Must be a positive amount" : null;
    }
}
