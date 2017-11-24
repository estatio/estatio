package org.estatio.module.index.dom.api;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.index.dom.Index;

public interface IndexCreator {

    Index findOrCreateIndex(
            final ApplicationTenancy applicationTenancy,
            final String reference,
            final String name);
}
