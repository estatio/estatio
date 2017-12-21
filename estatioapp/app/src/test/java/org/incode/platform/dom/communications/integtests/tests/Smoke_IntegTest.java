package org.incode.platform.dom.communications.integtests.tests;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.xactn.TransactionService;

import org.isisaddons.module.command.IncodeSpiCommandModule;
import org.isisaddons.module.command.dom.BackgroundCommandServiceJdoRepository;
import org.isisaddons.module.command.dom.CommandJdo;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwnerLinkRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.communications.dom.impl.commchannel.EmailAddress;
import org.incode.module.communications.dom.impl.commchannel.PostalAddress;
import org.incode.module.communications.dom.impl.comms.CommChannelRole;
import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.communications.dom.impl.comms.CommunicationState;
import org.incode.module.communications.dom.impl.comms.Communication_downloadPdfForPosting;
import org.incode.module.communications.dom.mixins.Document_sendByEmail;
import org.incode.module.communications.dom.mixins.Document_sendByPost;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.platform.dom.communications.integtests.CommunicationsModuleIntegTestAbstract;
import org.incode.platform.dom.communications.integtests.app.services.fakeemail.EmailMessage;
import org.incode.platform.dom.communications.integtests.app.services.fakeemail.FakeEmailService;
import org.incode.platform.dom.communications.integtests.demo.dom.demowithnotes.DemoObjectWithNotes;
import org.incode.platform.dom.communications.integtests.demo.dom.demowithnotes.DemoObjectWithNotesMenu;
import org.incode.platform.dom.communications.integtests.demo.dom.invoice.DemoInvoice;
import org.incode.platform.dom.communications.integtests.demo.dom.invoice.DemoInvoiceRepository;
import org.incode.platform.dom.communications.integtests.dom.communications.fixture.DemoObjectWithNotes_and_DemoInvoice_and_docs_and_comms_create;
import org.incode.platform.dom.communications.integtests.dom.communications.fixture.DemoObjectWithNotes_and_DemoInvoice_and_docs_and_comms_recreate;
import org.incode.platform.dom.communications.integtests.dom.communications.fixture.data.democust2.DemoObjectWithNote_and_DemoInvoice_create3;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class Smoke_IntegTest extends CommunicationsModuleIntegTestAbstract {

    @Inject
    FixtureScripts fixtureScripts;

    @Inject
    TransactionService transactionService;

    @Inject
    DemoObjectWithNotesMenu customerMenu;

    @Inject
    CommunicationChannelOwnerLinkRepository linkRepository;

    @Inject
    DemoInvoiceRepository invoiceRepository;

    @Inject
    PaperclipRepository paperclipRepository;


    @Test
    public void can_send_email() throws Exception {

        // given
        fixtureScripts.runFixtureScript(new IncodeSpiCommandModule().getTeardownFixtureWillDelete(), null);
        fixtureScripts.runFixtureScript(new DemoObjectWithNotes_and_DemoInvoice_and_docs_and_comms_create(), null);
        transactionService.nextTransaction();

        // and so given customer with an email
        final DemoObjectWithNotes fred = customerMenu.findDemoObjectsWithNotesByName(
                DemoObjectWithNote_and_DemoInvoice_create3.FRED_HAS_EMAIL_AND_PHONE).get(0);

        final EmailAddress fredEmail = (EmailAddress) linkRepository
                .findByOwnerAndCommunicationChannelType(fred, CommunicationChannelType.EMAIL_ADDRESS)
                .get(0)
                .getCommunicationChannel();

        // and with an invoice
        final DemoInvoice fredInvoice = invoiceRepository.findByCustomer(fred).get(0);

        // that has an attached document
        final Paperclip paperclip = paperclipRepository.findByAttachedTo(fredInvoice).get(0);
        final DocumentAbstract document = paperclip.getDocument();

        // when
        final Document_sendByEmail documentEmail = mixin(Document_sendByEmail.class, document);
        final Set<EmailAddress> emailAddresses = documentEmail.choices0Act();

        // then
        assertThat(emailAddresses).contains(fredEmail);

        // and when
        final Communication comm = wrap(documentEmail).act(fredEmail, null, null, null, null, null);

        // then
        assertThat(comm).isNotNull();

        assertThat(comm.getState()).isEqualTo(CommunicationState.PENDING);
        assertThat(comm.getCreatedAt()).isNotNull();
        assertThat(comm.getType()).isEqualTo(CommunicationChannelType.EMAIL_ADDRESS);
        assertThat(comm.getSubject()).isNotNull();
        assertThat(comm.getSentAt()).isNull();

        final List<CommunicationChannel> correspondentChannels =
                Lists.newArrayList(comm.getCorrespondents()).stream()
                        .map(CommChannelRole::getChannel)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
        assertThat(correspondentChannels).contains(fredEmail);

        List<EmailMessage> emailMessages = fakeEmailService.listSentEmails();
        assertThat(emailMessages).isEmpty();

        List<CommandJdo> commands = backgroundCommandRepository.findBackgroundCommandsNotYetStarted();
        assertThat(commands.size()).isEqualTo(1);

        // when
        fakeScheduler.runBackgroundCommands(5000);

        // then
        assertThat(comm.getState()).isEqualTo(CommunicationState.SENT);
        assertThat(comm.getSentAt()).isNotNull();

        emailMessages = fakeEmailService.listSentEmails();
        assertThat(emailMessages).isNotEmpty();
    }

    @Test
    public void can_create_postal_address() throws Exception {

        // given
        fixtureScripts.runFixtureScript(new DemoObjectWithNotes_and_DemoInvoice_and_docs_and_comms_recreate(), null);
        transactionService.nextTransaction();

        // and so given customer with an email
        final DemoObjectWithNotes mary = customerMenu.findDemoObjectsWithNotesByName(
                DemoObjectWithNote_and_DemoInvoice_create3.MARY_HAS_PHONE_AND_POST).get(0);

        final PostalAddress maryPost = (PostalAddress) linkRepository
                .findByOwnerAndCommunicationChannelType(mary, CommunicationChannelType.POSTAL_ADDRESS)
                .get(0)
                .getCommunicationChannel();

        // and with an invoice
        final DemoInvoice fredInvoice = invoiceRepository.findByCustomer(mary).get(0);

        // that has an attached document
        final Paperclip paperclip = paperclipRepository.findByAttachedTo(fredInvoice).get(0);
        final DocumentAbstract document = paperclip.getDocument();

        // when
        final Document_sendByPost documentPrint = mixin(Document_sendByPost.class, document);
        final Set<PostalAddress> postalAddresses = documentPrint.choices0Act();

        // then
        assertThat(postalAddresses).contains(maryPost);

        // and when
        final Communication comm = wrap(documentPrint).act(maryPost);

        // then
        assertThat(comm).isNotNull();

        assertThat(comm.getState()).isEqualTo(CommunicationState.PENDING);
        assertThat(comm.getCreatedAt()).isNotNull();    // same as emails
        assertThat(comm.getType()).isEqualTo(CommunicationChannelType.POSTAL_ADDRESS);
        assertThat(comm.getSubject()).isNotNull();
        assertThat(comm.getSentAt()).isNull();

        final List<CommunicationChannel> correspondentChannels =
                Lists.newArrayList(comm.getCorrespondents()).stream()
                        .map(CommChannelRole::getChannel)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
        assertThat(correspondentChannels).contains(maryPost);

        // when
        final Communication_downloadPdfForPosting mixin =
                mixin(Communication_downloadPdfForPosting.class, comm);
        wrap(mixin).act(mixin.default0Act());

        // then
        assertThat(comm.getState()).isEqualTo(CommunicationState.SENT);
        assertThat(comm.getSentAt()).isNotNull();
    }



    @Inject
    FakeEmailService fakeEmailService;

    @Inject
    BackgroundCommandServiceJdoRepository backgroundCommandRepository;

}

