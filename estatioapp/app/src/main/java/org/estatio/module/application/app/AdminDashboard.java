package org.estatio.module.application.app;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.Collection;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.ViewModel;
import org.apache.isis.applib.services.background.BackgroundService2;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.config.ConfigurationProperty;
import org.apache.isis.applib.services.config.ConfigurationService;
import org.apache.isis.applib.services.email.EmailService;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Markup;
import org.apache.isis.core.metamodel.services.configinternal.ConfigurationServiceInternal;
import org.apache.isis.core.metamodel.specloader.ServiceInitializer;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.publishmq.dom.servicespi.PublisherServiceUsingActiveMq;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.user.AccountType;
import org.isisaddons.module.security.dom.user.ApplicationUserRepository;
import org.isisaddons.module.security.dom.user.ApplicationUserStatus;
import org.isisaddons.module.servletapi.dom.HttpSessionProvider;
import org.isisaddons.module.stringinterpolator.dom.StringInterpolatorService;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.slack.impl.SlackService;

import org.estatio.module.application.app.dashboard.TenantReferenceMappingLine;
import org.estatio.module.application.contributions.Organisation_syncToCoda;
import org.estatio.module.application.exports.ActiveDelegatedUserExportLine;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.capex.app.taskreminder.TaskReminderService;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.coda.EstatioCodaModule;
import org.estatio.module.coda.app.CodaCmpCodeService;
import org.estatio.module.coda.app.CodaDocCodeService;
import org.estatio.module.coda.dom.doc.CodaDocHeadMenu;
import org.estatio.module.coda.dom.hwm.CodaHwm;
import org.estatio.module.coda.dom.hwm.CodaHwmRepository;
import org.estatio.module.countryapptenancy.dom.CountryServiceForCurrentUser;
import org.estatio.module.countryapptenancy.dom.EstatioApplicationTenancyRepositoryForCountry;
import org.estatio.module.invoice.dom.InvoiceItem;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemRepository;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemForDiscount;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemType;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentRepository;
import org.estatio.module.lease.dom.amendments.LeaseAmendmentState;
import org.estatio.module.lease.dom.amendments.Lease_amendments;
import org.estatio.module.lease.dom.amendments.Lease_closeOldAndOpenNewLeaseItem;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceForLeaseRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLease;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.settings.LeaseInvoicingSettingsService;
import org.estatio.module.lease.imports.InvoiceImportLine;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.OrganisationRepository;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonRepository;
import org.estatio.module.party.dom.role.IPartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;
import org.estatio.module.settings.dom.ApplicationSettingForEstatio;
import org.estatio.module.settings.dom.ApplicationSettingsServiceRW;
import org.estatio.module.task.dom.state.StateTransitionService;
import org.estatio.module.task.dom.task.Task;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.Type;
import org.estatio.module.turnoveraggregate.contributions.Lease_aggregateTurnovers;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        // WORKAROUND: using fqcn as objectType because Isis' invalidation of cache in prototyping mode causing NPEs in some situations
        objectType = "org.estatio.module.application.app.AdminDashboard"
)
public class AdminDashboard implements ViewModel {

    public static Logger LOG = LoggerFactory.getLogger(AdminDashboard.class);

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
    public void aggregateAllTurnovers(@Nullable final LocalDate startDate, @Nullable final LocalDate endDate, final boolean maintainOnly){
        final List<Lease> leaseSelection = turnoverReportingConfigRepository.listAll().stream()
                .filter(c -> c.getType() == Type.PRELIMINARY && c.getFrequency() == Frequency.MONTHLY)
                .map(c -> c.getOccupancy().getLease())
                .filter(l->l.getNext()==null || noConfigFor((Lease) l.getNext()))
                .filter(l -> l.getEffectiveInterval().overlaps(LocalDateInterval.including(startDate, null)))
                .collect(Collectors.toList());
        leaseSelection.forEach(l->{
            try {
                backgroundService2.executeMixin(Lease_aggregateTurnovers.class, l).$$(startDate, endDate, maintainOnly);
            } catch (Exception e){
                LOG.warn(String.format("Problem with aggregation for lease %s", l.getReference()));
                LOG.warn(e.getMessage());
            }
        });
    }

    private boolean noConfigFor(final Lease next) {
        final List<TurnoverReportingConfig> configs = new ArrayList<>();
        Lists.newArrayList(next.getOccupancies()).forEach(o->{
            final List<TurnoverReportingConfig> byOccupancyAndType = turnoverReportingConfigRepository
                    .findByOccupancyAndType(o, Type.PRELIMINARY);
            if (!byOccupancyAndType.isEmpty()){
                configs.addAll(byOccupancyAndType);
            }
        });
        return configs.isEmpty();
    }

