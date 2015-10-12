package org.estatio.fixture.budget.spreadsheets;

import org.apache.commons.lang3.StringUtils;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.ViewModel;
import org.estatio.dom.asset.*;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.budgeting.keyitem.KeyItems;
import org.estatio.dom.budgeting.keytable.FoundationValueType;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTables;
import org.estatio.dom.budgeting.keytable.KeyValueMethod;
import org.estatio.dom.geography.Countries;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;
import org.joda.time.LocalDate;

import javax.inject.Inject;
import java.math.BigDecimal;

@ViewModel
public class KeyTableImport implements Importable {

    private static int numberOfRecords = 0;
    private static int numberOfUnitRecordsCreated = 0;
    private static int numberOfARecordsCreated = 0;
    private static int numberOfBRecordsCreated = 0;
    private static int numberOfB1RecordsCreated = 0;
    private static int numberOfB2RecordsCreated = 0;
    private static int numberOfCRecordsCreated = 0;
    private static int numberOfC1RecordsCreated = 0;
    private static int numberOfDRecordsCreated = 0;
    private static int numberOfERecordsCreated = 0;
    private static int numberOfDirRecordsCreated = 0;
    private static int numberOfDirAcquaRecordsCreated = 0;

    private String propertyReference;
    private BigDecimal tabellaASourceValue;
    private BigDecimal tabellaAValue;
    private BigDecimal tabellaBSourceValue;
    private BigDecimal tabellaBValue;
    private BigDecimal tabellaBUnoSourceValue;
    private BigDecimal tabellaBUnoValue;
    private BigDecimal tabellaBDueSourceValue;
    private BigDecimal tabellaBDueValue;
    private BigDecimal tabellaCSourceValue;
    private BigDecimal tabellaCValue;
    private BigDecimal tabellaCUnoSourceValue;
    private BigDecimal tabellaCUnoValue;
    private BigDecimal tabellaDSourceValue;
    private BigDecimal tabellaDValue;
    private BigDecimal tabellaESourceValue;
    private BigDecimal tabellaEValue;
    private BigDecimal consumiDirettiSourceValue;
    private BigDecimal consumiDirettiValue;
    private BigDecimal consumiDirettiAcquaSourceValue;
    private BigDecimal consumiDirettiAcquaValue;
    private String unitReference;
    private String unitExternalReference;

    public String getUnitExternalReference() {
        return unitExternalReference;
    }

    public void setUnitExternalReference(String unitExternalReference) {
        this.unitExternalReference = unitExternalReference;
    }

    public String getPropertyReference() {
        return propertyReference;
    }

    public void setPropertyReference(String propertyReference) {
        this.propertyReference = propertyReference;
    }


    public BigDecimal getTabellaASourceValue() {
        return tabellaASourceValue;
    }

    public void setTabellaASourceValue(BigDecimal tabellaASourceValue) {
        this.tabellaASourceValue = tabellaASourceValue;
    }

    public BigDecimal getTabellaAValue() {
        return tabellaAValue;
    }

    public void setTabellaAValue(BigDecimal tabellaAValue) {
        this.tabellaAValue = tabellaAValue;
    }

    public String getUnitReference() {
        return unitReference;
    }

    public void setUnitReference(String unitReference) {
        this.unitReference = unitReference;
    }


    public BigDecimal getTabellaBSourceValue() {
        return tabellaBSourceValue;
    }

    public void setTabellaBSourceValue(BigDecimal tabellaBSourceValue) {
        this.tabellaBSourceValue = tabellaBSourceValue;
    }

    public BigDecimal getTabellaBValue() {
        return tabellaBValue;
    }

    public void setTabellaBValue(BigDecimal tabellaBValue) {
        this.tabellaBValue = tabellaBValue;
    }

    public BigDecimal getTabellaBUnoSourceValue() {
        return tabellaBUnoSourceValue;
    }

