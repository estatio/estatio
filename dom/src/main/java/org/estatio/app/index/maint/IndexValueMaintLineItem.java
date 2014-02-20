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
package org.estatio.app.index.maint;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.base.Objects;

import org.estatio.app.EstatioViewModel;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.index.Index;
import org.estatio.dom.index.IndexBase;
import org.estatio.dom.index.IndexBases;
import org.estatio.dom.index.IndexValue;
import org.estatio.dom.index.IndexValues;
import org.estatio.dom.index.Indices;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.Bulk;
import org.apache.isis.applib.annotation.MemberGroupLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.services.scratchpad.Scratchpad;

@MemberGroupLayout(
        columnSpans={4,4,4,0},
        left={"Index"},
        middle={"Index Base"},
        right={"Index Value"})
public class IndexValueMaintLineItem extends EstatioViewModel {

    // //////////////////////////////////////

    /**
     * {@link org.apache.isis.applib.ViewModel} implementation.
     */
    @Override
    public String viewModelMemento() {
        return maintService.mementoFor(this);
    }

    /**
     * {@link org.apache.isis.applib.ViewModel} implementation.
     */
    @Override
    public void viewModelInit(String memento) {
        maintService.initOf(memento, this);
    }


    // //////////////////////////////////////
    // index (programmatic property)
    // //////////////////////////////////////

    private Index index;
    
    @Programmatic
    public Index getIndex() {
        return index;
    }

    public void setIndex(final Index index) {
        this.index = index;
    }

    // //////////////////////////////////////
    // reference (property)
    // //////////////////////////////////////
    
    private String reference;

