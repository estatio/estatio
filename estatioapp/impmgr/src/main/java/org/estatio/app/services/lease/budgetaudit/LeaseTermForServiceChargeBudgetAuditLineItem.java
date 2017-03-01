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
package org.estatio.app.services.lease.budgetaudit;

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Property;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.dom.lease.LeaseTermForServiceCharge;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.app.services.lease.budgetaudit.LeaseTermForServiceChargeBudgetAuditLineItem"
)
@DomainObjectLayout(
        paged = Integer.MAX_VALUE
)
public class LeaseTermForServiceChargeBudgetAuditLineItem {

    //region > constructors, title

    public LeaseTermForServiceChargeBudgetAuditLineItem() {}

    public LeaseTermForServiceChargeBudgetAuditLineItem(LeaseTermForServiceCharge leaseTerm) {
        this.leaseTerm = leaseTerm;
        this.auditedValue = leaseTerm.getAuditedValue();
        this.budgetedValue = leaseTerm.getBudgetedValue();
        this.nextLeaseTerm = (LeaseTermForServiceCharge) leaseTerm.getNext();
        if (this.nextLeaseTerm != null) {
            this.nextBudgetedValue = nextLeaseTerm.getBudgetedValue();
        }
    }

    public String title() {
        return TitleBuilder.start()
                .withName(getLeaseTerm())
                .toString();
    }
    //endregion


    @Getter @Setter
    private LeaseTermForServiceCharge leaseTerm;


    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true") // is this ignored?
    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private BigDecimal budgetedValue;


    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true") // is this ignored?
    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private BigDecimal auditedValue;


    @Getter @Setter
    private LeaseTermForServiceCharge nextLeaseTerm;


    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private BigDecimal nextBudgetedValue;


}
