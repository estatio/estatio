package org.estatio.services.documents;

import com.google.common.base.Strings;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.commons.PropertyIds;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Programmatic;


public class DocumentViewModelFactory {

    @Programmatic
    public DocumentViewModel newViewModelFor(QueryResult result) {
        String cmisId = result.getPropertyValueById(PropertyIds.OBJECT_ID);

        final CmisObject obj = cmisRepository.findById(cmisId);
        if(obj == null || Strings.isNullOrEmpty(obj.getName())) {
            // TODO: why on earth could the obj.getName() be null even though result.get.... is non-null? 
            return null;
        }

        final DocumentViewModel dvm = container.newViewModelInstance(DocumentViewModel.class, cmisId);
        return dvm.init(obj);
    }

    @javax.inject.Inject
    CmisRepository cmisRepository;

    @javax.inject.Inject
    private DomainObjectContainer container;
}
