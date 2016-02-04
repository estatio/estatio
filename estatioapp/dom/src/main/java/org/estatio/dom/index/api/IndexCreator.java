package org.estatio.dom.index.api;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.index.Index;

public interface IndexCreator {

    Index findOrCreateIndex(
            final ApplicationTenancy applicationTenancy,
            final String reference,
            final String name);
}
