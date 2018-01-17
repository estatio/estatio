package org.estatio.module.party.dom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.isis.applib.annotation.DomainObject;

import org.incode.module.base.dom.utils.TitleBuilder;

import lombok.Getter;
import lombok.Setter;

@DomainObject(objectType = "org.estatio.module.party.dom.Supplier")
@XmlRootElement(name = "supplier")
@XmlType(
        propOrder = {
            "organisation"
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
public class Supplier {

    public Supplier(){}

    public Supplier(final Organisation organisation){
        this.organisation = organisation;
    }

    public String title() {
        return TitleBuilder.start()
                .withName(getOrganisation().getName())
                .withName(getOrganisation().getChamberOfCommerceCode())
                .withReference(getOrganisation().getReference())
                .toString();
    }

    @Getter @Setter
    private Organisation organisation;

}
