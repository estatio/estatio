package org.estatio.capex.dom.impmgr;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;

import org.estatio.dom.party.Organisation;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.capex.dom.impmgr.SupplierImportLine"
)
public class SupplierImportLine {

    public SupplierImportLine(
            final String reference,
            final String name,
            final String address,
            final String city,
            final String postcode,
            final String country) {
        this.reference = reference;
        this.name = name;
        this.address = address;
        this.city = city;
        this.postcode = postcode;
        this.country = country;
    }

    @Getter @Setter
    public String reference;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String address;

    @Getter @Setter
    private String city;

    @Getter @Setter
    private String postcode;

    @Getter @Setter
    private String country;

    @Action
    public List<Object> importLine() {

        Organisation organisation = supplierImportService.findOrCreateOrganisationAndAddressByName(getReference(), getName(), getAddress(), getPostcode(), getCity(), getCountry());

        return Lists.newArrayList(organisation);

    }

    @Inject
    private SupplierImportService supplierImportService;


}
