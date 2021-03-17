package org.estatio.module.coda.dom.doc;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidationStatus_invalidIfNull_Test {

    @Test
    public void when_valid() {
        assertThat(ValidationStatus.invalidIfNull(new Object())).isEqualTo(ValidationStatus.VALID);
    }
    @Test
    public void when_invalid() {
        assertThat(ValidationStatus.invalidIfNull(null)).isEqualTo(ValidationStatus.INVALID);
    }
}