package org.incode.module.docfragment.dom.impl;

import java.io.IOException;

import javax.inject.Inject;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.util.ObjectContracts;

import org.isisaddons.module.freemarker.dom.service.FreeMarkerService;

import org.incode.module.docfragment.dom.DocFragmentModuleDomModule;
import org.incode.module.docfragment.dom.types.AtPathType;
import org.incode.module.docfragment.dom.types.DocFragmentNameType;
import org.incode.module.docfragment.dom.types.ObjectTypeType;
import org.incode.module.docfragment.dom.types.TemplateTextType;

import freemarker.template.TemplateException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "incodeDocFragment",
        table = "DocFragment"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
        column="id")
@javax.jdo.annotations.Version(
        strategy= VersionStrategy.VERSION_NUMBER,
        column="version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByObjectTypeAndNameAndApplicableToAtPath",
                value = "SELECT "
                        + "FROM org.incode.module.docfragment.dom.impl.DocFragment "
                        + "WHERE objectType == :objectType "
                        + "   && name       == :name "
                        + "   && :atPath.startsWith(atPath) "
                        + "ORDER BY atPath DESC "
        ),
        @javax.jdo.annotations.Query(
                name = "findByObjectTypeAndNameAndAtPath",
                value = "SELECT "
                        + "FROM org.incode.module.docfragment.dom.impl.DocFragment "
                        + "WHERE objectType == :objectType "
                        + "   && name       == :name "
                        + "   && atPath     == :atPath "
                        + "ORDER BY atPath DESC "
        )
})
@javax.jdo.annotations.Unique(name="DocFragment_objectType_name_atPath_UNQ", members = {"objectType", "name", "atPath"})
@DomainObject(
        objectType = "docfragment.DocFragment"
)
public class DocFragment implements Comparable<DocFragment> {

    //region > domain event classes
    public static abstract class PropertyDomainEvent<T>
            extends DocFragmentModuleDomModule.PropertyDomainEvent<DocFragment, T> { }
    public static abstract class CollectionDomainEvent<T>
            extends DocFragmentModuleDomModule.CollectionDomainEvent<DocFragment, T> { }
    public static abstract class ActionDomainEvent
            extends DocFragmentModuleDomModule.ActionDomainEvent<DocFragment> { }
    //endregion

    //region > constructor
    @Builder
    public DocFragment(
            final String objectType,
            final String name,
            final String atPath,
            final String templateText) {
        this.objectType = objectType;
        this.name = name;
        setAtPath(atPath);
        setTemplateText(templateText);
    }
    //endregion


    //region > objectType
    public static class ObjectTypeDomainEvent extends DocFragment.PropertyDomainEvent<String>{}

    /**
     * for example, "invoice.Invoice" rather than "com.mycompany.myapp.invoicing.Invoice".
     * (to make maintenance/refactoring of application code easier).
     */
    @javax.jdo.annotations.Column(allowsNull = "false", length = ObjectTypeType.Meta.MAX_LEN)
    @Property(
            domainEvent = ObjectTypeDomainEvent.class,
            editing = Editing.DISABLED
    )
    @Title(sequence = "1", append = ":")
    @Getter @Setter
    private String objectType;
    //endregion

    //region > name
    public static class NameDomainEvent extends DocFragment.PropertyDomainEvent<String>{}

    /**
     * The name of this fragment (unique by object type)
     */
    @javax.jdo.annotations.Column(allowsNull = "false", length = DocFragmentNameType.Meta.MAX_LEN)
    @Property(
            domainEvent = NameDomainEvent.class,
            editing = Editing.DISABLED
    )
    @Title(sequence = "2", append = " @")
    @Getter @Setter
    private String name;
    //endregion

    //region > atPath
    public static class AtPathDomainEvent extends DocFragment.PropertyDomainEvent<String>{}

    /**
     * The application tenancy to which this fragment relates (unique within object type and name)
     * eg: "/ITA"
     */
    @javax.jdo.annotations.Column(allowsNull = "false", length = AtPathType.Meta.MAX_LEN)
    @Property(
            domainEvent = AtPathDomainEvent.class,
            editing = Editing.DISABLED
    )
    @Title(sequence = "3")
    @Getter @Setter
    private String atPath;
    //endregion

    //region > templateText (property)
    public static class TemplateTextDomainEvent extends DocFragment.PropertyDomainEvent<String>{}

    /**
     * eg: "yada yada {this.id} yada yada {adasd}"
     *
     * The text is {@link #render(Object) render}ed with respect to an object of the {@link #getObjectType()}.
     */
    @javax.jdo.annotations.Column(allowsNull = "false", length = TemplateTextType.Meta.MAX_LEN)
    @Property(
            domainEvent = TemplateTextDomainEvent.class,
            editing = Editing.ENABLED
    )
    @PropertyLayout(
            multiLine = TemplateTextType.Meta.MULTILINE
    )
    @Getter @Setter
    private String templateText;
    //endregion


    //region > changeTemplateText (action)
    public static class ChangeTemplateTextDomainEvent extends DocFragment.ActionDomainEvent{}

    @Action(
            domainEvent = ChangeTemplateTextDomainEvent.class,
            semantics = SemanticsOf.NON_IDEMPOTENT
    )
    public DocFragment changeTemplateText(
            @ParameterLayout(
                    named = TemplateTextType.Meta.NAME,
                    multiLine = TemplateTextType.Meta.MULTILINE
            )
            final String templateText) {
        setTemplateText(templateText);
        return this;
    }

    public String default0ChangeTemplateText() {
        return getTemplateText();
    }
    //endregion

    //region > delete (action)
    public static class DeleteDomainEvent extends DocFragment.ActionDomainEvent{}

    @Action(
            domainEvent = DeleteDomainEvent.class,
            semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE
    )
    public void delete() {
        final String title = titleService.titleOf(this);
        messageService.informUser(String.format("'%s' deleted", title));
        repositoryService.remove(this);
    }
    //endregion


    //region > render (programmatic)
    @Programmatic
    public String render(Object domainObject) throws IOException, TemplateException {
        return freeMarkerService.render(
                getFreemarkerTemplateName(), getTemplateText(), domainObject);

    }


    private String getFreemarkerTemplateName() { return String.format("%s#%s:%s", getObjectType(), getName(), getAtPath()); }
    //endregion


    //region > toString, compareTo
    @Override
    public String toString() {
        return ObjectContracts.toString(this, "objectType", "name", "atPath");
    }

    @Override
    public int compareTo(final DocFragment other) {
        return ObjectContracts.compare(this, other, "objectType", "name", "atPath");
    }
    //endregion

    //region > injected services
    @javax.inject.Inject
    RepositoryService repositoryService;

    @javax.inject.Inject
    TitleService titleService;

    @javax.inject.Inject
    MessageService messageService;

    @Inject
    private FreeMarkerService freeMarkerService;
    //endregion

}