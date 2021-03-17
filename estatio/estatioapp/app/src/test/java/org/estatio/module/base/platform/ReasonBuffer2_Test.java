package org.estatio.module.base.platform;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import org.estatio.module.base.platform.applib.ReasonBuffer2;

public class ReasonBuffer2_Test {

    @Test
    public void with_prefix() throws Exception {

        final ReasonBuffer2 buf = ReasonBuffer2.forAll("Cannot change");

        buf.append(true, "reason #1");
        buf.append(false, "reason #2");
        buf.append(true, "reason #3");

        Assertions.assertThat(buf.getReason()).isEqualTo("Cannot change: reason #1; reason #3");
    }

    @Test
    public void with_null_reasons() throws Exception {

        final ReasonBuffer2 buf = ReasonBuffer2.forAll();

        buf.append(true, null);

        Assertions.assertThat(buf.getReason()).isNull();
    }

    @Test
    public void using_lazy_reasons() throws Exception {

        final ReasonBuffer2 buf = ReasonBuffer2.forAll("Cannot change");

        buf.append(() -> "reason #1");
        buf.append(() -> null);
        buf.append((ReasonBuffer2.LazyReason)null);
        buf.append(() -> "reason #3");

        Assertions.assertThat(buf.getReason()).isEqualTo("Cannot change: reason #1; reason #3");
    }

    @Test
    public void using_predicates() throws Exception {

        final ReasonBuffer2 buf = ReasonBuffer2.forAll("Cannot change");

        buf.append(() -> false, "reason #1");
        buf.append(false, "reason #2");
        buf.append(true, "reason #3");
        buf.append(() -> true, "reason #4");

        Assertions.assertThat(buf.getReason()).isEqualTo("Cannot change: reason #3; reason #4");
    }

    @Test
    public void with_prefix_first() throws Exception {

        final ReasonBuffer2 buf = ReasonBuffer2.forSingle("Cannot change");

        buf.append(true, "reason #1");
        buf.append(false, "reason #2");
        buf.append(true, "reason #3");

        Assertions.assertThat(buf.getReason()).isEqualTo("Cannot change reason #1");
    }

    @Test
    public void with_prefix_first_and_condition() throws Exception {

        final ReasonBuffer2 buf = ReasonBuffer2.forSingle("Cannot change");

        buf.append((() -> true), "reason #1");
        buf.append(false, "reason #2");
        buf.append(true, "reason #3");

        Assertions.assertThat(buf.getReason()).isEqualTo("Cannot change reason #1");
    }

    @Test
    public void with_prefix_no_reasons() throws Exception {
        final ReasonBuffer2 buf = ReasonBuffer2.forAll("Cannot change");

        buf.append(false, "reason #1");
        buf.append(false, "reason #2");

        Assertions.assertThat(buf.getReason()).isNull();
    }

    @Test
    public void with_prefix_single_reasons() throws Exception {
        final ReasonBuffer2 buf = ReasonBuffer2.forAll("Cannot change");

        buf.append(true, "reason #1");
        buf.append(false, "reason #2");

        Assertions.assertThat(buf.getReason()).isEqualTo("Cannot change reason #1");
    }

    @Test
    public void with_no_prefix() throws Exception {
        final ReasonBuffer2 buf = ReasonBuffer2.forAll();

        buf.append(true, "reason #1");
        buf.append(false, "reason #2");
        buf.append(true, "reason #3");

        Assertions.assertThat(buf.getReason()).isEqualTo("reason #1; reason #3");
    }

    @Test
    public void plus() throws Exception {
        final ReasonBuffer2 buf = ReasonBuffer2.forAll("prefix A");
        final ReasonBuffer2 buf2 = ReasonBuffer2.forAll("prefix B");

        buf.append(true, "reason #1");
        buf.append(false, "reason #2");
        buf.append(true, "reason #3");

        buf.append(false, "reason #4");
        buf.append(true, "reason #5");
        buf.append(true, "reason #6");

        Assertions.assertThat(buf.getReason()).isEqualTo("prefix A: reason #1; reason #3; reason #5; reason #6");
    }

}