    public void setTabellaBUnoSourceValue(BigDecimal tabellaBUnoSourceValue) {
        this.tabellaBUnoSourceValue = tabellaBUnoSourceValue;
    }

    public BigDecimal getTabellaBUnoValue() {
        return tabellaBUnoValue;
    }

    public void setTabellaBUnoValue(BigDecimal tabellaBUnoValue) {
        this.tabellaBUnoValue = tabellaBUnoValue;
    }

    public BigDecimal getTabellaBDueSourceValue() {
        return tabellaBDueSourceValue;
    }

    public void setTabellaBDueSourceValue(BigDecimal tabellaBDueSourceValue) {
        this.tabellaBDueSourceValue = tabellaBDueSourceValue;
    }

    public BigDecimal getTabellaBDueValue() {
        return tabellaBDueValue;
    }

    public void setTabellaBDueValue(BigDecimal tabellaBDueValue) {
        this.tabellaBDueValue = tabellaBDueValue;
    }

    public BigDecimal getTabellaCSourceValue() {
        return tabellaCSourceValue;
    }

    public void setTabellaCSourceValue(BigDecimal tabellaCSourceValue) {
        this.tabellaCSourceValue = tabellaCSourceValue;
    }

    public BigDecimal getTabellaCValue() {
        return tabellaCValue;
    }

    public void setTabellaCValue(BigDecimal tabellaCValue) {
        this.tabellaCValue = tabellaCValue;
    }

    public BigDecimal getTabellaCUnoSourceValue() {
        return tabellaCUnoSourceValue;
    }

    public void setTabellaCUnoSourceValue(BigDecimal tabellaCUnoSourceValue) {
        this.tabellaCUnoSourceValue = tabellaCUnoSourceValue;
    }

    public BigDecimal getTabellaCUnoValue() {
        return tabellaCUnoValue;
    }

    public void setTabellaCUnoValue(BigDecimal tabellaCUnoValue) {
        this.tabellaCUnoValue = tabellaCUnoValue;
    }

    public BigDecimal getTabellaDSourceValue() {
        return tabellaDSourceValue;
    }

    public void setTabellaDSourceValue(BigDecimal tabellaDSourceValue) {
        this.tabellaDSourceValue = tabellaDSourceValue;
    }

    public BigDecimal getTabellaDValue() {
        return tabellaDValue;
    }

    public void setTabellaDValue(BigDecimal tabellaDValue) {
        this.tabellaDValue = tabellaDValue;
    }

    public BigDecimal getTabellaESourceValue() {
        return tabellaESourceValue;
    }

    public void setTabellaESourceValue(BigDecimal tabellaESourceValue) {
        this.tabellaESourceValue = tabellaESourceValue;
    }

    public BigDecimal getTabellaEValue() {
        return tabellaEValue;
    }

    public void setTabellaEValue(BigDecimal tabellaEValue) {
        this.tabellaEValue = tabellaEValue;
    }

    public BigDecimal getConsumiDirettiSourceValue() {
        return consumiDirettiSourceValue;
    }

    public void setConsumiDirettiSourceValue(BigDecimal consumiDirettiSourceValue) {
        this.consumiDirettiSourceValue = consumiDirettiSourceValue;
    }

    public BigDecimal getConsumiDirettiValue() {
        return consumiDirettiValue;
    }

    public void setConsumiDirettiValue(BigDecimal consumiDirettiValue) {
        this.consumiDirettiValue = consumiDirettiValue;
    }

    public BigDecimal getConsumiDirettiAcquaSourceValue() {
        return consumiDirettiAcquaSourceValue;
    }

    public void setConsumiDirettiAcquaSourceValue(BigDecimal consumiDirettiAcquaSourceValue) {
        this.consumiDirettiAcquaSourceValue = consumiDirettiAcquaSourceValue;
    }

    public BigDecimal getConsumiDirettiAcquaValue() {
        return consumiDirettiAcquaValue;
    }

