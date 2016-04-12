package org.estatio.canonical.party.v1;

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
    "reference",
    "name",
    "legalPostalAddress"
})
@XmlRootElement(name = "partyDto")
public class PartyDto implements VersionedDto, HasSelfDto {


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
    protected String reference;

    @XmlElement(required = true)
    @Getter @Setter
    protected String name;


    @XmlElement(required = false)
    @Getter @Setter
    protected OidDto legalPostalAddress;



}
