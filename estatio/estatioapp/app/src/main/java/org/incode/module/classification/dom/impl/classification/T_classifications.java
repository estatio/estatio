package org.incode.module.classification.dom.impl.classification;

import org.apache.isis.applib.annotation.*;
import org.incode.module.classification.dom.ClassificationModule;

import javax.inject.Inject;
import java.util.List;

public abstract class T_classifications<T> {

    //region > constructor
    private final T classified;
    public T_classifications(final T classified) {
        this.classified = classified;
    }

    public T getClassified() {
        return classified;
    }
    //endregion

    //region > $$
    public static class DomainEvent extends ClassificationModule.ActionDomainEvent<T_classifications> { } { }
    @Action(
            domainEvent = DomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            contributed = Contributed.AS_ASSOCIATION
    )
    @CollectionLayout(
            named = "Classifications",
            defaultView = "table"
    )
    public List<Classification> $$() {
        return classificationRepository.findByClassified(classified);
    }
    //endregion

    //region  > (injected)
    @Inject
    ClassificationRepository classificationRepository;
    //endregion


}
