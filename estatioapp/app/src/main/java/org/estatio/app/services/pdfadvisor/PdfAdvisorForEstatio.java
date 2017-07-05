package org.estatio.app.services.pdfadvisor;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.pdfjs.Scale;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService2;
import org.apache.isis.applib.services.jaxb.JaxbService;
import org.apache.isis.applib.services.urlencoding.UrlEncodingService;

import org.isisaddons.wicket.pdfjs.cpt.applib.PdfJsViewerAdvisor;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.viewmodel.IncomingDocViewModel;
import org.estatio.capex.dom.invoice.viewmodel.IncomingDocAsInvoiceViewModel;
import org.estatio.capex.dom.order.viewmodel.IncomingDocAsOrderViewModel;

@DomainService(nature = NatureOfService.DOMAIN)
public class PdfAdvisorForEstatio implements PdfJsViewerAdvisor {

    public static final Logger LOG = LoggerFactory.getLogger(PdfAdvisorForEstatio.class);

    // a more sophisticated implementation would use some sort of MRU/LRU cache.
    private final Map<InstanceKey.TypeKey, Advice.TypeAdvice> typeAdviceByTypeKey = Maps.newHashMap();

    // a more sophisticated implementation would use some sort of MRU/LRU cache.
    private final Map<InstanceKey, Integer> pageNumByInstanceKey = Maps.newHashMap();

    @Override
    public PdfJsViewerAdvisor.Advice advise(InstanceKey instanceKey) {
        if(instanceKey == null) {
            return null;
        }
        instanceKey = simplify(instanceKey);

        Advice advice = adviceFor(instanceKey);
        dump("advise", instanceKey);
        return advice;
    }

    private InstanceKey simplify(InstanceKey instanceKey) {
        InstanceKey.TypeKey typeKey = instanceKey.getTypeKey();
        if(typeKey.getObjectType().startsWith("capex.")) {
            instanceKey = new InstanceKey("capex", instanceKey.getIdentifier(), typeKey.getPropertyId(), typeKey.getUserName());
        }
        return instanceKey;
    }

    private Advice adviceFor(final InstanceKey instanceKey) {
        Integer pageNumber = pageNumByInstanceKey.computeIfAbsent(instanceKey, k -> 1);
        Advice.TypeAdvice typeAdvice = typeAdviceFor(instanceKey.getTypeKey());
        return new Advice(pageNumber, typeAdvice);
    }

    private Advice.TypeAdvice typeAdviceFor(InstanceKey.TypeKey typeKey) {
        Advice.TypeAdvice typeAdvice = typeAdviceByTypeKey
                .computeIfAbsent(typeKey, k -> new Advice.TypeAdvice(null, null));
        return typeAdvice;
    }

    @Override
    public void pageNumChangedTo(InstanceKey instanceKey, final int pageNum) {
        instanceKey = simplify(instanceKey);
        pageNumByInstanceKey.put(instanceKey, pageNum);
        dump("pageNumChangedTo", instanceKey);
    }

    @Override
    public void scaleChangedTo(InstanceKey instanceKey, final Scale scale) {
        instanceKey = simplify(instanceKey);
        InstanceKey.TypeKey typeKey = instanceKey.getTypeKey();
        Advice.TypeAdvice typeAdvice = typeAdviceFor(typeKey).withScale(scale);
        typeAdviceByTypeKey.put(typeKey, typeAdvice);
        dump("scaleChangedTo", instanceKey);
    }

    @Override
    public void heightChangedTo(InstanceKey instanceKey, final int height) {
        instanceKey = simplify(instanceKey);

        InstanceKey.TypeKey typeKey = instanceKey.getTypeKey();
        Advice.TypeAdvice typeAdvice = typeAdviceFor(typeKey).withHeight(height);
        typeAdviceByTypeKey.put(typeKey, typeAdvice);
        dump("scaleChangedTo", instanceKey);
    }

