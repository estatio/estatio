package org.incode.module.document.dom.impl.applicability;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.util.ObjectContracts;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.types.FqcnType;

import lombok.Getter;
import lombok.Setter;

/**
 * Indicates whether a domain object('s type) is applicable to a particular {@link DocumentTemplate}, providing the
 * (name of) the {@link RendererModelFactory} to use to create the renderer model to feed into that template, and the
 * (name of) the {@link AttachmentAdvisor} to use to specify which domain objects the resultant {@link Document}
 * should be attached to once created.
 */
@javax.jdo.annotations.PersistenceCapable(
        schema = "incodeDocuments",
        identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
    // none currently
})
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "Applicability_documentTemplate_domainClassName_UNQ",
                members = { "documentTemplate", "domainClassName" } )
})
@DomainObject(editing = Editing.DISABLED)
@DomainObjectLayout()
public class Applicability implements Comparable<Applicability> {

    public TranslatableString title() {
        final Applicability applicability = this;
        return TranslatableString.tr(
                "{simpleName} applies to [{docType}]",
                "simpleName", simpleNameOf(applicability.getDomainClassName()),
                "docType", applicability.getDocumentTemplate().getType().getReference());
    }

    private static String simpleNameOf(final String domainType) {
        int lastDot = domainType.lastIndexOf(".");
        return domainType.substring(lastDot+1);
    }


    Applicability() {
        // for testing only
    }
    public Applicability(
            final DocumentTemplate documentTemplate,
            final Class<?> domainClass,
            final Class<? extends RendererModelFactory> rendererModelFactoryClass,
            final Class<? extends AttachmentAdvisor> attachmentAdvisorClass
            ) {
        this(documentTemplate, domainClass.getName(), rendererModelFactoryClass.getName(), attachmentAdvisorClass.getName());
    }

    public Applicability(
            final DocumentTemplate documentTemplate,
            final String domainClassName,
            final String rendererModelFactoryClassName,
            final String attachmentAdvisorClassName) {
        setDocumentTemplate(documentTemplate);
        setDomainClassName(domainClassName);
        setRendererModelFactoryClassName(rendererModelFactoryClassName);
        setAttachmentAdvisorClassName(attachmentAdvisorClassName);
    }
    //endregion


    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", name = "documentTemplateId")
    @Property()
    private DocumentTemplate documentTemplate;

    /**
     * The class used as the input for the document
     */
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", length = FqcnType.Meta.MAX_LEN)
    @Property()
    private String domainClassName;

    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", length = FqcnType.Meta.MAX_LEN)
    @Property()
    private String rendererModelFactoryClassName;

    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", length = FqcnType.Meta.MAX_LEN)
    @Property()
    private String attachmentAdvisorClassName;


    @Override
    public String toString() {
        return ObjectContracts.toString(this, "documentTemplate", "domainClassName", "rendererModelFactoryClassName", "attachmentAdvisorClassName");
    }

    @Override
    public int compareTo(final Applicability other) {
        return ObjectContracts.compare(this, other, "documentTemplate", "domainClassName", "rendererModelFactoryClassName", "attachmentAdvisorClassName");
    }

}