    //@RegEx(validation = "[-/_A-Z0-9]+", caseSensitive = true)
    @Title(sequence = "1")
    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.REFERENCE)
    @MemberOrder(name="Index", sequence="1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }


    // //////////////////////////////////////
    // indexBase (programmatic property)
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
    // baseStartDate (property)
    // //////////////////////////////////////
    
    private LocalDate baseStartDate;

    @javax.jdo.annotations.Column(allowsNull="false")
    @MemberOrder(name="Index Base", sequence="2")
    @Title(sequence = "2", prepend=", ")
    public LocalDate getBaseStartDate() {
        return baseStartDate;
    }

    public void setBaseStartDate(final LocalDate startDate) {
        this.baseStartDate = startDate;
    }

    // //////////////////////////////////////
    // baseFactor (property)
    // //////////////////////////////////////

    private BigDecimal baseFactor;

    @javax.jdo.annotations.Column(scale = IndexBase.FACTOR_SCALE)
    @Optional
    @MemberOrder(name="Index Base", sequence="3")
    public BigDecimal getBaseFactor() {
        return baseFactor;
    }

    public void setBaseFactor(final BigDecimal factor) {
        this.baseFactor = factor;
    }

    
    // //////////////////////////////////////
    // indexValue (programmatic property)
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
    // valueStartDate (property)
    // //////////////////////////////////////
    
    private LocalDate valueStartDate;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Title(sequence = "3", prepend=": ")
    @MemberOrder(name="Index Value", sequence="1")
    public LocalDate getValueStartDate() {
        return valueStartDate;
    }

    public void setValueStartDate(final LocalDate valueStartDate) {
        this.valueStartDate = valueStartDate;
    }


    // //////////////////////////////////////
    // value (property)
    // //////////////////////////////////////

    private BigDecimal value;

    @javax.jdo.annotations.Column(scale = IndexValue.VALUE_SCALE, allowsNull = "false")
    @MemberOrder(name="Index Value", sequence="2")
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(final BigDecimal value) {
        this.value = value;
    }



    // //////////////////////////////////////
    // verify (action)
    // //////////////////////////////////////

    @MemberOrder(sequence="1")
    @ActionSemantics(Of.SAFE)
    @Bulk
    public void verify() {
        if(bulkInteractionContext.isFirst()) {
            String error = check();
            if(error != null) {
                getContainer().raiseError(error);
            } else {
                getContainer().informUser("All ok");
            }
        }
    }

    // //////////////////////////////////////
    // apply (action)
    // //////////////////////////////////////

    @MemberOrder(sequence="2")
    @ActionSemantics(Of.IDEMPOTENT)
    @Bulk
    public void apply() {

        if(bulkInteractionContext.isFirst()) {
            String error = check();
            if(error != null) {
                getContainer().raiseError(error);
                return;
            }
        }
        
        Index index = (Index) scratchpad.get("index"); // only null on first pass, then populated
        IndexBase previousBase = (IndexBase) scratchpad.get("previousBase"); // only null on first pass, and then only if !existingIndex
        
        final String reference = getReference();
        if(index == null) {
            index = indices.newIndex(reference, reference);
            scratchpad.put("index", index);
        }
        setIndex(index);
        
        final LocalDate baseStartDate = getBaseStartDate();
        final BigDecimal baseFactor = getBaseFactor();
        
        IndexBase indexBase = indexBases.findByIndexAndDate(index, baseStartDate);
        if(indexBase == null) {
            indexBase = indexBases.newIndexBase(index, previousBase, baseStartDate, baseFactor);
        }
        setIndexBase(indexBase);
        scratchpad.put("previousBase", indexBase); // for next time need to create
        
        final LocalDate valueStartDate = getValueStartDate();
        final BigDecimal value = getValue();
        
        IndexValue indexValue = indexValues.findIndexValueByIndexAndStartDate(index, valueStartDate);
        if(indexValue == null) {
            indexValue = indexValues.newIndexValue(index, valueStartDate, value);
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
        if(lineItems.isEmpty()) {
            return "No rows in spreadsheet";
        }
        
        // ensure all rows for a single index
        String reference = null;
        for (int i=0; i<lineItems.size(); i++) {
            IndexValueMaintLineItem lineItem = lineItems.get(i);
            String eachReference = lineItem.getReference();
            if(reference == null) {
                reference = eachReference;
            } else {
                if(!Objects.equal(reference, eachReference)) {
                    return "Row " + (i+1) + ": all rows must be for same index reference";
                }
            }
        }
        
        // ensure valueStartDates are sequential
        LocalDate previousValueStartDate = null;
        LocalDate eachValueStartDate;
        for (int i=0; i<lineItems.size(); i++) {
            IndexValueMaintLineItem lineItem = lineItems.get(i);

            eachValueStartDate = lineItem.getValueStartDate();
            if(previousValueStartDate != null) {
                if(!Objects.equal(eachValueStartDate.minusMonths(1), previousValueStartDate)) {
                    return "Row " + (i+1) + ": all rows must be for sequential; found " + previousValueStartDate.toString("yyyy/MM/dd") + " and " + eachValueStartDate.toString("yyyy/MM/dd");
                }
            }
            previousValueStartDate = eachValueStartDate;
        }

        
        // if existing index, ensure valueStartDate is:
        // * either for an existing month,
        // * or follows on from previous by no more than 1 month
        Index index = indices.findIndex(reference);
        boolean existingIndex = index != null;
        scratchpad.put("index", index);
        if(existingIndex) {
            LocalDate firstValueStartDate = null;
            for (IndexValueMaintLineItem lineItem : lineItems) {
                firstValueStartDate = lineItem.getValueStartDate();
                scratchpad.put("firstValueStartDate", firstValueStartDate);
                break;
            }
            
            IndexValue existingValue = indexValues.findIndexValueByIndexAndStartDate(index, firstValueStartDate);
            if(existingValue == null) {
                LocalDate previousMonthValueStartDate = firstValueStartDate.minusMonths(1);
                IndexValue previousValue = indexValues.findIndexValueByIndexAndStartDate(index, previousMonthValueStartDate);
                if(previousValue == null) {
                    IndexValue last = indexValues.findLastByIndex(index);
                    if(last != null) {
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
        for (int i=0; i<lineItems.size(); i++) {
            IndexValueMaintLineItem lineItem = lineItems.get(i);

            LocalDate eachBaseStartDate = lineItem.getBaseStartDate();
            BigDecimal eachBaseFactor = lineItem.getBaseFactor();
            if(previousBaseStartDate != null || previousBaseFactor != null) {
                if(  Objects.equal(previousBaseStartDate, eachBaseStartDate) && 
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
    // injected services
    // //////////////////////////////////////
    
    @javax.inject.Inject
    private Indices indices;
    
    @javax.inject.Inject
    private IndexBases indexBases;
    
    @javax.inject.Inject
    private IndexValues indexValues;

    @javax.inject.Inject
    private IndexValueMaintService maintService;
    
    @javax.inject.Inject
    private Bulk.InteractionContext bulkInteractionContext;
    
    @javax.inject.Inject
    private Scratchpad scratchpad;

}
