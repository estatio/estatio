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
package org.estatio.integration.tests.lease.roles;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

import org.hamcrest.core.Is;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementRoles;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForIndexableRent;
import org.estatio.dom.lease.LeaseTermForServiceCharge;
import org.estatio.dom.lease.LeaseTermStatus;
import org.estatio.dom.lease.LeaseTerms;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.Leases.InvoiceRunType;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioTransactionalObjectsFixture;
import org.estatio.integration.tests.EstatioIntegrationTest;
import org.estatio.services.settings.EstatioSettingsService;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AgreementRolesTest_finders extends EstatioIntegrationTest {

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new EstatioTransactionalObjectsFixture());
    }

    private Lease leaseTopModel;
    private Leases leases;
    private Parties parties;
    private AgreementRoles agreementRoles;
    private AgreementRoleTypes agreementRoleTypes;
    private AgreementRoleType artTenant;
    private LeaseTerms leaseTerms;
    private EstatioSettingsService estatioSettingsService;

    @Before
    public void setup() {
        leases = service(Leases.class);
        parties = service(Parties.class);
        agreementRoles = service(AgreementRoles.class);
        agreementRoleTypes = service(AgreementRoleTypes.class);
        leaseTerms = service(LeaseTerms.class);
        estatioSettingsService = service(EstatioSettingsService.class);
        
        artTenant = agreementRoleTypes.findByTitle(LeaseConstants.ART_TENANT);
        
        leaseTopModel = leases.findLeaseByReference("OXF-TOPMODEL-001");
    }

    @Test
    public void findByAgreementAndPartyAndTypeAndStartDate() throws Exception {
        // given lease has tenant role
        Party party = parties.findPartyByReferenceOrName("TOPMODEL");
        AgreementRole role = agreementRoles.findByAgreementAndPartyAndTypeAndStartDate(leaseTopModel, party, artTenant, null);
        Assert.assertNotNull(role);
    }

}
