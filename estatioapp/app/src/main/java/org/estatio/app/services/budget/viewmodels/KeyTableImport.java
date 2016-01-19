package org.estatio.app.services.budget.viewmodels;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.isis.applib.annotation.*;
import org.estatio.dom.Importable;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.UnitRepository;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.budgeting.keyitem.KeyItems;
import org.estatio.dom.budgeting.keytable.FoundationValueType;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTableRepository;
import org.estatio.dom.budgeting.keytable.KeyValueMethod;
import org.joda.time.LocalDate;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@DomainObject(nature = Nature.VIEW_MODEL)
public class KeyTableImport implements Importable {

    private static int numberOfRecords = 0;
    private static int[] counter = new int[10];

    private String propertyReference;
    private LocalDate startDate;
    private LocalDate endDate;
    private String unitReference;
    private BigDecimal keytableASourceValue;
    private BigDecimal keytableAValue;
    private BigDecimal keytableBSourceValue;
    private BigDecimal keytableBValue;
    private BigDecimal keytableCSourceValue;
    private BigDecimal keytableCValue;
    private BigDecimal keytableDSourceValue;
    private BigDecimal keytableDValue;
    private BigDecimal keytableESourceValue;
    private BigDecimal keytableEValue;
    private BigDecimal keytableFSourceValue;
    private BigDecimal keytableFValue;
    private BigDecimal keytableGSourceValue;
    private BigDecimal keytableGValue;
    private BigDecimal keytableHSourceValue;
    private BigDecimal keytableHValue;
    private BigDecimal keytableISourceValue;
    private BigDecimal keytableIValue;
    private BigDecimal keytableJSourceValue;
    private BigDecimal keytableJValue;

    private KeyItem findOrCreatKeyItem(
            final KeyTable keyTable,
            final Unit unit,
            final BigDecimal sourcevalue,
            final BigDecimal value){

        KeyItem keyItem = keyItems.findByKeyTableAndUnit(keyTable, unit);
        if (keyItem == null) {

            keyItem = keyItems.newItem(keyTable, unit, sourcevalue, value);
        }

        return keyItem;
    }

