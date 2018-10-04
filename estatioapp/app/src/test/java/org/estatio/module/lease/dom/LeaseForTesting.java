package org.estatio.module.lease.dom;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

@javax.jdo.annotations.Discriminator("org.estatio.module.lease.dom.LeaseForTesting")
public class LeaseForTesting extends Lease {

    public void setSecurityApplicationTenancyRepository(ApplicationTenancyRepository securityApplicationTenancyRepository){
        this.securityApplicationTenancyRepository = securityApplicationTenancyRepository;
    }

}
