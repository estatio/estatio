package org.estatio.module.application.app;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.ViewModel;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.config.ConfigurationProperty;
import org.apache.isis.applib.services.config.ConfigurationService;
import org.apache.isis.applib.services.email.EmailService;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;
import org.apache.isis.applib.value.Markup;
import org.apache.isis.core.metamodel.services.configinternal.ConfigurationServiceInternal;
import org.apache.isis.core.metamodel.specloader.ServiceInitializer;

import org.isisaddons.module.publishmq.dom.servicespi.PublisherServiceUsingActiveMq;
import org.isisaddons.module.servletapi.dom.HttpSessionProvider;
import org.isisaddons.module.stringinterpolator.dom.StringInterpolatorService;

import org.incode.module.slack.impl.SlackService;

import org.estatio.module.application.contributions.Organisation_syncToCoda;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.coda.EstatioCodaModule;
import org.estatio.module.coda.dom.doc.CodaDocHeadMenu;
import org.estatio.module.coda.dom.hwm.CodaHwm;
import org.estatio.module.coda.dom.hwm.CodaHwmRepository;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.settings.LeaseInvoicingSettingsService;
import org.estatio.module.settings.dom.ApplicationSettingForEstatio;
import org.estatio.module.settings.dom.ApplicationSettingsServiceRW;
import org.estatio.module.turnoveraggregate.contributions.Lease_aggregateTurnovers;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        // WORKAROUND: using fqcn as objectType because Isis' invalidation of cache in prototyping mode causing NPEs in some situations
        objectType = "org.estatio.module.application.app.AdminDashboard"
)
public class AdminDashboard implements ViewModel {

    public static final String KEY_ESTATIO_MOTD = "estatio.motd";
    public static final String KEY_MINIO_ARCHIVE_FOR_CALLER = "docBlobServer.caller";

    public String title() {
        return "Admin Dashboard";
    }

    @Override
    public String viewModelMemento() {
        return "1";
    }

    @Override
    public void viewModelInit(final String s) {
    }

    public enum DocBlobArchiveCaller {
        CAMEL("camel"),
        BATCH("batch"),
        CAMEL_AND_BATCH("camel,batch"),
        NONE("none");

        private final String title;

        DocBlobArchiveCaller(final String title) {
            this.title = title;
        }

        public String title() {
            return title;
        }

        public static DocBlobArchiveCaller lookup(final String title) {
            final DocBlobArchiveCaller[] values = values();
            for (final DocBlobArchiveCaller value : values) {
                if (value.title().equalsIgnoreCase(title)) {
                    return value;
                }
            }
            return null;
        }

    }

    @Property(
            editing = Editing.ENABLED,
            optionality = Optionality.OPTIONAL
    )
    public DocBlobArchiveCaller getDocBlobServerCaller() {
        ApplicationSettingForEstatio applicationSetting =
                (ApplicationSettingForEstatio) applicationSettingsServiceRW.find(KEY_MINIO_ARCHIVE_FOR_CALLER);
        return applicationSetting != null
                ? DocBlobArchiveCaller.lookup(applicationSetting.valueAsString())
                : null;
    }

    public void setDocBlobServerCaller(DocBlobArchiveCaller caller) {
        ApplicationSettingForEstatio applicationSetting =
                (ApplicationSettingForEstatio) applicationSettingsServiceRW.find(KEY_MINIO_ARCHIVE_FOR_CALLER);
        if (applicationSetting != null) {
            if (caller != null) {
                applicationSetting.setValueRaw(caller.title());
            } else {
                applicationSettingsServiceRW.delete(applicationSetting);
            }
        } else {
            if (caller != null) {
                applicationSettingsServiceRW.newString(KEY_MINIO_ARCHIVE_FOR_CALLER, "Which caller(s) to enable the archive of document blobs to Minio", caller.title());
            }
        }
    }

    @Property(
            editing = Editing.ENABLED,
            optionality = Optionality.OPTIONAL,
            hidden = Where.EVERYWHERE // for now...
    )
    public String getMotd() {
        ApplicationSettingForEstatio applicationSetting =
                (ApplicationSettingForEstatio) applicationSettingsServiceRW.find(KEY_ESTATIO_MOTD);
        return applicationSetting != null ? applicationSetting.valueAsString() : null;
    }

