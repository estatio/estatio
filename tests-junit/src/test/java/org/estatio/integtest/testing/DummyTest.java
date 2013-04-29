package org.estatio.integtest.testing;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.estatio.api.Api;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.party.Parties;
import org.estatio.integtest.IntegrationSystemForTestRule;
import org.estatio.jdo.LeasesJdo;
import org.estatio.jdo.PartiesJdo;
import org.estatio.jdo.PropertiesJdo;
import org.hamcrest.core.Is;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.apache.isis.core.integtestsupport.IsisSystemForTest;

public class DummyTest {


    @Test
    public void dummy() throws Exception {
        //Assume.assumeThat(System.getProperty("env"), is(not("CI")));
        Assume.assumeThat(System.getProperty("env"), is("CI"));
        fail();
    }


}
