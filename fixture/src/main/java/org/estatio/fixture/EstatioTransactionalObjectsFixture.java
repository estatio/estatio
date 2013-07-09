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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.jdo.datastore.JDOConnection;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.apache.isis.core.commons.exceptions.IsisException;
import org.apache.isis.objectstore.jdo.applib.service.support.IsisJdoSupport;

import org.estatio.fixture.asset.PropertiesAndUnitsFixture;
import org.estatio.fixture.invoice.InvoiceAndInvoiceItemFixture;
import org.estatio.fixture.lease.LeasesAndLeaseUnitsAndLeaseItemsAndLeaseTermsAndTagsFixture;
import org.estatio.fixture.party.PersonsAndOrganisationsAndBankAccountsAndCommunicationChannelsFixture;


public class EstatioTransactionalObjectsFixture extends AbstractFixture {

    @Override
    public void install() {
        
        truncateTables(isisJdoSupport);
        
        List<AbstractFixture> fixtures = Arrays.asList(
            newFixture(PersonsAndOrganisationsAndBankAccountsAndCommunicationChannelsFixture.class),
            newFixture(PropertiesAndUnitsFixture.class),
            newFixture(LeasesAndLeaseUnitsAndLeaseItemsAndLeaseTermsAndTagsFixture.class),
            newFixture(InvoiceAndInvoiceItemFixture.class)
        );

        for (AbstractFixture fixture : fixtures) {
            fixture.install(); 
            getContainer().flush();
        }

    }

    void truncateTables(IsisJdoSupport isisJdoSupport) {
        isisJdoSupport.executeUpdate("TRUNCATE TABLE INVOICEITEM");
        isisJdoSupport.executeUpdate("TRUNCATE TABLE INVOICE");
        
        isisJdoSupport.executeUpdate("TRUNCATE TABLE LEASETERM");
        isisJdoSupport.executeUpdate("TRUNCATE TABLE LEASEITEM");
        isisJdoSupport.executeUpdate("TRUNCATE TABLE LEASEUNIT");
        isisJdoSupport.executeUpdate("TRUNCATE TABLE TAG");
        
        isisJdoSupport.executeUpdate("TRUNCATE TABLE AGREEMENTROLECOMMUNICATIONCHANNEL");
        isisJdoSupport.executeUpdate("TRUNCATE TABLE AGREEMENTROLE");
        isisJdoSupport.executeUpdate("TRUNCATE TABLE AGREEMENT");
        
        isisJdoSupport.executeUpdate("TRUNCATE TABLE FINANCIALACCOUNT");
        
        isisJdoSupport.executeUpdate("TRUNCATE TABLE FIXEDASSET_COMMUNICATIONCHANNELS");
        isisJdoSupport.executeUpdate("TRUNCATE TABLE FIXEDASSETROLE");
        isisJdoSupport.executeUpdate("TRUNCATE TABLE FIXEDASSET");
        
        
        isisJdoSupport.executeUpdate("TRUNCATE TABLE PARTY_COMMUNICATIONCHANNELS");
        isisJdoSupport.executeUpdate("TRUNCATE TABLE PARTYREGISTRATION");
        isisJdoSupport.executeUpdate("TRUNCATE TABLE ORGANISATION");
        isisJdoSupport.executeUpdate("TRUNCATE TABLE PERSON");
        isisJdoSupport.executeUpdate("TRUNCATE TABLE PARTY");
        
        isisJdoSupport.executeUpdate("TRUNCATE TABLE NUMERATOR");
        isisJdoSupport.executeUpdate("TRUNCATE TABLE COMMUNICATIONCHANNEL");
    }

    private AbstractFixture newFixture(Class<? extends AbstractFixture> fixtureClass) {
        return getContainer().newTransientInstance(fixtureClass);
    }

    private IsisJdoSupport isisJdoSupport;
    public void injectIsisJdoSupport(IsisJdoSupport isisJdoSupport) {
        this.isisJdoSupport = isisJdoSupport;
    }
}
