package com.eurocommercialproperties.estatio.integtest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import junit.framework.Assert;

import org.apache.isis.runtimes.dflt.testsupport.IsisSystemForTest;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import com.eurocommercialproperties.estatio.dom.asset.Properties;
import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.asset.Unit;
import com.eurocommercialproperties.estatio.dom.lease.LeaseTerm;
import com.eurocommercialproperties.estatio.dom.lease.LeaseTerms;

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
    
    @Test
    public void getIndexationFrequencyCannotBeNull() throws Exception {
        LeaseTerms terms = isft.getService(LeaseTerms.class);
        List<LeaseTerm> alLeaseTerms = terms.allLeaseTerms();
        LeaseTerm term = alLeaseTerms.get(0);
        Assert.assertNotNull(term.getLeaseItem().getIndexationFrequency().nextDate(new LocalDate(2012,1,1)));
    }
    
    
}
