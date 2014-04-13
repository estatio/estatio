package org.estatio.services.documents;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.commons.PropertyIds;

import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.NotContributed.As;
import org.apache.isis.applib.annotation.NotInServiceMenu;

import org.estatio.dom.asset.Property;

public class DocumentContributions {

    @NotContributed(As.ASSOCIATION)
    // ie contributed as action
    @NotInServiceMenu
    public List<DocumentViewModel> relevantDocuments(Property property) {
        return Lists.newArrayList(
                Iterables.filter(
                        Iterables.transform(
                                cmisRepository.query(queryFor(property)),
                                newViewModel()),
                        Predicates.notNull()));
    }

    private Function<QueryResult, DocumentViewModel> newViewModel() {
        return new Function<QueryResult, DocumentViewModel>() {
            @Override
            public DocumentViewModel apply(QueryResult result) {
                return documentViewModelFactory.newViewModelFor(result);
            }
        };
    }

    static String queryFor(Property property) {
        final String city = property.getCity();
        return String.format(
                "SELECT %s, %s, %s "
                        + "FROM cmis:document "
                        + "WHERE CONTAINS('%s')",
                PropertyIds.OBJECT_ID, PropertyIds.NAME, PropertyIds.VERSION_LABEL,
                city);
    }

    @javax.inject.Inject
    DocumentViewModelFactory documentViewModelFactory;

    @javax.inject.Inject
    CmisRepository cmisRepository;
}
