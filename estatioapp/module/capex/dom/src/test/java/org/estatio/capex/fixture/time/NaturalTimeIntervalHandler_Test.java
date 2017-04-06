package org.estatio.capex.fixture.time;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class NaturalTimeIntervalHandler_Test {

    public static class GetParentNameTest extends NaturalTimeIntervalHandler_Test {

        @Test
        public void when_quarter() throws Exception {

            final NaturalTimeIntervalHandler handler = new NaturalTimeIntervalHandler();
            handler.setName("2016Q3");

            final String parentName = handler.getParentName();
            Assertions.assertThat(parentName).isEqualTo("2016");
        }

        @Test
        public void when_year() throws Exception {

            final NaturalTimeIntervalHandler handler = new NaturalTimeIntervalHandler();
            handler.setName("2016");

            final String parentName = handler.getParentName();
            Assertions.assertThat(parentName).isNull();
        }

    }


}