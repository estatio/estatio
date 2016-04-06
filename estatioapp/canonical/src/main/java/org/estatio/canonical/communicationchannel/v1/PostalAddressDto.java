package org.estatio.canonical.communicationchannel.v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
    "address1",
    "address2",
    "address3",
    "postalCode",
    "city",
    "state",
    "country",
})
@XmlRootElement(name = "postalAddressDto")
public class PostalAddressDto implements VersionedDto {

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
    protected String address1;

    @XmlElement(required = false)
    @Getter @Setter
    protected String address2;

    @XmlElement(required = false)
    @Getter @Setter
    protected String address3;

    @XmlElement(required = false)
    @Getter @Setter
    protected String postalCode;

    @XmlElement(required = false)
    @Getter @Setter
    protected String city;

    @XmlElement(required = false)
    @Getter @Setter
    protected String state;

    @XmlElement(required = true)
    @Getter @Setter
    protected String country;

}
