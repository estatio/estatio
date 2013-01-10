package org.estatio.dom.lease;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;


import org.apache.isis.applib.annotation.Hidden;
import org.estatio.dom.index.Index;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@Discriminator("LTRI")
public class LeaseTermForServiceCharge extends LeaseTerm {

    // {{
    public void verify() {
        //

    }

    // }}

    // {{
    private LeaseTerms leaseTermsService;

    public void setLeaseTermsService(LeaseTerms leaseTerms) {
        this.leaseTermsService = leaseTerms;
    }

    // }}
    @Hidden
    public Index getIndex() {
        return this.getLeaseItem().getIndex();
    }

}
