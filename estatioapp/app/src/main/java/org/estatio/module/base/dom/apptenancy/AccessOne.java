package org.estatio.module.base.dom.apptenancy;

import org.apache.isis.applib.annotation.Programmatic;
import org.estatio.module.base.dom.UdoDomainObject2;

public interface AccessOne<S> {
    @Programmatic
    public UdoDomainObject2<?> get(final S source);
}
