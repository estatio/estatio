package org.incode.module.document.dom.impl.docs;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.DocumentModule;

@Mixin
public class Document_movedToExternalUrl {


    //region > constructor
    private final Document document;

    public Document_movedToExternalUrl(final Document document) {
        this.document = document;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<Document_movedToExternalUrl> { }
    /**
     * The idea is that this would be called by a background process.  This is a prototyping action, for demo (or to
     * call programmatically by said background service).
     *
     * <p>
     *     Not yet tackled in this design is how to obtain the content of the service (what if there are credentials
     *     etc that need to be provided.  However this document object <i>does</i> store is the
     *     {@link Document#getSort() document sort} and {@link Document#getMimeType() mime type}  which lets us know how to interpret the remotely held data.
     * </p>
     */
    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class,
            restrictTo = RestrictTo.PROTOTYPING
    )
    @ActionLayout(named = "Moved to External URL")
    public Document $$(
            @ParameterLayout(named = "External URL")
            final String externalUrl,
            @Parameter(optionality = Optionality.OPTIONAL, maxLength = DocumentAbstract.NameType.Meta.MAX_LEN)
            @ParameterLayout(named = "Name")
            final String name
    ) {
        document.setExternalUrl(externalUrl);
        document.setBlobBytes(null);
        document.setClobChars(null);

        final DocumentSort sort = document.getSort();
        document.setSort(sort.asExternal());

        if(name != null) {
            document.setName(name);
        }

        return document;
    }

    public boolean hide$$() {
        return document.getSort() == DocumentSort.EMPTY || document.getSort().isExternal();
    }

    public String default1$$() {
        return document.getName();
    }


}
