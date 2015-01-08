package org.estatio.dom.apptenancy;

import org.apache.isis.applib.annotation.Programmatic;
import org.estatio.dom.EstatioDomainObject;

public interface AccessOne<S> {
    @Programmatic
    public EstatioDomainObject<?> get(final S source);
}
