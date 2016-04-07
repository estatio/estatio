package org.estatio.canonical.communicationchannel.v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.isis.schema.common.v1.OidDto;

import org.estatio.canonical.HasSelfDto;
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
    "self",
    "address1",
    "address2",
    "address3",
    "postalCode",
    "city",
    "state",
    "country",
})
@XmlRootElement(name = "postalAddressDto")
public class PostalAddressDto implements VersionedDto, HasSelfDto {

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
    protected OidDto self;

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
    protected String stateReference;

    @XmlElement(required = false)
    @Getter @Setter
    protected String stateName;

    /**
     * 3 characters in length.
     */
    @XmlElement(required = true)
    @Getter @Setter
    protected String countryReference;

    @XmlElement(required = true)
    @Getter @Setter
    protected String countryAlpha2Code;

    @XmlElement(required = true)
    @Getter @Setter
    protected String countryName;

}
