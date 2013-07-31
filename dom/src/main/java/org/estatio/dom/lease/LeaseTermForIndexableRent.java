/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.index.Index;
import org.estatio.dom.index.Indexable;
import org.estatio.dom.index.IndexationCalculator;
import org.estatio.dom.index.Indices;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
public class LeaseTermForIndexableRent extends LeaseTerm implements Indexable {

    @javax.jdo.annotations.Column(name = "INDEX_ID")
    private Index index;

    public Index getIndex() {
        return index;
    }

    public void setIndex(final Index index) {
        this.index = index;
    }

    public List<Index> choicesIndex() {
        return indices.allIndices();
    }

    // ///////////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate baseIndexStartDate;

    public LocalDate getBaseIndexStartDate() {
        return baseIndexStartDate;
    }

    public void setBaseIndexStartDate(final LocalDate baseIndexStartDate) {
        this.baseIndexStartDate = baseIndexStartDate;
    }

    // ///////////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal baseIndexValue;

    @Optional
    public BigDecimal getBaseIndexValue() {
        return baseIndexValue;
    }

    public void setBaseIndexValue(final BigDecimal baseIndexValue) {
        this.baseIndexValue = baseIndexValue;
    }

    // ///////////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate nextIndexStartDate;

    public LocalDate getNextIndexStartDate() {
        return nextIndexStartDate;
    }

    public void setNextIndexStartDate(final LocalDate nextIndexStartDate) {
        this.nextIndexStartDate = nextIndexStartDate;
    }

    // ///////////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal nextIndexValue;

    @Optional
    public BigDecimal getNextIndexValue() {
        return nextIndexValue;
    }

    public void setNextIndexValue(final BigDecimal nextIndexValue) {
        this.nextIndexValue = nextIndexValue;
    }

    // ///////////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate effectiveDate;

    @Optional
    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(final LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    // ///////////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 1)
    private BigDecimal indexationPercentage;

    @Optional
    public BigDecimal getIndexationPercentage() {
        return indexationPercentage;
    }

    public void setIndexationPercentage(final BigDecimal indexationPercentage) {
        this.indexationPercentage = indexationPercentage;
    }

    // ///////////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 1)
    private BigDecimal levellingPercentage;

    @Optional
    public BigDecimal getLevellingPercentage() {
        return levellingPercentage;
    }

    public void setLevellingPercentage(final BigDecimal levellingPercentage) {
        this.levellingPercentage = levellingPercentage;
    }

    // ///////////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal levellingValue;

    @Optional
    public BigDecimal getLevellingValue() {
        return levellingValue;
    }

    public void setLevellingValue(final BigDecimal levellingValue) {
        this.levellingValue = levellingValue;
    }

    // ///////////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal baseValue;

    @Optional
    public BigDecimal getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(final BigDecimal baseValue) {
        this.baseValue = baseValue;
    }

    // ///////////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal indexedValue;

    @Optional
    public BigDecimal getIndexedValue() {
        return indexedValue;
    }

    public void setIndexedValue(final BigDecimal indexedValue) {
        this.indexedValue = indexedValue;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal settledValue;

    @Optional
    public BigDecimal getSettledValue() {
        return settledValue;
    }

    public void setSettledValue(final BigDecimal settledValue) {
        this.settledValue = settledValue;
    }

    // //////////////////////////////////////

    @Override
    public BigDecimal getApprovedValue() {
        return getStatus().isLocked()
                ? getTrialValue()
                : null;
    }

    // //////////////////////////////////////

    @Override
    public BigDecimal getTrialValue() {
        return firstValue(getSettledValue(), getIndexedValue(), getBaseValue());
    }

    // ///////////////////////////////////////////

    @Override
    @Programmatic
    public void initialize() {
        super.initialize();
        final LeaseTermForIndexableRent previousTerm = (LeaseTermForIndexableRent) getPrevious();
        if (previousTerm != null) {
            LeaseTermFrequency frequency = previousTerm.getFrequency();
            if (frequency != null) {
                setIndex(previousTerm.getIndex());
                setBaseIndexStartDate(previousTerm.getNextIndexStartDate());
                setNextIndexStartDate(frequency.nextDate(previousTerm.getNextIndexStartDate()));
                setEffectiveDate(frequency.nextDate(previousTerm.getEffectiveDate()));
                setBaseValue(previousTerm.getSettledValue());
            }
        }
    }

    private BigDecimal firstValue(BigDecimal... values) {
        for (BigDecimal value : values) {
            if (value != null && value.compareTo(BigDecimal.ZERO) != 0) {
                return value;
            }
        }
        return BigDecimal.ZERO;
    }

    @Programmatic
    @Override
    public void update() {
        super.update();
        if (getStatus().isUnlocked()) {
            LeaseTermForIndexableRent previousTerm = (LeaseTermForIndexableRent) getPrevious();
            if (previousTerm != null) {
                BigDecimal newBaseValue = firstValue(previousTerm.getTrialValue(), previousTerm.getIndexedValue(), previousTerm.getBaseValue());
                if (getBaseValue() == null || newBaseValue.compareTo(getBaseValue()) != 0) {
                    setBaseValue(newBaseValue);
                }
            }
            IndexationCalculator calculator = new IndexationCalculator(getIndex(), getBaseIndexStartDate(), getNextIndexStartDate(), getBaseValue());
            calculator.calculate(this);
        }
    }

    @Override
    @Programmatic
    public BigDecimal valueForDueDate(LocalDate dueDate) {
        // use the indexed value on or after the effective date, use the base
        // otherwise
        if (getEffectiveDate() == null)
            return firstValue(getSettledValue(), getIndexedValue(), getBaseValue());
        if (getStartDate().compareTo(getEffectiveDate()) == 0)
            return firstValue(getSettledValue(), getIndexedValue(), getBaseValue());
        if (dueDate.compareTo(getEffectiveDate()) >= 0)
            return firstValue(getSettledValue(), getIndexedValue(), getBaseValue());
        return firstValue(getBaseValue(), getSettledValue());
    }

    // ///////////////////////////////////////////

    private Indices indices;

    public final void injectIndices(Indices indexes) {
        this.indices = indexes;
    }

}
