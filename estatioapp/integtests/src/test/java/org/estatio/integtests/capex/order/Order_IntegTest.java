package org.estatio.integtests.capex.order;

import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.transitions.Document_categorisationTransitions;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.order.OrderFixture;
import org.estatio.integtests.EstatioIntegrationTest;

public class Order_IntegTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final FixtureScript.ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new OrderFixture());
            }
        });
    }

    @Test
    public void orderFixtureLoadedProperly() throws Exception {

        Assertions.assertThat(orderRepository.listAll().size()).isEqualTo(1);

        List<Document> unclassifiedIncomingOrders = incomingDocumentRepository.findUnclassifiedIncomingOrders();
        Assertions.assertThat(unclassifiedIncomingOrders.size()).isEqualTo(0);

        Document fakeOrderDoc = incomingDocumentRepository.findAllIncomingDocumentsByName("fakeOrder2.pdf").get(0);
        List<IncomingDocumentCategorisationStateTransition> transitions = mixin(Document_categorisationTransitions.class, fakeOrderDoc).coll();
        Assertions.assertThat(transitions.size()).isEqualTo(2);
    }

    @Inject OrderRepository orderRepository;

    @Inject IncomingDocumentRepository incomingDocumentRepository;

}
