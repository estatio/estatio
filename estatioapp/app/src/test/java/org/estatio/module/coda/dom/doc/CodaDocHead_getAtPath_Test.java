package org.estatio.module.coda.dom.doc;

import org.junit.Before;
import org.junit.Test;

import org.estatio.module.party.dom.Organisation;

import static org.assertj.core.api.Assertions.assertThat;

public class CodaDocHead_getAtPath_Test {

    CodaDocHead codaDocHead;
    @Before
    public void setUp() throws Exception {
        codaDocHead = new CodaDocHead();
    }

    @Test
    public void when_undefined() throws Exception {
        // given
        codaDocHead.setCmpCodeBuyer(null);
        codaDocHead.setCmpCode(null);

        // when, then
        assertThat(codaDocHead.getAtPath()).isEqualTo("/");
    }
    @Test
    public void when_cmpCodeBuyer_is_known() throws Exception {
        // given
        final Organisation cmpCodeBuyer = new Organisation();
        cmpCodeBuyer.setApplicationTenancyPath("/XXX");

        codaDocHead.setCmpCodeBuyer(cmpCodeBuyer);

        // then
        assertThat(codaDocHead.getAtPath()).isEqualTo("/XXX");
    }

    @Test
    public void when_cmpCodeBuyer_unknown_but_cmpCode_is_IT() throws Exception {
        // given
        codaDocHead.setCmpCodeBuyer(null);
        codaDocHead.setCmpCode("ITxxxxxxxx");

        // then
        assertThat(codaDocHead.getAtPath()).isEqualTo("/ITA");
    }

    @Test
    public void when_cmpCodeBuyer_unknown_but_cmpCode_is_FR() throws Exception {
        // given
        codaDocHead.setCmpCodeBuyer(null);
        codaDocHead.setCmpCode("FRxxxxxxxx");

        // then
        assertThat(codaDocHead.getAtPath()).isEqualTo("/FRA");
    }

    @Test
    public void when_cmpCodeBuyer_unknown_but_cmpCode_is_something_else() throws Exception {
        // given
        codaDocHead.setCmpCodeBuyer(null);
        codaDocHead.setCmpCode("ECPNV");

        // then
        assertThat(codaDocHead.getAtPath()).isEqualTo("/");
    }
}