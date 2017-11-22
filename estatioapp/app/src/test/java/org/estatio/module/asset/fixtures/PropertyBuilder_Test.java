package org.estatio.module.asset.fixtures;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import org.estatio.module.asset.fixtures.PropertyBuilder;

public class PropertyBuilder_Test {

    @Test
    public void testBuildUnitReference() throws Exception {

        final PropertyBuilder propertyBuilder = new PropertyBuilder();

        Assertions.assertThat(propertyBuilder.buildUnitReference("ABC", 1)).isEqualTo("ABC-001");
        Assertions.assertThat(propertyBuilder.buildUnitReference("ABC", 23)).isEqualTo("ABC-023");
        Assertions.assertThat(propertyBuilder.buildUnitReference("ABC", 456)).isEqualTo("ABC-456");
    }
}