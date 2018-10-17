package org.estatio.module.coda.dom.doc;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.estatio.module.coda.dom.doc.ValidationStatus.INVALID;
import static org.estatio.module.coda.dom.doc.ValidationStatus.VALID;

public class ValidationStatus_deriveFrom_Test {

    @Test
    public void when_none() {
        assertThat(ValidationStatus.deriveFrom()).isEqualTo(VALID);
    }

    @Test
    public void when_one_is_invalid() {
        assertThat(ValidationStatus.deriveFrom(VALID, INVALID, VALID)).isEqualTo(INVALID);
    }

    @Test
    public void when_all_are_invalid() {
        assertThat(ValidationStatus.deriveFrom(VALID, VALID, VALID)).isEqualTo(VALID);
    }
}