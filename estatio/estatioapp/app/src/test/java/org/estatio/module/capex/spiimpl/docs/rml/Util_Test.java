package org.estatio.module.capex.spiimpl.docs.rml;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Util_Test {

    @Test
    public void capitalizeSentence() throws Exception {

        final String s = Util
                .capitalizeSentence("PROGETTO ERRE SRL");
        assertThat(s).isEqualTo("Progetto Erre Srl");
    }

    @Test
    public void handleSrlSuffix_happy() throws Exception {

        final String s = Util
                .handleSrlSuffix("Progetto Erre Srl");
        assertThat(s).isEqualTo("Progetto Erre S.r.l.");
    }

    @Test
    public void handleSrlSuffix_other() throws Exception {

        final String s = Util
                .handleSrlSuffix("Progetto Erre Xrl");
        assertThat(s).isEqualTo("Progetto Erre Xrl");
    }

    @Test
    public void stripSrlSuffix_happy() throws Exception {

        final String s = Util
                .stripSrlSuffix("Progetto Erre Srl");
        assertThat(s).isEqualTo("Progetto Erre");
    }

    @Test
    public void stripSrlSuffix_other() throws Exception {

        final String s = Util
                .stripSrlSuffix("Progetto Erre Xrl");
        assertThat(s).isEqualTo("Progetto Erre Xrl");
    }
}