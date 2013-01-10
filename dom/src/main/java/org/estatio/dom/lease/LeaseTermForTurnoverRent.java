package org.estatio.dom.lease;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.ObjectType;
import org.estatio.dom.index.Index;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
//@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@Discriminator("LTRT")
//required since subtypes are rolling-up
@ObjectType("LTRT")
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
