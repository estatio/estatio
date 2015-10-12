package org.estatio.fixture.budget.spreadsheets;

import org.apache.commons.lang3.StringUtils;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.ViewModel;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.asset.UnitRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.Budgets;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.budgetitem.BudgetItems;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTables;
import org.estatio.dom.budgeting.schedule.Schedule;
import org.estatio.dom.budgeting.schedule.Schedules;
import org.estatio.dom.budgeting.scheduleitem.ScheduleItems;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeGroup;
import org.estatio.dom.charge.ChargeGroups;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.Taxes;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;
import org.joda.time.LocalDate;

import javax.inject.Inject;
import java.math.BigDecimal;

@ViewModel
public class BudgetImport implements Importable {

    private static int numberOfRecords = 0;
    private static int numberOfBudgetsCreated = 0;
    private static int numberOfChargesCreated = 0;
    private static int numberOfBudgetItemsCreated = 0;
    private static int numberOfScheduleItemsACreated = 0;
    private static int numberOfScheduleItemsBCreated = 0;
    private static int numberOfScheduleItemsB1Created = 0;
    private static int numberOfScheduleItemsB2Created = 0;
    private static int numberOfScheduleItemsCCreated = 0;
    private static int numberOfScheduleItemsC1Created = 0;
    private static int numberOfScheduleItemsDCreated = 0;
    private static int numberOfScheduleItemsECreated = 0;
    private static int numberOfScheduleItemsDirCreated = 0;
    private static int numberOfScheduleItemsDirAcquaCreated = 0;

    private String charge;
    private String chargeExternalReference;
    private String chargeGroupName;
    private String chargeName;
    private String chargeDescription;
    private String chargeTaxReference;
    private BigDecimal budgetedValue;
    private BigDecimal tabellaAPercentage;
    private BigDecimal tabellaBPercentage;
    private BigDecimal tabellaBUnoPercentage;
    private BigDecimal tabellaBDuePercentage;
    private BigDecimal tabellaCPercentage;
    private BigDecimal tabellaCUnoPercentage;
    private BigDecimal tabellaDPercentage;
    private BigDecimal tabellaEPercentage;
    private BigDecimal consumiDirettiPercentage;
    private BigDecimal consumiDirettiAcquaPercentage;

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getChargeExternalReference() {
        return chargeExternalReference;
    }

    public void setChargeExternalReference(String chargeExternalReference) {
        this.chargeExternalReference = chargeExternalReference;
    }

    public String getChargeGroupName() {
        return chargeGroupName;
    }

    public void setChargeGroupName(String chargeGroupName) {
        this.chargeGroupName = chargeGroupName;
    }

    public String getChargeName() {
        return chargeName;
    }

    public void setChargeName(String chargeName) {
        this.chargeName = chargeName;
    }

    public String getChargeDescription() {
        return chargeDescription;
    }

    public void setChargeDescription(String chargeDescription) {
        this.chargeDescription = chargeDescription;
    }

    public String getChargeTaxReference() {
        return chargeTaxReference;
    }

    public void setChargeTaxReference(String chargeTaxReference) {
        this.chargeTaxReference = chargeTaxReference;
    }

    public BigDecimal getBudgetedValue() {
        return budgetedValue;
    }

    public void setBudgetedValue(BigDecimal budgetedValue) {
        this.budgetedValue = budgetedValue;
    }

    public BigDecimal getTabellaAPercentage() {
        return tabellaAPercentage;
    }

    public void setTabellaAPercentage(BigDecimal tabellaAPercentage) {
        this.tabellaAPercentage = tabellaAPercentage;
    }

    public BigDecimal getTabellaBPercentage() {
        return tabellaBPercentage;
    }

    public void setTabellaBPercentage(BigDecimal tabellaBPercentage) {
        this.tabellaBPercentage = tabellaBPercentage;
    }

    public BigDecimal getTabellaBUnoPercentage() {
        return tabellaBUnoPercentage;
    }

    public void setTabellaBUnoPercentage(BigDecimal tabellaBUnoPercentage) {
        this.tabellaBUnoPercentage = tabellaBUnoPercentage;
    }

    public BigDecimal getTabellaBDuePercentage() {
        return tabellaBDuePercentage;
    }

    public void setTabellaBDuePercentage(BigDecimal tabellaBDuePercentage) {
        this.tabellaBDuePercentage = tabellaBDuePercentage;
    }

    public BigDecimal getTabellaCPercentage() {
        return tabellaCPercentage;
    }

    public void setTabellaCPercentage(BigDecimal tabellaCPercentage) {
        this.tabellaCPercentage = tabellaCPercentage;
    }

