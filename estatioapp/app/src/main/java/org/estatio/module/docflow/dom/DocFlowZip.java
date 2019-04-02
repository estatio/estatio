package org.estatio.module.docflow.dom;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Uniques;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.ComparisonChain;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.eventbus.ObjectPersistedEvent;
import org.apache.isis.applib.services.eventbus.ObjectRemovingEvent;
import org.apache.isis.applib.services.eventbus.ObjectUpdatedEvent;
import org.apache.isis.applib.services.tablecol.TableColumnOrderService;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.HasAtPath;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.module.financial.dom.BankAccount;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.APPLICATION,
        schema = "dbo",
        table = "DocFlowZip"
)
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findBySdiId", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.docflow.dom.DocFlowZip "
                        + "WHERE sdiId == :sdiId "),
})
@Uniques({
        // none currently; note that sdiId is a primary key
})
@Indices({
        // none currently
})
@DomainObject(
        objectType = "docflow.DocFlowZip",
        editing = Editing.DISABLED,
        persistedLifecycleEvent = DocFlowZip.PersistedLifecycleEvent.class,
        updatedLifecycleEvent = DocFlowZip.UpdatedLifecycleEvent.class,
        removingLifecycleEvent = DocFlowZip.RemovingLifecycleEvent.class
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
        , titleUiEvent = DocFlowZip.TitleUiEvent.class
)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class DocFlowZip implements Comparable<DocFlowZip>, HasAtPath {

    public static class PersistedLifecycleEvent extends ObjectPersistedEvent<BankAccount> {}
    public static class UpdatedLifecycleEvent extends ObjectUpdatedEvent<BankAccount> {}
    public static class RemovingLifecycleEvent extends ObjectRemovingEvent<BankAccount> {}
    public static class TitleUiEvent extends org.apache.isis.applib.services.eventbus.TitleUiEvent <BankAccount> {}

    public DocFlowZip() {
    }

    public DocFlowZip(
            final long sdiId,
            final String atPath,
            final String sha256) {
        this.sdiId = sdiId;
        this.atPath = atPath;
        this.sha256 = sha256;
    }

    public String title() {
        return String.format("%s", getSdiId());
    }

    @PrimaryKey
    @Column(allowsNull = "false")
    @Property()
    @PropertyLayout(named = "SDI Id")
    @Getter @Setter
    private long sdiId;

    @Column(allowsNull = "false", length = 64)
    @Property()
    @Getter @Setter
    private String sha256;

    @javax.jdo.annotations.Column(
            length = ApplicationTenancy.MAX_LENGTH_PATH,
            allowsNull = "false",
            name = "atPath"
    )
    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private String atPath;


    @Programmatic
    public Document locateAttachedDocument(final String role) {
        return paperclipRepository.findByAttachedToAndRoleName(this, role)
                .stream()
                .map(Paperclip::getDocument)
                .filter(Document.class::isInstance)
                .map(Document.class::cast)
                .findFirst()
                .orElse(null);
    }


    @Programmatic
    public boolean isSameAs(final DocFlowZip other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        return Objects.equals(getSha256(), other.getSha256());
    }


    //region > compareTo, toString
    @Override
    public int compareTo(final DocFlowZip other) {
        return ComparisonChain.start()
                .compare(getSdiId(), other.getSdiId())
                .result();
    }

    @Override
    public String toString() {
        return "DocFlowZip{" +
                "sdiId='" + getSdiId() + '\'' +
                '}';
    }

    //endregion

    @DomainService(nature = NatureOfService.DOMAIN, menuOrder = "100")
    public static class TableColumnService implements TableColumnOrderService {

        @Override
        public List<String> orderParented(
                final Object parent,
                final String collectionId,
                final Class<?> collectionType,
                final List<String> propertyIds) {
            if (parent instanceof DocFlowZip) {
                // TODO: scaffolding for the future
                return propertyIds;
            }
            return null;
        }

        @Override
        public List<String> orderStandalone(final Class<?> collectionType, final List<String> propertyIds) {
            return null;
        }

    }

    @NotPersistent
    @Inject
    PaperclipRepository paperclipRepository;



}
