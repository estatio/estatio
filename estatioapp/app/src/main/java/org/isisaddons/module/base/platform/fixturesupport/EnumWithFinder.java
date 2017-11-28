package org.isisaddons.module.base.platform.fixturesupport;

import org.apache.isis.applib.services.registry.ServiceRegistry2;

public interface EnumWithFinder<T> {

    T findUsing(final ServiceRegistry2 serviceRegistry);

}

