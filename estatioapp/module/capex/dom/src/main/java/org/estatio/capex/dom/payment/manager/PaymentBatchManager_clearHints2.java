package org.estatio.capex.dom.payment.manager;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.applib.services.hint.HintStore;

/**
 * Workaround - the framework-provided clearHints isn't visible, because (I think) it is bound to a non-existent property.
 */
@Mixin(method = "act")
public class PaymentBatchManager_clearHints2 {

    private final PaymentBatchManager object;

    public PaymentBatchManager_clearHints2(PaymentBatchManager object) {
        this.object = object;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(
            cssClassFa = "fa-trash",
            position = ActionLayout.Position.PANEL_DROPDOWN
    )
    public Object act() {
        final Bookmark bookmark = bookmarkService.bookmarkFor(object);
        hintStore.removeAll(bookmark);
        return object;
    }

    @javax.inject.Inject
    HintStore hintStore;

    @javax.inject.Inject
    BookmarkService bookmarkService;
}
