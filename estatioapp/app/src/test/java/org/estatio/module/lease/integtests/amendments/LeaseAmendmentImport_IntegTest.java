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
package org.estatio.module.lease.integtests.amendments;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureResult;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.value.Blob;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.amendments.LeaseAmendment;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentItem;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemForDiscount;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemForFrequencyChange;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemType;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentManager;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentRepository;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentState;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentTemplate;
import org.estatio.module.lease.dom.amendments.Property_maintainLeaseAmendments;
import org.estatio.module.lease.fixtures.imports.LeaseAmendmentImportFixture;
import org.estatio.module.lease.fixtures.imports.LeaseAmendmentImportFixture2;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForRent_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceCharge_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.estatio.module.lease.dom.InvoicingFrequency.MONTHLY_IN_ADVANCE;
import static org.estatio.module.lease.dom.InvoicingFrequency.QUARTERLY_IN_ADVANCE;

public class LeaseAmendmentImport_IntegTest extends LeaseModuleIntegTestAbstract {

    List<FixtureResult> fixtureResults;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new LeaseAmendmentImportFixture());
                executionContext.executeChild(this, new LeaseAmendmentImportFixture2());
                executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForRent_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, Lease_enum.OxfMediaX002Gb.builder());
                executionContext.executeChild(this, LeaseItemForRent_enum.OxfMediaX002Gb.builder());
                executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfMediaX002Gb.builder());
                executionContext.executeChild(this, Lease_enum.OxfMiracl005Gb.builder());
                executionContext.executeChild(this, LeaseItemForRent_enum.OxfMiracl005Gb.builder());
                executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfMiracl005Gb.builder());
                executionContext.executeChild(this, Lease_enum.OxfPoison003Gb.builder());
                executionContext.executeChild(this, LeaseItemForRent_enum.OxfPoison003Gb.builder());
                executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfPoison003Gb.builder());
                fixtureResults = executionContext.getResults();
            }
        });
    }

    @Test
    public void import_leaseamendments_test() throws Exception {

        // given
        Blob excelSheet = (Blob) fixtureResults.get(0).getObject();
        Assertions.assertThat(excelSheet).isNotNull();
        Blob excelSheet2 = (Blob) fixtureResults.get(1).getObject();
        Assertions.assertThat(excelSheet2).isNotNull();
        Property oxf = Property_enum.OxfGb.findUsing(serviceRegistry);
        Assertions.assertThat(leaseAmendmentRepository.findByTemplate(LeaseAmendmentTemplate.DEMO_TYPE2)).isEmpty();


        // when
        final LeaseAmendmentManager manager = mixin(Property_maintainLeaseAmendments.class, oxf)
                .$$(LeaseAmendmentTemplate.DEMO_TYPE2,null);
        serviceRegistry2.injectServicesInto(manager);
        manager.importAmendments(excelSheet);

        // then
        Assertions.assertThat(leaseAmendmentRepository.findByTemplate(LeaseAmendmentTemplate.DEMO_TYPE2)).hasSize(4);
        final Lease topmodelLease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        final LeaseAmendment amendmentForTopmodel = leaseAmendmentRepository.findUnique(topmodelLease, LeaseAmendmentTemplate.DEMO_TYPE2);
        Assertions.assertThat(amendmentForTopmodel.getState()).isEqualTo(LeaseAmendmentState.APPLY);
        Assertions.assertThat(amendmentForTopmodel.getStartDate()).isEqualTo(new LocalDate(2020,6,1));
        Assertions.assertThat(amendmentForTopmodel.getDateSigned()).isNull();
        Assertions.assertThat(amendmentForTopmodel.getItems()).hasSize(1);
        final LeaseAmendmentItemForDiscount amendmentItemForTopmodel = (LeaseAmendmentItemForDiscount) amendmentForTopmodel.getItems().first();
        Assertions.assertThat(amendmentItemForTopmodel.getManualDiscountAmount()).isEqualTo(new BigDecimal("-3500.12"));
        Assertions.assertThat(amendmentItemForTopmodel.getStartDate()).isEqualTo(new LocalDate(2020,7,2));
        Assertions.assertThat(amendmentItemForTopmodel.getEndDate()).isEqualTo(new LocalDate(2020,8,30));

        final Lease poisonLease = Lease_enum.OxfPoison003Gb.findUsing(serviceRegistry);
        final LeaseAmendment amendmentForPoison = leaseAmendmentRepository.findUnique(poisonLease, LeaseAmendmentTemplate.DEMO_TYPE2);
        Assertions.assertThat(amendmentForPoison.getState()).isEqualTo(LeaseAmendmentState.SIGNED);
        Assertions.assertThat(amendmentForPoison.getStartDate()).isEqualTo(new LocalDate(2020,7,1));
        Assertions.assertThat(amendmentForPoison.getDateSigned()).isEqualTo(new LocalDate(2020,6, 20));
        Assertions.assertThat(amendmentForPoison.getItems()).hasSize(4);
        final LeaseAmendmentItemForDiscount firstPoisonAmendmentItem = (LeaseAmendmentItemForDiscount) amendmentForPoison.findItemsOfType(
                LeaseAmendmentItemType.DISCOUNT).stream().sorted(Comparator.comparing(LeaseAmendmentItem::getStartDate)).collect(
                Collectors.toList()).get(0);
        final LeaseAmendmentItemForDiscount secondPoisonAmendmentItem = (LeaseAmendmentItemForDiscount) amendmentForPoison.findItemsOfType(
                LeaseAmendmentItemType.DISCOUNT).stream().sorted(Comparator.comparing(LeaseAmendmentItem::getStartDate)).collect(
                Collectors.toList()).get(1);
        final LeaseAmendmentItemForDiscount thirdPoisonAmendmentItem = (LeaseAmendmentItemForDiscount) amendmentForPoison.findItemsOfType(
                LeaseAmendmentItemType.DISCOUNT).stream().sorted(Comparator.comparing(LeaseAmendmentItem::getStartDate)).collect(
                Collectors.toList()).get(2);
        final LeaseAmendmentItemForFrequencyChange lastPoisonAmendmentItem = (LeaseAmendmentItemForFrequencyChange) amendmentForPoison.getItems().last();
        Assertions.assertThat(firstPoisonAmendmentItem.getManualDiscountAmount()).isNull();
        Assertions.assertThat(firstPoisonAmendmentItem.getDiscountPercentage()).isEqualTo(new BigDecimal("100.0"));
        Assertions.assertThat(firstPoisonAmendmentItem.getStartDate()).isEqualTo(new LocalDate(2020,7,1));
        Assertions.assertThat(firstPoisonAmendmentItem.getEndDate()).isEqualTo(new LocalDate(2020,8,31));
        Assertions.assertThat(secondPoisonAmendmentItem.getManualDiscountAmount()).isNull();
        Assertions.assertThat(secondPoisonAmendmentItem.getDiscountPercentage()).isEqualTo(new BigDecimal("50.0"));
        Assertions.assertThat(secondPoisonAmendmentItem.getStartDate()).isEqualTo(new LocalDate(2020,9,1));
        Assertions.assertThat(secondPoisonAmendmentItem.getEndDate()).isEqualTo(new LocalDate(2020,9,30));
        Assertions.assertThat(thirdPoisonAmendmentItem.getManualDiscountAmount()).isNull();
        Assertions.assertThat(thirdPoisonAmendmentItem.getDiscountPercentage()).isEqualTo(new BigDecimal("25.0"));
        Assertions.assertThat(thirdPoisonAmendmentItem.getStartDate()).isEqualTo(new LocalDate(2020,10,1));
        Assertions.assertThat(thirdPoisonAmendmentItem.getEndDate()).isEqualTo(new LocalDate(2020,10,31));

        Assertions.assertThat(lastPoisonAmendmentItem.getAmendedInvoicingFrequency()).isEqualTo(MONTHLY_IN_ADVANCE);
        Assertions.assertThat(lastPoisonAmendmentItem.getInvoicingFrequencyOnLease()).isEqualTo(QUARTERLY_IN_ADVANCE);
        Assertions.assertThat(lastPoisonAmendmentItem.getStartDate()).isEqualTo(new LocalDate(2020,7,1));
        Assertions.assertThat(lastPoisonAmendmentItem.getEndDate()).isEqualTo(new LocalDate(2020,12,31));

        final Lease miracleLease = Lease_enum.OxfMiracl005Gb.findUsing(serviceRegistry);
        final LeaseAmendment amendmentForMiracle = leaseAmendmentRepository.findUnique(miracleLease, LeaseAmendmentTemplate.DEMO_TYPE2);
        Assertions.assertThat(amendmentForMiracle.getState()).isEqualTo(LeaseAmendmentState.PROPOSED);
        Assertions.assertThat(amendmentForMiracle.getStartDate()).isEqualTo(new LocalDate(2020,7,1));
        Assertions.assertThat(amendmentForMiracle.getDateSigned()).isNull();
        Assertions.assertThat(amendmentForMiracle.getItems()).hasSize(2);
        final LeaseAmendmentItemForDiscount firstMiracleAmendmentItem = (LeaseAmendmentItemForDiscount) amendmentForMiracle.getItems().first();
        Assertions.assertThat(firstMiracleAmendmentItem.getManualDiscountAmount()).isNull();
        Assertions.assertThat(firstMiracleAmendmentItem.getDiscountPercentage()).isEqualTo(new BigDecimal("55.55"));
        Assertions.assertThat(firstMiracleAmendmentItem.getStartDate()).isEqualTo(new LocalDate(2020,7,1));
        Assertions.assertThat(firstMiracleAmendmentItem.getEndDate()).isEqualTo(new LocalDate(2020,8,31));

        final Lease mediaLease = Lease_enum.OxfMediaX002Gb.findUsing(serviceRegistry);
        final LeaseAmendment amendmentForMedia = leaseAmendmentRepository.findUnique(mediaLease, LeaseAmendmentTemplate.DEMO_TYPE2);
        Assertions.assertThat(amendmentForMedia.getState()).isEqualTo(LeaseAmendmentState.PROPOSED);
        Assertions.assertThat(amendmentForMedia.getDateSigned()).isNull();
        Assertions.assertThat(amendmentForMedia.getItems()).isEmpty();

        // and when
        manager.importAmendments(excelSheet2);

        // then
        Assertions.assertThat(leaseAmendmentRepository.findByTemplate(LeaseAmendmentTemplate.DEMO_TYPE2)).hasSize(4);
        Assertions.assertThat(amendmentForTopmodel.getState()).isEqualTo(LeaseAmendmentState.APPLY);
        Assertions.assertThat(amendmentForTopmodel.getStartDate()).isEqualTo(new LocalDate(2020,6,1));
        Assertions.assertThat(amendmentForTopmodel.getDateSigned()).isNull();
        Assertions.assertThat(amendmentForTopmodel.getItems()).hasSize(1);
        final LeaseAmendmentItemForDiscount amendmentItemForTopmodel2 = (LeaseAmendmentItemForDiscount) amendmentForTopmodel.getItems().first();
        Assertions.assertThat(amendmentItemForTopmodel2.getManualDiscountAmount()).isEqualTo(new BigDecimal("-3500.12"));
        Assertions.assertThat(amendmentItemForTopmodel2.getStartDate()).isEqualTo(new LocalDate(2020,7,2));
        Assertions.assertThat(amendmentItemForTopmodel2.getEndDate()).isEqualTo(new LocalDate(2020,8,30));

        Assertions.assertThat(amendmentForPoison.getState()).isEqualTo(LeaseAmendmentState.SIGNED);
        Assertions.assertThat(amendmentForPoison.getStartDate()).isEqualTo(new LocalDate(2020,7,1));
        Assertions.assertThat(amendmentForPoison.getDateSigned()).isEqualTo(new LocalDate(2020,6, 20));
        Assertions.assertThat(amendmentForPoison.getItems()).hasSize(4);
        final LeaseAmendmentItemForDiscount firstPoisonAmendmentItem2 = (LeaseAmendmentItemForDiscount) amendmentForPoison.findItemsOfType(
                LeaseAmendmentItemType.DISCOUNT).stream().sorted(Comparator.comparing(LeaseAmendmentItem::getStartDate)).collect(
                Collectors.toList()).get(0);
        final LeaseAmendmentItemForDiscount secondPoisonAmendmentItem2 = (LeaseAmendmentItemForDiscount) amendmentForPoison.findItemsOfType(
                LeaseAmendmentItemType.DISCOUNT).stream().sorted(Comparator.comparing(LeaseAmendmentItem::getStartDate)).collect(
                Collectors.toList()).get(1);
        final LeaseAmendmentItemForDiscount thirdPoisonAmendmentItem2 = (LeaseAmendmentItemForDiscount) amendmentForPoison.findItemsOfType(
                LeaseAmendmentItemType.DISCOUNT).stream().sorted(Comparator.comparing(LeaseAmendmentItem::getStartDate)).collect(
                Collectors.toList()).get(2);
        final LeaseAmendmentItemForFrequencyChange lastPoisonAmendmentItem2 = (LeaseAmendmentItemForFrequencyChange) amendmentForPoison.getItems().last();
        Assertions.assertThat(firstPoisonAmendmentItem2.getManualDiscountAmount()).isNull();
        Assertions.assertThat(firstPoisonAmendmentItem2.getDiscountPercentage()).isEqualTo(new BigDecimal("100.0"));
        Assertions.assertThat(firstPoisonAmendmentItem2.getStartDate()).isEqualTo(new LocalDate(2020,8,1));
        Assertions.assertThat(firstPoisonAmendmentItem2.getEndDate()).isEqualTo(new LocalDate(2020,8,31));
        Assertions.assertThat(secondPoisonAmendmentItem2.getManualDiscountAmount()).isNull();
        Assertions.assertThat(secondPoisonAmendmentItem2.getDiscountPercentage()).isEqualTo(new BigDecimal("50.0"));
        Assertions.assertThat(secondPoisonAmendmentItem2.getStartDate()).isEqualTo(new LocalDate(2020,9,1));
        Assertions.assertThat(secondPoisonAmendmentItem2.getEndDate()).isEqualTo(new LocalDate(2020,9,30));
        Assertions.assertThat(thirdPoisonAmendmentItem2.getManualDiscountAmount()).isNull();
        Assertions.assertThat(thirdPoisonAmendmentItem2.getDiscountPercentage()).isEqualTo(new BigDecimal("30.0"));
        Assertions.assertThat(thirdPoisonAmendmentItem2.getStartDate()).isEqualTo(new LocalDate(2020,10,1));
        Assertions.assertThat(thirdPoisonAmendmentItem2.getEndDate()).isEqualTo(new LocalDate(2020,11,30));

        Assertions.assertThat(lastPoisonAmendmentItem2.getAmendedInvoicingFrequency()).isEqualTo(MONTHLY_IN_ADVANCE);
        Assertions.assertThat(lastPoisonAmendmentItem2.getInvoicingFrequencyOnLease()).isEqualTo(QUARTERLY_IN_ADVANCE);
        Assertions.assertThat(lastPoisonAmendmentItem2.getStartDate()).isEqualTo(new LocalDate(2020,7,1));
        Assertions.assertThat(lastPoisonAmendmentItem2.getEndDate()).isEqualTo(new LocalDate(2020,12,31));

        Assertions.assertThat(amendmentForMiracle.getState()).isEqualTo(LeaseAmendmentState.SIGNED);
        Assertions.assertThat(amendmentForMiracle.getStartDate()).isEqualTo(new LocalDate(2020,7,1));
        Assertions.assertThat(amendmentForMiracle.getDateSigned()).isEqualTo(new LocalDate(2020,7,21));
        Assertions.assertThat(amendmentForMiracle.getItems()).hasSize(2);
        final LeaseAmendmentItemForDiscount firstMiracleAmendmentItem2 = (LeaseAmendmentItemForDiscount) amendmentForMiracle.getItems().first();
        Assertions.assertThat(firstMiracleAmendmentItem2.getManualDiscountAmount()).isNull();
        Assertions.assertThat(firstMiracleAmendmentItem2.getDiscountPercentage()).isEqualTo(new BigDecimal("60.0"));
        Assertions.assertThat(firstMiracleAmendmentItem2.getStartDate()).isEqualTo(new LocalDate(2020,7,2));
        Assertions.assertThat(firstMiracleAmendmentItem2.getEndDate()).isEqualTo(new LocalDate(2020,8,30));

        Assertions.assertThat(amendmentForMedia.getState()).isEqualTo(LeaseAmendmentState.PROPOSED);
        Assertions.assertThat(amendmentForMedia.getDateSigned()).isNull();
        Assertions.assertThat(amendmentForMedia.getItems()).isEmpty();
    }

    @Inject
    LeaseAmendmentRepository leaseAmendmentRepository;

    @Inject
    ServiceRegistry2 serviceRegistry2;

}