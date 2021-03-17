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
package org.estatio.module.lease.fixtures.leaseitems.builders;

import java.math.BigInteger;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.base.dom.apptenancy.ApplicationTenancyLevel;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemStatus;
import org.estatio.module.lease.dom.LeaseItemType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import static org.incode.module.base.integtests.VT.bi;

@EqualsAndHashCode(of={"lease", "leaseItemType"}, callSuper = false)
@ToString(of={"lease", "leaseItemType"})
@Accessors(chain = true)
public class LeaseItemBuilder extends BuilderScriptAbstract<LeaseItem, LeaseItemBuilder> {

    @Getter @Setter Lease lease;
    @Getter @Setter Charge charge;
    @Getter @Setter LeaseItemType leaseItemType;
    @Getter @Setter InvoicingFrequency invoicingFrequency;
    @Getter @Setter LeaseAgreementRoleTypeEnum invoicedBy;
    @Getter @Setter PaymentMethod paymentMethod;
    @Getter @Setter LeaseItemStatus status;
    @Getter @Setter BigInteger sequence;

    @Getter LeaseItem object;

    @Override
    protected void execute(final ExecutionContext ec) {

        checkParam("lease", ec, Lease.class);
        checkParam("charge", ec, Charge.class);
        checkParam("leaseItemType", ec, LeaseItemType.class);
        checkParam("invoicingFrequency", ec, InvoicingFrequency.class);

        defaultParam("sequence", ec, bi(1));
        defaultParam("invoicedBy", ec, LeaseAgreementRoleTypeEnum.LANDLORD);
        defaultParam("paymentMethod", ec, PaymentMethod.DIRECT_DEBIT);
        defaultParam("status", ec, LeaseItemStatus.ACTIVE);

        final ApplicationTenancy leaseApplicationTenancy = lease.getApplicationTenancy();
        final ApplicationTenancy countryApplicationTenancy = leaseApplicationTenancy.getParent();
        if(!ApplicationTenancyLevel.of(countryApplicationTenancy).isCountry()) {
            // not expected to happen...
            throw new IllegalStateException(
                    String.format("Lease '%s' has an app tenancy '%s' whose parent is not at the country level",
                            lease.getReference(), leaseApplicationTenancy.getName()));
        }

        LeaseItem leaseItem = lease.findItem(leaseItemType, lease.getStartDate(), invoicedBy);

        if (leaseItem == null) {
            leaseItem = lease.newItem(leaseItemType, invoicedBy, charge, invoicingFrequency, paymentMethod, lease.getStartDate());
            leaseItem.setType(leaseItemType);
            leaseItem.setStatus(status);
            leaseItem.setEndDate(lease.getEndDate());

            leaseItem.setSequence(sequence);
            ec.addResult(this, leaseItem);
        }

        object = leaseItem;
    }


}
