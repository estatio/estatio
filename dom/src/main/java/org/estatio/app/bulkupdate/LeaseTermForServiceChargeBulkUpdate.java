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
package org.estatio.app.bulkupdate;

import java.math.BigDecimal;

import org.estatio.app.EstatioViewModel;
import org.estatio.dom.lease.LeaseTermForServiceCharge;
import org.estatio.dom.lease.LeaseTerms;

import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.MemberGroupLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;

@MemberGroupLayout(left={"Selected","Next"})
@Bookmarkable
public class LeaseTermForServiceChargeBulkUpdate extends EstatioViewModel {

    
    // //////////////////////////////////////
    
    /**
     * {@link org.apache.isis.applib.ViewModel} implementation.
     */
    @Override
    public String viewModelMemento() {
        return leaseTerms.identifierFor(leaseTerm);
    }

    /**
     * {@link org.apache.isis.applib.ViewModel} implementation.
     */
    @Override
    public void viewModelInit(String memento) {
        this.leaseTerm = (LeaseTermForServiceCharge) leaseTerms.lookupByIdentifier(memento);
        this.setAuditedValue(leaseTerm.getAuditedValue());
        this.nextLeaseTerm = (LeaseTermForServiceCharge) leaseTerm.getNext();
        if(nextLeaseTerm != null) {
            this.setNextBudgetedValue(nextLeaseTerm.getBudgetedValue());
        }
    }

    // //////////////////////////////////////

    private LeaseTermForServiceCharge leaseTerm;
    
    @Title(sequence="1")
    @MemberOrder(name="Selected", sequence="1")
    public LeaseTermForServiceCharge getLeaseTerm() {
        return leaseTerm;
    }

    // //////////////////////////////////////

    private BigDecimal auditedValue;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Optional
    @MemberOrder(name="Selected", sequence="2")
    public BigDecimal getAuditedValue() {
        return auditedValue;
    }

    public void setAuditedValue(final BigDecimal auditedValue) {
        this.auditedValue = auditedValue;
    }


    // //////////////////////////////////////

    private LeaseTermForServiceCharge nextLeaseTerm;
    @MemberOrder(name="Next", sequence="1")
    public LeaseTermForServiceCharge getNextLeaseTerm() {
        return nextLeaseTerm;
    }

    // //////////////////////////////////////

    
    private BigDecimal nextBudgetedValue;

    @Optional
    @MemberOrder(name="Next", sequence="2")
    public BigDecimal getNextBudgetedValue() {
        return nextBudgetedValue;
    }

    public void setNextBudgetedValue(final BigDecimal budgetedValue) {
        this.nextBudgetedValue = budgetedValue;
    }


    // //////////////////////////////////////
    
    
    private LeaseTerms leaseTerms;

    public final void injectLeaseTerms(final LeaseTerms leaseTerms) {
        this.leaseTerms = leaseTerms;
    }


}
