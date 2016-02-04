/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.budgeting.keytable;

import lombok.Getter;
import lombok.Setter;
import org.apache.isis.applib.annotation.*;
import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.UnitRepository;
import org.estatio.dom.budgeting.Distributable;
import org.estatio.dom.budgeting.DistributionService;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import javax.inject.Inject;
import javax.jdo.annotations.*;
import java.math.BigDecimal;
import java.util.*;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE
        //       ,schema = "budget"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByBudgetAndName", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.keytable.KeyTable " +
                        "WHERE name == :name "
                        + "&& budget == :budget"),
        @Query(
                name = "findByBudget", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.keytable.KeyTable " +
                        "WHERE budget == :budget "),
        @Query(
                name = "findKeyTableByNameMatches", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.keytable.KeyTable " +
                        "WHERE name.toLowerCase().indexOf(:name) >= 0 ")
})
@Unique(name = "KeyTable_budget_name", members = { "budget", "name" })
@DomainObject(autoCompleteRepository = KeyTableRepository.class, autoCompleteAction = "autoComplete")
public class KeyTable extends EstatioDomainObject<Budget> implements WithApplicationTenancyProperty {

    public KeyTable() {
        super("name, budget");
    }

    public String title() {
        return this.getName();
    }

    public String toString() {
        return this.getName();
    }

    @Column(name = "budgetId", allowsNull = "false")
    @PropertyLayout(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private Budget budget;

    @Column(allowsNull = "false")
    @Getter @Setter
    private String name;

    public KeyTable changeName(final String name) {
        setName(name);
        return this;
    }

    public String validateChangeName(final String name) {
        if (name.equals(null)) {
            return "Name can't be empty";
        }
        if (keyTableRepository.findByBudgetAndName(getBudget(), name)!=null) {
            return "There is already a keytable with this name for this budget";
        }
        return null;
    }

    public String default0ChangeName(final String name) {
        return getName();
    }

    @Column(name = "foundationValueTypeId", allowsNull = "false")
    @Getter @Setter
    private FoundationValueType foundationValueType;

    @ActionLayout(hidden = Where.EVERYWHERE)
    public KeyTable changeFoundationValueType(
            final FoundationValueType foundationValueType) {
        setFoundationValueType(foundationValueType);
        return this;
    }

    public FoundationValueType default0ChangeFoundationValueType(final FoundationValueType foundationValueType) {
        return getFoundationValueType();
    }

    public String validateChangeFoundationValueType(final FoundationValueType foundationValueType) {
        if (foundationValueType.equals(null)) {
            return "Foundation value type can't be empty";
        }
        return null;
    }


    @Column(name = "keyValueMethodId", allowsNull = "false")
    @Getter @Setter
    private KeyValueMethod keyValueMethod;

    @ActionLayout(hidden = Where.EVERYWHERE)
    public KeyTable changeKeyValueMethod(
            final KeyValueMethod keyValueMethod) {
        setKeyValueMethod(keyValueMethod);
        return this;
    }

    public KeyValueMethod default0ChangeKeyValueMethod(final KeyValueMethod keyValueMethod) {
        return getKeyValueMethod();
    }

    public String validateChangeKeyValueMethod(final KeyValueMethod keyValueMethod) {
        if (keyValueMethod.equals(null)) {
            return "Key value method can't be empty";
        }
        return null;
    }


    @Column(allowsNull = "false")
    @Getter @Setter
    private Integer precision;

    @ActionLayout(hidden = Where.EVERYWHERE)
    public KeyTable changePrecision(
            final Integer numberOfDigits) {
        setPrecision(numberOfDigits);
        return this;
    }

    public Integer default0ChangePrecision(final Integer numberOfDigits) {
        return getPrecision();
    }

    public String validateChangePrecision(final Integer numberOfDigits) {
        if (numberOfDigits < 0 || numberOfDigits > 6) {
            return "Number Of Digits must have a value between 0 and 6";
        }
        return null;
    }

    @CollectionLayout(render = RenderType.EAGERLY)
    @Persistent(mappedBy = "keyTable", dependentElement = "true")
    @Getter @Setter
    private SortedSet<KeyItem> items = new TreeSet<KeyItem>();

    public KeyTable generateItems(
            @Parameter(optionality = Optionality.OPTIONAL)
            boolean confirmGenerate) {

        //delete old items
        for (Iterator<KeyItem> it = this.getItems().iterator(); it.hasNext(); ) {
            it.next().deleteBudgetKeyItem();
        }

        /*
        create list of input pairs: identifier - sourcevalue
        sourcevalue is determined by FoundationValueType
        */
        List<Distributable> input = new ArrayList<>();

        for (Unit unit : unitRepository.findByProperty(this.getBudget().getProperty())) {

            if (unitIntervalValidForThisKeyTable(unit)) {
                BigDecimal sourceValue;
                if (getFoundationValueType().valueOf(unit) != null) {
                    sourceValue = getFoundationValueType().valueOf(unit);
                } else {
                    sourceValue = BigDecimal.ZERO;
                }
                KeyItem newItem = new KeyItem();
                newItem.setSourceValue(sourceValue);
                newItem.setValue(BigDecimal.ZERO);
                newItem.setUnit(unit);
                newItem.setKeyTable(this);
                persistIfNotAlready(newItem);
                input.add(newItem);
            }
        }

        /*
        call distribute method
         */
        DistributionService distributionService = new DistributionService();
        distributionService.distribute(input, getKeyValueMethod().targetTotal(), getPrecision());

        return this;
    }

    public String validateGenerateItems(boolean confirmGenerate) {
        return confirmGenerate ? null : "Please confirm";
    }

    public boolean hideGenerateItems(boolean confirmGenerate) {
        if (getFoundationValueType() == FoundationValueType.MANUAL) {
            return true;
        }
        return false;
    }

    // //////////////////////////////////////

    @MemberOrder(name = "items", sequence = "4")
    public KeyTable distributeSourceValues(
            @Parameter(optionality = Optionality.OPTIONAL)
            boolean confirmDistribute) {

        DistributionService distributionService = new DistributionService();
        distributionService.distribute(new ArrayList(getItems()), getKeyValueMethod().targetTotal(), getPrecision());

        return this;
    }

    public String validateDistributeSourceValues(boolean confirmDistribute) {
        return confirmDistribute ? null : "Please confirm";
    }

    // //////////////////////////////////////

    @Override
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return getBudget().getProperty().getApplicationTenancy();
    }