    public BigDecimal getTabellaCUnoPercentage() {
        return tabellaCUnoPercentage;
    }

    public void setTabellaCUnoPercentage(BigDecimal tabellaCUnoPercentage) {
        this.tabellaCUnoPercentage = tabellaCUnoPercentage;
    }

    public BigDecimal getTabellaDPercentage() {
        return tabellaDPercentage;
    }

    public void setTabellaDPercentage(BigDecimal tabellaDPercentage) {
        this.tabellaDPercentage = tabellaDPercentage;
    }

    public BigDecimal getTabellaEPercentage() {
        return tabellaEPercentage;
    }

    public void setTabellaEPercentage(BigDecimal tabellaEPercentage) {
        this.tabellaEPercentage = tabellaEPercentage;
    }

    public BigDecimal getConsumiDirettiPercentage() {
        return consumiDirettiPercentage;
    }

    public void setConsumiDirettiPercentage(BigDecimal consumiDirettiPercentage) {
        this.consumiDirettiPercentage = consumiDirettiPercentage;
    }

    public BigDecimal getConsumiDirettiAcquaPercentage() {
        return consumiDirettiAcquaPercentage;
    }

    public void setConsumiDirettiAcquaPercentage(BigDecimal consumiDirettiAcquaPercentage) {
        this.consumiDirettiAcquaPercentage = consumiDirettiAcquaPercentage;
    }


