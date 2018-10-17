package org.estatio.module.coda.dom.doc;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CodaDocLine_appendInvalidReason_Test {

    CodaDocLine line;

    @Before
    public void setUp() throws Exception {
        line = new CodaDocLine();
    }

    @Test
    public void when_null() throws Exception {
        line.appendInvalidReason("xxx");
        assertThat(line.getReasonInvalid()).isEqualTo("xxx");
    }

    @Test
    public void when_not_null() throws Exception {
        line.setReasonInvalid("xxx");
        line.appendInvalidReason("yyy");
        assertThat(line.getReasonInvalid()).isEqualTo("xxx\nyyy");
    }
}