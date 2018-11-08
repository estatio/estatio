package org.estatio.module.coda.dom.doc;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CodaDocLine_getEl3PropertyReference_Test {

    private CodaDocLine codaDocLine;

    @Before
    public void setUp() throws Exception {
        codaDocLine = new CodaDocLine();
    }

    @Test
    public void when_general_prefix_ITGXXX() throws Exception {

        // given
        codaDocLine.setAccountCodeEl3("ITGGEN0");

        // when
        final String ref = codaDocLine.getEl3PropertyReference();

        // then
        assertThat(ref).isNull();
    }

    @Test
    public void when_some_other_prefix_ITRXXX() throws Exception {

        // given
        codaDocLine.setAccountCodeEl3("ITRCAR0");

        // when
        final String ref = codaDocLine.getEl3PropertyReference();

        // then
        assertThat(ref).isEqualTo("CAR");

    }

    @Test
    public void when_some_other_prefix_ITJXXX() throws Exception {

        // given
        codaDocLine.setAccountCodeEl3("ITJFIO4");

        // when
        final String ref = codaDocLine.getEl3PropertyReference();

        // then
        assertThat(ref).isEqualTo("FIO");

    }

    @Test
    public void when_other_format() throws Exception {

        // given
        codaDocLine.setAccountCodeEl3("ITJFI"); // oops, only 5 chars

        // when
        final String ref = codaDocLine.getEl3PropertyReference();

        // then
        assertThat(ref).isNull();
    }


}