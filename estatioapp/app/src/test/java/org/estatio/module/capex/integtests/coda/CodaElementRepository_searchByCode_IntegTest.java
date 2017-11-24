package org.estatio.module.capex.integtests.coda;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.incode.module.base.dom.utils.StringUtils;

import org.estatio.module.capex.dom.coda.CodaElementLevel;
import org.estatio.module.capex.dom.coda.CodaElementRepository;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class CodaElementRepository_searchByCode_IntegTest extends CapexModuleIntegTestAbstract {

    @Before
    public void setUp() throws Exception {
        codaElementRepository.findOrCreate(
                CodaElementLevel.LEVEL_5,
                "XX1234",
                "Some Element");
        assertThat(codaElementRepository.listAll()).hasSize(1);
    }

    @Test
    public void findByLevelAndCode() throws Exception {
        assertThat(codaElementRepository.findByLevelAndCode(
                CodaElementLevel.LEVEL_5,
                "XX1234")).isPresent();
    }

    @Test
    public void searchByCode() throws Exception {
        //When then
        assertThat(codaElementRepository.searchByCodeOrName(StringUtils.wildcardToCaseInsensitiveRegex("*12*"))).hasSize(1);
        assertThat(codaElementRepository.searchByCodeOrName(StringUtils.wildcardToCaseInsensitiveRegex("*elem*"))).hasSize(1);
        assertThat(codaElementRepository.searchByCodeOrName(StringUtils.wildcardToCaseInsensitiveRegex("*xxxx*"))).hasSize(0);
    }

    @Inject CodaElementRepository codaElementRepository;

}