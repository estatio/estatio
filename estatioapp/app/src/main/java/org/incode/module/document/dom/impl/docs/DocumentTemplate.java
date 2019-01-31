package org.incode.module.document.dom.impl.docs;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.JDOHelper;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;

import com.google.common.collect.FluentIterable;
import com.google.common.eventbus.Subscribe;

import org.apache.commons.lang3.StringUtils;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.joda.time.LocalDate;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.background.BackgroundService2;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.services.xactn.TransactionService;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.document.DocumentModule;
import org.incode.module.document.dom.impl.applicability.Applicability;
import org.incode.module.document.dom.impl.applicability.ApplicabilityRepository;
import org.incode.module.document.dom.impl.applicability.AttachmentAdvisor;
import org.incode.module.document.dom.impl.applicability.AttachmentAdvisorAttachToNone;
import org.incode.module.document.dom.impl.applicability.RendererModelFactory;
import org.incode.module.document.dom.impl.renderers.Renderer;
import org.incode.module.document.dom.impl.renderers.RendererFromBytesToBytes;
import org.incode.module.document.dom.impl.renderers.RendererFromBytesToBytesWithPreviewToUrl;
import org.incode.module.document.dom.impl.renderers.RendererFromBytesToChars;
import org.incode.module.document.dom.impl.renderers.RendererFromBytesToCharsWithPreviewToUrl;
import org.incode.module.document.dom.impl.renderers.RendererFromCharsToBytes;
import org.incode.module.document.dom.impl.renderers.RendererFromCharsToBytesWithPreviewToUrl;
import org.incode.module.document.dom.impl.renderers.RendererFromCharsToChars;
import org.incode.module.document.dom.impl.renderers.RendererFromCharsToCharsWithPreviewToUrl;
import org.incode.module.document.dom.impl.rendering.RenderingStrategy;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.services.ClassNameViewModel;
import org.incode.module.document.dom.services.ClassService;
import org.incode.module.document.dom.spi.AttachmentAdvisorClassNameService;
import org.incode.module.document.dom.spi.RendererModelFactoryClassNameService;
import org.incode.module.document.dom.types.AtPathType;
import org.incode.module.document.dom.types.FqcnType;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType= IdentityType.DATASTORE,
        schema = "incodeDocuments"
)
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@Queries({
        @javax.jdo.annotations.Query(
                name = "findByTypeAndAtPath", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.docs.DocumentTemplate "
                        + "WHERE typeCopy   == :type "
                        + "   && atPathCopy == :atPath "
                        + "ORDER BY date DESC"
        ),
        @javax.jdo.annotations.Query(
                name = "findByTypeAndAtPathAndDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.docs.DocumentTemplate "
                        + "WHERE typeCopy   == :type "
                        + "   && atPathCopy == :atPath "
                        + "   && date       == :date "
        ),
        @javax.jdo.annotations.Query(
                name = "findByTypeAndApplicableToAtPath", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.docs.DocumentTemplate "
                        + "WHERE typeCopy   == :type "
                        + "   && :atPath.startsWith(atPathCopy) "
                        + "ORDER BY atPathCopy DESC, date DESC "
        ),
        @javax.jdo.annotations.Query(
                name = "findByApplicableToAtPath", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.docs.DocumentTemplate "
                        + "WHERE :atPath.startsWith(atPathCopy) "
                        + "ORDER BY typeCopy ASC, atPathCopy DESC, date DESC "
        ),
        @javax.jdo.annotations.Query(
                name = "findByTypeAndApplicableToAtPathAndCurrent", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.docs.DocumentTemplate "
                        + "WHERE typeCopy   == :type "
                        + "   && :atPath.startsWith(atPathCopy) "
                        + "   && (date == null || date <= :now) "
                        + "ORDER BY atPathCopy DESC, date DESC "
        ),
        @javax.jdo.annotations.Query(
                name = "findByType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.docs.DocumentTemplate "
                        + "WHERE typeCopy   == :type "
                        + "ORDER BY atPathCopy DESC, date DESC "
        ),
        @javax.jdo.annotations.Query(
                name = "findByApplicableToAtPathAndCurrent", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.docs.DocumentTemplate "
                        + "   && :atPath.startsWith(atPathCopy) "
                        + "   && (date == null || date <= :now) "
                        + "ORDER BY atPathCopy DESC, typeCopy, date DESC "
        )
})
@Uniques({
        @Unique(
                name = "DocumentTemplate_type_atPath_date_IDX",
                members = { "typeCopy", "atPathCopy", "date" }
        ),
})
@Indices({
        @Index(
                name = "DocumentTemplate_atPath_date_IDX",
                members = { "atPathCopy", "date" }
        ),
        @Index(
                name = "DocumentTemplate_type_date_IDX",
                members = { "typeCopy", "date" }
        ),
})
@DomainObject(
        objectType = "incodeDocuments.DocumentTemplate",
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        titleUiEvent = DocumentTemplate.TitleUiEvent.class,
        iconUiEvent = DocumentTemplate.IconUiEvent.class,
        cssClassUiEvent = DocumentTemplate.CssClassUiEvent.class,
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class DocumentTemplate extends DocumentAbstract<DocumentTemplate> {


    //region > ui event classes
    public static class TitleUiEvent extends DocumentModule.TitleUiEvent<DocumentTemplate>{}
    public static class IconUiEvent extends DocumentModule.IconUiEvent<DocumentTemplate>{}
    public static class CssClassUiEvent extends DocumentModule.CssClassUiEvent<DocumentTemplate>{}
    //endregion

    //region > title, icon, cssClass
    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class TitleSubscriber extends AbstractSubscriber {

        public String getId() {
            return "incodeDocuments.DocumentTemplate$TitleSubscriber";
        }

        @EventHandler
        @Subscribe
        public void on(DocumentTemplate.TitleUiEvent ev) {
            if(ev.getTitle() != null) {
                return;
            }
            ev.setTranslatableTitle(titleOf(ev.getSource()));
        }
        private TranslatableString titleOf(final DocumentTemplate template) {
            if(template.getDate() != null) {
                return TranslatableString.tr("[{type}] ({date})",
                        "type", template.getType().getReference(),
                        "date", template.getDate());
            } else {
                return TranslatableString.tr("[{type}] {name}",
                        "name", template.getName(),
                        "type", template.getType().getReference());
            }
        }
        @Inject
        TitleService titleService;
    }

    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class IconSubscriber extends AbstractSubscriber {

        public String getId() {
            return "incodeDocuments.DocumentTemplate$IconSubscriber";
        }

        @EventHandler
        @Subscribe
        public void on(DocumentTemplate.IconUiEvent ev) {
            if(ev.getIconName() != null) {
                return;
            }
            ev.setIconName("");
        }
    }

    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class CssClassSubscriber extends AbstractSubscriber {

        public String getId() {
            return "incodeDocuments.DocumentTemplate$CssClassSubscriber";
        }

        @EventHandler
        @Subscribe
        public void on(DocumentTemplate.CssClassUiEvent ev) {
            if(ev.getCssClass() != null) {
                return;
            }
            ev.setCssClass("");
        }
    }
    //endregion

    //region > constructor
    DocumentTemplate() {
        // for unit testing only
    }

    public DocumentTemplate(
            final DocumentType type,
            final LocalDate date,
            final String atPath,
            final String fileSuffix,
            final boolean previewOnly,
            final Blob blob,
            final RenderingStrategy contentRenderingStrategy,
            final String subjectText,
            final RenderingStrategy subjectRenderingStrategy) {
        super(type, atPath);
        modifyBlob(blob);
        init(type, date, atPath, fileSuffix, previewOnly, contentRenderingStrategy, subjectText, subjectRenderingStrategy);
    }

    public DocumentTemplate(
            final DocumentType type,
            final LocalDate date,
            final String atPath,
            final String fileSuffix,
            final boolean previewOnly,
            final String name, final String mimeType, final String text,
            final RenderingStrategy contentRenderingStrategy,
            final String subjectText,
            final RenderingStrategy subjectRenderingStrategy) {
        super(type, atPath);
        setTextData(name, mimeType, text);
        init(type, date, atPath, fileSuffix, previewOnly, contentRenderingStrategy, subjectText, subjectRenderingStrategy);
    }

    public DocumentTemplate(
            final DocumentType type,
            final LocalDate date,
            final String atPath,
            final String fileSuffix,
            final boolean previewOnly,
            final Clob clob,
            final RenderingStrategy contentRenderingStrategy,
            final String subjectText,
            final RenderingStrategy subjectRenderingStrategy) {
        super(type, atPath);
        modifyClob(clob);
        init(type, date, atPath, fileSuffix, previewOnly, contentRenderingStrategy, subjectText, subjectRenderingStrategy);
    }

    private void init(
            final DocumentType type,
            final LocalDate date,
            final String atPath,
            final String fileSuffix,
            final boolean previewOnly,
            final RenderingStrategy contentRenderingStrategy,
            final String nameText,
            final RenderingStrategy nameRenderingStrategy) {
        this.typeCopy = type;
        this.atPathCopy = atPath;
        this.date = date;
        this.fileSuffix = stripLeadingDotAndLowerCase(fileSuffix);
        this.previewOnly = previewOnly;
        this.contentRenderingStrategy = contentRenderingStrategy;
        this.nameText = nameText;
        this.nameRenderingStrategy = nameRenderingStrategy;
    }

    static String stripLeadingDotAndLowerCase(final String fileSuffix) {
        final int lastDot = fileSuffix.lastIndexOf(".");
        final String stripLeadingDot = fileSuffix.substring(lastDot+1);
        return stripLeadingDot.toLowerCase();
    }

    //endregion


    //region > typeCopy (derived property, persisted)
    /**
     * Copy of {@link #getType()}, for query purposes only.
     */
    @Getter @Setter
    @Column(allowsNull = "false", name = "typeId")
    @Property(
            notPersisted = true, // ignore for auditing
            hidden = Where.EVERYWHERE
    )
    private DocumentType typeCopy;
    //endregion

    //region > atPathCopy (derived property, persisted)
    /**
     * Copy of {@link #getAtPath()}, for query purposes only.
     */
    @Getter @Setter
    @Column(allowsNull = "false", length = AtPathType.Meta.MAX_LEN)
    @Property(
            notPersisted = true, // ignore for auditing
            hidden = Where.EVERYWHERE
    )
    private String atPathCopy;
    //endregion

    //region > date (property)
    public static class DateDomainEvent extends DocumentTemplate.PropertyDomainEvent<LocalDate> { }
    @Getter @Setter
    @Column(allowsNull = "false")
    @Property(
            domainEvent = DateDomainEvent.class,
            editing = Editing.DISABLED
    )
    private LocalDate date;
    //endregion


    //region > contentRenderingStrategy (property)
    public static class RenderingStrategyDomainEvent extends PropertyDomainEvent<RenderingStrategy> { }
    @Getter @Setter
    @Column(allowsNull = "false", name = "contentRenderStrategyId")
    @Property(
            domainEvent = RenderingStrategyDomainEvent.class,
            editing = Editing.DISABLED
    )
    private RenderingStrategy contentRenderingStrategy;
    //endregion

    //region > fileSuffix (property)
    public static class FileSuffixDomainEvent extends PropertyDomainEvent<String> { }
    @Getter @Setter
    @Column(allowsNull = "false", length = FileSuffixType.Meta.MAX_LEN)
    @Property(
            domainEvent = FileSuffixDomainEvent.class,
            editing = Editing.DISABLED
    )
    private String fileSuffix;
    //endregion


    //region > nameText (persisted property)
    public static class NameTextDomainEvent extends PropertyDomainEvent<Clob> { }

    /**
     * Used to determine the name of the {@link Document#getName() name} of the rendered {@link Document}.
     */
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", length = NameTextType.Meta.MAX_LEN)
    @Property(
            notPersisted = true, // exclude from auditing
            domainEvent = NameTextDomainEvent.class,
            editing = Editing.DISABLED
    )
    private String nameText;
    //endregion

    //region > nameRenderingStrategy (property)
    public static class NameRenderingStrategyDomainEvent extends PropertyDomainEvent<RenderingStrategy> { }
    @Getter @Setter
    @Column(allowsNull = "false", name = "nameRenderStrategyId")
    @Property(
            domainEvent = NameRenderingStrategyDomainEvent.class,
            editing = Editing.DISABLED
    )
    private RenderingStrategy nameRenderingStrategy;
    //endregion

    //region > PreviewOnly (property)
    public static class PreviewOnlyDomainEvent extends RenderingStrategy.PropertyDomainEvent<Boolean> { }

    /**
     * Whether this template can only be previewed (not used to also create a document).
     */
    @Getter @Setter
    @Column(allowsNull = "false")
    @Property(
            domainEvent = PreviewOnlyDomainEvent.class,
            editing = Editing.DISABLED
    )
    private boolean previewOnly;
    //endregion


    //region > applicabilities (collection)
    public static class ApplicabilitiesDomainEvent extends DocumentType.CollectionDomainEvent<Applicability> {
    }

    @javax.jdo.annotations.Persistent(mappedBy = "documentTemplate", dependentElement = "true")
    @Collection(
            domainEvent = ApplicabilitiesDomainEvent.class,
            editing = Editing.DISABLED
    )
    @Getter @Setter
    private SortedSet<Applicability> appliesTo = new TreeSet<>();

    //endregion

    //region > applicable (action)
    @Mixin
    public static class _applicable {
        private final DocumentTemplate documentTemplate;

        public _applicable(final DocumentTemplate documentTemplate) {
            this.documentTemplate = documentTemplate;
        }


        public static class ActionDomainEvent extends DocumentAbstract.ActionDomainEvent { }

        @Action(domainEvent = ActionDomainEvent.class, semantics = SemanticsOf.IDEMPOTENT)
        @ActionLayout(cssClassFa = "fa-plus", contributed = Contributed.AS_ACTION)
        @MemberOrder(name = "appliesTo", sequence = "1")
        public DocumentTemplate $$(
                @Parameter(maxLength = FqcnType.Meta.MAX_LEN, mustSatisfy = FqcnType.Meta.Specification.class)
                @ParameterLayout(named = "Domain type")
                final String domainClassName,
                @Parameter(maxLength = FqcnType.Meta.MAX_LEN, mustSatisfy = FqcnType.Meta.Specification.class)
                @ParameterLayout(named = "Renderer Model Factory")
                final ClassNameViewModel rendererModelFactoryClassNameViewModel,
                @Parameter(maxLength = FqcnType.Meta.MAX_LEN, mustSatisfy = FqcnType.Meta.Specification.class)
                @ParameterLayout(named = "Attachment Advisor")
                final ClassNameViewModel attachmentAdvisorClassNameViewModel) {

            applicable(
                    domainClassName, rendererModelFactoryClassNameViewModel.getFullyQualifiedClassName(), attachmentAdvisorClassNameViewModel.getFullyQualifiedClassName());
            return this.documentTemplate;
        }

        public TranslatableString disable$$() {
            if (rendererModelFactoryClassNameService == null) {
                return TranslatableString.tr(
                        "No RendererModelFactoryClassNameService registered to locate implementations of RendererModelFactory");
            }
            if (attachmentAdvisorClassNameService == null) {
                return TranslatableString.tr(
                        "No AttachmentAdvisorClassNameService registered to locate implementations of AttachmentAdvisor");
            }
            return null;
        }

        public List<ClassNameViewModel> choices1$$() {
            return rendererModelFactoryClassNameService.rendererModelFactoryClassNames();
        }

        public List<ClassNameViewModel> choices2$$() {
            return attachmentAdvisorClassNameService.attachmentAdvisorClassNames();
        }

        public TranslatableString validate0$$(final String domainTypeName) {

            return isApplicable(domainTypeName) ?
                    TranslatableString.tr(
                            "Already applicable for '{domainTypeName}'",
                            "domainTypeName", domainTypeName)
                    : null;
        }


        @Programmatic
        public Applicability applicable(
                final Class<?> domainClass,
                final Class<? extends RendererModelFactory> renderModelFactoryClass,
                final Class<? extends AttachmentAdvisor> attachmentAdvisorClass) {
            return applicable(
                    domainClass.getName(),
                    renderModelFactoryClass,
                    attachmentAdvisorClass != null
                            ? attachmentAdvisorClass
                            : AttachmentAdvisorAttachToNone.class
            );
        }

        @Programmatic
        public Applicability applicable(
                final String domainClassName,
                final Class<? extends RendererModelFactory> renderModelFactoryClass,
                final Class<? extends AttachmentAdvisor> attachmentAdvisorClass) {
            return applicable(domainClassName, renderModelFactoryClass.getName(), attachmentAdvisorClass.getName() );
        }

        @Programmatic
        public Applicability applicable(
                final String domainClassName,
                final String renderModelFactoryClassName,
                final String attachmentAdvisorClassName) {
            Applicability applicability = existingApplicability(domainClassName);
            if(applicability == null) {
                applicability = applicabilityRepository.create(documentTemplate, domainClassName, renderModelFactoryClassName, attachmentAdvisorClassName);
            }
            return applicability;
        }

        private boolean isApplicable(final String domainClassName) {
            return existingApplicability(domainClassName) != null;
        }
        private Applicability existingApplicability(final String domainClassName) {
            SortedSet<Applicability> applicabilities = documentTemplate.getAppliesTo();
            for (Applicability applicability : applicabilities) {
                if (applicability.getDomainClassName().equals(domainClassName)) {
                    return applicability;
                }
            }
            return null;
        }


        @Inject
        RendererModelFactoryClassNameService rendererModelFactoryClassNameService;
        @Inject
        AttachmentAdvisorClassNameService attachmentAdvisorClassNameService;
        @Inject
        ApplicabilityRepository applicabilityRepository;

    }

    //endregion

    //region > notApplicable (action)
    @Mixin
    public static class _notApplicable {

        private final DocumentTemplate documentTemplate;

        public _notApplicable(final DocumentTemplate documentTemplate) {
            this.documentTemplate = documentTemplate;
        }

        public static class NotApplicableDomainEvent extends DocumentTemplate.ActionDomainEvent {
        }

        @Action(
                domainEvent = NotApplicableDomainEvent.class,
                semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE
        )
        @ActionLayout(
                cssClassFa = "fa-minus"
        )
        @MemberOrder(name = "appliesTo", sequence = "2")
        public DocumentTemplate $$(final Applicability applicability) {
            applicabilityRepository.delete(applicability);
            return this.documentTemplate;
        }

        public TranslatableString disable$$() {
            final TranslatableString tr = factoryService.mixin(_applicable.class, documentTemplate).disable$$();
            if(tr != null) {
                return tr;
            }
            return choices0$$().isEmpty() ? TranslatableString.tr("No applicabilities to remove") : null;
        }

        public SortedSet<Applicability> choices0$$() {
            return documentTemplate.getAppliesTo();
        }

        @Inject
        ApplicabilityRepository applicabilityRepository;
        @Inject
        FactoryService factoryService;
    }

    //endregion


    //region > appliesTo, newRendererModelFactory + newRendererModel, newAttachmentAdvisor + newAttachmentAdvice

    /**
     * Whether this template applies to this domain object (that is, {@link #newRendererModel(Object)} and {@link #newAttachmentAdvice(Document, Object)} will both return non-null values).
     */
    @Programmatic
    public boolean appliesTo(final Object domainObject) {
        return newRendererModelFactory(domainObject) != null;
    }

    @Programmatic
    public RendererModelFactory newRendererModelFactory(final Object domainObject) {
        final Class<?> domainObjectClass = domainObject.getClass();
        final com.google.common.base.Optional<Applicability> applicabilityIfAny = FluentIterable.from(getAppliesTo())
                .filter(applicability -> applies(applicability, domainObjectClass)).first();
        if (!applicabilityIfAny.isPresent()) {
            return null;
        }
        final RendererModelFactory rendererModelFactory = (RendererModelFactory) classService.instantiate(applicabilityIfAny.get().getRendererModelFactoryClassName());
        serviceRegistry2.injectServicesInto(rendererModelFactory);
        return rendererModelFactory;
    }

    @Programmatic
    public AttachmentAdvisor newAttachmentAdvisor(final Object domainObject) {
        final Class<?> domainObjectClass = domainObject.getClass();
        final com.google.common.base.Optional<Applicability> applicabilityIfAny = FluentIterable.from(getAppliesTo())
                .filter(applicability -> applies(applicability, domainObjectClass)).first();
        if (!applicabilityIfAny.isPresent()) {
            return null;
        }
        final AttachmentAdvisor attachmentAdvisor = (AttachmentAdvisor) classService.instantiate(applicabilityIfAny.get().getAttachmentAdvisorClassName());
        serviceRegistry2.injectServicesInto(attachmentAdvisor);
        return attachmentAdvisor;
    }

    @Programmatic
    public Object newRendererModel(final Object domainObject) {
        final RendererModelFactory rendererModelFactory = newRendererModelFactory(domainObject);
        if(rendererModelFactory == null) {
            throw new IllegalStateException(String.format(
                    "For domain template %s, could not locate Applicability for domain object: %s",
                    getName(), domainObject.getClass().getName()));
        }
        final Object rendererModel = rendererModelFactory.newRendererModel(this, domainObject);
        serviceRegistry2.injectServicesInto(rendererModel);
        return rendererModel;
    }

    @Programmatic
    public List<AttachmentAdvisor.PaperclipSpec> newAttachmentAdvice(final Document document, final Object domainObject) {
        final AttachmentAdvisor attachmentAdvisor = newAttachmentAdvisor(domainObject);
        if(attachmentAdvisor == null) {
            throw new IllegalStateException(String.format(
                    "For domain template %s, could not locate Applicability for domain object: %s",
                    getName(), domainObject.getClass().getName()));
        }
        final List<AttachmentAdvisor.PaperclipSpec> paperclipSpecs = attachmentAdvisor.advise(this, domainObject,
                document);
        return paperclipSpecs;
    }

    private boolean applies(
            final Applicability applicability,
            final Class<?> domainObjectClass) {
        final Class<?> load = classService.load(applicability.getDomainClassName());
        return load.isAssignableFrom(domainObjectClass);
    }

    //endregion




    //region > preview (programmatic)


    @Programmatic
    public URL preview(final Object rendererModel) throws IOException {

        serviceRegistry2.injectServicesInto(rendererModel);

        if(!getContentRenderingStrategy().isPreviewsToUrl()) {
            throw new IllegalStateException(String.format("RenderingStrategy '%s' does not support previewing to URL",
                    getContentRenderingStrategy().getReference()));
        }

        final DocumentNature inputNature = getContentRenderingStrategy().getInputNature();
        final DocumentNature outputNature = getContentRenderingStrategy().getOutputNature();

        final Renderer renderer = getContentRenderingStrategy().newRenderer();
        switch (inputNature){
        case BYTES:
            switch (outputNature) {
            case BYTES:
                return ((RendererFromBytesToBytesWithPreviewToUrl) renderer).previewBytesToBytes(
                        getType(), getAtPath(), getVersion(),
                        asBytes(), rendererModel);
            case CHARACTERS:
                return ((RendererFromBytesToCharsWithPreviewToUrl) renderer).previewBytesToChars(
                        getType(), getAtPath(), getVersion(),
                        asBytes(), rendererModel);
            default:
                // shouldn't happen, above switch statement is complete
                throw new IllegalArgumentException(String.format("Unknown output DocumentNature '%s'", outputNature));
            }
        case CHARACTERS:
            switch (outputNature) {
            case BYTES:
                return ((RendererFromCharsToBytesWithPreviewToUrl) renderer).previewCharsToBytes(
                        getType(), getAtPath(), getVersion(),
                        asChars(), rendererModel);
            case CHARACTERS:
                return ((RendererFromCharsToCharsWithPreviewToUrl) renderer).previewCharsToChars(
                        getType(), getAtPath(), getVersion(),
                        asChars(), rendererModel);
            default:
                // shouldn't happen, above switch statement is complete
                throw new IllegalArgumentException(String.format("Unknown output DocumentNature '%s'", outputNature));
            }

        default:
            // shouldn't happen, above switch statement is complete
            throw new IllegalArgumentException(String.format("Unknown input DocumentNature '%s'", inputNature));
        }
    }

    //endregion

    //region > create, createAndRender, createAndScheduleRender (programmatic)

    @Programmatic
    public Document create(final Object domainObject) {
        final Document document = createDocumentUsingRendererModel(domainObject);
        transactionService.flushTransaction();
        return document;
    }

    @Programmatic
    public Document createAndScheduleRender(final Object domainObject) {
        final Document document = create(domainObject);
        backgroundService2.execute(document).render(this, domainObject);
        return document;
    }
    @Programmatic
    public Document createAndRender(final Object domainObject) {
        final Document document = create(domainObject);
        document.render(this, domainObject);
        return document;
    }
    //endregion

    //region > createDocument (programmatic)
    @Programmatic
    public Document createDocumentUsingRendererModel(
            final Object domainObject) {
        final Object rendererModel = newRendererModel(domainObject);
        final String documentName = determineDocumentName(rendererModel);
        return createDocument(documentName);
    }

    private String determineDocumentName(final Object contentDataModel) {

        serviceRegistry2.injectServicesInto(contentDataModel);

        // subject
        final RendererFromCharsToChars nameRenderer =
                (RendererFromCharsToChars) getNameRenderingStrategy().newRenderer();
        String renderedDocumentName;
        try {
            renderedDocumentName = nameRenderer.renderCharsToChars(
                    getType(), "name", getAtPath(), getVersion(),
                    getNameText(), contentDataModel);
        } catch (IOException e) {
            renderedDocumentName = getName();
        }
        return withFileSuffix(renderedDocumentName);
    }

    private Document createDocument(String documentName) {
        return documentRepository.create(getType(), getAtPath(), documentName, getMimeType());
    }
    //endregion

    //region > renderContent (programmatic)
    @Programmatic
    public void renderContent(
            final Document document,
            final Object contentDataModel) {

        final String variant = "content";
        final String documentName = document.getName();
        try {

            final DocumentNature inputNature = getContentRenderingStrategy().getInputNature();
            final DocumentNature outputNature = getContentRenderingStrategy().getOutputNature();

            final Renderer renderer = getContentRenderingStrategy().newRenderer();

            switch (inputNature){
                case BYTES:
                    switch (outputNature) {
                    case BYTES:
                        final byte[] renderedBytes = ((RendererFromBytesToBytes) renderer).renderBytesToBytes(
                                getType(), variant, getAtPath(), getVersion(),
                                asBytes(), contentDataModel);
                        final Blob blob = new Blob (documentName, getMimeType(), renderedBytes);
                        document.modifyBlob(blob);
                        return;
                    case CHARACTERS:
                        final String renderedChars = ((RendererFromBytesToChars) renderer).renderBytesToChars(
                            getType(), variant, getAtPath(), getVersion(),
                            asBytes(), contentDataModel);
                        if(renderedChars.length() <= TextType.Meta.MAX_LEN) {
                            document.setTextData(getName(), getMimeType(), renderedChars);
                        } else {
                            final Clob clob = new Clob (documentName, getMimeType(), renderedChars);
                            document.modifyClob(clob);
                        }
                        return;
                    default:
                    // shouldn't happen, above switch statement is complete
                    throw new IllegalArgumentException(String.format("Unknown output DocumentNature '%s'", outputNature));
                    }
                case CHARACTERS:
                    switch (outputNature) {
                    case BYTES:
                        final byte[] renderedBytes = ((RendererFromCharsToBytes) renderer).renderCharsToBytes(
                                getType(), variant, getAtPath(), getVersion(),
                                asChars(), contentDataModel);
                        final Blob blob = new Blob (documentName, getMimeType(), renderedBytes);
                        document.modifyBlob(blob);
                        return;
                    case CHARACTERS:
                        final String renderedChars = ((RendererFromCharsToChars) renderer).renderCharsToChars(
                                getType(), variant, getAtPath(), getVersion(),
                                asChars(), contentDataModel);
                        if(renderedChars.length() <= TextType.Meta.MAX_LEN) {
                            document.setTextData(getName(), getMimeType(), renderedChars);
                        } else {
                            final Clob clob = new Clob (documentName, getMimeType(), renderedChars);
                            document.modifyClob(clob);
                        }
                        return;
                    default:
                    // shouldn't happen, above switch statement is complete
                    throw new IllegalArgumentException(String.format("Unknown output DocumentNature '%s'", outputNature));
                    }
                default:
                    // shouldn't happen, above switch statement is complete
                    throw new IllegalArgumentException(String.format("Unknown input DocumentNature '%s'", inputNature));
            }

        } catch (IOException e) {
            throw new ApplicationException("Unable to render document template", e);
        }
    }
    //endregion


    //region > withFileSuffix (programmatic)
    @Programmatic
    public String withFileSuffix(final String documentName) {
        final String suffix = getFileSuffix();
        final int lastPeriod = suffix.lastIndexOf(".");
        final String suffixNoDot = suffix.substring(lastPeriod + 1);
        final String suffixWithDot = "." + suffixNoDot;
        if (documentName.endsWith(suffixWithDot)) {
            return trim(documentName, NameType.Meta.MAX_LEN);
        }
        else {
            return StringUtils.stripEnd(trim(documentName, NameType.Meta.MAX_LEN - suffixWithDot.length()),".") + suffixWithDot;
        }
    }

    private static String trim(final String name, final int length) {
        return name.length() > length ? name.substring(0, length) : name;
    }
    //endregion



    //region > getVersion (programmatic)
    @Programmatic
    private long getVersion() {
        return (Long)JDOHelper.getVersion(this);
    }

    //endregion

    //region > injected services
    @Inject
    RendererModelFactoryClassNameService rendererModelFactoryClassNameService;
    @Inject
    AttachmentAdvisorClassNameService attachmentAdvisorClassNameService;
    @Inject
    ApplicabilityRepository applicabilityRepository;
    @Inject
    ClassService classService;
    @Inject
    ServiceRegistry2 serviceRegistry2;
    @Inject
    TransactionService transactionService;
    @Inject
    BackgroundService2 backgroundService2;

    //endregion

    //region > types

    public static class FileSuffixType {

        private FileSuffixType() {}

        public static class Meta {

            public static final int MAX_LEN = 12;

            private Meta() {}

        }
    }

    public static class NameTextType {

        private NameTextType() {}

        public static class Meta {

            public static final int MAX_LEN = 255;

            private Meta() {}

        }
    }


    //endregion

}
