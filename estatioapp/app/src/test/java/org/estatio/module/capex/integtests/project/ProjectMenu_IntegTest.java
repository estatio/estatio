/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.module.capex.integtests.app.taskreminder;

import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;

import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.estatio.module.capex.app.taskreminder.NumeratorForOrderNumberMenu;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.numerator.dom.NumeratorRepository;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class NumeratorForOrderNumberMenu_IntegTest extends CapexModuleIntegTestAbstract {

    private final String format = "GBFO-%04d";
    private final BigInteger lastIncrement = BigInteger.ZERO;


    @Inject
    NumeratorForOrderNumberMenu numeratorForOrderNumberMenu;

    @Inject
    NumeratorRepository numeratorRepository;

    @Inject
    FakeDataService fakeDataService;

    @Inject
    BookmarkService bookmarkService;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext ec) {

                // we set up 4 orgs of a variety of countries, but this functionality only applies to Italian.
                final Organisation_enum[] orgs = {
                        Organisation_enum.HelloWorldIt,
                        Organisation_enum.HelloWorldGb,
                        Organisation_enum.HelloWorldFr,
                        Organisation_enum.HelloWorldIt01
                };

                for (final Organisation_enum org : orgs) {
                    ec.executeChild(this, org);
                    org.findUsing(serviceRegistry).addRole(IncomingInvoiceRoleTypeEnum.ECP);
                }
            }
        });
    }

    @Test
    public void create_for_Italy_only() {

        // given
        List<Numerator> numerators = numeratorRepository.allNumerators();
        assertThat(numerators).isEmpty();

        // when
        final List<Party> parties = numeratorForOrderNumberMenu.choices0CreateOrderNumberNumerator();

        // then only the Italian parties are available.
        assertThat(parties).hasSize(2);
        assertThat(parties).extracting(Party::getApplicationTenancyPath).containsOnly("/ITA");

        // when
        final Organisation selectedParty = (Organisation) fakeDataService.collections().anyOf(parties);
        final Numerator numerator =
                wrap(numeratorForOrderNumberMenu).createOrderNumberNumerator(selectedParty, format, lastIncrement);

        // then
        assertThat(numerator).isNotNull();
        assertThat(numerator.getApplicationTenancy()).isSameAs(selectedParty.getApplicationTenancy());
        assertThat(numerator.getFormat()).isEqualTo(format);
        assertThat(numerator.getLastIncrement()).isEqualTo(lastIncrement);

        final Bookmark bookmark = bookmarkService.bookmarkFor(selectedParty);
        assertThat(numerator.getObjectType()).isEqualTo(bookmark.getObjectType());
        assertThat(numerator.getObjectIdentifier()).isEqualTo(bookmark.getIdentifier());

        // when
        final Numerator numeratorAfter =
                wrap(numeratorForOrderNumberMenu).createOrderNumberNumerator(selectedParty, format, lastIncrement);

        // then
        assertThat(numeratorAfter).isSameAs(numerator);

        List<Numerator> numeratorsFromRepoAfter = numeratorRepository.allNumerators();
        assertThat(numeratorsFromRepoAfter).hasSize(1);
        assertThat(numeratorsFromRepoAfter.get(0)).isSameAs(numerator);

    }


}