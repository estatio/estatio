package org.estatio.dom.agreement;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

public class AgreementRoleCommunicationChannel_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    private ClockService mockClockService;

    private LocalDate now;

    @Test
    public void isCurrent_works_with_expired_lease() throws Exception {

        now = LocalDate.now();

        // given
        AgreementRoleCommunicationChannel channel = new AgreementRoleCommunicationChannel();
        channel.clockService = mockClockService;
        channel.setStartDate(now);
        AgreementRole agreementRole = new AgreementRole();
        Agreement agreement = new AgreementForTesting();
        agreement.setEndDate(now.minusDays(1));
        agreementRole.setAgreement(agreement);
        channel.setRole(agreementRole);

        // expect
        context.checking(new Expectations() {
            {
                oneOf(mockClockService).now();
                will(returnValue(now));
            }
        });

        // when, then
        Assertions.assertThat(channel.getInterval()).isEqualTo(LocalDateInterval.parseString("2017-09-26/----------"));
        Assertions.assertThat(agreementRole.getEffectiveInterval()).isEqualTo(LocalDateInterval.parseString("----------/2017-09-26"));
        Assertions.assertThat(channel.getEffectiveInterval()).isNull();
        Assertions.assertThat(channel.isCurrent()).isFalse();

    }

}