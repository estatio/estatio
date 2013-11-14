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

import javax.jdo.annotations.InheritanceStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Mandatory;
import org.apache.isis.applib.annotation.NotPersisted;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.index.Index;
import org.estatio.dom.index.Indexable;
import org.estatio.dom.index.IndexationService;
import org.estatio.dom.index.Indices;
import org.estatio.dom.utils.MathUtils;

@javax.jdo.annotations.PersistenceCapable
// identityType=IdentityType.DATASTORE inherited from superclass
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.SUPERCLASS_TABLE)
// no @DatastoreIdentity nor @Version, since inherited from supertype
public class LeaseTermForIndexableRent extends LeaseTerm implements Indexable {

    private Index index;

    @javax.jdo.annotations.Column(name = "indexId", allowsNull = "true")
    @Mandatory
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
    @Mandatory
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

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Optional
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
    @Mandatory
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

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Optional
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

    @NotPersisted
    @Override
    public BigDecimal getRebaseFactor() {
        return rebaseFactor;
    }

    @Override
    public void setRebaseFactor(BigDecimal rebaseFactor) {
        this.rebaseFactor = rebaseFactor;

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

    private BigDecimal indexationPercentage;

    @javax.jdo.annotations.Column(scale = 1, allowsNull = "true")
    @Optional
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
    @Optional
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
    @Optional
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
    @Optional
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
        return getStatus().isApproved() ? getTrialValue() : null;
    }

    // //////////////////////////////////////

    @Override
    public BigDecimal getTrialValue() {
        return MathUtils.firstNonZero(getSettledValue(), getIndexedValue(), getBaseValue());
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
                setLevellingPercentage(previousTerm.getLevellingPercentage());
            }
        }
    }

    @Programmatic
    @Override
    public void update() {
        super.update();
        LeaseTermForIndexableRent previousTerm = (LeaseTermForIndexableRent) getPrevious();
        if (previousTerm != null) {
            BigDecimal newBaseValue = MathUtils.firstNonZero(
                    previousTerm.getIndexedValue(),
                    previousTerm.getBaseValue());
            if (getBaseValue() == null || newBaseValue.compareTo(getBaseValue()) != 0) {
                setBaseValue(newBaseValue);
            }
        }
        indexationService.indexate(this);

    }

    // //////////////////////////////////////

    @Override
    public void copyValuesTo(LeaseTerm target) {
        LeaseTermForIndexableRent t = (LeaseTermForIndexableRent) target;
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
        if (getEffectiveDate() == null) {
            return MathUtils.firstNonZero(getSettledValue(), getIndexedValue(), getBaseValue());
        }
        if (getStartDate().compareTo(getEffectiveDate()) == 0) {
            return MathUtils.firstNonZero(getSettledValue(), getIndexedValue(), getBaseValue());
        }
        if (dueDate.compareTo(getEffectiveDate()) >= 0) {
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

    public final void injectIndexationService(IndexationService indexationService) {
        this.indexationService = indexationService;
    }

}
