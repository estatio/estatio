package org.estatio.module.capex.imports;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ProjectImportAdapter_Test {

    @Test
    public void limitLength_works() throws Exception {

        // given
        ProjectImportAdapter handler = new ProjectImportAdapter();
        String str1 = "123";
        String str2 = "1234";
        String str3 = "12345";
        // when, then
        Assertions.assertThat(handler.limitLength(str1, 4)).isEqualTo("123");
        Assertions.assertThat(handler.limitLength(str2, 4)).isEqualTo("1234");
        Assertions.assertThat(handler.limitLength(str3, 4)).isEqualTo("1234");
    }

}