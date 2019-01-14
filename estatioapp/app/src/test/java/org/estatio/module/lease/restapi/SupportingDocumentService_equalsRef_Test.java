package org.estatio.module.lease.restapi;

import org.junit.Before;
import org.junit.Test;

import org.estatio.module.party.dom.Party;

import static org.assertj.core.api.Assertions.assertThat;

public class SupportingDocumentService_equalsRef_Test {

    Party party;

    @Before
    public void setUp() throws Exception {
        party = new Party() {};
        party.setReference("REF-1");
    }

    @Test
    public void when_equal() throws Exception {
        assertThat(SupportingDocumentService.equalsRef("REF-1", party)).isTrue();
    }

    @Test
    public void when_not_equal() throws Exception {
        assertThat(SupportingDocumentService.equalsRef("REF-2", party)).isFalse();
    }
    @Test
    public void when_null_party() throws Exception {
        assertThat(SupportingDocumentService.equalsRef("REF-1", null)).isFalse();
    }

    @Test
    public void when_null_ref() throws Exception {
        assertThat(SupportingDocumentService.equalsRef(null, party)).isFalse();
    }

}