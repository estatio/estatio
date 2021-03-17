package org.estatio.module.numerator.dom;

import java.math.BigInteger;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

@Mixin(method = "act")
public class Numerator_reset {

    private final Numerator numerator;

    public Numerator_reset(Numerator numerator) {
        this.numerator = numerator;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public Numerator act(final BigInteger lastValue) {
        numerator.setLastIncrement(lastValue);
        return numerator;
    }

    public BigInteger default0Act() {
        return numerator.getLastIncrement();
    }


}
