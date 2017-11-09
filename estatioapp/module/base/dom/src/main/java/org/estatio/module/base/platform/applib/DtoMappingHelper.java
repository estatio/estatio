package org.estatio.module.base.platform.applib;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.schema.common.v1.BookmarkObjectState;
import org.apache.isis.schema.common.v1.OidDto;

@DomainService(nature = NatureOfService.DOMAIN)
public class DtoMappingHelper {

    @Programmatic
    public OidDto oidDtoFor(final Object object) {
        final Bookmark bookmark = bookmarkService.bookmarkFor(object);
        return asOidDto(bookmark);
    }

    private static OidDto asOidDto(final Bookmark reference) {
        OidDto argValue;
        if (reference != null) {
            argValue = new OidDto();
            argValue.setObjectType(reference.getObjectType());
            argValue.setObjectState(bookmarkObjectStateOf(reference));
            argValue.setObjectIdentifier(reference.getIdentifier());
        } else {
            argValue = null;
        }
        return argValue;
    }

    private static BookmarkObjectState bookmarkObjectStateOf(final Bookmark reference) {
        switch (reference.getObjectState()) {
        case PERSISTENT:
            return BookmarkObjectState.PERSISTENT;
        case TRANSIENT:
            return BookmarkObjectState.TRANSIENT;
        case VIEW_MODEL:
            return BookmarkObjectState.VIEW_MODEL;
        }
        throw new IllegalArgumentException(
                String.format("reference.objectState '%s' not recognized", reference.getObjectState()));
    }

    @Inject
    BookmarkService bookmarkService;
}
