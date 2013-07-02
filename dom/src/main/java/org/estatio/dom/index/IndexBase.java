/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.dom.index;

import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioRefDataObject;
import org.estatio.dom.WithStartDate;

@javax.jdo.annotations.PersistenceCapable
@Immutable
public class IndexBase extends EstatioRefDataObject<IndexBase> implements WithStartDate {

    public IndexBase() {
        super("index, startDate desc");
    }
    
    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name="INDEX_ID")
    private Index index;
    
    @Title(sequence = "1", append = ", ")
    @MemberOrder(sequence = "1")
    public Index getIndex() {
        return index;
    }

    public void setIndex(final Index index) {
        this.index = index;
    }

    public void modifyIndex(final Index index) {
        Index currentIndex = getIndex();
        if (index == null || index.equals(currentIndex)) {
            return;
        }
        index.addToIndexBases(this);
    }

    public void clearIndex() {
        Index currentIndex = getIndex();
        if (currentIndex == null) {
            return;
        }
        currentIndex.removeFromIndexBases(this);
    }
    
    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate startDate;

    @Title(sequence = "2")
    @MemberOrder(sequence = "2")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    public void modifyStartDate(final LocalDate startDate) {
        final LocalDate currentStartDate = getStartDate();
        if (startDate == null || startDate.equals(currentStartDate)) {
            return;
        }
        setStartDate(startDate);
    }

    public void clearStartDate() {
        LocalDate currentStartDate = getStartDate();
        if (currentStartDate == null) {
            return;
        }
        setStartDate(null);
    }


    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    @javax.jdo.annotations.Column(scale = 4)
    private BigDecimal factor;

    @Optional
    @MemberOrder(sequence = "4")
    public BigDecimal getFactor() {
        return factor;
    }

    public void setFactor(final BigDecimal factor) {
        this.factor = factor;
    }

    public String validateFactor(final BigDecimal factor) {
        return (getPreviousBase() == null) ? null : (factor == null || factor.compareTo(BigDecimal.ZERO) == 0) ? "Factor is mandatory when there is a previous base" : null;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name="PREVIOUSBASE_ID")
    @javax.jdo.annotations.Persistent(mappedBy = "nextBase")
    private IndexBase previousBase;

    /**
     * @see #getNextBase()
     */
    @Optional
    @MemberOrder(sequence = "3")
    public IndexBase getPreviousBase() {
        return previousBase;
    }

    public void setPreviousBase(final IndexBase previousBase) {
        this.previousBase = previousBase;
    }
    
    public void modifyPreviousBase(IndexBase previous) {
        setPreviousBase(previous);
        if (previous != null)
            previous.setNextBase(this);
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name="NEXTBASE_ID")
    private IndexBase nextBase;

    /**
     * @see #getPreviousBase()
     */
    @Optional
    @MemberOrder(sequence = "5")
    public IndexBase getNextBase() {
        return nextBase;
    }

    public void setNextBase(final IndexBase nextBase) {
        this.nextBase = nextBase;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "indexBase")
    private SortedSet<IndexValue> values = new TreeSet<IndexValue>();

    @MemberOrder(sequence = "6")
    public SortedSet<IndexValue> getValues() {
        return values;
    }

    public void setValues(final SortedSet<IndexValue> values) {
        this.values = values;
    }

    public void addToValues(final IndexValue value) {
        if (value == null || getValues().contains(value)) {
            return;
        }
        value.clearIndexBase();
        value.setIndexBase(this);
        getValues().add(value);
    }

    public void removeFromValues(final IndexValue value) {
        // check for no-op
        if (value == null || !getValues().contains(value)) {
            return;
        }
        value.setIndexBase(null);
        getValues().remove(value);
    }


    // //////////////////////////////////////

    @Programmatic
    public BigDecimal factorForDate(LocalDate date) {
        if (date.isBefore(getStartDate())) {
            return getFactor().multiply(getPreviousBase().factorForDate(date));
        }
        return BigDecimal.ONE;
    }

}
