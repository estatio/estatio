package org.estatio.module.budgetassignment.integtests.budget;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.sudo.SudoService;
import org.apache.isis.applib.services.wrapper.DisabledException;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.base.dom.EstatioRole;
import org.estatio.module.base.fixtures.security.users.personas.EstatioAdmin;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keytable.FoundationValueType;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.keytable.PartitioningTable;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.budget.dom.partioning.Partitioning;
import org.estatio.module.budget.fixtures.budgets.enums.Budget_enum;
import org.estatio.module.budget.fixtures.partitioning.enums.Partitioning_enum;
import org.estatio.module.budgetassignment.contributions.Budget_Calculate;
import org.estatio.module.budgetassignment.contributions.Budget_Remove;
import org.estatio.module.budgetassignment.integtests.BudgetAssignmentModuleIntegTestAbstract;
import org.estatio.module.lease.dom.LeaseTermForServiceCharge;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceCharge_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class Budget_IntegTest extends BudgetAssignmentModuleIntegTestAbstract {

    @Inject
    BudgetRepository budgetRepository;

    public static class NextBudgetTest extends Budget_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext executionContext) {
                    executionContext.executeChild(this, Partitioning_enum.OxfPartitioning2015.builder());
                    executionContext.executeChild(this, Partitioning_enum.BudPartitioning2015.builder());

                    executionContext.executeChildT(this, Lease_enum.BudPoison001Nl.builder());
                    executionContext.executeChildT(this, Lease_enum.BudMiracle002Nl.builder());
                    executionContext.executeChildT(this, Lease_enum.BudHello003Nl.builder());
                    executionContext.executeChildT(this, Lease_enum.BudDago004Nl.builder());
                    executionContext.executeChildT(this, Lease_enum.BudNlBank004Nl.builder());
                    executionContext.executeChildT(this, Lease_enum.BudHyper005Nl.builder());
                    executionContext.executeChildT(this, Lease_enum.BudHello006Nl.builder());
                }
            });
        }



        @Test
        public void nextBudgetTest() throws Exception {

            // given
            Budget budget2015 = Budget_enum.BudBudget2015.findUsing(serviceRegistry);

            Property propertyBud = Property_enum.BudNl.findUsing(serviceRegistry);
            List<Budget> budgetsForBud = budgetRepository.findByProperty(budget2015.getProperty());
            assertThat(budgetsForBud.size()).isEqualTo(1);

            // when
            Budget budget2016 = wrap(budget2015).createNextBudget();
            transactionService.nextTransaction();

            // then
            assertThat(budgetRepository.findByProperty(propertyBud).size()).isEqualTo(2);
            assertThat(budget2016.getProperty()).isEqualTo(propertyBud);
            assertThat(budget2016.getStartDate()).isEqualTo(new LocalDate(2016, 01, 01));
            assertThat(budget2016.getEndDate()).isEqualTo(new LocalDate(2016, 12, 31));
            assertThat(budget2016.getPartitionings().size()).isEqualTo(1);
            assertThat(budget2016.getPartitionings().first().getType()).isEqualTo(BudgetCalculationType.BUDGETED);
            assertThat(budget2016.getPartitionings().first().getStartDate()).isEqualTo(budget2016.getStartDate());
            assertThat(budget2016.getPartitionings().first().getEndDate()).isEqualTo(budget2016.getEndDate());

            assertThat(budget2016.getItems().size()).isEqualTo(budget2015.getItems().size());
            // this serves as createCopyFor test for budgetItem
            assertThat(budget2016.getItems().first().getBudget()).isEqualTo(budget2016);
            assertThat(budget2016.getItems().first().getCharge()).isEqualTo(budget2015.getItems().first().getCharge());
            assertThat(budget2016.getItems().first().getApplicationTenancy()).isEqualTo(budget2015.getItems().first().getApplicationTenancy());
            assertThat(budget2016.getItems().first().getValues().size()).isEqualTo(1);
            assertThat(budget2016.getItems().first().getValues().first().getValue()).isEqualTo(budget2015.getItems().first().getValues().first().getValue());
            assertThat(budget2016.getItems().first().getPartitionItems().size()).isEqualTo(1);
            assertThat(budget2016.getItems().first().getPartitionItems().get(0).getPartitioning()).isEqualTo(budget2016.getPartitionings().first());
            assertThat(budget2016.getItems().first().getPartitionItems().get(0).getCharge()).isEqualTo(budget2015.getItems().first().getPartitionItems().get(0).getCharge());
            assertThat(budget2016.getItems().first().getPartitionItems().get(0).getPercentage()).isEqualTo(budget2015.getItems().first().getPartitionItems().get(0).getPercentage());
            assertThat(budget2016.getItems().first().getPartitionItems().get(0).getPartitioningTable().getName()).isEqualTo(budget2015.getItems().first().getPartitionItems().get(0).getPartitioningTable().getName());

            assertThat(budget2016.getKeyTables().size()).isEqualTo(budget2015.getKeyTables().size());
            // this serves as createCopyFor test for keyTable
            KeyTable firstNewKeyTable = budget2016.getKeyTables().first();
            KeyTable lastNewKeyTable = budget2016.getKeyTables().last();
            assertThat(firstNewKeyTable.getName()).isEqualTo(budget2015.getKeyTables().first().getName());
            assertThat(lastNewKeyTable.getName()).isEqualTo(budget2015.getKeyTables().last().getName());
            assertThat(firstNewKeyTable.getFoundationValueType()).isEqualTo(FoundationValueType.AREA);
            assertThat(lastNewKeyTable.getFoundationValueType()).isEqualTo(FoundationValueType.COUNT);
            assertThat(firstNewKeyTable.getItems().size()).isEqualTo(budget2015.getKeyTables().first().getItems().size());
            assertThat(firstNewKeyTable.getItems().first().getValue()).isEqualTo(new BigDecimal("35.714286"));

        }

    }

    public static class NextBudget extends Budget_IntegTest {

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void when_does_not_exist(){

            // given
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext executionContext) {
                    executionContext.executeChild(this, Partitioning_enum.OxfPartitioning2015.builder());
                    executionContext.executeChild(this, Partitioning_enum.BudPartitioning2015.builder());

                    executionContext.executeChildT(this, Lease_enum.BudPoison001Nl.builder());
                    executionContext.executeChildT(this, Lease_enum.BudMiracle002Nl.builder());
                    executionContext.executeChildT(this, Lease_enum.BudHello003Nl.builder());
                    executionContext.executeChildT(this, Lease_enum.BudDago004Nl.builder());
                    executionContext.executeChildT(this, Lease_enum.BudNlBank004Nl.builder());
                    executionContext.executeChildT(this, Lease_enum.BudHyper005Nl.builder());
                    executionContext.executeChildT(this, Lease_enum.BudHello006Nl.builder());

                }
            });

            Budget budget2015 = Budget_enum.OxfBudget2015.findUsing(serviceRegistry);
            Budget budget2016 = Budget_enum.OxfBudget2016.findUsing(serviceRegistry);

            assertThat(budget2015).isNotNull();
            assertThat(budget2016).isNull();

            // when
            final Budget nextBudget = wrap(budget2015).createNextBudget();

            // then
            assertThat(nextBudget).isNotNull();
        }

        @Test
        public void when_exists_already(){

            // given
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext executionContext) {
                    executionContext.executeChild(this, Partitioning_enum.OxfPartitioning2015.builder());
                    executionContext.executeChild(this, Partitioning_enum.BudPartitioning2015.builder());

                    executionContext.executeChildT(this, Lease_enum.BudPoison001Nl.builder());
                    executionContext.executeChildT(this, Lease_enum.BudMiracle002Nl.builder());
                    executionContext.executeChildT(this, Lease_enum.BudHello003Nl.builder());
                    executionContext.executeChildT(this, Lease_enum.BudDago004Nl.builder());
                    executionContext.executeChildT(this, Lease_enum.BudNlBank004Nl.builder());
                    executionContext.executeChildT(this, Lease_enum.BudHyper005Nl.builder());
                    executionContext.executeChildT(this, Lease_enum.BudHello006Nl.builder());

                    executionContext.executeChild(this, Budget_enum.OxfBudget2016.builder());
                }
            });

            Budget budget2015 = Budget_enum.OxfBudget2015.findUsing(serviceRegistry);
            Budget budget2016 = Budget_enum.OxfBudget2016.findUsing(serviceRegistry);

            assertThat(budget2015).isNotNull();
            assertThat(budget2016).isNotNull();

            // expect
            expectedException.expect(InvalidException.class);
            expectedException.expectMessage("Reason: This budget already exists");

            // when
            wrap(budget2015).createNextBudget();
        }

    }

    public static class remove_budget extends Budget_IntegTest {


        LeaseTermForServiceCharge lastServiceChargeTerm;
        Budget topmodelBudget2015;
        LocalDate startDate;

        @Before
        public void setUp() throws Exception {

            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {

                    executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
                    executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb.builder());
                    executionContext.executeChild(this, Partitioning_enum.OxfPartitioning2015.builder());

                }
            });

        }

        @Test
        public void remove_works_when_not_assigned() {

            // given
            topmodelBudget2015 = Budget_enum.OxfBudget2015.findUsing(serviceRegistry);
            Assertions.assertThat(topmodelBudget2015).isNotNull();

            wrap(mixin(Budget_Calculate.class, topmodelBudget2015)).calculate(false);

            assertThat(budgetCalculationRepository.allBudgetCalculations()).isNotEmpty();
            assertThat(repositoryService.allInstances(PartitioningTable.class)).isNotEmpty();
            assertThat(repositoryService.allInstances(PartitionItem.class)).isNotEmpty();
            assertThat(repositoryService.allInstances(Partitioning.class)).isNotEmpty();
            assertThat(repositoryService.allInstances(BudgetItem.class)).isNotEmpty();
            assertThat(repositoryService.allInstances(Budget.class)).isNotEmpty();


            // when
            sudoService.sudo(EstatioAdmin.USER_NAME, Lists.newArrayList(EstatioRole.ADMINISTRATOR.getRoleName()),
                    new Runnable() {
                        @Override public void run() {
                            wrap(mixin(Budget_Remove.class, topmodelBudget2015)).removeBudget(true);
                        }
                    });

            // then
            assertThat(budgetCalculationRepository.allBudgetCalculations()).isEmpty();
            assertThat(repositoryService.allInstances(PartitioningTable.class)).isEmpty();
            assertThat(repositoryService.allInstances(PartitionItem.class)).isEmpty();
            assertThat(repositoryService.allInstances(Partitioning.class)).isEmpty();
            assertThat(repositoryService.allInstances(BudgetItem.class)).isEmpty();
            assertThat(repositoryService.allInstances(Budget.class)).isEmpty();

        }

        @Test
        public void remove_disabled_when_assigned() {

            // given
            topmodelBudget2015 = Budget_enum.OxfBudget2015.findUsing(serviceRegistry);
            Assertions.assertThat(topmodelBudget2015).isNotNull();

            wrap(mixin(Budget_Calculate.class, topmodelBudget2015)).calculate(true);

            // expect
            expectedExceptions.expect(DisabledException.class);
            expectedExceptions.expectMessage("This budget is assigned already");

            // when
            sudoService.sudo(EstatioAdmin.USER_NAME, Lists.newArrayList(EstatioRole.ADMINISTRATOR.getRoleName()),
                    new Runnable() {
                        @Override public void run() {
                            wrap(mixin(Budget_Remove.class, topmodelBudget2015)).removeBudget(true);
                        }
                    });

        }

        @Inject BudgetCalculationRepository budgetCalculationRepository;

        @Inject SudoService sudoService;

        @Inject RepositoryService repositoryService;


    }

}
