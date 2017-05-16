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

    public SupplierImportLine(){}

    public SupplierImportLine(
            final String elmcode,
            final String name,
            final String add1,
            final String add3,
            final String postcode,
            final String country) {
        this.elmcode = elmcode;
        this.name = name;
        this.add1 = add1;
        this.add3 = add3;
        this.postcode = postcode;
        this.country = country;
    }

    @Getter @Setter
    public String elmcode; // reference

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String add1; // address

    @Getter @Setter
    private String add3; // city

    @Getter @Setter
    private String postcode;

    @Getter @Setter
    private String country;

    @Action
    public List<Object> importLine() {

        Organisation organisation = supplierImportService.findOrCreateOrganisationAndAddressByName(getElmcode(), getName(), getAdd1(), getPostcode(), getAdd3(), getCountry());

        return Lists.newArrayList(organisation);

    }

    @Inject
    private SupplierImportService supplierImportService;


}