    @Override
    public void importData() {

        final Property property = propertyRepository.findPropertyByReference("CAR");
        final Tax tax = taxes.findByReference("ITA-VATSTD");
        final ChargeGroup chargeGroup = chargeGroups.findChargeGroup("SERVICE_CHARGE");
        final Charge targetCharge = charges.findByReference("ITA_SERVICE_CHARGE");
        final LocalDate startDate = new LocalDate(2014,01,01);
        final LocalDate endDate = new LocalDate(2014,12,31);


        numberOfRecords ++;

        try {

            //create budget
            Budget budget = budgets.findOrCreateBudget(property, startDate, endDate);
            numberOfBudgetsCreated ++;

            // create charge
            String chargeName;
            if (getChargeDescription().length()>10) {
                chargeName = getCharge().concat(getChargeDescription().toUpperCase().substring(0, 10));
            } else {
                chargeName = getCharge().concat(getChargeDescription().toUpperCase());
            }
            Charge charge = charges.newCharge(
                    property.getApplicationTenancy(),
                    getCharge(),
                    chargeName,
                    getChargeDescription(),
                    tax,
                    chargeGroup);
            numberOfChargesCreated ++;

            //create budget item
            BudgetItem budgetItem = budgetItems.newBudgetItem(budget, getBudgetedValue(), charge);
            numberOfBudgetItemsCreated ++;

            //create schedule for budget item and tabella A
            KeyTable keyTableA = keyTables.findByPropertyAndNameAndStartDate(property, "Tabella A", startDate);
            Schedule schedule = schedules.findOrCreateSchedule(property,budget,startDate,endDate, targetCharge, Schedule.Status.OPEN);

            if (getTabellaAPercentage() != null) {
                scheduleItems.newScheduleItem(schedule, keyTableA, budgetItem, getTabellaAPercentage());
                numberOfScheduleItemsACreated ++;
            }

            //create schedule for budget item and tabella B
            KeyTable keyTableB = keyTables.findByPropertyAndNameAndStartDate(property, "Tabella B", startDate);
            if (getTabellaBPercentage() != null) {
                scheduleItems.newScheduleItem(schedule, keyTableB, budgetItem, getTabellaBPercentage());
                numberOfScheduleItemsBCreated ++;
            }

            //create schedule for budget item and tabella B1
            KeyTable keyTableB1 = keyTables.findByPropertyAndNameAndStartDate(property, "Tabella B1", startDate);
            if (getTabellaBUnoPercentage() != null) {
                scheduleItems.newScheduleItem(schedule, keyTableB1, budgetItem, getTabellaBUnoPercentage());
                numberOfScheduleItemsB1Created ++;
            }

            //create schedule for budget item and tabella B2
            KeyTable keyTableB2 = keyTables.findByPropertyAndNameAndStartDate(property, "Tabella B2", startDate);
            if (getTabellaBDuePercentage() != null) {
                scheduleItems.newScheduleItem(schedule, keyTableB2, budgetItem, getTabellaBDuePercentage());
                numberOfScheduleItemsB2Created ++;
            }

            //create schedule for budget item and tabella C
            KeyTable keyTableC = keyTables.findByPropertyAndNameAndStartDate(property, "Tabella C", startDate);
            if (getTabellaCPercentage() != null) {
                scheduleItems.newScheduleItem(schedule, keyTableC, budgetItem, getTabellaCPercentage());
                numberOfScheduleItemsCCreated ++;
            }

            //create schedule for budget item and tabella C1
            KeyTable keyTableC1 = keyTables.findByPropertyAndNameAndStartDate(property, "Tabella C1", startDate);
            if (getTabellaCUnoPercentage() != null) {
                scheduleItems.newScheduleItem(schedule, keyTableC1, budgetItem, getTabellaCUnoPercentage());
                numberOfScheduleItemsC1Created ++;
            }

            //create schedule for budget item and tabella D
            KeyTable keyTableD = keyTables.findByPropertyAndNameAndStartDate(property, "Tabella D", startDate);
            if (getTabellaDPercentage() != null) {
                scheduleItems.newScheduleItem(schedule, keyTableD, budgetItem, getTabellaDPercentage());
                numberOfScheduleItemsDCreated ++;
            }

            //create schedule for budget item and tabella E
            KeyTable keyTableE = keyTables.findByPropertyAndNameAndStartDate(property, "Tabella E", startDate);
            if (getTabellaEPercentage() != null) {
                scheduleItems.newScheduleItem(schedule, keyTableE, budgetItem, getTabellaEPercentage());
                numberOfScheduleItemsECreated ++;
            }

            //create schedule for budget item and Consumi Diretti
            KeyTable keyTableDir = keyTables.findByPropertyAndNameAndStartDate(property, "Consumi Diretti", startDate);
            if (getConsumiDirettiPercentage() != null) {
                scheduleItems.newScheduleItem(schedule, keyTableDir, budgetItem, getConsumiDirettiPercentage());
                numberOfScheduleItemsDirCreated ++;
            }

            //create schedule for budget item and Consumi Diretti Acqua
            KeyTable keyTableDirAcqua = keyTables.findByPropertyAndNameAndStartDate(property, "Consumi Diretti Acqua", startDate);
            if (getConsumiDirettiAcquaPercentage() != null) {
                scheduleItems.newScheduleItem(schedule, keyTableDirAcqua, budgetItem, getConsumiDirettiAcquaPercentage());
                numberOfScheduleItemsDirAcquaCreated ++;
            }

            System.out.println("Number of records read: " + numberOfRecords);
            System.out.println("Number of budgets created: " + numberOfBudgetsCreated);
            System.out.println("Number of charges created: " + numberOfChargesCreated);
            System.out.println("Number of budgetItems created: " + numberOfBudgetItemsCreated);
            System.out.println("Number of scheduleItems for Tabella A created: " + numberOfScheduleItemsACreated);
            System.out.println("Number of scheduleItems for Tabella B created: " + numberOfScheduleItemsBCreated);
            System.out.println("Number of scheduleItems for Tabella B1 created: " + numberOfScheduleItemsB1Created);
            System.out.println("Number of scheduleItems for Tabella B2 created: " + numberOfScheduleItemsB2Created);
            System.out.println("Number of scheduleItems for Tabella C created: " + numberOfScheduleItemsCCreated);
            System.out.println("Number of scheduleItems for Tabella C1 created: " + numberOfScheduleItemsC1Created);
            System.out.println("Number of scheduleItems for Tabella D created: " + numberOfScheduleItemsDCreated);
            System.out.println("Number of scheduleItems for Tabella E created: " + numberOfScheduleItemsECreated);
            System.out.println("Number of scheduleItems for Tabella Consumi Diretti created: " + numberOfScheduleItemsDirCreated);
            System.out.println("Number of scheduleItems for Tabella Consumi Diretti Acqua created: " + numberOfScheduleItemsDirAcquaCreated);


        } catch (Exception e) {
            // REVIEW: ignore any garbage
            System.out.println("ERROR OR GARBAGE");
        }
    }

    private static String pretty(final String str) {
        return str == null? null : StringUtils.capitalize(str.toLowerCase());
    }

    @Inject
    DomainObjectContainer container;

    @Inject
    private Charges charges;

    @Inject
    private ChargeGroups chargeGroups;

    @Inject
    private Taxes taxes;

    @Inject
    private Budgets budgets;

    @Inject
    private BudgetItems budgetItems;

    @Inject
    private Schedules schedules;

    @Inject
    private ScheduleItems scheduleItems;

    @Inject
    private PropertyRepository propertyRepository;

    @Inject
    private UnitRepository unitRepository;

    @Inject
    private KeyTables keyTables;

    @Inject
    private ApplicationTenancyRepository applicationTenancyRepository;
}
