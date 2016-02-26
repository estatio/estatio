package org.estatio.canonical.party.v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.estatio.canonical.VersionedDto;

/**
 * Designed to be usable both as a view model (exposed from RO) and within the Camel ESB.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "majorVersion",
    "minorVersion"
})
@XmlRootElement(name = "bankAccountDto")
public class PartyDto implements VersionedDto {

    public PartyDto() {}

    @XmlElement(required = true, defaultValue = "1")
    public final String getMajorVersion() {
        return "1";
    }

    @XmlElement(required = true, defaultValue = "0")
    public String getMinorVersion() {
        return "0";
    }


}
