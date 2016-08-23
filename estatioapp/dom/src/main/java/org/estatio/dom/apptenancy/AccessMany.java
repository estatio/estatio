package org.estatio.dom.apptenancy;

import org.apache.isis.applib.annotation.Programmatic;
import org.estatio.dom.UdoDomainObject2;

public interface AccessMany<S> {
    @Programmatic
    public Iterable<? extends UdoDomainObject2<?>> get(final S source);
}
