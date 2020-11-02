package org.estatio.module.lease.dom.amendments;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseAmendmentManager_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    LeaseAmendmentService mockLeaseAmendmentService;

    @Test
    public void newLinesForLease_works() {

        final String leaseReference = "REF";

        // given
        LeaseAmendmentManager manager = new LeaseAmendmentManager();
        manager.leaseAmendmentService = mockLeaseAmendmentService;
        LeaseItem rentItem = new LeaseItem(){
            @Override public LocalDateInterval getEffectiveInterval() {
                return LocalDateInterval.including(LeaseAmendmentTemplate.COVID_FRA_50_PERC.getDiscountStartDate(), LeaseAmendmentTemplate.COVID_FRA_50_PERC.getFrequencyChangeEndDate());
            }
        };
        rentItem.setType(LeaseItemType.RENT);
        rentItem.setInvoicingFrequency(LeaseAmendmentTemplate.COVID_FRA_50_PERC.getFrequencyChanges().get(0).oldValue);
        Lease lease = new Lease(){
            @Override public SortedSet<LeaseItem> getItems() {
                return new TreeSet<>(Arrays.asList(
                        rentItem
                ));
            }
        };
        lease.setReference(leaseReference);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLeaseAmendmentService).findInvoiceFrequencyTupleOnfirstFrequencyChangeCandidate(lease, LeaseAmendmentTemplate.COVID_FRA_50_PERC);
            will(returnValue(LeaseAmendmentTemplate.COVID_FRA_50_PERC.getFrequencyChanges().get(0)));
        }});

        // when
        final List<LeaseAmendmentImportLine> lines = manager
                .newLinesForLease(lease, LeaseAmendmentTemplate.COVID_FRA_50_PERC);

        // then
        assertThat(lines).hasSize(1);
        final LeaseAmendmentImportLine line = lines.get(0);
        assertThat(line.getLeaseReference()).isEqualTo(leaseReference);
        assertThat(line.getLeaseAmendmentTemplate()).isEqualTo(LeaseAmendmentTemplate.COVID_FRA_50_PERC);
        assertThat(line.getLeaseAmendmentState()).isEqualTo(LeaseAmendmentState.PROPOSED);
        assertThat(line.getStartDate()).isEqualTo(LeaseAmendmentTemplate.COVID_FRA_50_PERC.getAmendmentStartDate());
        assertThat(line.getDiscountPercentage()).isEqualTo(LeaseAmendmentTemplate.COVID_FRA_50_PERC.getDiscountPercentage());
        assertThat(line.getDiscountApplicableTo()).isEqualTo(LeaseAmendmentItem.applicableToToString(
                LeaseAmendmentTemplate.COVID_FRA_50_PERC.getDiscountAppliesTo()));
        assertThat(line.getDiscountStartDate()).isEqualTo(LeaseAmendmentTemplate.COVID_FRA_50_PERC.getDiscountStartDate());
        assertThat(line.getDiscountEndDate()).isEqualTo(LeaseAmendmentTemplate.COVID_FRA_50_PERC.getDiscountEndDate());

        assertThat(line.getInvoicingFrequencyOnLease()).isEqualTo(
                LeaseAmendmentTemplate.COVID_FRA_50_PERC.getFrequencyChanges().get(0).oldValue);
        assertThat(line.getAmendedInvoicingFrequency()).isEqualTo(
                LeaseAmendmentTemplate.COVID_FRA_50_PERC.getFrequencyChanges().get(0).newValue);
        assertThat(line.getFrequencyChangeApplicableTo()).isEqualTo(LeaseAmendmentItem.applicableToToString(
                LeaseAmendmentTemplate.COVID_FRA_50_PERC.getFrequencyChangeAppliesTo()));
        assertThat(line.getFrequencyChangeStartDate()).isEqualTo(LeaseAmendmentTemplate.COVID_FRA_50_PERC.getFrequencyChangeStartDate());
        assertThat(line.getFrequencyChangeEndDate()).isEqualTo(LeaseAmendmentTemplate.COVID_FRA_50_PERC.getFrequencyChangeEndDate());
    }

    @Test
    public void newLinesForLease_works_for_first_frequency_encountered_on_candidates_for_frequency_change() {

        final String leaseReference = "REF";

        // given
        LeaseAmendmentManager manager = new LeaseAmendmentManager();
        manager.leaseAmendmentService = mockLeaseAmendmentService;
        LeaseItem rentItem = new LeaseItem(){
            @Override public LocalDateInterval getEffectiveInterval() {
                return LocalDateInterval.including(LeaseAmendmentTemplate.COVID_FRA_50_PERC.getDiscountStartDate(), LeaseAmendmentTemplate.COVID_FRA_50_PERC.getFrequencyChangeEndDate());
            }
        };
        rentItem.setType(LeaseItemType.RENT);
        rentItem.setInvoicingFrequency(LeaseAmendmentTemplate.COVID_FRA_50_PERC.getFrequencyChanges().get(1).oldValue);
        Lease lease = new Lease(){
            @Override public SortedSet<LeaseItem> getItems() {
                return new TreeSet<>(Arrays.asList(
                        rentItem
                ));
            }
        };
        lease.setReference(leaseReference);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockLeaseAmendmentService).findInvoiceFrequencyTupleOnfirstFrequencyChangeCandidate(lease, LeaseAmendmentTemplate.COVID_FRA_50_PERC);
            will(returnValue(LeaseAmendmentTemplate.COVID_FRA_50_PERC.getFrequencyChanges().get(1)));
        }});

        // when
        final List<LeaseAmendmentImportLine> lines = manager
                .newLinesForLease(lease, LeaseAmendmentTemplate.COVID_FRA_50_PERC);

        // then
        assertThat(lines).hasSize(1);
        final LeaseAmendmentImportLine line = lines.get(0);

        assertThat(line.getInvoicingFrequencyOnLease()).isEqualTo(
                LeaseAmendmentTemplate.COVID_FRA_50_PERC.getFrequencyChanges().get(1).oldValue);
        assertThat(line.getAmendedInvoicingFrequency()).isEqualTo(
                LeaseAmendmentTemplate.COVID_FRA_50_PERC.getFrequencyChanges().get(1).newValue);
    }

    @Test
    public void newLinesForLease_works_when_type_not_applicable_to_anything() {

        final String leaseReference = "REF";

        // given
        LeaseAmendmentManager manager = new LeaseAmendmentManager();
        Lease lease = new Lease();
        lease.setReference(leaseReference);

        // when
        final List<LeaseAmendmentImportLine> lines = manager
                .newLinesForLease(lease, LeaseAmendmentTemplate.DEMO_TYPE);

        // then
        assertThat(lines).hasSize(1);
        final LeaseAmendmentImportLine line = lines.get(0);
        assertThat(line.getLeaseReference()).isEqualTo(leaseReference);
        assertThat(line.getLeaseAmendmentTemplate()).isEqualTo(LeaseAmendmentTemplate.DEMO_TYPE);
        assertThat(line.getLeaseAmendmentState()).isEqualTo(LeaseAmendmentState.PROPOSED);
        assertThat(line.getStartDate()).isEqualTo(LeaseAmendmentTemplate.DEMO_TYPE.getAmendmentStartDate());
    }

    @Test
    public void newLinesForLease_works_when_lease_not_applicable_to_anything() {

        final String leaseReference = "REF";

        // given
        LeaseAmendmentManager manager = new LeaseAmendmentManager();
        Lease lease = new Lease();
        lease.setReference(leaseReference);

        // when
        final List<LeaseAmendmentImportLine> lines = manager
                .newLinesForLease(lease, LeaseAmendmentTemplate.COVID_ITA_FREQ_CHANGE_ONLY);

        // then
        assertThat(lines).hasSize(1);
        final LeaseAmendmentImportLine line = lines.get(0);
        assertThat(line.getLeaseReference()).isEqualTo(leaseReference);
        assertThat(line.getLeaseAmendmentTemplate()).isEqualTo(LeaseAmendmentTemplate.COVID_ITA_FREQ_CHANGE_ONLY);
        assertThat(line.getLeaseAmendmentState()).isEqualTo(LeaseAmendmentState.PROPOSED);
        assertThat(line.getStartDate()).isEqualTo(LeaseAmendmentTemplate.COVID_ITA_FREQ_CHANGE_ONLY.getAmendmentStartDate());
    }
}