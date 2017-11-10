package org.estatio.module.agreement;

import java.util.Set;

import com.google.common.collect.Sets;

import org.junit.Test;

import org.estatio.module.base.platform.applib.Module;

import static org.assertj.core.api.Assertions.assertThat;

public class EstatioAgreementModule_Test {

    @Test
    public void value_semantics_when_equal() throws Exception {
        final EstatioAgreementModule a = new EstatioAgreementModule();
        final EstatioAgreementModule b = new EstatioAgreementModule();

        assertThat(a).isNotSameAs(b);
        assertThat(a).isEqualTo(b);
    }

    @Test
    public void set_semantics() throws Exception {
        final EstatioAgreementModule a = new EstatioAgreementModule();
        final EstatioAgreementModule b = new EstatioAgreementModule();

        final Set<Module> objects = Sets.newHashSet();
        objects.add(a);
        objects.add(b);

        assertThat(objects).hasSize(1);
    }

    @Test
    public void value_semantics_when_not_equal() throws Exception {
        final EstatioAgreementModule a = new EstatioAgreementModule();
        final Object b = new Object();

        assertThat(a).isNotSameAs(b);
        assertThat(a).isNotEqualTo(b);
    }

}