    @Override
    @Action(invokeOn= InvokeOn.OBJECT_AND_COLLECTION)
    public List<Object> importData() {

        Property property = propertyRepository.findPropertyByReference(getPropertyReference());
        LocalDate startDate = getStartDate();
        LocalDate endDate = getEndDate();

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
                        property,
                        names[i],
                        startDate,
                        endDate,
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
                        property,
                        names[i],
                        startDate,
                        endDate,
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

    @MemberOrder(sequence = "1")
    public String getPropertyReference() {
        return propertyReference;
    }

    public void setPropertyReference(String propertyReference) {
        this.propertyReference = propertyReference;
    }

    @MemberOrder(sequence = "2")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @MemberOrder(sequence = "3")
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @MemberOrder(sequence = "4")
    public String getUnitReference() {
        return unitReference;
    }

    public void setUnitReference(String unitReference) {
        this.unitReference = unitReference;
    }

    @MemberOrder(sequence = "5")
    public BigDecimal getKeytableASourceValue() {
        return keytableASourceValue;
    }

    public void setKeytableASourceValue(BigDecimal keytableASourceValue) {
        this.keytableASourceValue = keytableASourceValue;
    }

    @MemberOrder(sequence = "6")
    public BigDecimal getKeytableAValue() {
        return keytableAValue;
    }

    public void setKeytableAValue(BigDecimal keytableAValue) {
        this.keytableAValue = keytableAValue;
    }

    @MemberOrder(sequence = "7")
    public BigDecimal getKeytableBSourceValue() {
        return keytableBSourceValue;
    }

    public void setKeytableBSourceValue(BigDecimal keytableBSourceValue) {
        this.keytableBSourceValue = keytableBSourceValue;
    }

    @MemberOrder(sequence = "8")
    public BigDecimal getKeytableBValue() {
        return keytableBValue;
    }

    public void setKeytableBValue(BigDecimal keytableBValue) {
        this.keytableBValue = keytableBValue;
    }

    @MemberOrder(sequence = "9")
    public BigDecimal getKeytableCSourceValue() {
        return keytableCSourceValue;
    }

    public void setKeytableCSourceValue(BigDecimal keytableCSourceValue) {
        this.keytableCSourceValue = keytableCSourceValue;
    }

    @MemberOrder(sequence = "10")
    public BigDecimal getKeytableCValue() {
        return keytableCValue;
    }

    public void setKeytableCValue(BigDecimal keytableCValue) {
        this.keytableCValue = keytableCValue;
    }

    @MemberOrder(sequence = "11")
    public BigDecimal getKeytableDSourceValue() {
        return keytableDSourceValue;
    }

    public void setKeytableDSourceValue(BigDecimal keytableDSourceValue) {
        this.keytableDSourceValue = keytableDSourceValue;
    }

    @MemberOrder(sequence = "12")
    public BigDecimal getKeytableDValue() {
        return keytableDValue;
    }

    public void setKeytableDValue(BigDecimal keytableDValue) {
        this.keytableDValue = keytableDValue;
    }

    @MemberOrder(sequence = "13")
    public BigDecimal getKeytableESourceValue() {
        return keytableESourceValue;
    }

    public void setKeytableESourceValue(BigDecimal keytableESourceValue) {
        this.keytableESourceValue = keytableESourceValue;
    }

    @MemberOrder(sequence = "14")
    public BigDecimal getKeytableEValue() {
        return keytableEValue;
    }

    public void setKeytableEValue(BigDecimal keytableEValue) {
        this.keytableEValue = keytableEValue;
    }

    @MemberOrder(sequence = "15")
    public BigDecimal getKeytableFSourceValue() {
        return keytableFSourceValue;
    }

    public void setKeytableFSourceValue(BigDecimal keytableFSourceValue) {
        this.keytableFSourceValue = keytableFSourceValue;
    }

    @MemberOrder(sequence = "16")
    public BigDecimal getKeytableFValue() {
        return keytableFValue;
    }

    public void setKeytableFValue(BigDecimal keytableFValue) {
        this.keytableFValue = keytableFValue;
    }

    @MemberOrder(sequence = "17")
    public BigDecimal getKeytableGSourceValue() {
        return keytableGSourceValue;
    }

    public void setKeytableGSourceValue(BigDecimal keytableGSourceValue) {
        this.keytableGSourceValue = keytableGSourceValue;
    }

    @MemberOrder(sequence = "18")
    public BigDecimal getKeytableGValue() {
        return keytableGValue;
    }

    public void setKeytableGValue(BigDecimal keytableGValue) {
        this.keytableGValue = keytableGValue;
    }

    @MemberOrder(sequence = "19")
    public BigDecimal getKeytableHSourceValue() {
        return keytableHSourceValue;
    }

    public void setKeytableHSourceValue(BigDecimal keytableHSourceValue) {
        this.keytableHSourceValue = keytableHSourceValue;
    }

    @MemberOrder(sequence = "20")
    public BigDecimal getKeytableHValue() {
        return keytableHValue;
    }

    public void setKeytableHValue(BigDecimal keytableHValue) {
        this.keytableHValue = keytableHValue;
    }

    @MemberOrder(sequence = "21")
    public BigDecimal getKeytableISourceValue() {
        return keytableISourceValue;
    }

    public void setKeytableISourceValue(BigDecimal keytableISourceValue) {
        this.keytableISourceValue = keytableISourceValue;
    }

    @MemberOrder(sequence = "22")
    public BigDecimal getKeytableIValue() {
        return keytableIValue;
    }

    public void setKeytableIValue(BigDecimal keytableIValue) {
        this.keytableIValue = keytableIValue;
    }

    @MemberOrder(sequence = "23")
    public BigDecimal getKeytableJSourceValue() {
        return keytableJSourceValue;
    }

    public void setKeytableJSourceValue(BigDecimal keytableJSourceValue) {
        this.keytableJSourceValue = keytableJSourceValue;
    }

    @MemberOrder(sequence = "24")
    public BigDecimal getKeytableJValue() {
        return keytableJValue;
    }

    public void setKeytableJValue(BigDecimal keytableJValue) {
        this.keytableJValue = keytableJValue;
    }


    private static String pretty(final String str) {
        return str == null? null : StringUtils.capitalize(str.toLowerCase());
    }

    @Inject
    private PropertyRepository propertyRepository;

    @Inject
    private UnitRepository unitRepository;

    @Inject
    private KeyTableRepository keyTableRepository;

    @Inject
    private KeyItems keyItems;

}
