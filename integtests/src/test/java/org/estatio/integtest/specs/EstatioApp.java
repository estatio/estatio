/*
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.integtest.specs;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.fixtures.InstallableFixture;
import org.apache.isis.applib.services.wrapper.WrapperFactory;
import org.apache.isis.core.integtestsupport.IsisSystemForTest;
import org.apache.isis.core.wrapper.WrapperFactoryDefault;

import org.estatio.api.Api;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannelTypes;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementRoles;
import org.estatio.dom.asset.FixedAssetRoles;
import org.estatio.dom.asset.FixedAssets;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Units;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.communicationchannel.CommunicationChannels;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.geography.Countries;
import org.estatio.dom.geography.States;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.lease.LeaseTerms;
import org.estatio.dom.lease.LeaseUnits;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.invoicing.InvoiceItemsForLease;
import org.estatio.dom.numerator.Numerators;
import org.estatio.dom.party.Organisations;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Persons;
import org.estatio.dom.tag.Tags;
import org.estatio.integtest.EstatioSystemOnThread;
import org.estatio.services.clock.ClockService;
import org.estatio.services.settings.EstatioSettingsService;


/**
 * Provides access to Estatio's domain services, for either
 * integration tests or Cucumber specifications. 
 */
public class EstatioApp {

    public WrapperFactory wrapperFactory;
    public DomainObjectContainer container;

    public Api api;

    public Countries countries;
    public States states;

    public Charges charges;

    public Numerators numerators;

    public AgreementRoles agreementRoles;
    public AgreementRoleTypes agreementRoleTypes;
    public AgreementRoleCommunicationChannelTypes agreementRoleCommunicationChannelTypes;

    public FixedAssets fixedAssets;
    public Properties properties;
    public FixedAssetRoles actors;
    public Units<?> units;

    public FinancialAccounts financialAccounts;

    public Parties parties;
    public Organisations organisations;
    public Persons persons;

    public Leases leases;
    public LeaseTerms leaseTerms;
    public LeaseUnits leaseUnits;
    
    public Invoices invoices;
    public InvoiceItemsForLease invoiceItemsForLease;

    public CommunicationChannels communicationChannels;

    public Tags tags;

    public EstatioSettingsService settings;
    public ClockService clock;
    
    private IsisSystemForTest isft;
    

    // //////////////////////////////////////

    /**
     * 
     */
    public EstatioApp() {
        init(EstatioSystemOnThread.getIsft());
    }
    
    private void init(IsisSystemForTest isft) {
        this.isft = isft;
        wrapperFactory = isft.getService(WrapperFactoryDefault.class);
        container = isft.container;

        api = isft.getService(Api.class);

        charges = isft.getService(Charges.class);

        countries = isft.getService(Countries.class);
        states = isft.getService(States.class);

        numerators = isft.getService(Numerators.class);

        agreementRoles = isft.getService(AgreementRoles.class);
        agreementRoleTypes = isft.getService(AgreementRoleTypes.class);
        agreementRoleCommunicationChannelTypes = isft.getService(AgreementRoleCommunicationChannelTypes.class);

        fixedAssets = isft.getService(FixedAssets.class);
        properties = isft.getService(Properties.class);
        actors = isft.getService(FixedAssetRoles.class);
        units = isft.getService(Units.class);

        financialAccounts = isft.getService(FinancialAccounts.class);

        parties = isft.getService(Parties.class);
        organisations = isft.getService(Organisations.class);
        persons = isft.getService(Persons.class);

        leases = isft.getService(Leases.class);
        leaseTerms = isft.getService(LeaseTerms.class);
        leaseUnits = isft.getService(LeaseUnits.class);
        invoices = isft.getService(Invoices.class);
        invoiceItemsForLease = isft.getService(InvoiceItemsForLease.class);

        communicationChannels = isft.getService(CommunicationChannels.class);
        
        tags = isft.getService(Tags.class);

        clock = isft.getService(ClockService.class);
        settings = isft.getService(EstatioSettingsService.class);
    }


    // //////////////////////////////////////
    
    /**
     * Install arbitrary fixtures, eg before an integration tests or as part of a 
     * Cucumber step definitions or hook.
     */
    public void install(InstallableFixture... fixtures) {
        isft.installFixtures(fixtures);
    }

    // //////////////////////////////////////

    /**
     * For Cucumber hooks to call, performing transaction management around each step.
     */
    public void beginTran() {
        isft.beginTran();
    }

    /**
     * For Cucumber hooks to call, performing transaction management around each step.
     */
    public void endTran(boolean ok) {
        if(ok) {
            isft.commitTran();
        } else {
            isft.abortTran();
        }
    }

    
}
