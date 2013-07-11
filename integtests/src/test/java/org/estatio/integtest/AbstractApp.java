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
package org.estatio.integtest;

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
import org.estatio.services.clock.ClockService;
import org.estatio.services.settings.EstatioSettingsService;


/**
 * Represents a running instance (global singleton) of the application, for use 
 * either integration tests or Cucumber step definitions.
 * 
 * <p>
 * The principal responsibility is to provide access to domain services, such that
 * Cucumber step defs or integration tests can do their work.  Obviously, the domain
 * services will vary by application and so do not appear here.  However, this
 * class does factor out a small amount of boilerplate.
 */
public abstract class AbstractApp {

    public WrapperFactory wrapperFactory;
    public DomainObjectContainer container;

    
    protected final IsisSystemForTest isft;
    
    public AbstractApp(IsisSystemForTest isft) {
        this.isft = isft;
        wrapperFactory = isft.getService(WrapperFactoryDefault.class);
        container = isft.container;
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

    
    protected <T> T wrap(T obj) {
        return wrapperFactory.wrap(obj);
    }

    protected <T> T unwrap(T obj) {
        return wrapperFactory.unwrap(obj);
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
