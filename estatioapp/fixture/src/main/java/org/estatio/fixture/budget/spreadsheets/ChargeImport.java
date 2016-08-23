package org.estatio.fixture.budget.spreadsheets;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.dom.Importable;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeGroup;
import org.estatio.dom.charge.ChargeGroupRepository;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.Taxes;

import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL)
public class ChargeImport implements ExcelFixtureRowHandler, Importable {

    private static int numberOfRecords = 0;
    private static int numberOfChargeGroupsCreated = 0;
    private static int numberOfChargesCreated = 0;

    @Getter @Setter
    private String chargeReference;

    @Getter @Setter
    private String chargeGroupName;

    @Getter @Setter
    private String chargeName;

    @Getter @Setter
    private String chargeDescription;

    @Getter @Setter
    private String chargeTaxReference;

    @Getter @Setter
    private String applicationTenancyPath;

    private ChargeGroup findOrCreateChargeGroup(String name) {

        String chargeRef = name.toUpperCase().replace(" ", "_");
        if (chargeRef.length() > 20) {
            chargeRef = chargeRef.substring(0, 20);
        }

        ChargeGroup group = chargeGroupRepository.findChargeGroup(chargeRef);
        if (group == null) {
            group = chargeGroupRepository.createChargeGroup(chargeRef, name);
            numberOfChargeGroupsCreated++;
        }
        return group;

    }

    private Charge findOrCreateCharge(
            ApplicationTenancy applicationTenancy,
            String ref,
            String name,
            String chargeDescription,
            Tax tax,
            ChargeGroup chargeGroup) {

        ref = ref.toUpperCase().replace(" ", "_");
        if (ref.length() > 24) {
            ref = ref.substring(0, 24);
        }

        Charge charge = chargeRepository.findByReference(ref);
        if (charge == null) {
            charge = chargeRepository.newCharge(applicationTenancy, ref, name, chargeDescription, tax, chargeGroup);
            numberOfChargesCreated++;
        }

        return charge;
    }

    @Programmatic
    @Override
    public List<Object> handleRow(FixtureScript.ExecutionContext executionContext, ExcelFixture excelFixture, Object previousRow) {
        return importData();
    }

    // REVIEW: is this view model actually ever surfaced in the UI?
    @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION)
    public List<Object> importData() {
        return importData(null);
    }

    @Override
    @Programmatic
    public List<Object> importData(Object previousRow) {

        ApplicationTenancy applicationTenancy = applicationTenancyRepository.findByPath("/" + getApplicationTenancyPath());
        Tax tax = taxes.findByReference(getChargeTaxReference());

        numberOfRecords++;

        try {

            //find or create charge group
            ChargeGroup chargeGroup = findOrCreateChargeGroup(getChargeGroupName());

            // find or create charge
            Charge charge = findOrCreateCharge(
                    applicationTenancy,
                    getChargeReference(),
                    getChargeName(),
                    getChargeDescription(),
                    tax,
                    chargeGroup
            );

            System.out.println("Number of records read: " + numberOfRecords);
            System.out.println("Number of chargeGroups created: " + numberOfChargeGroupsCreated);
            System.out.println("Number of charges created: " + numberOfChargesCreated);

        } catch (Exception e) {
            // REVIEW: ignore any garbage
            System.out.println("ERROR OR GARBAGE");
            container.informUser("ERRORS WERE FOUND!");
        }

        return Lists.newArrayList();
    }

    @MemberOrder(sequence = "4")
    public String getChargeDescription() {
        return chargeDescription;
    }

    public void setChargeDescription(String chargeDescription) {
        this.chargeDescription = chargeDescription;
    }

    @MemberOrder(sequence = "5")
    public String getChargeTaxReference() {
        return chargeTaxReference;
    }

    public void setChargeTaxReference(String chargeTaxReference) {
        this.chargeTaxReference = chargeTaxReference;
    }

    @MemberOrder(sequence = "6")
    public String getApplicationTenancyPath() {
        return applicationTenancyPath;
    }

    public void setApplicationTenancyPath(String applicationTenancyPath) {
        this.applicationTenancyPath = applicationTenancyPath;
    }

    private static String pretty(final String str) {
        return str == null ? null : StringUtils.capitalize(str.toLowerCase());
    }

    @Inject
    DomainObjectContainer container;

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    private ChargeGroupRepository chargeGroupRepository;

    @Inject
    private Taxes taxes;

    @Inject
    private ApplicationTenancyRepository applicationTenancyRepository;

}
