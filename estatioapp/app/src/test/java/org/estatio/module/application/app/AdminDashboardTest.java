package org.estatio.module.application.app;

import java.math.BigDecimal;
import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import org.estatio.module.fastnet.dom.RentRollLine;
import org.estatio.module.lease.dom.Lease;

import static org.junit.Assert.*;

public class AdminDashboardTest {

    @Test
    public void percentageFromRentRoleLines_works() throws Exception {

        // given
        Lease lease = new Lease();
        lease.setReference("REF");
        lease.setExternalReference("XREF");
        AdminDashboard adminDashboard = new AdminDashboard();
        // when
        RentRollLine l1 = new RentRollLine();
        final BigDecimal omsattProc = new BigDecimal("1.2");
        l1.setOmsattProc(omsattProc);
        BigDecimal percentage = adminDashboard.percentageFromRentRoleLines(lease, Arrays.asList(l1));
        // then
        Assertions.assertThat(percentage).isEqualTo(omsattProc);

        // and when
        RentRollLine l2 = new RentRollLine();
        final BigDecimal omsattProc2 = new BigDecimal("1.3");
        l2.setOmsattProc(omsattProc2);
        percentage = adminDashboard.percentageFromRentRoleLines(lease, Arrays.asList(l1, l2));
        // then
        Assertions.assertThat(percentage).isEqualTo(omsattProc2);



    }





}