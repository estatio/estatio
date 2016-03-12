package org.estatio.canonical.bankmandate.v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.isis.schema.common.v1.OidDto;

import org.estatio.canonical.VersionedDto;

import lombok.Getter;
import lombok.Setter;

/**
 * Designed to be usable both as a view model (exposed from RO) and within the Camel ESB.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "majorVersion",
        "minorVersion",
        "reference",
        "sequenceType",
        "scheme",
        "status",
        "signatureDate",
        "bankAccount"
})
@XmlRootElement(name = "bankMandateDto")
public class BankMandateDto implements VersionedDto {

    public BankMandateDto() {}

    @XmlElement(required = true, defaultValue = "1")
    public final String getMajorVersion() {
        return "1";
    }

    @XmlElement(required = true, defaultValue = "0")
    public String getMinorVersion() {
        return "0";
    }

    @XmlElement(required = true)
    @Getter @Setter
    private String reference;

    @XmlElement(required = true)
    @Getter @Setter
    private SequenceType sequenceType;

    @XmlElement(required = true)
    @Getter @Setter
    private Scheme scheme;

    @XmlElement(required = true)
    @Getter @Setter
    private Status status;

    @XmlElement(required = true)
    @Getter @Setter
    private XMLGregorianCalendar signatureDate;

    @XmlElement(required = true)
    @Getter @Setter
    private OidDto bankAccount;

}
