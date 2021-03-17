package org.incode.module.communications.dom.impl.paperclips;

import java.util.List;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.tablecol.TableColumnOrderService;

import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.mixins.T_documents;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType= IdentityType.DATASTORE
        , schema = "IncodeCommunications"
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@DomainObject()
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class PaperclipForCommunication extends Paperclip {

    //region > communication (property)
    @Column(
            allowsNull = "false",
            name = "communicationId"
    )
    @Getter @Setter
    private Communication communication;
    //endregion

    //region > attachedTo (hook, derived)
    @NotPersistent
    @Override
    public Object getAttachedTo() {
        return getCommunication();
    }

    @Override
    protected void setAttachedTo(final Object object) {
        setCommunication((Communication) object);
    }
    //endregion

    //region > SubtypeProvider SPI implementation
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SubtypeProvider extends PaperclipRepository.SubtypeProviderAbstract {
        public SubtypeProvider() {
            super(Communication.class, PaperclipForCommunication.class);
        }
    }
    //endregion

    //region > mixins

    @Mixin
    public static class _attachments extends T_documents<Communication> {
        public _attachments(final Communication communication) {
            super(communication);
        }

        @DomainService(
                nature = NatureOfService.DOMAIN,
                menuOrder = "98" // needs to be < implementations provided by document module.
        )
        public static class TableColumnOrderServiceForPaperclipsAttachedToCommunication implements
                TableColumnOrderService {

            @Override
            public List<String> orderParented(
                    final Object domainObject,
                    final String collectionId,
                    final Class<?> collectionType,
                    final List<String> propertyIds) {
                if (!Paperclip.class.isAssignableFrom(collectionType)) {
                    return null;
                }

                if (!(domainObject instanceof Communication)) {
                    return null;
                }

                if("attachments".equals(collectionId)) {
                    final List<String> trimmedPropertyIds = Lists.newArrayList(propertyIds);
                    trimmedPropertyIds.remove("attachedTo");
                    return trimmedPropertyIds;
                }

                return null;
            }

            @Override
            public List<String> orderStandalone(final Class<?> collectionType, final List<String> propertyIds) {
                return null;
            }
        }
    }

    //endregion


}