    private void dump(final String method, final InstanceKey instanceKey) {
        if(!LOG.isDebugEnabled()) {
            return;
        }

        LOG.debug("\n" + method + "(" + bookmarkFor(instanceKey) + "):\n");
        LOG.debug("  types:");
        for (InstanceKey.TypeKey key : typeAdviceByTypeKey.keySet()) {
            LOG.debug(String.format("    %s: %s", key.getObjectType(), typeAdviceByTypeKey.get(key)));
        }
        LOG.debug("  instances:");
        for (Iterator<InstanceKey> iterator = pageNumByInstanceKey.keySet().iterator(); iterator.hasNext(); ) {
            final InstanceKey key = iterator.next();
            String bookmark = bookmarkFor(key);
            if(bookmark != null) {
                final Integer integer = pageNumByInstanceKey.get(key);
                LOG.debug(String.format("    %s: %d", bookmark, integer));
            } else {
                // presumably deleted
                iterator.remove();
            }
        }
    }

    // TODO: we'll need a better way to prevent this class taking up too much memory
    // for now, this method clears up any cache for documents that have been deleted; but it isn't called yet...
    @Programmatic
    public void gc() {
        for (Iterator<InstanceKey> iterator = pageNumByInstanceKey.keySet().iterator(); iterator.hasNext(); ) {
            final InstanceKey key = iterator.next();
            String bookmark = bookmarkFor(key);
            if (bookmark == null) {
                iterator.remove();
            }
        }
    }

    private String bookmarkFor(final InstanceKey instanceKey) {

        Document document = determineDocument(instanceKey);
        InstanceKey.TypeKey typeKey = instanceKey.getTypeKey();
        String objectType = typeKey.getObjectType();

        if(Objects.equals(
                objectType,
                IncomingDocAsInvoiceViewModel.class.getName())) {

            String identifier = instanceKey.getIdentifier();
            final String xmlStr = urlEncodingService.decode(identifier);

            IncomingDocAsInvoiceViewModel viewModel = jaxbService.fromXml(IncomingDocAsInvoiceViewModel.class, xmlStr);

            final Bookmark bookmark = bookmarkService2.bookmarkFor(viewModel.getDocument());
            if (bookmark != null)
                return "incomingInvoiceViewModel:" + bookmark.getIdentifier();
            else {
                // object presumably deleted
                return null;
            }
        } else if(Objects.equals(
                objectType,
                IncomingDocAsOrderViewModel.class.getName())) {

            String identifier = instanceKey.getIdentifier();
            final String xmlStr = urlEncodingService.decode(identifier);

            IncomingDocAsOrderViewModel viewModel = jaxbService.fromXml(IncomingDocAsOrderViewModel.class, xmlStr);

            final Bookmark bookmark = bookmarkService2.bookmarkFor(viewModel.getDocument());
            if (bookmark != null)
                return "incomingOrderViewModel:" + bookmark.getIdentifier();
            else {
                // object presumably deleted
                return null;
            }
        } else {
            return instanceKey.asBookmark().toString();
        }
    }

    private Document determineDocument(final InstanceKey instanceKey) {

        Document document = determineDocument(instanceKey, IncomingDocAsInvoiceViewModel.class);
        if (document != null) {
            return document;
        }
        document = determineDocument(instanceKey, IncomingDocAsOrderViewModel.class);
        if (document != null) {
            return document;
        }
        return null;
    }

    private Document determineDocument(
            final InstanceKey instanceKey,
            final Class<? extends IncomingDocViewModel> viewModelClass) {

        final InstanceKey.TypeKey typeKey = instanceKey.getTypeKey();
        final String objectType = typeKey.getObjectType();
        if (!Objects.equals(objectType, viewModelClass.getName())) {
            return null;
        }
        final String identifier = instanceKey.getIdentifier();
        final String xmlStr = urlEncodingService.decode(identifier);

        final IncomingDocViewModel viewModel = jaxbService.fromXml(viewModelClass, xmlStr);
        return viewModel.getDocument();
    }

    @Inject
    BookmarkService2 bookmarkService2;

    @Inject
    UrlEncodingService urlEncodingService;

    @Inject
    JaxbService jaxbService;
}

