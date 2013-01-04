package com.eurocommercialproperties.estatio.dom.lease;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;

import com.eurocommercialproperties.estatio.dom.index.Index;

import org.apache.isis.applib.annotation.Hidden;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@Discriminator("LTRI")
public class LeaseTermForTurnoverRent extends LeaseTerm {

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
