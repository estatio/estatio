package com.eurocommercialproperties.estatio.integtest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.eurocommercialproperties.estatio.dom.asset.Properties;

import org.apache.isis.runtimes.dflt.testsupport.IsisSystemForTest;

public class IntegrationTest {

    @Rule
    public IsisSystemForTest isft = EstatioIntegTestBuilder.builder().build();


    @Before
    public void setUp() throws Exception {
        
    }
    
    @After
    public void tearDown() throws Exception {
        
    }
    
    @Test
    public void canBootstrap() throws Exception {
        Properties properties = isft.getService(Properties.class);
        assertThat(properties.allProperties().size(), is(2));
    }

}
