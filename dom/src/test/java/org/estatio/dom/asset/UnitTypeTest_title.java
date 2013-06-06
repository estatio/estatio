package org.estatio.dom.asset;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;


public class UnitTypeTest_title {

    @Test
    public void test() {
        assertThat(UnitType.BOUTIQUE.title(), is("Boutique"));
        assertThat(UnitType.HYPERMARKET.title(), is("Hypermarket"));
    }


}
