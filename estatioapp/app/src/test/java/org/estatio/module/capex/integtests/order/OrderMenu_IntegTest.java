package org.estatio.module.capex.integtests.order;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.sudo.SudoService;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.capex.app.OrderMenu;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.approval.OrderApprovalState;
import org.estatio.module.capex.dom.order.approval.transitions.Order_approvalTransitions;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.fixtures.project.enums.Project_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.fixtures.incoming.builders.CapexChargeHierarchyXlsxFixture;
import org.estatio.module.charge.fixtures.incoming.enums.IncomingCharge_enum;
import org.estatio.module.party.dom.Person;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderMenu_IntegTest extends CapexModuleIntegTestAbstract {

    @Inject OrderMenu orderMenu;

    @Inject SudoService sudoService;

    @Inject QueryResultsCache queryResultsCache;

    public static class CreateOrder extends OrderMenu_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.RonIt.builder());
                    executionContext.executeChild(this, new CapexChargeHierarchyXlsxFixture());
                    executionContext.executeChild(this, Project_enum.RonProjectIt.builder());
                    executionContext.executeChild(this, Person_enum.DomenicoOfficeAdministratorIt.builder());
                }
            });
        }

        @Test
        public void createOrderForItaly_happyCase() throws Exception {
            // given
            final Property property = PropertyAndUnitsAndOwnerAndManager_enum.RonIt.getProperty_d().findUsing(serviceRegistry);
            final Project project = Project_enum.RonProjectIt.findUsing(serviceRegistry);
            final Charge charge = IncomingCharge_enum.ItConstruction.findUsing(serviceRegistry);

            final Person officeAdministrator = Person_enum.DomenicoOfficeAdministratorIt.findUsing(serviceRegistry);

            // when
            queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
            final Order order = sudoService.sudo(officeAdministrator.getUsername(), () ->
                    wrap(orderMenu).createOrder(property, project, charge));

            // then
            assertThat(order.getOrderNumber()).isEqualTo("0001/RON/001/001");
            assertThat(order.getProperty()).isEqualTo(property);
            assertThat(order.getApprovalState()).isEqualTo(OrderApprovalState.NEW);
            assertThat(mixin(Order_approvalTransitions.class, order).coll()).hasSize(2);

            assertThat(order.getItems()).hasSize(1);
            final OrderItem item = order.getItems().first();

            assertThat(item.getProperty()).isEqualTo(property);
            assertThat(item.getProject()).isEqualTo(project);
            assertThat(item.getCharge()).isEqualTo(charge);

        }

    }
}
