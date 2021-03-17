package org.estatio.module.application.spiimpl.email;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class EmailServiceThrowingException_buildBackoffIntervals_Test {

    @Test
    public void happy_case() throws Exception {
        int[] ints = EmailServiceThrowingException.buildBackoffIntervals(3, 5);
        Assertions.assertThat(ints[0]).isEqualTo(5);
        Assertions.assertThat(ints[1]).isEqualTo(5);
        Assertions.assertThat(ints[2]).isEqualTo(0);
    }
}