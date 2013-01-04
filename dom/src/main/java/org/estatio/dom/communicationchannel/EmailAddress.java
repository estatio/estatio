package org.estatio.dom.communicationchannel;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Title;

@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.SUPERCLASS_TABLE)
@Discriminator("EMAI") // required since subtypes are rolling-up
@ObjectType("EMAI")
public class EmailAddress extends CommunicationChannel {

    // {{ EmailAddress (attribute)
    private String address;

    @Title
    @MemberOrder(sequence = "1")
    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }
    // }}

}