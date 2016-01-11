/*
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
package org.estatio.app.services.indexmaint;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.base.Objects;

import org.joda.time.LocalDate;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Bulk;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.MemberGroupLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.services.scratchpad.Scratchpad;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.index.Index;
import org.estatio.dom.index.IndexBase;
import org.estatio.dom.index.IndexBaseRepository;
import org.estatio.dom.index.IndexRepository;
import org.estatio.dom.index.IndexValue;
import org.estatio.dom.index.IndexValueRepository;

@DomainObjectLayout(paged = Integer.MAX_VALUE)
@MemberGroupLayout(
        columnSpans = { 4, 4, 4, 0 },
        left = { "Index" },
        middle = { "Index Base" },
        right = { "Index Value" })
public class IndexValueMaintLineItem {

    private Index index;

    @Programmatic
    public Index getIndex() {
        return index;
    }

    public void setIndex(final Index index) {
        this.index = index;
    }

    // //////////////////////////////////////

    private String atPath;

    @Title(prepend = "(", sequence = "2", append = ")")
    @javax.jdo.annotations.Column(allowsNull = "false", length = ApplicationTenancy.MAX_LENGTH_PATH)
    @MemberOrder(name = "Index", sequence = "1")
    public String getAtPath() {
        return atPath;
    }

    public void setAtPath(final String atPath) {
        this.atPath = atPath;
    }

    // //////////////////////////////////////

    private String reference;

    // @RegEx(validation = "[-/_A-Z0-9]+", caseSensitive = true)
    @Title(sequence = "1")
    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.REFERENCE)
    @MemberOrder(name = "Index", sequence = "1.5")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

    private IndexBase indexBase;

    @Programmatic
    public IndexBase getIndexBase() {
        return indexBase;
    }

    public void setIndexBase(final IndexBase indexBase) {
        this.indexBase = indexBase;
    }

    // //////////////////////////////////////

    private LocalDate baseStartDate;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @MemberOrder(name = "Index Base", sequence = "2")
    @Title(sequence = "2", prepend = ", ")
    public LocalDate getBaseStartDate() {
        return baseStartDate;
    }

    public void setBaseStartDate(final LocalDate startDate) {
        this.baseStartDate = startDate;
    }

    // //////////////////////////////////////

    private BigDecimal baseFactor;

    @javax.jdo.annotations.Column(scale = IndexBase.FACTOR_SCALE)
    @Optional
    @MemberOrder(name = "Index Base", sequence = "3")
    public BigDecimal getBaseFactor() {
        return baseFactor;
    }

    public void setBaseFactor(final BigDecimal factor) {
        this.baseFactor = factor;
    }

    // //////////////////////////////////////

    private IndexValue indexValue;

    @Programmatic
    public IndexValue getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(IndexValue indexValue) {
        this.indexValue = indexValue;
    }

    // //////////////////////////////////////

    private LocalDate valueStartDate;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Title(sequence = "3", prepend = ": ")
    @MemberOrder(name = "Index Value", sequence = "1")
    public LocalDate getValueStartDate() {
        return valueStartDate;
    }

    public void setValueStartDate(final LocalDate valueStartDate) {
        this.valueStartDate = valueStartDate;
    }

    // //////////////////////////////////////

    private BigDecimal value;

    @javax.jdo.annotations.Column(scale = IndexValue.VALUE_SCALE, allowsNull = "false")
    @MemberOrder(name = "Index Value", sequence = "2")
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(final BigDecimal value) {
        this.value = value;
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "1")
    @ActionSemantics(Of.SAFE)
    @Bulk
    public void verify() {
        if (bulkInteractionContext.isFirst()) {
            String error = check();
            if (error != null) {
                getContainer().raiseError(error);
            } else {
                getContainer().informUser("All ok");
            }
        }
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "2")
    @ActionSemantics(Of.IDEMPOTENT)
    @Bulk
    public void apply() {

        if (bulkInteractionContext.isFirst()) {
            String error = check();
            if (error != null) {
                getContainer().raiseError(error);
                return;
            }
        }

        // only null on first pass, then populated
        ApplicationTenancy applicationTenancy = (ApplicationTenancy) scratchpad.get("applicationTenancy");
        if(applicationTenancy == null) {
            final String atPath = getAtPath();
            applicationTenancy = applicationTenancies.findTenancyByPath(atPath);
            scratchpad.put("applicationTenancy", applicationTenancy);
        }

        // only null on first pass, then populated
        Index index = (Index) scratchpad.get("index");
        if (index == null) {
            final String reference = getReference();
            index = indexRepository.newIndex(reference, reference, applicationTenancy);
            scratchpad.put("index", index);
            setIndex(index);
        }

        // only null on first pass, then populated, and only if !existingIndex
        IndexBase previousBase = (IndexBase) scratchpad.get("previousBase");

        final LocalDate baseStartDate = getBaseStartDate();
        final BigDecimal baseFactor = getBaseFactor();

        IndexBase indexBase = indexBaseRepository.findByIndexAndDate(index, baseStartDate);
        if (indexBase == null) {
            indexBase = indexBaseRepository.newIndexBase(index, previousBase, baseStartDate, baseFactor);
        }
        setIndexBase(indexBase);
        scratchpad.put("previousBase", indexBase); // for next time need to create

        final LocalDate valueStartDate = getValueStartDate();
        final BigDecimal value = getValue();

        IndexValue indexValue = indexValueRepository.findByIndexAndStartDate(index, valueStartDate);
        if (indexValue == null) {
            indexValue = indexValueRepository.findOrCreate(index, valueStartDate, value);
        } else {
            indexValue.setValue(value);
        }
        setIndexValue(indexValue);

        // belt-n-braces so that subsequent queries succeed...
        getContainer().flush();
    }

    private String check() {

        @SuppressWarnings("rawtypes")
        List lineItemObjs = bulkInteractionContext.getDomainObjects();
        @SuppressWarnings("unchecked")
        List<IndexValueMaintLineItem> lineItems = lineItemObjs;

        // ensure items to process
        if (lineItems.isEmpty()) {
            return "No rows in spreadsheet";
        }

        // ensure all rows for a single index
        String reference = null;
        for (int i = 0; i < lineItems.size(); i++) {
            IndexValueMaintLineItem lineItem = lineItems.get(i);
            String eachReference = lineItem.getReference();
            if (reference == null) {
                reference = eachReference;
            } else {
                if (!Objects.equal(reference, eachReference)) {
                    return "Row " + (i + 1) + ": all rows must be for same index reference";
                }
            }
        }

        // ensure valueStartDates are sequential
        LocalDate previousValueStartDate = null;
        LocalDate eachValueStartDate;
        for (int i = 0; i < lineItems.size(); i++) {
            IndexValueMaintLineItem lineItem = lineItems.get(i);

            eachValueStartDate = lineItem.getValueStartDate();
            if (previousValueStartDate != null) {
                if (!Objects.equal(eachValueStartDate.minusMonths(1), previousValueStartDate)) {
                    return "Row " + (i + 1) + ": all rows must be for sequential; found " + previousValueStartDate.toString("yyyy/MM/dd") + " and " + eachValueStartDate.toString("yyyy/MM/dd");
                }
            }
            previousValueStartDate = eachValueStartDate;
        }

        // if existing index, ensure valueStartDate is:
        // * either for an existing month,
        // * or follows on from previous by no more than 1 month
        Index index = indexRepository.findIndex(reference);
        boolean existingIndex = index != null;
        scratchpad.put("index", index);
        if (existingIndex) {
            LocalDate firstValueStartDate = null;
            for (IndexValueMaintLineItem lineItem : lineItems) {
                firstValueStartDate = lineItem.getValueStartDate();
                scratchpad.put("firstValueStartDate", firstValueStartDate);
                break;
            }

            IndexValue existingValue = indexValueRepository.findByIndexAndStartDate(index, firstValueStartDate);
            if (existingValue == null) {
                LocalDate previousMonthValueStartDate = firstValueStartDate.minusMonths(1);
                IndexValue previousValue = indexValueRepository.findByIndexAndStartDate(index, previousMonthValueStartDate);
                if (previousValue == null) {
                    IndexValue last = indexValueRepository.findLastByIndex(index);
                    if (last != null) {
                        return "First row ("
                                + firstValueStartDate.toString("yyyy/MM/dd") + ") must be an existing month or "
                                + "for the 1 month after last ("
                                + last.getStartDate().toString("yyyy/MM/dd") + ")";
                    }
                } else {
                    scratchpad.put("previousBase", previousValue.getIndexBase());
                }
            } else {
                scratchpad.put("previousBase", existingValue.getIndexBase());
            }
        }

        // ensure that baseStartDate and baseFactors change in step
        LocalDate previousBaseStartDate = null;
        BigDecimal previousBaseFactor = null;
        for (int i = 0; i < lineItems.size(); i++) {
            IndexValueMaintLineItem lineItem = lineItems.get(i);

            LocalDate eachBaseStartDate = lineItem.getBaseStartDate();
            BigDecimal eachBaseFactor = lineItem.getBaseFactor();
            if (previousBaseStartDate != null || previousBaseFactor != null) {
                if (Objects.equal(previousBaseStartDate, eachBaseStartDate) &&
                        !Objects.equal(previousBaseFactor, eachBaseFactor)) {
                    return "Base factors can only change if base start date changes; "
                            + "baseStartDate: " + eachBaseStartDate.toString("yyyy/MM/dd")
                            + ", baseFactor: " + eachBaseFactor;
                }
            }
            previousBaseStartDate = eachBaseStartDate;
            previousBaseFactor = eachBaseFactor;
        }
        return null;
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private ApplicationTenancies applicationTenancies;

    @javax.inject.Inject
    private IndexRepository indexRepository;

    @javax.inject.Inject
    private IndexBaseRepository indexBaseRepository;

    @javax.inject.Inject
    private IndexValueRepository indexValueRepository;

    @javax.inject.Inject
    private Bulk.InteractionContext bulkInteractionContext;

    @javax.inject.Inject
    private Scratchpad scratchpad;

    @javax.inject.Inject
    private DomainObjectContainer container;

    private DomainObjectContainer getContainer() {
        return container;
    }

}
