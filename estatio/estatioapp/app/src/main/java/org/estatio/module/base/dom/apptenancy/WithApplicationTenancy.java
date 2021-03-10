package org.estatio.module.base.dom.apptenancy;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.HasAtPath;

public interface WithApplicationTenancy extends HasAtPath {

    ApplicationTenancy getApplicationTenancy();

    @Override
    String getAtPath();
}
