package org.isisaddons.module.base.platform.fixturesupport;

import org.apache.isis.applib.services.registry.ServiceRegistry2;

public interface EnumWithUpsert<T> extends EnumWithFinder<T> {

    T upsertUsing(final ServiceRegistry2 serviceRegistry);

}

