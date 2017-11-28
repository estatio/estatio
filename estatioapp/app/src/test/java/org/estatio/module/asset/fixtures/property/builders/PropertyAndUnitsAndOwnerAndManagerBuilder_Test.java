package org.estatio.module.asset.fixtures.property.builders;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class PropertyAndUnitsAndOwnerAndManagerBuilder_Test {

    @Test
    public void testBuildUnitReference() throws Exception {

        final PropertyAndUnitsAndOwnerAndManagerBuilder propertyAndUnitsAndOwnerAndManagerBuilder = new PropertyAndUnitsAndOwnerAndManagerBuilder();

        Assertions.assertThat(propertyAndUnitsAndOwnerAndManagerBuilder.buildUnitReference("ABC", 1)).isEqualTo("ABC-001");
        Assertions.assertThat(propertyAndUnitsAndOwnerAndManagerBuilder.buildUnitReference("ABC", 23)).isEqualTo("ABC-023");
        Assertions.assertThat(propertyAndUnitsAndOwnerAndManagerBuilder.buildUnitReference("ABC", 456)).isEqualTo("ABC-456");
    }
}