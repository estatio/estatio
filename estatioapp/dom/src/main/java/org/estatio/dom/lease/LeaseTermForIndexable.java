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

import javax.jdo.annotations.Column;
import javax.jdo.annotations.InheritanceStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.JdoColumnScale;
import org.estatio.dom.index.Index;
import org.estatio.dom.index.Indexable;
import org.estatio.dom.index.IndexationService;
import org.estatio.dom.index.Indices;
import org.estatio.dom.utils.MathUtils;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByIndexAndDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.LeaseTermForIndexable "
                        + "WHERE index == :index "
                        + "   && (baseIndexStartDate == :date || nextIndexStartDate == :date) ")
})
public class LeaseTermForIndexable extends LeaseTerm implements Indexable {

    //TODO: Eventually the indexation method must be persisted. Currently returning the default.
    public IndexationMethod getIndexationMethod() {
        return IndexationMethod.LAST_KNOWN_INDEX;
    }

    // //////////////////////////////////////

    private Index index;

    @javax.jdo.annotations.Column(name = "indexId", allowsNull = "true")
    @Override
    public Index getIndex() {
        return index;
    }

    @Override
    public void setIndex(final Index index) {
        this.index = index;
    }

    public List<Index> choicesIndex() {
        return indices.allIndices();
    }

    // ///////////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate baseIndexStartDate;

    @javax.jdo.annotations.Column(allowsNull = "true")
    @Override
    public LocalDate getBaseIndexStartDate() {
        return baseIndexStartDate;
    }

    @Override
    public void setBaseIndexStartDate(final LocalDate baseIndexStartDate) {
        this.baseIndexStartDate = baseIndexStartDate;
    }

    // ///////////////////////////////////////////

    private BigDecimal baseIndexValue;

    @javax.jdo.annotations.Column(scale = JdoColumnScale.IndexValue.INDEX_VALUE, allowsNull = "true")
    @Override
    public BigDecimal getBaseIndexValue() {
        return baseIndexValue;
    }

    @Override
    public void setBaseIndexValue(final BigDecimal baseIndexValue) {
        this.baseIndexValue = baseIndexValue;
    }

    // ///////////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate nextIndexStartDate;

    @javax.jdo.annotations.Column(allowsNull = "true")
    @Override
    public LocalDate getNextIndexStartDate() {
        return nextIndexStartDate;
    }

    @Override
    public void setNextIndexStartDate(final LocalDate nextIndexStartDate) {
        this.nextIndexStartDate = nextIndexStartDate;
    }

    // ///////////////////////////////////////////

    private BigDecimal nextIndexValue;

    @javax.jdo.annotations.Column(scale = JdoColumnScale.IndexValue.INDEX_VALUE, allowsNull = "true")
    @Override
    public BigDecimal getNextIndexValue() {
        return nextIndexValue;
    }

    @Override
    public void setNextIndexValue(final BigDecimal nextIndexValue) {
        this.nextIndexValue = nextIndexValue;
    }

    // //////////////////////////////////////

    private BigDecimal rebaseFactor;

    @javax.jdo.annotations.Column(scale = JdoColumnScale.IndexValue.REBASE_FACTOR, allowsNull = "true")
    @Override
    public BigDecimal getRebaseFactor() {
        return rebaseFactor;
    }

    @Override
    public void setRebaseFactor(final BigDecimal rebaseFactor) {
        this.rebaseFactor = rebaseFactor;

    }

    // //////////////////////////////////////

