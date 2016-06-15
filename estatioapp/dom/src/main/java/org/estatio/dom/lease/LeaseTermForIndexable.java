/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.dom.lease;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.JdoColumnScale;
import org.estatio.dom.index.Index;
import org.estatio.dom.index.IndexRepository;
import org.estatio.dom.lease.indexation.Indexable;
import org.estatio.dom.lease.indexation.IndexationCalculationMethod;
import org.estatio.dom.lease.indexation.IndexationMethod;
import org.estatio.dom.lease.indexation.IndexationService;
import org.estatio.dom.utils.MathUtils;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@Queries({
        @Query(
                name = "findByIndexAndDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.LeaseTermForIndexable "
                        + "WHERE index == :index "
                        + "   && (baseIndexStartDate == :date || nextIndexStartDate == :date) ")
})
public class LeaseTermForIndexable extends LeaseTerm implements Indexable {

    private IndexationMethod indexationMethod;

    @Column(allowsNull = "true")
    public IndexationMethod getIndexationMethod() {
        return indexationMethod == null ? IndexationMethod.LAST_KNOWN_INDEX : indexationMethod;
    }

    public void setIndexationMethod(final IndexationMethod indexationMethod) {
        this.indexationMethod = indexationMethod;
    }

    // //////////////////////////////////////

    @Column(name = "indexId", allowsNull = "true")
    @Getter @Setter
    private Index index;

    public List<Index> choicesIndex() {
        return indexRepository.all();
    }

    // ///////////////////////////////////////////

    @Column(allowsNull = "true")
    @Persistent
    @Getter @Setter
    private LocalDate baseIndexStartDate;

    // ///////////////////////////////////////////

    @Column(scale = JdoColumnScale.IndexValue.INDEX_VALUE, allowsNull = "true")
    @Getter @Setter
    private BigDecimal baseIndexValue;

    // ///////////////////////////////////////////

    @Persistent
    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate nextIndexStartDate;

    // ///////////////////////////////////////////

    @Column(scale = JdoColumnScale.IndexValue.INDEX_VALUE, allowsNull = "true")
    @Getter @Setter
    private BigDecimal nextIndexValue;

    // //////////////////////////////////////

    @Column(scale = JdoColumnScale.IndexValue.REBASE_FACTOR, allowsNull = "true")
    @Getter @Setter
    private BigDecimal rebaseFactor;

    // //////////////////////////////////////

    public LeaseTermForIndexable changeParameters(
            final IndexationMethod indexationMethod,
            final Index index,
            final LocalDate baseIndexDate,
            final LocalDate nextIndexDate,
            final @Parameter(optionality = Optionality.OPTIONAL) BigDecimal levellingPercentage,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate effectiveDate
    ) {
        setIndexationMethod(indexationMethod);
        setIndex(index);
        setBaseIndexStartDate(baseIndexDate);
        setNextIndexStartDate(nextIndexDate);
        setLevellingPercentage(levellingPercentage);
        setEffectiveDate(effectiveDate);
        // wipe current values
        setIndexedValue(null);
        setBaseIndexValue(null);
        setNextIndexValue(null);
        setIndexationPercentage(null);
        // align
        doAlign();
        return this;
    }

    public IndexationMethod default0ChangeParameters() {
        return getIndexationMethod();
    }

    public Index default1ChangeParameters() {
        return getIndex();
    }

    public LocalDate default2ChangeParameters() {
        return getBaseIndexStartDate();
    }

    public LocalDate default3ChangeParameters() {
        return getNextIndexStartDate();
    }

    public BigDecimal default4ChangeParameters() {
        return getLevellingPercentage();
    }

    public LocalDate default5ChangeParameters() {
        return getEffectiveDate();
    }

    public String disableChangeParameters() {
        return getStatus().isApproved() ? "Already approved" : null;
    }

    // ///////////////////////////////////////////

    @Column(allowsNull = "true")
    @Persistent
    @Getter @Setter
    private LocalDate effectiveDate;

    // ///////////////////////////////////////////

    @Column(scale = 3, allowsNull = "true")
    @Getter @Setter
    private BigDecimal indexationPercentage;

    // ///////////////////////////////////////////

