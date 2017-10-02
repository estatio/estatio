package org.estatio.dom.utils;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ReasonBuffer2_Test {

    @Test
    public void with_prefix() throws Exception {
        final ReasonBuffer2 buf = ReasonBuffer2.prefix("Cannot change");

        buf.appendOnCondition(true, "reason #1");
        buf.appendOnCondition(false, "reason #2");
        buf.appendOnCondition(true, "reason #3");

        Assertions.assertThat(buf.getReason()).isEqualTo("Cannot change: reason #1; reason #3");
    }

    @Test
    public void with_prefix_no_reasons() throws Exception {
        final ReasonBuffer2 buf = ReasonBuffer2.prefix("Cannot change");

        buf.appendOnCondition(false, "reason #1");
        buf.appendOnCondition(false, "reason #2");

        Assertions.assertThat(buf.getReason()).isNull();
    }

    @Test
    public void with_prefix_single_reasons() throws Exception {
        final ReasonBuffer2 buf = ReasonBuffer2.prefix("Cannot change");

        buf.appendOnCondition(true, "reason #1");
        buf.appendOnCondition(false, "reason #2");

        Assertions.assertThat(buf.getReason()).isEqualTo("Cannot change reason #1");
    }

    @Test
    public void with_no_prefix() throws Exception {
        final ReasonBuffer2 buf = ReasonBuffer2.create();

        buf.appendOnCondition(true, "reason #1");
        buf.appendOnCondition(false, "reason #2");
        buf.appendOnCondition(true, "reason #3");

        Assertions.assertThat(buf.getReason()).isEqualTo("reason #1; reason #3");
    }

}