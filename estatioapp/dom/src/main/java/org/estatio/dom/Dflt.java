package org.estatio.dom;

import java.util.List;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.apache.isis.applib.annotation.Programmatic;

public class Dflt {

    private Dflt(){}

    @Programmatic
    public static ApplicationTenancy of(final List<ApplicationTenancy> choices) {
        return choices.size() == 1? choices.get(0): null;
    }
}
