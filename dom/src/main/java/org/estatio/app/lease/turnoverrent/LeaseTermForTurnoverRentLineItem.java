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
package org.estatio.app.lease.turnoverrent;

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.MemberGroupLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Paged;
import org.apache.isis.applib.annotation.Title;

import org.estatio.app.EstatioViewModel;
import org.estatio.dom.lease.LeaseTermForTurnoverRent;

@Paged(Integer.MAX_VALUE)
@MemberGroupLayout(columnSpans = { 4, 4, 4, 0 }, left = { "Selected" }, right = { "Next" })
public class LeaseTermForTurnoverRentLineItem extends EstatioViewModel {

    // //////////////////////////////////////

    /**
     * {@link org.apache.isis.applib.ViewModel} implementation.
     */
    @Override
    public String viewModelMemento() {
        return auditService.mementoFor(this);
    }

    /**
     * {@link org.apache.isis.applib.ViewModel} implementation.
     */
    @Override
    public void viewModelInit(String memento) {
        auditService.initOf(memento, this);
    }

    // //////////////////////////////////////

    private LeaseTermForTurnoverRent leaseTerm;

    @Title(sequence = "1")
    @MemberOrder(name = "Selected", sequence = "1")
    public LeaseTermForTurnoverRent getLeaseTerm() {
        return leaseTerm;
    }

    public void setLeaseTerm(LeaseTermForTurnoverRent leaseTerm) {
        this.leaseTerm = leaseTerm;
    }

    public void modifyLeaseTerm(LeaseTermForTurnoverRent leaseTerm) {
        setLeaseTerm(leaseTerm);
        setAuditedTurnover(leaseTerm.getAuditedTurnover());
    }

    // //////////////////////////////////////

    private BigDecimal auditedTurnover;

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Optional
    @MemberOrder(name = "Selected", sequence = "2")
    public BigDecimal getAuditedTurnover() {
        return auditedTurnover;
    }

    public void setAuditedTurnover(BigDecimal auditedTurnover) {
        this.auditedTurnover = auditedTurnover;
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private LeaseTermForTurnoverRentService auditService;

}
