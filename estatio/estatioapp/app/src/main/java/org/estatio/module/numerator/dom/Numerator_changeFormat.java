package org.estatio.module.numerator.dom;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

@Mixin(method = "act")
public class Numerator_changeFormat {

    private final Numerator numerator;

    public Numerator_changeFormat(Numerator numerator) {
        this.numerator = numerator;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public Numerator act(final String newFormat) {
        numerator.setFormat(newFormat);
        return numerator;
    }

    public String default0Act() {
        return numerator.getFormat();
    }

}
