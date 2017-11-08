package org.estatio.module.asset.dom;

import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.user.ApplicationUser;

import org.incode.module.unittestsupport.dom.repo.FinderInteraction;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyRepository_Test {

    FinderInteraction finderInteraction;
    PropertyRepository propertyRepository;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    MeService mockMeService;

    @Before
    public void setup() {
        propertyRepository = new PropertyRepository() {

            @Override
            protected <T> T uniqueMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderInteraction.FinderMethod.UNIQUE_MATCH);
                return (T) new Property();
            }
            @Override
            protected List<Property> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderInteraction.FinderMethod.ALL_INSTANCES);
                return null;
            }
            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderInteraction.FinderMethod.ALL_MATCHES);
                return null;
            }
        };

        propertyRepository.meService = mockMeService;
    }

    public static class AutoComplete extends PropertyRepository_Test {

        @Test
        public void happyCase() {

            // given
            final ApplicationUser user = new ApplicationUser();
            context.checking(new Expectations() {{
                allowing(mockMeService).me();
                will(returnValue(user));
            }});
            user.setAtPath("/ITA");


            // when
            propertyRepository.autoComplete("X?yz");

            // then
            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(Property.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findByReferenceOrName");

            final Map<String, Object> argsByParam = finderInteraction.getArgumentsByParameterName();
            assertThat(argsByParam.get("referenceOrName")).isEqualTo((Object) "(?i).*X.yz.*");
            assertThat(argsByParam).hasSize(1);
        }

    }
}
