package org.estatio.dom.apptenancy;

import org.apache.isis.applib.annotation.Programmatic;
import org.estatio.dom.EstatioDomainObject;

public interface AccessMany<S> {
    @Programmatic
    public Iterable<? extends EstatioDomainObject<?>> get(final S source);
}
