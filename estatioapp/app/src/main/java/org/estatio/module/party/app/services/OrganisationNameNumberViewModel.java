package org.estatio.module.party.app.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.isis.applib.annotation.DomainObject;

import org.incode.module.base.dom.utils.TitleBuilder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DomainObject(objectType = "org.estatio.module.party.app.services.OrganisationNameNumberViewModel")
@XmlRootElement(name = "organisationNameNumberViewModel")
@XmlType(
        propOrder = {
                "organisationName",
                "chamberOfCommerceCode"
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
public class OrganisationNameNumberViewModel {

    public OrganisationNameNumberViewModel(final String organisationName, final String chamberOfCommerceCode){
        this.organisationName = organisationName;
        this.chamberOfCommerceCode = chamberOfCommerceCode;
    }

    public String title(){
        return TitleBuilder.start()
                .withName(getOrganisationName())
                .withName(getChamberOfCommerceCode())
                .toString();
    }

    @Getter @Setter
    private String organisationName;

    @Getter @Setter
    private String chamberOfCommerceCode;

}
