package org.estatio.fixture.budget.spreadsheets;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.ViewModel;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import org.estatio.dom.Importable;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.UnitRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.budgeting.keyitem.KeyItemRepository;
import org.estatio.dom.budgeting.keytable.FoundationValueType;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTableRepository;
import org.estatio.dom.budgeting.keytable.KeyValueMethod;

import lombok.Getter;
import lombok.Setter;

@ViewModel
public class KeyTableImport implements ExcelFixtureRowHandler, Importable {

    private static int numberOfRecords = 0;
    private static int[] counter = new int[10];

    @Getter @Setter
    private String propertyReference;

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private LocalDate endDate;

    @Getter @Setter
    private String unitReference;

    @Getter @Setter
    private BigDecimal keytableASourceValue;

    @Getter @Setter
    private BigDecimal keytableAValue;

    @Getter @Setter
    private BigDecimal keytableBSourceValue;

    @Getter @Setter
    private BigDecimal keytableBValue;

    @Getter @Setter
    private BigDecimal keytableCSourceValue;

    @Getter @Setter
    private BigDecimal keytableCValue;

    @Getter @Setter
    private BigDecimal keytableDSourceValue;

    @Getter @Setter
    private BigDecimal keytableDValue;

    @Getter @Setter
    private BigDecimal keytableESourceValue;

    @Getter @Setter
    private BigDecimal keytableEValue;

    @Getter @Setter
    private BigDecimal keytableFSourceValue;

    @Getter @Setter
    private BigDecimal keytableFValue;

    @Getter @Setter
    private BigDecimal keytableGSourceValue;

    @Getter @Setter
    private BigDecimal keytableGValue;

    @Getter @Setter
    private BigDecimal keytableHSourceValue;

    @Getter @Setter
    private BigDecimal keytableHValue;

    @Getter @Setter
    private BigDecimal keytableISourceValue;

    @Getter @Setter
    private BigDecimal keytableIValue;

    @Getter @Setter
    private BigDecimal keytableJSourceValue;

    @Getter @Setter
    private BigDecimal keytableJValue;


    private KeyItem findOrCreatKeyItem(
            final KeyTable keyTable,
            final Unit unit,
            final BigDecimal sourcevalue,
            final BigDecimal value){

        KeyItem keyItem = keyItemRepository.findByKeyTableAndUnit(keyTable, unit);
        if (keyItem == null) {

            keyItem = keyItemRepository.newItem(keyTable, unit, sourcevalue, value);
        }

        return keyItem;
    }

    @Programmatic
    @Override
    public List<Object> handleRow(FixtureScript.ExecutionContext executionContext, ExcelFixture excelFixture, Object previousRow) {
        return importData(previousRow);
    }

    // REVIEW: other import view models have @Action annotation here...  but in any case, is this view model actually ever surfaced in the UI?
    public List<Object> importData() {
        return importData(null);
    }

    @Programmatic
    @Override
    public List<Object> importData(final Object previousRow) {

        Property property = propertyRepository.findPropertyByReference(getPropertyReference());
        Budget budget = budgetRepository.findOrCreateBudget(property, startDate, endDate);

        List<KeyTable> tables = new ArrayList<>();
        String[] names = {
                "KeyTable A",
                "KeyTable B",
                "KeyTable C",
                "KeyTable D",
                "KeyTable E",
                "KeyTable F",
                "KeyTable G",
                "KeyTable H",
                "KeyTable I",
                "KeyTable J"
        };

        List<BigDecimal> sourceValues = new ArrayList<>();
        sourceValues.add(getKeytableASourceValue());
        sourceValues.add(getKeytableBSourceValue());
        sourceValues.add(getKeytableCSourceValue());
        sourceValues.add(getKeytableDSourceValue());
        sourceValues.add(getKeytableESourceValue());
        sourceValues.add(getKeytableFSourceValue());
        sourceValues.add(getKeytableGSourceValue());
        sourceValues.add(getKeytableHSourceValue());
        sourceValues.add(getKeytableISourceValue());
        sourceValues.add(getKeytableJSourceValue());

        List<BigDecimal> values = new ArrayList<>();
        values.add(getKeytableAValue());
        values.add(getKeytableBValue());
        values.add(getKeytableCValue());
        values.add(getKeytableDValue());
        values.add(getKeytableEValue());
        values.add(getKeytableFValue());
        values.add(getKeytableGValue());
        values.add(getKeytableHValue());
        values.add(getKeytableIValue());
        values.add(getKeytableJValue());

        numberOfRecords ++;

        try {

            Unit unit = unitRepository.findUnitByReference(unitReference);

            // find or create key tables (key value method: Promille)
            for (int i=0; i<8; i++){

                KeyTable table = keyTableRepository.findOrCreateBudgetKeyTable(
                        budget,
                        names[i],
                        FoundationValueType.MANUAL,
                        KeyValueMethod.PROMILLE,
                        3);
                tables.add(table);

                // create key items
                if (sourceValues.get(i)!=null && values.get(i)!=null) {
                    findOrCreatKeyItem(tables.get(i), unit, sourceValues.get(i).setScale(2), values.get(i).setScale(3));
                    counter[i]++;
                }
            }

            // NB. last two keytable (key value method: DEFAULT)
            for (int i=8; i<10; i++){

                KeyTable table = keyTableRepository.findOrCreateBudgetKeyTable(
                        budget,
                        names[i],
                        FoundationValueType.MANUAL,
                        KeyValueMethod.DEFAULT,
                        3);
                tables.add(table);

                // create key items
                if (sourceValues.get(i)!=null && values.get(i)!=null) {
                    findOrCreatKeyItem(tables.get(i), unit, sourceValues.get(i).setScale(2), values.get(i).setScale(3));
                    counter[i]++;
                }
            }

            // console feedback
            System.out.println("Number Of Records Read: " + numberOfRecords);
            for (int i = 0; i<10; i++){

                System.out.println("Number of items for " + tables.get(i).getName() + " : " + counter[i]);

            }


        } catch (Exception e) {
            // REVIEW: ignore any garbage
            System.out.println("ERROR OR GARBAGE");
            if (unitReference!=null) {
                System.out.println(unitReference);
            }
        }

        return Lists.newArrayList();
    }



    @Inject
    private PropertyRepository propertyRepository;

    @Inject
    private BudgetRepository budgetRepository;

    @Inject
    private UnitRepository unitRepository;

    @Inject
    private KeyTableRepository keyTableRepository;

    @Inject
    private KeyItemRepository keyItemRepository;

}
