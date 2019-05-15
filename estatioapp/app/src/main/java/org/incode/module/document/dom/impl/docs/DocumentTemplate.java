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
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.background.BackgroundService2;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.xactn.TransactionService;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.document.dom.impl.applicability.Applicability;
import org.incode.module.document.dom.impl.applicability.AttachmentAdvisor;
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
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.services.ClassService;
import org.incode.module.document.dom.types.AtPathType;

import org.estatio.module.invoice.dom.DocumentTemplateData;
import org.estatio.module.invoice.dom.DocumentTypeData;
import org.estatio.module.invoice.dom.RenderingStrategyData;

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
                name = "findByType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.docs.DocumentTemplate "
                        + "WHERE typeCopy   == :type "
                        + "ORDER BY atPathCopy DESC, date DESC "
        ),
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
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class DocumentTemplate
        extends DocumentAbstract<DocumentTemplate> {


    public TranslatableString title() {
        final DocumentTemplate template = this;
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
            final String subjectText) {
        this(type, date, atPath, fileSuffix, previewOnly, subjectText);
        modifyBlob(blob);
    }

    public DocumentTemplate(
            final DocumentType type,
            final LocalDate date,
            final String atPath,
            final String fileSuffix,
            final boolean previewOnly,
            final String name, final String mimeType, final String text,
            final String subjectText) {
        this(type, date, atPath, fileSuffix, previewOnly, subjectText);
        setTextData(name, mimeType, text);
    }

    public DocumentTemplate(
            final DocumentType type,
            final LocalDate date,
            final String atPath,
            final String fileSuffix,
            final boolean previewOnly,
            final Clob clob,
            final String subjectText) {
        this(type, date, atPath, fileSuffix, previewOnly, subjectText);
        modifyClob(clob);
    }

    private DocumentTemplate(
            final DocumentType type,
            final LocalDate date,
            final String atPath,
            final String fileSuffix,
            final boolean previewOnly,
            final String nameText) {
        super(type, atPath);

        this.typeCopy = type;
        this.atPathCopy = atPath;
        this.date = date;
        this.fileSuffix = stripLeadingDotAndLowerCase(fileSuffix);
        this.previewOnly = previewOnly;
        this.nameText = nameText;
    }

    static String stripLeadingDotAndLowerCase(final String fileSuffix) {
        final int lastDot = fileSuffix.lastIndexOf(".");
        final String stripLeadingDot = fileSuffix.substring(lastDot+1);
        return stripLeadingDot.toLowerCase();
    }



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

    @NotPersistent
    @Programmatic
    private DocumentTemplateData templateData;
    public DocumentTemplateData getTemplateData() {
        if (templateData == null) {
            templateData = getTypeData().lookup(getAtPath());
        }
        return templateData;
    }

    @NotPersistent
    @Programmatic
    private DocumentTypeData typeData;
    public DocumentTypeData getTypeData() {
        if (typeData == null) {
            typeData = DocumentTypeData.reverseLookup(getType());
        }
        return typeData;
    }

    public RenderingStrategyData getContentRenderingStrategyData() {
        return getTemplateData().getContentRenderingStrategy();
    }

    public RenderingStrategyData getNameRenderingStrategyData() {
        return getTemplateData().getNameRenderingStrategy();
    }

    /**
     * TODO: there is no need to have different versions of a DocumentTemplate over time,
     *  so we should just get rid of this property.
     *  It is already hidden in the UI via layout.xml
     */
    @Getter @Setter
    @Column(allowsNull = "false")
    @Property()
    private LocalDate date;

    @Getter @Setter
    @Column(allowsNull = "false", length = FileSuffixType.Meta.MAX_LEN)
    @Property()
    private String fileSuffix;

    /**
     * Used to determine the name of the {@link Document#getName() name} of the rendered {@link Document}.
     */
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", length = NameTextType.Meta.MAX_LEN)
    @Property()
    private String nameText;


    /**
     * Whether this template can only be previewed (not used to also create a document).
     */
    @Getter @Setter
    @Column(allowsNull = "false")
    @Property()
    private boolean previewOnly;


    @javax.jdo.annotations.Persistent(mappedBy = "documentTemplate", dependentElement = "true")
    @Collection()
    @Getter @Setter
    private SortedSet<Applicability> appliesTo = new TreeSet<>();


    //region > appliesTo, newRendererModelFactory + newRendererModel, newAttachmentAdvisor + newAttachmentAdvice

    private RendererModelFactory newRendererModelFactory(final Object domainObject) {
        final Class<?> domainClass = domainObject.getClass();
        return getTemplateData().newRenderModelFactory(domainClass, classService, serviceRegistry2);
    }

    @Programmatic
    public AttachmentAdvisor newAttachmentAdvisor(final Object domainObject) {
        final Class<?> domainClass = domainObject.getClass();
        return getTemplateData().newAttachmentAdvisor(domainClass, classService, serviceRegistry2);
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

    //endregion




    //region > preview, previewUrl (programmatic)


    @Programmatic
    public URL previewUrl(final Object rendererModel) throws IOException {

        serviceRegistry2.injectServicesInto(rendererModel);

        if(!getTemplateData().getContentRenderingStrategy().isPreviewsToUrl()) {
            throw new IllegalStateException(String.format("RenderingStrategy '%s' does not support previewing to URL",
                    getTemplateData().getContentRenderingStrategy().getReference()));
        }

        final DocumentNature inputNature = getContentRenderingStrategyData().getInputNature();
        final DocumentNature outputNature = getContentRenderingStrategyData().getOutputNature();

        final Renderer renderer = getContentRenderingStrategyData().newRenderer(classService, serviceRegistry2);
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
                (RendererFromCharsToChars) getNameRenderingStrategyData().newRenderer(classService, serviceRegistry2);
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
        renderContent((DocumentLike)document, contentDataModel);
    }

    @Programmatic
    public void renderContent(
            final DocumentLike document,
            final Object contentDataModel) {
        final String documentName = determineDocumentName(contentDataModel);
        document.setName(documentName);
        final String variant = "content";
        try {

            final DocumentNature inputNature = getContentRenderingStrategyData().getInputNature();
            final DocumentNature outputNature = getContentRenderingStrategyData().getOutputNature();

            final Renderer renderer = getContentRenderingStrategyData().newRenderer(classService, serviceRegistry2);

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
