package org.estatio.app.budget;

import java.math.BigDecimal;

/**
 * Created by jodo on 06/08/15.
 */
public class IdentifierValuesOutputObject {

    public IdentifierValuesOutputObject() {}

    public IdentifierValuesOutputObject(
            final Object identifier,
            final BigDecimal value,
            final BigDecimal roundedValue,
            final BigDecimal delta,
            final boolean corrected
            ) {
        this.identifier = identifier;
        this.value = value;
        this.roundedValue = roundedValue;
        this.delta = delta;
        this.corrected = corrected;
    }

    private Object identifier;
    private BigDecimal value;
    private BigDecimal roundedValue;
    private BigDecimal delta;
    private boolean corrected;

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

    public BigDecimal getRoundedValue() {
        return roundedValue;
    }

    public void setRoundedValue(final BigDecimal roundedValue) {
        this.roundedValue = roundedValue;
    }

    public BigDecimal getDelta() {
        return delta;
    }

    public void setDelta(final BigDecimal delta) {
        this.delta = delta;
    }

    public boolean isCorrected() {
        return corrected;
    }

    public void setCorrected(final boolean corrected) {
        this.corrected = corrected;
    }


}
