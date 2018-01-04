package org.incode.module.document.dom.impl.paperclips;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.xactn.TransactionService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Paperclip.class
)
public class PaperclipRepository {

    public String getId() {
        return "incodeDocuments.PaperclipRepository";
    }

    //region > findByDocument (programmatic)
    @Programmatic
    public List<Paperclip> findByDocument(final DocumentAbstract document) {
        return repositoryService.allMatches(
                new QueryDefault<>(Paperclip.class,
                        "findByDocument",
                        "document", document));
    }
    //endregion

    //region > findByAttachedTo (programmatic)
    @Programmatic
    public List<Paperclip> findByAttachedTo(final Object attachedTo) {
        if(attachedTo == null) {
            return null;
        }
        final Bookmark bookmark = bookmarkService.bookmarkFor(attachedTo);
        if(bookmark == null) {
            return null;
        }
        final String attachedToStr = bookmark.toString();
        return repositoryService.allMatches(
                new QueryDefault<>(Paperclip.class,
                        "findByAttachedTo",
                        "attachedToStr", attachedToStr));
    }
    //endregion

    //region > findByAttachedToAndRoleName (programmatic)
    @Programmatic
    public List<Paperclip> findByAttachedToAndRoleName(
            final Object attachedTo,
            final String roleName) {
        if(attachedTo == null) {
            return null;
        }
        if(roleName == null) {
            return null;
        }
        final Bookmark bookmark = bookmarkService.bookmarkFor(attachedTo);
        if(bookmark == null) {
            return null;
        }
        final String attachedToStr = bookmark.toString();
        return repositoryService.allMatches(
                new QueryDefault<>(Paperclip.class,
                        "findByAttachedToAndRoleName",
                        "attachedToStr", attachedToStr,
                        "roleName", roleName));
    }
    //endregion

    //region > findByDocumentAndAttachedTo (programmatic)
    @Programmatic
    public List<Paperclip> findByDocumentAndAttachedTo(
            final DocumentAbstract<?> document,
            final Object attachedTo) {
        if(document == null) {
            return null;
        }
        if(attachedTo == null) {
            return null;
        }
        final Bookmark bookmark = bookmarkService.bookmarkFor(attachedTo);
        if(bookmark == null) {
            return null;
        }
        final String attachedToStr = bookmark.toString();
        return repositoryService.allMatches(
                new QueryDefault<>(Paperclip.class,
                        "findByDocumentAndAttachedTo",
                        "document", document,
                        "attachedToStr", attachedToStr));
    }
    //endregion

    //region > findByDocumentAndAttachedToAndRoleName (programmatic)
    @Programmatic
    public Paperclip findByDocumentAndAttachedToAndRoleName(
            final DocumentAbstract<?> document,
            final Object attachedTo,
            final String roleName) {
        if(document == null) {
            return null;
        }
        if(attachedTo == null) {
            return null;
        }
        if(roleName == null) {
            return null;
        }
        final Bookmark bookmark = bookmarkService.bookmarkFor(attachedTo);
        if(bookmark == null) {
            return null;
        }
        final String attachedToStr = bookmark.toString();
        return repositoryService.firstMatch(
                new QueryDefault<>(Paperclip.class,
                        "findByDocumentAndAttachedToAndRoleName",
                        "document", document,
                        "attachedToStr", attachedToStr,
                        "roleName", roleName));
    }
    //endregion

    //region > canAttach (programmatic)
    @Programmatic
    public boolean canAttach(
            final Object candidateToAttachTo) {
        final Class<? extends Paperclip> subtype = subtypeClassForElseNull(candidateToAttachTo);
        return subtype != null;
    }
    //endregion

    //region > attach (programmatic)

    /**
     * This is an idempotent operation.
     */
    @Programmatic
    public Paperclip attach(
            final DocumentAbstract documentAbstract,
            final String roleName,
            final Object attachTo) {

        Paperclip paperclip = findByDocumentAndAttachedToAndRoleName(
                documentAbstract, attachTo, roleName);
        if(paperclip != null) {
            return paperclip;
        }

        final Class<? extends Paperclip> subtype = subtypeClassFor(attachTo);
        paperclip = repositoryService.instantiate(subtype);

        paperclip.setDocument(documentAbstract);
        paperclip.setRoleName(roleName);
        if(documentAbstract instanceof Document) {
            final Document document = (Document) documentAbstract;
            paperclip.setDocumentCreatedAt(document.getCreatedAt());
        }

        if(!repositoryService.isPersistent(attachTo)) {
            transactionService.flushTransaction();
        }

        final Bookmark bookmark = bookmarkService.bookmarkFor(attachTo);
        paperclip.setAttachedTo(attachTo);
        paperclip.setAttachedToStr(bookmark.toString());

        repositoryService.persistAndFlush(paperclip);

        return paperclip;
    }