    // //////////////////////////////////////
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public boolean isValid() {
        return (this.isValidForKeyValues() && this.isValidForUnits());
    }

    public boolean isValidForKeyValues() {
        return getKeyValueMethod().isValid(this);
    }

    @PropertyLayout(hidden = Where.EVERYWHERE)
    public boolean isValidForUnits() {
        for (KeyItem item : this.getItems()) {
            if (!this.unitIntervalValidForThisKeyTable(item.getUnit())) {
                return false;
            }
            if (!item.getUnit().hasOccupancyOverlappingInterval(getBudget().getInterval())) {
                return false;
            }
        }
        return true;
    }

    @Programmatic
    private boolean unitIntervalValidForThisKeyTable(final Unit unit) {
        return unit.getInterval().contains(getBudget().getInterval());
    }

    // //////////////////////////////////////

    @Action(restrictTo = RestrictTo.PROTOTYPING)
    public KeyTable deleteItems(@ParameterLayout(named = "Are you sure?") final boolean confirmDelete) {

        for (KeyItem keyItem : getItems()) {
            removeIfNotAlready(keyItem);
        }

        return this;
    }

    public String validateDeleteItems(final boolean confirmDelete) {
        return confirmDelete ? null : "Please confirm";
    }

    // //////////////////////////////////////

    @Inject
    UnitRepository unitRepository;

    @Inject
    private KeyTableRepository keyTableRepository;

}
