package org.estatio.module.capex.imports;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderProjectImportAdapterTest {

    @Test
    public void deriveOrderNumber() {

        // given
        OrderProjectImportAdapter adapter = new OrderProjectImportAdapter();

        // when
        adapter.setNumero(2234);
        adapter.setCentro("CAR");
        adapter.setProgressivoCentro("694");
        adapter.setCommessa("192");

        // then
        assertThat(adapter.deriveOrderNumber()).isEqualTo("2234/CAR/694/192");

    }

    @Test
    public void appendsZerosToProgressivoCentro() throws Exception {
        // given
        OrderProjectImportAdapter adapter = new OrderProjectImportAdapter();
        adapter.setProgressivoCentro("1");
        assertThat(adapter.getProgressivoCentro()).isEqualTo("1");

        // when
        adapter.correctProgressivoCentroIfNecessary();
        
        // then
        assertThat(adapter.getProgressivoCentro()).isEqualTo("001");

        // and when
        adapter.setProgressivoCentro("11");
        adapter.correctProgressivoCentroIfNecessary();

        // then
        assertThat(adapter.getProgressivoCentro()).isEqualTo("011");

        // and when
        adapter.setProgressivoCentro("111");
        adapter.correctProgressivoCentroIfNecessary();

        // then
        assertThat(adapter.getProgressivoCentro()).isEqualTo("111");
    }

    @Test
    public void deriveOrderNumber_when_nulls_oldFormat() {

        // given
        OrderProjectImportAdapter adapter = new OrderProjectImportAdapter();

        // when
        adapter.setNumero(null);
        adapter.setCentro("CAR");
        adapter.setProgressivoCentro("694");
        adapter.setCommessa("192");
        adapter.setWorkType(null);

        // then
        assertThat(adapter.deriveOrderNumber()).isNull();

        // and when
        adapter.setNumero(2234);
        adapter.setCentro(null);
        adapter.setProgressivoCentro("694");
        adapter.setCommessa("192");
        adapter.setWorkType(null);

        // then
        assertThat(adapter.deriveOrderNumber()).isEqualTo("2234//694/192");

        // and when
        adapter.setNumero(2234);
        adapter.setCentro("CAR");
        adapter.setProgressivoCentro(null);
        adapter.setCommessa("192");
        adapter.setWorkType(null);

        // then
        assertThat(adapter.deriveOrderNumber()).isEqualTo("2234/CAR//192");

        // and when
        adapter.setNumero(2234);
        adapter.setCentro("CAR");
        adapter.setProgressivoCentro("694");
        adapter.setCommessa(null);
        adapter.setWorkType(null);

        // then
        assertThat(adapter.deriveOrderNumber()).isEqualTo("2234/CAR/694/");
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
        assertThat(adapter.deriveOrderNumber()).isNull();

        // and when
        adapter.setNumero(2234);
        adapter.setCentro(null);
        adapter.setProgressivoCentro(null);
        adapter.setCommessa("192");
        adapter.setWorkType("001");

        // then
        assertThat(adapter.deriveOrderNumber()).isEqualTo("2234//192/001");
    }

    @Test
    public void deriveProjectNumberTest() {

        // given
        OrderProjectImportAdapter adapter = new OrderProjectImportAdapter();

        // when
        adapter.setCommessa("106");

        // then
        assertThat(adapter.deriveProjectReference()).isEqualTo("ITPR106");

    }
}