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
package org.estatio.module.lease.contributions;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.lease.dom.AgreementRoleCommunicationChannelTypeEnum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;

/**
 * TODO: inline this mixin (although it has a superclass, this is the only subclass implementation so the superclass' functionality too can be inlined)
 */
@Mixin
public class Lease_tenantInvoiceAddress extends Agreement_currentCommunicationChannel {

    public Lease_tenantInvoiceAddress(final Lease lease) {
        super(lease, LeaseAgreementRoleTypeEnum.TENANT.getTitle(), AgreementRoleCommunicationChannelTypeEnum.INVOICE_ADDRESS.getTitle());
    }


}
