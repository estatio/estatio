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
@Discriminator("PHON") // required since subtypes are rolling-up
@ObjectType("PHON")
public class PhoneNumber extends CommunicationChannel {

    // {{ Number (attribute)
    private String phoneNumber;

    @Title(sequence = "1", prepend = "Phone ")
    @MemberOrder(sequence = "1")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(final String number) {
        this.phoneNumber = number;
    }
    // }}

}
