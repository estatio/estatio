package org.estatio.dom.lease;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

public class LeaseForTest extends Lease {

    public void setSecurityApplicationTenancyRepository(ApplicationTenancyRepository securityApplicationTenancyRepository){
        this.securityApplicationTenancyRepository = securityApplicationTenancyRepository;
    }

}
