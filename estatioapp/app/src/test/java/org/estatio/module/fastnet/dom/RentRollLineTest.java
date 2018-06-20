package org.estatio.module.fastnet.dom;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class RentRollLineTest {

    @Test
    public void keyToLeaseExternalReference_works_when_not_null() throws Exception {

        // given
        RentRollLine line = new RentRollLine();
        // when
        line.setKontraktNr("351234-5678-01");
        // then
        Assertions.assertThat(line.keyToLeaseExternalReference()).isEqualTo("1234-5678-01");

    }

    @Test
    public void keyToLeaseExternalReference_works_when_null() throws Exception {

        // given
        RentRollLine line = new RentRollLine();
        // when
        // then
        Assertions.assertThat(line.keyToLeaseExternalReference()).isNull();

    }

}