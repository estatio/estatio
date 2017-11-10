package org.estatio.module.guarantee.dom;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Guarantee_Test {
    @Test
    public void changeGuaranteeType() throws Exception {
        // given
        final Guarantee guarantee = new Guarantee();
        guarantee.setGuaranteeType(GuaranteeType.BANK_GUARANTEE);

        // when
        guarantee.changeGuaranteeType(GuaranteeType.COMPANY_GUARANTEE);

        // then
        assertThat(guarantee.getGuaranteeType()).isEqualTo(GuaranteeType.COMPANY_GUARANTEE);
    }

}