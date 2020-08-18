package org.estatio.module.coda.integtests;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.base.dom.utils.StringUtils;

import org.estatio.module.coda.dom.elements.CodaElementLevel;
import org.estatio.module.coda.dom.elements.CodaElementRepository;
import org.estatio.module.coda.fixtures.elements.enums.CodaElement_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class CodaElementRepository_IntegTest extends CodaModuleIntegTestAbstract {

    public static class Repo_tests extends CodaElementRepository_IntegTest {

        @Before
        public void setupData() {

            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext ec) {
                    ec.executeChildren(this, CodaElement_enum.FRL5_12345.builder());
                    ec.executeChildren(this, CodaElement_enum.FRL4_77777.builder());
                }
            });
        }

        @Test
        public void finders_work() throws Exception {

            // given fixtures
            // when, then
            assertThat(codaElementRepository.findByLevel(CodaElementLevel.LEVEL_5)).hasSize(1);
            assertThat(codaElementRepository.findByLevel(CodaElementLevel.LEVEL_5)).contains(CodaElement_enum.FRL5_12345.findUsing(serviceRegistry));
            assertThat(codaElementRepository.findByLevel(CodaElementLevel.LEVEL_4)).hasSize(1);
            assertThat(codaElementRepository.findByLevel(CodaElementLevel.LEVEL_4)).contains(CodaElement_enum.FRL4_77777.findUsing(serviceRegistry));
            assertThat(codaElementRepository.findByLevelAndCode(CodaElementLevel.LEVEL_5, CodaElement_enum.FRL5_12345.getCode())).isEqualTo(CodaElement_enum.FRL5_12345.findUsing(serviceRegistry));

        }

    }

    public static class FindByLevelAndCode extends CodaElementRepository_IntegTest {

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
                    "XX1234"));
        }

        @Test
        public void searchByCode() throws Exception {
            //When then
            assertThat(codaElementRepository.searchByCodeOrName(StringUtils.wildcardToCaseInsensitiveRegex("*12*")))
                    .hasSize(1);
            assertThat(codaElementRepository.searchByCodeOrName(StringUtils.wildcardToCaseInsensitiveRegex("*elem*")))
                    .hasSize(1);
            assertThat(codaElementRepository.searchByCodeOrName(StringUtils.wildcardToCaseInsensitiveRegex("*xxxx*")))
                    .hasSize(0);
        }

    }

    @Inject CodaElementRepository codaElementRepository;

}