    private Class<? extends Paperclip> subtypeClassFor(final Object toAttachTo) {
        Class<? extends Paperclip> subtype = subtypeClassForElseNull(toAttachTo);
        if (subtype != null) {
            return subtype;
        }
        throw new IllegalStateException(String.format(
                "No subtype of Paperclip was found for '%s'; implement the PaperclipRepository.SubtypeProvider SPI",
                toAttachTo.getClass().getName()));
    }

    private Class<? extends Paperclip> subtypeClassForElseNull(final Object toAttachTo) {
        Class<?> domainClass = toAttachTo.getClass();
        for (SubtypeProvider subtypeProvider : subtypeProviders) {
            Class<? extends Paperclip> subtype = subtypeProvider.subtypeFor(domainClass);
            if(subtype != null) {
                return subtype;
            }
        }
        return null;
    }

    //endregion

    //region > attach (programmatic)

    @Programmatic
    public <T> T paperclipAttaches(final Document document, Class<T> typeAttachedTo) {
        final List<Paperclip> paperclips = findByDocument(document);
        for (Paperclip paperclip : paperclips) {
            final Object attachedTo = paperclip.getAttachedTo();
            if(typeAttachedTo.isAssignableFrom(attachedTo.getClass())) {
                return (T) attachedTo;
            }
        }
        return null;
    }

    //endregion


    //region > delete, deleteIfAttachedTo
    @Programmatic
    public void delete(final Paperclip paperclip) {
        repositoryService.remove(paperclip);
    }

    public enum Policy {
        /**
         * Delete the paperclips
         */
        PAPERCLIPS_ONLY,
        /**
         * Delete the paperclips, and also delete the documents if they are no longer attached to any objects
         */
        PAPERCLIPS_AND_DOCUMENTS_IF_ORPHANED
    }

    @Programmatic
    public void deleteIfAttachedTo(final Object domainObject) {
        deleteIfAttachedTo(domainObject, Policy.PAPERCLIPS_ONLY);
    }
    @Programmatic
    public void deleteIfAttachedTo(final Object domainObject, final Policy policy) {
        final List<Paperclip> paperclips = findByAttachedTo(domainObject);
        for (Paperclip paperclip : paperclips) {
            delete(paperclip);
            if(policy == Policy.PAPERCLIPS_AND_DOCUMENTS_IF_ORPHANED) {
                final DocumentAbstract document = paperclip.getDocument();
                if(orphaned(document, domainObject)) {
                    repositoryService.remove(document);
                }
            }
        }
    }

    private boolean orphaned(final DocumentAbstract document, final Object attachedTo) {
        final List<Paperclip> paperclips = findByDocument(document);
        for (Paperclip paperclip : paperclips) {
            if(paperclip.getAttachedTo() != attachedTo) {
                // found a paperclip for this document attached to some other object
                return false;
            }
        }
        return true;
    }
    //endregion



    //region > SubtypeProvider SPI

    /**
     * SPI to be implemented (as a {@link DomainService}) for any domain object to which {@link Paperclip}s can be
     * attached.
     */
    public interface SubtypeProvider {
        /**
         * @return the subtype of {@link Paperclip} to use to hold the (type-safe) paperclip of the domain object.
         */
        @Programmatic
        Class<? extends Paperclip> subtypeFor(Class<?> domainObject);
    }
    /**
     * Convenience adapter to help implement the {@link SubtypeProvider} SPI; ignores the roleName passed into
     * {@link SubtypeProvider#subtypeFor(Class)}, simply returns the class pair passed into constructor.
     */
    public abstract static class SubtypeProviderAbstract implements SubtypeProvider {
        private final Class<?> attachedToDomainType;
        private final Class<? extends Paperclip> attachedToSubtype;

        protected SubtypeProviderAbstract(final Class<?> attachedToDomainType, final Class<? extends Paperclip> attachedToSubtype) {
            this.attachedToDomainType = attachedToDomainType;
            this.attachedToSubtype = attachedToSubtype;
        }

        @Override
        public Class<? extends Paperclip> subtypeFor(final Class<?> domainType) {
            return attachedToDomainType.isAssignableFrom(domainType) ? attachedToSubtype : null;
        }
    }

    //endregion

    //region > injected services
    @Inject
    RepositoryService repositoryService;

    @Inject
    TransactionService transactionService;

    @Inject
    BookmarkService bookmarkService;

    @Inject
    List<SubtypeProvider> subtypeProviders;
    //endregion


}
