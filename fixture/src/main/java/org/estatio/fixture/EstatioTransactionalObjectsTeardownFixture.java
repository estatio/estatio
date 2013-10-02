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
package org.estatio.fixture;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.apache.isis.objectstore.jdo.applib.service.support.IsisJdoSupport;

import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;


public class EstatioTransactionalObjectsTeardownFixture extends AbstractFixture {

    @Override
    public void install() {
        
        deleteFrom("NUMERATOR");
        
        deleteFrom("INVOICEITEMFORLEASE");
        deleteFrom("INVOICEITEM");
        deleteFrom("INVOICE");
        
        deleteFrom("TAG");
        
        deleteFrom("LEASETERM");
        deleteFrom("LEASEITEM");
        deleteFrom("OCCUPANCY");
        
        deleteFrom("BANKMANDATE");
        deleteFrom("LEASE");
        
        deleteFrom("AGREEMENTROLECOMMUNICATIONCHANNEL");
        deleteFrom("AGREEMENTROLE");
        deleteFrom("AGREEMENT");
        
        deleteFrom("BANKACCOUNT");
        deleteFrom("FINANCIALACCOUNT");
        
        deleteFrom("COMMUNICATIONCHANNEL");
        
        deleteFrom("UNIT");
        deleteFrom("PROPERTY");
        deleteFrom("FIXEDASSETROLE");
        deleteFrom("FIXEDASSET");
        
        
        deleteFrom("PARTYREGISTRATION");
        deleteFrom("ORGANISATION");
        deleteFrom("PERSON");
        deleteFrom("PARTY");
    }

    private void deleteFrom(final String table) {
        isisJdoSupport.executeUpdate("DELETE FROM " + "\"" + table + "\"");
    }

    private IsisJdoSupport isisJdoSupport;
    public void injectIsisJdoSupport(IsisJdoSupport isisJdoSupport) {
        this.isisJdoSupport = isisJdoSupport;
    }
}
