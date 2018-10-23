package org.estatio.module.capex.imports;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class OrderProjectImportAdapterTest {

    @Test
    public void deriveOrderNumber() {

        // given
        OrderProjectImportAdapter adapter = new OrderProjectImportAdapter();

        // when
        adapter.setNumero(2234);
        adapter.setCentro("CAR");
        adapter.setProgressivoCentro(694);
        adapter.setCommessa("192");

        // then
        Assertions.assertThat(adapter.deriveOrderNumber()).isEqualTo("2234/CAR/694/192");

    }

    @Test
    public void deriveOrderNumber_when_nulls_oldFormat() {

        // given
        OrderProjectImportAdapter adapter = new OrderProjectImportAdapter();

        // when
        adapter.setNumero(null);
        adapter.setCentro("CAR");
        adapter.setProgressivoCentro(694);
        adapter.setCommessa("192");
        adapter.setWorkType(null);

        // then
        Assertions.assertThat(adapter.deriveOrderNumber()).isNull();

        // and when
        adapter.setNumero(2234);
        adapter.setCentro(null);
        adapter.setProgressivoCentro(694);
        adapter.setCommessa("192");
        adapter.setWorkType(null);

        // then
        Assertions.assertThat(adapter.deriveOrderNumber()).isEqualTo("2234//694/192");

        // and when
        adapter.setNumero(2234);
        adapter.setCentro("CAR");
        adapter.setProgressivoCentro(null);
        adapter.setCommessa("192");
        adapter.setWorkType(null);

        // then
        Assertions.assertThat(adapter.deriveOrderNumber()).isEqualTo("2234/CAR//192");

        // and when
        adapter.setNumero(2234);
        adapter.setCentro("CAR");
        adapter.setProgressivoCentro(694);
        adapter.setCommessa(null);
        adapter.setWorkType(null);

        // then
        Assertions.assertThat(adapter.deriveOrderNumber()).isEqualTo("2234/CAR/694/");
    }

    @Test
    public void deriveOrderNumber_when_nulls_newFormat() throws Exception {
        // given
        OrderProjectImportAdapter adapter = new OrderProjectImportAdapter();

        // when
        adapter.setNumero(null);
        adapter.setCentro("CAR");
        adapter.setProgressivoCentro(null);
        adapter.setCommessa("192");
        adapter.setWorkType("001");

        // then
        Assertions.assertThat(adapter.deriveOrderNumber()).isNull();

        // and when
        adapter.setNumero(2234);
        adapter.setCentro(null);
        adapter.setProgressivoCentro(null);
        adapter.setCommessa("192");
        adapter.setWorkType("001");

        // then
        Assertions.assertThat(adapter.deriveOrderNumber()).isEqualTo("2234//192/001");
    }
}