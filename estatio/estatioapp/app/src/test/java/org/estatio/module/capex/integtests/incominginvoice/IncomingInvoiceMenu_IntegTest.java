package org.estatio.module.capex.integtests.incominginvoice;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.sudo.SudoService;
import org.apache.isis.applib.services.wrapper.DisabledException;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.assetfinancial.fixtures.enums.BankAccountFaFa_enum;
import org.estatio.module.capex.app.IncomingInvoiceMenu;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_complete;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.financial.fixtures.bankaccount.enums.BankAccount_enum;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoiceMenu_IntegTest extends CapexModuleIntegTestAbstract {

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {

                executionContext.executeChild(this, OrganisationAndComms_enum.TopModelFr.builder());
                executionContext.executeChild(this, OrganisationAndComms_enum.HelloWorldFr.builder());
                executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.VivFr.builder());
                executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.MacFr.builder());
                executionContext.executeChild(this, Person_enum.FifineLacroixFr.builder());
                executionContext.executeChild(this, Person_enum.BertrandIncomingInvoiceManagerFr.builder());
            }
        });
    }

    @Test
    public  void findInvoicesBySupplierAndApprovalStateAndProperties_works() throws Exception {
        // given
        Party topModelFr = OrganisationAndComms_enum.TopModelFr.findUsing(serviceRegistry);
        Party helloWorldFr = OrganisationAndComms_enum.HelloWorldFr.findUsing(serviceRegistry);
        Property vivFr = Property_enum.VivFr.findUsing(serviceRegistry);
        Property macFr = Property_enum.MacFr.findUsing(serviceRegistry);

        IncomingInvoice invoiceTopViv = createIncomingInvoice(topModelFr, helloWorldFr, vivFr, IncomingInvoiceApprovalState.COMPLETED, "001");
        createIncomingInvoice(topModelFr, helloWorldFr, macFr, IncomingInvoiceApprovalState.COMPLETED, "002");
        IncomingInvoice invoiceHelloViv = createIncomingInvoice(helloWorldFr, topModelFr, vivFr, IncomingInvoiceApprovalState.COMPLETED, "003");
        createIncomingInvoice(helloWorldFr, topModelFr, macFr, IncomingInvoiceApprovalState.COMPLETED, "004");

        // when
        List<Property> propertyChoicesFifine = new ArrayList<>();
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.FifineLacroixFr.getRef().toLowerCase(), (Runnable) () ->
                propertyChoicesFifine.addAll(incomingInvoiceMenu.choices2FindInvoicesBySupplierAndApprovalStateAndProperties()));
        assertThat(propertyChoicesFifine).isNotEmpty();

        List<IncomingInvoice> foundInvoices1 = new ArrayList<>();
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.FifineLacroixFr.getRef().toLowerCase(), (Runnable) () ->
                foundInvoices1.addAll(wrap(incomingInvoiceMenu).findInvoicesBySupplierAndApprovalStateAndProperties(topModelFr, Collections.singletonList(IncomingInvoiceApprovalState.COMPLETED), propertyChoicesFifine)));

        List<IncomingInvoice> foundInvoices2 = new ArrayList<>();
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.FifineLacroixFr.getRef().toLowerCase(), (Runnable) () ->
                foundInvoices2.addAll(wrap(incomingInvoiceMenu).findInvoicesBySupplierAndApprovalStateAndProperties(helloWorldFr, Collections.singletonList(IncomingInvoiceApprovalState.COMPLETED), propertyChoicesFifine)));

        // then
        assertThat(foundInvoices1).containsExactly(invoiceTopViv);
        assertThat(foundInvoices2).containsExactly(invoiceHelloViv);

        // and when
        List<Property> propertyChoicesBertrand = new ArrayList<>();
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.BertrandIncomingInvoiceManagerFr.getRef().toLowerCase(), (Runnable) () ->
                propertyChoicesBertrand.addAll(incomingInvoiceMenu.choices2FindInvoicesBySupplierAndApprovalStateAndProperties()));
        assertThat(propertyChoicesBertrand).isEmpty();

        List<IncomingInvoice> foundInvoices3 = new ArrayList<>();
        queryResultsCache.resetForNextTransaction(); // workaround: clear MeService#me cache
        sudoService.sudo(Person_enum.FifineLacroixFr.getRef().toLowerCase(), (Runnable) () ->
                foundInvoices3.addAll(wrap(incomingInvoiceMenu).findInvoicesBySupplierAndApprovalStateAndProperties(helloWorldFr, Collections.singletonList(IncomingInvoiceApprovalState.COMPLETED), propertyChoicesBertrand)));

        // then
        assertThat(foundInvoices3).isEmpty();
    }


    private IncomingInvoice createIncomingInvoice(Party seller, Party buyer, Property property, IncomingInvoiceApprovalState approvalState, String invoiceNumber) {
        LocalDate invoiceDate = new LocalDate(2017, 1, 1);
        LocalDate dueDate = invoiceDate.minusMonths(1);
        PaymentMethod paymentMethod = PaymentMethod.BANK_TRANSFER;
        InvoiceStatus invoiceStatus = InvoiceStatus.NEW;
        String atPath = "/FRA";

        return incomingInvoiceRepository.create(IncomingInvoiceType.CAPEX, invoiceNumber, property, atPath, buyer, seller, invoiceDate, dueDate,
                null, paymentMethod, invoiceStatus, null, null,
                approvalState, false, null);
    }

    @Inject
    IncomingInvoiceMenu incomingInvoiceMenu;

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    SudoService sudoService;

}
