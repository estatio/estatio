package org.estatio.dom.communicationchannel;

import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Title;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy=InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator("EMAI") // required since subtypes are rolling-up
@ObjectType("EMAI")
public class EmailAddress extends CommunicationChannel {

    @Title
    @Override
    public String getName() {
        return getAddress();
    }

    // //////////////////////////////////////

    private String address;

    @MemberOrder(sequence = "1")
    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

}