    @Column(scale = 1, allowsNull = "true")
    @Getter @Setter
    private BigDecimal levellingPercentage;

    // ///////////////////////////////////////////

    @Column(scale = 2, allowsNull = "true")
    @Getter @Setter
    private BigDecimal baseValue;

    // ///////////////////////////////////////////

    @Column(scale = 2, allowsNull = "true")
    @Getter @Setter
    private BigDecimal indexedValue;

    // ///////////////////////////////////////////

    @Column(scale = 2, allowsNull = "true")
    @Getter @Setter
    private BigDecimal effectiveIndexedValue;

    // //////////////////////////////////////

    @Column(scale = 2, allowsNull = "true")
    @Getter @Setter
    private BigDecimal settledValue;

    // //////////////////////////////////////

    public LeaseTermForIndexable changeValues(
            final BigDecimal baseValue,
            final @Parameter(optionality = Optionality.OPTIONAL) BigDecimal settledValue) {
        setBaseValue(baseValue);
        setSettledValue(settledValue);
        setIndexedValue(null);
        doAlign();
        return this;
    }

    public BigDecimal default0ChangeValues() {
        return getBaseValue();
    }

    public BigDecimal default1ChangeValues() {
        return getSettledValue();
    }

    public String disableChangeValues(){
        return getStatus() == LeaseTermStatus.APPROVED ? "Already approved" : null;
    }

    // //////////////////////////////////////

    @Override
    public BigDecimal getEffectiveValue() {
        return MathUtils.firstNonZero(getSettledValue(), getEffectiveIndexedValue(), getIndexedValue(), getBaseValue());
    }

    // ///////////////////////////////////////////

    @Override
    @Programmatic
    public void doInitialize() {
        final LeaseTermForIndexable previous = (LeaseTermForIndexable) getPrevious();
        if (previous != null) {
            setIndexationMethod(previous.getIndexationMethod());
            setIndex(previous.getIndex());
            setLevellingPercentage(previous.getLevellingPercentage());
            getIndexationMethod().doInitialize(this, (Indexable) getPrevious());
        }
    }

    // //////////////////////////////////////

    @Override
    protected void doAlign() {
        if (getStatus().isUpdatable()) {
            getIndexationMethod().doAlignBeforeIndexation(this, (Indexable) getPrevious());
            if (getStatus() == LeaseTermStatus.NEW) {
                indexationService.indexate(this);
            }
            getIndexationMethod().doAlignAfterIndexation(this, (Indexable) getPrevious());
        }
    }

    // //////////////////////////////////////

    @Override
    public void copyValuesTo(final LeaseTerm target) {
        LeaseTermForIndexable t = (LeaseTermForIndexable) target;
        super.copyValuesTo(t);
        t.setIndex(getIndex());
        t.setBaseIndexStartDate(getBaseIndexStartDate());
        t.setBaseIndexValue(getBaseIndexValue());
        t.setNextIndexStartDate(getNextIndexStartDate());
        t.setNextIndexValue(getNextIndexValue());
        t.setEffectiveDate(getEffectiveDate());
        t.setIndexationPercentage(getIndexationPercentage());
        t.setLevellingPercentage(getLevellingPercentage());
        t.setBaseValue(getBaseValue());
        t.setIndexedValue(getIndexedValue());
        t.setSettledValue(getSettledValue());
    }

    // //////////////////////////////////////

    @Override
    @Programmatic
    public BigDecimal valueForDate(final LocalDate dueDate) {
        // use the indexed value on or after the effective date, use the base
        // otherwise
        if (getEffectiveDate() == null || dueDate.compareTo(getEffectiveDate()) >= 0) {
            return MathUtils.firstNonZero(getSettledValue(), getEffectiveIndexedValue(), getIndexedValue(), getBaseValue());
        }
        return MathUtils.firstNonZero(getBaseValue(), getSettledValue());
    }

    // ///////////////////////////////////////////

    @Override
    @Programmatic
    public IndexationCalculationMethod getIndexationCalculation() {
        return getIndexationMethod().indexationCalculation();
    }

    // ///////////////////////////////////////////

    @Inject
    private IndexRepository indexRepository;

    @Inject
    IndexationService indexationService;
}