    public void sendApprovalRemindersItaly(@Nullable final Person approver){
        if (approver==null) {
            taskReminderService.sendRemindersToAllItalianApprovers();
        } else {
            final List<Task> taskList = taskReminderService.findIncompleteItalianApprovalTasks().stream()
                    .filter(t->t.getPersonAssignedTo()!=null)
                    .filter(t -> t.getPersonAssignedTo().equals(approver))
                    .collect(Collectors.toList());
            taskReminderService.sendReminder(approver, taskList);
        }
    }

    public List<Person> choices0SendApprovalRemindersItaly(){
        return personRepository.allPersons();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-wrench")
    @MemberOrder(sequence = "98")
    public MissingChamberOfCommerceCodeManager fixMissingChamberOfCommerceCodes(
            final Country country,
            final IPartyRoleType role,
            final @ParameterLayout(named = "Start from bottom?") boolean reversed) {
        final ApplicationTenancy applicationTenancy = estatioApplicationTenancyRepository.findOrCreateTenancyFor(country);
        List<Organisation> organisationsMissingCode = organisationRepository.findByAtPathMissingChamberOfCommerceCode(applicationTenancy.getPath())
                .stream()
                .filter(org -> org.hasPartyRoleType(role))
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(), lst -> {
                            if (reversed)
                                Collections.reverse(lst);
                            return lst;
                        }
                ));

