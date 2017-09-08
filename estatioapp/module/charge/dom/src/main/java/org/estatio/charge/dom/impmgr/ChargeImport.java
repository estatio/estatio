package org.estatio.charge.dom.impmgr;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.charge.dom.menu.ChargeMenu;
import org.estatio.dom.Importable;
import org.estatio.dom.charge.Applicability;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeGroup;
import org.estatio.dom.charge.ChargeGroupRepository;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.tax.dom.Tax;
import org.estatio.tax.dom.TaxRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.app.services.budget.ChargeImport"
)
public class ChargeImport implements ExcelFixtureRowHandler, Importable {

    public String title() {
        return "charge import";
    }

    public ChargeImport(){}

    public ChargeImport(
            final String atPath,
            final String reference,
            final String name,
            final String description,
            final String taxReference,
            final String chargeGroupReference,
            final String chargeGroupName,
            final String applicability,
            final String parent
    ){
        this();
        this.atPath = atPath;
        this.reference = reference;
        this.name = name;
        this.description = description;
        this.taxReference = taxReference;
        this.chargeGroupReference = chargeGroupReference;
        this.chargeGroupName = chargeGroupName;
        this.applicability = applicability;
        this.parent = parent;
    }

    @Getter @Setter
    private String atPath;

    @Getter @Setter
    private String reference;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String description;

    @Getter @Setter
    private String taxReference;

    @Getter @Setter
    private String sortOrder;

    @Getter @Setter
    private String chargeGroupReference;

    @Getter @Setter
    private String chargeGroupName;

    @Getter @Setter
    private String externalReference;

    @Getter @Setter
    private String applicability;

    @Getter @Setter
    private String parent;

    @Programmatic
    @Override
    public List<Object> handleRow(FixtureScript.ExecutionContext executionContext, ExcelFixture excelFixture, Object previousRow) {
        return importData(previousRow);
    }

    @Override
    @Programmatic
    public List<Object> importData(Object previousRow) {

        ChargeGroup chargeGroup = null;

        if (getChargeGroupReference()!=null) {
            chargeGroup = findOrCreateChargeGroup(chargeGroupReference, chargeGroupName);
        }

        final ApplicationTenancy applicationTenancy = securityApplicationTenancyRepository.findByPath(atPath);

        final Applicability applicability = this.applicability != null ? Applicability.valueOf(this.applicability) : Applicability.IN_AND_OUT;

        Tax tax = null;
        if (getTaxReference()!=null) {
            tax = taxRepository.findOrCreate(taxReference, taxReference, applicationTenancy);
        }

        if (getReference()==null){
            setReference(getName());
        }

        final Charge charge = wrap(chargeMenu)
                .newCharge(applicationTenancy, reference, name, description, tax, chargeGroup, applicability);

        if (getParent()!=null){
            Charge parentCharge = chargeRepository.findByReference(getParent());
            if (parentCharge!=null){
                charge.setParent(parentCharge);
            }
        }

        if (externalReference !=  null) {
            charge.setExternalReference(externalReference);
        }
        if(sortOrder != null){
            charge.setSortOrder(sortOrder);
        }

        return Lists.newArrayList(charge);
    }

    private ChargeGroup findOrCreateChargeGroup(final String reference, final String name) {
        ChargeGroup chargeGroup = chargeGroupRepository.findChargeGroup(reference);
        if (chargeGroup == null) {
            chargeGroup = chargeGroupRepository.createChargeGroup(reference, name);
        }
        chargeGroup.setName(name);
        return chargeGroup;
    }

    private <T> T wrap(final T obj) {
        return wrapperFactory.wrap(obj);
    }

    @Inject
    private ApplicationTenancyRepository securityApplicationTenancyRepository;

    @Inject
    private TaxRepository taxRepository;

    @Inject
    private ChargeMenu chargeMenu;

    @Inject
    private WrapperFactory wrapperFactory;

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    private ChargeGroupRepository chargeGroupRepository;

}
