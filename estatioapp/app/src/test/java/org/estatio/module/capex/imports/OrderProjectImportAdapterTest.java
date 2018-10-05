package org.estatio.module.capex.imports;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.junit.Assert.*;

public class OrderProjectImportAdapterTest {

    @Test
    public void deriverOrderNumber() {

        // given
        OrderProjectImportAdapter adapter = new OrderProjectImportAdapter();

        // when
        adapter.setNumero(2234);
        adapter.setCentro("CAR");
        adapter.setProgressivoCentro(694);
        adapter.setCommessa(192);

        // then
        Assertions.assertThat(adapter.deriverOrderNumber()).isEqualTo("2234/CAR/694/192");

    }

    @Test
    public void deriverOrderNumber_when_nulls() {

        // given
        OrderProjectImportAdapter adapter = new OrderProjectImportAdapter();

        // when
        adapter.setNumero(null);
        adapter.setCentro("CAR");
        adapter.setProgressivoCentro(694);
        adapter.setCommessa(192);

        // then
        Assertions.assertThat(adapter.deriverOrderNumber()).isNull();

        // and when
        adapter.setNumero(2234);
        adapter.setCentro(null);
        adapter.setProgressivoCentro(694);
        adapter.setCommessa(192);

        // then
        Assertions.assertThat(adapter.deriverOrderNumber()).isEqualTo("2234//694/192");

        // and when
        adapter.setNumero(2234);
        adapter.setCentro("CAR");
        adapter.setProgressivoCentro(null);
        adapter.setCommessa(192);

        // then
        Assertions.assertThat(adapter.deriverOrderNumber()).isEqualTo("2234/CAR//192");

        // and when
        adapter.setNumero(2234);
        adapter.setCentro("CAR");
        adapter.setProgressivoCentro(694);
        adapter.setCommessa(null);

        // then
        Assertions.assertThat(adapter.deriverOrderNumber()).isEqualTo("2234/CAR/694/");

    }
}