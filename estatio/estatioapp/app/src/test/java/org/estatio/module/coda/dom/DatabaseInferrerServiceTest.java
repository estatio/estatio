package org.estatio.module.coda.dom;

import com.google.common.collect.ImmutableMap;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class DatabaseInferrerServiceTest {

    DatabaseInferrerService service;

    @Before
    public void setUp() throws Exception {
        service = new DatabaseInferrerService();
    }

    @Test
    public void when_empty_then_unknown() {
        service.init(ImmutableMap.of());
        Assertions.assertThat(service.getDriver()).isEqualTo(DatabaseInferrerService.Driver.UNKNOWN);
    }

    @Test
    public void when_anything_else_then_unknown() {
        service.init(ImmutableMap.of(DatabaseInferrerService.KEY, "jdbc:garbage:xxxx"));
        Assertions.assertThat(service.getDriver()).isEqualTo(DatabaseInferrerService.Driver.UNKNOWN);
    }

    @Test
    public void when_sqlserver() {
        service.init(ImmutableMap.of(DatabaseInferrerService.KEY, "jdbc:sqlserver:whatever"));
        Assertions.assertThat(service.getDriver()).isEqualTo(DatabaseInferrerService.Driver.SQLSERVER);
    }

    @Test
    public void when_hsqldb() {
        service.init(ImmutableMap.of(DatabaseInferrerService.KEY, "jdbc:hsqldb:whatever"));
        Assertions.assertThat(service.getDriver()).isEqualTo(DatabaseInferrerService.Driver.HSQLDB);
    }
}