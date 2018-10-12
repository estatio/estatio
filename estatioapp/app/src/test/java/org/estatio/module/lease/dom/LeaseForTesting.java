package org.estatio.module.lease.dom;

import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

@Programmatic
public class LeaseForTesting extends Lease {

    public void setSecurityApplicationTenancyRepository(ApplicationTenancyRepository securityApplicationTenancyRepository){
        this.securityApplicationTenancyRepository = securityApplicationTenancyRepository;
    }

}
