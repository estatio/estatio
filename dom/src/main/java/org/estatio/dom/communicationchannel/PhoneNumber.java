package org.estatio.dom.communicationchannel;

import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.util.TitleBuffer;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator("PHON") // required since subtypes are rolling-up
@ObjectType("PHON")
public class PhoneNumber extends CommunicationChannel {

    @Title
    @Override
    public String getName() {
        return new TitleBuffer("Phone").append(getPhoneNumber()).toString();
    }

    // //////////////////////////////////////

    private String phoneNumber;

    @MemberOrder(sequence = "1")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(final String number) {
        this.phoneNumber = number;
    }

}
