package org.estatio.dom.charge.viewmodels;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.dom.Importable;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeGroup;
import org.estatio.dom.charge.ChargeGroups;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.Taxes;

import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL)
public class ChargeImport implements ExcelFixtureRowHandler, Importable {

    public String title(){
        return "charge import";
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

    @Programmatic
    @Override
    public List<Object> handleRow(FixtureScript.ExecutionContext executionContext, ExcelFixture excelFixture, Object previousRow) {
        return importData(previousRow);
    }

    // REVIEW: is this view model actually ever surfaced in the UI?
    @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION, publishing = Publishing.DISABLED, semantics = SemanticsOf.IDEMPOTENT)
    public List<Object> importData() {
        return importData(null);
    }

    @Override
    @Programmatic
    public List<Object> importData(Object previousRow) {

        final ChargeGroup chargeGroup = fetchOrCreateChargeGroup(chargeGroupReference, chargeGroupName);

        final ApplicationTenancy applicationTenancy = securityApplicationTenancyRepository.findByPath(atPath);

        final Tax tax = taxes.findOrCreate(taxReference, taxReference, applicationTenancy);
        final Charge charge = chargeRepository.newCharge(applicationTenancy, reference, name, description, tax, chargeGroup);

        charge.setExternalReference(externalReference);
        charge.setSortOrder(sortOrder);

        return Lists.newArrayList(charge);
    }

    private ChargeGroup fetchOrCreateChargeGroup(final String reference, final String name) {
        ChargeGroup chargeGroup = chargeGroups.findChargeGroup(reference);
        if (chargeGroup == null) {
            chargeGroup = chargeGroups.createChargeGroup(reference, name);
        }
        chargeGroup.setName(name);
        return chargeGroup;
    }

    @Inject
    private ApplicationTenancyRepository securityApplicationTenancyRepository;

    @Inject
    private Taxes taxes;

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    private ChargeGroups chargeGroups;

}
