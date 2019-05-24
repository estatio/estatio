package org.estatio.module.party.app.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.schema.utils.jaxbadapters.JodaLocalDateStringAdapter;

import org.incode.module.base.dom.utils.TitleBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DomainObject(objectType = "org.estatio.module.party.app.services.OrganisationNameNumberViewModel")
@XmlRootElement(name = "organisationNameNumberViewModel")
@XmlType(
        propOrder = {
                "organisationName",
                "chamberOfCommerceCode",
                "entryDate"
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
@AllArgsConstructor
public class OrganisationNameNumberViewModel {

    public String title(){
        return TitleBuilder.start()
                .withName(getOrganisationName())
                .withName(getChamberOfCommerceCode())
                .withReference(getEntryDate().toString())
                .toString();
    }

    @Getter @Setter
    private String organisationName;

    @Getter @Setter
    private String chamberOfCommerceCode;

    @Getter @Setter
    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    private LocalDate entryDate;

}
