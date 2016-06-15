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

@DomainObject(nature = Nature.VIEW_MODEL)
public class ChargeImport implements ExcelFixtureRowHandler, Importable {

    private static int numberOfRecords = 0;
    private static int numberOfChargeGroupsCreated = 0;
    private static int numberOfChargesCreated = 0;

    private String chargeReference;
    private String chargeGroupName;
    private String chargeName;
    private String chargeDescription;
    private String chargeTaxReference;
    private String applicationTenancyPath;

    @Override
    public List<Object> handleRow(FixtureScript.ExecutionContext executionContext, ExcelFixture excelFixture, Object o) {
        return importData();
    }

    private ChargeGroup findOrCreateChargeGroup(String name) {

        String chargeRef = name.toUpperCase().replace(" ","_");
        if (chargeRef.length()>20) {
            chargeRef = chargeRef.substring(0,20);
        }

        ChargeGroup group = chargeGroups.findChargeGroup(chargeRef);
        if (group == null ) {
            group = chargeGroups.createChargeGroup(chargeRef, name);
            numberOfChargeGroupsCreated ++;
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

        ref = ref.toUpperCase().replace(" ","_");
        if (ref.length()>24) {
            ref = ref.substring(0,24);
        }

        Charge charge = chargeRepository.findByReference(ref);
        if (charge == null) {
            charge = chargeRepository.newCharge(applicationTenancy, ref,name,chargeDescription,tax, chargeGroup);
            numberOfChargesCreated++;
        }

        return charge;
    }

    @Override
    @Action(invokeOn= InvokeOn.OBJECT_AND_COLLECTION)
    public List<Object> importData() {

        ApplicationTenancy applicationTenancy = applicationTenancyRepository.findByPath("/" + getApplicationTenancyPath());
        Tax tax = taxes.findByReference(getChargeTaxReference());

        numberOfRecords ++;

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

    @Override public List<Class> importAfter() {
        return null;
    }

    @MemberOrder(sequence = "1")
    public String getChargeReference() {
        return chargeReference;
    }

    public void setChargeReference(String chargeReference) {
        this.chargeReference = chargeReference;
    }

    @MemberOrder(sequence = "2")
    public String getChargeGroupName() {
        return chargeGroupName;
    }

    public void setChargeGroupName(String chargeGroupName) {
        this.chargeGroupName = chargeGroupName;
    }

    @MemberOrder(sequence = "3")
    public String getChargeName() {
        return chargeName;
    }

    public void setChargeName(String chargeName) {
        this.chargeName = chargeName;
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
        return str == null? null : StringUtils.capitalize(str.toLowerCase());
    }

    @Inject
    DomainObjectContainer container;

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    private ChargeGroups chargeGroups;

    @Inject
    private Taxes taxes;

    @Inject
    private ApplicationTenancyRepository applicationTenancyRepository;

}
