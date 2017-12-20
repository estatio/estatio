package org.incode.module.alias.dom.impl;

import javax.inject.Inject;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.eventbus.Subscribe;

import org.axonframework.eventhandling.annotation.EventHandler;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.util.TitleBuffer;

import org.incode.module.alias.dom.AliasModule;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "incodeAlias",
        table = "Alias"
)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByAliased", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.alias.dom.impl.Alias "
                        + "WHERE aliasedStr == :aliasedStr ")
})
@javax.jdo.annotations.Uniques({
    @Unique(
            name = "Alias_aliased_atPath_aliasTypeId_IDX",
            members = { "aliasedStr", "atPath", "aliasTypeId" }
    )
})
@DomainObject(
        objectType = "incodeAlias.Alias",
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        titleUiEvent = Alias.TitleUiEvent.class,
        iconUiEvent = Alias.IconUiEvent.class,
        cssClassUiEvent = Alias.CssClassUiEvent.class
)
public abstract class Alias implements Comparable<Alias> {

    //region > ui event classes
    public static class TitleUiEvent extends AliasModule.TitleUiEvent<Alias>{}
    public static class IconUiEvent extends AliasModule.IconUiEvent<Alias>{}
    public static class CssClassUiEvent extends AliasModule.CssClassUiEvent<Alias>{}
    //endregion

    //region > domain event classes
    public static abstract class PropertyDomainEvent<T> extends AliasModule.PropertyDomainEvent<Alias, T> { }
    public static abstract class CollectionDomainEvent<T> extends AliasModule.CollectionDomainEvent<Alias, T> { }
    public static abstract class ActionDomainEvent extends AliasModule.ActionDomainEvent<Alias> { }
    //endregion

    //region > title, icon, cssClass
    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class TitleSubscriber extends AbstractSubscriber {
        @EventHandler
        @Subscribe
        public void on(Alias.TitleUiEvent ev) {
            if(ev.getTitle() != null) {
                return;
            }
            ev.setTitle(titleOf(ev.getSource()));
        }
        private String titleOf(final Alias alias) {
            final TitleBuffer buf = new TitleBuffer();
            buf.append(alias.getAtPath());
            buf.append(",");
            buf.append(alias.getAliasTypeId());
            buf.append(",");
            buf.append(alias.getReference());
            buf.append(":");
            buf.append(titleService.titleOf(alias.getAliased()));
            return buf.toString();
        }
        @Inject
        TitleService titleService;
    }

    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class IconSubscriber extends AbstractSubscriber {
        @EventHandler
        @Subscribe
        public void on(Alias.IconUiEvent ev) {
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
        @EventHandler
        @Subscribe
        public void on(Alias.CssClassUiEvent ev) {
            if(ev.getCssClass() != null) {
                return;
            }
            ev.setCssClass("");
        }
    }
    //endregion


    //region > aliasedStr (hidden property)

    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", length = AliasModule.JdoColumnLength.BOOKMARK)
    @Property(
            hidden = Where.EVERYWHERE
    )
    private String aliasedStr;

    //endregion

    //region > aliased (derived property, hooks)
    /**
     * Polymorphic association to the aliased object.
     */
    @javax.jdo.annotations.NotPersistent
    @Programmatic
    public abstract Object getAliased();

    protected abstract void setAliased(final Object aliased);

    //endregion


    //region > atPath (property)
    public static class AtPathDomainEvent extends PropertyDomainEvent<String> { }
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "true", length = AliasModule.JdoColumnLength.AT_PATH)
    @Property(
            domainEvent = AtPathDomainEvent.class,
            editing = Editing.DISABLED
    )
    @PropertyLayout(
            named = "Application tenancy"
    )
    private String atPath;
    //endregion

    //region > aliasType (property)
    public static class AliasTypeIdDomainEvent extends PropertyDomainEvent<String> { }
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", length = AliasModule.JdoColumnLength.ALIAS_TYPE_ID)
    @Property(
            domainEvent = AliasTypeIdDomainEvent.class
    )
    @PropertyLayout(
            named = "Alias type"
    )
    private String aliasTypeId;
    //endregion

    //region > reference (property)
    public static class ReferenceDomainEvent extends PropertyDomainEvent<String> { }
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", length = AliasModule.JdoColumnLength.ALIAS_REFERENCE)
    @Property(
            domainEvent = ReferenceDomainEvent.class
    )
    private String reference;
    //endregion


    //region > remove (action)
    public static class RemoveDomainEvent extends ActionDomainEvent { }
    @Action(
            domainEvent = RemoveDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE
    )
    @ActionLayout(
            cssClass = "btn-warning",
            cssClassFa = "trash"
    )
    public Object remove() {
        final Object aliased = getAliased();
        aliasRepository.remove(this);
        return aliased;
    }

    //endregion


    //region > toString, compareTo

    @Override
    public String toString() {
        return ObjectContracts.toString(this, "aliasedStr", "atPath", "aliasTypeId", "reference");
    }

    @Override
    public int compareTo(final Alias other) {
        return ObjectContracts.compare(this, other, "aliasedStr", "atPath", "aliasTypeId", "reference");
    }

    //endregion

    //region > injected services
    @Inject
    AliasRepository aliasRepository;
    //endregion

}
