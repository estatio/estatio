package org.estatio.module.lease.dom;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.module.lease.dom.Lease;

public class LeaseForTesting extends Lease {

    public void setSecurityApplicationTenancyRepository(ApplicationTenancyRepository securityApplicationTenancyRepository){
        this.securityApplicationTenancyRepository = securityApplicationTenancyRepository;
    }

}
