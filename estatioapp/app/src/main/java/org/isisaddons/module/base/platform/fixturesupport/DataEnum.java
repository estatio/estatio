package org.isisaddons.module.base.platform.fixturesupport;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

public interface DataEnum<T> {

    T upsertUsing(final ServiceRegistry2 serviceRegistry);
    T findUsing(final ServiceRegistry2 serviceRegistry);
    FixtureScript toFixtureScript();

}