    public void setConsumiDirettiAcquaValue(BigDecimal consumiDirettiAcquaValue) {
        this.consumiDirettiAcquaValue = consumiDirettiAcquaValue;
    }


    @Override
    public void importData() {

        Property property = propertyRepository.findPropertyByReference("CAR");
        LocalDate startDate = new LocalDate(2014,01,01);
        LocalDate endDate = new LocalDate(2014,12,31);

        KeyTable tabellaA = keyTables.findOrCreateBudgetKeyTable(
                property,
                "Tabella A",
                startDate,
                endDate,
                FoundationValueType.MANUAL,
                KeyValueMethod.PROMILLE,
                3);

        KeyTable tabellaB = keyTables.findOrCreateBudgetKeyTable(
                property,
                "Tabella B",
                startDate,
                endDate,
                FoundationValueType.MANUAL,
                KeyValueMethod.PROMILLE,
                3);

        KeyTable tabellaB1 = keyTables.findOrCreateBudgetKeyTable(
                property,
                "Tabella B1",
                startDate,
                endDate,
                FoundationValueType.MANUAL,
                KeyValueMethod.PROMILLE,
                3);

        KeyTable tabellaB2 = keyTables.findOrCreateBudgetKeyTable(
                property,
                "Tabella B2",
                startDate,
                endDate,
                FoundationValueType.MANUAL,
                KeyValueMethod.PROMILLE,
                3);

        KeyTable tabellaC = keyTables.findOrCreateBudgetKeyTable(
                property,
                "Tabella C",
                startDate,
                endDate,
                FoundationValueType.MANUAL,
                KeyValueMethod.PROMILLE,
                3);

        KeyTable tabellaC1 = keyTables.findOrCreateBudgetKeyTable(
                property,
                "Tabella C1",
                startDate,
                endDate,
                FoundationValueType.MANUAL,
                KeyValueMethod.PROMILLE,
                3);

        KeyTable tabellaD = keyTables.findOrCreateBudgetKeyTable(
                property,
                "Tabella D",
                startDate,
                endDate,
                FoundationValueType.MANUAL,
                KeyValueMethod.PROMILLE,
                3);

        KeyTable tabellaE = keyTables.findOrCreateBudgetKeyTable(
                property,
                "Tabella E",
                startDate,
                endDate,
                FoundationValueType.MANUAL,
                KeyValueMethod.PROMILLE,
                3);


        KeyTable tabellaConsumiDiretti = keyTables.findOrCreateBudgetKeyTable(
                property,
                "Consumi Diretti",
                startDate,
                endDate,
                FoundationValueType.MANUAL,
                KeyValueMethod.DEFAULT,
                3);

        KeyTable tabellaConsumiDirettiAcqua = keyTables.findOrCreateBudgetKeyTable(
                property,
                "Consumi Diretti Acqua",
                startDate,
                endDate,
                FoundationValueType.MANUAL,
                KeyValueMethod.DEFAULT,
                3);


        numberOfRecords ++;

        try {
            Unit unit = unitRepository.findUnitByReference(unitReference);
            if (unit==null) {
                unit = unitRepository.newUnit(property, unitReference, "unit " + unitReference, UnitType.BOUTIQUE);
                numberOfUnitRecordsCreated++;
            }
            if (tabellaASourceValue!=null && tabellaAValue!=null) {
                KeyItem itemA = keyItems.newItem(tabellaA, unit, tabellaASourceValue.setScale(2), tabellaAValue.setScale(3));
                numberOfARecordsCreated ++;
            }
            if (tabellaBSourceValue!=null && tabellaBValue!=null) {
                KeyItem itemB = keyItems.newItem(tabellaB, unit, tabellaBSourceValue.setScale(2), tabellaBValue.setScale(3));
                numberOfBRecordsCreated ++;
            }
            if (tabellaBUnoSourceValue!=null && tabellaBUnoValue!=null) {
                KeyItem itemB1 = keyItems.newItem(tabellaB1, unit, tabellaBUnoSourceValue.setScale(2), tabellaBUnoValue.setScale(3));
                numberOfB1RecordsCreated ++;
            }
            if (tabellaBDueSourceValue!=null && tabellaBDueValue!=null) {
                KeyItem itemB2 = keyItems.newItem(tabellaB2, unit, tabellaBDueSourceValue.setScale(2), tabellaBDueValue.setScale(3));
                numberOfB2RecordsCreated ++;
            }
            if (tabellaCSourceValue!=null && tabellaCValue!=null) {
                KeyItem itemC = keyItems.newItem(tabellaC, unit, tabellaCSourceValue.setScale(2), tabellaCValue.setScale(3));
                numberOfCRecordsCreated ++;
            }
            if (tabellaCUnoSourceValue!=null && tabellaCUnoValue!=null) {
                KeyItem itemC1 = keyItems.newItem(tabellaC1, unit, tabellaCUnoSourceValue.setScale(2), tabellaCUnoValue.setScale(3));
                numberOfC1RecordsCreated ++;
            }
            if (tabellaDSourceValue!=null && tabellaDValue!=null) {
                KeyItem itemD = keyItems.newItem(tabellaD, unit, tabellaDSourceValue.setScale(2), tabellaDValue.setScale(3));
                numberOfDRecordsCreated ++;
            }
            if (tabellaESourceValue!=null && tabellaEValue!=null) {
                KeyItem itemE = keyItems.newItem(tabellaE, unit, tabellaESourceValue.setScale(2), tabellaEValue.setScale(3));
                numberOfERecordsCreated ++;
            }
            if (consumiDirettiSourceValue!=null && consumiDirettiValue!=null) {
                KeyItem itemConsumiDiretti = keyItems.newItem(tabellaConsumiDiretti, unit, consumiDirettiSourceValue.setScale(2), consumiDirettiValue.setScale(3));
                numberOfDirRecordsCreated ++;
            }
            if (consumiDirettiAcquaSourceValue!=null && consumiDirettiAcquaValue!=null) {
                KeyItem itemConsumiDirettiAcqua = keyItems.newItem(tabellaConsumiDirettiAcqua, unit, consumiDirettiAcquaSourceValue.setScale(2), consumiDirettiAcquaValue.setScale(3));
                numberOfDirAcquaRecordsCreated ++;
            }


            System.out.println("Number Of Records Read: " + numberOfRecords + " Number of units created: " + numberOfUnitRecordsCreated);
            System.out.println("Tab A " + numberOfARecordsCreated);
            System.out.println("Tab B " + numberOfBRecordsCreated);
            System.out.println("Tab B1 " + numberOfB1RecordsCreated);
            System.out.println("Tab B2 " + numberOfB2RecordsCreated);
            System.out.println("Tab C " + numberOfCRecordsCreated);
            System.out.println("Tab C1 " + numberOfC1RecordsCreated);
            System.out.println("Tab D " + numberOfDRecordsCreated);
            System.out.println("Tab E " + numberOfERecordsCreated);
            System.out.println("Tab Diretti " + numberOfDirRecordsCreated);
            System.out.println("Tab Diretti Acqua " + numberOfDirAcquaRecordsCreated);

        } catch (Exception e) {
            // REVIEW: ignore any garbage
            System.out.println("ERROR OR GARBAGE");
            if (unitReference!=null) {
                System.out.println(unitReference);
            }
        }
    }

    private static String pretty(final String str) {
        return str == null? null : StringUtils.capitalize(str.toLowerCase());
    }

    @Inject
    DomainObjectContainer container;

    @Inject
    private PropertyRepository propertyRepository;

    @Inject
    private UnitRepository unitRepository;

    @Inject
    private KeyTables keyTables;

    @Inject
    private KeyItems keyItems;

    @Inject
    private Countries countries;

    @Inject
    private ApplicationTenancyRepository applicationTenancyRepository;
}
