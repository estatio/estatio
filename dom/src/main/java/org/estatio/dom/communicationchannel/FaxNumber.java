package org.estatio.dom.communicationchannel;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Title;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator("FAXN") // required since subtypes are rolling-up
@ObjectType("FAXN")
public class FaxNumber extends CommunicationChannel {

    @Override
    public String getName() {
        return "Fax ".concat(getFaxNumber());
    }

    // {{ Number (title, attribute)
    private String faxNumber;

    @Title(prepend = "Fax ", sequence = "1")
    @MemberOrder(sequence = "1")
    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(final String number) {
        this.faxNumber = number;
    }
    // }}

}
