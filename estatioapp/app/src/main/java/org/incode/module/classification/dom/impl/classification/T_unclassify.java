package org.incode.module.classification.dom.impl.classification;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.incode.module.classification.dom.ClassificationModule;

public abstract class T_unclassify<T> {

    //region > constructor
    private final T classified;

    public T_unclassify(final T classified) {
        this.classified = classified;
    }

    public T getClassified() {
        return classified;
    }
    //endregion

    //region > unclassify

    public static class DomainEvent extends ClassificationModule.ActionDomainEvent<T_unclassify> {
    }

    {
    }

    @Action(
            domainEvent = DomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            cssClassFa = "fa-minus",
            contributed = Contributed.AS_ACTION
    )
    @MemberOrder(name = "classifications", sequence = "2")
    public Object unclassify(final Classification classification) {
        classificationRepository.remove(classification);
        return this.classified;
    }

    public TranslatableString disableUnclassify() {
        return choices0Unclassify().isEmpty() ? TranslatableString.tr("No classifications to delete") : null;
    }

    public Classification default0Unclassify() {
        List<Classification> classifications = choices0Unclassify();
        return classifications.size() == 1 ? classifications.iterator().next() : null;
    }

    public List<Classification> choices0Unclassify() {
        return classificationRepository.findByClassified(this.classified);
    }

    //endregion

    //region  > (injected)
    @Inject
    ClassificationRepository classificationRepository;
    //endregion
}