        return new MissingChamberOfCommerceCodeManager(organisationsMissingCode);
    }

    public List<Country> choices0FixMissingChamberOfCommerceCodes() {
        return countryServiceForCurrentUser.countriesForCurrentUser();
    }

    public List<PartyRoleType> choices1FixMissingChamberOfCommerceCodes() {
        return Arrays.asList(
                partyRoleTypeRepository.findByKey(LeaseAgreementRoleTypeEnum.TENANT.getKey()),
                partyRoleTypeRepository.findByKey(IncomingInvoiceRoleTypeEnum.SUPPLIER.getKey())
        );
    }

    @Action(restrictTo = RestrictTo.PROTOTYPING)
    public void fixUpTransitionsForAllIncomingInvoices() {

        final List<IncomingInvoice> incomingInvoices = incomingInvoiceRepository.listAll();
        for (IncomingInvoice incomingInvoice : incomingInvoices) {
            stateTransitionService.trigger(incomingInvoice, IncomingInvoiceApprovalStateTransition.class, null, null, null);
        }

    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public void closeOldAndOpenNewLeaseItem(@Nullable final org.estatio.module.asset.dom.Property property, final LocalDate startDate, final boolean removeinvoicesOldItem, @Nullable final String excludeAtPathContains){
        List<Lease> leases;
        if (property!=null) {
            leases = leaseRepository.findLeasesByProperty(property);

        } else {
            leases = leaseRepository.allLeases().stream()
                    .filter(l->l.getAtPath().startsWith("/ITA"))
                    .filter(l->l.getEffectiveInterval()!=null)
                    .filter(l->l.getEffectiveInterval().contains(startDate))
                    .collect(Collectors.toList());
        }
        if (excludeAtPathContains!=null){
            leases = leases.stream().filter(l->!l.getAtPath().contains(excludeAtPathContains)).collect(Collectors.toList());
        }
        leases.forEach(l -> {
            factoryService.mixin(Lease_closeOldAndOpenNewLeaseItem.class, l)
                    .act(startDate, LeaseItemType.RENT, InvoicingFrequency.QUARTERLY_IN_ADVANCE, InvoicingFrequency.MONTHLY_IN_ADVANCE,
                            removeinvoicesOldItem);
            factoryService.mixin(Lease_closeOldAndOpenNewLeaseItem.class, l)
                    .act(startDate, LeaseItemType.SERVICE_CHARGE, InvoicingFrequency.QUARTERLY_IN_ADVANCE, InvoicingFrequency.MONTHLY_IN_ADVANCE,
                            removeinvoicesOldItem);
            factoryService.mixin(Lease_closeOldAndOpenNewLeaseItem.class, l)
                    .act(startDate, LeaseItemType.SERVICE_CHARGE_INDEXABLE, InvoicingFrequency.QUARTERLY_IN_ADVANCE, InvoicingFrequency.MONTHLY_IN_ADVANCE,
                            removeinvoicesOldItem);
        });
    }

    public void recreateLeasePreviewsForAllLeaseAmendmentsNotApplied(){
        leaseAmendmentRepository.listAll().stream().filter(la->la.getState()!=LeaseAmendmentState.APPLIED).forEach(la->{
            backgroundService2.execute(la).createOrRenewLeasePreview();
        });
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public void recalculateAmortisationEndDateOnAllAmendmentItemsForDiscount(){
        leaseAmendmentRepository.listAll().forEach(la->{
            la.findItemsOfType(LeaseAmendmentItemType.DISCOUNT).forEach(lai->{
                LeaseAmendmentItemForDiscount castedItem = (LeaseAmendmentItemForDiscount) lai;
                castedItem.reCalculateAmortisationEndDate();
            });
        });
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public void assignUUIDToAllIncomingInvoicesThatHaveNone(){
        incomingInvoiceRepository.listAll().forEach(i->{
            if (i.getUuid()==null){
                i.setUuid(UUID.randomUUID().toString());
            }
        });
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public void linkManualInvoicesToTerms(final org.estatio.module.asset.dom.Property property, final LeaseItemType leaseItemType, final Charge charge){
        List<LeaseItem> itemsForPropertyTypeAndCharge = new ArrayList<>();
        leaseRepository.findLeasesByProperty(property).forEach(l->{
            final List<LeaseItem> leaseItemsByTypeAndChargedInvoicedByLandlord = leaseItemRepository.findLeaseItemsByType(l, leaseItemType)
                    .stream()
                    .filter(li->li.getInvoicedBy()==LeaseAgreementRoleTypeEnum.LANDLORD)
                    .filter(li->li.getCharge().equals(charge))
                    .collect(Collectors.toList());
            itemsForPropertyTypeAndCharge.addAll(leaseItemsByTypeAndChargedInvoicedByLandlord);
        });

        List<InvoiceItemForLease> unlinkedInvoiceItem = findUnlinkedInvoiceItemsForPropertyAndCharge(property, charge);

        for (LeaseItem leaseItem : itemsForPropertyTypeAndCharge){
            for (LeaseTerm lt : leaseItem.getTerms()){
                unlinkedInvoiceItem.stream()
                        .filter(ii->ii.getLease()==leaseItem.getLease())
                        .filter(ii->lt.getInterval().contains(ii.getDueDate()))
                        .forEach(ii->{
                            LOG.info(String.format("Linking item for invoice %s to term %s of item of type %s of lease %s",ii.getInvoice().getInvoiceNumber(), lt.getSequence(), lt.getLeaseItem().getType(), lt.getLeaseItem().getLease().getReference()));
                            ii.setLeaseTerm(lt);
                        });

            }
        }
    }

    public List<LeaseItemType> choices1LinkManualInvoicesToTerms(){
        return Arrays.asList(LeaseItemType.PROPERTY_TAX, LeaseItemType.OFFICE_TAX, LeaseItemType.RETAIL_TAX);
    }

    public List<Charge> choices2LinkManualInvoicesToTerms(){
        List<Charge> result = new ArrayList<>();
        // property tax all except ..
        result.add(chargeRepository.findByReference("FR4560"));
        // prop tax SL
        result.add(chargeRepository.findByReference("FR4561"));

        // office tax all except
        result.add(chargeRepository.findByReference("FR4580"));
        // prop tax SL
        result.add(chargeRepository.findByReference("FR4583"));

        // retail tax
        result.add(chargeRepository.findByReference("FR4570"));
        return result;
    }

    private List<InvoiceItemForLease> findUnlinkedInvoiceItemsForPropertyAndCharge(final org.estatio.module.asset.dom.Property property, final Charge charge){
        final List<InvoiceForLease> invoices = invoiceForLeaseRepository
                .findByFixedAssetAndStatus(property, InvoiceStatus.INVOICED);
        List<InvoiceItemForLease> result = new ArrayList<>();
        for (InvoiceForLease invoice : invoices){
            for (InvoiceItem invoiceItem : invoice.getItems()){
                InvoiceItemForLease castedItem = (InvoiceItemForLease) invoiceItem;
                if (castedItem.getLeaseTerm()==null && castedItem.getCharge()==charge){
                    result.add(castedItem);
                }
            }
        }
        return result;
    }


    @Action(semantics = SemanticsOf.SAFE)
    public Blob createInvoiceImportSheetForPropertyAndCharge(final org.estatio.module.asset.dom.Property property, final Charge charge){
        List<InvoiceImportLine> result = new ArrayList<>();
        leaseRepository.findLeasesByProperty(property).stream()
                .filter(l->!factoryService.mixin(Lease_amendments.class,l).$$().isEmpty())
                .forEach(l->{
                    InvoiceImportLine line = new InvoiceImportLine();
                    line.setLeaseReference(l.getReference());
                    line.setItemChargeReference(charge.getReference());
                    final Occupancy occupancy = l.primaryOccupancy().orElse(null);
                    if (occupancy!=null && occupancy.getUnit()!=null){
                        line.setUnitReference(occupancy.getUnit().getReference());
                    }
                    result.add(line);
                });
        return excelService.toExcel(result, InvoiceImportLine.class, "InvoiceForLease", property.getReference() + "-invoice-import.xlsx");
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public void importInvoiceImportSheet(final Blob sheet){
        excelService.fromExcel(sheet, InvoiceImportLine.class, "InvoiceForLease").forEach(
                l->{
                    final List<Object> invoices = l.importData();
                    invoices.forEach(o->{
                        if (l.getInvoiceNumber()!=null) {
                            InvoiceForLease invoice = (InvoiceForLease) o;
                            invoice.setStatus(InvoiceStatus.INVOICED);
                            invoice.setInvoiceNumber(l.getInvoiceNumber());
                        }
                    });
                }
        );
    }


    @Action(semantics = SemanticsOf.SAFE)
    public Blob downloadActiveDelegatedUsers() {
        List<ActiveDelegatedUserExportLine> exportLines = new ArrayList<>();
        applicationUserRepository.allUsers().stream()
                .filter(user ->
                        user.getAccountType() == AccountType.DELEGATED &&
                                user.getStatus() == ApplicationUserStatus.ENABLED)
                .forEach(l -> {
                    ActiveDelegatedUserExportLine line = new ActiveDelegatedUserExportLine();
                    line.setUsername(l.getUsername());
                    line.setStatus(l.getStatus().toString());
                    line.setAtPath(l.getAtPath());
                    line.setFamilyName(l.getFamilyName());
                    line.setGivenName(l.getGivenName());
                    Person person = personRepository.findByUsername(l.getUsername());
                    if (person != null) {
                        line.setPersonRef(person.getReference());
                        List<String> roles = person.getRoles().stream().map(role -> role.getRoleType().getTitle())
                                .distinct().collect(Collectors.toList());
                        line.setPartyRoles(StringUtils.join(roles, ", "));
                    }
                    exportLines.add(line);
                });

        return excelService.toExcel(exportLines, ActiveDelegatedUserExportLine.class, "ActiveDelegatedUsers",
                String.format("AD-users-Estatio-per-%s.xlsx", LocalDate.now().toString()));
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public void uploadTenantReferenceMappingWo(final Blob sheet){

        excelService.fromExcel(sheet, TenantReferenceMappingLine.class, "Sheet1").forEach(
                l->{
                    l.importData();
                }
        );

    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public List<LeaseItem> adaptServiceChargeLeaseItems(final List<org.estatio.module.asset.dom.Property> properties, final LocalDate date){
        List<LeaseItem> newServiceCharges = new ArrayList<>();
        properties.stream().forEach(property -> {
            leaseRepository.findByAssetAndActiveOnDateIncludingPreviews(property, date).stream().forEach(lease -> {
                List<LeaseItem> leaseItems = new ArrayList<>(lease.getItems());
                List<LeaseItem> rentItems = leaseItems.stream().filter(li -> li.getType() == LeaseItemType.RENT && li.getInterval().contains(date)).collect(Collectors.toList());
                if (!rentItems.isEmpty()) {
                    LeaseItem rent = rentItems.get(0);
                    leaseItems.stream()
                            .filter(li -> li.getType() == LeaseItemType.SERVICE_CHARGE
                                    && li.getInvoicedBy() == LeaseAgreementRoleTypeEnum.MANAGER
                                    && li.getInterval().contains(date))
                            .forEach(li -> {
                                li.verifyUntil(date.plusYears(1));
                                newServiceCharges.add(li.copy(date, rent.getInvoicingFrequency(), rent.getPaymentMethod(), li.getCharge()));
                                LOG.info(String.format("Adapting service charge item with new invoicing frequency %s, payment method %s and charge %s on lease %s", rent.getInvoicingFrequency(), rent.getPaymentMethod(), li.getCharge().getReference(), lease.getReference()));
                            });
                } else {
                    LOG.info(String.format("No rent item found for lease %s; could not adapt service charge item", lease.getReference()));
                }
            });
        });

        return newServiceCharges;
    }

    @Inject PropertyRepository propertyRepository;

    @Inject LeaseItemRepository leaseItemRepository;

    @Inject InvoiceForLeaseRepository invoiceForLeaseRepository;

    @Inject
    ChargeRepository chargeRepository;

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

    @Inject
    BackgroundService2 backgroundService2;

    @Inject TurnoverReportingConfigRepository turnoverReportingConfigRepository;

    @Inject
    TaskReminderService taskReminderService;

    @Inject
    PersonRepository personRepository;

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    OrganisationRepository organisationRepository;

    @Inject
    CountryServiceForCurrentUser countryServiceForCurrentUser;

    @Inject
    EstatioApplicationTenancyRepositoryForCountry estatioApplicationTenancyRepository;

    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;

    @Inject
    StateTransitionService stateTransitionService;

    @Inject
    LeaseAmendmentRepository leaseAmendmentRepository;

    @Inject
    ExcelService excelService;

    @Inject
    ApplicationUserRepository applicationUserRepository;

}
