package org.estatio.dom.lease;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

public class LeaseForTesting extends Lease {

    public void setSecurityApplicationTenancyRepository(ApplicationTenancyRepository securityApplicationTenancyRepository){
        this.securityApplicationTenancyRepository = securityApplicationTenancyRepository;
    }

}
