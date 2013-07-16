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
import org.apache.isis.core.runtime.fixtures.FixturesInstallerDelegate;
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

        final FixturesInstallerDelegate installer = new FixturesInstallerDelegate();
        for (AbstractFixture fixture : fixtures) {
            installer.addFixture(fixture);
        }
        installer.installFixtures(); 
        getContainer().flush();
    }

    void truncateTables(IsisJdoSupport isisJdoSupport) {
        isisJdoSupport.executeUpdate("DELETE FROM INVOICEITEM");
        isisJdoSupport.executeUpdate("DELETE FROM INVOICE");
        
        isisJdoSupport.executeUpdate("DELETE FROM LEASETERM");
        isisJdoSupport.executeUpdate("DELETE FROM LEASEITEM");
        isisJdoSupport.executeUpdate("DELETE FROM LEASEUNIT");
        isisJdoSupport.executeUpdate("DELETE FROM TAG");
        
        isisJdoSupport.executeUpdate("DELETE FROM AGREEMENTROLECOMMUNICATIONCHANNEL");
        isisJdoSupport.executeUpdate("DELETE FROM AGREEMENTROLE");
        isisJdoSupport.executeUpdate("DELETE FROM AGREEMENT");
        
        isisJdoSupport.executeUpdate("DELETE FROM FINANCIALACCOUNT");
        
        isisJdoSupport.executeUpdate("DELETE FROM FIXEDASSET_COMMUNICATIONCHANNELS");
        isisJdoSupport.executeUpdate("DELETE FROM FIXEDASSETROLE");
        isisJdoSupport.executeUpdate("DELETE FROM FIXEDASSET");
        
        
        isisJdoSupport.executeUpdate("DELETE FROM PARTY_COMMUNICATIONCHANNELS");
        isisJdoSupport.executeUpdate("DELETE FROM PARTYREGISTRATION");
        isisJdoSupport.executeUpdate("DELETE FROM ORGANISATION");
        isisJdoSupport.executeUpdate("DELETE FROM PERSON");
        isisJdoSupport.executeUpdate("DELETE FROM PARTY");
        
        isisJdoSupport.executeUpdate("DELETE FROM NUMERATOR");
        isisJdoSupport.executeUpdate("DELETE FROM COMMUNICATIONCHANNEL");
    }

    private AbstractFixture newFixture(Class<? extends AbstractFixture> fixtureClass) {
        return getContainer().newTransientInstance(fixtureClass);
    }

    private IsisJdoSupport isisJdoSupport;
    public void injectIsisJdoSupport(IsisJdoSupport isisJdoSupport) {
        this.isisJdoSupport = isisJdoSupport;
    }
}
