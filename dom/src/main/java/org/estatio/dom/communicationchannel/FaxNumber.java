package org.estatio.dom.communicationchannel;

import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
public class FaxNumber extends CommunicationChannel {

    @Override
    @Title
    public String getName() {
        return "Fax ".concat(getFaxNumber());
    }

    // //////////////////////////////////////

    private String faxNumber;

    @MemberOrder(sequence = "1")
    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(final String number) {
        this.faxNumber = number;
    }

    public String disableFaxNumber() {
        return getStatus().isLocked() ? "Cannot modify when locked" : null;
    }

}
