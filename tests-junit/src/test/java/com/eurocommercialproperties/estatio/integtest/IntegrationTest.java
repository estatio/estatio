package com.eurocommercialproperties.estatio.integtest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import com.eurocommercialproperties.estatio.dom.asset.Properties;
import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.asset.Unit;

import org.apache.isis.runtimes.dflt.testsupport.IsisSystemForTest;

public class IntegrationTest {


    public TestTimer outer = new TestTimer("outer");

    public IsisSystemForTest isft = buildSystemOncePerTestClass();

    public TestTimer inner = new TestTimer("inner");

    private static IsisSystemForTest system;
    private static IsisSystemForTest buildSystemOncePerTestClass() {
        if(system == null) {
            system = EstatioIntegTestBuilder.builder().build();
        }
        return system;
    }


    @Rule
    public RuleChain chain = RuleChain.outerRule(outer).around(isft).around(inner);


    
    @Test
    public void properties() throws Exception {
        Properties properties = isft.getService(Properties.class);
        assertThat(properties.allProperties().size(), is(2));
    }

    @Test
    public void units() throws Exception {
        Properties properties = isft.getService(Properties.class);
        List<Property> allProperties = properties.allProperties();
        Property property = allProperties.get(0);
        List<Unit> units = property.getUnits();
        assertThat(units.size(), is(25));
    }

    @Test
    public void units2() throws Exception {
        Properties properties = isft.getService(Properties.class);
        List<Property> allProperties = properties.allProperties();
        Property property = allProperties.get(1);
        List<Unit> units = property.getUnits();
        assertThat(units.size(), is(40));
    }

    @Test
    public void units3() throws Exception {
        Properties properties = isft.getService(Properties.class);
        List<Property> allProperties = properties.allProperties();
        Property property = allProperties.get(1);
        List<Unit> units = property.getUnits();
        assertThat(units.size(), is(40));
    }
    
    @Test
    public void units4() throws Exception {
        Properties properties = isft.getService(Properties.class);
        List<Property> allProperties = properties.allProperties();
        Property property = allProperties.get(1);
        List<Unit> units = property.getUnits();
        assertThat(units.size(), is(40));
    }
    
    @Test
    public void units5() throws Exception {
        Properties properties = isft.getService(Properties.class);
        List<Property> allProperties = properties.allProperties();
        Property property = allProperties.get(1);
        List<Unit> units = property.getUnits();
        assertThat(units.size(), is(40));
    }
    
    
    @Test
    public void units6() throws Exception {
        Properties properties = isft.getService(Properties.class);
        List<Property> allProperties = properties.allProperties();
        Property property = allProperties.get(1);
        List<Unit> units = property.getUnits();
        assertThat(units.size(), is(40));
    }
    
    
    @Test
    public void units7() throws Exception {
        Properties properties = isft.getService(Properties.class);
        List<Property> allProperties = properties.allProperties();
        Property property = allProperties.get(1);
        List<Unit> units = property.getUnits();
        assertThat(units.size(), is(40));
    }
    
}