    public LeaseTermForIndexable changeParameters(
            final Index index,
            final @ParameterLayout(named = "Base index date") LocalDate baseIndexDate,
            final @ParameterLayout(named = "Next index date") LocalDate nextIndexDate,
            final @ParameterLayout(named = "Levelling percentage") @Parameter(optionality = Optionality.OPTIONAL) BigDecimal levellingPercentage,
            final @ParameterLayout(named = "Effective date") @Parameter(optionality = Optionality.OPTIONAL) LocalDate effectiveDate
    ) {
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

    public Index default0ChangeParameters() {
        return getIndex();
    }

    public LocalDate default1ChangeParameters() {
        return getBaseIndexStartDate();
    }

    public LocalDate default2ChangeParameters() {
        return getNextIndexStartDate();
    }

    public BigDecimal default3ChangeParameters() {
        return getLevellingPercentage();
    }

    public LocalDate default4ChangeParameters() {
        return getEffectiveDate();
    }

    // ///////////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate effectiveDate;

    @javax.jdo.annotations.Column(allowsNull = "true")
    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(final LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    // ///////////////////////////////////////////

    private BigDecimal indexationPercentage;

    @javax.jdo.annotations.Column(scale = 1, allowsNull = "true")
    @Override
    public BigDecimal getIndexationPercentage() {
        return indexationPercentage;
    }

    @Override
    public void setIndexationPercentage(final BigDecimal indexationPercentage) {
        this.indexationPercentage = indexationPercentage;
    }

    // ///////////////////////////////////////////

    private BigDecimal levellingPercentage;

    @javax.jdo.annotations.Column(scale = 1, allowsNull = "true")
    @Override
    public BigDecimal getLevellingPercentage() {
        return levellingPercentage;
    }

    public void setLevellingPercentage(final BigDecimal levellingPercentage) {
        this.levellingPercentage = levellingPercentage;
    }

    // ///////////////////////////////////////////

    private BigDecimal baseValue;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Override
    public BigDecimal getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(final BigDecimal baseValue) {
        this.baseValue = baseValue;
    }

    // ///////////////////////////////////////////

    private BigDecimal indexedValue;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Override
    public BigDecimal getIndexedValue() {
        return indexedValue;
    }

    @Override
    public void setIndexedValue(final BigDecimal indexedValue) {
        this.indexedValue = indexedValue;
    }

    // //////////////////////////////////////

    private BigDecimal settledValue;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    public BigDecimal getSettledValue() {
        return settledValue;
    }

    public void setSettledValue(final BigDecimal settledValue) {
        this.settledValue = settledValue;
    }

    // //////////////////////////////////////

    public LeaseTermForIndexable changeValues(
            final @ParameterLayout(named = "Base value") BigDecimal baseValue,
            final @ParameterLayout(named = "Settled value") @Parameter(optionality = Optionality.OPTIONAL) BigDecimal settledValue) {
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

    // //////////////////////////////////////

    @Override
    public BigDecimal getEffectiveValue() {
        return MathUtils.firstNonZero(getSettledValue(), getIndexedValue(), getBaseValue());
    }

    // ///////////////////////////////////////////

    @Override
    @Programmatic
    public void doInitialize() {

        final LeaseTermForIndexable previousTerm = (LeaseTermForIndexable) getPrevious();
        if (previousTerm != null) {
            //setIndexationMethod(previousTerm.getIndexationMethod());
            getIndexationMethod().doInitialze(this);
            LeaseTermFrequency frequency = previousTerm.getFrequency();
            if (frequency != null) {
                setIndex(previousTerm.getIndex());
                setNextIndexStartDate(frequency.nextDate(previousTerm.getNextIndexStartDate()));
                setEffectiveDate(frequency.nextDate(previousTerm.getEffectiveDate()));
                setBaseValue(previousTerm.getSettledValue());
                setLevellingPercentage(previousTerm.getLevellingPercentage());
            }
        }

    }

    // //////////////////////////////////////

    @Programmatic
    public boolean isIndexable() {
        return getIndexedValue() == null
                && (getBaseIndexStartDate() != null && getNextIndexStartDate() != null)
                && (getBaseIndexStartDate().compareTo(getNextIndexStartDate()) < 0);
    }

    // //////////////////////////////////////

    @Override
    protected void doAlign() {
        getIndexationMethod().doAlign(this);
        if (isIndexable()) {
            indexationService.indexate(this);
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
            return MathUtils.firstNonZero(getSettledValue(), getIndexedValue(), getBaseValue());
        }
        return MathUtils.firstNonZero(getBaseValue(), getSettledValue());
    }

    // ///////////////////////////////////////////

    private Indices indices;

    public final void injectIndices(final Indices indexes) {
        this.indices = indexes;
    }

    private IndexationService indexationService;

    public final void injectIndexationService(final IndexationService indexationService) {
        this.indexationService = indexationService;
    }

}
