package com.eurocommercialproperties.estatio.dom.assets;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.eurocommercialproperties.estatio.dom.asset.UnitType;

public class UnitTypeTest_title {

    @Test
    public void test() {
        assertThat(UnitType.BOUTIQUE.title(), is("Boutique"));
        assertThat(UnitType.HYPERMARKET.title(), is("Hypermarket"));
    }


}
