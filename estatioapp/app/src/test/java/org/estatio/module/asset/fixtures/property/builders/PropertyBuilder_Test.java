package org.estatio.module.asset.fixtures.property.builders;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class PropertyBuilder_Test {

    @Test
    public void testBuildUnitReference() throws Exception {

        final PropertyUnitsBuilder builder = new PropertyUnitsBuilder();

        Assertions.assertThat(builder.buildUnitReference("ABC", 1)).isEqualTo("ABC-001");
        Assertions.assertThat(builder.buildUnitReference("ABC", 23)).isEqualTo("ABC-023");
        Assertions.assertThat(builder.buildUnitReference("ABC", 456)).isEqualTo("ABC-456");
    }
}