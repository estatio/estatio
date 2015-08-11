package org.estatio.app.budget;

import java.math.BigDecimal;

/**
 * Created by jodo on 06/08/15.
 */
public class IdentifierValueInputPair {

    public IdentifierValueInputPair() {}

    public IdentifierValueInputPair(
            final Object identifier,
            final BigDecimal value) {
        this.identifier = identifier;
        this.value = value;
    }

    private Object identifier;
    private BigDecimal value;

    public Object getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final Object identifier) {
        this.identifier = identifier;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(final BigDecimal value) {
        this.value = value;
    }
}
