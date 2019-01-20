package org.incode.module.document.dom.impl.docs.minio;

import java.util.Collections;
import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.DocumentModule;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentSort;
import org.incode.module.document.spi.minio.DomainObjectPropertyProviderForDocument;

/**
 * Requests to archive the blob from this {@link Document}, on an ad-hoc basis.
 *
 * <p>
 *     Normally blobs are archived from {@link Document}s only once they are older than a certain age, as defined by
 *     {@link DomainObjectPropertyProviderForDocument}.  This action allows an administrator to request the blob for
 *     any arbitrary {@link Document} to be archived.  This is primarily for support/testing purposes.
 * </p>
 */
@Mixin(method = "act")
public class Document_archive {

    private final Document document;

    public Document_archive(final Document document) {
        this.document = document;
    }

    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<Document_archive> { }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    public Document act(final String property) {
        return document;
    }

    public String default0Act() {
        return "blob";
    }
    public List<String> choices0Act() {
        return Collections.singletonList("blob");
    }
    public boolean hideAct() {
        return document.getSort() == DocumentSort.EMPTY || document.getSort().isExternal();
    }


}
