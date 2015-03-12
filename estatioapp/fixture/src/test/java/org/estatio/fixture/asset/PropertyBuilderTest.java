package org.estatio.fixture.asset;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class PropertyBuilderTest {

    @Test
    public void testBuildUnitReference() throws Exception {

        final PropertyBuilder propertyBuilder = new PropertyBuilder();

        Assertions.assertThat(propertyBuilder.buildUnitReference("ABC", 1)).isEqualTo("ABC-001");
        Assertions.assertThat(propertyBuilder.buildUnitReference("ABC", 23)).isEqualTo("ABC-023");
        Assertions.assertThat(propertyBuilder.buildUnitReference("ABC", 456)).isEqualTo("ABC-456");
    }
}