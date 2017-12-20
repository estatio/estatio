package org.incode.module.document.dom.mixins;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.tablecol.TableColumnOrderService;

import org.incode.module.document.dom.DocumentModule;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

/**
 * Contributes a collection of all {@link org.incode.module.document.dom.impl.docs.Document}s that are attached to
 * this domain object.
 *
 * <p>
 *     More precisely, lists the {@link Paperclip}s where the paperclip is {@link Paperclip#getAttachedTo() attached to}
 *     this domain object; and shows the {@link Paperclip#getDocument() paperclip's associated document}.
 * </p>
 *
 * @param <T> - the type of the domain object which supports having {@link Paperclip}s associated with it.
 */
public abstract class T_documents<T> {

    private final T attachedTo;
    public T_documents(final T attachedTo) {
        this.attachedTo = attachedTo;
    }

    @Programmatic
    public T getAttachedTo() {
        return attachedTo;
    }



    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<T_documents>  { }
    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            contributed = Contributed.AS_ASSOCIATION
    )
    @CollectionLayout(defaultView = "table")
    public List<Paperclip> $$() {
        return queryResultsCache.execute(
                (Callable<List<Paperclip>>) () -> {
                    final List<Paperclip> paperclips = paperclipRepository.findByAttachedTo(attachedTo);
                    return Lists.newArrayList(FluentIterable.from(paperclips).filter(filter()).toList());
                },
                getClass(), "$$", attachedTo);
    }

    /**
     * Optional hook.
     */
    protected com.google.common.base.Predicate<? super Paperclip> filter() {
        return Predicates.alwaysTrue();
    }

    @Inject
    PaperclipRepository paperclipRepository;
    @Inject
    QueryResultsCache queryResultsCache;

    @DomainService(
            nature = NatureOfService.DOMAIN,
            menuOrder = "100"
    )
    public static class ColumnOrderService implements TableColumnOrderService {

        @Override
        public List<String> orderParented(
                final Object domainObject,
                final String collectionId,
                final Class<?> collectionType,
                final List<String> propertyIds) {
            if (!Paperclip.class.isAssignableFrom(collectionType)) {
                return null;
            }

            final List<String> trimmedPropertyIds = Lists.newArrayList(propertyIds);

            if("documents".equals(collectionId)) {
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