    public void setMotd(String motd) {
        ApplicationSettingForEstatio applicationSetting =
                (ApplicationSettingForEstatio) applicationSettingsServiceRW.find(KEY_ESTATIO_MOTD);
        if (applicationSetting != null) {
            if (motd != null) {
                applicationSetting.setValueRaw(motd);
            } else {
                applicationSettingsServiceRW.delete(applicationSetting);
            }
        } else {
            if (motd != null) {
                applicationSettingsServiceRW.newString(KEY_ESTATIO_MOTD, "Message of the Day", motd);
            }
        }
    }

    @Property(editing = Editing.DISABLED, optionality = Optionality.OPTIONAL)
    public LocalDate getEpochDate() {
        return settingsService.fetchEpochDate();
    }

    public void setEpochDate(final LocalDate epochDate) {
        settingsService.updateEpochDate(epochDate);
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public AdminDashboard updateEpochDate(final LocalDate epochDate) {
        setEpochDate(epochDate);
        return this;
    }

    public LocalDate default0UpdateEpochDate() {
        return getEpochDate();
    }

    @Property()
    @MemberOrder(sequence = "3")
    public Integer getHttpSessionTimeout() {
        return httpSessionProvider.getHttpSession().map(HttpSession::getMaxInactiveInterval).orElse(null);
    }

    @Collection()
    @MemberOrder(sequence = "2")
    public List<ApplicationSettingForEstatio> getApplicationSettings() {
        return applicationSettingsServiceRW.listAll()
                .stream()
                .filter(ApplicationSettingForEstatio.class::isInstance)
                .map(ApplicationSettingForEstatio.class::cast)
                .collect(Collectors.toList());
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public AdminDashboard retrieveCodaSupplier(
            final String supplierReference) {
        return this;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT, publishing = Publishing.DISABLED)
    public AdminDashboard retrieveCodaDoc(
            final String cmpCode,
            final String docCode,
            final int docNum) {
        wrapperFactory.wrapSkipRules(codaDocHeadMenu).retrieveCodaDoc(cmpCode, docCode, docNum);
        return this;
    }

    public List<String> choices0RetrieveCodaDoc() {
        return codaCmpCodeService.listAll();
    }

    public List<String> choices1RetrieveCodaDoc() {
        return codaDocCodeService.listAll();
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public AdminDashboard syncAllCurrentCodaDocs() {
        return this;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public AdminDashboard syncAllSuppliers() {
        return this;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public AdminDashboard syncAllFrenchAndBelgianSuppliersToCoda() {
        final List<IncomingInvoice> invoicesToSync = incomingInvoiceRepository.findByAtPathPrefixesAndApprovalState(IncomingInvoiceRepository.AT_PATHS_FRA_OFFICE, IncomingInvoiceApprovalState.COMPLETED);
        invoicesToSync.addAll(incomingInvoiceRepository.findByAtPathPrefixesAndApprovalState(IncomingInvoiceRepository.AT_PATHS_FRA_OFFICE, IncomingInvoiceApprovalState.APPROVED));
        invoicesToSync.addAll(incomingInvoiceRepository.findByAtPathPrefixesAndApprovalState(IncomingInvoiceRepository.AT_PATHS_FRA_OFFICE, IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR));
        invoicesToSync.addAll(incomingInvoiceRepository.findByAtPathPrefixesAndApprovalState(IncomingInvoiceRepository.AT_PATHS_FRA_OFFICE, IncomingInvoiceApprovalState.APPROVED_BY_CORPORATE_MANAGER));
        invoicesToSync.addAll(incomingInvoiceRepository.findByAtPathPrefixesAndApprovalState(IncomingInvoiceRepository.AT_PATHS_FRA_OFFICE, IncomingInvoiceApprovalState.PENDING_BANK_ACCOUNT_CHECK));
        invoicesToSync.addAll(incomingInvoiceRepository.findByAtPathPrefixesAndApprovalState(IncomingInvoiceRepository.AT_PATHS_FRA_OFFICE, IncomingInvoiceApprovalState.PAYABLE));
        invoicesToSync.addAll(incomingInvoiceRepository.findByAtPathPrefixesAndApprovalState(IncomingInvoiceRepository.AT_PATHS_FRA_OFFICE, IncomingInvoiceApprovalState.PAID));

        invoicesToSync.stream()
                .map(IncomingInvoice::getSeller)
                .distinct()
                .forEach(organisation -> wrapperFactory.wrap(factoryService.mixin(Organisation_syncToCoda.class, organisation)).act());

        return this;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "3.1")
    public AdminDashboard testEmail(
            @ParameterLayout(named = "To") final String to,
            @ParameterLayout(named = "Subject") final String subject,
            @ParameterLayout(
                    named = "Body",
                    multiLine = 5
            ) final String body) {
        final List<String> toList = Collections.singletonList(to);
        final List<String> ccList = Collections.emptyList();
        final List<String> bccList = Collections.emptyList();
        emailService.send(toList, ccList, bccList, subject, body);
        return this;
    }

    public String disableTestEmail() {
        if (emailService == null) {
            return "No EmailService defined";
        }
        if (!emailService.isConfigured()) {
            return "EmailService is not configured";
        }
        return null;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "3.2")
    public AdminDashboard testSlack(
            String channel,
            String message) {
        slackService.sendMessage(channel, message);
        return this;
    }

    public String disableTestSlack() {
        if (slackService == null) {
            return "No SlackService defined";
        }
        if (!slackService.isConfigured()) {
            return "SlackService is not configured";
        }
        return null;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    @MemberOrder(sequence = "3.4")
    public AdminDashboard patchDatabase(
            @ParameterLayout(multiLine = 10)
                    String sql) {
        final Integer integer = isisJdoSupport.executeUpdate(sql);
        messageService.informUser("executeUpdate(sql) returned: " + integer);
        return this;
    }

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "3.5")
    public void raiseRuntimeException() {
        throw new RuntimeException();
    }

    public enum Level {
        INFO {
            @Override
            public void raise(final MessageService messageService, final String message) {
                messageService.informUser(message);
            }
        },
        WARN {
            @Override
            public void raise(final MessageService messageService, final String message) {
                messageService.warnUser(message);
            }
        },
        ERROR {
            @Override
            public void raise(final MessageService messageService, final String message) {
                messageService.raiseError(message);
            }
        };

        public abstract void raise(final MessageService messageService, final String message);
    }

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "3.5")
    public AdminDashboard testMessageService(final Level level, final String message) {
        level.raise(messageService, message);
        return this;
    }

    public Level default0TestMessageService() {
        return Level.INFO;
    }

    public String default1TestMessageService() {
        return "test message";
    }

    @Collection
    @CollectionLayout(defaultView = "table")
    public Set<ConfigurationProperty> getConnections() {
        return configurationService.allProperties().stream()
                .filter(this::match)
                .collect(Collectors.toSet());
    }

    private boolean match(final ConfigurationProperty prop) {
        final List<String> props = Arrays.asList(
                "estatio.application.cmisServerDefaultRepoBaseUrl",
                "estatio.application.reportServerBaseUrl",
                "estatio.datawarehouse.ConnectionURL",
                "incode.module.docrendering.stringinterpolator.UrlDownloaderUsingNtlmCredentials.host",
                "isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionURL",
                "isis.service.email.sender.hostname",
                "isis.services.PublisherServiceUsingActiveMq.vmTransportUri");
        return props.contains(prop.getKey());
    }

    public Markup getKibanaLog() {
        return new Markup(stringInterpolatorService.interpolate(
                this, "<iframe src=\"${properties['estatio.application.kibanaEmbeddedLogUrl']}?embed=true\" height=\"${this.kibanaLogHeight}\" width=\"${this.kibanaLogWidth}\"></iframe>"));
    }

    public boolean hideKibanaLog() {
        return interpolateOpenLogUrl() == null;
    }

    @Getter @Setter
    @Property(hidden = Where.EVERYWHERE)
    private int kibanaLogWidth = 1400;
    @Getter @Setter
    @Property(hidden = Where.EVERYWHERE)
    private int kibanaLogHeight = 800;

    public AdminDashboard updateKibanaLogDimensions(
            int width,
            int height
    ) {
        this.setKibanaLogWidth(width);
        this.setKibanaLogHeight(height);
        return this;
    }

    public boolean hideUpdateKibanaLogDimensions() {
        return interpolateOpenLogUrl() == null;
    }

    public int default0UpdateKibanaLogDimensions() {
        return getKibanaLogWidth();
    }

    public int default1UpdateKibanaLogDimensions() {
        return getKibanaLogHeight();
    }

    @Action(semantics = SemanticsOf.SAFE)
    public URL openKibanaLog() throws MalformedURLException {
        return interpolateOpenLogUrl();
    }

    public boolean hideOpenKibanaLog() {
        return interpolateOpenLogUrl() == null;
    }

    private URL interpolateOpenLogUrl() {
        try {
            return new URL(stringInterpolatorService.interpolate(this, "${properties['estatio.application.kibanaOpenLogUrl']}"));
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Collection
    @CollectionLayout(defaultView = "table")
    public List<CodaHwm> getCodaHwms() {
        return codaHwmRepository.listAll();
    }

    @Action(
            semantics = SemanticsOf.NON_IDEMPOTENT,
            associateWith = "codaHwms",
            associateWithSequence = "1"
    )
    @ActionLayout(named = "Create")
    public AdminDashboard createCodaHwm(
            final EstatioCodaModule.FeedName feedName,
            final String cmpCode,
            final LocalDateTime lastRan) {
        CodaHwm codaHwm = codaHwmRepository.findByFeedNameAndCmpCode(feedName.name(), cmpCode);
        if (codaHwm != null) {
            messageService.warnUser("That feed already exists");
            return this;
        }
        codaHwm = codaHwmRepository.findOrCreate(feedName.name(), cmpCode);
        codaHwm.setLastRan(lastRan);
        return this;
    }

    public List<String> choices1CreateCodaHwm() {
        return codaCmpCodeService.listAll();
    }

    public LocalDateTime default2CreateCodaHwm() {
        return clockService.nowAsLocalDateTime();
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            associateWith = "codaHwms",
            associateWithSequence = "2"
    )
    @ActionLayout(named = "Update")
    public AdminDashboard updateCodaHwm(
            final CodaHwm codaHwm,
            final LocalDateTime lastRan) {
        codaHwm.setLastRan(lastRan);
        return this;
    }

    public String disableUpdateCodaHwm() {
        return getCodaHwms().isEmpty() ? "No HWMs to update" : null;
    }

    public List<CodaHwm> choices0UpdateCodaHwm() {
        return getCodaHwms();
    }
    // not yet supported...
    //public LocalDateTime default1UpdateCodaHwm(CodaHwm codaHwm) {
    //    return codaHwm.getLastRan();
    //}

    /**
     * Allows Camel to be restarted without having to restart the application.
     */
    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public void reinitPublisherService() {

        final ServiceInitializer serviceInitializer =
                new ServiceInitializer(
                        isisConfiguration, Arrays.asList(publisherServiceUsingActiveMq));
        serviceInitializer.validate();
        serviceInitializer.preDestroy();
        serviceInitializer.postConstruct();
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public void aggregateAllTurnovers(){
        leaseRepository.allLeases().forEach(l->{
            final Lease_aggregateTurnovers mixin = factoryService.mixin(Lease_aggregateTurnovers.class, l);
            wrapperFactory.wrap(mixin).$$();
        });
    }

    @Inject LeaseRepository leaseRepository;

    @Inject
    CodaCmpCodeService codaCmpCodeService;

    @Inject
    CodaDocCodeService codaDocCodeService;

    @Inject
    ClockService clockService;

    @Inject
    CodaHwmRepository codaHwmRepository;

    @Inject
    PublisherServiceUsingActiveMq publisherServiceUsingActiveMq;

    @Inject
    ConfigurationServiceInternal isisConfiguration;

    @Inject
    ConfigurationService configurationService;

    @Inject
    StringInterpolatorService stringInterpolatorService;

    @Inject
    SlackService slackService;

    @Inject
    EmailService emailService;

    @Inject
    LeaseInvoicingSettingsService settingsService;

    @Inject
    ApplicationSettingsServiceRW applicationSettingsServiceRW;

    @Inject
    HttpSessionProvider httpSessionProvider;

    @Inject
    IsisJdoSupport isisJdoSupport;

    @Inject
    MessageService messageService;

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    WrapperFactory wrapperFactory;

    @Inject
    FactoryService factoryService;

    @Inject
    CodaDocHeadMenu codaDocHeadMenu;

}
