package org.estatio.dom.party;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class PersonGenderTypeTest_title {

    @Test
    public void test() {
        assertThat(PersonGenderType.MALE.title(), is("Male"));
    }


}
